package teammates.ui.webapi;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.RequestContext;
import teammates.common.datatransfer.logs.RequestLogUser;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.JsonUtils;
import teammates.logic.api.Logic;
import teammates.logic.api.RecaptchaVerifier;
import teammates.logic.api.UserProvision;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.BasicRequest;

/**
 * An "action" to be performed by the system.
 * If the requesting user is allowed to perform the requested action,
 * this object can talk to the back end to perform that action.
 */
public abstract class Action {

    Logic logic = Logic.inst();
    UserProvision userProvision = UserProvision.inst();
    GateKeeper gateKeeper = GateKeeper.inst();
    RecaptchaVerifier recaptchaVerifier = RecaptchaVerifier.inst();

    HttpServletRequest req;
    RequestContext requestContext;

    // buffer to store the request body
    private String requestBody;

    /**
     * Initializes the action object based on the HTTP request.
     */
    public void init(HttpServletRequest req) throws UnauthorizedAccessException {
        this.req = req;
        AuthContext authContext = userProvision.getAuthContextFromRequest(req);
        this.requestContext = new RequestContext(authContext);
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

    public void setRecaptchaVerifier(RecaptchaVerifier recaptchaVerifier) {
        this.recaptchaVerifier = recaptchaVerifier;
    }

    /**
     * Checks if the requesting user has sufficient authority to access the resource.
     */
    public void checkAccessControl() throws InvalidHttpRequestBodyException, UnauthorizedAccessException {
        if (requestContext.getAuthType().getLevel() < getMinAuthLevel().getLevel()) {
            // Access control level lower than required
            throw new UnauthorizedAccessException("Not authorized to access this resource.");
        }

        if (requestContext.getAuthType() == AuthType.ALL_ACCESS) {
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
        User regKeyUser = requestContext.getRegKeyUser();

        if (account != null) {
            user.setEmail(account.getEmail());
        } else if (regKeyUser != null) {
            user.setEmail(regKeyUser.getEmail());
        }

        return user;
    }

    Account getCurrentAccount() {
        return requestContext.getAccount();
    }

    UUID getCurrentUserAccountId() {
        Account account = getCurrentAccount();
        return account == null ? null : account.getId();
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
     * Returns the first value or null for the specified parameter expected to be present in the HTTP request as boolean.
     */
    Optional<Boolean> getNullableBooleanRequestParamValue(String paramName) {
        String value = getRequestParamValue(paramName);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(getBooleanRequestParamValue(paramName));
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
     * Returns the first value for the specified parameter expected to be present in the HTTP request as an enum.
     */
    <T extends Enum<T>> T getEnumRequestParamValue(String paramName, Class<T> enumType) {
        String value = getNonNullRequestParamValue(paramName);
        return getEnumFromParam(paramName, value, enumType);
    }

    /**
     * Returns all values for the specified parameter expected to be present in the HTTP request as enums.
     */
    <T extends Enum<T>> List<T> getEnumRequestParamValues(String paramName, Class<T> enumType) {
        return Arrays.stream(getNonNullRequestParamValues(paramName))
                .map(value -> getEnumFromParam(paramName, value, enumType))
                .toList();
    }

    /**
     * Returns the first value or null for the specified parameter expected to be present in the HTTP request as an enum.
     */
    <T extends Enum<T>> T getNullableEnumRequestParamValue(String paramName, Class<T> enumType) {
        String value = getRequestParamValue(paramName);
        if (value == null) {
            return null;
        }
        return getEnumFromParam(paramName, value, enumType);
    }

    /**
     * Returns the value of the {@code limit} parameter as a positive integer, or null if not present.
     */
    Integer getLimitParamValue() {
        String value = getRequestParamValue(Const.ParamsNames.LIMIT);
        if (value == null) {
            return null;
        }
        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(
                    "Expected integer value for " + Const.ParamsNames.LIMIT + " parameter, but found: [" + value + "]",
                    e);
        }
        if (parsed <= 0) {
            throw new InvalidHttpParameterException(
                    "Expected positive integer value for " + Const.ParamsNames.LIMIT
                            + " parameter, but found: [" + value + "]");
        }
        return parsed;
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
     * Converts a string to a UUID.
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
     * Converts a string to an enum value.
     */
    private <T extends Enum<T>> T getEnumFromParam(String paramName, String value, Class<T> enumType) {
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(
                    "Invalid value for " + paramName + " parameter: [" + value + "]", e);
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
        return requestContext.getInstructorForCourse(courseId, logic::getInstructorFromAuthContext);
    }

    Student getStudentFromRequest(String courseId) {
        return requestContext.getStudentForCourse(courseId, logic::getStudentFromAuthContext);
    }

    /**
     * Gets the user information from the request context.
     *
     * <p>If the user is both an instructor and a student in the course,
     * the instructor information will be returned.
     */
    User getUserFromRequest(String courseId) {
        User regKeyUser = requestContext.getRegKeyUser();
        if (regKeyUser != null) {
            return regKeyUser;
        }
        Instructor instructor = getInstructorFromRequest(courseId);
        if (instructor != null) {
            return instructor;
        }
        return getStudentFromRequest(courseId);
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
    public abstract JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException;

}
