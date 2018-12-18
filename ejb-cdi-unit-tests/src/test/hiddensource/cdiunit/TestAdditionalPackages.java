package cdiunit;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.ejbcdiunit.EjbUnitRunner;

import cdiunit.packagetest.PackageImpl;
import cdiunit.packagetest.PackageInterface;

@SutPackages(PackageInterface.class)
@RunWith(CdiUnit2Runner.class)
public class TestAdditionalPackages {

    @Inject
    private PackageInterface p;

    @Test
    public void testResolvedPackage() {
        Assert.assertTrue(p instanceof PackageImpl);
    }


}