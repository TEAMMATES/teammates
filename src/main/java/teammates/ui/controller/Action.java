package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;

/** An 'action' to be performed by the system. If the logged in user is allowed
 * to perform the requested action, this object can talk to the back end to
 * perform that action.
 */
public abstract class Action {
    protected static Logger log = Utils.getLogger();
    
    protected Logic logic;
    
    /** This is used to ensure unregistered users don't access certain pages in the system */
    public String regkey = null;
    
    /** This will be the admin user if the application is running under the masquerade mode. */
    public AccountAttributes loggedInUser;
    
    /** This is the 'nominal' user. Need not be the logged in user */
    public AccountAttributes account;
    
    /** This is the unregistered and not loggedin student's attributes. */
    public StudentAttributes student = null;
    
    /** The full request URL e.g., {@code /page/instructorHome?user=abc&course=c1} */
    protected String requestUrl;
    
    /** Parameters received with the request */
    protected Map<String, String[]> requestParameters;
    
    /** Execution status info to be shown to he admin (in 'activity log')*/
    protected String statusToAdmin; //TODO: make this a list?
    
    /** Execution status info to be shown to the user */
    protected List<String> statusToUser = new ArrayList<String>();
    
    /** Whether the execution completed without any errors */
    protected boolean isError = false;
    
    /** Session that contains status message information */
    protected HttpSession session;

    /** This is to get the blobInfo for any file upload from prev pages */
    protected HttpServletRequest request;
    
    /** This is for authentication at Action Level */
    private String authenticationRedirectUrl = ""; 
    
    /** Initializes variables. 
     * Aborts with an {@link UnauthorizedAccessException} if the user is not
     * logged in or if a non-admin tried to masquerade as another user.
     * 
     */
    public void init(HttpServletRequest req){
        initialiseAttributes(req);
        authenticateUser();
    }

    @SuppressWarnings("unchecked")
    protected void initialiseAttributes(HttpServletRequest req) {
        request = req;
        requestUrl = HttpRequestHelper.getRequestedURL(request);
        logic = new Logic();
        requestParameters = request.getParameterMap();
        session = request.getSession();
        
        //---- set error status forwarded from the previous action
        isError = getRequestParamAsBoolean(Const.ParamsNames.ERROR);
    }

    protected void authenticateUser() {
        UserType currentUser = logic.getCurrentUser();
        loggedInUser = authenticateAndGetActualUser(currentUser);
        if (isValidUser()) {
            account = authenticateAndGetNominalUser(currentUser);
        }
    }
    
    protected AccountAttributes authenticateAndGetActualUser(UserType currentUser) {
        if (doesUserNeedToLogin(currentUser)) return null;
        
        AccountAttributes loggedInUser = null;
        
        regkey = getRegkeyFromRequest();
        String email = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        if (currentUser == null) {
            Assumption.assertNotNull(regkey);
            loggedInUser = authenticateNotLoggedInUser(email, courseId);
        } else {
            loggedInUser = logic.getAccount(currentUser.id);
            if (doesRegkeyMatchLoggedInUserGoogleId(currentUser.id)) {
                loggedInUser = createDummyAccountIfUserIsUnregistered(currentUser, loggedInUser);
            }
        }
        return loggedInUser;
    }

    protected String getRegkeyFromRequest() {
        String regkey = getRequestParamValue(Const.ParamsNames.REGKEY);
        if (regkey == null) {
            //TODO: remove this branch on October 15th 2014.
            return getRequestParamValue(Const.ParamsNames.REGKEY_LEGACY);
        } else {
            return regkey;
        }
    }

    protected AccountAttributes createDummyAccountIfUserIsUnregistered(UserType currentUser,
            AccountAttributes loggedInUser) {
        if(loggedInUser==null) { //Unregistered but loggedin user
            loggedInUser = new AccountAttributes();
            loggedInUser.googleId = currentUser.id;
        }
        return loggedInUser;
    }

