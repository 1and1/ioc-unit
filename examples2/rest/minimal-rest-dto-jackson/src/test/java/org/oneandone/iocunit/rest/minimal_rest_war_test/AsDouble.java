package org.oneandone.iocunit.rest.minimal_rest_war_test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * @author aschoerk
 */
public class AsDouble extends BaseMatcher<Number> {
    Matcher submatcher;

    public AsDouble(Matcher submatcher) {
        this.submatcher = submatcher;
    }

    @Override
    public boolean matches(final Object item) {
        return Number.class.isInstance(item) && submatcher.matches(((Number) item).doubleValue());
    }

    @Override
    public void describeTo(final Description description) {
        description
                .appendText("AsDouble ")
                .appendText(" of ")
                .appendDescriptionOf(submatcher);
    }

    @Override
    public void describeMismatch(final Object item, final Description description) {
        if(Number.class.isInstance(item)) {
            submatcher.describeMismatch(item, description);
        }
        else {
            description
                    .appendText("AsDouble ")
                    .appendValue(item == null ? null : item.getClass());
        }
    }
};
