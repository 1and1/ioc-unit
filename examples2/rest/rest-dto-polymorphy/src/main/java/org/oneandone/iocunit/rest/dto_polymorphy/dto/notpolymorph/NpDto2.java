package org.oneandone.iocunit.rest.dto_polymorphy.dto.notpolymorph;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author aschoerk
 */
public class NpDto2 {

    String dto2Name;

    @JsonCreator
    public NpDto2(@JsonProperty("dto2Name") String dto2Name) {
        this.dto2Name = dto2Name;
    }

    public String getDto2Name() {
        return dto2Name;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        NpDto2 dto2 = (NpDto2) o;
        return  Objects.equals(getDto2Name(), dto2.getDto2Name());
    }

    @Override
    public int hashCode() {
        return getDto2Name().hashCode();
    }
}
