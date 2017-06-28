package com.oneandone.ejbcdiunit.ex1service1entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;

/**
 * Created by aschoerk on 28.06.17.
 */
@Entity
public class Entity1 {
    @Id
    private BigInteger id;
    private int intValue;
    private String stringValue;

}
