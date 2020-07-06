## ioc-unit-jta-jpa

Useful when
* Transactions on JPA-Connections are necessary
* @Transactional - Annotations need to be handled correctly
* JPA provided by Hibernate is used

Principle:
1. Hibernate is configured to use Narayana as jta-platform by the entry "hibernate.transaction.io.narayana.jta.platform"
in the persistence.xml-properties
1. Hibernate is configured to use a special ConnectionProvider. _PersistenceXmlConnectionProvider_ 
by the entry _hibernate.connection.provider_class_ in the persistence.xml-properties
2. EntityManagers are produced using a Class derived from JtaEntityManagerFactoryBase



### How:
#### pom.xml
* Include the hibernate and jdbc-dependencies in your pom
####  persistence.xml
Define a persistence.xml
Example using H2
```xml
    <persistence-unit name="test" transaction-type="JTA">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <properties>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
            <property name="hibernate.id.new_generator_mappings" value="false"/>
            <!-- Set Hibernate to use Narayana as a JTA platform -->
            <property name="hibernate.transaction.io.narayana.jta.platform" value="JBossTS"/>
            <!-- We provide our own connection provider, in order to integrate TransactionalDriver with Hibernate -->
            <property name="hibernate.connection.provider_class" value="com.oneandone.iocunit.jtajpa.PersistenceXmlConnectionProvider" />
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=0"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>
        </properties>
    </persistence-unit>
```

Example using Testcontainer
```xml
    <persistence-unit name="test" transaction-type="JTA">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
         <properties>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
            <property name="hibernate.id.new_generator_mappings" value="false"/>
            <!-- Set Hibernate to use Narayana as a JTA platform -->
            <property name="hibernate.transaction.io.narayana.jta.platform" value="JBossTS"/>
            <!-- We provide our own connection provider, in order to integrate TransactionalDriver with Hibernate -->
            <property name="hibernate.connection.provider_class" value="com.oneandone.iocunit.jtajpa.PersistenceXmlConnectionProvider" />
            <property name="javax.persistence.jdbc.driver" value="com.oneandone.iocunit.jtajpa.TestContainer"/>
         </properties>
    </persistence-unit>
```

####  testclass

##### Producer for EntityManager and PersistenceunitName

```java
 static class Q2Factory extends JtaEntityManagerFactoryBase {
        @Override
        public String getPersistenceUnitName() {
            return "q2";
        }

        @Override
        @Q2
        @Produces
        public EntityManager produceEntityManager() {
            return super.produceEntityManager();
        }
    }
```

This Factory also needs to return the PersistenceUnitName as it can be found in the persistence.xml
   

##### When using org.testcontainers some initializing in the Test-Class is also necessary
(Docker is necessary for tests)

The Unittest should initialize the testcontainer like this:
    
````java
      @Before
      public void beforeTestJtaJpa() {
          container = new TestContainer(new MariaDBContainer<>());
          container.start();
      }
````
    
                  