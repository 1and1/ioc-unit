package cdiunittests;

import jakarta.inject.Inject;

public class CircularB {
    @Inject
    private CircularA a;
}