    protected boolean doesRegkeyMatchLoggedInUserGoogleId(
            String loggedInUserId) {
        if(regkey != null && loggedInUserId != null) {
            student = logic.getStudentForRegistrationKey(regkey);
            boolean isKnownKey = student != null;
            if (isKnownKey) {
                if (student.isRegistered() && !loggedInUserId.equals(student.googleId)) {
                    String expectedId = StringHelper.obscure(student.googleId);
                    expectedId = StringHelper.encrypt(expectedId);
                    Url redirectUrl = new Url(Const.ViewURIs.LOGOUT)
                                        .withParam(Const.ParamsNames.NEXT_URL, Logic.getLoginUrl(requestUrl))
                                        .withParam(Const.ParamsNames.HINT, expectedId)
                                        .withUserId(StringHelper.encrypt(loggedInUserId)); 
                    
                    setRedirectPage(redirectUrl.toString());
                    return false;
                }
            }
        }
        return true;
    }

    protected AccountAttributes authenticateNotLoggedInUser(String email, String courseId) {
        
        student = logic.getStudentForRegistrationKey(regkey);
        boolean isUnknownKey = student == null;
        boolean isARegisteredUser = !isUnknownKey && student.googleId != null && !student.googleId.isEmpty();
        boolean isMissingAdditionalAuthenticationInfo = email == null || courseId == null;
        boolean isAuthenticationFailure = !isUnknownKey &&  (!student.email.equals(email) || !student.course.equals(courseId));
        
        AccountAttributes loggedInUser = null;
        
        if (isUnknownKey) {
            throw new UnauthorizedAccessException("Unknown Registration Key " + regkey);
        } else if (isARegisteredUser) {
            setRedirectPage(Logic.getLoginUrl(requestUrl));
            return null;
        } else if (isNotLegacyLink() && isMissingAdditionalAuthenticationInfo) {
            throw new UnauthorizedAccessException("Insufficient information to authenticate user");
        } else if (isNotLegacyLink() && isAuthenticationFailure) {
            throw new UnauthorizedAccessException("Invalid email/course for given Registration Key");
        } else {
            // unregistered and not logged in access given to page
            loggedInUser = new AccountAttributes();
            loggedInUser.email = student.email;
        }
        
        return loggedInUser;
    }

    private boolean isNotLegacyLink() {
        return !Const.SystemParams.LEGACY_PAGES_WITH_REDUCED_SECURITY.contains(request.getRequestURI());
    }

    private boolean doesUserNeedToLogin(UserType currentUser) {
        boolean userNeedsGoogleAccountForPage = !Const.SystemParams.PAGES_ACCESSIBLE_WITHOUT_GOOGLE_LOGIN.contains(request.getRequestURI());
        boolean userIsNotLoggedIn = currentUser == null;
        boolean noRegkeyGiven = getRegkeyFromRequest() == null;
        
        if (userIsNotLoggedIn && (userNeedsGoogleAccountForPage || noRegkeyGiven)) {
            setRedirectPage(Logic.getLoginUrl(requestUrl));
            return true;
        }
        return false;
    }

