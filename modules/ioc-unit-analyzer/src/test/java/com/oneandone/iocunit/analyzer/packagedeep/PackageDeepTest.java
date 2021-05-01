package com.oneandone.iocunit.analyzer.packagedeep;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
public class PackageDeepTest extends BaseTest {

    @Test
    public void canFindTestPackagesDeep() {
        this.createTest(DeepUsingTestPackagesTest.class);
        assertEquals(2, toBeStarted.size());
        Assertions.assertTrue(toBeStarted.contains(DeepUsingTestPackagesTest.class));
        Assertions.assertTrue(toBeStarted.contains(A_BBean1.class));
    }

    @Test
    public void canFindSutPackagesDeep() {
        this.createTest(DeepUsingSutPackagesTest.class);
        assertEquals(2, toBeStarted.size());
        Assertions.assertTrue(toBeStarted.contains(DeepUsingSutPackagesTest.class));
        Assertions.assertTrue(toBeStarted.contains(A_CBean2.class));
    }

    @Test
    public void cannotFindTestPackagesFlat() {
        this.createTest(FlatUsingTestPackagesTest.class, false);
        assertEquals(1, toBeStarted.size());
        Assertions.assertTrue(toBeStarted.contains(FlatUsingTestPackagesTest.class));
    }

    @Test
    public void cannotFindSutPackagesFlat() {
        this.createTest(FlatUsingSutPackagesTest.class, false);
        assertEquals(1, toBeStarted.size());
        Assertions.assertTrue(toBeStarted.contains(FlatUsingSutPackagesTest.class));
    }

    @Test
    public void canFindByGuessing() {
        this.createTest(NoPackagesTest.class);
        assertEquals(3, toBeStarted.size());
        Assertions.assertTrue(toBeStarted.contains(NoPackagesTest.class));
        Assertions.assertTrue(toBeStarted.contains(A_CBean2.class));
        Assertions.assertTrue(toBeStarted.contains(A_BBean1.class));
    }


}
