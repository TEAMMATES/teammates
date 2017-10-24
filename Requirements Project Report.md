**REQUIREMENTS PROJECT REPORT - TEAM MAVERICKS**
================================================

**1. LIST OF FINAL ASSURANCE CLAIMS**
-------------------------------------

* Feedback session module of TeamMates is acceptably secure to HTTP response splitting Weaknesses
* Login Module of TeamMates is acceptably secure to brute force attack
* Courses Module of TeamMates is acceptably secure to exploitable injection weaknesses.
* Teammates acceptably preserves the privacy of user feedbacks.
* Feedback Module of TeamMates is acceptably secure to exploitable XSS weaknesses.

**2. SECURITY REQUIREMENTS OF THE PROJECT CAPTURED USING MIS-USE CASE DIAGRAM**
--------------------------------------------------------------------------------
 
  * TeamMates needs to ensure proper sanitization practices which includes filtering and encoding the user inputs to prevent injection of any malicious characters into a feedback session module by a malicious user.
  
  * TeamMates needs to ensure proper access control rights to prevent unauthorized access to the TeamMates Login Module.
 
  ### LucidChart Link
  + LucidChart Link to Misuse Cases. Please click the [link](https://www.lucidchart.com/documents/edit/ae54e2f8-8f75-4d7f-b591-1a4fc93d6dab/0).

**3. REVIEW OSS PROJECT DOCUMENTATION FOR ALIGNMENT OF SECURITY FEATURES**
-------------------------------------------------------------------

1. As per the design documentation of TeamMates it follows secure input validation methods.The User Inputs are sanitized so that 
  they conform to the data format required by TeamMates which provides security against HTTP Response Splitting Attack.
  
   **Observations From the Code**
  
   The Http responses for the FeedbackSession Module are properly sanitized in TeamMates using the SanitizationHelper class. This class:
   
   * Follows sanitization Practices for the string with rich-text to remove disallowed elements as per the TeamMates policy.
   * Sanitizes the feedback session URL by proper encoding methods.
   * Follows secure filtering mechanisms i.e. removes leading, trailing whitespaces from the input string.
   * Sanitizes the string for inserting into HTML. Converts special characters into HTML-safe equivalents. e.g. "<" is replaced "&lt;"
   * Sanitizes Strings containing java scripts by escaping some of the malicious special characters e.g. str.replace("\\", "\\\\") that     prevents any CRLF attacks 


2. As per the design documentation of TeamMates the Login Module enforces secure access control rights and uses Google App Engine i.e third-party server
 
   **Observations From the Code**
 
   * As per the documentation TeamMates runs on GoogleAppEngine. So, to login a user needs to have a google account. Account creation in TeamMates follows similar secure practices as google. Detailed practices are not mentioned in the document as GAE is a third-party server.
   
   * TeamMates enforces secure access control rights during user Login by using a GateKeeper class which is accessed by the LoginServlet to check the access control. The GateKeeper class checks if the user account details are present in the datastore. After which the privileges are assigned to the user based on its role. This prevents unauthorized users to access and exploit the teammates modules.
   
3.

4.

5.

**4. REVIEW OSS PROJECT DOCUMENTATION FOR SECURITY RELATED CONFIGURATION AND INSTALLATION ISSUES**
--------------------------------------------------------------------------------------------------

+ Index.jsp in the teammates webapp module has enforced secure practices for all the links where target="_blank" is mentioned by adding rel="noopener noreferrer" with it as mentioned below and this prevents reverse tab nabbing 
"http://conferences.computer.org/cseet/2011/CSEET_2011/Index.html" target="_blank" rel="noopener noreferrer"

Whereas for the below link although target="_blank" is mentioned but rel="noopener noreferrer" is not added
"https://developers.google.com/open-source/gsoc/" target="_blank".

+ After the installation process was done in the local server adding a new instructor in the adminhome page did not work as the button was blocked and no instructions were given in the TeamMates documentation on how to resolve it. The solution was to add “npm run build” first then try to add the instructor in the dev server from the Admin Home page. This detail needs to be added to the Development Guidelines of the TeamMates project documentation under “As Instructor” so that new users don’t face any issue while testing for adding new instructors in the dev server. 



