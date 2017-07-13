package com.oneandone.ejbcdiunit.camunda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.ws.rs.Produces;

import org.camunda.bpm.engine.cdi.impl.context.DefaultContextAssociationManager;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Alternative
public class CdiUnitContextAssociationManager extends DefaultContextAssociationManager {

    private static final long serialVersionUID = -1456230354054346930L;

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

    @ApplicationScoped
    public static class ApplicationScopedAssociation extends ScopedAssociation implements Serializable {
        private static final long serialVersionUID = 3924846638712987532L;
    }
}
