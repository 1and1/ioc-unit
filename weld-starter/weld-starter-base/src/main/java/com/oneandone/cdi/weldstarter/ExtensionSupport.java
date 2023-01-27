package com.oneandone.cdi.weldstarter;

import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.PassivationCapable;

import org.apache.deltaspike.core.util.bean.BeanBuilder;

/**
 * @author aschoerk
 */
public class ExtensionSupport {
    public static void addTypeAfterBeanDiscovery(final AfterBeanDiscovery abd, BeanManager bm, Class<?> type) {
        Iterable<? extends AnnotatedType<?>> res = abd.getAnnotatedTypes(type);
        if(!res.iterator().hasNext()) {
            AnnotatedType<?> annotatedType = bm.createAnnotatedType(type);
            BeanBuilder<Object> builder = new BeanBuilder<>(bm);
            if(PassivationCapable.class.isAssignableFrom(type)) {
                builder = builder.passivationCapable(true);
            }
            abd.addBean(builder.readFromType((AnnotatedType<Object>) annotatedType).create());
        }
    }

}
