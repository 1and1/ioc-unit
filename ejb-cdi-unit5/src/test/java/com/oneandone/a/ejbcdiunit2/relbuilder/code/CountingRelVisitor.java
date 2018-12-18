package com.oneandone.a.ejbcdiunit2.relbuilder.code;

/**
 * @author aschoerk
 */
public class CountingRelVisitor extends AllRelVisitor {
    int count = 0;

    @Override
    protected Object visitRel(final Rels.Rel rel, final Object p) {
        count++;
        return null;
    }

    public int getCount() {
        return count;
    }
}
