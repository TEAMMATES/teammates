package teammates.common.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Stores constants that are widely used across classes.
 * this class contains several nested classes, each containing a specific
 * category of constants.
 */
public final class Const {

    /*
     * This section holds constants that are defined as constants primarily
     * because they are repeated in many places.
     */

    public static final String USER_NOBODY_TEXT = "-";

    public static final String USER_TEAM_FOR_INSTRUCTOR = "Instructors";

    public static final String DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT = "Anonymous";

    public static final int SECTION_SIZE_LIMIT = 100;

    public static final String DEFAULT_SECTION = "None";

    public static final ZoneId DEFAULT_TIME_ZONE = ZoneId.of("UTC");
    public static final String ENCODING = "UTF8";

    public static final Duration FEEDBACK_SESSIONS_SEARCH_WINDOW = Duration.ofDays(30);

    /*
     * These constants are used as variable values to mean that the variable
     * is in a 'special' state.
     */
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

    /*
     * Other Constants
     */

    private Const() {
        // Utility class containing constants
    }

    public static class InstructorPermissionRoleNames {
        public static final String INSTRUCTOR_PERMISSION_ROLE_COOWNER = "Co-owner";
        public static final String INSTRUCTOR_PERMISSION_ROLE_MANAGER = "Manager";
        public static final String INSTRUCTOR_PERMISSION_ROLE_OBSERVER = "Observer";
        public static final String INSTRUCTOR_PERMISSION_ROLE_TUTOR = "Tutor";
        public static final String INSTRUCTOR_PERMISSION_ROLE_CUSTOM = "Custom";
    }

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

    public static class ParamsNames {

        public static final String IS_IN_RECYCLE_BIN = "isinrecyclebin";

        public static final String IS_STUDENT_REJOINING = "isstudentrejoining";
        public static final String IS_INSTRUCTOR_REJOINING = "isinstructorrejoining";

        public static final String COURSE_ID = "courseid";
        public static final String COURSE_STATUS = "coursestatus";
        public static final String INSTRUCTOR_ID = "instructorid";
        public static final String INSTRUCTOR_EMAIL = "instructoremail";
        public static final String INSTRUCTOR_INSTITUTION = "instructorinstitution";
        public static final String INSTITUTION_MAC = "mac";

        public static final String INSTRUCTOR_ROLE_NAME = "instructorrole";

        public static final String LOCAL_DATE_TIME = "localdatetime";
        public static final String TIME_ZONE = "timezone";

        public static final String FEEDBACK_SESSION_NAME = "fsname";
        public static final String FEEDBACK_SESSION_STARTTIME = "starttime";
        public static final String FEEDBACK_SESSION_ENDTIME = "endtime";
        public static final String FEEDBACK_SESSION_MODERATED_PERSON = "moderatedperson";

        public static final String FEEDBACK_QUESTION_ID = "questionid";

        public static final String FEEDBACK_RESPONSE_ID = "responseid";

        public static final String FEEDBACK_RESPONSE_COMMENT_ID = "responsecommentid";

        public static final String FEEDBACK_RESULTS_GROUPBYSECTION = "frgroupbysection";

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

        public static final String ENTITY_TYPE = "entitytype";

        public static final String INTENT = "intent";
    }

    public static class SearchIndex {
        public static final String FEEDBACK_RESPONSE_COMMENT = "feedbackresponsecomment";
        public static final String STUDENT = "student";
        public static final String INSTRUCTOR = "instructor";
    }

    public static class SearchDocumentField {
        public static final String FEEDBACK_RESPONSE_COMMENT_GIVER_NAME = "frCommentGiverName";
        public static final String FEEDBACK_RESPONSE_GIVER_NAME = "feedbackResponseGiverName";
        public static final String FEEDBACK_RESPONSE_RECEIVER_NAME = "feedbackResponseReceiverName";
        public static final String SEARCHABLE_TEXT = "searchableText";
        public static final String COURSE_ID = "courseId";
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

    public static class EntityType {

        public static final String STUDENT = "student";
        public static final String INSTRUCTOR = "instructor";
        public static final String ADMIN = "admin";

    }

    public static class CsrfConfig {

        public static final String TOKEN_HEADER_NAME = "X-CSRF-TOKEN";
        public static final String TOKEN_COOKIE_NAME = "CSRF-TOKEN";

    }

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

