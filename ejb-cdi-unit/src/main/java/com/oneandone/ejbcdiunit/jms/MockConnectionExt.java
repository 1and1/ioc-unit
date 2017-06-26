package com.oneandone.ejbcdiunit.jms;

import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;
import com.mockrunner.mock.jms.MockConnection;
import com.mockrunner.mock.jms.MockSession;

/**
 * used to support new interface version of jms
 *
 * @author aschoerk
 */
public class MockConnectionExt implements Connection {

    private final MockConnection mockConnection;

    MockConnectionExt(MockConnection mockConnection) {
        this.mockConnection = mockConnection;
    }

    /**
     * Returns the user name.
     * @return the user name
     */
    public String getUserName() {
        return mockConnection.getUserName();
    }

    /**
     * Returns the password.
     * @return the password
     */
    public String getPassword() {
        return mockConnection.getPassword();
    }

    /**
     * Returns the {@link DestinationManager}.
     * @return the {@link DestinationManager}
     */
    public DestinationManager getDestinationManager() {
        return mockConnection.getDestinationManager();
    }

    /**
     * Returns the {@link ConfigurationManager}.
     * @return the {@link ConfigurationManager}
     */
    public ConfigurationManager getConfigurationManager() {
        return mockConnection.getConfigurationManager();
    }

    /**
     * Returns the list of {@link MockSession} objects.
     * @return the list
     */
    public List getSessionList() {
        return mockConnection.getSessionList();
    }

    /**
     * Returns a {@link MockSession}. If there's no such
     * {@link MockSession}, <code>null</code> is returned.
     * @param index the index of the session object
     * @return the session object
     */
    public MockSession getSession(int index) {
        return mockConnection.getSession(index);
    }

    /**
     * Set an exception that will be thrown when calling one
     * of the interface methods. Since the mock implementation
     * cannot fail like a full blown message server you can use
     * this method to simulate server errors. After the exception
     * was thrown it will be deleted.
     * @param exception the exception to throw
     */
    private void setJMSException(JMSException exception) {
        mockConnection.setJMSException(exception);
    }

    /**
     * Throws a <code>JMSException</code> if one is set with
     * {@link #setJMSException}. Deletes the exception.
     * @throws JMSException thrown of JMSException has been set.
     */
    public void throwJMSException() throws JMSException {
        mockConnection.throwJMSException();
    }

    /**
     * Calls the <code>ExceptionListener</code>
     * if an exception is set {@link #setJMSException}.
     * Deletes the exception after calling the <code>ExceptionListener</code>.
     */
    public void callExceptionListener() {
        mockConnection.callExceptionListener();
    }

    /**
     * Calls the <code>ExceptionListener</code>
     * using the specified exception.
     * @param exception the exception
     */
    public void callExceptionListener(JMSException exception) {
        mockConnection.callExceptionListener(exception);
    }

    /**
     * You can use this to set the <code>ConnectionMetaData</code>.
     * Usually this should not be necessary. Per default an instance
     * of MockConnectionMetaData is returned when calling
     * {@link #getMetaData}.
     * @param metaData the meta data
     */
    public void setMetaData(ConnectionMetaData metaData) {
        mockConnection.setMetaData(metaData);
    }

    @Override
    public Session createSession(boolean b, int i) throws JMSException {
        return mockConnection.createSession(b, i);
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return mockConnection.createConnectionConsumer(destination, s, serverSessionPool, i);
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return mockConnection.createDurableConnectionConsumer(topic, s, s1, serverSessionPool, i);
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return mockConnection.getMetaData();
    }

    @Override
    public String getClientID() throws JMSException {
        return mockConnection.getClientID();
    }

    @Override
    public void setClientID(String s) throws JMSException {
        mockConnection.setClientID(s);
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return mockConnection.getExceptionListener();
    }

    @Override
    public void setExceptionListener(ExceptionListener exceptionListener) throws JMSException {
        mockConnection.setExceptionListener(exceptionListener);
    }

    @Override
    public void start() throws JMSException {
        mockConnection.start();
    }

    @Override
    public void stop() throws JMSException {
        mockConnection.stop();
    }

    @Override
    public void close() throws JMSException {
        mockConnection.close();
    }

    public boolean isStarted() {
        return mockConnection.isStarted();
    }

    public boolean isStopped() {
        return mockConnection.isStopped();
    }

    public boolean isClosed() {
        return mockConnection.isClosed();
    }

