package com.oneandone.a.ejbcdiunit2.relbuilder.code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author aschoerk
 */
public class AllRelVisitor implements RelVisitor {

    protected Object visitRel(final Rels.Rel rel, final Object p) {
        return null;
    }

    protected Object visitIntermediate(final Rels.Intermediate intermediate) {
        return visitIntermediate(intermediate, null);
    }

    protected Object visitIntermediate(final Rels.Intermediate intermediate, final Object p) {
        List<Object> results = new ArrayList<>();
        results.add(visitRel(intermediate, p));
        for (Rels.Rel rel : intermediate.children()) {
            results.add(rel.accept(this, p));
        }
        return results;
    }

    @Override
    public Object visit(final Rels.ProducerFieldRel producerFieldRel, final Object p) {
        return visitRel(producerFieldRel, p);
    }

    @Override
    public Object visit(final Rels.ProducerMethodRel producerMethodRel, final Object p) {
        return visitIntermediate(producerMethodRel, p);
    }

    @Override
    public Object visit(final Rels.InjectedFieldRel injectedFieldRel, final Object p) {
        return visitRel(injectedFieldRel, p);
    }

    @Override
    public Object visit(final Rels.BeanClassRel beanClassRel, final Object p) {
        return visitIntermediate(beanClassRel, p);
    }

    @Override
    public Object visit(final Rels.SimpleClassRel simpleClassRel, final Object p) {
        return visitIntermediate(simpleClassRel, p);
    }

    @Override
    public Object visit(final Rels.ConstructorInjectRel constructorInjectRel, final Object p) {
        return visitIntermediate(constructorInjectRel, p);
    }

    @Override
    public Object visit(final Rels.InjectedParameterRel injectedParameterRel, final Object p) {
        Object beanRes = visitIntermediate(injectedParameterRel.getBean());
        return Arrays.asList(beanRes, visitRel(injectedParameterRel, p));
    }

    @Override
    public Object visit(final Rels.RootRel rootRel, final Object p) {
        return visitIntermediate(rootRel, p);
    }

    @Override
    public Object visit(final Rels.AdditionalClasspathRel additionalClasspathRel, final Object p) {
        return visitIntermediate(additionalClasspathRel, p);
    }

    @Override
    public Object visit(final Rels.AdditionalClassesRel additionalClassesRel, final Object p) {
        return visitIntermediate(additionalClassesRel, p);
    }

    @Override
    public Object visit(final Rels.AdditionalPackageRel additionalPackageRel, final Object p) {
        return visitIntermediate(additionalPackageRel, p);
    }

    @Override
    public Object visit(final Rels.ExcludedClassesRel excludedClassesRel, final Object p) {
        return visitRel(excludedClassesRel, p);
    }

    @Override
    public Object visit(final Rels.EjbClasspathRel ejbClasspathRel, final Object p) {
        return visitRel(ejbClasspathRel, p);
    }

    @Override
    public Object visit(final Rels.ActivatedAlternativesRel activatedAlternativesRel, final Object p) {
        return visitIntermediate(activatedAlternativesRel, p);
    }
}
