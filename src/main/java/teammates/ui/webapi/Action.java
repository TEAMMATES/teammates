package teammates.ui.webapi;

import java.lang.reflect.Type;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.logs.RequestLogUser;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.JsonUtils;
import teammates.logic.api.EmailGenerator;
import teammates.logic.api.EmailSender;
import teammates.logic.api.Logic;
import teammates.logic.api.LogsProcessor;
import teammates.logic.api.RecaptchaVerifier;
import teammates.logic.api.TaskQueuer;
import teammates.logic.api.UserProvision;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
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

    HttpServletRequest req;
    AuthContext authContext;

    // buffer to store the request body
    private String requestBody;

    /**
     * Initializes the action object based on the HTTP request.
     */
    public void init(HttpServletRequest req) throws UnauthorizedAccessException {
        this.req = req;
        this.authContext = userProvision.getAuthContextFromRequest(req);
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

    public void setEmailGenerator(EmailGenerator emailGenerator) {
        this.emailGenerator = emailGenerator;
    }

    /**
     * Checks if the requesting user has sufficient authority to access the resource.
     */
    public void checkAccessControl() throws InvalidHttpRequestBodyException, UnauthorizedAccessException {
        if (authContext.authType().getLevel() < getMinAuthLevel().getLevel()) {
            // Access control level lower than required
            throw new UnauthorizedAccessException("Not authorized to access this resource.");
        }

        if (authContext.authType() == AuthType.ALL_ACCESS) {
            // All-access auth type is allowed to access all resources without further checks
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

        Account account = getCurrentAccount();
        User regKeyUser = authContext.regKeyUser();

        if (account != null) {
            user.setEmail(account.getEmail());
            user.setGoogleId(account.getGoogleId());
        } else if (regKeyUser != null) {
            user.setEmail(regKeyUser.getEmail());
        }

        return user;
    }

    Account getCurrentAccount() {
        return authContext.account();
    }

    String getCurrentUserGoogleId() {
        Account account = getCurrentAccount();
        return account == null ? null : account.getGoogleId();
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

    /**
     * Returns all values for the specified parameter expected to be present in the HTTP request.
     */
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
        T reqBody = JsonUtils.fromJson(getRequestBody(), typeOfBody);
        if (reqBody == null) {
            throw new InvalidHttpRequestBodyException("The request body is null");
        }
        reqBody.validate();
        return reqBody;
    }

    Instructor getInstructorFromRequest(String courseId) {
        return logic.getInstructorFromAuthContext(authContext, courseId);
    }

    Student getStudentFromRequest(String courseId) {
        return logic.getStudentFromAuthContext(authContext, courseId);
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
    abstract void checkSpecificAccessControl() throws InvalidHttpRequestBodyException, UnauthorizedAccessException;

    /**
     * Executes the action.
     */
    public abstract ActionResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException;

}