    /**
     * Creates a {@code Session} object, specifying {@code sessionMode}.
     * <p>
     * The effect of setting the {@code sessionMode}
     * argument depends on whether this method is called in a Java SE environment,
     * in the Java EE application client container, or in the Java EE web or EJB container.
     * If this method is called in the Java EE web or EJB container then the
     * effect of setting the {@code sessionMode} argument also depends on
     * whether or not there is an active JTA transaction in progress.
     * <p>
     * In a <b>Java SE environment</b> or in <b>the Java EE application client container</b>:
     * <ul>
     * <li>If {@code sessionMode} is set to {@code Session.SESSION_TRANSACTED} then the session
     * will use a local transaction which may subsequently be committed or rolled back
     * by calling the session's {@code commit} or {@code rollback} methods.
     * <li>If {@code sessionMode} is set to any of
     * {@code Session.CLIENT_ACKNOWLEDGE},
     * {@code Session.AUTO_ACKNOWLEDGE} or
     * {@code Session.DUPS_OK_ACKNOWLEDGE}.
     * then the session will be non-transacted and
     * messages received by this session will be acknowledged
     * according to the value of {@code sessionMode}.
     * For a definition of the meaning of these acknowledgement modes see the links below.
     * </ul>
     * <p>
     * In a <b>Java EE web or EJB container, when there is an active JTA transaction in progress</b>:
     * <ul>
     * <li>The argument {@code sessionMode} is ignored.
     * The session will participate in the JTA transaction and will be committed or rolled back
     * when that transaction is committed or rolled back,
     * not by calling the session's {@code commit} or {@code rollback} methods.
     * Since the argument is ignored, developers are recommended to use
     * {@code createSession()}, which has no arguments, instead of this method.
     * </ul>
     * <p>
     * In the <b>Java EE web or EJB container, when there is no active JTA transaction in progress</b>:
     * <ul>
     * <li>The argument {@code acknowledgeMode} must be set to either of
     * {@code Session.AUTO_ACKNOWLEDGE} or
     * {@code Session.DUPS_OK_ACKNOWLEDGE}.
     * The session will be non-transacted and messages received by this session will be acknowledged
     * automatically according to the value of {@code acknowledgeMode}.
     * For a definition of the meaning of these acknowledgement modes see the links below.
     * The values {@code Session.SESSION_TRANSACTED} and {@code Session.CLIENT_ACKNOWLEDGE} may not be used.
     * </ul>
     * <p>
     * Applications running in the Java EE web and EJB containers must not attempt
     * to create more than one active (not closed) {@code Session} object per connection.
     * If this method is called in a Java EE web or EJB container when an active
     * {@code Session} object already exists for this connection then a {@code JMSException} will be thrown.
     *
     * @param i indicates which of four possible session modes will be used.
     *                    <ul>
     *                    <li>If this method is called in a Java SE environment or in the Java EE application client container,
     *                    the permitted values are
     *                    {@code Session.SESSION_TRANSACTED},
     *                    {@code Session.CLIENT_ACKNOWLEDGE},
     *                    {@code Session.AUTO_ACKNOWLEDGE} and
     *                    {@code Session.DUPS_OK_ACKNOWLEDGE}.
     *                    <li> If this method is called in the Java EE web or EJB container when there is an active JTA transaction in progress
     *                    then this argument is ignored.
     *                    <li>If this method is called in the Java EE web or EJB container when there is no active JTA transaction in progress, the permitted values are
     *                    {@code Session.AUTO_ACKNOWLEDGE} and
     *                    {@code Session.DUPS_OK_ACKNOWLEDGE}.
     *                    In this case the values {@code Session.TRANSACTED} and {@code Session.CLIENT_ACKNOWLEDGE} are not permitted.
     *                    </ul>
     * @return a newly created  session
     * @throws JMSException if the {@code Connection} object fails
     *                      to create a session due to
     *                      <ul>
     *                      <li>some internal error,
     *                      <li>lack of support for the specific transaction and acknowledgement mode, or
     *                      <li>because this method is being called in a Java EE web or EJB application
     *                      and an active session already exists for this connection.
     *                      </ul>
     * @see Session#SESSION_TRANSACTED
     * @see Session#AUTO_ACKNOWLEDGE
     * @see Session#CLIENT_ACKNOWLEDGE
     * @see Session#DUPS_OK_ACKNOWLEDGE
     * @see Connection#createSession(boolean, int)
     * @see Connection#createSession()
     * @since JMS 2.0
     */
    @Override
    public Session createSession(int i) throws JMSException {
        return mockConnection.createSession(false, i);
    }

