package com.oneandone.iocunit.dbunit;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.analyzer.annotations.TestPackages;
import com.oneandone.iocunit.entities.Aa;
import com.oneandone.iocunit.entities.TestData;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({XmlLessPersistenceFactory.class})
@TestPackages({TestData.class})
public class TestWithAnnotation {

    @Inject
    EntityManager em;

    @Test
    @DbUnitDataSet("classpath:/testdata.json")
    public void canAnnotationTestData() {
        Aa aa = em.find(Aa.class, 1);

    }

    @Test
    @IocUnitDataSets({@IocUnitDataSet(value = "classpath:/testdata.json", unitName = "test")})
    public void canAnnotateMultipleTestData() {
        Aa aa = em.find(Aa.class, 1);

    }
}
