### TEAM - MAVERICKS - Designing SSE Report


### Threat Model Report
Please  click the link below for the threat model report which includes the DFD diagram and the mitigations done.

[TeamMates Threat Model Report](https://nbiswal.github.io/teammates/)

### Observations From The OSS Project 

+ As per the design documentation of TeamMates, it uses activity logs to record all the activities which mitigates any type of repudiation from an external interactor such as google app engine or thirdy party email api. 

+ Feedback session form filled by external interactors i.e.students are properly validated and sanitized in the logic api and storage api to neutralize any malicious special characters. Apart from that TeamMates uses AES encryption prevent any tampering of data from a malicious user. 

+ As per the design document of TeamMates, the Logic component which includes LoginServlet and Controller Servlet sanitizes input values received from web browser and checks the access rights using the Gate keeper logic. This prevents the spoofing of Logic Component by an attacker.Same practices are followed for storage API,TeamMates UI server and storage entities to prevent spoofing. 
