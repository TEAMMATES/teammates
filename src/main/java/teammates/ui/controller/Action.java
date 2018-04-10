package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidOriginException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.CryptoHelper;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.LogMessageGenerator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.logic.api.EmailSender;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;
import teammates.logic.api.TaskQueuer;
import teammates.ui.pagedata.PageData;

/** An 'action' to be performed by the system. If the logged in user is allowed
 * to perform the requested action, this object can talk to the back end to
 * perform that action.
 */
public abstract class Action {

    /** This is used to ensure unregistered users don't access certain pages in the system. */
    public String regkey;

    /** The regkey may also contain a next url parameter as well. */
    public String nextUrlFromRegkey;

    /** This will be the admin user if the application is running under the masquerade mode. */
    public AccountAttributes loggedInUser;

    /** This is the 'nominal' user. Need not be the logged in user */
    public AccountAttributes account;

    /** This is the unregistered and not loggedin student's attributes. */
    public StudentAttributes student;

    protected Logic logic;
    protected GateKeeper gateKeeper;
    protected TaskQueuer taskQueuer;
    protected EmailSender emailSender;

    /** The full request URL e.g., {@code /page/instructorHome?user=abc&course=c1}. */
    protected String requestUrl;

    /** Parameters received with the request. */
    protected Map<String, String[]> requestParameters;

    /** Execution status info to be shown to he admin (in 'activity log'). */
    protected String statusToAdmin; // TODO: make this a list?

    /** Execution status info to be shown to the user. */
    protected List<StatusMessage> statusToUser = new ArrayList<>();

    /**
     * Whether the execution completed without any errors or
     * when we are unable to perform the requested action(s).
     **/
    protected boolean isError;

    /** Session that contains status message information. */
    protected HttpSession session;

    /** Session token used in forms/links to actions requiring origin validation. */
    protected String sessionToken;

    /** This is to get the blobInfo for any file upload from prev pages. */
    protected HttpServletRequest request;

    /** This is for authentication at Action Level. */
    private String authenticationRedirectUrl = "";

    /** Initializes variables.
     * Aborts with an {@link UnauthorizedAccessException} if the user is not
     * logged in or if a non-admin tried to masquerade as another user.
     *
     */
    public void init(HttpServletRequest req) {
        initialiseAttributes(req);
        validateOriginIfRequired();
        authenticateUser();
    }

    @SuppressWarnings("unchecked")
    protected void initialiseAttributes(HttpServletRequest req) {
        request = req;
        requestUrl = HttpRequestHelper.getRequestedUrl(request);
        logic = new Logic();
        gateKeeper = new GateKeeper();
        setTaskQueuer(new TaskQueuer());
        setEmailSender(new EmailSender());
        requestParameters = request.getParameterMap();
        session = request.getSession();
        sessionToken = CryptoHelper.computeSessionToken(session.getId());
        parseAndInitializeRegkeyFromRequest();
        // Set error status forwarded from the previous action
        isError = getRequestParamAsBoolean(Const.ParamsNames.ERROR);
    }

    /**
     * Parses and initializes the regkey from the http request.
     */
    private void parseAndInitializeRegkeyFromRequest() {
        String regkeyFromRequest = getRegkeyFromRequest();
        boolean isNextParamInRegkey = regkeyFromRequest != null
                                      && regkeyFromRequest.contains("${amp}" + Const.ParamsNames.NEXT_URL + "=");
        if (isNextParamInRegkey) {
            /*
             * Here regkey may contain the nextUrl as well. This is due to
             * a workaround which replaces "&" with a placeholder "${amp}", thus the
             * next parameter, nextUrl, is treated as part of the "regkey".
             */
            String[] split = regkeyFromRequest.split("\\$\\{amp\\}" + Const.ParamsNames.NEXT_URL + "=");
            regkey = split[0];
            nextUrlFromRegkey = SanitizationHelper.desanitizeFromNextUrl(split[1]);
        } else {
            regkey = regkeyFromRequest;
            nextUrlFromRegkey = null;
        }
    }

