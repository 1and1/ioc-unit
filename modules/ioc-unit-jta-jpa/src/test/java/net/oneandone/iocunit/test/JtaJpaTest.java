package net.oneandone.iocunit.test;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jtajpa.JtaEntityManagerFactoryBase;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({MainBean.class, SecondBean.class})
@TestClasses({EntityManagerFactoryFactory.class})
public class JtaJpaTest {
    @Inject
    MainBean mainBean;
    @Inject
    UserTransaction userTransaction;

    @Test
    public void test() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        mainBean.call();
        userTransaction.commit();
    }

    static class TestFactory extends JtaEntityManagerFactoryBase {
        @Override
        public String getPersistenceUnitName() {
            return "test";
        }

        @Override
        @Produces
        public EntityManager produceEntityManager() {
            return super.produceEntityManager();
        }
    }
}
