### TEAM - MAVERICKS - Designing SSE Report


### Threat Model Report
Please  click the link below for the threat model report which includes the DFD diagram and the mitigations done.

[TeamMates Threat Model Report](https://nbiswal.github.io/teammates/)

### Observations From The OSS Project 

+ As per the design document of TeamMates, the Logic component which includes LoginServlet and Controller Servlet sanitizes input values received from web browser and checks the access rights using the Gate keeper logic. This prevents the spoofing of Logic Component by an attacker.Same practices are followed for storage API,TeamMates UI server and storage entities to prevent spoofing.  

+ As per the design documentation of TeamMates, it uses activity logs to record all the activities which mitigates any type of repudiation from an external interactor such as google app engine or thirdy party email api. 

+ Feedback session form filled by external interactors i.e.students are properly validated and sanitized in the logic api and storage api to neutralize any malicious special characters. Apart from that TeamMates uses AES encryption. All these processes followed by TeamMates mitigates tampering of data and data sniffing from a malicious user. 

+ As per the design documentation of teamMates it enforces authentication of every user who access the teammates web page and also avoids using verbose error messages which prevents any sensitive information disclosure across the HTTP requests or responses.

+ TeamMates is based on GAE which is a cloud service for developing and hosting web applications, there may be vulnerabilities that could allow an attacker to escape from JVM security sandbox and execute any code in the underlying system.We can bypass JRE classes restricted by GAE Security sandbox.This feature needs to be investigated. 
