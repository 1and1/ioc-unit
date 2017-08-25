package net.oneandone.example;

import org.apache.deltaspike.core.impl.scope.DeltaSpikeContextExtension;
import org.apache.deltaspike.core.spi.activation.ClassDeactivator;
import org.apache.deltaspike.core.spi.activation.Deactivatable;

/**
 * @author aschoerk
 */
public class DeltaspikeClassDeactivator implements ClassDeactivator {
    private static final long serialVersionUID = 4260994564454029617L;

    /**
     * Provides classes which should be deactivated.
     *
     * @param targetClass
     *            class which should be checked
     * @return {@link Boolean#FALSE} if class should get activated, {@link Boolean#FALSE} if class must be available and <code>null</code> to let it
     *         as is (defined by default or other
     */
    @Override
    public Boolean isActivated(Class<? extends Deactivatable> targetClass) {
        return !targetClass.equals(DeltaSpikeContextExtension.class);
    }
}
