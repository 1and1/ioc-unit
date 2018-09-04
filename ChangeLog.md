**Changelog By Versions in Mavencentral**

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
