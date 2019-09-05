package org.oneandone.iocunit.rest.dto_polymorphy.dto.second;

import java.util.Objects;

import org.oneandone.iocunit.rest.dto_polymorphy.dto.typename.DtoInterface;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author aschoerk
 */
public class BDto2 implements DtoInterface, DtoInterface2 {
    int id;
    String dto2Name;

    @JsonCreator
    public BDto2(@JsonProperty("id") final int ID, @JsonProperty("dto2Name") String dto2Name) {
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
        BDto2 dto2 = (BDto2) o;
        return getId() == dto2.getId() &&
               Objects.equals(getDto2Name(), dto2.getDto2Name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDto2Name());
    }
}
