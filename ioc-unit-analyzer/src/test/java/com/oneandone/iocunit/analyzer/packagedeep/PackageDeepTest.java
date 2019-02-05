package com.oneandone.iocunit.analyzer.packagedeep;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.packagedeep.teststructure.DeepUsingSutPackagesTest;
import com.oneandone.iocunit.analyzer.packagedeep.teststructure.DeepUsingTestPackagesTest;
import com.oneandone.iocunit.analyzer.packagedeep.teststructure.FlatUsingSutPackagesTest;
import com.oneandone.iocunit.analyzer.packagedeep.teststructure.FlatUsingTestPackagesTest;
import com.oneandone.iocunit.analyzer.packagedeep.teststructure.NoPackagesTest;
import com.oneandone.iocunit.analyzer.packagedeep.teststructure.a.b.A_BBean1;
import com.oneandone.iocunit.analyzer.packagedeep.teststructure.a.c.A_CBean2;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class PackageDeepTest extends BaseTest {

    @Test
    public void canFindTestPackagesDeep() {
        this.createTest(DeepUsingTestPackagesTest.class);
        assertEquals(2,toBeStarted.size());
        Assert.assertTrue(toBeStarted.contains(DeepUsingTestPackagesTest.class));
        Assert.assertTrue(toBeStarted.contains(A_BBean1.class));
    }

    @Test
    public void canFindSutPackagesDeep() {
        this.createTest(DeepUsingSutPackagesTest.class);
        assertEquals(2,toBeStarted.size());
        Assert.assertTrue(toBeStarted.contains(DeepUsingSutPackagesTest.class));
        Assert.assertTrue(toBeStarted.contains(A_CBean2.class));
    }

    @Test
    public void cannotFindTestPackagesFlat() {
        this.createTest(FlatUsingTestPackagesTest.class);
        assertEquals(1,toBeStarted.size());
        Assert.assertTrue(toBeStarted.contains(FlatUsingTestPackagesTest.class));
    }

    @Test
    public void cannotFindSutPackagesFlat() {
        this.createTest(FlatUsingSutPackagesTest.class);
        assertEquals(1,toBeStarted.size());
        Assert.assertTrue(toBeStarted.contains(FlatUsingSutPackagesTest.class));
    }

    @Test
    public void canFindByGuessing() {
        this.createTest(NoPackagesTest.class);
        assertEquals(3,toBeStarted.size());
        Assert.assertTrue(toBeStarted.contains(NoPackagesTest.class));
        Assert.assertTrue(toBeStarted.contains(A_CBean2.class));
        Assert.assertTrue(toBeStarted.contains(A_BBean1.class));
    }


}
