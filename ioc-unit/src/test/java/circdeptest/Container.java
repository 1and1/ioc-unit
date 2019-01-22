package circdeptest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public abstract class Container {
    @Inject
    Bean bean;

    @Produces
    Bean producedBean = new Bean();

    @Produces
    @ProducesAlternative
    int test = 10;

    @Produces
    int notalt = 11;


    @Produces
    public Logger createLogger(InjectionPoint injectionPoint) {
        String name = injectionPoint.getMember().getDeclaringClass().getName();
        return LoggerFactory.getLogger(name);
    }

}