    public TaskQueuer getTaskQueuer() {
        return taskQueuer;
    }

    public void setTaskQueuer(TaskQueuer taskQueuer) {
        this.taskQueuer = taskQueuer;
    }

    public EmailSender getEmailSender() {
        return emailSender;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    // These methods are used for Cross-Site Request Forgery (CSRF) prevention

    private void validateOriginIfRequired() {
        if (!Const.SystemParams.PAGES_REQUIRING_ORIGIN_VALIDATION.contains(request.getRequestURI())) {
            return;
        }

        String referrer = request.getHeader("referer");
        if (referrer == null) {
            // Requests with missing referrer information are given the benefit of the doubt to
            // accommodate users who choose to disable the HTTP referrer setting in their browser
            // for privacy reasons
        } else if (!isHttpReferrerValid(referrer)) {
            throw new InvalidOriginException("Invalid HTTP referrer");
        }

        String sessionToken = getRequestParamValue(Const.ParamsNames.SESSION_TOKEN);
        if (sessionToken == null) {
            throw new InvalidOriginException("Missing session token");
        }

        if (!isSessionTokenValid(sessionToken)) {
            throw new InvalidOriginException("Invalid session token");
        }
    }

    /**
     * Validates the HTTP referrer against the request URL. The origin is the
     * base URL of the HTTP referrer, which includes the protocol and authority
     * (host name + port number if specified). Similarly, the target is the base
     * URL of the requested action URL. For the referrer to be considered valid,
     * origin and target must match exactly. Otherwise, the request is likely to
     * be a CSRF attack, and is considered invalid.
     *
     * <p>Example of malicious request originating from embedded image in email:
     * <pre>
     * Request URL: https://teammatesv4.appspot.com/page/instructorCourseDelete?courseid=abcdef
     * Referrer:    https://mail.google.com/mail/u/0/
     *
     * Target: https://teammatesv4.appspot.com
     * Origin: https://mail.google.com
     * </pre>
     * Origin does not match target. This request is invalid.</p>
     *
     * <p>Example of legitimate request originating from instructor courses page:
     * <pre>
     * Request URL: https://teammatesv4.appspot.com/page/instructorCourseDelete?courseid=abcdef
     * Referrer:    https://teammatesv4.appspot.com/page/instructorCoursesPage
     *
     * Target: https://teammatesv4.appspot.com
     * Origin: https://teammatesv4.appspot.com
     * </pre>
     * Origin matches target. This request is valid.</p>
     */
    private boolean isHttpReferrerValid(String referrer) {
        String origin;
        try {
            origin = new Url(referrer).getBaseUrl();
        } catch (AssertionError e) { // due to MalformedURLException
            return false;
        }

        String requestUrl = request.getRequestURL().toString();
        String target = new Url(requestUrl).getBaseUrl();

        return origin.equals(target);
    }

    private boolean isSessionTokenValid(String actualToken) {
        String sessionId = request.getRequestedSessionId();
        if (sessionId == null) {
            // Newly-created session
            sessionId = session.getId();
        }

        String expectedToken = CryptoHelper.computeSessionToken(sessionId);

        return actualToken.equals(expectedToken);
    }

    // These methods are used for user authentication

    protected void authenticateUser() {
        UserType currentUser = gateKeeper.getCurrentUser();
        loggedInUser = authenticateAndGetActualUser(currentUser);
        if (isValidUser()) {
            account = authenticateAndGetNominalUser(currentUser);
        }
    }

    protected AccountAttributes authenticateAndGetActualUser(UserType currentUser) {
        if (doesUserNeedToLogin(currentUser)) {
            return null;
        }

        AccountAttributes loggedInUser = null;

        String email = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (currentUser == null) {
            Assumption.assertPostParamNotNull(Const.ParamsNames.REGKEY, regkey);
            loggedInUser = authenticateNotLoggedInUser(email, courseId);
        } else {
            loggedInUser = logic.getAccount(currentUser.id);
            if (doesRegkeyMatchLoggedInUserGoogleId(currentUser.id)) {
                loggedInUser = createDummyAccountIfUserIsUnregistered(currentUser, loggedInUser);
            }
        }

        return loggedInUser;
    }

    /**
     * Retrieves registration key from the HTTP request.
     *
     * @return Registration key or null if key not in HTTP request
     */
    protected String getRegkeyFromRequest() {
        return getRequestParamValue(Const.ParamsNames.REGKEY);
    }

    protected AccountAttributes createDummyAccountIfUserIsUnregistered(UserType currentUser,
            AccountAttributes loggedInUser) {
        if (loggedInUser == null) { // Unregistered but loggedin user
            return AccountAttributes.builder()
                    .withGoogleId(currentUser.id)
                    .build();
        }
        return loggedInUser;
    }

    protected boolean doesRegkeyMatchLoggedInUserGoogleId(
            String loggedInUserId) {
        if (regkey != null && loggedInUserId != null) {
            student = logic.getStudentForRegistrationKey(regkey);
            boolean isKnownKey = student != null;
            if (isKnownKey && student.isRegistered() && !loggedInUserId.equals(student.googleId)) {
                String expectedId = StringHelper.obscure(student.googleId);
                expectedId = StringHelper.encrypt(expectedId);
                String redirectUrl = Config.getAppUrl(Const.ActionURIs.LOGOUT)
                                          .withUserId(StringHelper.encrypt(loggedInUserId))
                                          .withParam(Const.ParamsNames.NEXT_URL, gateKeeper.getLoginUrl(requestUrl))
                                          .withParam(Const.ParamsNames.HINT, expectedId)
                                          .toString();

                setRedirectPage(redirectUrl);
                return false;
            }
        }
        return true;
    }

    protected AccountAttributes authenticateNotLoggedInUser(String email, String courseId) {
        student = logic.getStudentForRegistrationKey(regkey);
        boolean isUnknownKey = student == null;
        boolean isARegisteredUser = !isUnknownKey && student.googleId != null && !student.googleId.isEmpty();
        boolean isMissingAdditionalAuthenticationInfo = email == null || courseId == null;
        boolean isAuthenticationFailure = !isUnknownKey
                                          && (!student.email.equals(email) || !student.course.equals(courseId));

        AccountAttributes loggedInUser = null;

        if (isUnknownKey) {
            throw new UnauthorizedAccessException("Unknown Registration Key " + regkey);
        } else if (isARegisteredUser) {
            setRedirectPage(gateKeeper.getLoginUrl(requestUrl));
            return null;
        } else if (isNotLegacyLink() && isMissingAdditionalAuthenticationInfo) {
            throw new UnauthorizedAccessException("Insufficient information to authenticate user");
        } else if (isNotLegacyLink() && isAuthenticationFailure) {
            throw new UnauthorizedAccessException("Invalid email/course for given Registration Key");
        } else {
            // Unregistered and not logged in access given to page
            loggedInUser = AccountAttributes.builder()
                    .withEmail(student.email)
                    .build();
        }

        return loggedInUser;
    }

    private boolean isNotLegacyLink() {
        return !Const.SystemParams.LEGACY_PAGES_WITH_REDUCED_SECURITY.contains(request.getRequestURI());
    }

    private boolean doesUserNeedToLogin(UserType currentUser) {
        boolean isGoogleLoginRequired =
                !Const.SystemParams.PAGES_ACCESSIBLE_WITHOUT_GOOGLE_LOGIN.contains(request.getRequestURI());
        boolean isUserLoggedIn = currentUser != null;
        boolean hasRegkey = getRegkeyFromRequest() != null;

        if (!isUserLoggedIn && (isGoogleLoginRequired || !hasRegkey)) {
            setRedirectPage(gateKeeper.getLoginUrl(requestUrl));
            return true;
        }

        return false;
    }

    protected AccountAttributes authenticateAndGetNominalUser(UserType loggedInUserType) {
        String paramRequestedUserId = request.getParameter(Const.ParamsNames.USER_ID);

        AccountAttributes account = null;

        if (isMasqueradeModeRequested(loggedInUser, paramRequestedUserId)) {
            if (loggedInUserType.isAdmin) {
                // Allowing admin to masquerade as another user
                account = logic.getAccount(paramRequestedUserId);
                if (account == null) { // Unregistered user
                    if (regkey == null) {
                        // since admin is masquerading, fabricate a regkey
                        regkey = "any-non-null-value";
                    }
                    account = AccountAttributes.builder()
                            .withGoogleId(paramRequestedUserId)
                            .build();
                }
                return account;
            }
            throw new UnauthorizedAccessException("User " + loggedInUserType.id
                                                + " is trying to masquerade as " + paramRequestedUserId
                                                + " without admin permission.");
        }

        account = loggedInUser;
        if (isPersistenceIssue() && isHomePage()) {
            // let the user go through as this is a persistence issue
        } else if (doesUserNeedRegistration(account) && !loggedInUserType.isAdmin) {
            if (regkey != null && student != null) {
                // TODO: encrypt the email as currently anyone with the regkey can
                //       get the email because of this redirect:
                String joinUrl = Config.getAppUrl(student.getRegistrationUrl())
                                    .withParam(Const.ParamsNames.NEXT_URL, requestUrl)
                                    .toString();
                setRedirectPage(joinUrl);
                return null;
            }

            throw new UnauthorizedAccessException("Unregistered user for a page that needs registration");
        }

        boolean isUserLoggedIn = account.googleId != null;
        if (isPageNotCourseJoinRelated() && doesRegkeyBelongToUnregisteredStudent() && isUserLoggedIn) {
            String redirectUrl = Config.getAppUrl(student.getRegistrationUrl())
                                  .withParam(Const.ParamsNames.NEXT_URL, requestUrl)
                                  .toString();
            setRedirectPage(redirectUrl);
            return null;
        }

        return account;
    }

    protected boolean isPersistenceIssue() {
        String persistenceCheckString1 =
                getRequestParamValue(Const.ParamsNames.CHECK_PERSISTENCE_COURSE);

        return persistenceCheckString1 != null;
    }

    private boolean isPageNotCourseJoinRelated() {
        String currentUri = request.getRequestURI();
        return !currentUri.equals(Const.ActionURIs.STUDENT_COURSE_JOIN)
               && !currentUri.equals(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
               && !currentUri.equals(Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED);
    }

    private boolean isHomePage() {
        String currentUri = request.getRequestURI();
        return currentUri.equals(Const.ActionURIs.STUDENT_HOME_PAGE)
               || currentUri.equals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
    }

    private boolean doesRegkeyBelongToUnregisteredStudent() {
        return student != null && !student.isRegistered();
    }

    private boolean doesUserNeedRegistration(AccountAttributes user) {
        boolean userNeedsRegistrationForPage =
                !Const.SystemParams.PAGES_ACCESSIBLE_WITHOUT_REGISTRATION.contains(request.getRequestURI())
                && !Const.SystemParams.PAGES_ACCESSIBLE_WITHOUT_GOOGLE_LOGIN.contains(request.getRequestURI());
        boolean userIsNotRegistered = user.createdAt == null;
        return userNeedsRegistrationForPage && userIsNotRegistered;
    }

    // These methods are used for CRUD operations on urls used for redirecting users to login page

    public boolean isValidUser() {
        return authenticationRedirectUrl.isEmpty();
    }

    private void setRedirectPage(String redirectUrl) {
        authenticationRedirectUrl = redirectUrl;
        statusToAdmin = "Redirecting user to " + redirectUrl;
    }

    public String getAuthenticationRedirectUrl() {
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
    public ActionResult executeAndPostProcess() {
        if (!isValidUser()) {
            return createRedirectResult(getAuthenticationRedirectUrl());
        }

        // get the result from the child class.
        ActionResult response;
        try {
            response = execute();
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        // set error flag of the result
        response.isError = isError;

        // Set the common parameters for the response
        if (gateKeeper.getCurrentUser() != null) {
            response.responseParams.put(Const.ParamsNames.USER_ID, account.googleId);
        }

        if (regkey != null) {
            response.responseParams.put(Const.ParamsNames.REGKEY, getRegkeyFromRequest());

            if (student != null) {
                response.responseParams.put(Const.ParamsNames.STUDENT_EMAIL, student.email);
                response.responseParams.put(Const.ParamsNames.COURSE_ID, student.course);
            }

            if (getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME) != null) {
                response.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME,
                        getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME));
            }
        }
        response.responseParams.put(Const.ParamsNames.ERROR, Boolean.toString(response.isError));

        // Pass status message using session to prevent XSS attack
        if (!response.getStatusMessage().isEmpty()) {
            putStatusMessageToSession(response);
        }

        return response;
    }

    /**
     * Adds the list of status messages from ActionResult into session variables.
     * @param response ActionResult
     */
    protected void putStatusMessageToSession(ActionResult response) {
        @SuppressWarnings("unchecked")
        List<StatusMessage> statusMessagesToUser =
                (List<StatusMessage>) session.getAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST);

        if (statusMessagesToUser == null) {
            statusMessagesToUser = new ArrayList<>();
        }

        statusMessagesToUser.addAll(response.statusToUser);
        session.setAttribute(Const.ParamsNames.STATUS_MESSAGES_LIST, statusMessagesToUser);
    }

