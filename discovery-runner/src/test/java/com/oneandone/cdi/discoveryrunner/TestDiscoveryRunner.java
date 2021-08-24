package com.oneandone.cdi.discoveryrunner;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.xml.bind.ValidationException;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(WeldDiscoveryRunner.class)
public class TestDiscoveryRunner {

    @Inject
    Bean bean;

    @Test
    public void startTest() {
        assertEquals((Integer) 10, bean.returnInt(10));
    }

    @Test(expected = ValidationException.class)
    public void generateValidationException() throws ValidationException {
        bean.throwValidationException();
        throw new NullPointerException("expected Validation instead of Nullpointerexception");
    }

}
