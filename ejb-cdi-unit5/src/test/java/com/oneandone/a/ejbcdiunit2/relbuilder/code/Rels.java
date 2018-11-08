package com.oneandone.a.ejbcdiunit2.relbuilder.code;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author aschoerk
 */
public class Rels {
    public interface Rel {
        Rel parent();

        RelType type();

        ClassWrapper getAffectedClass();

        Object accept(RelVisitor visitor, Object p);
    }

    public interface Intermediate extends Rel {
        List<Rel> children();

        default void add(Rel rel) {
            Objects.requireNonNull(rel);
            children().add(rel);
        }

        default int size() {
            return children().size();
        }

        default boolean remove(Object o) {
            return children().remove(o);
        }
    }

    public abstract static class AnnotationIntermediate extends BaseIntermediateRel {
        private final Annotation ann;

        public AnnotationIntermediate(Annotation ann, final Intermediate parent) {
            super(parent, parent.getAffectedClass());
            this.ann = ann;
        }

    }

    public abstract static class AnnotationRel extends BaseRel {
        private final Annotation ann;

        public AnnotationRel(Annotation ann, final Intermediate parent) {
            super(parent, parent.getAffectedClass());
            this.ann = ann;
        }

    }

    public static class ActivatedAlternativesRel extends AnnotationIntermediate {
        public ActivatedAlternativesRel(Annotation ann, final Intermediate parent) {
            super(ann, parent);
        }

        @Override
        public RelType type() {
            return RelType.ACTIVATED_ALTERNATIVES_ANNOTATION;
        }

        @Override
        public Object accept(final RelVisitor visitor, final Object p) {
            return visitor.visit(this, p);
        }
    }

    public static class AdditionalClassesRel extends AnnotationIntermediate {
        public AdditionalClassesRel(Annotation ann, final Intermediate parent) {
            super(ann, parent);
        }

        @Override
        public RelType type() {
            return RelType.ADDITIONAL_CLASSES_ANNOTATION;
        }

        @Override
        public Object accept(final RelVisitor visitor, final Object p) {
            return visitor.visit(this, p);
        }
    }

    public static class AdditionalPackageRel extends AnnotationIntermediate {
        public AdditionalPackageRel(Annotation ann, final Intermediate parent) {
            super(ann, parent);
        }

        @Override
        public RelType type() {
            return RelType.ADDITIONAL_PACKAGE_ANNOTATION;
        }

        @Override
        public Object accept(final RelVisitor visitor, final Object p) {
            return visitor.visit(this, p);
        }
    }

    public static class AdditionalClasspathRel extends AnnotationIntermediate {
        public AdditionalClasspathRel(Annotation ann, final Intermediate parent) {
            super(ann, parent);
        }

        @Override
        public RelType type() {
            return RelType.ADDITIONAL_CLASSPATH_ANNOTATION;
        }

        @Override
        public Object accept(final RelVisitor visitor, final Object p) {
            return visitor.visit(this, p);
        }
    }

    public static class ExcludedClassesRel extends AnnotationRel {
        public ExcludedClassesRel(Annotation ann, final Intermediate parent) {
            super(ann, parent);
        }

        @Override
        public RelType type() {
            return RelType.EXCLUDED_CLASSES_ANNOTATION;
        }

        @Override
        public Object accept(final RelVisitor visitor, final Object p) {
            return visitor.visit(this, p);
        }
    }

    public static class EjbClasspathRel extends AnnotationRel {
        public EjbClasspathRel(Annotation ann, final Intermediate parent) {
            super(ann, parent);
        }

        @Override
        public RelType type() {
            return RelType.EJB_CLASSPATH_ANNOTATION;
        }

        @Override
        public Object accept(final RelVisitor visitor, final Object p) {
            return visitor.visit(this, p);
        }
    }

    public static abstract class BaseRel implements Rel {
        private Rel parent;
        ClassWrapper affectedClass;

        public BaseRel(final Intermediate parent, final ClassWrapper affectedClass) {
            Objects.requireNonNull(parent);
            this.parent = parent;
            this.affectedClass = affectedClass;
            if (parent != null) {
                parent.add(this);
            }
        }

        @Override
        public ClassWrapper getAffectedClass() {
            return affectedClass;
        }

        @Override
        public Rel parent() {
            return parent;
        }

    }

    public static abstract class BaseIntermediateRel extends BaseRel implements Intermediate {
        List<Rel> children = new ArrayList<>();

        public BaseIntermediateRel(final Intermediate parent, final ClassWrapper affectedClass) {
            super(parent, affectedClass);
        }

        public List<Rel> children() {
            return children;
        }
    }

    public static class BeanClassRel extends BaseIntermediateRel {
        public BeanClassRel(final Intermediate parent, final ClassWrapper affectedClass) {
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
        public SimpleClassRel(final Intermediate parent, final ClassWrapper affectedClass) {
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

        private final Collection<ClassWrapper> initialClasses;
        private Map<String, Intermediate> beanClasses;
        private List<Rel> children = new ArrayList<>();

        public RootRel(Collection<ClassWrapper> initialClasses) {
            this.initialClasses = initialClasses;
        }

        @Override
        public Rel parent() {
            return null;
        }

        @Override
        public ClassWrapper getAffectedClass() {
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

        public void setBeanClasses(final Map<String, Intermediate> beanClasses) {
            this.beanClasses = beanClasses;
        }

        public Map<String, Intermediate> getBeanClasses() {
            return beanClasses;
        }
    }

    public static abstract class BaseFieldRel extends BaseRel {
        protected Field f;

        public BaseFieldRel(final BeanClassRel r, final Field field) {
            super(r, new ClassWrapper(field));
            this.f = field;
        }

        public Field getField() {
            return f;
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
            super(r, new ClassWrapper(constructor));
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

    public static class InjectedParameterRel extends BaseRel {

        protected Parameter p;

        public InjectedParameterRel(final Intermediate r, final Parameter parameter) {
            super(r, new ClassWrapper(parameter));
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

        public Parameter getParameter() {
            return p;
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

        public Method getMethod() {
            return m;
        }

        public ProducerMethodRel(final BeanClassRel r, final Method method) {
            super(r, new ClassWrapper(method));
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
}
