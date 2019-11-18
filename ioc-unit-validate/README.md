ioc-unit-validate
=================

Using the Annotation @ValidateClasses({}), Validation using your favorite validation module (hibernate-validate??) can be
activated at testclasses.


Usage example in Junit-Test:

Example-Class to be tested:
```Java
public class Sut1 {
    public Integer method1(@NotNull Integer notnull) {
        return notnull;
    }
}
```

JunitTest:
```Java
@RunWith(IocUnitRunner.class)
@ValidateClasses(Sut1.class)
@SutClasses(Sut1.class)
public class ValidationTest {

    @Inject
    Sut1 sut1;

    @Test(expected = ConstraintViolationException.class)
    public void test() {
        sut1.method1(null);
    }
}
```

In case of javaee-7.x necessary dependencies in pom when using hibernate-validater:
```XML
        <dependency>
           <groupId>net.oneandone</groupId>
           <artifactId>ioc-unit-validate</artifactId>
           <version>2.0.18</version>
           <scope>test</scope>
       </dependency>              
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>5.3.1.Final</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator-cdi</artifactId>
            <version>5.3.1.Final</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.glassfish</groupId>
            <artifactId>jakarta.el</artifactId>
            <version>3.0.3</version>
            <scope>test</scope>
        </dependency>
``` 


In case of javaee-8.x necessary dependencies in pom when using hibernate-validater:
```XML
        <dependency>
           <groupId>net.oneandone</groupId>
           <artifactId>ioc-unit-validate</artifactId>
           <version>2.0.18</version>
           <scope>test</scope>
       </dependency>              
       <dependency>
          <groupId>org.hibernate</groupId>
          <artifactId>hibernate-validator</artifactId>
          <version>6.1.0.Final</version>
          <scope>test</scope>
      </dependency>
      <dependency>
          <groupId>org.hibernate</groupId>
          <artifactId>hibernate-validator-cdi</artifactId>
          <version>6.1.0.Final</version>
          <scope>test</scope>
      </dependency>
        <dependency>
            <groupId>org.glassfish</groupId>
            <artifactId>jakarta.el</artifactId>
            <version>3.0.3</version>
            <scope>test</scope>
        </dependency>
``` 
