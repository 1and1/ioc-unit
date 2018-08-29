/*
 * Copyright 2011 Bryn Cooke Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package cdiunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.InConversationScope;
import org.jglue.cdiunit.InRequestScope;
import org.jglue.cdiunit.InSessionScope;
import org.jglue.cdiunit.ProducesAlternative;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.oneandone.ejbcdiunit.ContextControllerEjbCdiUnit;
import com.oneandone.ejbcdiunit.cdiunit.ExcludedClasses;
import com.oneandone.ejbcdiunit5.JUnit5Extension;

@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ ESupportClass.class, ScopedFactory.class })
@ExcludedClasses({ Scoped.class }) // cdi1.0 does not recognize @Vetoed
public class TestCdiUnitRunner extends BaseTest {


    @Inject
    private AImplementation1 aImpl;

    @Inject
    private Provider<BRequestScoped> requestScoped;

    @Inject
    private Provider<CSessionScoped> sessionScoped;

    @Inject
    private Provider<DConversationScoped> conversationScoped;

    @Inject
    private PostConstructTest postConstructTest;

    @Inject
    private Provider<AInterface> a;

    @Inject
    private BeanManager beanManager;

    @Inject
    private FApplicationScoped f1;

    @Inject
    private FApplicationScoped f2;

    @Inject
    private ContextControllerEjbCdiUnit contextControllerEjbCdiUnit;

    @Inject
    private BRequestScoped request;

    @Inject
    private Conversation conversation;

    @Produces
    private ProducedViaField produced;
    @Mock
    @ProducesAlternative
    @Produces
    private AInterface mockA;
    @Inject
    private Provider<Scoped> scoped;
    @Mock
    private Runnable disposeListener;

    public static <T> T getContextualInstance(final BeanManager manager, final Class<T> type, Annotation... qualifiers) {
        T result = null;
        Bean<T> bean = (Bean<T>) manager.resolve(manager.getBeans(type, qualifiers));
        if (bean != null) {
            CreationalContext<T> context = manager.createCreationalContext(bean);
            if (context != null) {
                result = (T) manager.getReference(bean, type, context);
            }
        }
        return result;
    }

    @Produces
    public ProducedViaMethod getProducedViaMethod() {
        return new ProducedViaMethod(2);
    }

    @Test
    @InRequestScope
    public void testRequestScope() {
        BRequestScoped b1 = requestScoped.get();
        b1.setFoo("test"); // Force scoping
        BRequestScoped b2 = requestScoped.get();
        assertEquals(b1, b2);
    }

    @Test
    public void testRequestScopeFail() {
        assertThrows(ContextNotActiveException.class, () -> {
            BRequestScoped b1 = requestScoped.get();
            b1.setFoo("test"); // Force scoping
        });
    }

    @InRequestScope
    @InSessionScope
    public void testSessionScope() {
        CSessionScoped c1 = sessionScoped.get();
        c1.setFoo("test"); // Force scoping
        CSessionScoped c2 = sessionScoped.get();
        assertEquals(c1, c2);
    }

    @Test
    public void testSessionScopeFail() {
        assertThrows(ContextNotActiveException.class, () -> {
            CSessionScoped c1 = sessionScoped.get();
            c1.setFoo("test"); // Force scoping
        });
    }

    @InRequestScope
    @InConversationScope
    public void testConversationScope() {
        DConversationScoped d1 = conversationScoped.get();
        d1.setFoo("test"); // Force scoping
        DConversationScoped d2 = conversationScoped.get();
        Assertions.assertEquals(d1, d2);

    }

    @Test
    public void testConversationScopeFail() {
        assertThrows(ContextNotActiveException.class, () -> {
            DConversationScoped d1 = conversationScoped.get();
            d1.setFoo("test"); // Force scoping
        });
    }

    /**
     * Test that we can use the testIntercepted alternative annotation to specify that a mock is used
     */
    @Test
    public void testTestAlternative() {
        AInterface a1 = a.get();
        assertEquals(mockA, a1);
    }

    @Test
    public void testPostConstruct() {
        // JUnit5 implementation will not call postconstruct
        Assertions.assertTrue(postConstructTest.postConstructCalled());
    }


    @Test
    public void testBeanManager() {
        assertNotNull(getBeanManager());
        assertNotNull(beanManager);
    }

    @Test
    public void testSuper() {
        assertNotNull(aImpl.getBeanManager());
    }

    @Test
    public void testApplicationScoped() {
        assertNotNull(f1);
        assertNotNull(f2);
        assertEquals(f1, f2);

        AInterface a1 = f1.getA();
        assertEquals(mockA, a1);
    }

    @Test
    public void testContextController() {
        contextControllerEjbCdiUnit.openRequest();

        Scoped b1 = scoped.get();
        Scoped b2 = scoped.get();
        assertEquals(b1, b2);
        b1.setDisposedListener(disposeListener);
        contextControllerEjbCdiUnit.closeRequest();
        Mockito.verify(disposeListener).run();
    }

    @Test
    public void testContextControllerRequestScoped() {
        contextControllerEjbCdiUnit.openRequest();

        BRequestScoped b1 = requestScoped.get();
        b1.setFoo("Bar");
        BRequestScoped b2 = requestScoped.get();
        Assertions.assertSame(b1.getFoo(), b2.getFoo());
        contextControllerEjbCdiUnit.closeRequest();
        contextControllerEjbCdiUnit.openRequest();
        BRequestScoped b3 = requestScoped.get();
        assertEquals(null, b3.getFoo());
    }

    @Test
    public void testContextControllerSessionScoped() {
        contextControllerEjbCdiUnit.openRequest();


        CSessionScoped b1 = sessionScoped.get();
        b1.setFoo("Bar");
        CSessionScoped b2 = sessionScoped.get();
        assertEquals(b1.getFoo(), b2.getFoo());
        contextControllerEjbCdiUnit.closeRequest();
        contextControllerEjbCdiUnit.closeSession();


        contextControllerEjbCdiUnit.openRequest();
        CSessionScoped b3 = sessionScoped.get();
        assertEquals(null, b3.getFoo());

    }

    @Test
    public void testContextControllerSessionScopedWithRequest() {
        contextControllerEjbCdiUnit.openRequest();


        CSessionScoped b1 = sessionScoped.get();
        b1.setFoo("Bar");

        BRequestScoped r1 = requestScoped.get();
        b1.setFoo("Bar");
        BRequestScoped r2 = requestScoped.get();
        Assertions.assertSame(r1.getFoo(), r2.getFoo());
        contextControllerEjbCdiUnit.closeRequest();
        contextControllerEjbCdiUnit.openRequest();
        BRequestScoped r3 = requestScoped.get();
        assertEquals(null, r3.getFoo());


        CSessionScoped b2 = sessionScoped.get();
        assertEquals(b1.getFoo(), b2.getFoo());
        assertNotNull(b2.getFoo());

    }

    @Test
    public void testContextControllerConversationScoped() {
        HttpServletRequest request = contextControllerEjbCdiUnit.openRequest();
        request.getSession(true);

        conversation.begin();

        DConversationScoped b1 = conversationScoped.get();
        b1.setFoo("Bar");
        DConversationScoped b2 = conversationScoped.get();
        assertEquals(b1.getFoo(), b2.getFoo());
        conversation.end();
        contextControllerEjbCdiUnit.closeRequest();
        contextControllerEjbCdiUnit.openRequest();

        conversation.begin();
        DConversationScoped b3 = conversationScoped.get();
        assertEquals(null, b3.getFoo());
    }

    @Test
    public void testProducedViaField() {
        produced = new ProducedViaField(2);
        ProducedViaField tmpProduced = getContextualInstance(beanManager, ProducedViaField.class);
        assertEquals(tmpProduced, tmpProduced);
    }

    @Test
    public void testProducedViaMethod() {
        ProducedViaMethod tmpProduced = getContextualInstance(beanManager, ProducedViaMethod.class);
        assertNotNull(tmpProduced);
    }
}
