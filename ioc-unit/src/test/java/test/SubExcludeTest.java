package test;


import org.junit.Assert;
import org.junit.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

/**
 * @author aschoerk
 */
// @ExcludedClasses({ ExcludeTest.class })
@Dependent
@Specializes
public class SubExcludeTest extends AbstractExcludeTest {
    @Inject
    SutBean sutBean;

    @Test
    public void test() {
        // Assert.assertEquals(0, toInclude.count);
        Assert.assertEquals(0, sutBean.toInclude.count);
    }
}