    /**
     * The method is expected to: <br>
     * 1. Check if the user has rights to execute this action. <br>
     * 2. Execute the action.<br>
     * 3. If the action requires showing a page, prepare the matching PageData object.<br>
     * 4. Set the status messages to be shown to the user (if any) and to the admin (compulsory).
     *    The latter is used for generating the adminActivityLogPage.
     */
    // TODO handle the EntityDoesNotExistException properly in the method body so it does not
    // have to be re-thrown here
    protected abstract ActionResult execute() throws EntityDoesNotExistException;

    /**
     * Returns The log message in the special format used for generating the 'activity log' for the Admin.
     */
    public String getLogMessage() {
        UserType currUser = gateKeeper.getCurrentUser();
        return new LogMessageGenerator().generatePageActionLogMessage(requestUrl, requestParameters, currUser,
                                                                      account, student, statusToAdmin);
    }

    /**
     * Returns null if the specified parameter was not found in the request.
     */
    public String getRequestParamValue(String paramName) {
        return HttpRequestHelper.getValueFromParamMap(requestParameters, paramName);
    }

    /**
     * Returns the value for the specified parameter expected to be present in the http request.
     * Assumption: the requested parameter is not null.
     *
     * @param paramName  a constant from the {@link Const.ParamsNames} class.
     */
    public String getNonNullRequestParamValue(String paramName) {
        return getNonNullRequestParamValues(paramName)[0];
    }

