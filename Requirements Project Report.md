**REQUIREMENTS PROJECT REPORT - TEAM MAVERICKS**
================================================

**LIST OF FINAL ASSURANCE CLAIMS

* Feedback session module of TeamMates is acceptably secure to HTTP response splitting Weaknesses
* Login Module of TeamMates is acceptably secure to brute force attack
* Courses Module of TeamMates is acceptably secure to exploitable injection weaknesses.
* Teammates acceptably preserves the privacy of user feedbacks.
* Feedback Module of TeamMates is acceptably secure to exploitable XSS weaknesses.

**SECURITY REQUIREMENTS OF THE PROJECT CAPTURED USING MIS-USE CASE DIAGRAM
  
  + LucidChart Link to Misuse Cases. Please click the [link](https://www.lucidchart.com/documents/edit/ae54e2f8-8f75-4d7f-b591-1a4fc93d6dab/0).

**OSS PROJECT DOCUMENTATION FOR ALIGNMENT OF SECURITY FEATURES

1. As per the design documentation of TeamMates it follows secure input validation methods.The User Inputs are sanitized so that 
  they conform to the data format required by TeamMates which provides security against HTTP Response Splitting Attack.
  
  **Observations From the Code
  
   The Http responses for the FeedbackSession Module are properly sanitized in TeamMates using the SanitizationHelper class. This class:
   
     1. Follows sanitization Practices for the string with rich-text to remove disallowed elements as per the TeamMates policy.
     2. Sanitizes the feedback session URL by proper encoding methods.
     3. Follows secure filtering mechanisms i.e. removes leading, trailing whitespaces from the input string.
     4. Sanitizes the string for inserting into HTML. Converts special characters into HTML-safe equivalents. e.g. "<" is replaced "&lt;"
     5. Sanitizes Strings containing java scripts by escaping some of the malicious special characters e.g. str.replace("\\", "\\\\") that                            prevents any CRLF attacks 


