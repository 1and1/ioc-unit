package iocunit.cdiunit;

import jakarta.inject.Inject;

public class CircularA {

    @Inject
    private CircularB b;

}
