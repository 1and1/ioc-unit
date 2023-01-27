package com.oneandone.iocunit.jtajpa.narayana.cdi;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.interceptor.Interceptor;
import javax.naming.InitialContext;
import jakarta.transaction.TransactionSynchronizationRegistry;

import com.arjuna.ats.jta.common.jtaPropertyManager;

/**
 * <p>
 * This bean produces the {@link TransactionSynchronizationRegistry}.
 * If there is not defined the JNDI binding (the {@link InitialContext} lookup
 * has precedence over the CDI) then the CDI bean is taken for the source
 * for the instance of the txn synchronization registry.
 * </p>
 * <p>
 * This producer defines a way how the {@link TransactionSynchronizationRegistry}
 * is obtained by the application. The {@link Alternative} is used for enabling
 * the bean only in case when CDI binding test case is run.
 * </p>
 * <p>
 * If this producer is not activated then default Narayana implementation
 * of {@link TransactionSynchronizationRegistry} is used.
 * </p>
 */
@Alternative
// priority is needed for the bean being accessible from any CDI scope
@Priority(Interceptor.Priority.APPLICATION+10)
public class CDITransactionProducers {

    @Produces
    @ApplicationScoped
    public TransactionSynchronizationRegistry produceTransactionSynchronizationRegistry() {
        return jtaPropertyManager.getJTAEnvironmentBean().getTransactionSynchronizationRegistry();
    }
}
