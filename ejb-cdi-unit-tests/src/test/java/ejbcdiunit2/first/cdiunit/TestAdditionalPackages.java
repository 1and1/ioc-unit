package ejbcdiunit2.first.cdiunit;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.ejbcdiunit2.runner.EjbCdiUnit2Runner;

import ejbcdiunit2.first.cdiunit.packagetest.PackageImpl;
import ejbcdiunit2.first.cdiunit.packagetest.PackageInterface;

@SutPackages(PackageInterface.class)
@RunWith(EjbCdiUnit2Runner.class)
public class TestAdditionalPackages {

    @Inject
    private PackageInterface p;

    @Test
    public void testResolvedPackage() {
        Assert.assertTrue(p instanceof PackageImpl);
    }


}