package teammates.common.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

/**
 * Stores constants that are widely used across classes.
 * this class contains several nested classes, each containing a specific
 * category of constants.
 */
public final class Const {

    // This section holds constants that are defined as constants primarily because they are repeated in many places.

    public static final String USER_NOBODY_TEXT = "-";

    public static final String USER_TEAM_FOR_INSTRUCTOR = "Instructors";

    public static final String DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR = "Instructor";

    public static final String DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT = "Anonymous";

    public static final int SECTION_SIZE_LIMIT = 100;

    public static final String DEFAULT_SECTION = "None";

    public static final String UNKNOWN_INSTITUTION = "Unknown Institution";

    public static final String DEFAULT_TIME_ZONE = "UTC";
    public static final Charset ENCODING = StandardCharsets.UTF_8;

    public static final Duration FEEDBACK_SESSIONS_SEARCH_WINDOW = Duration.ofDays(30);
    public static final Duration LOGS_RETENTION_PERIOD = Duration.ofDays(30);
    public static final Duration COOKIE_VALIDITY_PERIOD = Duration.ofDays(7);

    public static final int SEARCH_QUERY_SIZE_LIMIT = 50;

    // These constants are used as variable values to mean that the variable is in a 'special' state.

    public static final int INT_UNINITIALIZED = -9999;

    public static final int MAX_POSSIBLE_RECIPIENTS = -100;

    public static final int POINTS_EQUAL_SHARE = 100;
    public static final int POINTS_NOT_SURE = -101;
    public static final int POINTS_NOT_SUBMITTED = -999;
    public static final int POINTS_NO_VALUE = Integer.MIN_VALUE;

    public static final String GENERAL_QUESTION = "%GENERAL%";

    public static final Instant TIME_REPRESENTS_FOLLOW_OPENING;
    public static final Instant TIME_REPRESENTS_FOLLOW_VISIBLE;
    public static final Instant TIME_REPRESENTS_LATER;
    public static final Instant TIME_REPRESENTS_NOW;
    public static final Instant TIME_REPRESENTS_DEFAULT_TIMESTAMP;

    static {
        TIME_REPRESENTS_FOLLOW_OPENING = TimeHelper.parseInstant("1970-12-31T00:00:00Z");
        TIME_REPRESENTS_FOLLOW_VISIBLE = TimeHelper.parseInstant("1970-06-22T00:00:00Z");
        TIME_REPRESENTS_LATER = TimeHelper.parseInstant("1970-01-01T00:00:00Z");
        TIME_REPRESENTS_NOW = TimeHelper.parseInstant("1970-02-14T00:00:00Z");
        TIME_REPRESENTS_DEFAULT_TIMESTAMP = TimeHelper.parseInstant("2011-01-01T00:00:00Z");
    }

    public static final String TEST_EMAIL_DOMAIN = "@gmail.tmt";

    // Other Constants

    private Const() {
        // Utility class containing constants
    }

    /**
     * Represents role names for instructors based on their permission settings.
     */
    public static class InstructorPermissionRoleNames {
        public static final String INSTRUCTOR_PERMISSION_ROLE_COOWNER = "Co-owner";
        public static final String INSTRUCTOR_PERMISSION_ROLE_MANAGER = "Manager";
        public static final String INSTRUCTOR_PERMISSION_ROLE_OBSERVER = "Observer";
        public static final String INSTRUCTOR_PERMISSION_ROLE_TUTOR = "Tutor";
        public static final String INSTRUCTOR_PERMISSION_ROLE_CUSTOM = "Custom";
    }

    /**
     * Represents atomic permission for instructors.
     */
    public static class InstructorPermissions {
        public static final String CAN_MODIFY_COURSE = "canmodifycourse";
        public static final String CAN_MODIFY_INSTRUCTOR = "canmodifyinstructor";
        public static final String CAN_MODIFY_SESSION = "canmodifysession";
        public static final String CAN_MODIFY_STUDENT = "canmodifystudent";
        public static final String CAN_VIEW_STUDENT_IN_SECTIONS = "canviewstudentinsection";
        public static final String CAN_VIEW_SESSION_IN_SECTIONS = "canviewsessioninsection";
        public static final String CAN_SUBMIT_SESSION_IN_SECTIONS = "cansubmitsessioninsection";
        public static final String CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS = "canmodifysessioncommentinsection";
    }

