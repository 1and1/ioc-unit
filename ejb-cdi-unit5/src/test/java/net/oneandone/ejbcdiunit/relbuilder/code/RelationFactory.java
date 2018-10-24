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

    Map<String, Rels.SimpleClassRel> simpleClassRelMap = new HashMap<String, Rels.SimpleClassRel>();
    Map<String, Rels.Intermediate> beanClasses = new HashMap<String, Rels.Intermediate>();

    Rels.SimpleClassRel createSimple(Rels.Intermediate parent, ClassWrapper c) throws CdiRelBuilder.AnalyzerException {
        if (simpleClassRelMap.containsKey(c.getName())) {
            return simpleClassRelMap.get(c.getName());
        }
        Rels.SimpleClassRel r = new Rels.SimpleClassRel(parent, c);
        simpleClassRelMap.put(c.getName(), r);
        return r;
    }

    Rels.Intermediate createBeanFromClass(Rels.Intermediate parent, Class<?> c) throws CdiRelBuilder.AnalyzerException {
        return createBeanFromClass(parent, new ClassWrapper(c));

    }

    Rels.Intermediate createBeanFromClass(Rels.Intermediate parent, ClassWrapper c) throws CdiRelBuilder.AnalyzerException {
        if (beanClasses.containsKey(c.getName())) {
            return beanClasses.get(c.getName());
        }
        if (simpleClassRelMap.containsKey(c.getName())) {
            return simpleClassRelMap.get(c.getName());
        }
        Rels.Intermediate res = null;
        try {
            if (c.isInterface()) {
                res = new Rels.SimpleClassRel(parent, c);
            } else {
                Rels.BeanClassRel br = new Rels.BeanClassRel(parent, c);
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

    private void createProducerMethod(final Rels.BeanClassRel r, final Method method) throws CdiRelBuilder.AnalyzerException {
        Rels.ProducerMethodRel res = new Rels.ProducerMethodRel(r, method);
        addInjectedParameters(r, method.getParameters(), res);
    }

    private void addInjectedParameters(final Rels.BeanClassRel r, final Parameter[] params, final Rels.Intermediate res)
            throws CdiRelBuilder.AnalyzerException {
        for (Parameter p : params) {
            Rels.InjectedParameterRel pRel = createInjectParameter(res, p);
            Rels.Intermediate bean = createTypeBean(r, new ClassWrapper(p));
            pRel.setBean(bean);
        }
    }


    private void createInjectConstructor(final Rels.BeanClassRel r, final Constructor constructor) throws CdiRelBuilder.AnalyzerException {
        Rels.ConstructorInjectRel res = new Rels.ConstructorInjectRel(r, constructor);
        addInjectedParameters(r, constructor.getParameters(), res);
    }

    private Rels.InjectedParameterRel createInjectParameter(final Rels.Intermediate parent, final Parameter p) {
        return new Rels.InjectedParameterRel(parent, p);
    }

    private Rels.InjectedFieldRel createInjectField(final Rels.BeanClassRel r, final Field field)
            throws CdiRelBuilder.AnalyzerException {
        if (field.getType().equals(Provider.class)) {
            throw new NotImplementedYetException();
        }
        if (field.getType().equals(Instance.class)) {
            throw new NotImplementedYetException();
        }
        Rels.InjectedFieldRel res = new Rels.InjectedFieldRel(r, field);
        try {
            Rels.Intermediate bean = createTypeBean(r, new ClassWrapper(field));
            res.setBean(bean);
        } catch (NoClassDefFoundError ex) {
            Rels.SimpleClassRel bean = createNotAvailableBean(r, new ClassWrapper(field));
            res.setBean(bean);
        }
        return res;

    }

    private Rels.SimpleClassRel createNotAvailableBean(final Rels.BeanClassRel parent, final ClassWrapper type) {
        return new Rels.SimpleClassRel(parent, type);
    }

    private Rels.ProducerFieldRel createProducedField(final Rels.BeanClassRel parent, final Field field) {
        return new Rels.ProducerFieldRel(parent, field);
    }

    Rels.Intermediate createParameterized(Rels.Intermediate parent, ParameterizedType type) throws CdiRelBuilder.AnalyzerException {
        Rels.Intermediate raw = createBeanFromClass(parent, new ClassWrapper(type));
        for (Type arg : type.getActualTypeArguments()) {
            createTypeBean(parent, new ClassWrapper(arg));
        }
        return raw;
    }

    Rels.Intermediate createTypeBean(Rels.Intermediate parent, ClassWrapper typeDelegate) throws CdiRelBuilder.AnalyzerException {
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