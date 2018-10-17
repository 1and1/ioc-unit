package net.oneandone.ejbcdiunit.relbuilder.code;

/**
 * @author aschoerk
 */
public class CountingRelVisitor extends AllRelVisitor {
    int count = 0;

    @Override
    protected Object visitRel(final CdiRelBuilder.Rel rel, final Object p) {
        count++;
        return null;
    }

    public int getCount() {
        return count;
    }
}
