package com.oneandone.iocunitejb.entities;

import java.rmi.RemoteException;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
@Entity
@Table(name = "test_entity_1")
public class TestEntity1 implements EntityBean {

    static Logger logger = LoggerFactory.getLogger(TestEntity1.class);

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "string_attribute")
    private String stringAttribute;

    @Column(name = "int_attribute")
    private int intAttribute;

    public TestEntity1() {

    }

    public Long getId() {
        return id;
    }

    public void setId(long idP) {
        this.id = idP;
    }

    public String getStringAttribute() {
        return stringAttribute;
    }

    public void setStringAttribute(String stringAttributeP) {
        this.stringAttribute = stringAttributeP;
    }

    public int getIntAttribute() {
        return intAttribute;
    }

    public void setIntAttribute(int intAttributeP) {
        this.intAttribute = intAttributeP;
    }

    @Resource
    @Transient
    private EJBContext ejbContext;

    public EJBContext getEjbContext() {
        return ejbContext;
    }

    /**
     * Set the associated entity context. The container invokes this method
     * on an instance after the instance has been created.
     *
     * <p> This method is called in an unspecified transaction context.
     *
     * @param ctx An EntityContext interface for the instance. The instance
     *            should store the reference to the context in an instance variable.
     * @throws EJBException    Thrown by the method to indicate a failure
     *                         caused by a system-level error.
     * @throws RemoteException This exception is defined in the method
     *                         signature to provide backward compatibility for enterprise beans
     *                         written for the EJB 1.0 specification. Enterprise beans written
     *                         for the EJB 1.1 specification should throw the
     *                         javax.ejb.EJBException instead of this exception.
     *                         Enterprise beans written for the EJB2.0 and higher specifications
     *                         must throw the javax.ejb.EJBException instead of this exception.
     */
    @Override
    public void setEntityContext(final EntityContext ctx) throws EJBException, RemoteException {
        logger.info("setEntityContext");
        ejbContext = ctx;
    }

    /**
     * Unset the associated entity context. The container calls this method
     * before removing the instance.
     *
     * <p> This is the last method that the container invokes on the instance.
     * The Java garbage collector will eventually invoke the finalize() method
     * on the instance.
     *
     * <p> This method is called in an unspecified transaction context.
     *
     * @throws EJBException    Thrown by the method to indicate a failure
     *                         caused by a system-level error.
     * @throws RemoteException This exception is defined in the method
     *                         signature to provide backward compatibility for enterprise beans
     *                         written for the EJB 1.0 specification. Enterprise beans written
     *                         for the EJB 1.1 specification should throw the
     *                         javax.ejb.EJBException instead of this exception.
     *                         Enterprise beans written for the EJB2.0 and higher specifications
     *                         must throw the javax.ejb.EJBException instead of this exception.
     */
    @Override
    public void unsetEntityContext() throws EJBException, RemoteException {
        logger.info("unsetEntityContext");
        ejbContext = null;
    }

    /**
     * A container invokes this method before it removes the EJB object
     * that is currently associated with the instance. This method
     * is invoked when a client invokes a remove operation on the
     * entity bean's home interface or the EJB object's remote interface.
     * This method transitions the instance from the ready state to the pool
     * of available instances.
     *
     * <p> This method is called in the transaction context of the remove
     * operation.
     *
     * @throws RemoveException The enterprise Bean does not allow
     *                         destruction of the object.
     * @throws EJBException    Thrown by the method to indicate a failure
     *                         caused by a system-level error.
     * @throws RemoteException This exception is defined in the method
     *                         signature to provide backward compatibility for enterprise beans
     *                         written for the EJB 1.0 specification. Enterprise beans written
     *                         for the EJB 1.1 specification should throw the
     *                         javax.ejb.EJBException instead of this exception.
     *                         Enterprise beans written for the EJB2.0 and higher specifications
     *                         must throw the javax.ejb.EJBException instead of this exception.
     */
    @Override
    public void ejbRemove() throws RemoveException, EJBException, RemoteException {

        logger.info("ejbRemove");
    }

    /**
     * A container invokes this method when the instance
     * is taken out of the pool of available instances to become associated
     * with a specific EJB object. This method transitions the instance to
     * the ready state.
     *
     * <p> This method executes in an unspecified transaction context.
     *
     * @throws EJBException    Thrown by the method to indicate a failure
     *                         caused by a system-level error.
     * @throws RemoteException This exception is defined in the method
     *                         signature to provide backward compatibility for enterprise beans
     *                         written for the EJB 1.0 specification. Enterprise beans written
     *                         for the EJB 1.1 specification should throw the
     *                         javax.ejb.EJBException instead of this exception.
     *                         Enterprise beans written for the EJB2.0 and higher specifications
     *                         must throw the javax.ejb.EJBException instead of this exception.
     */
    @Override
    public void ejbActivate() throws EJBException, RemoteException {
        logger.info("ejbActivate");
    }

    /**
     * A container invokes this method on an instance before the instance
     * becomes disassociated with a specific EJB object. After this method
     * completes, the container will place the instance into the pool of
     * available instances.
     *
     * <p> This method executes in an unspecified transaction context.
     *
     * @throws EJBException    Thrown by the method to indicate a failure
     *                         caused by a system-level error.
     * @throws RemoteException This exception is defined in the method
     *                         signature to provide backward compatibility for enterprise beans
     *                         written for the EJB 1.0 specification. Enterprise beans written
     *                         for the EJB 1.1 specification should throw the
     *                         javax.ejb.EJBException instead of this exception.
     *                         Enterprise beans written for the EJB2.0 and higher specifications
     *                         must throw the javax.ejb.EJBException instead of this exception.
     */
    @Override
    public void ejbPassivate() throws EJBException, RemoteException {
        logger.info("ejbPassivate");
    }

    /**
     * A container invokes this method to instruct the
     * instance to synchronize its state by loading it state from the
     * underlying database.
     *
     * <p> This method always executes in the transaction context determined
     * by the value of the transaction attribute in the deployment descriptor.
     *
     * @throws EJBException    Thrown by the method to indicate a failure
     *                         caused by a system-level error.
     * @throws RemoteException This exception is defined in the method
     *                         signature to provide backward compatibility for enterprise beans
     *                         written for the EJB 1.0 specification. Enterprise beans written
     *                         for the EJB 1.1 specification should throw the
     *                         javax.ejb.EJBException instead of this exception.
     *                         Enterprise beans written for the EJB2.0 and higher specifications
     *                         must throw the javax.ejb.EJBException instead of this exception.
     */
    @Override
    public void ejbLoad() throws EJBException, RemoteException {
        logger.info("ejbLoad");
    }

    /**
     * A container invokes this method to instruct the
     * instance to synchronize its state by storing it to the underlying
     * database.
     *
     * <p> This method always executes in the transaction context determined
     * by the value of the transaction attribute in the deployment descriptor.
     *
     * @throws EJBException    Thrown by the method to indicate a failure
     *                         caused by a system-level error.
     * @throws RemoteException This exception is defined in the method
     *                         signature to provide backward compatibility for enterprise beans
     *                         written for the EJB 1.0 specification. Enterprise beans written
     *                         for the EJB 1.1 specification should throw the
     *                         javax.ejb.EJBException instead of this exception.
     *                         Enterprise beans written for the EJB2.0 and higher specifications
     *                         must throw the javax.ejb.EJBException instead of this exception.
     */
    @Override
    public void ejbStore() throws EJBException, RemoteException {
        logger.info("ejbStore");
    }
}
