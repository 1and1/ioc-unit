package cdiunittests;

import jakarta.inject.Inject;

public class CircularA {

    @Inject
    private CircularB b;

}
