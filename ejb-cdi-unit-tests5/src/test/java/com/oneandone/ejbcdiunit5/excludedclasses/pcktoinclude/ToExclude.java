package com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class ToExclude {

    @Produces
    ToExcludeProduced toExcludeProduced = new ToExcludeProduced(10);

    @PostConstruct
    public void postConstruct() {
        ToInclude.count++;
    }

    static public class ToExcludeProduced {
        int value;

        public ToExcludeProduced(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
