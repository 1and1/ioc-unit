package net.oneandone.iocunit;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.spi.Context;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * @author aschoerk
 */
public class WeldStarter {
    public void start(List<List<Class<?>>> jarClassesToStart) {
        ClassLoader lastClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            Weld weld = new Weld();
            try (WeldContainer container = weld.initialize()) {
                BeanManager bm = container.getBeanManager();
                List<CreationalContext> creationalContexts = new ArrayList<>();
                for (Class<?> clazz : jarClassesToStart.get(0)) {
                    Bean<?> bean = bm.resolve(bm.getBeans(clazz));
                    CreationalContext creationalContext = bm.createCreationalContext(bean);
                    // assumes the bean will exist only once
                    Context context = bm.getContext(ApplicationScoped.class);
                    final Object o = context.get(bean, creationalContext);
                    creationalContexts.add(creationalContext);
                }
                for (CreationalContext c : creationalContexts) {
                    c.release();
                }
            }


            Weld weld2 = new Weld().setClassLoader(this.getClass().getClassLoader())
                    .beanClasses(jarClassesToStart.get(0).toArray(new Class[jarClassesToStart.get(0).size()]));


            try (WeldContainer container = weld2.initialize()) {
                BeanManager bm = container.getBeanManager();
                List<CreationalContext> creationalContexts = new ArrayList<>();
                for (Class<?> clazz : jarClassesToStart.get(0)) {
                    Bean<?> bean = bm.resolve(bm.getBeans(clazz));
                    CreationalContext creationalContext = bm.createCreationalContext(bean);
                    // assumes the bean will exist only once
                    Context context = bm.getContext(ApplicationScoped.class);
                    final Object o = context.get(bean, creationalContext);
                    creationalContexts.add(creationalContext);
                }
                for (CreationalContext c : creationalContexts) {
                    c.release();
                }
            }
        }
        finally {
            Thread.currentThread().setContextClassLoader(lastClassLoader);
        }
    }
}
