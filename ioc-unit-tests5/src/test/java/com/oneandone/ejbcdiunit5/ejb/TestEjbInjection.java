package com.oneandone.ejbcdiunit5.ejb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.lang.reflect.Field;

import javax.ejb.EJB;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.ejb.persistence.TestPersistenceFactory;
import com.oneandone.iocunitejb.ejbs.StatelessEJB;
import com.oneandone.ejbcdiunit5.helpers.LoggerGenerator;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@SutClasses({ StatelessEJB.class, LoggerGenerator.class, TestPersistenceFactory.class })
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
