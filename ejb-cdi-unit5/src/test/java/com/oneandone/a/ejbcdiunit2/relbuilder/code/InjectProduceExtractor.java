package com.oneandone.a.ejbcdiunit2.relbuilder.code;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Qualifier;

import org.apache.commons.lang3.reflect.TypeUtils;

/**
 * @author aschoerk
 */
public class InjectProduceExtractor extends AllRelVisitor {
    public InjectProduceExtractor() {}

    List<QualifiedDesc> producers = new ArrayList<>();
    List<QualifiedDesc> injects = new ArrayList<>();
    public List<QualifiedDesc> emptyInjects = new ArrayList<>();
    public HashMap<QualifiedDesc, List<QualifiedDesc>> ambiguusQualifiedDescs = new HashMap<>();
    public HashMap<QualifiedDesc, List<QualifiedDesc>> matchingQualifiedDescs = new HashMap<>();

    public List<QualifiedDesc> getEmptyInjects() {
        return emptyInjects;
    }

    public HashMap<QualifiedDesc, List<QualifiedDesc>> getAmbiguusQualifiedDescs() {
        return ambiguusQualifiedDescs;
    }

    public HashMap<QualifiedDesc, List<QualifiedDesc>> getMatchingQualifiedDescs() {
        return matchingQualifiedDescs;
    }

    @Default
    static class ToGetDefault {

    }

    static public class QualifiedDesc {
        public QualifiedDesc(final Rels.Rel org, final Type type, final Set<Annotation> qualifiers) {
            this.org = org;
            this.type = type;
            this.qualifiers = qualifiers;
        }

        Rels.Rel org;
        Type type;
        Set<Annotation> qualifiers;
    }


    static Annotation defaultAnnotation = ToGetDefault.class.getAnnotation(Default.class);

    @Override
    public Object visit(final Rels.ProducerFieldRel producerFieldRel, final Object p) {
        Annotation[] annotations = producerFieldRel.f.getAnnotations();
        addProducerInfo(producerFieldRel, producerFieldRel.f.getGenericType(), annotations);
        return super.visit(producerFieldRel, p);
    }

    @Override
    public Object visit(final Rels.ProducerMethodRel producerMethodRel, final Object p) {
        Annotation[] annotations = producerMethodRel.getMethod().getAnnotations();
        addProducerInfo(producerMethodRel, producerMethodRel.getMethod().getGenericReturnType(), annotations);
        return super.visit(producerMethodRel, p);
    }

    @Override
    public Object visit(final Rels.InjectedFieldRel injectedFieldRel, final Object p) {
        Annotation[] annotations = injectedFieldRel.f.getAnnotations();
        addInjectsInfo(injectedFieldRel, injectedFieldRel.f.getGenericType(), annotations);
        return super.visit(injectedFieldRel, p);
    }

    @Override
    public Object visit(final Rels.BeanClassRel beanClassRel, final Object p) {
        Class clazz = beanClassRel.affectedClass.getBaseclass();
        if (clazz.isAnnotation() || Extension.class.isAssignableFrom(clazz) || clazz.isPrimitive()) {

        } else {
            addProducerInfo(beanClassRel, beanClassRel.affectedClass.getType(), beanClassRel.affectedClass.getAnnotations());
        }
        return super.visit(beanClassRel, p);
    }

    @Override
    public Object visit(final Rels.InjectedParameterRel injectedParameterRel, final Object p) {
        Type type = injectedParameterRel.getParameter().getParameterizedType();
        Annotation[] annotations = injectedParameterRel.getParameter().getAnnotations();
        addInjectsInfo(injectedParameterRel, type, annotations);
        return super.visit(injectedParameterRel, p);
    }


    private void addProducerInfo(final Rels.Rel rel, Type type, final Annotation[] annotations) {
        Set<Annotation> qualifiers = getQualifiers(annotations);
        producers.add(new QualifiedDesc(rel, type, qualifiers));
    }

    private void addInjectsInfo(final Rels.Rel rel, Type type, final Annotation[] annotations) {
        Set<Annotation> qualifiers = getQualifiers(annotations);
        injects.add(new QualifiedDesc(rel, type, qualifiers));
    }

    private Set<Annotation> getQualifiers(final Annotation[] annotations) {
        Set<Annotation> qualifiers = new HashSet<>();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                qualifiers.add(annotation);
            }
        }

        if (qualifiers.isEmpty())
            qualifiers.add(defaultAnnotation);
        return qualifiers;
    }


    public void produceFitsToInject(Rels.RootRel root) {
        visit(root, null);
        emptyInjects.clear();
        ambiguusQualifiedDescs.clear();
        matchingQualifiedDescs.clear();

        for (QualifiedDesc qi : injects) {
            matchingQualifiedDescs.put(qi, new ArrayList<>());
            for (QualifiedDesc qp : producers) {
                if (TypeUtils.isAssignable(qp.type, qi.type)) {
                    if (qualifiersMatch(qi, qp))
                        matchingQualifiedDescs.get(qi).add(qp);
                }
            }
            if (matchingQualifiedDescs.get(qi).size() == 0) {
                emptyInjects.add(qi);
            } else if (matchingQualifiedDescs.get(qi).size() > 1) {
                ambiguusQualifiedDescs.put(qi, matchingQualifiedDescs.get(qi));
            }
        }
    }

    private Boolean qualifiersMatch(final QualifiedDesc qi, final QualifiedDesc qp) {
        if (qi.qualifiers.isEmpty()) {
            if (hasDefault(qp.qualifiers) || hasAny(qp.qualifiers)) {
                return true;
            } else
                return false;
        }
        if (qp.qualifiers.isEmpty()) {
            if (qi.qualifiers.size() <= 1 && hasDefault(qi.qualifiers))
                return true;
            else
                return false;
        }
        for (Annotation ai : qi.qualifiers) {
            if (ai.annotationType().getName().equals(Default.class.getName())) {
                if (!hasDefault(qp.qualifiers)) {
                    return false;
                }
            } else {
                boolean found = false;
                for (Annotation ap : qp.qualifiers) {
                    if (ap.equals(ai)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasDefault(final Set<Annotation> qualifiers) {
        if (qualifiers.isEmpty())
            return true;
        for (Annotation a : qualifiers) {
            if (a.annotationType().getName().equals(Default.class.getName()))
                return true;
        }
        return false;
    }

    private boolean hasAny(final Set<Annotation> qualifiers) {
        for (Annotation a : qualifiers) {
            if (a.annotationType().getName().equals(Any.class.getName()))
                return true;
        }
        return false;
    }

}
