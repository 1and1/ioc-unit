package org.oneandone.iocunit.rest.minimal_rest_war.restassured_1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author aschoerk
 */
public class Dto {
    private long id;
    private int number;
    private Integer nullableNumber;
    private Double doubleNumber;
    private String name;
    private Date timestamp;
    private java.sql.Date sqlTimestamp;
    private Character character;
    private List<Integer> integers = new ArrayList<>();
    private List<SmallDto> smallDtos = new ArrayList<>();


    public Dto() {
    }

    public Dto(final long id) {
        this();
        this.id = id;
        this.number = (int)id + 1;
        this.nullableNumber = (int)id + 2;
        doubleNumber = (double)id + 3;
        name = Long.toString(id + 4);

        final long time = new Date().getTime();
        long normed = ((time + 3599999L) / 3600000L) * 3600000;
        timestamp = new Date(normed + id);
        sqlTimestamp = new java.sql.Date(timestamp.getTime() + 1);
        character = (char)((int)id + 5);
        for (int i = 0; i < id % 10; i++) {
            integers.add((int) id * i);
            smallDtos.add(new SmallDto((int)id + i,Long.toString(id + i)));
        }

    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public Integer getNullableNumber() {
        return nullableNumber;
    }

    public void setNullableNumber(final Integer nullableNumber) {
        this.nullableNumber = nullableNumber;
    }

    public Double getDoubleNumber() {
        return doubleNumber;
    }

    public void setDoubleNumber(final Double doubleNumber) {
        this.doubleNumber = doubleNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public java.sql.Date getSqlTimestamp() {
        return sqlTimestamp;
    }

    public void setSqlTimestamp(final java.sql.Date sqlTimestamp) {
        this.sqlTimestamp = sqlTimestamp;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(final Character character) {
        this.character = character;
    }

    public List<SmallDto> getSmallDtos() {
        return smallDtos;
    }

    public void setSmallDtos(final List<SmallDto> smallDtos) {
        this.smallDtos = smallDtos;
    }

    public List<Integer> getIntegers() {
        return integers;
    }

    public void setIntegers(final List<Integer> integers) {
        this.integers = integers;
    }
}
