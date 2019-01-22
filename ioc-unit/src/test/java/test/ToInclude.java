package test;

import javax.annotation.PostConstruct;

/**
 * @author aschoerk
 */
public class ToInclude {

    public ToInclude() {
        System.out.println("Called constructor");
    }

    public static int count;

    @PostConstruct
    public void postConstruct() {
        count++;
    }
}
