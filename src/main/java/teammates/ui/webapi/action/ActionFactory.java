package teammates.ui.webapi.action;

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
        map(ResourceURIs.DATABUNDLE, POST, PutDataBundleAction.class);
        // Even though this is a DELETE action, PUT is used as DELETE does not allow usage of response body
        map(ResourceURIs.DATABUNDLE, PUT, DeleteDataBundleAction.class);
        map(ResourceURIs.EXCEPTION, GET, AdminExceptionTestAction.class);
        map(ResourceURIs.ERROR_REPORT, POST, SendErrorReportAction.class);
        map(ResourceURIs.TIMEZONE, GET, GetTimeZonesAction.class);
        map(ResourceURIs.NATIONALITIES, GET, GetNationalitiesAction.class);
        map(ResourceURIs.AUTH, GET, GetAuthInfoAction.class);
        map(ResourceURIs.ACCOUNTS_SEARCH, GET, SearchAccountsAction.class);
        map(ResourceURIs.ACCOUNT, GET, GetAccountAction.class);
        map(ResourceURIs.ACCOUNT, POST, CreateAccountAction.class);
        map(ResourceURIs.ACCOUNT, DELETE, DeleteAccountAction.class);
        map(ResourceURIs.ACCOUNT_DOWNGRADE, PUT, DowngradeAccountAction.class);
        map(ResourceURIs.ACCOUNT_RESET, PUT, ResetAccountAction.class);
        map(ResourceURIs.COURSE, GET, GetCourseAction.class);
        map(ResourceURIs.COURSE, DELETE, DeleteCourseAction.class);
        map(ResourceURIs.COURSE, POST, CreateCourseAction.class);
        map(ResourceURIs.COURSE, PUT, UpdateCourseAction.class);
        map(ResourceURIs.COURSE_ARCHIVE, PUT, ArchiveCourseAction.class);
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
        map(ResourceURIs.QUESTIONS, GET, GetFeedbackQuestionsAction.class);
        map(ResourceURIs.QUESTION, POST, CreateFeedbackQuestionAction.class);
        map(ResourceURIs.QUESTION, PUT, UpdateFeedbackQuestionAction.class);
        map(ResourceURIs.QUESTION, DELETE, DeleteFeedbackQuestionAction.class);
        map(ResourceURIs.QUESTION_RECIPIENTS, GET, GetFeedbackQuestionRecipientsAction.class);
        map(ResourceURIs.RESPONSES, GET, GetFeedbackResponsesAction.class);
        map(ResourceURIs.RESPONSE, POST, CreateFeedbackResponseAction.class);
        map(ResourceURIs.RESPONSE, PUT, UpdateFeedbackResponseAction.class);
        map(ResourceURIs.RESPONSE, DELETE, DeleteFeedbackResponseAction.class);
        map(ResourceURIs.HAS_RESPONSES, GET, GetHasResponsesAction.class);
        map(ResourceURIs.SUBMISSION_CONFIRMATION, POST, ConfirmFeedbackSessionSubmissionAction.class);
        map(ResourceURIs.LOCAL_DATE_TIME, GET, GetLocalDateTimeInfoAction.class);
        map(ResourceURIs.SESSION_LINKS_RECOVERY, POST, SessionLinksRecoveryAction.class);
        map(ResourceURIs.JOIN, GET, GetCourseJoinStatusAction.class);
        map(ResourceURIs.JOIN, PUT, JoinCourseAction.class);
        map(ResourceURIs.JOIN_REMIND, POST, SendJoinReminderEmailAction.class);
        map(ResourceURIs.COURSE_ENROLL_STUDENTS, GET, GetCourseEnrollStudentsAction.class);
        map(ResourceURIs.INSTRUCTOR_COURSES, GET, GetInstructorCoursesAction.class);
        map(ResourceURIs.COURSE_STUDENT_DETAILS, GET, GetCourseStudentDetailsAction.class);
        map(ResourceURIs.STUDENT_COURSE, GET, StudentGetCourseDetailsAction.class);
        map(ResourceURIs.STUDENT_PROFILE, GET, GetStudentProfileAction.class);
        map(ResourceURIs.STUDENT_PROFILE, PUT, UpdateStudentProfileAction.class);
        map(ResourceURIs.STUDENT_PROFILE_PICTURE, GET, GetStudentProfilePictureAction.class);
        map(ResourceURIs.STUDENT_PROFILE_PICTURE, POST, PostStudentProfilePictureAction.class);
        map(ResourceURIs.STUDENT_PROFILE_PICTURE, DELETE, DeleteStudentProfilePictureAction.class);
        map(ResourceURIs.STUDENT_COURSES, GET, GetStudentCoursesAction.class);
        map(ResourceURIs.STUDENTS_CSV, GET, GetStudentsAsCsvAction.class);
        map(ResourceURIs.STUDENTS_AND_FEEDBACK_SESSION_DATA_SEARCH, GET, SearchStudentsAndFeedbackSessionDataAction.class);
        map(ResourceURIs.STUDENT_EDIT_DETAILS, GET, GetStudentEditDetailsAction.class);
        map(ResourceURIs.COURSE_EDIT_DETAILS, GET, GetCourseEditDetailsAction.class);
        map(ResourceURIs.INSTRUCTOR, PUT, UpdateInstructorAction.class);
        map(ResourceURIs.INSTRUCTOR, POST, CreateInstructorAction.class);
        map(ResourceURIs.STUDENT_RECORDS, GET, GetStudentRecordsAction.class);
        map(ResourceURIs.INSTRUCTOR_COURSE_DETAILS, GET, GetInstructorCourseDetailsAction.class);
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
