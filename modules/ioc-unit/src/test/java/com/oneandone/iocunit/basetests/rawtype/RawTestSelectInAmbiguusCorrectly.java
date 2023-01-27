package com.oneandone.iocunit.basetests.rawtype;

import java.util.List;

import jakarta.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasspaths(RawProducer.class)
public class RawTestSelectInAmbiguusCorrectly {
    @Inject
    StringList stringListSub;

    @Inject
    List list;

    @Inject
    List<String> stringList;

    @Inject
    RawListSub rawListSub;

    @Test
    public void test() {
        Assert.assertEquals(RawListSub.class, rawListSub.getClass());
        Assert.assertEquals(StringList.class, stringListSub.getClass());
        Assert.assertEquals(RawListSub.class, list.getClass());
        Assert.assertEquals(StringList.class, stringList.getClass());

    }
}
