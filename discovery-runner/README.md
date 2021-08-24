Purpose Easier to define a Testcontainer for a Deployable that should be used in a very realistic environment. So most
of the beans should be started. Mocks should be easily excludable. CDI from 1.2.

This module contains starters which do no extensive analyzing. They

    1. Search only dependencies in the test-classpath
    2. They react to Annotations 
        - @ExcludeBeans
        - @AddBeans
        - no Alternatives, this should be handled by creating ambiguities and using @ExcludeBeans 
            to avoid them
        - probably later @ExcludeClasspath, @ExcludePackage.
    3. Start the Weld-container in discovery mode
    4. Remove beans to be excluded using an Extension 
    5. Therefore   
        - Extensions are started if found in dependencies (META-INF/services)
        - Services are started if found in dependencies (META-INF/services)
        - Alternatives, Decorators according to beans.xml

Idea, how to run JUnit4 classes in JUnit5 Environment @TestFactory:
a method that:

- scans the testclass for @Test, @Before, @After
- creates DynamicTest with reasonable name and
    - creation of WeldContainer
    - call of Before if existing
    - call of TestMethod
    - accept expected Exception, otherwise error
    - call of After if existing
