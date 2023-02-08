package org.oneandone.iocunit.rest.dto_polymorphy.dto.typename;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author aschoerk
 */
public class Dto1 implements DtoInterface {
    int id;
    String dto1Name;

    @JsonCreator
    public Dto1(@JsonProperty("id") final int ID, @JsonProperty("dto1Name") final String dto1Name) {
        this.id = ID;
        this.dto1Name = dto1Name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return dto1Name;
    }

    public String getDto1Name() {
        return dto1Name;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Dto1 dto1 = (Dto1) o;
        return getId() == dto1.getId() &&
               Objects.equals(getDto1Name(), dto1.getDto1Name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDto1Name());
    }
}
