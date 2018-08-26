package cdiunit5;

import cdiunit5.packagetest.PackageImpl;
import cdiunit5.packagetest.PackageInterface;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.jglue.cdiunit.AdditionalPackages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@AdditionalPackages(PackageInterface.class)
@ExtendWith(JUnit5Extension.class)
public class TestAdditionalPackages {

    @Inject
    private PackageInterface p;

    @Test
    public void testResolvedPackage() {
        assertTrue(p instanceof PackageImpl);
    }


}