    /**
     * Creates a {@code Session} object,
     * specifying no arguments.
     * <p>
     * The behaviour of the session that is created depends on
     * whether this method is called in a Java SE environment,
     * in the Java EE application client container, or in the Java EE web or EJB container.
     * If this method is called in the Java EE web or EJB container then the
     * behaviour of the session also depends on whether or not
     * there is an active JTA transaction in progress.
     * <p>
     * In a <b>Java SE environment</b> or in <b>the Java EE application client container</b>:
     * <ul>
     * <li>The session will be non-transacted and received messages will be acknowledged automatically
     * using an acknowledgement mode of {@code Session.AUTO_ACKNOWLEDGE}
     * For a definition of the meaning of this acknowledgement mode see the link below.
     * </ul>
     * <p>
     * In a <b>Java EE web or EJB container, when there is an active JTA transaction in progress</b>:
     * <ul>
     * <li>The session will participate in the JTA transaction and will be committed or rolled back
     * when that transaction is committed or rolled back,
     * not by calling the session's {@code commit} or {@code rollback} methods.
     * </ul>
     * <p>
     * In the <b>Java EE web or EJB container, when there is no active JTA transaction in progress</b>:
     * <ul>
     * <li>The session will be non-transacted and received messages will be acknowledged automatically
     * using an acknowledgement mode of {@code Session.AUTO_ACKNOWLEDGE}
     * For a definition of the meaning of this acknowledgement mode see the link below.
     * </ul>
     * <p>
     * Applications running in the Java EE web and EJB containers must not attempt
     * to create more than one active (not closed) {@code Session} object per connection.
     * If this method is called in a Java EE web or EJB container when an active
     * {@code Session} object already exists for this connection then a {@code JMSException} will be thrown.
     *
     * @return a newly created  session
     * @throws JMSException if the {@code Connection} object fails
     *                      to create a session due to
     *                      <ul>
     *                      <li>some internal error or
     *                      <li>because this method is being called in a Java EE web or EJB application
     *                      and an active session already exists for this connection.
     *                      </ul>
     * @see Session#AUTO_ACKNOWLEDGE
     * @see Connection#createSession(boolean, int)
     * @see Connection#createSession(int)
     * @since JMS 2.0
     */
    @Override
    public Session createSession() throws JMSException {
        return createSession(true, Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * Creates a connection consumer for this connection (optional operation)
     * on the specific topic using a shared non-durable subscription with
     * the specified name.
     * <p>
     * This is an expert facility not used by ordinary JMS clients.
     * <p>
     * This method must not be used in a Java EE web or EJB application. Doing
     * so may cause a {@code JMSException} to be thrown though this is not
     * guaranteed.
     *
     * @param topic            the topic to access
     * @param s the name used to identify the shared non-durable subscription
     * @param s1  only messages with properties matching the message selector
     *                         expression are delivered. A value of null or an empty string
     *                         indicates that there is no message selector for the message
     *                         consumer.
     * @param serverSessionPool      the server session pool to associate with this connection
     *                         consumer
     * @param i      the maximum number of messages that can be assigned to a
     *                         server session at one time
     * @return the connection consumer
     * @throws JMSException                if the {@code Connection} object fails to create a
     *                                     connection consumer for one of the following reasons:
     *                                     <ul>
     *                                     <li>an internal error has occurred
     *                                     <li>invalid arguments for {@code sessionPool} and
     *                                     {@code messageSelector} or
     *                                     <li>this method has been called in a Java EE web or EJB
     *                                     application (though it is not guaranteed that an exception
     *                                     is thrown in this case)
     *                                     </ul>
     * @see ConnectionConsumer
     * @since JMS 2.0
     */
    @Override
    public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;
    }

    /**
     * Creates a connection consumer for this connection (optional operation)
     * on the specific topic using a shared durable subscription with
     * the specified name.
     * <p>
     * This is an expert facility not used by ordinary JMS clients.
     * <p>
     * This method must not be used in a Java EE web or EJB application. Doing
     * so may cause a {@code JMSException} to be thrown though this is not
     * guaranteed.
     *
     * @param topic            topic to access
     * @param s the name used to identify the shared durable subscription
     * @param s1  only messages with properties matching the message selector
     *                         expression are delivered. A value of null or an empty string
     *                         indicates that there is no message selector for the message
     *                         consumer.
     * @param serverSessionPool      the server session pool to associate with this durable
     *                         connection consumer
     * @param i      the maximum number of messages that can be assigned to a
     *                         server session at one time
     * @return the durable connection consumer
     * @throws JMSException                if the {@code Connection} object fails to create a
     *                                     connection consumer for one of the following reasons:
     *                                     <ul>
     *                                     <li>an internal error has occurred
     *                                     <li>invalid arguments
     *                                     for {@code sessionPool} and {@code messageSelector} or
     *                                     <li>this method has been called in a Java EE web or EJB
     *                                     application (though it is not guaranteed that an exception
     *                                     is thrown in this case)
     *                                     </ul>
     * @see ConnectionConsumer
     * @since JMS 2.0
     */
    @Override
    public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;
    }
}