    /**
     * Represents keys for HTTP requests to the API layer.
     */
    public static class ParamsNames {

        public static final String IS_IN_RECYCLE_BIN = "isinrecyclebin";

        public static final String IS_STUDENT_REJOINING = "isstudentrejoining";
        public static final String IS_INSTRUCTOR_REJOINING = "isinstructorrejoining";

        public static final String COURSE_ID = "courseid";
        public static final String COURSE_STATUS = "coursestatus";
        public static final String INSTRUCTOR_ID = "instructorid";
        public static final String INSTRUCTOR_EMAIL = "instructoremail";
        public static final String INSTRUCTOR_INSTITUTION = "instructorinstitution";
        public static final String IS_CREATING_ACCOUNT = "iscreatingaccount";
        public static final String IS_INSTRUCTOR = "isinstructor";

        public static final String FEEDBACK_SESSION_NAME = "fsname";
        public static final String FEEDBACK_SESSION_STARTTIME = "starttime";
        public static final String FEEDBACK_SESSION_ENDTIME = "endtime";
        public static final String FEEDBACK_SESSION_MODERATED_PERSON = "moderatedperson";
        public static final String FEEDBACK_SESSION_LOG_TYPE = "fsltype";
        public static final String FEEDBACK_SESSION_LOG_STARTTIME = "fslstarttime";
        public static final String FEEDBACK_SESSION_LOG_ENDTIME = "fslendtime";

        public static final String FEEDBACK_QUESTION_ID = "questionid";

        public static final String FEEDBACK_RESPONSE_ID = "responseid";

        public static final String FEEDBACK_RESPONSE_COMMENT_ID = "responsecommentid";

        public static final String FEEDBACK_RESULTS_GROUPBYSECTION = "frgroupbysection";

        public static final String FEEDBACK_RESULTS_SECTION_BY_GIVER_RECEIVER = "frsessionbygiverreceiver";

        public static final String PREVIEWAS = "previewas";

        public static final String STUDENT_ID = "googleid";
        public static final String INVITER_ID = "invitergoogleid";

        public static final String REGKEY = "key";
        public static final String STUDENT_EMAIL = "studentemail";

        public static final String SECTION_NAME = "sectionname";

        public static final String TEAM_NAME = "teamname";

        public static final String ERROR = "error";
        public static final String USER_ID = "user";

        public static final String SEARCH_KEY = "searchkey";

        public static final String USER_CAPTCHA_RESPONSE = "captcharesponse";

        public static final String EMAIL_TYPE = "emailtype";
        public static final String USER_EMAIL = "useremail";

        public static final String ENTITY_TYPE = "entitytype";

        public static final String INTENT = "intent";

        public static final String TIMEZONE = "timezone";

        public static final String NOTIFY_ABOUT_DEADLINES = "notifydeadlines";

        public static final String QUERY_LOGS_STARTTIME = "starttime";
        public static final String QUERY_LOGS_ENDTIME = "endtime";
        public static final String QUERY_LOGS_SEVERITY = "severity";
        public static final String QUERY_LOGS_MIN_SEVERITY = "minseverity";
        public static final String QUERY_LOGS_TRACE = "traceid";
        public static final String QUERY_LOGS_ACTION_CLASS = "actionclass";
        public static final String QUERY_LOGS_EMAIL = "email";
        public static final String QUERY_LOGS_EVENT = "logevent";
        public static final String QUERY_LOGS_SOURCE_LOCATION_FILE = "sourcelocationfile";
        public static final String QUERY_LOGS_SOURCE_LOCATION_FUNCTION = "sourcelocationfunction";
        public static final String QUERY_LOGS_EXCEPTION_CLASS = "exceptionclass";
        public static final String QUERY_LOGS_LATENCY = "latency";
        public static final String QUERY_LOGS_STATUS = "status";
        public static final String QUERY_LOGS_VERSION = "version";
        public static final String QUERY_LOGS_EXTRA_FILTERS = "extrafilters";
        public static final String QUERY_LOGS_ORDER = "order";

