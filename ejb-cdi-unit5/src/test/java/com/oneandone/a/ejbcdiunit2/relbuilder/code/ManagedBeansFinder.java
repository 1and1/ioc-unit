package com.oneandone.a.ejbcdiunit2.relbuilder.code;

/**
 * @author aschoerk
 */
public class ManagedBeansFinder extends AllRelVisitor {

    @Override
    public Object visit(final Rels.AdditionalClasspathRel additionalClasspathRel, final Object p) {
        return super.visit(additionalClasspathRel, p);
    }

    @Override
    public Object visit(final Rels.AdditionalClassesRel additionalClassesRel, final Object p) {
        return super.visit(additionalClassesRel, p);
    }

    @Override
    public Object visit(final Rels.AdditionalPackageRel additionalPackageRel, final Object p) {
        return super.visit(additionalPackageRel, p);
    }

    @Override
    public Object visit(final Rels.ActivatedAlternativesRel activatedAlternativesRel, final Object p) {
        return super.visit(activatedAlternativesRel, p);
    }

    @Override
    public Object visit(final Rels.ProducerFieldRel producerFieldRel, final Object p) {
        return super.visit(producerFieldRel, p);
    }

    @Override
    public Object visit(final Rels.ProducerMethodRel producerMethodRel, final Object p) {
        return super.visit(producerMethodRel, p);
    }
}
