package com.oneandone.ejbcdiunit.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

import com.mockrunner.mock.jms.MockConnection;
import com.mockrunner.mock.jms.MockConnectionFactory;

/**
 * @author aschoerk
 */
class MockConnectionFactoryExt implements ConnectionFactory {
    private final MockConnectionFactory connectionFactory;

    MockConnectionFactoryExt(MockConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Connection createConnection() throws JMSException {
        return new MockConnectionExt((MockConnection) connectionFactory.createConnection());
    }

    @Override
    public Connection createConnection(String s, String s1) throws JMSException {
        return new MockConnectionExt((MockConnection) connectionFactory.createConnection(s, s1));
    }

    public QueueConnection createQueueConnection() throws JMSException {
        return connectionFactory.createQueueConnection();
    }

    public QueueConnection createQueueConnection(String name, String password) throws JMSException {
        return connectionFactory.createQueueConnection(name, password);
    }

    public TopicConnection createTopicConnection() throws JMSException {
        return connectionFactory.createTopicConnection();
    }

    public TopicConnection createTopicConnection(String name, String password) throws JMSException {
        return connectionFactory.createTopicConnection(name, password);
    }

    /**
     * Set an exception that will be passed to all
     * created connections. This can be used to
     * simulate server errors. Check out
     * {@link MockConnection#setJMSException}
     * for details.
     * @param exception the exception
     */
    public void setJMSException(JMSException exception) {
        connectionFactory.setJMSException(exception);
    }

    /**
     * Clears the list of connections
     */
    public void clearConnections() {
        connectionFactory.clearConnections();
    }

    /**
     * Returns the connection with the specified index
     * or <code>null</code> if no such connection
     * exists.
     * @param index the index
     * @return the connection
     */
    public MockConnection getConnection(int index) {
        return connectionFactory.getConnection(index);
    }

    /**
     * Returns the latest created connection
     * or <code>null</code> if no such connection
     * exists.
     * @return the connection
     */
    public MockConnection getLatestConnection() {
        return connectionFactory.getLatestConnection();
    }

    /**
     * Creates a JMSContext with the default user identity
     * and an unspecified sessionMode.
     * <p>
     * A connection and session are created for use by the new JMSContext.
     * The connection is created in stopped mode but will be automatically started
     * when a JMSConsumer is created.
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
     * using an acknowledgement mode of {@code JMSContext.AUTO_ACKNOWLEDGE}
     * For a definition of the meaning of this acknowledgement mode see the link below.
     * </ul>
     * <p>
     * In a <b>Java EE web or EJB container, when there is an active JTA transaction in progress</b>:
     * <ul>
     * <li>The session will participate in the JTA transaction and will be committed or rolled back
     * when that transaction is committed or rolled back,
     * not by calling the {@code JMSContext}'s {@code commit} or {@code rollback} methods.
     * </ul>
     * <p>
     * In the <b>Java EE web or EJB container, when there is no active JTA transaction in progress</b>:
     * <ul>
     * <li>The session will be non-transacted and received messages will be acknowledged automatically
     * using an acknowledgement mode of {@code JMSContext.AUTO_ACKNOWLEDGE}
     * For a definition of the meaning of this acknowledgement mode see the link below.
     * </ul>
     *
     * @return a newly created JMSContext
     * @see JMSContext#AUTO_ACKNOWLEDGE
     * @see ConnectionFactory#createContext(int)
     * @see ConnectionFactory#createContext(String, String)
     * @see ConnectionFactory#createContext(String, String, int)
     * @see JMSContext#createContext(int)
     * @since JMS 2.0
     */
    @Override
    public JMSContext createContext() {
        return null;
    }

    /**
     * Creates a JMSContext with the specified user identity
     * and an unspecified sessionMode.
     * <p>
     * A connection and session are created for use by the new JMSContext.
     * The connection is created in stopped mode but will be automatically started
     * when a JMSConsumer.
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
     * using an acknowledgement mode of {@code JMSContext.AUTO_ACKNOWLEDGE}
     * For a definition of the meaning of this acknowledgement mode see the link below.
     * </ul>
     * <p>
     * In a <b>Java EE web or EJB container, when there is an active JTA transaction in progress</b>:
     * <ul>
     * <li>The session will participate in the JTA transaction and will be committed or rolled back
     * when that transaction is committed or rolled back,
     * not by calling the {@code JMSContext}'s {@code commit} or {@code rollback} methods.
     * </ul>
     * <p>
     * In the <b>Java EE web or EJB container, when there is no active JTA transaction in progress</b>:
     * <ul>
     * <li>The session will be non-transacted and received messages will be acknowledged automatically
     * using an acknowledgement mode of {@code JMSContext.AUTO_ACKNOWLEDGE}
     * For a definition of the meaning of this acknowledgement mode see the link below.
     * </ul>
     *
     * @param s the caller's user name
     * @param s1 the caller's password
     * @return a newly created JMSContext
     * @see JMSContext#AUTO_ACKNOWLEDGE
     * @see ConnectionFactory#createContext()
     * @see ConnectionFactory#createContext(int)
     * @see ConnectionFactory#createContext(String, String, int)
     * @see JMSContext#createContext(int)
     * @since JMS 2.0
     */
    @Override
    public JMSContext createContext(String s, String s1) {
        return null;
    }

    /**
     * Creates a JMSContext with the specified user identity
     * and the specified session mode.
     * <p>
     * A connection and session are created for use by the new JMSContext.
     * The JMSContext is created in stopped mode but will be automatically started
     * when a JMSConsumer is created.
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
     * <li>If {@code sessionMode} is set to {@code JMSContext.SESSION_TRANSACTED} then the session
     * will use a local transaction which may subsequently be committed or rolled back
     * by calling the {@code JMSContext}'s {@code commit} or {@code rollback} methods.
     * <li>If {@code sessionMode} is set to any of
     * {@code JMSContext.CLIENT_ACKNOWLEDGE},
     * {@code JMSContext.AUTO_ACKNOWLEDGE} or
     * {@code JMSContext.DUPS_OK_ACKNOWLEDGE}.
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
     * not by calling the {@code JMSContext}'s {@code commit} or {@code rollback} methods.
     * Since the argument is ignored, developers are recommended to use
     * {@code createSession()}, which has no arguments, instead of this method.
     * </ul>
     * <p>
     * In the <b>Java EE web or EJB container, when there is no active JTA transaction in progress</b>:
     * <ul>
     * <li>The argument {@code acknowledgeMode} must be set to either of
     * {@code JMSContext.AUTO_ACKNOWLEDGE} or
     * {@code JMSContext.DUPS_OK_ACKNOWLEDGE}.
     * The session will be non-transacted and messages received by this session will be acknowledged
     * automatically according to the value of {@code acknowledgeMode}.
     * For a definition of the meaning of these acknowledgement modes see the links below.
     * The values {@code JMSContext.SESSION_TRANSACTED} and {@code JMSContext.CLIENT_ACKNOWLEDGE} may not be used.
     * </ul>
     *
     * @param s    the caller's user name
     * @param s1    the caller's password
     * @param i indicates which of four possible session modes will be used.
     *                    <ul>
     *                    <li>If this method is called in a Java SE environment or in the Java EE application client container,
     *                    the permitted values are
     *                    {@code JMSContext.SESSION_TRANSACTED},
     *                    {@code JMSContext.CLIENT_ACKNOWLEDGE},
     *                    {@code JMSContext.AUTO_ACKNOWLEDGE} and
     *                    {@code JMSContext.DUPS_OK_ACKNOWLEDGE}.
     *                    <li> If this method is called in the Java EE web or EJB container when there is an active JTA transaction in progress
     *                    then this argument is ignored.
     *                    <li>If this method is called in the Java EE web or EJB container when there is no active JTA transaction in progress, the permitted values are
     *                    {@code JMSContext.AUTO_ACKNOWLEDGE} and
     *                    {@code JMSContext.DUPS_OK_ACKNOWLEDGE}.
     *                    In this case the values {@code JMSContext.TRANSACTED} and {@code JMSContext.CLIENT_ACKNOWLEDGE} are not permitted.
     *                    </ul>
     * @return a newly created JMSContext
     * @see JMSContext#SESSION_TRANSACTED
     * @see JMSContext#CLIENT_ACKNOWLEDGE
     * @see JMSContext#AUTO_ACKNOWLEDGE
     * @see JMSContext#DUPS_OK_ACKNOWLEDGE
     * @see ConnectionFactory#createContext()
     * @see ConnectionFactory#createContext(int)
     * @see ConnectionFactory#createContext(String, String)
     * @see JMSContext#createContext(int)
     * @since JMS 2.0
     */
    @Override
    public JMSContext createContext(String s, String s1, int i) {
        return null;
    }

    /**
     * Creates a JMSContext with the default user identity
     * and the specified session mode.
     * <p>
     * A connection and session are created for use by the new JMSContext.
     * The JMSContext is created in stopped mode but will be automatically started
     * when a JMSConsumer is created.
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
     * <li>If {@code sessionMode} is set to {@code JMSContext.SESSION_TRANSACTED} then the session
     * will use a local transaction which may subsequently be committed or rolled back
     * by calling the {@code JMSContext}'s {@code commit} or {@code rollback} methods.
     * <li>If {@code sessionMode} is set to any of
     * {@code JMSContext.CLIENT_ACKNOWLEDGE},
     * {@code JMSContext.AUTO_ACKNOWLEDGE} or
     * {@code JMSContext.DUPS_OK_ACKNOWLEDGE}.
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
     * not by calling the {@code JMSContext}'s {@code commit} or {@code rollback} methods.
     * Since the argument is ignored, developers are recommended to use
     * {@code createSession()}, which has no arguments, instead of this method.
     * </ul>
     * <p>
     * In the <b>Java EE web or EJB container, when there is no active JTA transaction in progress</b>:
     * <ul>
     * <li>The argument {@code acknowledgeMode} must be set to either of
     * {@code JMSContext.AUTO_ACKNOWLEDGE} or
     * {@code JMSContext.DUPS_OK_ACKNOWLEDGE}.
     * The session will be non-transacted and messages received by this session will be acknowledged
     * automatically according to the value of {@code acknowledgeMode}.
     * For a definition of the meaning of these acknowledgement modes see the links below.
     * The values {@code JMSContext.SESSION_TRANSACTED} and {@code JMSContext.CLIENT_ACKNOWLEDGE} may not be used.
     * </ul>
     *
     * @param i indicates which of four possible session modes will be used.
     *                    <ul>
     *                    <li>If this method is called in a Java SE environment or in the Java EE application client container,
     *                    the permitted values are
     *                    {@code JMSContext.SESSION_TRANSACTED},
     *                    {@code JMSContext.CLIENT_ACKNOWLEDGE},
     *                    {@code JMSContext.AUTO_ACKNOWLEDGE} and
     *                    {@code JMSContext.DUPS_OK_ACKNOWLEDGE}.
     *                    <li> If this method is called in the Java EE web or EJB container when there is an active JTA transaction in progress
     *                    then this argument is ignored.
     *                    <li>If this method is called in the Java EE web or EJB container when there is no active JTA transaction in progress, the permitted values are
     *                    {@code JMSContext.AUTO_ACKNOWLEDGE} and
     *                    {@code JMSContext.DUPS_OK_ACKNOWLEDGE}.
     *                    In this case the values {@code JMSContext.TRANSACTED} and {@code JMSContext.CLIENT_ACKNOWLEDGE} are not permitted.
     *                    </ul>
     * @return a newly created JMSContext
     * @see JMSContext#SESSION_TRANSACTED
     * @see JMSContext#CLIENT_ACKNOWLEDGE
     * @see JMSContext#AUTO_ACKNOWLEDGE
     * @see JMSContext#DUPS_OK_ACKNOWLEDGE
     * @see ConnectionFactory#createContext()
     * @see ConnectionFactory#createContext(String, String)
     * @see ConnectionFactory#createContext(String, String, int)
     * @see JMSContext#createContext(int)
     * @since JMS 2.0
     */
    @Override
    public JMSContext createContext(int i) {
        return null;
    }
}

