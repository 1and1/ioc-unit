
# todo
* speedup by reusing scanned configuration
* idea: ejb-deployment-mode, only accepts Alternatives
* inject of EjbContext not in CDI-Beans allowed
* Speedup using H2PersistenceFactory with two property combinations and copy of H2-File-db in between.
* abstract classes or interfaces as TestClasses or SutClasses should lead to making available instances of them obligatory. 
* add name="hibernate.transaction.jta.platform" value="org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform" to XmllessPersistenceFactory
* dbunit only works with Javaee 8.0 - check that
* add new dependency modules for validator and Persistence-versions to bom as done with jms
* avoid necessity of jms if using ejb.
* activemq-version needs to wait for Async-Manager after sending message because of extra thread in activemq
* bom for javaee 7.0
* bom for javaee 8.0
* bom for microprofile 3.3

# doing
* XmlLessPersistenceFactory ease usage!!!


# done
* weld 3
* JUnit5
* Transactional-Interceptor-Annotation
* Transactional-Interceptor-rollbackOn, dontRollbackOn
* Transactional-Interceptor-rollbackOn, dontRollbackOn - tests
* SourceInterceptor zo manipulated sql before jdbc-execution