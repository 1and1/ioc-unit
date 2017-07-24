#ejb-cdi-unit-arq

Demonstrate how some of the tests from ejb-cdi-unit-tests can be implemented using embedded tomee.

* WildflyArquillianAsynchTest - the equivalent of AsynchTest
* WildflyArquillianMdbTest - the equivalent of MdbTest
* WildflyArquillianTransactionTest - the equivalent of EjbTest

Additionally the correctness of the simulation of the ejb-cdi-unit-transaction handling is tested.
Since the same base-classes are used for WildflyArquillianTransactionTest and TestEjb.

The single difference encountered until now is: EJBException is thrown instead of TransactionRequiredException.