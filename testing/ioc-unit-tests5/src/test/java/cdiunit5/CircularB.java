package cdiunit5;

import jakarta.inject.Inject;

public class CircularB {
    @Inject
    private CircularA a;
}
