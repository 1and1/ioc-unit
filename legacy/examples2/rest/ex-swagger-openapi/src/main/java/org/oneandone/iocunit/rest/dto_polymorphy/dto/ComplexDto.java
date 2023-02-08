package org.oneandone.iocunit.rest.dto_polymorphy.dto;

import java.util.Objects;

import org.oneandone.iocunit.rest.dto_polymorphy.dto.second.DtoInterface2;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.typename.DtoInterface;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author aschoerk
 */
public class ComplexDto {
    @JsonCreator
    public ComplexDto(@JsonProperty("dtoPart1") DtoInterface d1, @JsonProperty("dtoPart2") DtoInterface2 d2) {
        this.dtoPart1 = d1;
        this.dtoPart2 = d2;
    }
    private DtoInterface dtoPart1;
    private DtoInterface2 dtoPart2;

    public DtoInterface getDtoPart1() {
        return dtoPart1;
    }

    public DtoInterface2 getDtoPart2() {
        return dtoPart2;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ComplexDto that = (ComplexDto) o;
        return Objects.equals(dtoPart1, that.dtoPart1) &&
               Objects.equals(dtoPart2, that.dtoPart2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dtoPart1, dtoPart2);
    }
}
