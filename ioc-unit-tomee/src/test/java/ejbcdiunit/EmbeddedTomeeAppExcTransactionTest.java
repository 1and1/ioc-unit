package ejbcdiunit;

import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.AssemblyDescriptor;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.StatelessBean;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.junit.ApplicationComposerRule;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.Module;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.ejbcdiunit.ejbs.CDIClass;
import com.oneandone.ejbcdiunit.ejbs.appexc.SaveAndThrowCaller;
import com.oneandone.ejbcdiunit.ejbs.appexc.SaveAndThrower;
import com.oneandone.ejbcdiunit.ejbs.appexc.TestBaseClass;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppExcExampleInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppExcExampleInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppExcExampleNotInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppExcExampleNotInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.DerivedAppExcExampleInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.DerivedAppExcExampleInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.notrtex.DeclaredAppExcExampleInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.notrtex.DeclaredAppExcExampleInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.notrtex.DeclaredAppExcExampleNotInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.notrtex.DeclaredAppExcExampleNotInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex.DeclaredAppRtExcExampleInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex.DeclaredAppRtExcExampleInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex.DeclaredAppRtExcExampleNotInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex.DeclaredAppRtExcExampleNotInheritedRollback;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.jpa.TomeeResources;

public class EmbeddedTomeeAppExcTransactionTest extends TestBaseClass {

    @Rule
    public ApplicationComposerRule applicationComposerRule = new ApplicationComposerRule(this);