    /**
     * Returns null if the specified parameter was not found in the request.
     */
    public String[] getRequestParamValues(String paramName) {
        return HttpRequestHelper.getValuesFromParamMap(requestParameters, paramName);
    }

    /**
     * Returns the values for the specified parameter expected to be present in the http request.
     * Assumption: the requested parameter is not null.
     *
     * @param paramName  a constant from the {@link Const.ParamsNames} class.
     */
    public String[] getNonNullRequestParamValues(String paramName) {
        String[] values = getRequestParamValues(paramName);
        Assumption.assertPostParamNotNull(paramName, values);
        return values;
    }

    /**
     * Returns false if the specified parameter was not found in the request.
     */
    public boolean getRequestParamAsBoolean(String paramName) {
        return Boolean.parseBoolean(getRequestParamValue(paramName));
    }

    /**
     * Generates a {@link ShowPageResult} with the information in this object.
     */
    public ShowPageResult createShowPageResult(String destination, PageData pageData) {
        return new ShowPageResult(destination,
                                  account,
                                  pageData,
                                  statusToUser);
    }

    //TODO: Replace this with a overloaded constructor in AjaxResult?
    /**
     * Generates a {@link AjaxResult} with the information in this object.
     */
    public AjaxResult createAjaxResult(PageData pageData) {
        return new AjaxResult(account,
                              statusToUser,
                              pageData);
    }