        public static final String LIMIT = "limit";

        public static final String NOTIFICATION_ID = "notificationid";
        public static final String NOTIFICATION_TARGET_USER = "usertype";
        public static final String NOTIFICATION_IS_FETCHING_ALL = "isfetchingall";
        public static final String NOTFICATION_END_TIME = "endtime";
    }

    /**
     * Represents custom header names used by the system.
     */
    public static class HeaderNames {
        public static final String BACKDOOR_KEY = "Backdoor-Key";
        public static final String CSRF_KEY = "CSRF-Key";
        public static final String WEB_VERSION = "X-WEB-VERSION";
        public static final String CSRF_TOKEN = "X-CSRF-TOKEN";
    }

    /**
     * The course status respect to the instructor's point of view.
     * This parameter is used to get a course list for instructor.
     */
    public static class CourseStatus {
        public static final String ACTIVE = "active";
        public static final String ARCHIVED = "archived";
        public static final String SOFT_DELETED = "softDeleted";
    }

    /**
     * Represents user types.
     */
    public static class EntityType {

        public static final String STUDENT = "student";
        public static final String INSTRUCTOR = "instructor";
        public static final String ADMIN = "admin";
        public static final String MAINTAINER = "maintainer";

    }

    /**
     * Represents security-related configuration.
     */
    public static class SecurityConfig {

        public static final String CSRF_COOKIE_NAME = "CSRF-TOKEN";
        public static final String AUTH_COOKIE_NAME = "AUTH-TOKEN";

    }

    /**
     * Represents URIs of accessible pages in the front-end in past versions (V6 and before).
     */
    @Deprecated
    public static class LegacyURIs {

        public static final String INSTRUCTOR_COURSE_JOIN = "/page/instructorCourseJoin";
        public static final String STUDENT_COURSE_JOIN = "/page/studentCourseJoin";
        public static final String STUDENT_COURSE_JOIN_NEW = "/page/studentCourseJoinAuthentication";
        public static final String INSTRUCTOR_HOME_PAGE = "/page/instructorHomePage";
        public static final String STUDENT_HOME_PAGE = "/page/studentHomePage";
        public static final String STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE = "/page/studentFeedbackSubmissionEditPage";
        public static final String STUDENT_FEEDBACK_RESULTS_PAGE = "/page/studentFeedbackResultsPage";
        public static final String INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE = "/page/instructorFeedbackSubmissionEditPage";
        public static final String INSTRUCTOR_FEEDBACK_RESULTS_PAGE = "/page/instructorFeedbackResultsPage";

    }

    /**
     * Represents URIs of accessible pages in the front-end.
     */
    public static class WebPageURIs {
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        private static final String URI_PREFIX = "/web";

        private static final String STUDENT_PAGE = URI_PREFIX + "/" + EntityType.STUDENT;
        private static final String INSTRUCTOR_PAGE = URI_PREFIX + "/" + EntityType.INSTRUCTOR;
        private static final String ADMIN_PAGE = URI_PREFIX + "/" + EntityType.ADMIN;
        private static final String MAINTAINER_PAGE = URI_PREFIX + "/" + EntityType.MAINTAINER;
        private static final String FRONT_PAGE = URI_PREFIX + "/front";
        public static final String JOIN_PAGE = URI_PREFIX + "/join";

        public static final String ADMIN_HOME_PAGE = ADMIN_PAGE + "/home";
        public static final String ADMIN_ACCOUNTS_PAGE = ADMIN_PAGE + "/accounts";
        public static final String ADMIN_SEARCH_PAGE = ADMIN_PAGE + "/search";
        public static final String ADMIN_SESSIONS_PAGE = ADMIN_PAGE + "/sessions";
        public static final String ADMIN_TIMEZONE_PAGE = ADMIN_PAGE + "/timezone";
        public static final String ADMIN_LOGS_PAGE = ADMIN_PAGE + "/logs";
        public static final String ADMIN_NOTIFICATIONS_PAGE = ADMIN_PAGE + "/notifications";