    protected AccountAttributes authenticateAndGetNominalUser(UserType loggedInUserType) {
        String paramRequestedUserId = request.getParameter(Const.ParamsNames.USER_ID);
        
        AccountAttributes account = null;
        
        if (!isMasqueradeModeRequested(loggedInUser, paramRequestedUserId)) {
            account = loggedInUser;
            boolean isUserLoggedIn = account.googleId != null;
            if (doesUserNeedRegistration(account) && !loggedInUserType.isAdmin) {
                if (regkey != null) {
                    // TODO: encrypt the email as currently anyone with the regkey can
                    //       get the email because of this redirect:
                    
                    String joinUrl = new Url(student.getRegistrationUrl())
                                        .withParam(Const.ParamsNames.NEXT_URL, requestUrl)
                                        .toString();
                    setRedirectPage(joinUrl);
                    return null;
                }
                throw new UnauthorizedAccessException("Unregistered user for a page that needs one");
            } else if (isPageNotCourseJoinRelated() && doesRegkeyBelongToUnregisteredStudent() &&  isUserLoggedIn) {
                Url redirectUrl = new Url(Const.ViewURIs.LOGOUT)
                .withParam(Const.ParamsNames.NEXT_URL, requestUrl)
                .withUserId(StringHelper.encrypt(account.googleId)); 
                
                setRedirectPage(redirectUrl.toString());
                return null;
            }
        } else if (loggedInUserType.isAdmin) {
            //Allowing admin to masquerade as another user
            account = logic.getAccount(paramRequestedUserId);
            if(account==null){ //Unregistered user
                regkey = getRegkeyFromRequest();
                if (regkey == null) {
                    // since admin is masquerading, fabricate a regkey
                    regkey = "any-non-null-value";
                }
                account = new AccountAttributes();
                account.googleId = paramRequestedUserId;
            }
        } else {
            throw new UnauthorizedAccessException("User " + loggedInUserType.id
                    + " is trying to masquerade as " + paramRequestedUserId
                    + " without admin permission.");
        }
        
        return account;
    }

