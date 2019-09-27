package org.oneandone.iocunit.rest.dto_polymorphy.dto.abstractsuper;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author aschoerk
 */
public class CDto2 extends DtoSuper {

    String dto2Name;

    @JsonCreator
    public CDto2(@JsonProperty("id") final int ID, @JsonProperty("dto2Name") String dto2Name) {
        super(ID);
        this.dto2Name = dto2Name;
    }

    public String getDto2Name() {
        return dto2Name;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        CDto2 dto2 = (CDto2) o;
        return getId() == dto2.getId() &&
               Objects.equals(getDto2Name(), dto2.getDto2Name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDto2Name());
    }
}
