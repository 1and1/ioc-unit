package org.camunda.bpm.engine.cdi.cdiunittest;

import org.camunda.bpm.engine.cdi.impl.context.DefaultContextAssociationManager;
import org.jglue.cdiunit.ProducesAlternative;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.ws.rs.Produces;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Alternative
public class CdiUnitContextAssociationManager extends DefaultContextAssociationManager {

    private static final long serialVersionUID = -1456230354054346930L;

    @ApplicationScoped
    public static class ApplicationScopedAssociation extends ScopedAssociation implements Serializable {
        private static final long serialVersionUID = 3924846638712987532L;
    }

    @Produces
    ApplicationScopedAssociation createApplicationScopedAssociation() {
        return new ApplicationScopedAssociation();
    }

    protected List<Class< ? extends ScopedAssociation>> getAvailableScopedAssociationClasses() {
        ArrayList<Class< ? extends ScopedAssociation>> scopeTypes = new ArrayList<Class< ? extends ScopedAssociation>>();
        scopeTypes.add(ConversationScopedAssociation.class);
        scopeTypes.add(RequestScopedAssociation.class);
        scopeTypes.add(ApplicationScopedAssociation.class);
        return scopeTypes;
    }
}
