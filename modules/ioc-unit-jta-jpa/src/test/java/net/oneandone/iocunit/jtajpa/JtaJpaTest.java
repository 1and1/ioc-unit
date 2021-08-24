package net.oneandone.iocunit.jtajpa;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.inject.Inject;
import javax.persistence.TransactionRequiredException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.TransactionalException;
import javax.transaction.UserTransaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.discoveryrunner.WeldDiscoveryExtension;

import net.oneandone.iocunit.jtajpa.beans.MainBean;

/**
 * @author aschoerk
 */
@ExtendWith(WeldDiscoveryExtension.class)
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class JtaJpaTest extends TestBeanBase {
    @Inject
    MainBean mainBean;
    @Inject
    UserTransaction userTransaction;

    @Test
    public void testReqNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        mainBean.callReqNew();
        userTransaction.begin();
        mainBean.callReqNew();
        // userTransaction.commit();
    }

    @Test
    public void reqNewCanCallMandatory() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        reqNewBean.callMandatoryBean();
    }

    @Test
    public void reqNewCanNotCallNever() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionalException.class, () ->
                reqNewBean.callNeverBean());
    }

    @Test
    public void reqNewCanCallRequired() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        reqNewBean.callRequiredBean();
    }

    @Test
    public void reqNewCanCallReqNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        reqNewBean.callReqNewBean();
    }

    @Test
    public void reqNewCanCallSupports() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        reqNewBean.callSupportsBean();
    }

    @Test
    public void reqNewCanCallNotSupp() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        reqNewBean.callNotSuppBean();
    }

    @Test
    public void requiredCanCallMandatory() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        requiredBean.callMandatoryBean();
    }

    @Test
    public void requiredCanNotCallNever() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionalException.class, () ->
                requiredBean.callNeverBean()
        );
    }

    @Test
    public void requiredCanCallReqNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        requiredBean.callReqNewBean();
    }

    @Test
    public void requiredCanCallRequired() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        requiredBean.callRequiredBean();
    }

    @Test
    public void requiredCanCallSupports() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        requiredBean.callSupportsBean();
    }

    @Test
    public void requiredCanCallNotSupp() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        requiredBean.callNotSuppBean();
    }

    @Test
    public void supportsCanNotCallMandatory() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionalException.class, () ->
                supportsBean.callMandatoryBean()
        );
    }

    @Test
    public void supportsCanCallNever() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        supportsBean.callNeverBean();
    }

    @Test
    public void supportsCanCallReqNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        supportsBean.callReqNewBean();
    }

    @Test
    public void supportsCanCallRequired() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        supportsBean.callRequiredBean();
    }

    @Test
    public void supportsCanCallSupports() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        supportsBean.callSupportsBean();
    }

    @Test
    public void supportsCanCallNotSupp() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        supportsBean.callNotSuppBean();
    }


    @Test
    public void supportsCanNotWrite() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionRequiredException.class, () ->
                supportsBean.writing());
    }


    @Test
    public void notSuppCanNotCallMandatory() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionalException.class, () ->
                notSuppBean.callMandatoryBean());
    }

    @Test
    public void notSuppCanCallNever() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        notSuppBean.callNeverBean();
    }

    @Test
    public void notSuppCanCallReqNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        notSuppBean.callReqNewBean();
    }

    @Test
    public void notSuppCanCallRequired() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        notSuppBean.callRequiredBean();
    }

    @Test
    public void notSuppCanCallSupports() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        notSuppBean.callSupportsBean();
    }

    @Test
    public void notSuppCanCallNotSupp() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        notSuppBean.callNotSuppBean();
    }

    @Test
    public void notSuppCanNotWrite() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionRequiredException.class, () ->
                notSuppBean.writing());
    }

    @Test
    public void mandatoryCanCallMandatory() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        mandatoryBean.callMandatoryBean();
        // userTransaction.commit();
    }

    @Test
    public void mandatoryCanNotCallNever() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionalException.class, () -> {
            userTransaction.begin();
            mandatoryBean.callNeverBean();
        });
        // userTransaction.commit();
    }

    @Test
    public void mandatoryCanCallRequired() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        mandatoryBean.callRequiredBean();
        // userTransaction.commit();
    }

    @Test
    public void mandatoryCanCallReqNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        mandatoryBean.callReqNewBean();
        // userTransaction.commit();
    }

    @Test
    public void mandatoryCanCallSupports() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        mandatoryBean.callSupportsBean();
        // userTransaction.commit();
    }

    @Test
    public void mandatoryCanCallNotSupp() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        mandatoryBean.callNotSuppBean();
        // userTransaction.commit();
    }

    @Test
    public void neverCanBeCalledInTransaction() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionalException.class, () -> {
            userTransaction.begin();
            neverBean.callSupportsBean();
        });
        // userTransaction.commit();
    }


    @Test
    public void neverCanNotCallMandatory() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionalException.class, () ->
                neverBean.callMandatoryBean());
    }

    @Test
    public void neverCanNotWrite() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionRequiredException.class, () ->
                neverBean.writing());
    }

    @Test
    public void neverCanNotCallNever() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        neverBean.callNeverBean();
    }

    @Test
    public void neverCanCallRequired() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        neverBean.callRequiredBean();
    }

    @Test
    public void neverCanCallReqNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        neverBean.callReqNewBean();
    }

    @Test
    public void neverCanCallSupports() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        neverBean.callSupportsBean();
    }

    @Test
    public void neverCanCallNotSupp() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        neverBean.callNotSuppBean();
    }


    @Test
    public void testMandatory() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        requiredBean.callMandatoryBean();
    }

}
