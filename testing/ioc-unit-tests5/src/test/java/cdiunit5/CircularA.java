package cdiunit5;

import jakarta.inject.Inject;

public class CircularA {

    @Inject
    private CircularB b;

}
