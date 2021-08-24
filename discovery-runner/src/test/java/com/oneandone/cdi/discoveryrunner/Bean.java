package com.oneandone.cdi.discoveryrunner;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.ValidationException;

@ApplicationScoped
public class Bean {
    public Integer returnInt(int i) {
        return i;
    }

    public void throwValidationException() throws ValidationException {
        throw new ValidationException("serve Exception for Test");

    }
}
