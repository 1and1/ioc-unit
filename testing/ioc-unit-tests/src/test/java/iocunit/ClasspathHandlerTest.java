package iocunit;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.oneandone.iocunit.analyzer.ClasspathHandler;

import iocunit.cdiunit.AImplementation1;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class ClasspathHandlerTest {
    @Test
    public void testSimplePackage() throws MalformedURLException {
        Set<Class<?>> result = new HashSet<>();
        final Class<AImplementation1> additionalPackageClass = AImplementation1.class;
        checkAddPackageResult(AImplementation1.class, "");
        checkAddPackageResult(AImplementation1.class, null);
        checkAddPackageResult(AImplementation1.class, "AImplementation1");
        checkAddPackageDeepResult(AImplementation1.class, "");
        checkAddPackageDeepResult(AImplementation1.class, null);
        checkAddPackageDeepResult(AImplementation1.class, "AImplementation1");
        checkAddClasspathResult(AImplementation1.class, "");
        checkAddClasspathResult(AImplementation1.class, null);
        checkAddClasspathResult(AImplementation1.class, "AImplementation1");
    }

    @Test
    public void testJarPackage() throws MalformedURLException {
        checkAddPackageResult(Assert.class, "");
        checkAddPackageResult(Assert.class, null);
        checkAddPackageResult(Assert.class, "Assert|Assum");
        checkAddPackageDeepResult(Assert.class, "");
        checkAddPackageDeepResult(Assert.class, null);
        checkAddPackageDeepResult(Assert.class, "Assert|Assum");
        checkAddClasspathResult(Assert.class, "");
        checkAddClasspathResult(Assert.class, null);
        checkAddClasspathResult(Assert.class, "Assert|Assum");
    }

    private void checkAddPackageResult(final Class<?> additionalPackageClass, String filterRegex) throws MalformedURLException {
        Set<Class<?>> result = new HashSet<>();
        ClasspathHandler.addPackage(additionalPackageClass, result, filterRegex);
        Predicate<String> patternPred = filterRegex == null || filterRegex.isEmpty()
                ? null : Pattern.compile(filterRegex).asPredicate();
        if (patternPred == null)
            Assert.assertTrue(result.contains(additionalPackageClass));
        for (Class<?> c : result) {
            Assert.assertTrue(c.getPackage().equals(additionalPackageClass.getPackage()));
            if(patternPred != null) {
                Assert.assertTrue(patternPred.test(c.getName()));
            }
        }
    }

    private void checkAddClasspathResult(final Class<?> additionalClasspath, String filterRegex) throws MalformedURLException {
        Set<Class<?>> result = new HashSet<>();
        ClasspathHandler.addClassPath(additionalClasspath, result, null, filterRegex);
        Predicate<String> patternPred = filterRegex == null || filterRegex.isEmpty()
                ? null : Pattern.compile(filterRegex).asPredicate();
        if (patternPred == null)
            Assert.assertTrue(result.contains(additionalClasspath));
        else {
            for (Class<?> c : result) {
                    Assert.assertTrue(patternPred.test(c.getName()));
            }
        }
    }


    private void checkAddPackageDeepResult(final Class<?> additionalPackageClass, String filterRegex) throws MalformedURLException {
        Set<Class<?>> result = new HashSet<>();
        ClasspathHandler.addPackageDeep(additionalPackageClass, result, filterRegex);
        Predicate<String> patternPred = filterRegex == null || filterRegex.isEmpty()
                ? null : Pattern.compile(filterRegex).asPredicate();
        if (patternPred == null)
            Assert.assertTrue(result.contains(additionalPackageClass));
        for (Class<?> c : result) {
            Assert.assertTrue(c.getPackage().getName().startsWith(additionalPackageClass.getPackage().getName()));
            if(patternPred != null) {
                Assert.assertTrue(patternPred.test(c.getName()));
            }
        }
    }
}
