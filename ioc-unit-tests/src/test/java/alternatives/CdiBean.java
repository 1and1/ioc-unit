package alternatives;

import javax.inject.Inject;

/**
 * @author aschoerk
 */
public class CdiBean {
    @Inject
    CdiHelperBeanIntf cdiHelperBean;

    public boolean callThis() {
        return true;
    }

    public CdiHelperBeanIntf getCdiHelperBean() {
        return cdiHelperBean;
    }
}
