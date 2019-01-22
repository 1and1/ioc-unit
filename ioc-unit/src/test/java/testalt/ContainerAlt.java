package testalt;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
@Alternative
public class ContainerAlt extends Container {

    @Produces
    int producerInt = 10;
}
