
# todo
* speedup by reusing scanned configuration
* idea: ejb-deployment-mode, only accepts Alternatives
* inject of EjbContext not in CDI-Beans allowed
* Speedup using H2PersistenceFactory with two property combinations and copy of H2-File-db in between.
* abstract classes or interfaces as TestClasses or SutClasses should lead to making available instances of them obligatory. 
* add name="hibernate.transaction.jta.platform" value="org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform" to XmllessPersistenceFactory

# doing
* XmlLessPersistenceFactory ease usage!!!


# done
* weld 3
* JUnit5
* Transactional-Interceptor-Annotation
* Transactional-Interceptor-rollbackOn, dontRollbackOn
* Transactional-Interceptor-rollbackOn, dontRollbackOn - tests
* SourceInterceptor zo manipulated sql before jdbc-execution