package org.oneandone.iocunit.rest.dto_polymorphy.dto.abstractsuper;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author aschoerk
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = CDto1.class, name = "cdto1type"),
        @JsonSubTypes.Type(value = CDto2.class, name = "cdto2type")
})
public class DtoSuper {
    private int id;

    public DtoSuper(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
