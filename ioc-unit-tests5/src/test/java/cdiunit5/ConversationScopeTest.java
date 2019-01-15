package cdiunit5;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.jupiter.api.Assertions;

import com.oneandone.iocunit.contexts.InConversationScope;
import com.oneandone.iocunit.contexts.InRequestScope;

/**
 * @author aschoerk
 */
public class ConversationScopeTest {

    @Inject
    private Provider<DConversationScoped> conversationScoped;


    @InRequestScope
    @InConversationScope
    public void testConversationScope() {
        DConversationScoped d1 = conversationScoped.get();
        d1.setFoo("test"); // Force scoping
        DConversationScoped d2 = conversationScoped.get();
        Assertions.assertEquals(d1, d2);

    }

    public DConversationScoped get() {
        return conversationScoped.get();
    }
}
