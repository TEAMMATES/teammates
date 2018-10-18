package teammates.ui.newcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import teammates.common.exception.ActionMappingException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Logger;

/**
 * Generates the matching {@link Action} for a given URI and request method.
 */
public class ActionFactory {

    private static final Logger log = Logger.getLogger();

    private static final Map<String, Map<String, Class<? extends Action>>> ACTION_MAPPINGS = new HashMap<>();

    static {
        map("/auth", "GET", AuthInfoAction.class);
    }

    private static void map(String actionUri, String method, Class<? extends Action> actionClass) {
        ACTION_MAPPINGS.computeIfAbsent("/webapi" + actionUri, k -> new HashMap<>()).put(method, actionClass);
    }

    /**
     * Returns the matching {@link Action} object for the URI and method in {@code req}.
     */
    public Action getAction(HttpServletRequest req) throws ActionMappingException {
        String url = req.getRequestURL().toString();
        String method = req.getMethod();
        log.info("URL received: [" + method + "] " + url);

        String uri = req.getRequestURI();
        if (uri.contains(";")) {
            uri = uri.split(";")[0];
        }
        Action action = getAction(uri, method);
        action.init(req);
        return action;
    }

    private Action getAction(String uri, String method) throws ActionMappingException {
        if (!ACTION_MAPPINGS.containsKey(uri)) {
            throw new ActionMappingException("Resource with URI " + uri + " is not found.", 404);
        }

        Class<? extends Action> controllerClass =
                ACTION_MAPPINGS.getOrDefault(uri, new HashMap<>()).get(method);

        if (controllerClass == null) {
            throw new ActionMappingException("Method [" + method + "] is not allowed for URI " + uri + ".", 405);
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
