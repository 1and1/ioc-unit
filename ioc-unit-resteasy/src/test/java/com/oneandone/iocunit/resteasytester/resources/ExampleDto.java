package com.oneandone.iocunit.resteasytester.resources;

/**
 * @author aschoerk
 */
public class ExampleDto {
    public int id;
    public String comment;

    public ExampleDto() {
    }

    public ExampleDto(final int id, final String comment) {
        this.id = id;
        this.comment = comment;
    }
}
