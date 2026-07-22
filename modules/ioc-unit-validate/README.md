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

That's it — `hibernate-validator` and `hibernate-validator-cdi` are pulled in automatically at
`compile` scope by `ioc-unit-validate` itself, so a standalone Weld SE test JVM (which, unlike a
real WildFly container, has nothing else to supply these) has everything it needs to build a
real `jakarta.validation.ValidatorFactory`. You do **not** need to declare
`hibernate-validator`/`hibernate-validator-cdi` yourself.

A working EL implementation (`jakarta.el:jakarta.el-api` + `org.glassfish:jakarta.el`), also
required to build a `ValidatorFactory`, is supplied separately by `weld-starter` (which every
IocUnit test already depends on) — see the [weld-starter README](../../weld-starter/README.md)
for why it lives there instead of here.

## When to use this module standalone

Use `ioc-unit-validate` on its own (without `ioc-unit-resteasy`) whenever a test is purely about
CDI/service-level Bean Validation: enforcing `@NotNull`/`@Size`/custom constraints on method
parameters or return values, or injecting/using a real `Validator`/`ValidatorFactory` directly —
with no JAX-RS/REST layer involved at all. Only add `ioc-unit-resteasy` as well if the same test
also needs to dispatch requests through a mocked REST endpoint; `ioc-unit-resteasy`'s own
`resteasy-validator-provider` wires RESTEasy's `@Valid` interceptor but does not itself provide a
CDI `ValidatorFactory` bean.
