package teammates.ui.automated;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.ActionMappingException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ActionURIs;
import teammates.common.util.Const.TaskQueue;

/**
 * Generates the matching {@link AutomatedAction} for a given URI.
 */
public class AutomatedActionFactory {

    private static final Map<String, Class<? extends AutomatedAction>> ACTION_MAPPINGS = new HashMap<>();

    static {
        // Cron jobs
        map(ActionURIs.AUTOMATED_LOG_COMPILATION, CompileLogsAction.class);
        map(ActionURIs.AUTOMATED_DATASTORE_BACKUP, DatastoreBackupAction.class);
        map(ActionURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS, FeedbackSessionOpeningRemindersAction.class);
        map(ActionURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS, FeedbackSessionClosedRemindersAction.class);
        map(ActionURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS, FeedbackSessionClosingRemindersAction.class);
        map(ActionURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS, FeedbackSessionPublishedRemindersAction.class);

        // Task queue workers
        map(TaskQueue.ADMIN_PREPARE_EMAIL_ADDRESS_MODE_WORKER_URL, AdminPrepareEmailAddressModeWorkerAction.class);
        map(TaskQueue.ADMIN_PREPARE_EMAIL_GROUP_MODE_WORKER_URL, AdminPrepareEmailGroupModeWorkerAction.class);
        map(TaskQueue.ADMIN_SEND_EMAIL_WORKER_URL, AdminSendEmailWorkerAction.class);
        map(TaskQueue.FEEDBACK_RESPONSE_ADJUSTMENT_WORKER_URL, FeedbackResponseAdjustmentWorkerAction.class);
        map(TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_WORKER_URL, FeedbackSessionPublishedEmailWorkerAction.class);
        map(TaskQueue.FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_WORKER_URL,
                FeedbackSessionResendPublishedEmailWorkerAction.class);
        map(TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_WORKER_URL, FeedbackSessionRemindEmailWorkerAction.class);
        map(TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_URL,
                FeedbackSessionRemindParticularUsersEmailWorkerAction.class);
        map(TaskQueue.FEEDBACK_SESSION_UNPUBLISHED_EMAIL_WORKER_URL, FeedbackSessionUnpublishedEmailWorkerAction.class);
        map(TaskQueue.FEEDBACK_SESSION_UPDATE_RESPONDENT_WORKER_URL, FeedbackSessionUpdateRespondentWorkerAction.class);
        map(TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL, InstructorCourseJoinEmailWorkerAction.class);
        map(TaskQueue.SEND_EMAIL_WORKER_URL, SendEmailWorkerAction.class);
        map(TaskQueue.STUDENT_COURSE_JOIN_EMAIL_WORKER_URL, StudentCourseJoinEmailWorkerAction.class);
    }

    private static void map(String actionUri, Class<? extends AutomatedAction> actionClass) {
        ACTION_MAPPINGS.put(actionUri, actionClass);
    }

    /**
     * Returns the matching {@link AutomatedAction} object for the URI in the {@code req}.
     */
    public AutomatedAction getAction(HttpServletRequest req, HttpServletResponse resp) throws ActionMappingException {
        String uri = req.getRequestURI();
        if (uri.contains(";")) {
            uri = uri.split(";")[0];
        }

        AutomatedAction action = getAction(uri);
        action.initialiseAttributes(req, resp);
        return action;
    }

    private AutomatedAction getAction(String uri) throws ActionMappingException {
        Class<? extends AutomatedAction> action = ACTION_MAPPINGS.get(uri);

        if (action == null) {
            throw new ActionMappingException("Resource with URI " + uri + " is not found.", 404);
        }

        try {
            return action.newInstance();
        } catch (Exception e) {
            Assumption.fail("Could not create the action for " + uri + ": "
                    + TeammatesException.toStringWithStackTrace(e));
            return null;
        }
    }

}
