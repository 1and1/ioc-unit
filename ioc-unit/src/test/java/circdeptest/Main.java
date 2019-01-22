package circdeptest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class Main {
    @Inject
    Container bean;

    @Inject
    int produced10;
}
