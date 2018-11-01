package net.oneandone.ejbcdiunit.relbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.ProducesAlternative;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.oneandone.ejbcdiunit.relbuilder.beans.BeanContainingNotAvailable;
import net.oneandone.ejbcdiunit.relbuilder.beans.BeanToBeInjected;
import net.oneandone.ejbcdiunit.relbuilder.beans.ConstructedTestBean;
import net.oneandone.ejbcdiunit.relbuilder.beans.TestBeanWithInjectedField;
import net.oneandone.ejbcdiunit.relbuilder.beans.TestBeanWithProducerInject;
import net.oneandone.ejbcdiunit.relbuilder.beans.additional_package.AdditionalPackageBeanToBeInjected;
import net.oneandone.ejbcdiunit.relbuilder.beans.additional_package.AdditionalPackageBeanToBeInjected2;
import net.oneandone.ejbcdiunit.relbuilder.code.CdiRelBuilder;
import net.oneandone.ejbcdiunit.relbuilder.code.CountingRelVisitor;
import net.oneandone.ejbcdiunit.relbuilder.code.InjectProduceExtractor;
import net.oneandone.ejbcdiunit.relbuilder.code.InjectsFinder;
import net.oneandone.ejbcdiunit.relbuilder.code.LoggingCountingRelVisitor;
import net.oneandone.ejbcdiunit.relbuilder.code.Rels;
import net.oneandone.ejbcdiunit.tests.notavailable.InjectedBean;
import net.oneandone.ejbcdiunit.tests.notavailable.NotAvailableInjectedBean;

/**
 * @author aschoerk
 */
public class RelBuilderTest {

    Logger logger = LoggerFactory.getLogger("RelBuilderTest");

    protected CdiRelBuilder cdiRelBuilder;
    protected Rels.RootRel root;
    protected Map<String, Rels.Intermediate> beanClasses;
    protected CountingRelVisitor countingRelVisitor;
    protected InjectsFinder injectsFinder;
    protected InjectProduceExtractor injectProduceExtractor;

    @BeforeEach
    public void beforeEach() {
        root = null;
        beanClasses = null;
        cdiRelBuilder = null;
        countingRelVisitor = null;
    }

    private void initTest(Class<?>... classes) throws CdiRelBuilder.AnalyzerException {
        cdiRelBuilder = new CdiRelBuilder(Arrays.asList(classes));
        root = cdiRelBuilder.getRootRel();
        beanClasses = root.getBeanClasses();
        countingRelVisitor = new LoggingCountingRelVisitor();
        countingRelVisitor.visit(root, null);
        injectsFinder = new InjectsFinder();
        injectsFinder.visit(root, null);
        injectProduceExtractor = new InjectProduceExtractor();
        injectProduceExtractor.produceFitsToInject(root);

    }

    @Nested
    class SimpleTests {
        @Test
        public void canInjectOneField() throws CdiRelBuilder.AnalyzerException {
            initTest(TestBeanWithInjectedField.class,BeanToBeInjected.class);

            assertNotNull(beanClasses.get(TestBeanWithInjectedField.class.getName()));
            assertNotNull(beanClasses.get(BeanToBeInjected.class.getName()));
            assertEquals(2, beanClasses.size());
            assertEquals(4, countingRelVisitor.getCount());
            assertEquals(injectProduceExtractor.getEmptyInjects().size(),0);
            assertEquals(injectProduceExtractor.getAmbiguusQualifiedDescs().size(),0);
            assertEquals(injectProduceExtractor.getMatchingQualifiedDescs().size(),1);

            // CountingRelVisitor - visiting type: RootRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$RootRel@4803b726, parent: root
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@6253c26, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$RootRel@4803b726
            // CountingRelVisitor - visiting type: InjectedFieldRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$InjectedFieldRel@49049a04,
            // parent: com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@6253c26
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@71a8adcf, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@6253c26

        }

