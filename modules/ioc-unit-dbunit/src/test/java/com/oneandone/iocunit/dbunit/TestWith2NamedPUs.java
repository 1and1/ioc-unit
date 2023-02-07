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
@TestClasses({TestWith2NamedPUs.PU1.class, PU2.class})
@IocUnitDataSets({
        @IocUnitDataSet(value = "classpath:/testdata.json", unitName = "pu1"),
        @IocUnitDataSet(value = "classpath:/testdata.json", unitName = "pu2")})
public class TestWith2NamedPUs {
    static class PU1 extends XmlLessPersistenceFactory {
        public PU1() {
            addProperty( "jakarta.persistence.jdbc.url",
                    "jdbc:h2:file:/tmp/pu1;DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000;TRACE_LEVEL_SYSTEM_OUT=1");
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
    EntityManager empu1;

    @Inject
    EntityManager empu2;

    @Test
    public void checkClassDataset() {
        Aa aapu1 = empu1.find(Aa.class, 1);
        Assert.assertNotNull(aapu1);
        Aa aapu2 = empu2.find(Aa.class, 1);
        Assert.assertNotNull(aapu2);
    }

    @Test
    @IocUnitDataSets({
            @IocUnitDataSet(value = "classpath:/A_AA_2.json", unitName = "pu1"),
            @IocUnitDataSet(value = "classpath:/A_AA_2.json", unitName = "pu2"),
            @IocUnitDataSet(value = "classpath:/A_AA_3.json", unitName = "pu2")})
    public void canHandle2Datasets() {
        checkDataInPU(empu1);
        Aa aapu1 = empu1.find(Aa.class, 8);
        Assert.assertNull(aapu1);
        checkDataInPU(empu2);
        Aa aapu2 = empu2.find(Aa.class, 8);
        Assert.assertNotNull(aapu2);
    }

    private void checkDataInPU(final EntityManager em) {
        for (int i = 1; i < 7; i++) {
            Aa aa = em.find(Aa.class, i);
            Assert.assertNotNull("Aa with  value " + i, aa);
        }
        Aa aa7 = em.find(Aa.class, 7);
        Assert.assertNull(aa7);
    }

}
