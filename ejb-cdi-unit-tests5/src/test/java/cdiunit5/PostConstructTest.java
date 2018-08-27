package cdiunit5;

import javax.annotation.PostConstruct;

/**
 * @author aschoerk
 */
public class PostConstructTest {
    private boolean postConstructCalled = false;

    @PostConstruct
    public void postConstruct() {
        this.postConstructCalled = true;
    }

    public boolean postConstructCalled() {
        return postConstructCalled;
    }
}
