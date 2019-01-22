package testalt;

import javax.annotation.PostConstruct;

/**
 * @author aschoerk
 */
public class Bean {
    boolean didPostConstruct = false;
    @PostConstruct
    public void postConstruct() {
        didPostConstruct = true;
    }

}
