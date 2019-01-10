package cdiunit5;

import javax.inject.Inject;

public class CircularB {
    @Inject
    private CircularA a;
}
