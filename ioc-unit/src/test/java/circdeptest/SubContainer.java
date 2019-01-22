package circdeptest;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class SubContainer extends Container {
    public int calculate(int a, int b) {
        return a+b;
    }

}
