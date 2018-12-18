package cdiunit;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.SutClasses;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.ejbcdiunit.EjbUnitRunner;

@RunWith(CdiUnit2Runner.class)
@SutClasses({ InheretedExtension.class })
public class ExtensionInheritanceTest {

    @Test
    public void test() {

    }
}