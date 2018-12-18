package com.oneandone.a.ejbcdiunit2.relbuilder.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class LoggingCountingRelVisitor extends CountingRelVisitor {
    Logger logger = LoggerFactory.getLogger("CountingRelVisitor");

    @Override
    protected Object visitRel(final Rels.Rel rel, final Object p) {
        try {
            logger.info("visiting rel: {}, affected Class: {}, parent: {}, {}", rel.getClass().getSimpleName(),
                    rel.getAffectedClass() != null ? rel.getAffectedClass().getSimpleName() : "null",
                    rel.parent() != null ? rel.parent().getClass().getSimpleName() : "root",
                    rel);
        } catch (NoClassDefFoundError e) {
            logger.info("after NoClassDefFoundError {} visiting classWrapper: {}, parent: {}, {}", e,
                    rel.getClass().getSimpleName(),
                    rel.parent() != null ? rel.parent().getClass().getSimpleName() : "root",
                    rel);
        }
        return super.visitRel(rel, p);
    }
}
