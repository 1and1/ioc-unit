package alternatives;

import javax.inject.Inject;

/**
 * @author aschoerk
 */
public class CdiHelperBean implements CdiHelperBeanIntf {

    @Inject
    private DummyClass value;

    @Override
    public boolean callHelper() {
        return true;
    }
}