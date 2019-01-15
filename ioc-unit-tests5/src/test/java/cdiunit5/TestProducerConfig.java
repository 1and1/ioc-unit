package cdiunit5;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashSet;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.ProducerConfig;
import com.oneandone.iocunit.ProducerConfigExtension;

@Disabled
@ExtendWith(IocJUnit5Extension.class)
@TestClasses({TestProducerConfig.Producers.class, ProducerConfigExtension.class})
@TestProducerConfig.ProducerConfigClass(Object.class)
@TestProducerConfig.ProducerConfigNum(0)
public class TestProducerConfig {

    @Inject
    @Named("a")
    private String valueNamedA;

    @Inject
    @Named("object")
    private Object object;

    @Test
    @ProducerConfigNum(1)
    public void testA1() {
        assertEquals("A1", valueNamedA);
    }

    @Test
    @ProducerConfigNum(2)
    public void testA2() {
        assertEquals("A2", valueNamedA);
    }

    @Test
    @ProducerConfigClass(ArrayList.class)
    public void testArrayList() {
        assertEquals(ArrayList.class, object.getClass());
    }

    @Test
    @ProducerConfigClass(HashSet.class)
    public void testHashSet() {
        assertEquals(HashSet.class, object.getClass());
    }

    // example ProducerConfig annotations
    @Retention(RUNTIME)
    @Target({METHOD, TYPE})
    @ProducerConfig
    public @interface ProducerConfigNum {
        int value();
    }

    @Retention(RUNTIME)
    @Target({METHOD, TYPE})
    @ProducerConfig
    public @interface ProducerConfigClass {
        Class<?> value();
    }

    // Producers kept out of the injected testIntercepted class to avoid Weld circularity warnings:
    static class Producers {
        @Produces
        @Named("a")
        private String getValueA(ProducerConfigNum config) {
            return "A" + config.value();
        }

        @Produces
        @Named("object")
        private Object getObject(ProducerConfigClass config) throws Exception {
            return config.value().newInstance();
        }
    }

}
