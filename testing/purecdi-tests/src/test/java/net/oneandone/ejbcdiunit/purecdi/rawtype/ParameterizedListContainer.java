package net.oneandone.ejbcdiunit.purecdi.rawtype;

import java.util.List;

import jakarta.inject.Inject;

/**
 * @author aschoerk
 */
public class ParameterizedListContainer {
    @Inject
    List<String> list;
}
