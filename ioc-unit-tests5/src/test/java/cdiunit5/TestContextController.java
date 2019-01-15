package cdiunit5;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.JUnit5Extension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

@ExtendWith(JUnit5Extension.class)
@SutClasses(ContextControlledBean.class)
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
