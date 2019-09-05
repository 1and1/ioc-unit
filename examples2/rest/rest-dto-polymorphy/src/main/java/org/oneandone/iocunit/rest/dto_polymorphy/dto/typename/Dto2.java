package org.oneandone.iocunit.rest.dto_polymorphy.dto.typename;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author aschoerk
 */
public class Dto2 implements DtoInterface {
    int id;
    String dto2Name;

    @JsonCreator
    public Dto2(@JsonProperty("id") final int ID, @JsonProperty("dto2Name") String dto2Name) {
        this.id = ID;
        this.dto2Name = dto2Name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override

    public String getName() {
        return dto2Name;
    }

    public String getDto2Name() {
        return dto2Name;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Dto2 dto2 = (Dto2) o;
        return getId() == dto2.getId() &&
               Objects.equals(getDto2Name(), dto2.getDto2Name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDto2Name());
    }
}
