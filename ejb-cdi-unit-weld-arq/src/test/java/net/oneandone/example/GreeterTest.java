package net.oneandone.example;

import java.io.UnsupportedEncodingException;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.AdditionalClasspaths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.ClassWithTwoDifferentEntityManagers;
import com.oneandone.ejbcdiunit.SessionContextFactory;
import com.oneandone.ejbcdiunit.SupportEjbExtended;
import com.oneandone.ejbcdiunit.cdiunit.ExcludedClasses;
import com.oneandone.ejbcdiunit.ejbs.MdbEjbInfoSingleton;
import com.oneandone.ejbcdiunit.ejbs.QMdbEjb;
import com.oneandone.ejbcdiunit.ejbs.SingletonEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessEJB;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.internal.EjbExtensionExtended;
import com.oneandone.ejbcdiunit.persistence.SimulatedTransactionManager;
import com.oneandone.ejbcdiunit.persistence.TestPersistenceFactory;
import com.oneandone.ejbcdiunit.resources.Resources;
import com.oneandone.ejbcdiunit.resourcesimulators.SimulatedUserTransaction;
import com.oneandone.ejbcdiunit.testbases.EJBTransactionTestBase;

/**
 * @author aschoerk
 */
@AdditionalClasses({ StatelessEJB.class, SingletonEJB.class,
        TestPersistenceFactory.class, SessionContextFactory.class,
        QMdbEjb.class, MdbEjbInfoSingleton.class, LoggerGenerator.class, EjbExtensionExtended.class })
@AdditionalClasspaths({ QMdbEjb.class })
@ExcludedClasses({ Resources.class, ClassWithTwoDifferentEntityManagers.class })
@SupportEjbExtended
@RunWith(Arquillian.class)
public class GreeterTest extends EJBTransactionTestBase {

    @Inject
    SimulatedTransactionManager simulatedTransactionManager;
    @Inject
    Greeter greeter;
    @Inject
    AsynchronousManager asynchronousManager;

    public GreeterTest() {
        System.out.println("calling constructor");
    }

    @Deployment
    public static JavaArchive createDeployment2() throws ClassNotFoundException, UnsupportedEncodingException {
        return new ShrinkwrapGenerator(GreeterTest.class).create();
    }

    // @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(Greeter.class)
                .addClass(AsynchronousManager.class)
                .addClass(StatelessEJB.class)
                .addClass(TestPersistenceFactory.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Produces
    UserTransaction getUserTransaction() {
        return new SimulatedUserTransaction();
    }

    @Test
    public void greet() {
        String name = "Arquillian";
        Assert.assertEquals("Hello, Arquillian!", greeter.createGreeting(name));
        greeter.greet(System.out, name);
        asynchronousManager.untilNothingLeft();
        entityManager.clear();
        outerClass.saveNewInNewTra(new TestEntity1());
        statelessEJB.saveInCurrentTransaction(new TestEntity1());
    }

}