        public static final String MAINTAINER_HOME_PAGE = MAINTAINER_PAGE + "/home";

        public static final String INSTRUCTOR_HOME_PAGE = INSTRUCTOR_PAGE + "/home";
        public static final String INSTRUCTOR_SEARCH_PAGE = INSTRUCTOR_PAGE + "/search";
        public static final String INSTRUCTOR_SESSIONS_PAGE = INSTRUCTOR_PAGE + "/sessions";
        public static final String INSTRUCTOR_SESSION_SUBMISSION_PAGE = INSTRUCTOR_PAGE + "/sessions/submission";
        public static final String INSTRUCTOR_SESSION_EDIT_PAGE = INSTRUCTOR_PAGE + "/sessions/edit";
        public static final String INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE = INSTRUCTOR_PAGE
                + "/sessions/individual-extension";
        public static final String INSTRUCTOR_SESSION_RESULTS_PAGE = INSTRUCTOR_PAGE + "/sessions/result";
        public static final String INSTRUCTOR_SESSION_REPORT_PAGE = INSTRUCTOR_PAGE + "/sessions/report";
        public static final String INSTRUCTOR_COURSES_PAGE = INSTRUCTOR_PAGE + "/courses";
        public static final String INSTRUCTOR_COURSE_DETAILS_PAGE = INSTRUCTOR_PAGE + "/courses/details";
        public static final String INSTRUCTOR_COURSE_EDIT_PAGE = INSTRUCTOR_PAGE + "/courses/edit";
        public static final String INSTRUCTOR_COURSE_ENROLL_PAGE = INSTRUCTOR_PAGE + "/courses/enroll";
        public static final String INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE = INSTRUCTOR_PAGE + "/courses/student/details";
        public static final String INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_PAGE = INSTRUCTOR_PAGE + "/courses/student/edit";
        public static final String INSTRUCTOR_STUDENT_LIST_PAGE = INSTRUCTOR_PAGE + "/students";
        public static final String INSTRUCTOR_STUDENT_RECORDS_PAGE = INSTRUCTOR_PAGE + "/students/records";
        public static final String INSTRUCTOR_STUDENT_ACTIVITY_LOGS_PAGE = INSTRUCTOR_PAGE
                + "/courses/student-activity-logs";
        public static final String INSTRUCTOR_NOTIFICATIONS_PAGE = INSTRUCTOR_PAGE + "/notifications";

        public static final String STUDENT_HOME_PAGE = STUDENT_PAGE + "/home";
        public static final String STUDENT_COURSE_DETAILS_PAGE = STUDENT_PAGE + "/course";
        public static final String STUDENT_SESSION_SUBMISSION_PAGE = STUDENT_PAGE + "/sessions/submission";
        public static final String STUDENT_SESSION_RESULTS_PAGE = STUDENT_PAGE + "/sessions/result";
        public static final String STUDENT_NOTIFICATIONS_PAGE = STUDENT_PAGE + "/notifications";

        public static final String SESSION_RESULTS_PAGE = URI_PREFIX + "/sessions/result";
        public static final String SESSION_SUBMISSION_PAGE = URI_PREFIX + "/sessions/submission";
        public static final String SESSIONS_LINK_RECOVERY_PAGE = FRONT_PAGE + "/help/session-links-recovery";
    }

    /**
     * Represents URIs of resource endpoints.
     */
    public static class ResourceURIs {
        private static final String URI_PREFIX = "/webapi";

