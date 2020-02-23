package com.oneandone.iocunit.jpa.jpa;

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

/**
 * @author aschoerk
 */
public class ConnectionDelegate implements Connection {

    private final boolean doClose;
    Connection connection;
    private boolean inAutocommit = false;
    private JdbcSqlConverter jdbcSqlConverter;
    private boolean shouldBeClosed = false;
    private Object jpaInfo;
    private JpaProvider jpaProvider;

    public void setJdbcSqlConverter(final JdbcSqlConverter jdbcSqlConverter) {
        this.jdbcSqlConverter = jdbcSqlConverter;
    }

    public Object getJpaInfo() {
        return jpaInfo;
    }

    public void setJpaInfo(final Object jpaInfo) {
        this.jpaInfo = jpaInfo;
    }

    public ConnectionDelegate(final Connection connection, JdbcSqlConverter jdbcSqlConverter) {
        this(connection, jdbcSqlConverter, true);
    }

    public ConnectionDelegate(final Connection connection, JdbcSqlConverter jdbcSqlConverter, boolean doClose) {
        this.doClose = doClose;
        this.connection = connection;
        this.jdbcSqlConverter = jdbcSqlConverter;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void checkClosed() {
        try {
            if (shouldBeClosed || connection.isClosed())
                throw new RuntimeException("connection already closed");
        } catch (SQLException e) {
            throw new RuntimeException("isClosed: ",e);
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        checkClosed();
        return new StatementDelegate(connection.createStatement(), jdbcSqlConverter);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkClosed();
        return connection.prepareStatement(convert(sql));
    }

    private String convert(final String sql) {
        if(jdbcSqlConverter != null) {
            return jdbcSqlConverter.convert(sql);
        }
        else {
            return sql;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        checkClosed();
        return connection.prepareCall(convert(sql));
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        checkClosed();
        return connection.nativeSQL(convert(sql));
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        checkClosed();
        return connection.getAutoCommit();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkClosed();
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {
        checkClosed();
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        checkClosed();
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        if (isClosed())
            return;
        try {
            if(!inAutocommit) {
                connection.commit();
            }
        } catch (Exception e) {
            ;
        }
        if(jpaProvider != null) {
            try {
                jpaProvider.close(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else {
            if(doClose) {
                shouldBeClosed = true;
                try {
                    connection.close();
                } catch(Throwable thw) {
                    throw new RuntimeException(thw);
                }
            }
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        checkClosed();
        return connection.getMetaData();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        checkClosed();
        return connection.isReadOnly();
    }


    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        checkClosed();
        connection.setReadOnly(readOnly);
    }

    /**
     * Retrieves this <code>Connection</code> object's current catalog name.
     *
     * @return the current catalog name or <code>null</code> if there is none
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     * @see #setCatalog
     */
    @Override
    public String getCatalog() throws SQLException {
        checkClosed();
        return connection.getCatalog();
    }


    @Override
    public void setCatalog(String catalog) throws SQLException {
        checkClosed();
        connection.setCatalog(catalog);
    }


    @Override
    public int getTransactionIsolation() throws SQLException {
        checkClosed();
        return connection.getTransactionIsolation();
    }


    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        checkClosed();
        connection.setTransactionIsolation(level);
    }


    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkClosed();
        return connection.getWarnings();
    }


    @Override
    public void clearWarnings() throws SQLException {
        checkClosed();
        connection.clearWarnings();
    }


    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        checkClosed();
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkClosed();
        return connection.prepareStatement(convert(sql), resultSetType, resultSetConcurrency);
    }


    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkClosed();
        return connection.prepareCall(convert(sql), resultSetType, resultSetConcurrency);
    }


    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        checkClosed();
        return connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        checkClosed();
        connection.setTypeMap(map);
    }


    @Override
    public int getHoldability() throws SQLException {
        checkClosed();
        return connection.getHoldability();
    }


    @Override
    public void setHoldability(int holdability) throws SQLException {
        checkClosed();
        connection.setHoldability(holdability);
    }


    @Override
    public Savepoint setSavepoint() throws SQLException {
        checkClosed();
        return connection.setSavepoint();
    }


    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        checkClosed();
        return connection.setSavepoint(name);
    }


    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        checkClosed();
        connection.rollback(savepoint);
    }


    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkClosed();
        connection.releaseSavepoint(savepoint);
    }


    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkClosed();
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkClosed();
        return connection.prepareStatement(convert(sql), resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkClosed();
        return connection.prepareCall(convert(sql), resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        checkClosed();
        return connection.prepareStatement(convert(sql), autoGeneratedKeys);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        checkClosed();
        return connection.prepareStatement(convert(sql), columnIndexes);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        checkClosed();
        return connection.prepareStatement(convert(sql), columnNames);
    }


    @Override
    public Clob createClob() throws SQLException {
        checkClosed();
        return connection.createClob();
    }


    @Override
    public Blob createBlob() throws SQLException {
        checkClosed();
        return connection.createBlob();
    }


    @Override
    public NClob createNClob() throws SQLException {
        checkClosed();
        return connection.createNClob();
    }


    @Override
    public SQLXML createSQLXML() throws SQLException {
        checkClosed();
        return connection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        checkClosed();
        return connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        checkClosed();
        connection.setClientInfo(name, value);
    }


    @Override
    public String getClientInfo(String name) throws SQLException {
        checkClosed();
        return connection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        checkClosed();
        return connection.getClientInfo();
    }


    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        checkClosed();
        connection.setClientInfo(properties);
    }


    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        checkClosed();
        return connection.createArrayOf(typeName, elements);
    }


    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        checkClosed();
        return connection.createStruct(typeName, attributes);
    }


    @Override
    public String getSchema() throws SQLException {
        checkClosed();
        return connection.getSchema();
    }


    @Override
    public void setSchema(String schema) throws SQLException {
        checkClosed();
        connection.setSchema(schema);
    }


    @Override
    public void abort(Executor executor) throws SQLException {
        checkClosed();
        connection.abort(executor);
    }


    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        checkClosed();
        connection.setNetworkTimeout(executor, milliseconds);
    }


    @Override
    public int getNetworkTimeout() throws SQLException {
        checkClosed();
        return connection.getNetworkTimeout();
    }


    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        checkClosed();
        return connection.unwrap(iface);
    }


    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        checkClosed();
        return connection.isWrapperFor(iface);
    }

    public void setJpaProviderProvider(final JpaProvider jpaProvider) {
        this.jpaProvider = jpaProvider;
    }
}
