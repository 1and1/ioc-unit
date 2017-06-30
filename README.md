<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [ejb-cdi-unit](#ejb-cdi-unit)
	- [Motivation](#motivation)
	- [Requirements](#requirements)
	- [Solution](#solution)
	- [Usage](#usage)
	- [Examples](#examples)
		- [One Service and One Entity](#one-service-and-one-entity)
		- [One Service and One Synchronously Consumed Service](#one-service-and-one-synchronously-consumed-service)
		- [One Service and One Asynchronously Consumed Service](#one-service-and-one-asynchronously-consumed-service)
		- [One Service and One Asynchronously Consumed Service Plus Asynchronous Callback](#one-service-and-one-asynchronously-consumed-service-plus-asynchronous-callback)
		- [One Service and One Asynchronously Consumed Service internally using Messaging](#one-service-and-one-asynchronously-consumed-service-internally-using-messaging)
	- [Acknowledgments](#acknowledgments)
	- [License](#license)

<!-- /TOC -->
# ejb-cdi-unit

Simplify test driven development of ejb-3.x Services.

![Build Status](https://travis-ci.org/1and1/ejb-cdi-unit.svg?branch=master)


## Motivation
During the development of services, the necessity to implement automatic module-tests arises. In this context, a module means one deployable artifact.

Given the development occurs using an IDE (integrated development environment) like Eclipse or Intellij, the aim is to provide a module which makes it easy, to first write the test and then to develop the service. The standard approach to achieve this is to recreate a kind of server environment, including the destination server runtime, the destination datasources and message queues.
The effort to recreate these target runtime conditions on the development machine is often quite substantial. Sometimes it is even avoided and all testing is done using test servers or clients, often in a non automatic way.

ejb-cdi-unit is an extension of cdi-unit which contains modules/classes as they became necessary to support automatic tests of about 10 different service artifacts at our site. The about 2000 test functions in about 50000 lines of test code run without special requirements on the machine except java 8.x and maven. No dbms, messaging or other external server is necessary to run these tests. Some of this testcode is implemented in a way so that the main code can also be used in an arquillian-test-environment.


## Requirements
What do we need to be able to achieve this?

* We need a kind of "test-enabled" ejb-containerhttps://travis-ci.org/1and1/ejb-cdi-unit.svg?branch=https://travis-ci.org/1and1/ejb-cdi-unit.svg?branch=master
    * Message queues must be simulated in memory (mockrunner)
    * @TransactionAttribute on EJBs must be handled in a correct way (at least not ignored)
    * @Startup-annotated Beans must be initialized so that other beans might refer to them indirectly.
    * You must be able to fill @Resource annotated fields by "something", which handles the calls in a feasable way.
    * You must be able to handle or simulate arbitrary situations which are possible in an asynchronous working environment, as it is an ejb-server.
    * Sometimes it might be necessary to test using more than one thread. The test-container must be able to handle this as well.
    * The tests must be executable without much effort inside the IDE used for the test and application development.

## Solution

[*cdiunit*](http://jglue.org/cdi-unit/) helps very much by making it very easy

* to integrate the Weld SE
* to define the components (classes) of an project to be tested
* to define Alternatives to include mocks or simulators
* provided an extension which could be used to scan the classes and manipulate injections as it is necessary to build up a (automatic-) test environment

*ejbcdiunit* helps by extending the mentioned extensions so that ejb-specific injections are "doable" using cdi-technics. Additionally it provides helper classes which provide functionality that otherwise would have to be implemented in every test class or test project again.

* *PersistenceFactory* allows it, to use alternative (test) datasources in a easy way. There is a default which always searches a persistence-unit named "test".
* TransactionManager handles per thread the stack of different transaction environments
* TransactionInterceptor is used by the extension to encapsulate calls to ejbs
* AsynchronousManager is a singleton where asynchronously to be executed routines can be stored and later executed in a deterministic way.
* AsynchronousInterceptor encapsulate beans have method annotated @Asynchronous
* SessionContextFactory provides a SessionContextSimulation which will be injected where necessary.



## Usage

The usage does not differ very much from cdiunit, except:

* You need to include additionally:    

        <dependency>
            <groupId>net.oneandone</groupId>
            <artifactId>ejb-cdi-unit</artifactId>
            <version>${ejb-cdi-unit.version}</version>
            <scope>test</scope>
        </dependency>


## Examples

There will be several examples which should demonstrate how different kinds of artifacts can be tested using ejb-cdi-unit.

### One Service and One Entity
This example contains a Service implemented as stateless EJB which can return a constant number and offers the possibility to
add an Entity to a database and to search for it by its id.

[see](https://github.com/1and1/ejb-cdi-unit/tree/master/ejb-cdi-unit-examples/ex1-1entity)


### One Service and One Synchronously Consumed Service

This simple kind of service just provides a service-interface does some calculations and  synchronously consumes some interfaces from other services it uses. A suggestion how such a service can be tested using ejb-cdi-unit will be shown [here](https://github.com/1and1/ejb-cdi-unit/blob/master/ejb-cdi-unit-examples/test).

In this sub-module several possibilities to inject remote services and to simulate those remote services by the Tests are shown.
There are not many calculations done, the servicecall here is for demonstration purposes just propagated to the remote site.

* [Service1]() to be tested hypothetically gets the remote callable interface injected via @EJB.
      @EJB(mappedName = "RemoteServiceIntf/remote")
      RemoteServiceIntf remoteService;
might be code that allows to inject the reference to a remote bean. The following Tests use ejb-cdi-unit to test this configuration in 2 of many possible ways:

    * [ServiceTest]() simulates the remote Service by implementing the interface using a specific class. This is configured into the container inside the @AdditionalClasses Annotation.
    * [ServiceTestWithMockito]() does not use an implementation of the remote interface, but mockito-expressions to generate the required behaviour.  

* [Service2]() alternatively uses a Resources-Bean which handles the lookups of the remote bean. The Test [ServiceTestWithAlternative]() is used to demonstrate the usage of @ActivatedAlternatives.


### One Service and One Asynchronously Consumed Service

The service-interface of the previous tests has been changed, so that the answer of the remote service will not be awaited. Instead of returning the actual result, a CorrelationId is returned which later can be used to query for the result itself. For this query 2 function pollId and pushString have been added. They return null, if the result is not ready yet.
The Servercode achieves the asynchronous behaviour by using the @Asynchronous annotation. Each request to the consumed service is forwarded to a bean that delegates these calls to the remote interface inside asynchronous methods. These Methods return "Future"-object which can be used to check, whether the call has been completed, and when, which return the result.

The EjbExtensions which are initiated by EjbUnitRunner intercept these calls by AsynchronousInterceptor which generates a special future. There are two modes of operation possible. Either the future methods: "isDone" and "get" lead to synchronous calls, or the future checks if a lambda routine created to handle the embedded call is completed. In the second mode the AsynchronousManager-Singleton is responsible to handle the call.

To illustrate this two Tests are created
* [ServiceTest]()
 	* the poll-Routines can be called without further actions, they query the furtures which internally do direct calls
* [AsynchronousServiceTestDeterministic]()
	* the poll-Routines will return null as long as the asynchronous handling is not initiated yet. Using AsynchronousManager#once works through all current queued activities and handle these synchronously. In this way the test itself can decide in a deterministic way, when the handling is to be done.
* [AsynchronousServiceTestMultithreaded]()
	* AsynchronousInterceptor works in non-implicit mode. Additionally using AsynchronousManager#startThread a thread is started that handles the queued routines in background. To make this test deterministic all poll-queries must loop until they return null.

### One Service and One Asynchronously Consumed Service Plus Asynchronous Callback

The previous example gets extended in a way so that the original service consumes a special interface its client provides and calls back as soon as the answer is ready.

-- not implemented yet

### One Service and One Asynchronously Consumed Service internally using Messaging

To provide a safe handling of service calls often message driven beans are used.
In this way it can be made sure that requests are not lost even if a process or thread dies. Additionally in this way other cluster nodes can pick up in the processing.

-- not implemented yet


## Acknowledgments

* The base and idea for this module comes from [cdiunit](https://github.com/BrynCooke/cdi-unit). Some of the code has been shamelessly copied from there.
* [mockrunner](http://mockrunner.github.io/) is used for all jms-simulations.
* [weld](http://weld.cdi-spec.org/) is the cdi-container used for the tests as it is also determined because of the usage of cdi-unit. Since on our site jboss7  is yet in use, compatibility to 1.1.14 is required throughout.


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
