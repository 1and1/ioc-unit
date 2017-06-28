package com.oneandone.ejbcdiunit.ex1service1entity;

import javax.ejb.Stateless;

/**
 * Created by aschoerk on 28.06.17.
 */
@Stateless
public class Service implements ServiceIntf {

    @Override
    public int returnFive() {
        return 5;
    }
}
