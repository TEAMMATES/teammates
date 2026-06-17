package teammates.ui.webapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.Provider;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.logic.api.Logic;
import teammates.logic.email.AccountVerificationEmailsLogic;
import teammates.logic.email.EmailQueueService;
import teammates.storage.entity.Account;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.ui.output.ApiOutput;
import teammates.ui.request.BasicRequest;

/**
 * Base test class for testing API Actions.
 *
 * @param <T> the type of Action being tested
 * @param <R> the type of ApiOutput expected from the Action
 */
public abstract class BaseActionTest<T extends Action, R extends ApiOutput> extends BaseTestCaseWithDatabaseAccess {

    // Intentionally made private to encourage subclasses to use `GivenData` and execute() instead.
    private final Logic logic = Logic.inst();
    private final Class<T> actionClass;

    @SuppressWarnings("unchecked")
    protected BaseActionTest() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        this.actionClass = (Class<T>) type.getActualTypeArguments()[0];
    }

    private T getAction(RequestContext testRequest) {
        HttpServletRequest request = getMockRequest(testRequest);

        T action;
        try {
            action = actionClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate action class: " + actionClass.getName(), e);
        }

        AccountVerificationEmailsLogic.inst().init(EmailQueueService.withTaskQueuer(mockTaskQueuer));
        action.setEmailQueueService(EmailQueueService.withTaskQueuer(mockTaskQueuer));
        action.setRecaptchaVerifier(mockRecaptchaVerifier);
        configureAction(action);
        inTransaction(() -> action.init(request));

        return action;
    }

    /**
     * Override to perform additional setup on the action before it is initialized.
     * Used by tests that need to inject dependencies not held by the base {@link Action} class.
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void configureAction(T action) {
        // no-op by default
    }

    /**
     * Executes the action with the given test request and returns the output.
     */
    @SuppressWarnings("unchecked")
    protected R execute(RequestContext testRequest) {
        T action = getAction(testRequest);
        return inTransaction(() -> {
            action.checkAccessControl();
            JsonResult result = action.execute();
            if (result.getStatusCode() < 200 || result.getStatusCode() >= 300) {
                throw new AssertionError("Action execution failed with status code: " + result.getStatusCode());
            }
            return (R) result.getOutput();
        });
    }

    /**
     * Asserts that executing the action with the given test request throws an
     * exception of the expected type.
     */
    protected <E extends Throwable> E assertActionThrows(Class<E> expectedType, RequestContext testRequest) {
        T action = getAction(testRequest);
        return assertThrowsInTransaction(expectedType, () -> {
            action.checkAccessControl();
            action.execute();
        });
    }

    /**
     * Generates an authentication cookie for the given account ID.
     */
    protected Cookie getAuthCookie(UUID accountId) {
        UserInfoCookie uic = new UserInfoCookie(accountId);
        String cookieValue = StringHelper.encrypt(JsonUtils.toCompactJson(uic));
        return new Cookie(Const.SecurityConfig.AUTH_COOKIE_NAME, cookieValue);
    }

    private HttpServletRequest getMockRequest(RequestContext testRequest) {
        HttpServletRequest request = mock(HttpServletRequest.class);

        try {
            Map<String, String[]> parameterMap = testRequest.params.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toArray(String[]::new)));

            when(request.getParameterMap()).thenReturn(parameterMap);
            testRequest.params.forEach((key, values) -> {
                when(request.getParameter(key)).thenReturn(values.isEmpty() ? null : values.get(0));
                when(request.getParameterValues(key)).thenReturn(values.toArray(String[]::new));
            });

            testRequest.headers.forEach((key, value) -> when(request.getHeader(key)).thenReturn(value));
            when(request.getCookies()).thenReturn(testRequest.cookies.toArray(Cookie[]::new));
            when(request.getContextPath()).thenReturn(testRequest.contextPath);
            when(request.getRequestURI()).thenReturn(testRequest.uri);

            String body = testRequest.request == null ? "" : JsonUtils.toJson(testRequest.request);
            when(request.getReader()).thenReturn(new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)),
                            StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up mock request", e);
        }
        return request;
    }

    /**
     * Helper class to build test request contexts for API actions.
     */
    protected class RequestContext {
        Map<String, List<String>> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        List<Cookie> cookies = new ArrayList<>();
        BasicRequest request;
        String contextPath = "";
        String uri = "";

        public RequestContext withParam(String key, String value) {
            this.params.computeIfAbsent(key, unused -> new ArrayList<>()).add(value);
            return this;
        }

        public RequestContext withHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public RequestContext withCookie(Cookie cookie) {
            this.cookies.add(cookie);
            return this;
        }

        public RequestContext withRequest(BasicRequest request) {
            this.request = request;
            return this;
        }

        public RequestContext withRegKey(String regKey) {
            return withParam(Const.ParamsNames.REGKEY, regKey);
        }

        public RequestContext withAccountAuth(UUID accountId) {
            return withCookie(getAuthCookie(accountId));
        }

        public RequestContext withAdminAuth() {
            String adminEmail = Config.APP_ADMINS.get(0);
            Account admin = inTransaction(() ->
                    logic.createOrGetAccount(Provider.TEAMMATES_DEV, adminEmail, Account.NO_TENANT, adminEmail));
            return withCookie(getAuthCookie(admin.getId()));
        }

        public RequestContext withWorkerAuth() {
            this.uri = Const.TaskQueue.URI_PREFIX + "/";
            return withHeader(Const.HeaderNames.AUTHORIZATION_KEY, "Bearer " + Config.CRON_AND_WORKER_SECRET);
        }
    }
}
