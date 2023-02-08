package org.oneandone.iocunit.rest.dto_polymorphy.dto.abstractsuper;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author aschoerk
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public class DtoSuper {
    private int id;

    public DtoSuper(final int id) {
        this.id = id;
    }

    int getId() {
        return id;
    }
}
