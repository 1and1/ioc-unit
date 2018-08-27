package com.oneandone.ejbcdiunit5.mvcc;

import static com.oneandone.ejbcdiunit5.mvcc.MvccTest.Mode.H2;
import static com.oneandone.ejbcdiunit5.mvcc.MvccTest.Mode.MYSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.JUnit5Extension;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
public class MvccTest {

    Mode mode = MYSQL;
    private DataSource ds;
    private DB mariaDb;
    private String mariaDbUrl;;

    String autoCommitFalse() {
        switch (mode) {
            case H2: {
                return "set autocommit false";

            }
            case MYSQL: {
                return "set autocommit = 0";

            }
            default:
                throw new RuntimeException("unexpected dbms");
        }
    }

    public Connection createConnection()
            throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, ManagedProcessException {
        switch (mode) {
            case H2: {
                Class.forName("org.h2.Driver");
                Connection conn = DriverManager.getConnection("jdbc:h2:mem:testIntercepted;MODE=MySQL;MVCC=TRUE;MV_STORE=TRUE;DB_CLOSE_DELAY=1",
                        "sa", "");
                return conn;
            }
            case MYSQL: {
                if (mariaDb == null) {
                    DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder().setPort(3406);
                    mariaDb = DB.newEmbeddedDB(config.build());
                    mariaDb.start();
                    mariaDbUrl = config.getURL("test");
                }
                Object i = Class.forName("com.mysql.jdbc.Driver").newInstance();
                Connection conn = DriverManager.getConnection(mariaDbUrl, "root", "");
                return conn;
            }
            default:
                throw new RuntimeException("unexpected dbms");
        }


    }

    public void initDb() throws SQLException, ClassNotFoundException, IllegalAccessException, ManagedProcessException, InstantiationException {

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
    public void testH2() throws SQLException, ClassNotFoundException, IllegalAccessException, ManagedProcessException, InstantiationException {
        mode = H2;
        initDb();
        try (Connection conn1 = createConnection();
                Connection conn2 = createConnection();
                Statement stmt1 = conn1.createStatement();
                Statement stmt2 = conn2.createStatement();) {
            stmt1.execute(autoCommitFalse());
            stmt2.execute(autoCommitFalse());
            try (ResultSet res = stmt2.executeQuery("select * from a")) {
                Assertions.assertEquals(res.first(), true);
                Assertions.assertEquals(res.next(), true);
                Assertions.assertEquals(res.next(), false);
            }

            stmt1.execute("insert into a (a) values (1)");
            stmt1.execute("insert into a (a) values (2)");
            stmt1.execute("commit");
            try (ResultSet res = stmt2.executeQuery("select * from a")) {

                Assertions.assertEquals(res.first(), true);
                Assertions.assertEquals(res.next(), true);
                Assertions.assertEquals(res.next(), true); // that's wrong changes on connection
                                                       // 1 should be visible after commit
                Assertions.assertEquals(res.next(), true); // that's wrong
                Assertions.assertEquals(res.next(), false);
            }
            stmt2.execute("commit");
            try (ResultSet res = stmt2.executeQuery("select * from a")) {

                Assertions.assertEquals(res.first(), true);
                Assertions.assertEquals(res.next(), true);
                Assertions.assertEquals(res.next(), true);
                Assertions.assertEquals(res.next(), true);
                Assertions.assertEquals(res.next(), false);
            }


        }
    }

    @Test
    public void testMariaDb() throws SQLException, ClassNotFoundException, IllegalAccessException, ManagedProcessException, InstantiationException {
        mode = MYSQL;
        initDb();
        try (Connection conn1 = createConnection();
                Connection conn2 = createConnection();
                Statement stmt1 = conn1.createStatement();
                Statement stmt2 = conn2.createStatement();) {
            stmt1.execute(autoCommitFalse());
            stmt2.execute(autoCommitFalse());
            stmt2.execute("select 1 from b");

            stmt1.execute("insert into a (a) values (1)");
            stmt1.execute("insert into a (a) values (2)");
            stmt1.execute("commit");
            try (ResultSet res = stmt2.executeQuery("select * from a")) {

                Assertions.assertEquals(res.first(), true);
                Assertions.assertEquals(res.next(), true);
                Assertions.assertEquals(res.next(), false);
            }
            stmt2.execute("commit");
            try (ResultSet res = stmt2.executeQuery("select * from a")) {

                Assertions.assertEquals(res.first(), true);
                Assertions.assertEquals(res.next(), true);
                Assertions.assertEquals(res.next(), true);
                Assertions.assertEquals(res.next(), true);
                Assertions.assertEquals(res.next(), false);
            }


        }
    }


    enum Mode {
        MYSQL,
        H2
    }

}
