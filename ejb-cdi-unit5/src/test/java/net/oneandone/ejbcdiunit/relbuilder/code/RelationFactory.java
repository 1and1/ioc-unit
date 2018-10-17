package net.oneandone.ejbcdiunit.relbuilder.code;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.PersistenceContext;

import org.jboss.resteasy.spi.NotImplementedYetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationFactory {
    Logger logger = LoggerFactory.getLogger(RelationFactory.class);

    Map<String, CdiRelBuilder.SimpleClassRel> simpleClassRelMap = new HashMap<String, CdiRelBuilder.SimpleClassRel>();
    Map<String, CdiRelBuilder.Intermediate> beanClasses = new HashMap<String, CdiRelBuilder.Intermediate>();

    CdiRelBuilder.SimpleClassRel createSimple(CdiRelBuilder.Intermediate parent, ClassWrapper c) throws CdiRelBuilder.AnalyzerException {
        if (simpleClassRelMap.containsKey(c.getName())) {
            return simpleClassRelMap.get(c.getName());
        }
        CdiRelBuilder.SimpleClassRel r = new CdiRelBuilder.SimpleClassRel(parent, c);
        simpleClassRelMap.put(c.getName(), r);
        return r;
    }

    CdiRelBuilder.Intermediate createBeanFromClass(CdiRelBuilder.Intermediate parent, Class<?> c) throws CdiRelBuilder.AnalyzerException {
        return createBeanFromClass(parent, new ClassWrapper(c));

    }

    CdiRelBuilder.Intermediate createBeanFromClass(CdiRelBuilder.Intermediate parent, ClassWrapper c) throws CdiRelBuilder.AnalyzerException {
        if (beanClasses.containsKey(c.getName())) {
            return beanClasses.get(c.getName());
        }
        if (simpleClassRelMap.containsKey(c.getName())) {
            return simpleClassRelMap.get(c.getName());
        }
        CdiRelBuilder.Intermediate res = null;
        try {
            if (c.isInterface()) {
                res = new CdiRelBuilder.SimpleClassRel(parent, c);
            } else {
                CdiRelBuilder.BeanClassRel br = new CdiRelBuilder.BeanClassRel(parent, c);
                new CdiUnitAnnotationHandler(this, br).handleAdditionalClassAnnotations(c);
                res = br;
                ClassWrapper superClass = c.getGenericSuperclass();
                if (!superClass.isNull() && !superClass.equals(Object.class)) {
                    createBeanFromClass(br, superClass);
                }
                for (Field field : c.getDeclaredFields()) {
                    Annotation[] annotations = field.getAnnotations();
                    if (field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(EJB.class)
                            || field.isAnnotationPresent(Resource.class)
                            || field.isAnnotationPresent(PersistenceContext.class)) {
                        createInjectField(br, field);
                    } else if (field.isAnnotationPresent(Produces.class)) {
                        createProducedField(br, field);
                    }
                }
                for (Constructor constructor : c.getDeclaredConstructors()) {
                    if (constructor.isAnnotationPresent(Inject.class)) {
                        createInjectConstructor(br, constructor);
                    }
                }

                for (Method method : c.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Produces.class)) {
                        createProducerMethod(br, method);
                    }
                }
            }
        } catch (NoClassDefFoundError e) {
            res = createSimple(parent, c);
        } catch (Throwable e) {
            res = createSimple(parent, c);
        } finally {
            beanClasses.put(c.getName(), res);
        }
        return res;
    }

    private void createProducerMethod(final CdiRelBuilder.BeanClassRel r, final Method method) throws CdiRelBuilder.AnalyzerException {
        CdiRelBuilder.ProducerMethodRel res = new CdiRelBuilder.ProducerMethodRel(r, method);
        addInjectedParameters(r, method.getParameters(), res);
    }

    private void addInjectedParameters(final CdiRelBuilder.BeanClassRel r, final Parameter[] params, final CdiRelBuilder.Intermediate res)
            throws CdiRelBuilder.AnalyzerException {
        for (Parameter p : params) {
            CdiRelBuilder.InjectedParameterRel pRel = createInjectParameter(res, p);
            CdiRelBuilder.Intermediate bean = createTypeBean(r, new ClassWrapper(p));
            pRel.setBean(bean);
        }
    }


    private void createInjectConstructor(final CdiRelBuilder.BeanClassRel r, final Constructor constructor) throws CdiRelBuilder.AnalyzerException {
        CdiRelBuilder.ConstructorInjectRel res = new CdiRelBuilder.ConstructorInjectRel(r, constructor);
        addInjectedParameters(r, constructor.getParameters(), res);
    }

    private CdiRelBuilder.InjectedParameterRel createInjectParameter(final CdiRelBuilder.Intermediate parent, final Parameter p) {
        return new CdiRelBuilder.InjectedParameterRel(parent, p);
    }

    private CdiRelBuilder.InjectedFieldRel createInjectField(final CdiRelBuilder.BeanClassRel r, final Field field)
            throws CdiRelBuilder.AnalyzerException {
        if (field.getType().equals(Provider.class)) {
            throw new NotImplementedYetException();
        }
        if (field.getType().equals(Instance.class)) {
            throw new NotImplementedYetException();
        }
        CdiRelBuilder.InjectedFieldRel res = new CdiRelBuilder.InjectedFieldRel(r, field);
        try {
            CdiRelBuilder.Intermediate bean = createTypeBean(r, new ClassWrapper(field));
            res.setBean(bean);
        } catch (NoClassDefFoundError ex) {
            CdiRelBuilder.SimpleClassRel bean = createNotAvailableBean(r, new ClassWrapper(field));
            res.setBean(bean);
        }
        return res;

    }

    private CdiRelBuilder.SimpleClassRel createNotAvailableBean(final CdiRelBuilder.BeanClassRel parent, final ClassWrapper type) {
        return new CdiRelBuilder.SimpleClassRel(parent, type);
    }

    private CdiRelBuilder.ProducerFieldRel createProducedField(final CdiRelBuilder.BeanClassRel parent, final Field field) {
        return new CdiRelBuilder.ProducerFieldRel(parent, field);
    }

    CdiRelBuilder.Intermediate createParameterized(CdiRelBuilder.Intermediate parent, ParameterizedType type) throws CdiRelBuilder.AnalyzerException {
        CdiRelBuilder.Intermediate raw = createBeanFromClass(parent, new ClassWrapper(type));
        for (Type arg : type.getActualTypeArguments()) {
            createTypeBean(parent, new ClassWrapper(arg));
        }
        return raw;
    }

    CdiRelBuilder.Intermediate createTypeBean(CdiRelBuilder.Intermediate parent, ClassWrapper typeDelegate) throws CdiRelBuilder.AnalyzerException {
        if (typeDelegate.getType() instanceof Class) {
            return createBeanFromClass(parent, typeDelegate);

        }
        if (typeDelegate.getType() instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) typeDelegate.getType();
            return createParameterized(parent, ptype);
        }
        throw new CdiRelBuilder.NotSupportedTypeYet();
    }
}