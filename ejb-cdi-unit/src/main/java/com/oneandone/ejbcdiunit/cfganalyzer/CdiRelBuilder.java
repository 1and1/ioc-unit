package com.oneandone.ejbcdiunit.cfganalyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.resteasy.spi.NotImplementedYetException;

/**
 * @author aschoerk
 */
public class CdiRelBuilder {

    public enum RelType {
        ROOT,
        PRODUCER_FIELD,
        PRODUCER_METHOD,
        INJECTED_FIELD,
        INJECTED_PARAMETER,
        ADDED_BEAN,
        ADDED_CLASS,
        ADDED_ANNOTATION,
        SIMPLE_BEAN,
        CONSTRUCTOR_INJECT;
    }

    public interface RelVisitor {
        Object visit(ProducerFieldRel producerMethodRel, Object p);

        Object visit(ProducerMethodRel producerMethodRel, Object p);

        Object visit(InjectedFieldRel producerMethodRel, Object p);

        Object visit(BeanClassRel producerMethodRel, Object p);

        Object visit(SimpleClassRel producerMethodRel, Object p);

        Object visit(ConstructorInjectRel producerMethodRel, Object p);

        Object visit(ParameterInjectRel producerMethodRel, Object p);

        Object visit(RootRel producerMethodRel, Object p);
    }

    public interface Rel {
        Rel parent();

        RelType type();

        Class<?> getAffectedClass();

        Object accept(RelVisitor visitor, Object p);
    }

    public interface Intermediate extends Rel {
        List<Rel> children();

        default void add(Rel rel) {
            Objects.requireNonNull(rel);
            children().add(rel);
        }
    }

    public static abstract class BaseRel implements Rel {
        private Rel parent;
        Class<?> affectedClass;

        public BaseRel(final Intermediate parent, final Class<?> affectedClass) {
            Objects.requireNonNull(parent);
            this.parent = parent;
            this.affectedClass = affectedClass;
            if (parent != null) {
                parent.add(this);
            }
        }

        @Override
        public Class<?> getAffectedClass() {
            return affectedClass;
        }

        @Override
        public Rel parent() {
            return parent;
        }

    }

    public static abstract class BaseIntermediateRel extends BaseRel implements Intermediate {
        List<Rel> children = new ArrayList<>();

        public BaseIntermediateRel(final Intermediate parent, final Class<?> affectedClass) {
            super(parent, affectedClass);
        }

        public List<Rel> children() {
            return children;
        }
    }

    public static class BeanClassRel extends BaseIntermediateRel {
        public BeanClassRel(final Intermediate parent, final Class<?> affectedClass) {
            super(parent, affectedClass);
        }

        @Override
        public RelType type() {
            return RelType.ADDED_BEAN;
        }

        @Override
        public Object accept(RelVisitor visitor, Object p) {
            return visitor.visit(this, p);
        }
    }

    public static class SimpleClassRel extends BaseIntermediateRel {
        public SimpleClassRel(final Intermediate parent, final Class<?> affectedClass) {
            super(parent, affectedClass);
        }

        @Override
        public RelType type() {
            return RelType.SIMPLE_BEAN;
        }

        @Override
        public Object accept(RelVisitor visitor, Object p) {
            return visitor.visit(this, p);
        }
    }

    public static class RootRel implements Intermediate {

        private final Collection<Class<?>> initialClasses;
        private Map<Class<?>, Intermediate> beanClasses;
        private List<Rel> children = new ArrayList<>();

        public RootRel(Collection<Class<?>> initialClasses) {
            this.initialClasses = initialClasses;
        }

        @Override
        public Rel parent() {
            return null;
        }

        @Override
        public Class<?> getAffectedClass() {
            return null;
        }

        @Override
        public List<Rel> children() {
            return children;
        }

        @Override
        public RelType type() {
            return RelType.ROOT;
        }

        @Override
        public Object accept(RelVisitor visitor, Object p) {
            return visitor.visit(this, p);
        }

        public void setBeanClasses(final Map<Class<?>, Intermediate> beanClasses) {
            this.beanClasses = beanClasses;
        }

        public Map<Class<?>, Intermediate> getBeanClasses() {
            return beanClasses;
        }
    }

    public static abstract class BaseFieldRel extends BaseRel {
        protected Field f;

        public BaseFieldRel(final BeanClassRel r, final Field field) {
            super(r, (Class) field.getType());
            this.f = field;
        }

    }


    public static class InjectedFieldRel extends BaseFieldRel {

        private Intermediate bean;

        public InjectedFieldRel(final BeanClassRel r, final Field field) {
            super(r, field);
        }

        @Override
        public RelType type() {
            return RelType.INJECTED_FIELD;
        }

        @Override
        public Object accept(RelVisitor visitor, Object p) {
            return visitor.visit(this, p);
        }

        public void setBean(final Intermediate bean) {
            this.bean = bean;
        }

        public Intermediate getBean() {
            return bean;
        }
    }

    public static class ProducerFieldRel extends BaseFieldRel {

        public ProducerFieldRel(final BeanClassRel r, final Field field) {
            super(r, field);
        }

        @Override
        public RelType type() {
            return RelType.PRODUCER_FIELD;
        }

        @Override
        public Object accept(RelVisitor visitor, Object p) {
            return visitor.visit(this, p);
        }
    }

    public static class ConstructorInjectRel extends BaseIntermediateRel {

        protected Constructor m;

