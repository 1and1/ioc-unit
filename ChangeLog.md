**Changelog By Versions in Mavencentral**

**ioc-unit**:

|Version|Changes|
|-------|-------|
|2.0.19||
|2.0.18|new module ioc-unit-validate, allows it to define Beans (@ValidateClasses) where Validation should be activated during the tests|
|2.0.17|Recognize Restresources even by @Path in interfaces, make jdbcSqlConverter work with XmlAwarePersistenceFactories Eclipselink, Phase4Guesser may not try to resolve Instance-Injects, weld(x)-starter will not bring javaee-api as dependency anymore|
|2.0.16|make jdbcSqlConverter work with XmlAwarePersistenceFactories (persistence.xml) make TestPersistenceFactory final replace it by XmlLess- or XmlAwarePersistenceFactory| 
|2.0.15|jdbcSqlConverter-interface also supported for JPA (hibernate, eclipselink) |
|2.0.14|jdbcSqlConverter-interface allows the replacement of native-statements |
|2.0.13|commons-lang3:3.8.1,enable only Interceptors without Priority automatically|
|2.0.12|@Transactional, XmlLessPersistenceFactory |
|2.0.11|Add generally @Provider-classes defined as Sut or Test to MockDispatcher. --> possible to define ObjectMapper, does also handle @Context in Provider especially: ExceptionMapper |
|2.0.10|reorganized poms, starters depend on the correct javaee-api compiled. If users don't like this, they must exclude it. ioc-unit-xxxx modules depend on the api-modules they need provided, so they don't transitively depend on any javax-api.|
|2.0.9|added javax.json provider from glassfish, include io.rest-assured, if existing allow fluent test of rest-resources using MockDispatcherFactory |
|2.0.8|fix handling of default initial context, transporting the beanmanager|
|2.0.7|ioc-unit-resteasy identifies JaxRS-Restresources and JaxRs-Exception in sutclasses and registers them with Resteasy MockDispatcherFactory. Example, see Test in ioc-unit-resteasy|
|2.0.6|Show Qualifierdiffs at Resource-Injects, if there are; extended TestPersistenceFactory; Improved support of multiple different DataSources and PersistenceContexts so: @PersistenceContext might be necessary to produce it with qualifier similar to @Resource |
|2.0.5|Reverted Qualifiers generated on SessionContext and MessageDrivenContext|
|2.0.4|TestPersistenceFactory can set inital schema, don't use if using Resource SessionContext and MessageDrivenContext|
|2.0.3|fixed Reflections8, changed warnings of analyzer, don't use if using Resource SessionContext and MessageDrivenContext|
|2.0.2|RequestScoped does not need ioc-unit-contexts anymore|
|2.0.1|first ioc-unit-version|

**ejb-cdi-unit**:

|Version|Changes|
|-------|-------|
|1.1.16|Junit5.3.0 use TestInstanceFactory, get rid of restrictions on Testclasses concerning CDI|
|1.1.15|Junit5 nested classes, get rid of cdi-unit dependencies|
|1.1.14|fixes for weld 3.0, reflections8-code-base in package org.reflections8|
|1.1.13|weld 3.0, reflections8 (no guava), no fastclasspathscanner, support java 10 only classpath (ejb) no modules recognized|
|1.1.12|support Java10|s rec
|1.1.11|@EjbJarClasspath: Support ApplicationExceptions defined by ejb-jar.xml either in META-INF or in WEB-INF|
|1.1.10|<ul><li>Support String-Resource-Injection, <li>change debug level for chatty TransactionalInterceptor|
|1.1.7|<ul><li>fixed sftp-simulator tests by increasing mina-version, |
|1.1.6|<ul><li>Pull request: Decode CodeSource-Path if not found<li>fixed TransactionAttribute is not evaluated when its on a non ejb super-class<li>fixed errors in rollback handling |
|1.1.4|<ul><li>introduced transaction logging (changed level in 1.1.10)|
|1.1.3|<ul><li>added sftserver simulator|
|1.1.0|<ul><li>better rulesupport by own TestConfigAnalyzer<li>allow self-injection with interceptors|
|1.0.12|<ul><li>exclusion of beans using @ExcludedClasses<li>start to support Rules for camunda-tests<li>remove production of UserTransaction will be done by EjbUnitRunner|
|1.0.11|<ul><li>corrected usertransaction and rollback-handling<li>corrected handling of begin-transaction producing exception<li>handle entitymanager leak<li>support ApplicationException-Annotation|
|1.0.10|easy use of @ejb, no special Qualifier required anymore. Aligned transaction handling of ejb-unit in SUPPORTED and NOT_SUPPORTED and implicite in Testcode to Arquillian and tomee|
|1.0.9|improve reinit of static variables between tests|
|1.0.8|Readmes, Examples|
|1.0.7|fixed initialization problem in transactionmanager, corrected README.md|
|1.0.6|first release on Maven-Central|
