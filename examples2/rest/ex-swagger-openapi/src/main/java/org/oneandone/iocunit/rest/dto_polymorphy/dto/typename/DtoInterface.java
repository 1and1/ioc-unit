package org.oneandone.iocunit.rest.dto_polymorphy.dto.typename;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author aschoerk
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes(value = {
        @Type(value = Dto1.class, name = "dto1type"),
        @Type(value = Dto2.class, name = "dto2type")
})
public interface DtoInterface {
    int getId();
    String getName();
}
