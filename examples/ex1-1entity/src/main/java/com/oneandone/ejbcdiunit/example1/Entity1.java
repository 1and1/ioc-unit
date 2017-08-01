package com.oneandone.ejbcdiunit.example1;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by aschoerk on 28.06.17.
 */
@Entity
public class Entity1 {
    @Id
    @GeneratedValue
    private Long id;
    private int intValue;
    private String stringValue;

    public Entity1() {}

    public Entity1(int intValue, String stringValue) {
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
