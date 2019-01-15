package cditester;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocUnitRunner;

import cditester.producing.ProducingClass2;
import cditester.test2.Test2Interface;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutPackages({ ProducingClass2.class })
public class TestStaticProducer {
    @Inject
    Test2Interface test2b;

    @Test
    public void test() {
        Assert.assertNotNull(test2b);
        assertEquals("Test2B", test2b.call());
    }
}
