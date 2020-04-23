package cdiunit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocJUnit5Extension;

@ExtendWith(IocJUnit5Extension.class)
@TestClasses({ InheretedExtension.class })
public class ExtensionInheritanceTest {

    @Test
    public void test() {

    }
}