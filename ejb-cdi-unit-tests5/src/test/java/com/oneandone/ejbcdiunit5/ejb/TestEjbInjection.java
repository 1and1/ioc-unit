package com.oneandone.ejbcdiunit5.ejb;

import com.oneandone.ejbcdiunit.ejbs.StatelessEJB;
import com.oneandone.ejbcdiunit.persistence.TestPersistenceFactory;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.LoggerGenerator;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ejb.EJB;
import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ StatelessEJB.class, LoggerGenerator.class, TestPersistenceFactory.class })
public class TestEjbInjection {

    @EJB(name = "StatelessEJB")
    protected StatelessEJB statelessEJB;

    @Test
    public void testEjbName() {
        boolean fieldFound = false;
        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.getName().equals("statelessEJB")) {
                fieldFound = true;
                EJB ann = f.getAnnotation(EJB.class);
                assertThat(ann, notNullValue());
                assertThat(ann.name(), is("StatelessEJB"));
            }
        }
        Assertions.assertTrue(fieldFound);
    }


}
