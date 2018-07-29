package com.oneandone.ejbcdiunit.internal;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class EjbInformationBean {

    Logger log = LoggerFactory.getLogger("EjbInformationBean");

    public EjbInformationBean() {
        log.info("created EjbInformationBean");
    }

    private List<ApplicationExceptionDescription> applicationExceptionDescriptions;

    public List<ApplicationExceptionDescription> getApplicationExceptionDescriptions() {
        return applicationExceptionDescriptions;
    }

    public void setApplicationExceptionDescriptions(final List<ApplicationExceptionDescription> applicationExceptionDescriptionsP) {
        this.applicationExceptionDescriptions = applicationExceptionDescriptionsP;
    }
}