        public ConstructorInjectRel(final BeanClassRel r, final Constructor constructor) {
            super(r, constructor.getDeclaringClass());
            this.m = constructor;
        }

        @Override
        public RelType type() {
            return RelType.CONSTRUCTOR_INJECT;
        }

        @Override
        public Object accept(RelVisitor visitor, Object p) {
            return visitor.visit(this, p);
        }

    }

    public static class ParameterInjectRel extends BaseRel {

        protected Parameter p;

        public ParameterInjectRel(final Intermediate r, final Parameter parameter) {
            super(r, parameter.getType());
            this.p = parameter;
        }

        @Override
        public RelType type() {
            return RelType.INJECTED_PARAMETER;
        }

        @Override
        public Object accept(RelVisitor visitor, Object p) {
            return visitor.visit(this, p);
        }

        private Intermediate bean;

        public void setBean(final Intermediate bean) {
            this.bean = bean;
        }

        public Intermediate getBean() {
            return bean;
        }

    }


    public static class ProducerMethodRel extends BaseIntermediateRel {

        protected Method m;

        public ProducerMethodRel(final BeanClassRel r, final Method method) {
            super(r, method.getReturnType());
            this.m = method;
        }

        @Override
        public RelType type() {
            return RelType.PRODUCER_METHOD;
        }

        @Override
        public Object accept(RelVisitor visitor, Object p) {
            return visitor.visit(this, p);
        }
    }


    public static class RelFactory {
        Map<Class<?>, Intermediate> beanClasses = new HashMap<>();

        SimpleClassRel createSimple(Intermediate parent, Class<?> c) throws AnalyzerException {
            SimpleClassRel r = new SimpleClassRel(parent, c);
            return r;
        }

        Intermediate createBeanFromClass(Intermediate parent, Class<?> c) throws AnalyzerException {
            if (beanClasses.containsKey(c))
                return beanClasses.get(c);
            Intermediate res = null;
            try {
                if (c.isInterface()) {
                    res = new SimpleClassRel(parent, c);
                } else {
                    BeanClassRel br = new BeanClassRel(parent, c);
                    res = br;
                    Type superClass = c.getGenericSuperclass();
                    if (superClass != null && superClass != Object.class) {
                        createBeanFromClass(br, (Class) superClass);
                    }
                    for (Field field : c.getDeclaredFields()) {
                        if (field.isAnnotationPresent(Inject.class)) {
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
            } finally {
                beanClasses.put(c, res);
            }
            return res;
        }

        private void createProducerMethod(final BeanClassRel r, final Method method) throws AnalyzerException {
            ProducerMethodRel res = new ProducerMethodRel(r, method);
            for (Parameter p : method.getParameters()) {
                ParameterInjectRel pRel = createInjectParameter(res, p);
                res.add(pRel);
                Intermediate bean = createTypeBean(r, p.getParameterizedType());
                pRel.setBean(bean);
            }
        }


        private void createInjectConstructor(final BeanClassRel r, final Constructor constructor) throws AnalyzerException {
            ConstructorInjectRel res = new ConstructorInjectRel(r, constructor);
            for (Parameter p : constructor.getParameters()) {
                ParameterInjectRel pRel = createInjectParameter(r, p);
                res.add(pRel);
                Intermediate bean = createTypeBean(r, p.getParameterizedType());
                pRel.setBean(bean);
            }
        }

        private ParameterInjectRel createInjectParameter(final Intermediate parent, final Parameter p) {
            return new ParameterInjectRel(parent, p);
        }

        private InjectedFieldRel createInjectField(final BeanClassRel r, final Field field) throws AnalyzerException {
            if (field.getType().equals(Provider.class)) {
                throw new NotImplementedYetException();
            }
            if (field.getType().equals(Instance.class)) {
                throw new NotImplementedYetException();
            }
            InjectedFieldRel res = new InjectedFieldRel(r, field);
            Intermediate bean = createTypeBean(r, field.getType());
            res.setBean(bean);
            return res;

        }

        private ProducerFieldRel createProducedField(final BeanClassRel r, final Field field) {
            return new ProducerFieldRel(r, field);
        }

        Intermediate createParameterized(Intermediate parent, ParameterizedType type) throws AnalyzerException {
            Intermediate raw = this.createBeanFromClass(parent, (Class) (type.getRawType()));
            for (Type arg : type.getActualTypeArguments()) {
                createTypeBean(parent, arg);
            }
            return raw;
        }

        Intermediate createTypeBean(Intermediate parent, Type type) throws AnalyzerException {
            if (type instanceof Class) {
                return this.createBeanFromClass(parent, (Class) type);

            }
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                return createParameterized(parent, ptype);
            }
            throw new NotSupportedTypeYet();
        }
    }

    public static class NotSupportedTypeYet extends AnalyzerException {
        public static final long serialVersionUID = -7645090605110467195L;
    }

    public static class AnalyzerException extends Exception {
        public static final long serialVersionUID = -4731804312861785688L;
    }


    private final RootRel rootRel;

    public RootRel getRootRel() {
        return rootRel;
    }

    public CdiRelBuilder(Collection<Class<?>> initialClasses) throws AnalyzerException {
        this.rootRel = new RootRel(initialClasses);
        RelFactory relFactory = new RelFactory();
        for (Class<?> clazz : initialClasses) {
            relFactory.createBeanFromClass(this.rootRel, clazz);
        }
        rootRel.setBeanClasses(relFactory.beanClasses);
    }

}
