package cdiunit5;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FApplicationScoped {
    @Inject
    private AInterface a;

    public AInterface getA() {
        return a;
    }

}
