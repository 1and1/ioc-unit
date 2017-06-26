package com.oneandone.ejbcdiunit.helpers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

/**
 * @author aschoerk
 */
public class MySqlStarter {

    private static DB mariaDb;

    public String url = null;

    @PostConstruct
    public void create() throws ManagedProcessException {
        if (mariaDb == null) {
            DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder().setPort(3406);
            mariaDb = DB.newEmbeddedDB(config.build());
            mariaDb.start();
            url = config.getURL("test");
        }
    }

    @PreDestroy
    public void destroy() throws ManagedProcessException {
    }
}
