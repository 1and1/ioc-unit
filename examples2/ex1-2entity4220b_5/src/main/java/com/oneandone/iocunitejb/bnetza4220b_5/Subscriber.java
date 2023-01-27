package com.oneandone.iocunitejb.bnetza4220b_5;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * @author aschoerk
 */
@Entity
public class Subscriber {
    @Id
    @GeneratedValue
    private Long notUsedId;

    private Integer externalSubscriberId;
    public String state;
    public String sim_serial;

    public Subscriber() {
    }

    public Subscriber(int externalSubscriberId, String state, String sim_serial) {
        this.externalSubscriberId = externalSubscriberId;
        this.state = state;
        this.sim_serial = sim_serial;

    }

}
