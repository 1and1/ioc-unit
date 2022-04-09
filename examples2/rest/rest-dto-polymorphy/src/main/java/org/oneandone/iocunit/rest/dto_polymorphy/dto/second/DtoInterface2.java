package org.oneandone.iocunit.rest.dto_polymorphy.dto.second;

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
        @JsonSubTypes.Type(value = BDto1.class, name = "bdto1type"),
        @JsonSubTypes.Type(value = BDto2.class, name = "bdto2type")})
public interface DtoInterface2 {
    int getId();
}
