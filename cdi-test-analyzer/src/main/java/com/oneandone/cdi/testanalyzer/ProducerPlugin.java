package com.oneandone.cdi.testanalyzer;

import javax.enterprise.inject.spi.Extension;
import java.lang.annotation.Annotation;


public interface ProducerPlugin {
    boolean isProducing(Annotation[] annotations);
    Class<? extends Extension> extensionToInstall();
}
