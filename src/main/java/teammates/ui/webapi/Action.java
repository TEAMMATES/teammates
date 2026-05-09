package teammates.ui.webapi;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.datatransfer.logs.RequestLogUser;
import teammates.common.util.AutomatedRequestAuth;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.logic.api.AuthProxy;
import teammates.logic.api.EmailGenerator;
import teammates.logic.api.EmailSender;
import teammates.logic.api.Logic;
import teammates.logic.api.LogsProcessor;
import teammates.logic.api.RecaptchaVerifier;
import teammates.logic.api.TaskQueuer;
import teammates.logic.api.UserProvision;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.BasicRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * An "action" to be performed by the system.
 * If the requesting user is allowed to perform the requested action,
 * this object can talk to the back end to perform that action.
 */
public abstract class Action {

    Logic logic = Logic.inst();
    UserProvision userProvision = UserProvision.inst();
    GateKeeper gateKeeper = GateKeeper.inst();
    EmailGenerator emailGenerator = EmailGenerator.inst();
    TaskQueuer taskQueuer = TaskQueuer.inst();
    EmailSender emailSender = EmailSender.inst();
    RecaptchaVerifier recaptchaVerifier = RecaptchaVerifier.inst();
    LogsProcessor logsProcessor = LogsProcessor.inst();
    AuthProxy authProxy = AuthProxy.inst();

    HttpServletRequest req;
    UserInfo userInfo;
    AuthType authType;

    private Student unregisteredStudent;
    private Instructor unregisteredInstructor;

    // buffer to store the request body
    private String requestBody;

    /**
     * Initializes the action object based on the HTTP request.
     */
    public void init(HttpServletRequest req) {
        this.req = req;
        initAuthInfo();
    }

    /**
     * Inject logic class for use in tests.
     */
    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public void setUserProvision(UserProvision userProvision) {
        this.userProvision = userProvision;
    }

    public void setTaskQueuer(TaskQueuer taskQueuer) {
        this.taskQueuer = taskQueuer;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void setRecaptchaVerifier(RecaptchaVerifier recaptchaVerifier) {
        this.recaptchaVerifier = recaptchaVerifier;
    }

    public void setLogsProcessor(LogsProcessor logsProcessor) {
        this.logsProcessor = logsProcessor;
    }

    public void setAuthProxy(AuthProxy authProxy) {
        this.authProxy = authProxy;
    }

    public void setEmailGenerator(EmailGenerator emailGenerator) {
        this.emailGenerator = emailGenerator;
    }

    /**
     * Checks if the requesting user has sufficient authority to access the resource.
     */
    public void checkAccessControl() throws UnauthorizedAccessException {
        String userParam = getRequestParamValue(Const.ParamsNames.USER_ID);
        if (userInfo != null && userParam != null && !userInfo.isAdmin && !userParam.equals(userInfo.id)) {
            throw new UnauthorizedAccessException("User " + userInfo.id
                    + " is trying to masquerade as " + userParam + " without admin permission.");
        }

        if (authType.getLevel() < getMinAuthLevel().getLevel()) {
            // Access control level lower than required
            throw new UnauthorizedAccessException("Not authorized to access this resource.");
        }

        if (authType == AuthType.ALL_ACCESS) {
            // All-access pass granted
            return;
        }

        // All other cases: to be dealt in case-by-case basis
        checkSpecificAccessControl();
    }

    /**
     * Gets the user information of the current user.
     */
    public RequestLogUser getUserInfoForLogging() {
        RequestLogUser user = new RequestLogUser();

        String googleId = userInfo == null ? null : userInfo.getId();

        user.setGoogleId(googleId);
        if (unregisteredStudent == null && unregisteredInstructor == null) {
            user.setRegkey(getRequestParamValue(Const.ParamsNames.REGKEY));
        } else if (unregisteredStudent != null) {
            user.setRegkey(unregisteredStudent.getRegKey());
            user.setEmail(unregisteredStudent.getEmail());
        } else {
            user.setRegkey(unregisteredInstructor.getRegKey());
            user.setEmail(unregisteredInstructor.getEmail());
        }
        return user;
    }

    private void initAuthInfo() {
        if (Config.BACKDOOR_KEY.equals(req.getHeader(Const.HeaderNames.BACKDOOR_KEY))) {
            authType = AuthType.ALL_ACCESS;
            userInfo = userProvision.getAdminOnlyUser(getRequestParamValue(Const.ParamsNames.USER_ID));
            userInfo.isStudent = true;
            userInfo.isInstructor = true;
            return;
        }

        boolean trustedAutomatedCronOrWorker = AutomatedRequestAuth.isTrustedCronOrWorkerRequest(req);
        if (trustedAutomatedCronOrWorker) {
            userInfo = userProvision.getAutomatedServiceUser(
                    AutomatedRequestAuth.isCronRequestPath(req)
                            ? Const.AutomatedService.CRON_SERVICE_USER_ID
                            : Const.AutomatedService.WORKER_SERVICE_USER_ID);
        } else {
            String cookie = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.AUTH_COOKIE_NAME);
            UserInfoCookie uic = UserInfoCookie.fromCookie(cookie);
            userInfo = userProvision.getCurrentUser(uic);
        }

        String regKey = getRequestParamValue(Const.ParamsNames.REGKEY);
        String userParam = getRequestParamValue(Const.ParamsNames.USER_ID);

        if (userInfo == null) {
            authType = StringHelper.isEmpty(regKey) ? AuthType.PUBLIC : AuthType.REG_KEY;
        } else if (userParam != null && userInfo.isAdmin) {
            userInfo = userProvision.getMasqueradeUser(userParam);
            authType = AuthType.MASQUERADE;
        } else {
            authType = AuthType.LOGGED_IN;
        }
    }

