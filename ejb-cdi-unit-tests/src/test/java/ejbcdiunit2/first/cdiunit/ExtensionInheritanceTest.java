package ejbcdiunit2.first.cdiunit;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.ejbcdiunit2.runner.EjbCdiUnit2Runner;

@RunWith(EjbCdiUnit2Runner.class)
@TestClasses({ InheretedExtension.class })
public class ExtensionInheritanceTest {

    @Test
    public void test() {

    }
}