package com.oneandone.iocunit.ejb.persistence;

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

import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;

/**
 * @author aschoerk
 */
public class ConnectionDelegate implements Connection {

    private final boolean doClose;
    Connection connection;
    private Object jdbcConnectionAccess;
    private boolean inAutocommit = false;
    private JdbcSqlConverter jdbcSqlConverter;


    public ConnectionDelegate(SessionImplementor sessionImplementor, JdbcSqlConverter jdbcSqlConverter) {
        this.doClose = false;
        this.jdbcSqlConverter = jdbcSqlConverter;
        try {
            try {
                Method method = SessionImplementor.class.getMethod("connection");
                this.connection = (Connection) method.invoke(sessionImplementor);
                connection.setAutoCommit(false);

                this.jdbcConnectionAccess = null;
            } catch (NoSuchMethodException e) {
                try {
                    Method method = SessionImplementor.class.getMethod("getJdbcConnectionAccess");
                    jdbcConnectionAccess = method.invoke(sessionImplementor);
                    this.connection = ((JdbcConnectionAccess)jdbcConnectionAccess).obtainConnection();
                } catch (NoSuchMethodException | SQLException e1) {
                    throw new RuntimeException(e1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public ConnectionDelegate(final Connection connection, JdbcSqlConverter jdbcSqlConverter) {
        this.doClose = false;
        this.connection = connection;
        this.jdbcSqlConverter = jdbcSqlConverter;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.jdbcConnectionAccess = null;

    }

    @Override
    public Statement createStatement() throws SQLException {
        return new StatementDelegate(connection.createStatement(), jdbcSqlConverter);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
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
        return connection.prepareCall(convert(sql));
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(convert(sql));
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
        try {
            if(!inAutocommit) {
                connection.commit();
            }
        } catch (Exception e) {
            ;
        }
        if(jdbcConnectionAccess != null) {
            try {
                Method method = jdbcConnectionAccess.getClass().getMethod("releaseConnection", Connection.class);
                method.invoke(jdbcConnectionAccess, connection);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else {
            if(doClose) {
                connection.close();
            }
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


    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
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
        return connection.getCatalog();
    }


    @Override
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }


    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }


    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }


    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }


    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }


    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(convert(sql), resultSetType, resultSetConcurrency);
    }


    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(convert(sql), resultSetType, resultSetConcurrency);
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
        return connection.prepareStatement(convert(sql), resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareCall(convert(sql), resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(convert(sql), autoGeneratedKeys);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return connection.prepareStatement(convert(sql), columnIndexes);
    }


    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return connection.prepareStatement(convert(sql), columnNames);
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
