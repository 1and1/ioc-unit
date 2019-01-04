package test;


import org.junit.Assert;
import org.junit.Test;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author aschoerk
 */
// @ExcludedClasses({ ExcludeTest.class })
// @ApplicationScoped
public class SubExcludeTest extends AbstractExcludeTest {
    @Test
    public void test() {
        Assert.assertEquals(0, toInclude.count);
    }
}
