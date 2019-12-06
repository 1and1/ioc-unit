This module handles the first step in starting ioc-unit tests. 
The goal ist to create all information that weld needs to start a test-container which lets the tests run in a
configuration as intended. 

Starting with the testclass it analyzes dependencies according to 
* instructions issued by annotations
* detection of unresolved dependencies 
* selection of classes or producers as necessary and provided by annotations
* identification of classes which are interceptors, services, ...
* call of service interfaces of other ioc-unit-modules to allow the easy adaption of the analyzation project.
