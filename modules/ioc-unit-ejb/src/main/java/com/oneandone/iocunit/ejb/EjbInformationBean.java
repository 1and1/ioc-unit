package com.oneandone.iocunit.ejb;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

/**
 * Used to transport information from the Analyzation process to the individual started instance of weld.
 *
 * @author aschoerk
 */
@ApplicationScoped
public class EjbInformationBean {

    private List<ApplicationExceptionDescription> applicationExceptionDescriptions;

    public List<ApplicationExceptionDescription> getApplicationExceptionDescriptions() {
        return applicationExceptionDescriptions;
    }

    public void setApplicationExceptionDescriptions(final List<ApplicationExceptionDescription> applicationExceptionDescriptionsP) {
        this.applicationExceptionDescriptions = applicationExceptionDescriptionsP;
    }
}
