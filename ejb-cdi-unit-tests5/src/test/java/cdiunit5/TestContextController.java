package cdiunit5;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.JUnit5Extension;

@ExtendWith(JUnit5Extension.class)
public class TestContextController {

    @Inject
    ContextControlledBean contextControlledBean;

    @Test
    public void testSynchronousExecution() {
        contextControlledBean.testSynchronousExecution();
    }

    @Test
    public void testAsynchronousExecution() throws ExecutionException, InterruptedException {
        contextControlledBean.testAsynchronousExecution();
    }

}
