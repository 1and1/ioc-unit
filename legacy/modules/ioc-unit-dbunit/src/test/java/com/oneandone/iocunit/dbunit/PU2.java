package com.oneandone.iocunit.dbunit;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;

import com.oneandone.iocunit.entities.A;
import com.oneandone.iocunit.entities.Aa;
import com.oneandone.iocunit.entities.TestData;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

/**
 * @author aschoerk
 */
class PU2 extends XmlLessPersistenceFactory {
    public PU2() {
        addProperty( "jakarta.persistence.jdbc.url",
                "jdbc:h2:file:/tmp/pu2;DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000;TRACE_LEVEL_SYSTEM_OUT=1");
    }

    @Override
    public String getPersistenceUnitName() {
        return "pu2";
    }

    @Override
    protected List<String> getManagedClassNames() {
        List<String> res = new ArrayList<>();
        res.add(Aa.class.getName());
        res.add(A.class.getName());
        res.add(TestData.class.getName());
        return res;
    }

    @Produces
    EntityManager createEntityManager() {
        return super.produceEntityManager();
    }
}
