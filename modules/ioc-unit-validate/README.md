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

## pom.xml

Import the WildFly BOMs and the `ioc-unit-bom` in your `dependencyManagement` (see the
[root README](../../README.md#dependency-management-bom-first) for the full snippet), then
declare, without any explicit version:

```XML
<dependency>
   <groupId>net.oneandone.ioc-unit</groupId>
   <artifactId>ioc-unit-validate</artifactId>
   <scope>test</scope>
</dependency>
```

That's it — `hibernate-validator`, `hibernate-validator-cdi`, and a working EL implementation
(`jakarta.el:jakarta.el-api` + `org.glassfish:jakarta.el`) are pulled in automatically at
`compile` scope by `ioc-unit-validate` itself, so a standalone Weld SE test JVM (which, unlike a
real WildFly container, has nothing else to supply these) has everything it needs to build a
real `jakarta.validation.ValidatorFactory`. You do **not** need to declare
`hibernate-validator`/`hibernate-validator-cdi`/`jakarta.el` yourself.
