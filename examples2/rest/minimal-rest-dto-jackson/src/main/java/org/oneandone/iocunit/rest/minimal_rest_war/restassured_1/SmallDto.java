package org.oneandone.iocunit.rest.minimal_rest_war.restassured_1;

/**
 * @author aschoerk
 */
public class SmallDto {
    private int id;
    private String value;

    public SmallDto(final int id, final String value) {
        this.id = id;
        this.value = value;
    }

    public SmallDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