        @Test
        public void canInjectViaConstructor() throws CdiRelBuilder.AnalyzerException {
            initTest(ConstructedTestBean.class);

            assertNotNull(beanClasses.get(ConstructedTestBean.class.getName()));
            assertNotNull(beanClasses.get(BeanToBeInjected.class.getName()));
            assertEquals(2, beanClasses.size());
            assertEquals(6, countingRelVisitor.getCount());
            assertEquals(injectProduceExtractor.getEmptyInjects().size(), 0);
            assertEquals(injectProduceExtractor.getAmbiguusQualifiedDescs().size(), 0);
            assertEquals(injectProduceExtractor.getMatchingQualifiedDescs().size(), 1);

            // CountingRelVisitor - visiting type: RootRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$RootRel@7c711375, parent: root
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@2c1b194a, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$RootRel@7c711375
            // CountingRelVisitor - visiting type: ConstructorInjectRel,
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$ConstructorInjectRel@4dbb42b7, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@2c1b194a
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@66f57048, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@2c1b194a
            // CountingRelVisitor - visiting type: ParameterInjectRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$ParameterInjectRel@550dbc7a,
            // parent: com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$ConstructorInjectRel@4dbb42b7
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@66f57048, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@2c1b194a

        }

        @Test
        public void canInjectOneProducerParameter() throws CdiRelBuilder.AnalyzerException {
            initTest(TestBeanWithProducerInject.class);

            assertNotNull(beanClasses.get(TestBeanWithProducerInject.class.getName()));
            assertNotNull(beanClasses.get(BeanToBeInjected.class.getName()));
            assertEquals(2, beanClasses.size());
            assertEquals(6, countingRelVisitor.getCount());
            // CountingRelVisitor - visiting type: RootRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$RootRel@7c711375, parent: root
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@2c1b194a, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$RootRel@7c711375
            // CountingRelVisitor - visiting type: ProducerMethodRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$ProducerMethodRel@4dbb42b7,
            // parent: com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@2c1b194a
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@66f57048, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@2c1b194a
            // CountingRelVisitor - visiting type: ParameterInjectRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$ParameterInjectRel@550dbc7a,
            // parent: com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$ProducerMethodRel@4dbb42b7
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@66f57048, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@2c1b194a
        }
    }

    @AdditionalPackages({ AdditionalPackageBeanToBeInjected.class })
    static class AdditionalPackageTestBean {
        @Inject
        AdditionalPackageBeanToBeInjected additionalPackageBeanToBeInjected;
    }

    @AdditionalPackages({ AdditionalPackageBeanToBeInjected.class, AdditionalPackageBeanToBeInjected.class })
    static class AdditionalPackageTestBeanWithMultipleSame {
        @Inject
        AdditionalPackageBeanToBeInjected additionalPackageBeanToBeInjected;
    }

    @AdditionalPackages({ AdditionalPackageBeanToBeInjected.class })
    static class DiffAdditionalPackage {
        @Inject
        AdditionalPackageTestBeanWithMultipleSame2 additionalPackageTestBeanWithMultipleSame2;
    }

