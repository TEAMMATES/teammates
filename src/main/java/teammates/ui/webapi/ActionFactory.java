package teammates.ui.webapi;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import teammates.common.util.Const.CronJobURIs;
import teammates.common.util.Const.ResourceURIs;
import teammates.common.util.Const.TaskQueue;

/**
 * Generates the matching {@link Action} for a given URI and request method.
 */
public final class ActionFactory {

    static final Map<String, Map<String, Class<? extends Action>>> ACTION_MAPPINGS = new HashMap<>();

    private static final String GET = HttpGet.METHOD_NAME;
    private static final String POST = HttpPost.METHOD_NAME;
    private static final String PUT = HttpPut.METHOD_NAME;
    private static final String DELETE = HttpDelete.METHOD_NAME;

    static {
        map(ResourceURIs.DATABUNDLE, POST, PutDataBundleAction.class);
        // Even though this is a DELETE action, PUT is used as DELETE does not allow usage of response body
        map(ResourceURIs.DATABUNDLE, PUT, DeleteDataBundleAction.class);
        map(ResourceURIs.DATABUNDLE_DOCUMENTS, PUT, PutDataBundleDocumentsAction.class);
        map(ResourceURIs.EXCEPTION, GET, AdminExceptionTestAction.class);
        // Even though this is a GET action, POST is used in order to get extra protection from CSRF
        map(ResourceURIs.USER_COOKIE, POST, GetUserCookieAction.class);

        map(ResourceURIs.ERROR_REPORT, POST, SendErrorReportAction.class);
        map(ResourceURIs.TIMEZONE, GET, GetTimeZonesAction.class);
        map(ResourceURIs.AUTH, GET, GetAuthInfoAction.class);
        map(ResourceURIs.AUTH_REGKEY, GET, GetRegkeyValidityAction.class);
        map(ResourceURIs.ACCOUNT, GET, GetAccountAction.class);
        map(ResourceURIs.ACCOUNT, POST, CreateAccountAction.class);
        map(ResourceURIs.ACCOUNT, DELETE, DeleteAccountAction.class);
        map(ResourceURIs.ACCOUNT_RESET, PUT, ResetAccountAction.class);
        map(ResourceURIs.ACCOUNT_REQUEST, GET, GetAccountRequestAction.class);
        map(ResourceURIs.ACCOUNT_REQUEST, POST, CreateAccountRequestAction.class);
        map(ResourceURIs.ACCOUNT_REQUEST, DELETE, DeleteAccountRequestAction.class);
        map(ResourceURIs.ACCOUNT_REQUEST_RESET, PUT, ResetAccountRequestAction.class);
        map(ResourceURIs.ACCOUNTS, GET, GetAccountsAction.class);
        map(ResourceURIs.COURSE, GET, GetCourseAction.class);
        map(ResourceURIs.COURSE, DELETE, DeleteCourseAction.class);
        map(ResourceURIs.COURSE, POST, CreateCourseAction.class);
        map(ResourceURIs.COURSE, PUT, UpdateCourseAction.class);
        map(ResourceURIs.COURSE_ARCHIVE, PUT, ArchiveCourseAction.class);
        map(ResourceURIs.DEADLINE_EXTENSION, GET, GetDeadlineExtensionAction.class);
        map(ResourceURIs.BIN_COURSE, PUT, BinCourseAction.class);
        map(ResourceURIs.BIN_COURSE, DELETE, RestoreCourseAction.class);
        map(ResourceURIs.COURSES, GET, GetCoursesAction.class);
        map(ResourceURIs.COURSE_SECTIONS, GET, GetCourseSectionNamesAction.class);
        map(ResourceURIs.INSTRUCTORS, GET, GetInstructorsAction.class);
        map(ResourceURIs.INSTRUCTOR, GET, GetInstructorAction.class);
        map(ResourceURIs.INSTRUCTOR, DELETE, DeleteInstructorAction.class);
        map(ResourceURIs.INSTRUCTOR_PRIVILEGE, GET, GetInstructorPrivilegeAction.class);
        map(ResourceURIs.INSTRUCTOR_PRIVILEGE, PUT, UpdateInstructorPrivilegeAction.class);
        map(ResourceURIs.RESPONSE_COMMENT, POST, CreateFeedbackResponseCommentAction.class);
        map(ResourceURIs.RESPONSE_COMMENT, GET, GetFeedbackResponseCommentAction.class);
        map(ResourceURIs.RESPONSE_COMMENT, PUT, UpdateFeedbackResponseCommentAction.class);
        map(ResourceURIs.RESPONSE_COMMENT, DELETE, DeleteFeedbackResponseCommentAction.class);
        map(ResourceURIs.RESULT, GET, GetSessionResultsAction.class);

        //STUDENTS APIs
        map(ResourceURIs.STUDENTS, GET, GetStudentsAction.class);
        map(ResourceURIs.STUDENTS, PUT, EnrollStudentsAction.class);
        map(ResourceURIs.STUDENTS, DELETE, DeleteStudentsAction.class);

        //STUDENT APIs
        map(ResourceURIs.STUDENT, DELETE, DeleteStudentAction.class);
        map(ResourceURIs.STUDENT, GET, GetStudentAction.class);
        map(ResourceURIs.STUDENT, PUT, UpdateStudentAction.class);

        // NOTIFICATION APIs
        map(ResourceURIs.NOTIFICATION, GET, GetNotificationAction.class);
        map(ResourceURIs.NOTIFICATION, POST, CreateNotificationAction.class);
        map(ResourceURIs.NOTIFICATION, PUT, UpdateNotificationAction.class);
        map(ResourceURIs.NOTIFICATION, DELETE, DeleteNotificationAction.class);
        map(ResourceURIs.NOTIFICATION_READ, POST, MarkNotificationAsReadAction.class);
        map(ResourceURIs.NOTIFICATION_READ, GET, GetReadNotificationsAction.class);

        // NOTIFICATIONS APIs
        map(ResourceURIs.NOTIFICATIONS, GET, GetNotificationsAction.class);

        //SEARCH APIs
        map(ResourceURIs.SEARCH_INSTRUCTORS, GET, SearchInstructorsAction.class);
        map(ResourceURIs.SEARCH_STUDENTS, GET, SearchStudentsAction.class);
        map(ResourceURIs.SEARCH_ACCOUNT_REQUESTS, GET, SearchAccountRequestsAction.class);
        map(ResourceURIs.EMAIL, GET, GenerateEmailAction.class);

        map(ResourceURIs.SESSIONS_ONGOING, GET, GetOngoingSessionsAction.class);
        map(ResourceURIs.SESSION_STATS, GET, GetSessionResponseStatsAction.class);
        map(ResourceURIs.SESSION, GET, GetFeedbackSessionAction.class);
        map(ResourceURIs.SESSION, PUT, UpdateFeedbackSessionAction.class);
        map(ResourceURIs.SESSION, POST, CreateFeedbackSessionAction.class);
        map(ResourceURIs.SESSION, DELETE, DeleteFeedbackSessionAction.class);
        map(ResourceURIs.SESSION_PUBLISH, POST, PublishFeedbackSessionAction.class);
        map(ResourceURIs.SESSION_PUBLISH, DELETE, UnpublishFeedbackSessionAction.class);
        map(ResourceURIs.SESSION_SUBMITTED_GIVER_SET, GET, GetFeedbackSessionSubmittedGiverSetAction.class);
        map(ResourceURIs.SESSION_REMIND_SUBMISSION, POST, RemindFeedbackSessionSubmissionAction.class);
        map(ResourceURIs.SESSION_REMIND_RESULT, POST, RemindFeedbackSessionResultAction.class);
        map(ResourceURIs.SESSIONS, GET, GetFeedbackSessionsAction.class);
        map(ResourceURIs.BIN_SESSION, PUT, BinFeedbackSessionAction.class);
        map(ResourceURIs.BIN_SESSION, DELETE, RestoreFeedbackSessionAction.class);
        map(ResourceURIs.INSTRUCTOR_KEY, POST, RegenerateInstructorKeyAction.class);
        map(ResourceURIs.STUDENT_KEY, POST, RegenerateStudentKeyAction.class);
        map(ResourceURIs.QUESTIONS, GET, GetFeedbackQuestionsAction.class);
        map(ResourceURIs.QUESTION, POST, CreateFeedbackQuestionAction.class);
        map(ResourceURIs.QUESTION, PUT, UpdateFeedbackQuestionAction.class);
        map(ResourceURIs.QUESTION, DELETE, DeleteFeedbackQuestionAction.class);
        map(ResourceURIs.QUESTION_RECIPIENTS, GET, GetFeedbackQuestionRecipientsAction.class);
        map(ResourceURIs.RESPONSES, GET, GetFeedbackResponsesAction.class);
        map(ResourceURIs.RESPONSES, PUT, SubmitFeedbackResponsesAction.class);
        map(ResourceURIs.HAS_RESPONSES, GET, GetHasResponsesAction.class);
        map(ResourceURIs.SESSION_LINKS_RECOVERY, POST, SessionLinksRecoveryAction.class);
        map(ResourceURIs.JOIN, GET, GetCourseJoinStatusAction.class);
        map(ResourceURIs.JOIN, PUT, JoinCourseAction.class);
        map(ResourceURIs.JOIN_REMIND, POST, SendJoinReminderEmailAction.class);
        map(ResourceURIs.INSTRUCTOR, PUT, UpdateInstructorAction.class);
        map(ResourceURIs.INSTRUCTOR, POST, CreateInstructorAction.class);

        // Logging and tracking
        map(ResourceURIs.SESSION_LOGS, POST, CreateFeedbackSessionLogAction.class);
        map(ResourceURIs.SESSION_LOGS, GET, GetFeedbackSessionLogsAction.class);
        map(ResourceURIs.LOGS, GET, QueryLogsAction.class);
        map(ResourceURIs.USAGE_STATISTICS, GET, GetUsageStatisticsAction.class);
        map(ResourceURIs.ACTION_CLASS, GET, GetActionClassesAction.class);

        // Cron jobs; use GET request
        // Reference: https://cloud.google.com/appengine/docs/standard/java11/scheduling-jobs-with-cron-yaml

        map(CronJobURIs.AUTOMATED_LOG_COMPILATION, GET, CompileLogsAction.class);
        map(CronJobURIs.AUTOMATED_DATASTORE_BACKUP, GET, DatastoreBackupAction.class);
        map(CronJobURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS, GET, FeedbackSessionOpeningRemindersAction.class);
        map(CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS, GET, FeedbackSessionClosedRemindersAction.class);
        map(CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS, GET, FeedbackSessionClosingRemindersAction.class);
        map(CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS, GET, FeedbackSessionPublishedRemindersAction.class);
        map(CronJobURIs.AUTOMATED_FEEDBACK_OPENING_SOON_REMINDERS, GET,
                FeedbackSessionOpeningSoonRemindersAction.class);
        map(CronJobURIs.AUTOMATED_USAGE_STATISTICS_COLLECTION, GET, CalculateUsageStatisticsAction.class);

        // Task queue workers; use POST request
        // Reference: https://cloud.google.com/tasks/docs/creating-appengine-tasks

        map(TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_WORKER_URL, POST, FeedbackSessionPublishedEmailWorkerAction.class);
        map(TaskQueue.FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_WORKER_URL, POST,
                FeedbackSessionResendPublishedEmailWorkerAction.class);
        map(TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_WORKER_URL, POST, FeedbackSessionRemindEmailWorkerAction.class);
        map(TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_URL, POST,
                FeedbackSessionRemindParticularUsersEmailWorkerAction.class);
        map(TaskQueue.FEEDBACK_SESSION_UNPUBLISHED_EMAIL_WORKER_URL, POST,
                FeedbackSessionUnpublishedEmailWorkerAction.class);
        map(TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL, POST, InstructorCourseJoinEmailWorkerAction.class);
        map(TaskQueue.SEND_EMAIL_WORKER_URL, POST, SendEmailWorkerAction.class);
        map(TaskQueue.STUDENT_COURSE_JOIN_EMAIL_WORKER_URL, POST, StudentCourseJoinEmailWorkerAction.class);
        map(TaskQueue.ACCOUNT_REQUEST_SEARCH_INDEXING_WORKER_URL, POST, AccountRequestSearchIndexingWorkerAction.class);
        map(TaskQueue.INSTRUCTOR_SEARCH_INDEXING_WORKER_URL, POST, InstructorSearchIndexingWorkerAction.class);
        map(TaskQueue.STUDENT_SEARCH_INDEXING_WORKER_URL, POST, StudentSearchIndexingWorkerAction.class);

    }

    private ActionFactory() {
        // prevent initialization
    }

    private static void map(String uri, String method, Class<? extends Action> actionClass) {
        ACTION_MAPPINGS.computeIfAbsent(uri, k -> new HashMap<>()).put(method, actionClass);
    }

    /**
     * Returns the matching {@link Action} object for the URI and method in {@code req}.
     */
    public static Action getAction(HttpServletRequest req, String method) throws ActionMappingException {
        String uri = req.getRequestURI();
        if (uri.contains(";")) {
            uri = uri.split(";")[0];
        }
        return getAction(uri, method);
    }

    private static Action getAction(String uri, String method) throws ActionMappingException {
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
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            assert false : "Could not create the action for " + uri;
            return null;
        }
    }

}
