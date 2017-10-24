**REQUIREMENTS PROJECT REPORT - TEAM MAVERICKS**
================================================

###LIST OF FINAL ASSURANCE CLAIMS

* Feedback session module of TeamMates is acceptably secure to HTTP response splitting Weaknesses
* Login Module of TeamMates is acceptably secure to brute force attack
* Courses Module of TeamMates is acceptably secure to exploitable injection weaknesses.
* Teammates acceptably preserves the privacy of user feedbacks.
* Feedback Module of TeamMates is acceptably secure to exploitable XSS weaknesses.

###SECURITY REQUIREMENTS OF THE PROJECT CAPTURED USING MIS-USE CASE DIAGRAM


###OSS PROJECT DOCUMENTATION FOR ALIGNMENT OF SECURITY FEATURES

1 As per the design documentation of TeamMates it follows secure input validation methods. 
  The user inputs are sanitized so that they conform to the data format required by TeamMates which provides security against HTTP Response Splitting attack
  
  *Observations From the Code
   > The Http responses for the FeedbackSession Module are properly sanitized in TeamMates using the SanitizationHelper class. 
   > This class:
   > 1 Follows sanitization Practices for the string with rich-text to remove disallowed elements as per the TeamMates policy.


