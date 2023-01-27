package com.oneandone.iocunit.dbunit;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.entities.A;
import com.oneandone.iocunit.entities.Aa;
import com.oneandone.iocunit.entities.TestData;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({TestWithNamedPU.PU1.class})
@IocUnitDataSet(value = "classpath:/testdata.json", unitName = "pu1")
public class TestWithNamedPU {
    static class PU1 extends XmlLessPersistenceFactory {
        public PU1() {
            addProperty( "javax.persistence.jdbc.url",
                    "jdbc:h2:mem:pu1;DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000");
        }

        @Override
        public String getPersistenceUnitName() {
            return "pu1";
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
        @PU1Qual
        EntityManager createEntityManager() {
            return super.produceEntityManager();
        }
    }

    @Inject
    @PU1Qual
    EntityManager em;

    @Test
    public void testPu1() {
        Aa aa = em.find(Aa.class, 1);
        Assert.assertNotNull(aa);
    }

    @Test
    @IocUnitDataSets({
            @IocUnitDataSet(value = "classpath:/A_AA_2.json", unitName = "pu1"),
            @IocUnitDataSet(value = "classpath:/A_AA_3.json", unitName = "pu1")})
    public void testPu1With3Datasets() {
        for (int i = 0; i < 7; i++) {
            Aa aa = em.find(Aa.class, 1);
            Assert.assertNotNull(aa);
        }
        Aa aa7 = em.find(Aa.class, 7);
        Assert.assertNull(aa7);
        Aa aa8 = em.find(Aa.class, 8);
        Assert.assertNotNull(aa8);
    }

}
