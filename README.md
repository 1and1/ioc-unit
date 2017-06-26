# ejb-cdi-unit 

Simplify test driven development of j2ee-3.x Services. 

## Motivation
During the development of services the necessity of module-tests arrise. Here a module means one deployable.

Given the development occurs using an IDE like Eclipse or Intellij, the aim is to provide a module which makes it easy to first write the test and then develop the service. To do this you might not have a real deployable yet, you might not have access to the target dbms or have a messaging solution or even the target container itself installed. The same holds for the system which should execute the test later. 
Never the less to be able to develop by creating the tests first, I need to be able to formulate all usecases to be implemented as tests, and (re-)create all situations, which might arise, deterministic and automatically.

## Requirements
What do we need to be able to achieve this:

* We need a kind of testenabled ejb-container
    * Datasources must be simulated using an in memory database (H2)
    * Messagequeues must be simulated in memory (mockrunner)
    * TransactionAttributes on EJBs must be handled in a correct way (at least not ignored)
    * @Startup-annotated Beans must be initialized so that other beans might refer to them indirectly. Sometimes there were problems because of the lazy initialization of ApplicationScoped Beans.
    * you must be able to fill @Resource annotated fields by "something", which handles the calls in a feasable way.
    * You must be able to handle or simulate arbitrary situations which are possible in an asynchronous working environment, as it is an ejb-server.
    * Sometimes it might be necessary to test using more than one thread. The test-container must be able to handle this as well.
    * The tests must be executable without much effort inside the IDE used for the test and application development.

## Solution

[*cdiunit*](http://jglue.org/cdi-unit/) helps very much by making it very easy 

* to integrate the Weld SE
* to define the components (classes) of an project to be tested
* to define Alternatives to include mocks or simulators
* provided an extension which could be used to scan the classes and manipulate injections as it is necessary to build up a (automatic-) test environment

*ejbcdiunit* helps by extending the mentioned extensions so that ejb-specific injections are "doable" using cdi-technics. Additionally it provides helper classes which provide functionality that otherwise would have to be implemented in every Testclass or Testproject again.

* *PersistenceFactory* allows it to use alternative datasources in a easy way. There is a default which always searches a persistenceunit named "test".
* Transactionmanager handles per thread the stack of different transaction enviroments
* TransactionInterceptor is used by the extension to encapsulate calls to ejbs
* AsynchronousManager is a singleton where asynchronously to be executed routines can be stored and later executed in a deterministic way.
* AsynchronousInterceptor encapsulate beans have method annotated @Asynchronous
* SessionContextFactory provides a SessionContextSimulation which will be injected where necessary.


## Comparison of different approaches for automatic tests

The following is a short description of different approaches used at our site:

* pure JUnit testing - feasible for simple classes or combinations of classes. 
* JUnit together with mocking frameworks (mockito, powermock...): 
    * The DML is quite expressive. You can simulate quite flexible consumed services, databasecalls...
    * often very much internal information must be used to mock effectivly this leads often to the breaking of tests 
    further development of the code.
    * Quirks of CDI-Injections are not tested.
    * Interceptors are not active, transactionscontexts are not testable
* Arquillian
    * The module is tested in an environment, quite near to the productive environment.
    * Transactioncontexts, Database-accesses can be tested in the future or a simulated database.
    * The configuration is quite complicated
    * The determinism of the tests often is questionable because of multithreaded execution.
    * Tests and debugging inside an IDE is often slow and complicated. 
* CdiUnit
    * Using the annotations @AdditionalClass* it is quite easy to create a testing 
    configuration. @ActivatedAlternatives allows it quite easy to replace parts of the code by simulators, 
    alternative datasources or other resources. 
    * @AdditionalClasspath easily allows it to include a complete deployable module in the test.
    * Testing and Debugging is as easy as JUnit-testing, very good integration in IDEs.
    * Transactioncontexts, Database-accesses can be tested in the future or a simulated database.
    * @Asynchronous, @Schedule, @Resource, @PersistenceContext ... the Ejb-Injections and Annotations are not 
    recognized and Injections stay null.
    * TransactionAttributes are not recognized.
    
Because of the very good integratability into a IDE and the most flexible way to create a deterministic test-environment 
cdiunit is used for most automatic module tests at about 10 services. To overcome the restrictions mentioned above, 
some extensions have been developed and collected in the module ejb-cdi-unit.


## What is ejb-cdi-unit

This is a thin extension of [cdiunit](http://jglue.org/cdi-unit/) [source](https://github.com/BrynCooke/cdi-unit) supporting 

* Injection of PersistenceContexts
* Ejb-Transaction-Handling
* JMS-Messaging
* Injection of Resources
* Creation of @Startup
* Injection of Contexts (Session, Messaging, WebService)

The jumpstart to most of these extension was the EjbExtension which originally can be found in cdi-unit.

## Usage

The usage does not differ very much from cdiunit, except: 

* You need to include additionally:    

        <dependency>
            <groupId>com.oneandone</groupId>
            <artifactId>ejb-cdi-unit</artifactId>
            <version>${ejb-cdi-unit.version}</version>
            <scope>test</scope>
        </dependency>   

* 


## Acknowledgments

* [cdiunit](https://github.com/BrynCooke/cdi-unit)
* [mockrunner]()


## License

Copyright 2017 1&amp;1 Internet AG, https://github.com/1and1/ejb-cdi-unit

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

