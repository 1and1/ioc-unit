package com.oneandone.ejbcdiunit.repro;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author aschoerk
 */
@Entity
public class ReproEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String string;

    public ReproEntity() {
    }

    public ReproEntity(String string) {
        this.string = string;
    }

    public Long getId() {
        return id;
    }

    public String getString() {
        return string;
    }
}
