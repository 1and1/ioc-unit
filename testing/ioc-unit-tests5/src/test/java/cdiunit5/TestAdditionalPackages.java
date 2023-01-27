package cdiunit5;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocJUnit5Extension;

import cdiunit5.packagetest.PackageImpl;
import cdiunit5.packagetest.PackageInterface;

@SutPackages(PackageInterface.class)
@ExtendWith(IocJUnit5Extension.class)
public class TestAdditionalPackages {

    @Inject
    private PackageInterface p;

    @Test
    public void testResolvedPackage() {
        assertTrue(p instanceof PackageImpl);
    }


}