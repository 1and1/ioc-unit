package com.oneandone.cdi.tester.ejb;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

/**
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
