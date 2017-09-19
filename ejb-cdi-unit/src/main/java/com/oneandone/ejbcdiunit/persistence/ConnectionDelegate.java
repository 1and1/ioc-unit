package com.oneandone.ejbcdiunit.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.hibernate.engine.spi.SessionImplementor;

/**
 * @author aschoerk
 */
public class ConnectionDelegate implements Connection {

    Connection connection;
    private Object jdbcConnectionAccess;

    public ConnectionDelegate(SessionImplementor sessionImplementor) {
        try {
            try {
                Method method = SessionImplementor.class.getMethod("connection");

                this.connection = (Connection) method.invoke(sessionImplementor);

                this.jdbcConnectionAccess = null;

            } catch (NoSuchMethodException e) {
                try {
                    Method method = SessionImplementor.class.getMethod("getJdbcConnectionAccess");
                    jdbcConnectionAccess = method.invoke(sessionImplementor);
                } catch (NoSuchMethodException e1) {
                    throw new RuntimeException(e1);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        if (jdbcConnectionAccess != null) {
            try {
                Method method = jdbcConnectionAccess.getClass().getMethod("releaseConnection", Connection.class);
                method.invoke(jdbcConnectionAccess, connection);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            ; // don't close necessary for other operations in this session.
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    /**
     * Puts this connection in read-only mode as a hint to the driver to enable database optimizations.
     * <P>
     * <B>Note:</B> This method cannot be called during a transaction.
     *
     * @param readOnly
     *            <code>true</code> enables read-only mode; <code>false</code> disables it
     * @exception SQLException
     *                if a database access error occurs, this method is called on a closed connection or this method is called during a transaction
     */
    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);
    }

    /**
     * Retrieves this <code>Connection</code> object's current catalog name.
     *
     * @return the current catalog name or <code>null</code> if there is none
     * @exception SQLException
     *                if a database access error occurs or this method is called on a closed connection
     * @see #setCatalog
     */
    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    /**
     * Sets the given catalog name in order to select a subspace of this <code>Connection</code> object's database in which to work.
     * <P>
     * If the driver does not support catalogs, it will silently ignore this request.
     * <p>
     * Calling {@code setCatalog} has no effect on previously created or prepared {@code Statement} objects. It is implementation defined whether a
     * DBMS prepare operation takes place immediately when the {@code Connection} method {@code prepareStatement} or {@code prepareCall} is invoked.
     * For maximum portability, {@code setCatalog} should be called before a {@code Statement} is created or prepared.
     *
     * @param catalog
     *            the name of a catalog (subspace in this <code>Connection</code> object's database) in which to work
     * @exception SQLException
     *                if a database access error occurs or this method is called on a closed connection
     * @see #getCatalog
     */
    @Override
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }

    /**
     * Retrieves this <code>Connection</code> object's current transaction isolation level.
     *
     * @return the current transaction isolation level, which will be one of the following constants:
     *         <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>, <code>Connection.TRANSACTION_READ_COMMITTED</code>,
     *         <code>Connection.TRANSACTION_REPEATABLE_READ</code>, <code>Connection.TRANSACTION_SERIALIZABLE</code>, or
     *         <code>Connection.TRANSACTION_NONE</code>.
     * @exception SQLException
     *                if a database access error occurs or this method is called on a closed connection
     * @see #setTransactionIsolation
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    /**
     * Attempts to change the transaction isolation level for this <code>Connection</code> object to the one given. The constants defined in the
     * interface <code>Connection</code> are the possible transaction isolation levels.
     * <P>
     * <B>Note:</B> If this method is called during a transaction, the result is implementation-defined.
     *
     * @param level
     *            one of the following <code>Connection</code> constants: <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>,
     *            <code>Connection.TRANSACTION_READ_COMMITTED</code>, <code>Connection.TRANSACTION_REPEATABLE_READ</code>, or
     *            <code>Connection.TRANSACTION_SERIALIZABLE</code>. (Note that <code>Connection.TRANSACTION_NONE</code> cannot be used because it
     *            specifies that transactions are not supported.)
     * @exception SQLException
     *                if a database access error occurs, this method is called on a closed connection or the given parameter is not one of the
     *                <code>Connection</code> constants
     * @see DatabaseMetaData#supportsTransactionIsolationLevel
     * @see #getTransactionIsolation
     */
    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }

    /**
     * Retrieves the first warning reported by calls on this <code>Connection</code> object. If there is more than one warning, subsequent warnings
     * will be chained to the first one and can be retrieved by calling the method <code>SQLWarning.getNextWarning</code> on the warning that was
     * retrieved previously.
     * <P>
     * This method may not be called on a closed connection; doing so will cause an <code>SQLException</code> to be thrown.
     * <P>
     * <B>Note:</B> Subsequent warnings will be chained to this SQLWarning.
     *
     * @return the first <code>SQLWarning</code> object or <code>null</code> if there are none
     * @exception SQLException
     *                if a database access error occurs or this method is called on a closed connection
     * @see SQLWarning
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    /**
     * Clears all warnings reported for this <code>Connection</code> object. After a call to this method, the method <code>getWarnings</code> returns
     * <code>null</code> until a new warning is reported for this <code>Connection</code> object.
     *
     * @exception SQLException
     *                SQLException if a database access error occurs or this method is called on a closed connection
     */
    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

    /**
     * Creates a <code>Statement</code> object that will generate <code>ResultSet</code> objects with the given type and concurrency. This method is
     * the same as the <code>createStatement</code> method above, but it allows the default result set type and concurrency to be overridden. The
     * holdability of the created result sets can be determined by calling {@link #getHoldability}.
     *
     * @param resultSetType
     *            a result set type; one of <code>ResultSet.TYPE_FORWARD_ONLY</code>, <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
     *            <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
     * @param resultSetConcurrency
     *            a concurrency type; one of <code>ResultSet.CONCUR_READ_ONLY</code> or <code>ResultSet.CONCUR_UPDATABLE</code>
     * @return a new <code>Statement</code> object that will generate <code>ResultSet</code> objects with the given type and concurrency
     * @exception SQLException
     *                if a database access error occurs, this method is called on a closed connection or the given parameters are not
     *                <code>ResultSet</code> constants indicating type and concurrency
     * @since 1.2
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }


    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }


    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }


    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }


    @Override
    public void setHoldability(int holdability) throws SQLException {
        connection.setHoldability(holdability);
    }


    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }


    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return connection.setSavepoint(name);
    }


    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }


    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }


    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return connection.prepareStatement(sql, columnIndexes);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return connection.prepareStatement(sql, columnNames);
    }


    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }


    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }


    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }


    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }


    @Override
    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }


    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }


    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }


    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return connection.createStruct(typeName, attributes);
    }


    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema();
    }


    @Override
    public void setSchema(String schema) throws SQLException {
        connection.setSchema(schema);
    }


    @Override
    public void abort(Executor executor) throws SQLException {
        connection.abort(executor);
    }


    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor, milliseconds);
    }


    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }


    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }


    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }


}
