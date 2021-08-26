package com.oneandone.iocunit.jtajpa.helpers;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
