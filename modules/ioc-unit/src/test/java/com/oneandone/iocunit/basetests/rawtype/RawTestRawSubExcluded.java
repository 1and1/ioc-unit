package com.oneandone.iocunit.basetests.rawtype;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasspaths(RawProducer.class)
@ExcludedClasses({RawListSubProducer.class, RawListSub.class})
public class RawTestRawSubExcluded {
    @Inject
    StringList stringListSub;

    @Inject
    List list;

    @Inject
    List<String> stringList;

    @Test
    public void test() {
        Assert.assertEquals(ArrayList.class, list.getClass());
        Assert.assertEquals(StringList.class, stringListSub.getClass());
        Assert.assertEquals(StringList.class, stringList.getClass());

    }
}
