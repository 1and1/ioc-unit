package net.oneandone.ejbcdiunit.relbuilder.code;

/**
 * @author aschoerk
 */
public class ManagedBeansFinder extends AllRelVisitor {

    @Override
    public Object visit(final CdiRelBuilder.AdditionalClasspathRel additionalClasspathRel, final Object p) {
        return super.visit(additionalClasspathRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.AdditionalClassesRel additionalClassesRel, final Object p) {
        return super.visit(additionalClassesRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.AdditionalPackageRel additionalPackageRel, final Object p) {
        return super.visit(additionalPackageRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.ActivatedAlternativesRel activatedAlternativesRel, final Object p) {
        return super.visit(activatedAlternativesRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.ProducerFieldRel producerFieldRel, final Object p) {
        return super.visit(producerFieldRel, p);
    }

    @Override
    public Object visit(final CdiRelBuilder.ProducerMethodRel producerMethodRel, final Object p) {
        return super.visit(producerMethodRel, p);
    }
}
