package ejbcdiunit;

import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.oneandone.ejbcdiunit.ejbs.CdiMdbClient;
import com.oneandone.ejbcdiunit.ejbs.MdbEjbInfoSingleton;
import com.oneandone.ejbcdiunit.ejbs.SingletonMdbClient;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class EmbeddedTomeeMdbTest {

    static EJBContainer container;
    @Inject
    SingletonMdbClient singletonMdbClient;
    @Inject
    CdiMdbClient cdiMdbClient;
    @Inject
    MdbEjbInfoSingleton mdbEjbInfoSingleton;
    Context context;
    @Produces
    @Resource(name = "MyJmsConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(name = "myQueue1")
    private Queue myQueue1;
    @Resource(name = "myTopic")
    private Topic topic;

    @Before
    public void beforeServiceTest() throws NamingException, SystemException, NotSupportedException, JMSException {
        final Properties p = new Properties();

        p.put("exampleDS", "new://Resource?type=DataSource");
        p.put("exampleDS.JdbcDriver", "org.h2.Driver");
        p.put("exampleDS.JdbcUrl", "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=0");
        ;
        p.put("MyJmsResourceAdapter", "new://Resource?type=ActiveMQResourceAdapter");
        p.put("MyJmsResourceAdapter.ServerUrl", "vm://localhost");
        p.put("MyJmsResourceAdapter.BrokerXmlConfig", "");

        p.put("MyConnectionFactory", "new://Resource?type=javax.jms.ConnectionFactory");
        p.put("MyConnectionFactory.ResourceAdapter", "MyJmsResourceAdapter");

        p.put("MyJmsMdbContainer", "new://Container?type=MESSAGE");
        p.put("MyJmsMdbContainer.ResourceAdapter", "MyJmsResourceAdapter");

        p.put("myQueue1", "new://Resource?type=javax.jms.Queue");
        p.put("myTopic", "new://Resource?type=javax.jms.Topic");

        container = EJBContainer.createEJBContainer(p);
        context = container.getContext();
        context.bind("inject", this);
        @SuppressWarnings("resource")
        final Connection connection = connectionFactory.createConnection();

    }

    @After
    public void afterServiceTest() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SQLException {
        container.close();
    }

    @Test
    public void testQueue() {

        Assert.assertThat(singletonMdbClient, Matchers.notNullValue());
        Assert.assertThat(mdbEjbInfoSingleton, Matchers.notNullValue());


        singletonMdbClient.sendMessageToQueue();
        waitForMdbCalls(5, false);
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfQCalls(), Matchers.is(5));
        cdiMdbClient.sendMessageToQueue();
        waitForMdbCalls(10, false);
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfQCalls(), Matchers.is(10));
    }

    @Test
    public void testTopic() {

        Assert.assertThat(singletonMdbClient, Matchers.notNullValue());
        Assert.assertThat(mdbEjbInfoSingleton, Matchers.notNullValue());

        singletonMdbClient.sendMessageToTopic();
        waitForMdbCalls(10,true);
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfTCalls(), Matchers.is(10));
        cdiMdbClient.sendMessageToTopic();
        waitForMdbCalls(20, true);
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfTCalls(), Matchers.is(20));
    }

    public void waitForMdbCalls(int calls, boolean isTopic) {
        long currentTime = System.currentTimeMillis();
        while (((mdbEjbInfoSingleton.getNumberOfTCalls() < calls && isTopic)
                || (mdbEjbInfoSingleton.getNumberOfQCalls() < calls && !isTopic))
                && System.currentTimeMillis() < currentTime + 30000 * calls) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }


}