    /**
     * Returns the first value for the specified parameter in the HTTP request, or null if such parameter is not found.
     */
    String getRequestParamValue(String paramName) {
        return req.getParameter(paramName);
    }

    /**
     * Returns the first value for the specified parameter expected to be present in the HTTP request.
     */
    String getNonNullRequestParamValue(String paramName) {
        String value = req.getParameter(paramName);
        if (value == null) {
            throw new InvalidHttpParameterException(String.format("The [%s] HTTP parameter is null.", paramName));
        }
        return value;
    }

    String[] getNonNullRequestParamValues(String paramName) {
        String[] values = req.getParameterValues(paramName);
        if (values == null || values.length == 0) {
            throw new InvalidHttpParameterException(String.format("The [%s] HTTP parameter is null.", paramName));
        }
        return values;
    }

    /**
     * Returns the first value for the specified parameter expected to be present in the HTTP request as boolean.
     */
    boolean getBooleanRequestParamValue(String paramName) {
        String value = getNonNullRequestParamValue(paramName);
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        } else {
            throw new InvalidHttpParameterException(
                    "Expected boolean value for " + paramName + " parameter, but found: [" + value + "]");
        }
    }

    /**
     * Returns the first value for the specified parameter expected to be present in the HTTP request as long.
     */
    long getLongRequestParamValue(String paramName) {
        String value = getNonNullRequestParamValue(paramName);
        try {
            return Long.parseLong(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(
                    "Expected long value for " + paramName + " parameter, but found: [" + value + "]", e);
        }
    }

    /**
     * Returns the first value for the specified parameter expected to be present in the HTTP request as UUID.
     */
    UUID getUuidRequestParamValue(String paramName) {
        String value = getNonNullRequestParamValue(paramName);
        return getUuidFromParam(paramName, value);
    }

    /**
     * Returns the first value or null for the specified parameter expected to be present in the HTTP request as UUID.
     */
    UUID getNullableUuidRequestParamValue(String paramName) {
        String value = getRequestParamValue(paramName);
        if (value == null) {
            return null;
        }
        return getUuidFromParam(paramName, value);
    }

    /**
     * Converts a uuid to a string.
     */
    private UUID getUuidFromParam(String paramName, String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(
                    "Expected UUID value for " + paramName + " parameter, but found: [" + uuid + "]", e);
        }
    }

    /**
     * Returns the request body payload.
     */
    public String getRequestBody() {
        if (!hasDefinedRequestBody()) {
            requestBody = HttpRequestHelper.getRequestBody(req);
        }
        return requestBody;
    }

    /**
     * Returns true if the action has a request body already defined in it.
     */
    public boolean hasDefinedRequestBody() {
        return requestBody != null;
    }

    FeedbackSession getNonNullFeedbackSession(String feedbackSessionName, String courseId) {
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }
        return feedbackSession;
    }

    /**
     * Deserializes and validates the request body payload.
     */
    <T extends BasicRequest> T getAndValidateRequestBody(Type typeOfBody) throws InvalidHttpRequestBodyException {
        T requestBody = JsonUtils.fromJson(getRequestBody(), typeOfBody);
        if (requestBody == null) {
            throw new InvalidHttpRequestBodyException("The request body is null");
        }
        requestBody.validate();
        return requestBody;
    }

    /**
     * Gets the unregistered student by the HTTP param.
     */
    Optional<Student> getUnregisteredStudent() {
        String key = getRequestParamValue(Const.ParamsNames.REGKEY);
        if (!StringHelper.isEmpty(key)) {
            Student student = logic.getStudentByRegistrationKey(key);
            if (student == null) {
                return Optional.empty();
            }
            unregisteredStudent = student;
            return Optional.of(student);
        }
        return Optional.empty();
    }

    /**
     * Gets the unregistered instructor by the HTTP param.
     */
    Optional<Instructor> getUnregisteredInstructor() {
        String key = getRequestParamValue(Const.ParamsNames.REGKEY);
        if (!StringHelper.isEmpty(key)) {
            Instructor instructor = logic.getInstructorByRegistrationKey(key);
            if (instructor == null) {
                return Optional.empty();
            }
            unregisteredInstructor = instructor;
            return Optional.of(instructor);
        }
        return Optional.empty();
    }

    Instructor getPossiblyUnregisteredInstructor(String courseId) {
        return getUnregisteredInstructor().orElseGet(() -> {
            if (userInfo == null) {
                return null;
            }
            return logic.getInstructorByGoogleId(courseId, userInfo.getId());
        });
    }

    Student getPossiblyUnregisteredStudent(String courseId) {
        return getUnregisteredStudent().orElseGet(() -> {
            if (userInfo == null) {
                return null;
            }
            return logic.getStudentByGoogleId(courseId, userInfo.getId());
        });
    }

    InstructorPermissionSet constructInstructorPrivileges(Instructor instructor, String feedbackSessionName) {
        InstructorPermissionSet privilege = instructor.getPrivileges().getCourseLevelPrivileges();
        if (feedbackSessionName != null) {
            privilege.setCanSubmitSessionInSections(
                    instructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)
                            || instructor.isAllowedForPrivilegeAnySection(
                            feedbackSessionName, Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
            privilege.setCanViewSessionInSections(
                    instructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS)
                            || instructor.isAllowedForPrivilegeAnySection(
                            feedbackSessionName, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
            privilege.setCanModifySessionCommentsInSections(
                    instructor.isAllowedForPrivilege(
                            Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                            || instructor.isAllowedForPrivilegeAnySection(feedbackSessionName,
                            Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        }
        return privilege;
    }

    /**
     * Gets the minimum access control level required to access the resource.
     */
    abstract AuthType getMinAuthLevel();

    /**
     * Checks the specific access control needs for the resource.
     */
    abstract void checkSpecificAccessControl() throws UnauthorizedAccessException;

    /**
     * Executes the action.
     */
    public abstract ActionResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException;

}
