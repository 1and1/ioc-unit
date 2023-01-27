package com.oneandone.iocunit.jtajpa.helpers;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * @author aschoerk
 */
@Entity
public class TestEntityH2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String entityName;

    public Long getId() {
        return id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(final String entityName) {
        this.entityName = entityName;
    }
}
