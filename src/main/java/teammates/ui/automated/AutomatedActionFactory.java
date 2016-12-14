package teammates.ui.automated;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import teammates.common.exception.PageNotFoundException;
import teammates.common.exception.TeammatesException;

/**
 * Generates the matching {@link AutomatedAction} for a given URI.
 */
public class AutomatedActionFactory {
    
    private static Map<String, Class<? extends AutomatedAction>> actionMappings =
            new HashMap<String, Class<? extends AutomatedAction>>();
    
    static {
        map("/auto/adminEmailPrepareTaskQueueWorker", null);
        map("/auto/adminEmailWorker", null);
        map("/auto/compileLogs", null);
        map("/auto/courseJoinRemindEmailWorker", null);
        map("/auto/emailWorker", null);
        map("/auto/feedbackRemindEmailParticularUsersWorker", null);
        map("/auto/feedbackRemindEmailWorker", null);
        map("/auto/feedbackSessionOpeningReminders", null);
        map("/auto/feedbackSessionClosedReminders", null);
        map("/auto/feedbackSessionClosingReminders", null);
        map("/auto/feedbackSessionPublishedReminders", null);
        map("/auto/feedbackSubmissionAdjustmentWorker", null);
        map("/auto/sendEmailWorker", null);
    }
    
    private static void map(String actionUri, Class<? extends AutomatedAction> actionClass) {
        actionMappings.put(actionUri, actionClass);
    }
    
    /**
     * @return the matching {@link AutomatedAction} object for the URI in the {@code req}.
     */
    public AutomatedAction getAction(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (uri.contains(";")) {
            uri = uri.split(";")[0];
        }
        
        return getAction(uri);
    }
    
    private AutomatedAction getAction(String uri) {
        Class<? extends AutomatedAction> action = actionMappings.get(uri);
        
        if (action == null) {
            throw new PageNotFoundException("Page not found for " + uri);
        }
        
        try {
            return action.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create the action for " + uri + ": "
                                       + TeammatesException.toStringWithStackTrace(e));
        }
    }
    
}