    public static class WebPageURIs {
        public static final String LOGOUT = "/logout";
        private static final String URI_PREFIX = "/web";

        private static final String STUDENT_PAGE = URI_PREFIX + "/" + EntityType.STUDENT;
        private static final String INSTRUCTOR_PAGE = URI_PREFIX + "/" + EntityType.INSTRUCTOR;
        private static final String ADMIN_PAGE = URI_PREFIX + "/" + EntityType.ADMIN;
        private static final String FRONT_PAGE = URI_PREFIX + "/front";
        public static final String JOIN_PAGE = URI_PREFIX + "/join";

        public static final String ADMIN_HOME_PAGE = ADMIN_PAGE + "/home";
        public static final String ADMIN_ACCOUNTS_PAGE = ADMIN_PAGE + "/accounts";
        public static final String ADMIN_SEARCH_PAGE = ADMIN_PAGE + "/search";
        public static final String ADMIN_SESSIONS_PAGE = ADMIN_PAGE + "/sessions";
        public static final String ADMIN_TIMEZONE_PAGE = ADMIN_PAGE + "/timezone";

        public static final String INSTRUCTOR_HOME_PAGE = INSTRUCTOR_PAGE + "/home";
        public static final String INSTRUCTOR_SEARCH_PAGE = INSTRUCTOR_PAGE + "/search";
        public static final String INSTRUCTOR_SESSIONS_PAGE = INSTRUCTOR_PAGE + "/sessions";
        public static final String INSTRUCTOR_SESSION_SUBMISSION_PAGE = INSTRUCTOR_PAGE + "/sessions/submission";
        public static final String INSTRUCTOR_SESSION_EDIT_PAGE = INSTRUCTOR_PAGE + "/sessions/edit";
        public static final String INSTRUCTOR_SESSION_RESULTS_PAGE = INSTRUCTOR_PAGE + "/sessions/result";
        public static final String INSTRUCTOR_COURSES_PAGE = INSTRUCTOR_PAGE + "/courses";
        public static final String INSTRUCTOR_COURSE_DETAILS_PAGE = INSTRUCTOR_PAGE + "/courses/details";
        public static final String INSTRUCTOR_COURSE_EDIT_PAGE = INSTRUCTOR_PAGE + "/courses/edit";
        public static final String INSTRUCTOR_COURSE_ENROLL_PAGE = INSTRUCTOR_PAGE + "/courses/enroll";
        public static final String INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE = INSTRUCTOR_PAGE + "/courses/student/details";
        public static final String INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_PAGE = INSTRUCTOR_PAGE + "/courses/student/edit";
        public static final String INSTRUCTOR_STUDENT_LIST_PAGE = INSTRUCTOR_PAGE + "/students";
        public static final String INSTRUCTOR_STUDENT_RECORDS_PAGE = INSTRUCTOR_PAGE + "/students/records";

        public static final String STUDENT_HOME_PAGE = STUDENT_PAGE + "/home";
        public static final String STUDENT_COURSE_DETAILS_PAGE = STUDENT_PAGE + "/course";
        public static final String STUDENT_PROFILE_PAGE = STUDENT_PAGE + "/profile";
        public static final String STUDENT_SESSION_SUBMISSION_PAGE = STUDENT_PAGE + "/sessions/submission";
        public static final String STUDENT_SESSION_RESULTS_PAGE = STUDENT_PAGE + "/sessions/result";

        public static final String SESSION_RESULTS_PAGE = URI_PREFIX + "/sessions/result";
        public static final String SESSION_SUBMISSION_PAGE = URI_PREFIX + "/sessions/submission";
        public static final String SESSIONS_LINK_RECOVERY_PAGE = FRONT_PAGE + "/help/session-links-recovery";
    }

    public static class ResourceURIs {
        private static final String URI_PREFIX = "/webapi";

