package iocunit.ejbresource;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;

import iocunit.ejbresource.two_different_resources.SutUsesTwoDataSourceResources;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({Test2Datasources.class})
public class TwoDifferentDatasourcesTest {

    @Inject
    SutUsesTwoDataSourceResources usesTwoResources;

    @Test
    public void canHandleTwoSeparateDbs() {
        usesTwoResources.doInDb1("create table T(a varchar)");
        usesTwoResources.doInDb2("create table T(a varchar)");
    }


}
