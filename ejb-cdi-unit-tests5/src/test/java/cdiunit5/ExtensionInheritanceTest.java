package cdiunit5;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ InheretedExtension.class })
public class ExtensionInheritanceTest {

    @Test
    public void test() {

    }
}