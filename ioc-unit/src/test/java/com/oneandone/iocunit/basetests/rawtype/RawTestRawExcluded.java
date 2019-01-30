package com.oneandone.iocunit.basetests.rawtype;

import java.util.List;

import javax.inject.Inject;

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
@ExcludedClasses({RawProducer.class})
public class RawTestRawExcluded {
    @Inject
    StringList stringListSub;

    @Inject
    List list;

    @Inject
    List<String> stringList;

    @Test
    public void test() {
        Assert.assertEquals(RawListSub.class, list.getClass());
        Assert.assertEquals(StringList.class, stringListSub.getClass());
        Assert.assertEquals(StringList.class, stringList.getClass());

    }
}