        public static final String DATABUNDLE = URI_PREFIX + "/databundle";
        public static final String DATABUNDLE_DOCUMENTS = URI_PREFIX + "/databundle/documents";
        public static final String DEADLINE_EXTENSION = URI_PREFIX + "/deadlineextension";
        public static final String EXCEPTION = URI_PREFIX + "/exception";
        public static final String ERROR_REPORT = URI_PREFIX + "/errorreport";
        public static final String AUTH = URI_PREFIX + "/auth";
        public static final String AUTH_REGKEY = URI_PREFIX + "/auth/regkey";
        public static final String ACCOUNT = URI_PREFIX + "/account";
        public static final String ACCOUNT_RESET = URI_PREFIX + "/account/reset";
        public static final String ACCOUNT_REQUEST = URI_PREFIX + "/account/request";
        public static final String ACCOUNT_REQUEST_RESET = ACCOUNT_REQUEST + "/reset";
        public static final String ACCOUNTS = URI_PREFIX + "/accounts";
        public static final String RESPONSE_COMMENT = URI_PREFIX + "/responsecomment";
        public static final String COURSE = URI_PREFIX + "/course";
        public static final String COURSE_ARCHIVE = URI_PREFIX + "/course/archive";
        public static final String BIN_COURSE = URI_PREFIX + "/bin/course";
        public static final String COURSE_SECTIONS = URI_PREFIX + "/course/sections";
        public static final String COURSES = URI_PREFIX + "/courses";
        public static final String INSTRUCTORS = URI_PREFIX + "/instructors";
        public static final String INSTRUCTOR = URI_PREFIX + "/instructor";
        public static final String INSTRUCTOR_PRIVILEGE = URI_PREFIX + "/instructor/privilege";
        public static final String INSTRUCTOR_KEY = URI_PREFIX + "/instructor/key";
        public static final String RESULT = URI_PREFIX + "/result";
        public static final String STUDENTS = URI_PREFIX + "/students";
        public static final String STUDENT = URI_PREFIX + "/student";
        public static final String STUDENT_KEY = URI_PREFIX + "/student/key";
        public static final String NOTIFICATION = URI_PREFIX + "/notification";
        public static final String NOTIFICATIONS = URI_PREFIX + "/notifications";
        public static final String NOTIFICATION_READ = URI_PREFIX + "/notification/read";
        public static final String SESSIONS_ONGOING = URI_PREFIX + "/sessions/ongoing";
        public static final String SESSION = URI_PREFIX + "/session";
        public static final String SESSION_PUBLISH = URI_PREFIX + "/session/publish";
        public static final String SESSION_REMIND_SUBMISSION = URI_PREFIX + "/session/remind/submission";
        public static final String SESSION_REMIND_RESULT = URI_PREFIX + "/session/remind/result";
        public static final String SESSION_STATS = URI_PREFIX + "/session/stats";
        public static final String SESSION_SUBMITTED_GIVER_SET = URI_PREFIX + "/session/submitted/giverset";
        public static final String SESSIONS = URI_PREFIX + "/sessions";
        public static final String SEARCH_ACCOUNT_REQUESTS = URI_PREFIX + "/search/accountrequests";
        public static final String SEARCH_INSTRUCTORS = URI_PREFIX + "/search/instructors";
        public static final String SEARCH_STUDENTS = URI_PREFIX + "/search/students";
        public static final String BIN_SESSION = URI_PREFIX + "/bin/session";
        public static final String QUESTIONS = URI_PREFIX + "/questions";
        public static final String QUESTION = URI_PREFIX + "/question";
        public static final String QUESTION_RECIPIENTS = URI_PREFIX + "/question/recipients";
        public static final String RESPONSES = URI_PREFIX + "/responses";
        public static final String USAGE_STATISTICS = URI_PREFIX + "/usagestats";
        public static final String HAS_RESPONSES = URI_PREFIX + "/hasResponses";
        public static final String JOIN = URI_PREFIX + "/join";
        public static final String JOIN_REMIND = URI_PREFIX + "/join/remind";
        public static final String TIMEZONE = URI_PREFIX + "/timezone";
        public static final String SESSION_LINKS_RECOVERY = URI_PREFIX + "/sessionlinksrecovery";
        public static final String EMAIL = URI_PREFIX + "/email";
        public static final String SESSION_LOGS = URI_PREFIX + "/logs/session";
        public static final String LOGS = URI_PREFIX + "/logs/query";
        public static final String ACTION_CLASS = URI_PREFIX + "/actionclass";
        public static final String USER_COOKIE = URI_PREFIX + "/cookie";
    }

