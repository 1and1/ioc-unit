# ioc-unit-resteasy

Using MockDispatcherFactory started in a weldcontainer makes it possible to test almost all details 
of a jaxrs-application.

* Resource
* all Types of Providers
* Authorization aspects (roles)

## Principle

* By using **@JaxRsClasses** at the Testclass, Resources can be made explicit. They might also be recognized by @Path but that does not work if the annotation
is set at an interface or superclass. Provider classes are recognized by @Provider. **@JaxRsPackagesDeep** together with a class representing 
one package leads to inclusion of all classes in that package and all its subpackages as JaxRsClasses, if that leads to too many classes added, 
a regular expression can be provided, to improve the selection.
* Use **@TestAuth** at the method to define the username and the roles the user might have. If that annotation is set, the
authorization constraints are enforced.
* Use **IocUnitResteasyClientBuilder** to create webtargets for mocked resources. The webtargets use a special HttpCLient 
**IocUnitResteasyHttpClient** which routes all request via the MockDispatcherFactory-generated Mocks. These webtargets are ResteasyWebTargets 
and therefore can easily proxied to a java-interface. 

## Pure Resteasy

Inclusion of this module as test-dependency allows it to include **jaxrs** RestResources and ExceptionMappers in JUnit-Tests.
To do so the JaxRS-Classes must either get included as SutClasses or explicitly using the Annotation JaxRSClasses (version >= 20.0.19) so that they are explicitly recognized by the resteasy MockDispatcherFactory.

### example

    /**
     * @author aschoerk
     */
    @RunWith(IocUnitRunner.class)
    @JaxRSResources({ExampleErrorMapper.class,ExampleResource.class})
    public class PureResteasyTest {
        @Inject
        Dispatcher dispatcher;
    
        @Test
        public void testGreen() throws URISyntaxException {
            MockHttpRequest request =
                    MockHttpRequest.get("/restpath/method1");
            MockHttpResponse response = new MockHttpResponse();
            dispatcher.invoke(request, response);
            assertEquals(200, response.getStatus());
        }
    
    
        @Test
        public void testExceptionMapper() throws URISyntaxException {
            MockHttpRequest request =
                    MockHttpRequest.get("/restpath/error");
            MockHttpResponse response = new MockHttpResponse();
            dispatcher.invoke(request, response);
            assertEquals(400, response.getStatus());
        }
    }

### pom.xml

To support jaxrs to gether with json-marshalling:

    <parent>
        <groupId>net.oneandone.ioc-unit</groupId>
        <artifactId>ioc-unit-resteasy</artifactId>
        <version>${version.ioc-unit}</version>
        <scope>test</scope>
    </parent>
    <dependency>
        <groupId>org.jboss.resteasy</groupId>
        <artifactId>resteasy-jaxrs</artifactId>
        <version>${resteasy.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.jboss.resteasy</groupId>
        <artifactId>resteasy-jackson-provider</artifactId>
        <version>${resteasy.version}</version>
        <scope>test</scope>
    </dependency>

## Restassured included

if restassured can be found by ioc-unit-resteasy in the test-classpath, it can be used in the testcode.

* **But**: Make sure not to override the RestAssuredConfig. 
The Initial Configuration will make sure that the RestResources and Providers
 will be registered in the Resteasy-Mockdispatcher! To change the RestassuredConfig always use
 `Restassured.config`
 
* Always have a resteasy...provider available in your pom, otherwise in returning a response, a perfect OK-Status
 will become a 500.

### Example

    @RunWith(IocUnitRunner.class)
    @SutClasses({ExampleErrorMapper.class, ExampleResource.class})
    public class RestAssuredTest {
    
        @Test
        public void testGreen() {
            given()
                    .expect()
                    .statusCode(200)
                    .when()
                    .get("/restpath/method1");
        }
     }



### pom.xml

        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <version>${rest-assured.version}</version>
            <scope>test</scope>
        </dependency>



