package com.oneandone.a.ejbcdiunit2.relbuilder.code;

/**
 * @author aschoerk
 */
public interface RelVisitor {
    Object visit(Rels.ProducerFieldRel producerFieldRel, Object p);

    Object visit(Rels.ProducerMethodRel producerMethodRel, Object p);

    Object visit(Rels.InjectedFieldRel injectedFieldRel, Object p);

    Object visit(Rels.BeanClassRel beanClassRel, Object p);

    Object visit(Rels.SimpleClassRel simpleClassRel, Object p);

    Object visit(Rels.ConstructorInjectRel constructorInjectRel, Object p);

    Object visit(Rels.InjectedParameterRel injectedParameterRel, Object p);

    Object visit(Rels.RootRel rootRel, Object p);

    Object visit(Rels.AdditionalClasspathRel additionalClasspathRel, Object p);

    Object visit(Rels.AdditionalClassesRel additionalClassesRel, Object p);

    Object visit(Rels.AdditionalPackageRel additionalPackageRel, Object p);

    Object visit(Rels.ExcludedClassesRel excludedClassesRel, Object p);

    Object visit(Rels.EjbClasspathRel ejbClasspathRel, Object p);

    Object visit(Rels.ActivatedAlternativesRel activatedAlternativesRel, Object p);
}
