package cdiunit5;

import javax.inject.Inject;

public class CircularA {

    @Inject
    private CircularB b;

}
