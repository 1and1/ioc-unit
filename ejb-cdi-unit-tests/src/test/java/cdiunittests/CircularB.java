package cdiunittests;

import javax.inject.Inject;

public class CircularB {
    @Inject
    private CircularA a;
}
