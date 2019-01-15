package cditester.cdiunit;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.CdiUnit2Runner;

import cditester.cdiunit.packagetest.PackageImpl;
import cditester.cdiunit.packagetest.PackageInterface;

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