    /**
     * Generates a {@link AjaxResult} with the information in the {@code pageData},
     * but without removing any status message from the session.
     */
    public AjaxResult createAjaxResultWithoutClearingStatusMessage(PageData pageData) {
        return new AjaxResult(account,
                              statusToUser,
                              pageData, false);
    }

    protected boolean isJoinedCourse(String courseId) {
        if (student == null) {
            return logic.getStudentForGoogleId(courseId, account.googleId) != null;
        }
        return true;
    }

    /**
     * Generates a {@link RedirectResult} with the information in this object.
     */
    public RedirectResult createRedirectResult(String destination) {
        return new RedirectResult(destination,
                                  account,
                                  statusToUser);
    }

    /**
     * Generates a {@link FileDownloadResult} with the information in this object.
     */
    public FileDownloadResult createFileDownloadResult(String fileName, String fileContent) {
        return new FileDownloadResult("filedownload",
                                      account,
                                      statusToUser,
                                      fileName,
                                      fileContent);
    }

    protected ActionResult createPleaseJoinCourseResponse(String courseId) {
        String errorMessage = "You are not registered in the course " + SanitizationHelper.sanitizeForHtml(courseId);
        statusToUser.add(new StatusMessage(errorMessage, StatusMessageColor.DANGER));
        isError = true;
        statusToAdmin = Const.ACTION_RESULT_FAILURE + " : " + errorMessage;
        return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
    }

