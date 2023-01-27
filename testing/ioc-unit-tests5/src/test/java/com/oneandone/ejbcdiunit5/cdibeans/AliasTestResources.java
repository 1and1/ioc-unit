package com.oneandone.ejbcdiunit5.cdibeans;

import static org.mockito.Mockito.when;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Produces;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;

import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;
import com.oneandone.iocunitejb.cdibeans.ServiceBeanDepScopedHelper;
import com.oneandone.iocunitejb.cdibeans.ServiceBeanHelperHelper;

/**
 * @author aschoerk
 */
public class AliasTestResources {

    @Produces
    @ProducesAlternative
    @Mock
    ServiceBeanDepScopedHelper serviceBeanDepScopedHelper;

    @PostConstruct
    public void postConstruct() throws NoSuchFieldException {
        when(serviceBeanDepScopedHelper.getInitCalled()).thenReturn(111);
        FieldSetter.setField(serviceBeanDepScopedHelper,
                ServiceBeanDepScopedHelper.class.getDeclaredField("initCalled"), 112);
        // when(serviceBeanDepScopedHelper.initCalled).thenReturn(112);
        when(helperHelperProducer.getServiceBeanDepScopedHelper()).thenReturn(serviceBeanDepScopedHelper);
        Assertions.assertEquals(111, serviceBeanDepScopedHelper.getInitCalled());
        Assertions.assertEquals(112, serviceBeanDepScopedHelper.initCalled);
    }

    @Produces
    @ProducesAlternative
    @Mock
    ServiceBeanHelperHelper helperHelperProducer;
}
