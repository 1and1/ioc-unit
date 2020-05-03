/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.iocunit.jta.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.NamingException;

import org.h2.jdbcx.JdbcDataSource;

import com.arjuna.ats.jdbc.TransactionalDriver;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class H2HibernateTransactionalConnectionProvider extends ConnectionProviderBase {

    private static final long serialVersionUID = 5136867989194663732L;
    private final String url;
    private final TransactionalDriver arjunaJDBC2Driver;
    private final Properties dbProps;

    public H2HibernateTransactionalConnectionProvider() throws NamingException {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        url = "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=0";
        jdbcDataSource.setUrl(url);
        dbProps = new Properties();

        dbProps.put(TransactionalDriver.userName, "sa");

        dbProps.put(TransactionalDriver.password, "");

        dbProps.put(TransactionalDriver.XADataSource, jdbcDataSource);

        this.arjunaJDBC2Driver = new TransactionalDriver();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return arjunaJDBC2Driver.connect("jdbc:arjuna:" + url, dbProps);
    }



}
