package alternatives;

import jakarta.enterprise.inject.Alternative;

/**
 * @author aschoerk
 */
@Alternative
public class CdiHelperBeanAlt implements CdiHelperBeanIntf {
    @Override
    public boolean callHelper() {
        return false;
    }
}