package teammates.ui.newcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
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

    private static final Map<String, Map<String, Class<? extends Action>>> ACTION_MAPPINGS = new HashMap<>();

    static {
        map(ResourceURIs.EXCEPTION, HttpGet.METHOD_NAME, AdminExceptionTestAction.class);
        map(ResourceURIs.AUTH, HttpGet.METHOD_NAME, GetAuthInfoAction.class);
        map(ResourceURIs.ACCOUNTS, HttpGet.METHOD_NAME, SearchAccountsAction.class);
        map(ResourceURIs.ACCOUNTS, HttpPost.METHOD_NAME, CreateAccountAction.class);
        map(ResourceURIs.JOIN, HttpGet.METHOD_NAME, GetCourseJoinStatusAction.class);
        map(ResourceURIs.JOIN, HttpPut.METHOD_NAME, JoinCourseAction.class);
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
