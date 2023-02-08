package org.oneandone.iocunit.rest.dto_polymorphy.dto.second;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author aschoerk
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public interface DtoInterface2 {
    int getId();
}