    /**
     * Represents URIs of endpoints used by cron jobs.
     */
    public static class CronJobURIs {
        private static final String URI_PREFIX = "/auto";

        public static final String AUTOMATED_LOG_COMPILATION = URI_PREFIX + "/compileLogs";
        public static final String AUTOMATED_DATASTORE_BACKUP = URI_PREFIX + "/datastoreBackup";
        public static final String AUTOMATED_FEEDBACK_OPENING_SOON_REMINDERS =
                URI_PREFIX + "/feedbackSessionOpeningSoonReminders";
        public static final String AUTOMATED_FEEDBACK_OPENING_REMINDERS =
                URI_PREFIX + "/feedbackSessionOpeningReminders";
        public static final String AUTOMATED_FEEDBACK_CLOSED_REMINDERS =
                URI_PREFIX + "/feedbackSessionClosedReminders";
        public static final String AUTOMATED_FEEDBACK_CLOSING_REMINDERS =
                URI_PREFIX + "/feedbackSessionClosingReminders";
        public static final String AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS =
                URI_PREFIX + "/feedbackSessionPublishedReminders";
        public static final String AUTOMATED_USAGE_STATISTICS_COLLECTION =
                URI_PREFIX + "/calculateUsageStatistics";
    }

    /**
     * Configurations for task queue.
     */
    public static class TaskQueue {
        public static final String URI_PREFIX = "/worker";

        public static final String FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME =
                "feedback-session-published-email-queue";
        public static final String FEEDBACK_SESSION_PUBLISHED_EMAIL_WORKER_URL =
                URI_PREFIX + "/feedbackSessionPublishedEmail";

        public static final String FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_QUEUE_NAME =
                "feedback-session-resend-published-email-queue";
        public static final String FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_WORKER_URL =
                URI_PREFIX + "/feedbackSessionResendPublishedEmail";

        public static final String FEEDBACK_SESSION_REMIND_EMAIL_QUEUE_NAME = "feedback-session-remind-email-queue";
        public static final String FEEDBACK_SESSION_REMIND_EMAIL_WORKER_URL = URI_PREFIX + "/feedbackSessionRemindEmail";

        public static final String FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_QUEUE_NAME =
                "feedback-session-remind-particular-users-email-queue";
        public static final String FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_URL =
                URI_PREFIX + "/feedbackSessionRemindParticularUsersEmail";

        public static final String FEEDBACK_SESSION_UNPUBLISHED_EMAIL_QUEUE_NAME =
                "feedback-session-unpublished-email-queue";
        public static final String FEEDBACK_SESSION_UNPUBLISHED_EMAIL_WORKER_URL =
                URI_PREFIX + "/feedbackSessionUnpublishedEmail";

        public static final String INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME = "instructor-course-join-email-queue";
        public static final String INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL = URI_PREFIX + "/instructorCourseJoinEmail";

        public static final String SEND_EMAIL_QUEUE_NAME = "send-email-queue";
        public static final String SEND_EMAIL_WORKER_URL = URI_PREFIX + "/sendEmail";

        public static final String STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME = "student-course-join-email-queue";
        public static final String STUDENT_COURSE_JOIN_EMAIL_WORKER_URL = URI_PREFIX + "/studentCourseJoinEmail";

        public static final String SEARCH_INDEXING_QUEUE_NAME = "search-indexing-queue";
        public static final String INSTRUCTOR_SEARCH_INDEXING_WORKER_URL = URI_PREFIX + "/instructorSearchIndexing";
        public static final String ACCOUNT_REQUEST_SEARCH_INDEXING_WORKER_URL =
                URI_PREFIX + "/accountRequestSearchIndexing";
        public static final String STUDENT_SEARCH_INDEXING_WORKER_URL = URI_PREFIX + "/studentSearchIndexing";
    }

}
