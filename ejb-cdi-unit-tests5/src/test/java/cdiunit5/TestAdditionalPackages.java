package cdiunit5;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.cdi.tester.JUnit5Extension;

import cdiunit5.packagetest.PackageImpl;
import cdiunit5.packagetest.PackageInterface;

@SutPackages(PackageInterface.class)
@ExtendWith(JUnit5Extension.class)
public class TestAdditionalPackages {

    @Inject
    private PackageInterface p;

    @Test
    public void testResolvedPackage() {
        assertTrue(p instanceof PackageImpl);
    }


}