        public static final String DATABUNDLE = URI_PREFIX + "/databundle";
        public static final String DATABUNDLE_DOCUMENTS = URI_PREFIX + "/databundle/documents";
        public static final String EXCEPTION = URI_PREFIX + "/exception";
        public static final String ERROR_REPORT = URI_PREFIX + "/errorreport";
        public static final String AUTH = URI_PREFIX + "/auth";
        public static final String AUTH_REGKEY = URI_PREFIX + "/auth/regkey";
        public static final String ACCOUNT = URI_PREFIX + "/account";
        public static final String ACCOUNT_RESET = URI_PREFIX + "/account/reset";
        public static final String ACCOUNT_DOWNGRADE = URI_PREFIX + "/account/downgrade";
        public static final String RESPONSE_COMMENT = URI_PREFIX + "/responsecomment";
        public static final String COURSE = URI_PREFIX + "/course";
        public static final String COURSE_ARCHIVE = URI_PREFIX + "/course/archive";
        public static final String BIN_COURSE = URI_PREFIX + "/bin/course";
        public static final String COURSE_SECTIONS = URI_PREFIX + "/course/sections";
        public static final String COURSES = URI_PREFIX + "/courses";
        public static final String INSTRUCTORS = URI_PREFIX + "/instructors";
        public static final String INSTRUCTOR = URI_PREFIX + "/instructor";
        public static final String INSTRUCTOR_PRIVILEGE = URI_PREFIX + "/instructor/privilege";
        public static final String RESULT = URI_PREFIX + "/result";
        public static final String STUDENTS = URI_PREFIX + "/students";
        public static final String STUDENT = URI_PREFIX + "/student";
        public static final String SESSIONS_ONGOING = URI_PREFIX + "/sessions/ongoing";
        public static final String SESSION = URI_PREFIX + "/session";
        public static final String SESSION_PUBLISH = URI_PREFIX + "/session/publish";
        public static final String SESSION_REMIND_SUBMISSION = URI_PREFIX + "/session/remind/submission";
        public static final String SESSION_REMIND_RESULT = URI_PREFIX + "/session/remind/result";
        public static final String SESSION_STATS = URI_PREFIX + "/session/stats";
        public static final String SESSION_SUBMITTED_GIVER_SET = URI_PREFIX + "/session/submitted/giverset";
        public static final String SESSIONS = URI_PREFIX + "/sessions";
        public static final String SEARCH_COMMENTS = URI_PREFIX + "/search/comments";
        public static final String SEARCH_INSTRUCTORS = URI_PREFIX + "/search/instructors";
        public static final String SEARCH_STUDENTS = URI_PREFIX + "/search/students";
        public static final String BIN_SESSION = URI_PREFIX + "/bin/session";
        public static final String QUESTIONS = URI_PREFIX + "/questions";
        public static final String QUESTION = URI_PREFIX + "/question";
        public static final String QUESTION_RECIPIENTS = URI_PREFIX + "/question/recipients";
        public static final String RESPONSES = URI_PREFIX + "/responses";
        public static final String HAS_RESPONSES = URI_PREFIX + "/hasResponses";
        public static final String JOIN = URI_PREFIX + "/join";
        public static final String JOIN_REMIND = URI_PREFIX + "/join/remind";
        public static final String TIMEZONE = URI_PREFIX + "/timezone";
        public static final String LOCAL_DATE_TIME = URI_PREFIX + "/localdatetime";
        public static final String SESSION_LINKS_RECOVERY = URI_PREFIX + "/sessionlinksrecovery";
        public static final String NATIONALITIES = URI_PREFIX + "/nationalities";
        public static final String EMAIL = URI_PREFIX + "/email";

        public static final String STUDENT_PROFILE_PICTURE = URI_PREFIX + "/student/profilePic";
        public static final String STUDENT_PROFILE = URI_PREFIX + "/student/profile";
        public static final String STUDENT_COURSE_LINKS_REGENERATION = URI_PREFIX + "/student/courselinks/regeneration";
    }

    public static class CronJobURIs {
        private static final String URI_PREFIX = "/auto";

        public static final String AUTOMATED_LOG_COMPILATION = URI_PREFIX + "/compileLogs";
        public static final String AUTOMATED_DATASTORE_BACKUP = URI_PREFIX + "/datastoreBackup";
        public static final String AUTOMATED_FEEDBACK_OPENING_REMINDERS =
                URI_PREFIX + "/feedbackSessionOpeningReminders";
        public static final String AUTOMATED_FEEDBACK_CLOSED_REMINDERS =
                URI_PREFIX + "/feedbackSessionClosedReminders";
        public static final String AUTOMATED_FEEDBACK_CLOSING_REMINDERS =
                URI_PREFIX + "/feedbackSessionClosingReminders";
        public static final String AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS =
                URI_PREFIX + "/feedbackSessionPublishedReminders";
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

    }

}
