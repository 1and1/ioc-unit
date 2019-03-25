ioc-unit-ejb
============

Adapts the CDI-Container to 

* recognize EJ-Beans: Entity, Stateless, Singleton, Stateful
* recognize and handle transactionality according to ejb-spec (without distributes transcations)
* Offers Simulations for PersistenceContexts by default in memory.
* recognize @Resource,@EJB,@PersistenceContext-Injects and map them 
to CDI-Injects so that simple producers can replace those in a testenvironment.
* qualify ResourceInjects
   * java.lang.String: ResourceQualifier with name, mappedName and lookup if set
   * javax.ejb.EJBContext: ResourceQualifier("javax.ejb.EJBContext")
   * javax.ejb.SessionContext,javax.ejb.MessageDrivenContext,javax.ejb.EntityContext: no Qualifier
   * other Resources(e.g. java.sql.DataSource): if name, mappedName or lookup set: ResourceQualifier accordingly.
* recognize timers and asynchronous to allow to handle them deterministicly
* recognize startup-beans to make tester aware if they are to be included 
in the testconfiguration
* recognize Messagedriven Beans and JMS-Resources and allows the in memory simulation of JMS-Queues, JMSContext,...
