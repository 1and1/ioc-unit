 Erledigt
 * Lesen des WAR-Zipfiles
    * identifikation von Classfiles
    * feststellen, ob beans.xml vorhanden
 
 Todo

* Sammeln der WAR-Beans anhand der Annotationen 
ApplicationScoped, BeanScoped, SessionScoped, ThreadScoped, Startup, Singleton, 
Stateless, Stateful, Singleton, MessageDriven
* Start eine Weldcontainers mit den betreffenden Beans
    * alle beans.xml - Jars und das WAR-File
* Erzeugen aller EJbs, ApplicationScoped, Startup, Singleton, MessageDriven Beans
* interpretieren von jboss-deployment
* Testen als Java-executable mit Beispiel-Warfiles    