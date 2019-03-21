package teammates.ui.controller;

// CHECKSTYLE.OFF:AvoidStarImport as there would be many (>100) import lines added if we were to import all of the ActionURIs
import static teammates.common.util.Const.ActionURIs.*;
// CHECKSTYLE.ON:AvoidStarImport

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import teammates.common.exception.ActionMappingException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Logger;

/**
 * Is used to generate the matching {@link Action} for a given URI.
 */
public class ActionFactory {
    private static final Logger log = Logger.getLogger();

    private static Map<String, Class<? extends Action>> actionMappings = new HashMap<>();

    static {
        map(INSTRUCTOR_FEEDBACK_RESULTS_PAGE, InstructorFeedbackResultsPageAction.class);
        map(INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD, InstructorFeedbackResultsDownloadAction.class);
        map(INSTRUCTOR_STUDENT_RECORDS_AJAX_PAGE, InstructorStudentRecordsAjaxPageAction.class);
    }

    /**
     * Returns the matching {@link Action} object for the URI in the {@code req}.
     */
    public Action getAction(HttpServletRequest req) throws ActionMappingException {

        String url = req.getRequestURL().toString();
        log.info("URL received : [" + req.getMethod() + "] " + url);

        String uri = req.getRequestURI();
        if (uri.contains(";")) {
            uri = uri.split(";")[0];
        }
        Action c = getAction(uri);
        c.init(req);
        return c;

    }

    private static Action getAction(String uri) throws ActionMappingException {
        Class<? extends Action> controllerClass = actionMappings.get(uri);

        if (controllerClass == null) {
            throw new ActionMappingException(uri, 404);
        }

        try {
            return controllerClass.newInstance();
        } catch (Exception e) {
            Assumption.fail("Could not create the action for " + uri + ": "
                            + TeammatesException.toStringWithStackTrace(e));
            return null;

        }

    }

    private static void map(String actionUri, Class<? extends Action> actionClass) {
        actionMappings.put(actionUri, actionClass);
    }

}