    protected ActionResult createImageResult(String blobKey) {
        return new ImageResult("imagedisplay",
                               blobKey,
                               account,
                               statusToUser);
    }

    /**
     * Status messages to be shown to the user and the admin will be set based
     * on the error message in the exception {@code e}.<br>
     * {@code isError} is also set to true.
     */
    protected void setStatusForException(Exception e) {
        isError = true;

        String exceptionMessageForHtml = e.getMessage().replace(System.lineSeparator(), Const.HTML_BR_TAG);
        statusToUser.add(new StatusMessage(exceptionMessageForHtml, StatusMessageColor.DANGER));
        statusToAdmin = Const.ACTION_RESULT_FAILURE + " : " + exceptionMessageForHtml;
    }

    /**
     * Status messages to be shown to the admin will be set based
     * on the error message in the exception {@code e}.<br>
     * Status message to be shown to the user will be set as {@code statusMessageToUser}.<br>
     * {@code isError} is also set to true.
     */
    protected void setStatusForException(Exception e, String statusMessageToUser) {
        isError = true;

        String statusMessageForHtml = statusMessageToUser.replace(System.lineSeparator(), Const.HTML_BR_TAG);
        statusToUser.add(new StatusMessage(statusMessageForHtml, StatusMessageColor.DANGER));

        String exceptionMessageForHtml = e.getMessage().replace(System.lineSeparator(), Const.HTML_BR_TAG);
        statusToAdmin = Const.ACTION_RESULT_FAILURE + " : " + exceptionMessageForHtml;
    }

    private boolean isMasqueradeModeRequested(AccountAttributes loggedInUser, String requestedUserId) {
        return loggedInUser != null && requestedUserId != null
               && !"null".equals(requestedUserId.trim())
               && loggedInUser.googleId != null
               && !loggedInUser.googleId.equals(requestedUserId);
    }

    // ===================== Utility methods used by some child classes========

    protected void excludeStudentDetailsFromResponseParams() {
        regkey = null;
    }
}
