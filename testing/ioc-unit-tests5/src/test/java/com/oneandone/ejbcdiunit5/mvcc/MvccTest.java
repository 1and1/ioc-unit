package com.oneandone.ejbcdiunit5.mvcc;

import static com.oneandone.ejbcdiunit5.mvcc.MvccTest.Mode.H2;
import static com.oneandone.ejbcdiunit5.mvcc.MvccTest.Mode.MYSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.IocJUnit5Extension;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

@ExtendWith(IocJUnit5Extension.class)
public class MvccTest {

    Mode mode = MYSQL;
    private DB mariaDb;
    private String mariaDbUrl;

    String autoCommitFalse() {
        return switch (mode) {
            case H2 -> "set autocommit false";
            case MYSQL -> "set autocommit = 0";
        };
    }

    public Connection createConnection() throws Exception {
        switch (mode) {
            case H2: {
                Class.forName("org.h2.Driver");
                return DriverManager.getConnection("jdbc:h2:mem:testIntercepted;MODE=MySQL;MV_STORE=TRUE;DB_CLOSE_DELAY=1",
                        "sa", "");
            }
            case MYSQL: {
                if (mariaDb == null) {
                    DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder().setPort(3407);
                    mariaDb = DB.newEmbeddedDB(config.build());
                    mariaDb.start();
                    mariaDbUrl = config.getURL("test");
                }
                Class.forName("org.mariadb.jdbc.Driver").getDeclaredConstructor().newInstance();
                return DriverManager.getConnection(mariaDbUrl, "root", "");
            }
            default:
                throw new RuntimeException("unexpected dbms");
        }


    }

    public void initDb() throws Exception {

        try (Connection conn = createConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("create table a (a varchar(200))");
                stmt.execute("create table b (b varchar(200))");
                stmt.execute("insert into a (a) values (1)");
                stmt.execute("insert into a (a) values (2)");
                stmt.execute("commit");

            }
        }
    }

    @Test
    public void testH2() throws Exception {
        mode = H2;
        initDb();
        try (Connection conn1 = createConnection();
                Connection conn2 = createConnection();
                Statement stmt1 = conn1.createStatement();
                Statement stmt2 = conn2.createStatement()) {
            stmt1.execute(autoCommitFalse());
            stmt2.execute(autoCommitFalse());
            try (ResultSet res = stmt2.executeQuery("select * from a")) {
                Assertions.assertTrue(res.next());
                Assertions.assertTrue(res.next());
                Assertions.assertFalse(res.next());
            }

            stmt1.execute("insert into a (a) values (1)");
            stmt1.execute("insert into a (a) values (2)");
            stmt1.execute("commit");
            try (ResultSet res = stmt2.executeQuery("select * from a")) {

                Assertions.assertTrue(res.next());
                Assertions.assertTrue(res.next());
                Assertions.assertTrue(res.next()); // that's wrong changes on connection
                                                       // 1 should be visible after commit
                Assertions.assertTrue(res.next()); // that's wrong
                Assertions.assertFalse(res.next());
            }
            stmt2.execute("commit");
            try (ResultSet res = stmt2.executeQuery("select * from a")) {

                Assertions.assertTrue(res.first());
                Assertions.assertTrue(res.next());
                Assertions.assertTrue(res.next());
                Assertions.assertTrue(res.next());
                Assertions.assertFalse(res.next());
            }
        }
    }

    @Test
    public void testMariaDb() throws Exception {
        mode = MYSQL;
        initDb();
        try (Connection conn1 = createConnection();
                Connection conn2 = createConnection();
                Statement stmt1 = conn1.createStatement();
                Statement stmt2 = conn2.createStatement()) {
            stmt1.execute(autoCommitFalse());
            stmt2.execute(autoCommitFalse());
            stmt2.execute("select 1 from b");

            stmt1.execute("insert into a (a) values (1)");
            stmt1.execute("insert into a (a) values (2)");
            stmt1.execute("commit");
            try (ResultSet res = stmt2.executeQuery("select * from a")) {

                Assertions.assertTrue(res.next());
                Assertions.assertTrue(res.next());
                Assertions.assertFalse(res.next());
            }
            stmt2.execute("commit");
            try (ResultSet res = stmt2.executeQuery("select * from a")) {

                Assertions.assertTrue(res.next());
                Assertions.assertTrue(res.next());
                Assertions.assertTrue(res.next());
                Assertions.assertTrue(res.next());
                Assertions.assertFalse(res.next());
            }
        }
    }


    enum Mode {
        MYSQL,
        H2
    }

}