    @Produces
    @PersistenceContext(unitName = "test-unit", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @Module
    public PersistenceUnit persistence1() {
        return persistence("test-unit");
    }

    @Module
    public PersistenceUnit persistence2() {
        return persistence("EjbTestPU");
    }

    @Module
    public PersistenceUnit persistence3() {
        return persistence("EjbTestPUOperating");
    }


    public PersistenceUnit persistence(String name) {
        PersistenceUnit unit = new PersistenceUnit(name);
        unit.setJtaDataSource(name + "Database");
        unit.setNonJtaDataSource(name + "DatabaseUnmanaged");
        unit.getClazz().add(TestEntity1.class.getName());
        unit.setProperty("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
        return unit;
    }

    @Module
    public EjbModule module() {
        AssemblyDescriptor assemblyDescriptor = new AssemblyDescriptor();
        assemblyDescriptor.addApplicationException(DeclaredAppExcExampleInheritedNoRollback.class, false, true);
        assemblyDescriptor.addApplicationException(DeclaredAppExcExampleNotInheritedNoRollback.class, false, false);
        assemblyDescriptor.addApplicationException(DeclaredAppExcExampleInheritedRollback.class, false, true);
        assemblyDescriptor.addApplicationException(DeclaredAppExcExampleNotInheritedRollback.class, false, false);
        assemblyDescriptor.addApplicationException(DeclaredAppRtExcExampleInheritedNoRollback.class, false, true);
        assemblyDescriptor.addApplicationException(DeclaredAppRtExcExampleNotInheritedNoRollback.class, false, false);
        assemblyDescriptor.addApplicationException(DeclaredAppRtExcExampleInheritedRollback.class, false, true);
        assemblyDescriptor.addApplicationException(DeclaredAppRtExcExampleNotInheritedRollback.class, false, false);
        final EjbJar ejbJar = new EjbJar("test-appexc-beans")
                .enterpriseBean(new StatelessBean(SaveAndThrower.class))
                .enterpriseBean(new StatelessBean(SaveAndThrowCaller.class));
        ejbJar.setAssemblyDescriptor(assemblyDescriptor);
        EjbModule module = new EjbModule(ejbJar);
        Beans beans = new Beans();
        beans
                .managedClass(TomeeResources.class.getName())
                .managedClass(CDIClass.class.getName());
        module.setBeans(beans);
        module.setModuleId("test-appexc-module");
        return module;
    }


    @Configuration
    public Properties config() throws Exception {
        Properties p = new Properties();
        p.put("testDatabase", "new://Resource?type=DataSource");
        p.put("testDatabase.JdbcDriver", "org.h2.Driver");
        p.put("testDatabase.JdbcUrl", "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=0");
        return p;
    }

    @Before
    public void emptyEntity() throws Throwable {
        clearEntity();
    }


    @Override
    public void testAppExcInCurrentTra() throws Throwable {
        clearEntity();
        callAndCheckInCurrentTra(new AppExcExampleInheritedRollback("test"), 0L);
        callAndCheckInCurrentTra(new AppExcExampleNotInheritedRollback("test"), 0L);
        callAndCheckInCurrentTra(new AppExcExampleInheritedNoRollback("test"), 1L);
        callAndCheckInCurrentTra(new AppExcExampleNotInheritedNoRollback("test"), 2L);
        callAndCheckInCurrentTra(new DerivedAppExcExampleInheritedRollback("test"), 2L);
        // callAndCheckInCurrentTra(new DerivedAppExcExampleNotInheritedRollback("test"), 3L);
        callAndCheckInCurrentTra(new DerivedAppExcExampleInheritedNoRollback("test"), 3L);
        // callAndCheckInCurrentTra(new DerivedAppExcExampleNotInheritedNoRollback("test"), 5L);
    }

    @Override
    public void testAppExcInRequired() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInRequired(new AppExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequired(new AppExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequired(new AppExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInRequired(new AppExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInRequired(new DerivedAppExcExampleInheritedRollback("test"), 2L);
        // saveAndThrowCaller.callInRequired(new DerivedAppExcExampleNotInheritedRollback("test"), 3L);
        saveAndThrowCaller.callInRequired(new DerivedAppExcExampleInheritedNoRollback("test"), 3L);
        // saveAndThrowCaller.callInRequired(new DerivedAppExcExampleNotInheritedNoRollback("test"), 5L);
    }

    @Override
    public void testAppExcInRequiresNew() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInRequiresNew(new AppExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequiresNew(new AppExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequiresNew(new AppExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInRequiresNew(new AppExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInRequiresNew(new DerivedAppExcExampleInheritedRollback("test"), 2L);
        // saveAndThrowCaller.callInRequiresNew(new DerivedAppExcExampleNotInheritedRollback("test"), 3L);
        saveAndThrowCaller.callInRequiresNew(new DerivedAppExcExampleInheritedNoRollback("test"), 3L);
        // saveAndThrowCaller.callInRequiresNew(new DerivedAppExcExampleNotInheritedNoRollback("test"), 5L);
    }

    @Override
    public void testAppExcInSupports() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInSupports(new AppExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInSupports(new AppExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInSupports(new AppExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInSupports(new AppExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInSupports(new DerivedAppExcExampleInheritedRollback("test"), 2L);
        // saveAndThrowCaller.callInSupports(new DerivedAppExcExampleNotInheritedRollback("test"), 3L);
        saveAndThrowCaller.callInSupports(new DerivedAppExcExampleInheritedNoRollback("test"), 3L);
        // saveAndThrowCaller.callInSupports(new DerivedAppExcExampleNotInheritedNoRollback("test"), 5L);
    }

    @Override
    public void testAppExcInNotSupported() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInNotSupported(new AppExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInNotSupported(new AppExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInNotSupported(new AppExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInNotSupported(new AppExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInNotSupported(new DerivedAppExcExampleInheritedRollback("test"), 2L);
        // saveAndThrowCaller.callInNotSupported(new DerivedAppExcExampleNotInheritedRollback("test"), 3L);
        saveAndThrowCaller.callInNotSupported(new DerivedAppExcExampleInheritedNoRollback("test"), 3L);
        // saveAndThrowCaller.callInNotSupported(new DerivedAppExcExampleNotInheritedNoRollback("test"), 5L);
    }


    // ApplicationException Handling buggy in tomee
    @Ignore
    @Override
    @Test
    public void testDeclaredAppExcInCurrentTra() throws Throwable {}

    // ApplicationException Handling buggy in tomee
    @Ignore
    @Override
    @Test
    public void testDeclaredAppRtExcInCurrentTra() throws Throwable {}
}