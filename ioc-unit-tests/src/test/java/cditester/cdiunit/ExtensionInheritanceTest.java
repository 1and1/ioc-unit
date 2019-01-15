package cditester.cdiunit;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.CdiUnit2Runner;

@RunWith(CdiUnit2Runner.class)
@TestClasses({ InheretedExtension.class })
public class ExtensionInheritanceTest {

    @Test
    public void test() {

    }
}