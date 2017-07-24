package ejbcdiunit;

import javax.inject.Inject;

import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.ejbs.CdiMdbClient;
import com.oneandone.ejbcdiunit.ejbs.MdbEjbInfoSingleton;
import com.oneandone.ejbcdiunit.ejbs.SingletonMdbClient;

/**
 * @author aschoerk
 */
@RunWith(Arquillian.class)
public class WildflyArquillianMdbTest {
    @Inject
    SingletonMdbClient singletonMdbClient;
    @Inject
    CdiMdbClient cdiMdbClient;
    @Inject
    MdbEjbInfoSingleton mdbEjbInfoSingleton;

    @Deployment
    public static Archive<?> createTestArchive() {
        return WildflyArquillianTransactionTest.createTestArchive();
    }

    @Test
    public void testQueue() {

        Assert.assertThat(singletonMdbClient, Matchers.notNullValue());
        Assert.assertThat(mdbEjbInfoSingleton, Matchers.notNullValue());

        singletonMdbClient.sendMessageToQueue();
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfQCalls(), Matchers.is(5));
        cdiMdbClient.sendMessageToQueue();
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfQCalls(), Matchers.is(10));
    }

    @Test
    public void testTopic() {

        Assert.assertThat(singletonMdbClient, Matchers.notNullValue());
        Assert.assertThat(mdbEjbInfoSingleton, Matchers.notNullValue());

        singletonMdbClient.sendMessageToTopic();
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfTCalls(), Matchers.is(10));
        cdiMdbClient.sendMessageToTopic();
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfTCalls(), Matchers.is(20));
    }


}
