package com.oneandone.ejbcdiunit.ejb;

import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.cdiunit.EjbJarClasspath;
import com.oneandone.ejbcdiunit.ejbs.appexc.TestBaseClass;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.persistence.TestPersistenceFactory;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.AdditionalPackages;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ TestPersistenceFactory.class })
@AdditionalPackages({ TestBaseClass.class, TestEntity1.class })
@EjbJarClasspath(TestBaseClass.class)
public class AppExceptionTest extends TestBaseClass {

    @Override
    @Test
    public void testAppExcInCurrentTra() throws Throwable {
        super.testAppExcInCurrentTra();
    }

    @Override
    @Test
    public void testDeclaredAppExcInCurrentTra() throws Throwable {
        super.testDeclaredAppExcInCurrentTra();
    }

    @Override
    @Test
    public void testDeclaredAppRtExcInCurrentTra() throws Throwable {
        super.testDeclaredAppRtExcInCurrentTra();
    }

    @Override
    @Test
    public void testAppRTExcInCurrentTra() throws Throwable {
        super.testAppRTExcInCurrentTra();
    }

    @Override
    @Test
    public void testAppExcInRequired() throws Throwable {
        super.testAppExcInRequired();
    }

    @Override
    @Test
    public void testAppRTExcInRequired() throws Throwable {
        super.testAppRTExcInRequired();
    }

    @Override
    @Test
    public void testAppExcInRequiresNew() throws Throwable {
        super.testAppExcInRequiresNew();
    }

    @Override
    @Test
    public void testAppRTExcInRequiresNew() throws Throwable {
        super.testAppRTExcInRequiresNew();
    }

    @Override
    @Test
    public void testAppExcInSupports() throws Throwable {
        super.testAppExcInSupports();
    }

    @Override
    @Test
    public void testAppRTExcInSupports() throws Throwable {
        super.testAppRTExcInSupports();
    }

    @Override
    @Test
    public void testAppExcInNotSupported() throws Throwable {
        super.testAppExcInNotSupported();
    }

    @Override
    @Test
    public void testAppRTExcInNotSupported() throws Throwable {
        super.testAppRTExcInNotSupported();
    }


}