    @AdditionalPackages({ AdditionalPackageBeanToBeInjected.class })
    static class AdditionalPackageTestBeanWithMultipleSame2 {
        @Inject
        @Any
        AdditionalPackageBeanToBeInjected additionalPackageBeanToBeInjected;
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AdditionalPackagesTests {


        private void checkOneAdditionalPackage(int expectedBeans, int expectedRels) {
            assertNotNull(beanClasses.get(AdditionalPackageBeanToBeInjected.class.getName()));
            assertNotNull(beanClasses.get(AdditionalPackageBeanToBeInjected2.class.getName()));
            assertEquals(expectedBeans, beanClasses.size());
            assertEquals(expectedRels, countingRelVisitor.getCount());
        }


        @Test
        public void canAddPackageBeans() throws CdiRelBuilder.AnalyzerException {
            initTest(AdditionalPackageTestBean.class);
            assertNotNull(beanClasses.get(AdditionalPackageTestBean.class.getName()));
            checkOneAdditionalPackage(3, 6);
            // CountingRelVisitor - visiting type: RootRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$RootRel@5038d0b5, parent: root
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@32115b28, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$RootRel@5038d0b5
            // CountingRelVisitor - visiting type: AdditionalPackageRel,
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$AdditionalPackageRel@2ad48653, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@32115b28
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@6bb4dd34, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$AdditionalPackageRel@2ad48653
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@7d9f158f, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$AdditionalPackageRel@2ad48653
            // CountingRelVisitor - visiting type: InjectedFieldRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$InjectedFieldRel@45efd90f,
            // parent: com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@32115b28
        }

        @Test
        public void canAddPackageBeansAndIdentifySame() throws CdiRelBuilder.AnalyzerException {
            initTest(AdditionalPackageTestBeanWithMultipleSame.class);
            assertNotNull(beanClasses.get(AdditionalPackageTestBeanWithMultipleSame.class.getName()));
            checkOneAdditionalPackage(3, 6);
            // CountingRelVisitor - visiting type: RootRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$RootRel@5038d0b5, parent: root
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@32115b28, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$RootRel@5038d0b5
            // CountingRelVisitor - visiting type: AdditionalPackageRel,
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$AdditionalPackageRel@2ad48653, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@32115b28
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@6bb4dd34, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$AdditionalPackageRel@2ad48653
            // CountingRelVisitor - visiting type: BeanClassRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@7d9f158f, parent:
            // com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$AdditionalPackageRel@2ad48653
            // CountingRelVisitor - visiting type: InjectedFieldRel, com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$InjectedFieldRel@45efd90f,
            // parent: com.oneandone.ejbcdiunit.cfganalyzer.CdiRelBuilder$BeanClassRel@32115b28
        }


        @Test
        public void canAddPackageBeansAndIdentifySameAsDifferentAnnotations() throws CdiRelBuilder.AnalyzerException {
            initTest(DiffAdditionalPackage.class);
            assertNotNull(beanClasses.get(DiffAdditionalPackage.class.getName()));
            assertNotNull(beanClasses.get(AdditionalPackageTestBeanWithMultipleSame2.class.getName()));
            checkOneAdditionalPackage(4, 8);
            assertEquals(AdditionalPackageTestBeanWithMultipleSame2.class, getInjectedClass(0));
            assertTrue(hasInjectedQualifier(0, "@javax.enterprise.inject.Default()"));
            assertEquals(AdditionalPackageBeanToBeInjected.class, getInjectedClass(1));
            assertTrue(hasInjectedQualifier(1, "@javax.enterprise.inject.Any()"));

            assertEquals(2, injectsFinder.getInjectionPoints().size());
        }

    }


    static class MockingBean {
        @Mock
        @ProducesAlternative
        BeanContainingNotAvailable beanContainingNotAvailable;
    }


    @Nested
    class CanHandleMockingNotAvailableBeans {

        @Test
        public void canUseDirectMockingBean() throws CdiRelBuilder.AnalyzerException {
            initTest(MockingBean.class);
        }

        @Test
        public void canUseDirectBeanContainingNotAvailable() throws CdiRelBuilder.AnalyzerException {
            try {
                initTest(BeanContainingNotAvailable.class);
            } catch (NoClassDefFoundError e) {

                logger.error("canUseDirectBeanContainingNotAvailable", e);
                throw e;
            }
        }

        @Test
        @DisabledIfSystemProperty(named = "surefire.test.class.path", matches = ".*purecdi-tests.*")
        public void checkBeansReallyNotAvailable() {
            assertThrows(NoClassDefFoundError.class, () -> {
                NotAvailableInjectedBean.class.newInstance();
            });
            assertThrows(NoClassDefFoundError.class, () -> {
                InjectedBean.class.newInstance();
            });
        }

    }

    private boolean hasInjectedQualifier(final int index, String qualifier) {
        for (Annotation a : injectsFinder.getInjectionPoints().get(index).getQualifiers()) {
            if (a.toString().equals(qualifier))
                return true;
        }
        return false;
    }

    private Class getInjectedClass(final int index) {
        return injectsFinder.getInjectionPoints().get(index).getClassWrapper().getBaseclass();
    }

}
