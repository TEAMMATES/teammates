package teammates.ui.newcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import teammates.common.exception.ActionMappingException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ResourceURIs;

/**
 * Generates the matching {@link Action} for a given URI and request method.
 */
public class ActionFactory {

    private static final String GET = HttpGet.METHOD_NAME;
    private static final String POST = HttpPost.METHOD_NAME;
    private static final String PUT = HttpPut.METHOD_NAME;
    private static final String DELETE = HttpDelete.METHOD_NAME;

    private static final Map<String, Map<String, Class<? extends Action>>> ACTION_MAPPINGS = new HashMap<>();

    static {
        map(ResourceURIs.EXCEPTION, GET, AdminExceptionTestAction.class);
        map(ResourceURIs.ERROR_REPORT, POST, SendErrorReportAction.class);
        map(ResourceURIs.TIMEZONE, GET, GetTimeZonesAction.class);
        map(ResourceURIs.AUTH, GET, GetAuthInfoAction.class);
        map(ResourceURIs.ACCOUNTS_SEARCH, GET, SearchAccountsAction.class);
        map(ResourceURIs.ACCOUNTS, GET, GetAccountAction.class);
        map(ResourceURIs.ACCOUNTS, POST, CreateAccountAction.class);
        map(ResourceURIs.ACCOUNTS, DELETE, DeleteAccountAction.class);
        map(ResourceURIs.ACCOUNTS_DOWNGRADE, PUT, DowngradeAccountAction.class);
        map(ResourceURIs.ACCOUNTS_RESET, PUT, ResetAccountAction.class);
        map(ResourceURIs.INSTRUCTORS, DELETE, DeleteInstructorAction.class);
        map(ResourceURIs.STUDENTS, DELETE, DeleteStudentAction.class);
        map(ResourceURIs.SESSIONS_ADMIN, GET, GetOngoingSessionsAction.class);
        map(ResourceURIs.SESSIONS_STATS, GET, GetSessionResponseStatsAction.class);
        map(ResourceURIs.JOIN, GET, GetCourseJoinStatusAction.class);
        map(ResourceURIs.JOIN, PUT, JoinCourseAction.class);
        map(ResourceURIs.COURSE_STUDENT_DETAILS, GET, GetCourseStudentDetailsAction.class);
        map(ResourceURIs.STUDENT_PROFILE_PICTURE, GET, GetStudentProfilePictureAction.class);
    }

    private static void map(String uri, String method, Class<? extends Action> actionClass) {
        ACTION_MAPPINGS.computeIfAbsent(ResourceURIs.URI_PREFIX + uri, k -> new HashMap<>()).put(method, actionClass);
    }

    /**
     * Returns the matching {@link Action} object for the URI and method in {@code req}.
     */
    public Action getAction(HttpServletRequest req, String method, HttpServletResponse resp) throws ActionMappingException {
        String uri = req.getRequestURI();
        if (uri.contains(";")) {
            uri = uri.split(";")[0];
        }
        Action action = getAction(uri, method);
        action.init(req, resp);
        return action;
    }

    private Action getAction(String uri, String method) throws ActionMappingException {
        if (!ACTION_MAPPINGS.containsKey(uri)) {
            throw new ActionMappingException("Resource with URI " + uri + " is not found.", HttpStatus.SC_NOT_FOUND);
        }

        Class<? extends Action> controllerClass =
                ACTION_MAPPINGS.getOrDefault(uri, new HashMap<>()).get(method);

        if (controllerClass == null) {
            throw new ActionMappingException("Method [" + method + "] is not allowed for URI " + uri + ".",
                    HttpStatus.SC_METHOD_NOT_ALLOWED);
        }

        try {
            return controllerClass.newInstance();
        } catch (Exception e) {
            Assumption.fail("Could not create the action for " + uri + ": "
                    + TeammatesException.toStringWithStackTrace(e));
            return null;
        }
    }

}