    private boolean isPageNotCourseJoinRelated() {
        String currentURI = request.getRequestURI();
        return !currentURI.equals(Const.ActionURIs.STUDENT_COURSE_JOIN) && !currentURI.equals(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                && !currentURI.equals(Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED);
    }

    private boolean doesRegkeyBelongToUnregisteredStudent() {
        return student != null && !student.isRegistered();
    }

    private boolean doesUserNeedRegistration(AccountAttributes user) {
        boolean userNeedsRegistrationForPage = !Const.SystemParams.PAGES_ACCESSIBLE_WITHOUT_REGISTRATION.contains(request.getRequestURI())
                && !Const.SystemParams.PAGES_ACCESSIBLE_WITHOUT_GOOGLE_LOGIN.contains(request.getRequestURI());
        boolean userIsNotRegistered = user.createdAt == null;
        return userNeedsRegistrationForPage && userIsNotRegistered;
    }
    
    /** ------------------------------------------------
     * These methods are used for CRUD operations on
     * urls used for redirecting users to login page
     * @return
     */
    public boolean isValidUser() {
        return authenticationRedirectUrl.isEmpty();
    }
    
    private void setRedirectPage(String redirectUrl) {
        authenticationRedirectUrl = redirectUrl;
        statusToAdmin = "Redirecting user to " + redirectUrl;
    }
    
    protected String getAuthenticationRedirectUrl() {
        return authenticationRedirectUrl;
    }
    
    /** ------------------------------------------------ */

    /**
     * Executes the action (as implemented by a child class). Before passing 
     * the result to the caller, it does some post processing: <br>
     * 1. If the original request contained a URL to redirect after performing 
     *    the action, the result will be replaced with a new 'redirect' type
     *    result. Note: Redirection is not allowed to third-party destinations. <br>
     * 2. User ID, error flag, and the status message will be added to the response,
     *    to be encoded into the URL. The error flag is also added to the
     *    {@code isError} flag in the {@link ActionResult} object.
     */
    public ActionResult executeAndPostProcess() throws EntityDoesNotExistException {
        if (!isValidUser()) {
            return createRedirectResult(getAuthenticationRedirectUrl());
        }
        //get the result from the child class.
        ActionResult response = execute();
        
        //set error flag of the result
        response.isError = isError;
        
        // Set the common parameters for the response
        if (logic.getCurrentUser() != null) {
            response.responseParams.put(Const.ParamsNames.USER_ID, account.googleId);
        } 
        if (regkey != null) {
            response.responseParams.put(Const.ParamsNames.REGKEY, regkey);
            
            if (student != null) {
                response.responseParams.put(Const.ParamsNames.STUDENT_EMAIL, student.email);
                response.responseParams.put(Const.ParamsNames.COURSE_ID, student.course);
            }
            
            if (getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME) != null) {
                response.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, 
                        getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME));
            }
        }
        response.responseParams.put(Const.ParamsNames.ERROR, "" + response.isError);
        
        //Pass status message using session to prevent XSS attack
        if(!response.getStatusMessage().isEmpty()){
            putStatusMessageToSession(response);
        }
        
        return response;
    }

    protected void putStatusMessageToSession(ActionResult response) {
        String statusMessageInSession = (String) session.getAttribute(Const.ParamsNames.STATUS_MESSAGE);
        if(statusMessageInSession == null || statusMessageInSession.isEmpty()){
            session.setAttribute(Const.ParamsNames.STATUS_MESSAGE, response.getStatusMessage());
        } else {
            session.setAttribute(Const.ParamsNames.STATUS_MESSAGE, statusMessageInSession + "<br />"  + response.getStatusMessage());
        }
    }

    /**
     * The method is expected to: <br>
     * 1. Check if the user has rights to execute this action. <br>
     * 2. Execute the action.<br>
     * 3. If the action requires showing a page, prepare the matching PageData object.<br>
     * 4. Set the status messages to be shown to the user (if any) and to the admin (compulsory).
     *    The latter is used for generating the adminActivityLogPage.
     * @throws NullPostParametersException 
     */
    protected abstract ActionResult execute() 
            throws EntityDoesNotExistException;

    /**
     * @return The log message in the special format used for generating 
     *   the 'activity log' for the Admin.
     */
    public String getLogMessage(){
        ActivityLogEntry activityLogEntry = new ActivityLogEntry(
                account, 
                isInMasqueradeMode(),
                statusToAdmin, 
                requestUrl);
        return activityLogEntry.generateLogMessage();
    }
    

    /**
     * @return null if the specified parameter was not found in the request.
     */
    public String getRequestParamValue(String paramName) {
        return HttpRequestHelper.getValueFromParamMap(requestParameters, paramName);
    }
    
    /**
     * @return null if the specified parameter was not found in the request.
     */
    public String[] getRequestParamValues(String paramName) {
        return HttpRequestHelper.getValuesFromParamMap(requestParameters, paramName);
    }
    
    public boolean getRequestParamAsBoolean(String paramName) {
        return Boolean.parseBoolean(HttpRequestHelper.getValueFromParamMap(requestParameters, paramName));
    }

    /**
     * Generates a {@link ShowPageResult} with the information in this object.
     */
    public ShowPageResult createShowPageResult(String destination, PageData pageData) {
        return new ShowPageResult(
                destination, 
                account,
                requestParameters,
                pageData,
                statusToUser);
    }
    
    /**
     * Generates a {@link ShowPageResult} with the information in this object.
     */
    public AjaxResult createAjaxResult(String destination, PageData pageData) {
        return new AjaxResult(
                destination, 
                account,
                requestParameters,
                statusToUser,
                pageData);
    }
    
    protected boolean isJoinedCourse(String courseId, String googleId) {
        if (student != null) {
            return true;
        } else {
            return logic.getStudentForGoogleId(courseId, account.googleId) != null;
        }
    }

    /**
     * Generates a {@link RedirectResult} with the information in this object.
     */
    public RedirectResult createRedirectResult(String destination) {
        return new RedirectResult(
                destination, 
                account,
                requestParameters,
                statusToUser);
    }
    
    /**
     * Generates a {@link FileDownloadResult} with the information in this object.
     */
    public FileDownloadResult createFileDownloadResult(String fileName, String fileContent) {
        return new FileDownloadResult(
                "filedownload", 
                account,
                requestParameters,
                statusToUser,
                fileName,
                fileContent);
    }

    protected ActionResult createPleaseJoinCourseResponse(String courseId) {
        String errorMessage = "You are not registered in the course " + Sanitizer.sanitizeForHtml(courseId);
        statusToUser.add(errorMessage);
        isError = true;
        statusToAdmin = Const.ACTION_RESULT_FAILURE + " : " + errorMessage; 
        return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
    }
    
    protected ActionResult createImageResult(String blobKey) {
        return new ImageResult(
                "imagedisplay",
                blobKey,
                account,
                requestParameters,
                statusToUser);
    }

    /**
     * Status messages to be shown to the user and the admin will be set based
     * on the error message in the exception {@code e}.<br>
     * {@code isError} is also set to true.
     */
    protected void setStatusForException(Exception e) {
        statusToUser.add(e.getMessage());
        isError = true;
        statusToAdmin = Const.ACTION_RESULT_FAILURE + " : " + e.getMessage();
    }
    
    /**
     * Status messages to be shown to the admin will be set based
     * on the error message in the exception {@code e}.<br>
     * Status message to be shown to the user will be set as {@code statusMessageToUser}.<br>
     * {@code isError} is also set to true.
     */
    protected void setStatusForException(Exception e, String statusMessageToUser) {
        statusToUser.add(statusMessageToUser);
        isError = true;
        statusToAdmin = Const.ACTION_RESULT_FAILURE + " : " + e.getMessage();
    }

    protected boolean isInMasqueradeMode() {
        try { 
            return !loggedInUser.googleId.equals(account.googleId);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean isMasqueradeModeRequested(AccountAttributes loggedInUser, String requestedUserId) {
        return loggedInUser != null && requestedUserId != null
                && !requestedUserId.trim().equals("null")
                && !loggedInUser.googleId.equals(requestedUserId);
    }
    
    //===================== Utility methods used by some child classes========
    
    protected EvaluationAttributes extractEvaluationData() {
        EvaluationAttributes newEval = new EvaluationAttributes();
        newEval.courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        newEval.name = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        newEval.p2pEnabled = getRequestParamAsBoolean(Const.ParamsNames.EVALUATION_COMMENTSENABLED);
        
        newEval.startTime = TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.EVALUATION_START),
                getRequestParamValue(Const.ParamsNames.EVALUATION_STARTTIME));

        newEval.endTime = TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.EVALUATION_DEADLINE),
                getRequestParamValue(Const.ParamsNames.EVALUATION_DEADLINETIME));

        String paramTimeZone = getRequestParamValue(Const.ParamsNames.EVALUATION_TIMEZONE);
        if (paramTimeZone != null) {
            newEval.timeZone = Double.parseDouble(paramTimeZone);
        }

        String paramGracePeriod = getRequestParamValue(Const.ParamsNames.EVALUATION_GRACEPERIOD);
        if (paramGracePeriod != null) {
            newEval.gracePeriod = Integer.parseInt(paramGracePeriod);
        }

        newEval.instructions = new Text(getRequestParamValue(Const.ParamsNames.EVALUATION_INSTRUCTIONS));

        Assumption.assertNotNull("courseId is null when extracting evaluation data", newEval.courseId);
        Assumption.assertNotNull("Evaluation name is null when extracting evaluation data", newEval.name);
        Assumption.assertNotNull("p2pEnabled is null when extracting evaluation data", newEval.p2pEnabled);
        Assumption.assertNotNull("startTime is null when extracting evaluation data", newEval.startTime);
        Assumption.assertNotNull("endTime is null when extracting evaluation data", newEval.endTime);
        Assumption.assertNotNull("timeZone is null when extracting evaluation data", newEval.timeZone);
        Assumption.assertNotNull("gracePeriod is null when extracting evaluation data", newEval.gracePeriod);
        Assumption.assertNotNull("instructions is null when extracting evaluation data", newEval.instructions);
                
        return newEval;
    }
    
    protected void excludeStudentDetailsFromResponseParams() {
        regkey = null;
    }
}
