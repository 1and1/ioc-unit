package net.oneandone.ejbcdiunit.relbuilder.code;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import javax.inject.Qualifier;


/**
 * @author aschoerk
 */
public class InjectsFinder extends AllRelVisitor {

    final static Annotation DEFAULT_ANNOTATION = InjectionPoint.class.getAnnotation(Default.class);

    static public class TypedPoint {

        List<Annotation> qualifiers;
        ClassWrapper classWrapper;

        TypedPoint(final List<Annotation> qualifiers, final ClassWrapper classWrapper) {
            this.qualifiers = qualifiers;
            this.classWrapper = classWrapper;
        }


        public List<Annotation> getQualifiers() {
            return qualifiers;
        }

        public ClassWrapper getClassWrapper() {
            return classWrapper;
        }

    }

    static public class ManagedBean extends TypedPoint {
        private final CdiRelBuilder.BeanClassRel beanClassRel;

        public ManagedBean(final CdiRelBuilder.BeanClassRel beanClass) {
            super(InjectsFinder.getQualifiers(beanClass.getAffectedClass().getAnnotations()), beanClass.getAffectedClass());
            this.beanClassRel = beanClass;
        }
    }

    static public class ProducerPoint extends TypedPoint {
        private CdiRelBuilder.ProducerMethodRel methodRel;
        private CdiRelBuilder.ProducerFieldRel fieldRel;

        public ProducerPoint(final CdiRelBuilder.ProducerFieldRel fieldRel) {
            super(InjectsFinder.getQualifiers(fieldRel.getField().getAnnotations()), new ClassWrapper(fieldRel.getField()));
            this.fieldRel = fieldRel;
        }

        public ProducerPoint(final CdiRelBuilder.ProducerMethodRel methodRel) {
            super(InjectsFinder.getQualifiers(methodRel.getMethod().getAnnotations()), new ClassWrapper(methodRel.getMethod()));
            this.methodRel = methodRel;
        }

    }


    @Default
    static public class InjectionPoint extends TypedPoint {
        private CdiRelBuilder.InjectedParameterRel parameterRel;
        private CdiRelBuilder.InjectedFieldRel field;

        public InjectionPoint(final CdiRelBuilder.InjectedFieldRel fieldRel) {
            super(InjectsFinder.getQualifiers(fieldRel.getField().getAnnotations()), new ClassWrapper(fieldRel.getField()));
            field = fieldRel;
        }

        public InjectionPoint(final CdiRelBuilder.InjectedParameterRel parameterRel) {
            super(InjectsFinder.getQualifiers(parameterRel.getParameter().getAnnotations()), new ClassWrapper(parameterRel.getParameter()));
            this.parameterRel = parameterRel;
        }
    }


    public List<InjectionPoint> getInjectionPoints() {
        return injectionPoints;
    }

    public static List<Annotation> getQualifiers(final Annotation[] annotations) {
        List<Annotation> res = new ArrayList<>();
        for (Annotation ann : annotations) {
            if (ann.annotationType().isAnnotationPresent(Qualifier.class)) {
                res.add(ann);
            }
        }
        if (res.isEmpty()) {
            res.add(DEFAULT_ANNOTATION);
        }
        return res;
    }

    @Override
    public Object visit(final CdiRelBuilder.BeanClassRel beanClassRel, final Object p) {
        boolean found = false;
        ClassWrapper aClass = beanClassRel.getAffectedClass();
        if (!aClass.isInterface() && !Modifier.isAbstract(aClass.getModifiers())) {
            if (!aClass.isMemberClass() || Modifier.isStatic(aClass.getModifiers())) {

                if (aClass.getAnnotation(Vetoed.class) == null) { // TODO: package vetoed

                    for (Constructor c : aClass.getDeclaredConstructors()) {
                        if (c.getAnnotation(Inject.class) == null) {
                            if (c.getParameters().length == 0) {
                                found = true;
                                break;
                            }
                        } else {
                            found = true;
                            break;
                        }
                    }

                }
            }
        }

        if (found) {
            managedBeans.add(new ManagedBean(beanClassRel));
        }

        return super.visit(beanClassRel, p);
    }


    @Override
    public Object visit(final CdiRelBuilder.InjectedFieldRel injectedFieldRel, final Object p) {
        injectionPoints.add(new InjectionPoint(injectedFieldRel));
        return super.visit(injectedFieldRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.InjectedParameterRel injectedParameterRel, final Object p) {
        injectionPoints.add(new InjectionPoint(injectedParameterRel));
        return super.visit(injectedParameterRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.ProducerFieldRel producerFieldRel, final Object p) {
        producerPoints.add(new ProducerPoint(producerFieldRel));
        return super.visit(producerFieldRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.ProducerMethodRel producerMethodRel, final Object p) {
        producerPoints.add(new ProducerPoint(producerMethodRel));
        return super.visit(producerMethodRel, p);
    }


    private List<InjectionPoint> injectionPoints = new ArrayList<>();
    private List<ProducerPoint> producerPoints = new ArrayList<>();
    private List<ManagedBean> managedBeans = new ArrayList<>();
}
