package org.oneandone.iocunit.rest.dto_polymorphy.dto;

import java.util.Objects;

import org.oneandone.iocunit.rest.dto_polymorphy.dto.second.BDto1;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.second.DtoInterface2;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.typename.Dto2;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.typename.DtoInterface;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author aschoerk
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public class ComplexDtoWithSetters {
    private DtoInterface dtoPart1;
    private DtoInterface2 dtoPart2;

    // necessary to allow filling with setters by jackson.
    public ComplexDtoWithSetters() {
    }

    public ComplexDtoWithSetters(final Dto2 dto, final BDto1 bdto) {
        this.dtoPart1 = dto;
        this.dtoPart2 = bdto;
    }

    public void setDtoPart1(final DtoInterface dtoPart1) {
        this.dtoPart1 = dtoPart1;
    }

    public void setDtoPart2(final DtoInterface2 dtoPart2) {
        this.dtoPart2 = dtoPart2;
    }

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
        ComplexDtoWithSetters that = (ComplexDtoWithSetters) o;
        return Objects.equals(dtoPart1, that.dtoPart1) &&
               Objects.equals(dtoPart2, that.dtoPart2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dtoPart1, dtoPart2);
    }
}
