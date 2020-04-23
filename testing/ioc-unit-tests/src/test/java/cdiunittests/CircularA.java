package cdiunittests;

import javax.inject.Inject;

public class CircularA {

    @Inject
    private CircularB b;

}
