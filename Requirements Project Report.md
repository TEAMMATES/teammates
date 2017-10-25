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
 
  * TeamMates needs to ensure proper sanitization practices which includes filtering and encoding the user inputs to prevent injection of any malicious characters through HTTP Response splitting attack into a feedback session module by a malicious user.
  
  * TeamMates needs to ensure proper access control rights to prevent unauthorized access to exploit the TeamMates Login Module through brute force attack.
  
  * As TeamMates uses a NoSQL database it needs to ensure secure coding and sanitization practices before storing the course details in the GAE datastore.
  
  * TeamMates needs to ensure proper privacy policies and role based login to not only prevent unauthorized access to user feedbacks but also to preserve the privacy of the user feedbacks. 
  
  * TeamMates needs to ensure secure encryption practices, secure sanitization practices as well as proper session management to prevent unauthorizeds user from injecting any malicious scripts through XSS attack.
  
  
 
  ### LucidChart Link
  + LucidChart Link to Misuse Cases. Please click the [link](https://www.lucidchart.com/documents/edit/ae54e2f8-8f75-4d7f-b591-1a4fc93d6dab/0).

**3. REVIEW OSS PROJECT DOCUMENTATION FOR ALIGNMENT OF SECURITY FEATURES**
-------------------------------------------------------------------

1. As per the design documentation of TeamMates the Logic component which includes the FeedbackSessionLogic sanitizes input values received from the UI component to conform to the data format required by TeamMates.This feature of TeamMates provides a certain level of security against HTTP Response Splitting Attack.
  
   **Few Observations From the Code**
  
   The Http responses for the FeedbackSession Module are properly sanitized in TeamMates using the SanitizationHelper class. This class:
   
   * Follows sanitization Practices for the string with rich-text to remove disallowed elements as per the TeamMates policy.
   * Sanitizes the feedback session URL by proper encoding methods.
   * Follows secure filtering mechanisms i.e. removes leading, trailing whitespaces from the input string.
   * Sanitizes the string for inserting into HTML. Converts special characters into HTML-safe equivalents. e.g. "<" is replaced "&lt;"
   * Sanitizes Strings containing java scripts by escaping some of the malicious special characters e.g. str.replace("\\", "\\\\") that     prevents any CRLF attacks 


2. As per the design documentation of TeamMates,it provides a mechanism for checking access control rights which in turn provides protection against any brute force attack staged by a malicious user.Also the documentation mentions TeamMates runs on GoogleAppEngine. So, to login a user needs to have a google account. Account creation in TeamMates follows similar secure practices as google. Detailed practices are not mentioned in the document as GAE is a third-party server.
 
   **Few Other Observations From the Code**
   
   * TeamMates enforces secure access control rights during user Login by using a GateKeeper class which is accessed by the LoginServlet to check the access control. The GateKeeper class checks if the user account details are present in the datastore. After which the privileges are assigned to the user based on its role. This prevents unauthorized users to access and exploit the teammates modules.
   
3.

4. Based on the design documentation of TeamMates, the logic to ensure privacy comes from implementing role based login. Role-based login ensures restricted access to authorized users by providing clear access levels. 

   **Few Observations from Code**

   * The user roles are specified with the UserRole package with three roles ADMIN, INSTRUCTOR, STUDENT declared as constants, enums. The userRole variable stores the role, and assertEquals function validates for user role and to ensure their respective actions

5. Based on the design documentation of teammates, for strong encryption policies against XSS attacks while proving feedback the logic components use Fieldvalidator and sanitizationHelper packages to sanitize the data received from the users browser. 

   **Few Observations from Code**
   
   * The Http responses for the FeedbackSession Module are properly sanitized in TeamMates using the SanitizationHelper class. This class contains methods to sanitize user provided parameters so that they conform to given data format and possible threats can be removed first as well as methods to revert sanitized text back to its previous unsanitized state.
The class CryptoHelper Ensures the encryption policies required for the feedback session. 


**4. REVIEW OSS PROJECT DOCUMENTATION FOR SECURITY RELATED CONFIGURATION AND INSTALLATION ISSUES**
--------------------------------------------------------------------------------------------------

+ Index.jsp in the teammates webapp module has enforced secure practices for all the links where target="_blank" is mentioned by adding       rel="noopener noreferrer" with it as mentioned in the link "http://conferences.computer.org/cseet/2011/CSEET_2011/Index.html" target="_blank" rel="noopener noreferrer" and this prevents any type of phishing attack. Whereas for the link
"https://developers.google.com/open-source/gsoc/" target="_blank" although target="_blank" is mentioned but rel="noopener noreferrer" is not added


+ After the installation process was done in the local server, adding a new instructor in the adminhome page did not work as the button was blocked and no instructions were given in the TeamMates documentation on how to resolve it. The solution was to add “npm run build” first then try to add the instructor in the dev server from the Admin Home page. This detail needs to be added to the Development Guidelines of the TeamMates project documentation under “As Instructor” so that new users don’t face any issue while testing for adding new instructors in the dev server. 



