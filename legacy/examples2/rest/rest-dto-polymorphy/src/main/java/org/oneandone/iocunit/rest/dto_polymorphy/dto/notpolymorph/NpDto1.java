package org.oneandone.iocunit.rest.dto_polymorphy.dto.notpolymorph;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author aschoerk
 */
public class NpDto1 {

    String dto1Name;

    @JsonCreator
    public NpDto1(@JsonProperty("dto1Name") final String dto1Name) {

        this.dto1Name = dto1Name;
    }

    public String getDto1Name() {
        return dto1Name;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        NpDto1 dto1 = (NpDto1) o;
        return                Objects.equals(getDto1Name(), dto1.getDto1Name());
    }

    @Override
    public int hashCode() {
        return getDto1Name().hashCode();
    }
}
