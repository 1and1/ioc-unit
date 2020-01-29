package org.oneandone.iocunit.rest.dto_polymorphy.dto.notpolymorph;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author aschoerk
 */
public class NpDTo implements Serializable {
    private static final long serialVersionUID = 4504077643800295816L;
    private NpDto1 NPDto1;
    private NpDto2 npDto2;

    @JsonCreator
    public NpDTo(final NpDto1 NPDto1) {
        this.NPDto1 = NPDto1;
    }

    @JsonCreator
    public NpDTo(final NpDto2 npDto2) {
        this.npDto2 = npDto2;
    }

    public NpDto1 getNPDto1() {
        return NPDto1;
    }

    public NpDto2 getNpDto2() {
        return npDto2;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        NpDTo npDTo = (NpDTo) o;
        return Objects.equals(NPDto1, npDTo.NPDto1) &&
               Objects.equals(npDto2, npDTo.npDto2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(NPDto1, npDto2);
    }
}
