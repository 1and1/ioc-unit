package net.oneandone.ejbcdiunit.relbuilder.code;

import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.ActivatedAlternativesRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.AdditionalClassesRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.AdditionalClasspathRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.AdditionalPackageRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.BeanClassRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.ConstructorInjectRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.EjbClasspathRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.ExcludedClassesRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.InjectedFieldRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.InjectedParameterRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.Intermediate;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.ProducerFieldRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.ProducerMethodRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.Rel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.RootRel;
import static net.oneandone.ejbcdiunit.relbuilder.code.Rels.SimpleClassRel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author aschoerk
 */
public class AllRelVisitor implements RelVisitor {

    protected Object visitRel(final Rel rel, final Object p) {
        return null;
    }

    protected Object visitIntermediate(final Intermediate intermediate) {
        return visitIntermediate(intermediate, null);
    }

    protected Object visitIntermediate(final Intermediate intermediate, final Object p) {
        List<Object> results = new ArrayList<>();
        results.add(visitRel(intermediate, p));
        for (Rel rel : intermediate.children()) {
            results.add(rel.accept(this, p));
        }
        return results;
    }

    @Override
    public Object visit(final ProducerFieldRel producerFieldRel, final Object p) {
        return visitRel(producerFieldRel, p);
    }

    @Override
    public Object visit(final ProducerMethodRel producerMethodRel, final Object p) {
        return visitIntermediate(producerMethodRel, p);
    }

    @Override
    public Object visit(final InjectedFieldRel injectedFieldRel, final Object p) {
        return visitRel(injectedFieldRel, p);
    }

    @Override
    public Object visit(final BeanClassRel beanClassRel, final Object p) {
        return visitIntermediate(beanClassRel, p);
    }

    @Override
    public Object visit(final SimpleClassRel simpleClassRel, final Object p) {
        return visitIntermediate(simpleClassRel, p);
    }

    @Override
    public Object visit(final ConstructorInjectRel constructorInjectRel, final Object p) {
        return visitIntermediate(constructorInjectRel, p);
    }

    @Override
    public Object visit(final InjectedParameterRel injectedParameterRel, final Object p) {
        Object beanRes = visitIntermediate(injectedParameterRel.getBean());
        return Arrays.asList(beanRes, visitRel(injectedParameterRel, p));
    }

    @Override
    public Object visit(final RootRel rootRel, final Object p) {
        return visitIntermediate(rootRel, p);
    }

    @Override
    public Object visit(final AdditionalClasspathRel additionalClasspathRel, final Object p) {
        return visitIntermediate(additionalClasspathRel, p);
    }

    @Override
    public Object visit(final AdditionalClassesRel additionalClassesRel, final Object p) {
        return visitIntermediate(additionalClassesRel, p);
    }

    @Override
    public Object visit(final AdditionalPackageRel additionalPackageRel, final Object p) {
        return visitIntermediate(additionalPackageRel, p);
    }

    @Override
    public Object visit(final ExcludedClassesRel excludedClassesRel, final Object p) {
        return visitRel(excludedClassesRel, p);
    }

    @Override
    public Object visit(final EjbClasspathRel ejbClasspathRel, final Object p) {
        return visitRel(ejbClasspathRel, p);
    }

    @Override
    public Object visit(final ActivatedAlternativesRel activatedAlternativesRel, final Object p) {
        return visitIntermediate(activatedAlternativesRel, p);
    }
}
