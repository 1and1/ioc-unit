ioc-unit-dbunit
=================

When building tests using JPA most of the schema-creation-actions are done by the persistence-unit. 
This modules allows it, to define the initial content as DBUNIT (https://org.dbunit) allows it to be defined.
Additionally used ist dbunit-plus (https://github.com/mjeanroy/dbunit-plus) that provides an extension 
to allow the definition of the datasets using Json.

To define Testdata, to be provided by dbunit, two Annotations are available. These Annotations can be added to the 
testclass or the testmethod. 
    
    public @interface IocUnitDataSet {
        String[] value() default {};

        boolean order() default true;

        String unitName() default "";
    } 
    
* value defines the resource or file, that can be loaded
* order can be set to false, if no ordering of the load according to foreign key relations should be done.
* unitName can be used to define the persistenceunit where the data is to be loaded


    public @interface IocUnitDataSets {
        IocUnitDataSet[] value() default {};
    }
    
can be used to define multiple dbunit-datasets to be loaded. If order should be false at one dataset, 
it has to be set to false at all of them. In that case the order of datasets and records in them must adhere
to foreign key relationships, otherwise the data can not be loaded.

# Dependencies

This module ioc-unit-dbunit needs ioc-unit-ejb to work.

# Usageexample

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
            assertNotNull(aa);
        }

        @Test
        @IocUnitDataSets({@IocUnitDataSet(value = "classpath:/testdata.json", unitName = "test")})
        public void canAnnotateMultipleTestData() {
            Aa aa = em.find(Aa.class, 1);
            assertNotNull(aa);
        }
    }