package com.oneandone.iocunitejb.example1;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Created by aschoerk on 28.06.17.
 */
@Entity
public class ExampleEntity1 {
    @Id
    @GeneratedValue
    private Long id;
    private int intValue;
    private String stringValue;

    public ExampleEntity1() {}

    public ExampleEntity1(int intValue, String stringValue) {
        this.intValue = intValue;
        this.stringValue = stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public Long getId() {
        return id;
    }
}
