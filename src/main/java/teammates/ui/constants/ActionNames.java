package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Action names for http requests.
 */
public enum ActionNames {
    //CHECKSTYLE.OFF:JavadocVariable
    PUT_DATA_BUNDLE_ACTION("PutDataBundleAction"),
    DELETE_DATA_BUNDLE_ACTION("DeleteDataBundleAction"),
    PUT_DATA_BUNDLE_DOCUMENTS_ACTION("PutDataBundleDocumentsAction"),
    ADMIN_EXCEPTION_TEST_ACTION("AdminExceptionTestAction"),
    SEND_ERROR_REPORT_ACTION("SendErrorReportAction"),
    GET_TIME_ZONES_ACTION("GetTimeZonesAction"),
    GET_NATIONALITIES_ACTION("GetNationalitiesAction"),
    GET_AUTH_INFO_ACTION("GetAuthInfoAction"),
    GET_REGKEY_VALIDITY_ACTION("GetRegkeyValidityAction"),
    GET_ACCOUNT_ACTION("GetAccountAction"),
    CREATE_ACCOUNT_ACTION("CreateAccountAction"),
    DELETE_ACCOUNT_ACTION("DeleteAccountAction"),
    DOWNGRADE_ACCOUNT_ACTION("DowngradeAccountAction"),
    RESET_ACCOUNT_ACTION("ResetAccountAction"),
    GET_COURSE_ACTION("GetCourseAction"),
    DELETE_COURSE_ACTION("DeleteCourseAction"),
    CREATE_COURSE_ACTION("CreateCourseAction"),
    UPDATE_COURSE_ACTION("UpdateCourseAction"),
    ARCHIVE_COURSE_ACTION("ArchiveCourseAction"),
    BIN_COURSE_ACTION("BinCourseAction"),
    RESTORE_COURSE_ACTION("RestoreCourseAction"),
    GET_COURSES_ACTION("GetCoursesAction"),
    GET_COURSE_SECTION_NAMES_ACTION("GetCourseSectionNamesAction"),
    GET_INSTRUCTORS_ACTION("GetInstructorsAction"),
    GET_INSTRUCTOR_ACTION("GetInstructorAction"),
    DELETE_INSTRUCTOR_ACTION("DeleteInstructorAction"),
    GET_INSTRUCTOR_PRIVILEGE_ACTION("GetInstructorPrivilegeAction"),
    UPDATE_INSTRUCTOR_PRIVILEGE_ACTION("UpdateInstructorPrivilegeAction"),
    CREATE_FEEDBACK_RESPONSE_COMMENT_ACTION("CreateFeedbackResponseCommentAction"),
    GET_FEEDBACK_RESPONSE_COMMENT_ACTION("GetFeedbackResponseCommentAction"),
    UPDATE_FEEDBACK_RESPONSE_COMMENT_ACTION("UpdateFeedbackResponseCommentAction"),
    DELETE_FEEDBACK_RESPONSE_COMMENT_ACTION("DeleteFeedbackResponseCommentAction"),
    GET_SESSION_RESULTS_ACTION("GetSessionResultsAction"),
    GET_STUDENTS_ACTION("GetStudentsAction"),
    ENROLL_STUDENTS_ACTION("EnrollStudentsAction"),
    DELETE_STUDENTS_ACTION("DeleteStudentsAction"),
    DELETE_STUDENT_ACTION("DeleteStudentAction"),
    GET_STUDENT_ACTION("GetStudentAction"),
    UPDATE_STUDENT_ACTION("UpdateStudentAction"),
    SEARCH_INSTRUCTORS_ACTION("SearchInstructorsAction"),
    SEARCH_STUDENTS_ACTION("SearchStudentsAction"),
    GENERATE_EMAIL_ACTION("GenerateEmailAction"),
    GET_ONGOING_SESSIONS_ACTION("GetOngoingSessionsAction"),
    GET_SESSION_RESPONSE_STATS_ACTION("GetSessionResponseStatsAction"),
    GET_FEEDBACK_SESSION_ACTION("GetFeedbackSessionAction"),
    UPDATE_FEEDBACK_SESSION_ACTION("UpdateFeedbackSessionAction"),
    CREATE_FEEDBACK_SESSION_ACTION("CreateFeedbackSessionAction"),
    DELETE_FEEDBACK_SESSION_ACTION("DeleteFeedbackSessionAction"),
    PUBLISH_FEEDBACK_SESSION_ACTION("PublishFeedbackSessionAction"),
    UNPUBLISH_FEEDBACK_SESSION_ACTION("UnpublishFeedbackSessionAction"),
    GET_FEEDBACK_SESSION_SUBMITTED_GIVER_SET_ACTION("GetFeedbackSessionSubmittedGiverSetAction"),
    REMIND_FEEDBACK_SESSION_SUBMISSION_ACTION("RemindFeedbackSessionSubmissionAction"),
    REMIND_FEEDBACK_SESSION_RESULT_ACTION("RemindFeedbackSessionResultAction"),
    GET_FEEDBACK_SESSIONS_ACTION("GetFeedbackSessionsAction"),
    BIN_FEEDBACK_SESSION_ACTION("BinFeedbackSessionAction"),
    RESTORE_FEEDBACK_SESSION_ACTION("RestoreFeedbackSessionAction"),
    REGENERATE_STUDENT_COURSE_LINKS_ACTION("RegenerateStudentCourseLinksAction"),
    GET_FEEDBACK_QUESTIONS_ACTION("GetFeedbackQuestionsAction"),
    CREATE_FEEDBACK_QUESTION_ACTION("CreateFeedbackQuestionAction"),
    UPDATE_FEEDBACK_QUESTION_ACTION("UpdateFeedbackQuestionAction"),
    DELETE_FEEDBACK_QUESTION_ACTION("DeleteFeedbackQuestionAction"),
    GET_FEEDBACK_QUESTION_RECIPIENTS_ACTION("GetFeedbackQuestionRecipientsAction"),
    GET_FEEDBACK_RESPONSES_ACTION("GetFeedbackResponsesAction"),
    SUBMIT_FEEDBACK_RESPONSES_ACTION("SubmitFeedbackResponsesAction"),
    GET_HAS_RESPONSES_ACTION("GetHasResponsesAction"),
    GET_LOCAL_DATE_TIME_INFO_ACTION("GetLocalDateTimeInfoAction"),
    SESSION_LINKS_RECOVERY_ACTION("SessionLinksRecoveryAction"),
    GET_COURSE_JOIN_STATUS_ACTION("GetCourseJoinStatusAction"),
    JOIN_COURSE_ACTION("JoinCourseAction"),
    SEND_JOIN_REMINDER_EMAIL_ACTION("SendJoinReminderEmailAction"),
    GET_STUDENT_PROFILE_ACTION("GetStudentProfileAction"),
    UPDATE_STUDENT_PROFILE_ACTION("UpdateStudentProfileAction"),
    GET_STUDENT_PROFILE_PICTURE_ACTION("GetStudentProfilePictureAction"),
    POST_STUDENT_PROFILE_PICTURE_ACTION("PostStudentProfilePictureAction"),
    DELETE_STUDENT_PROFILE_PICTURE_ACTION("DeleteStudentProfilePictureAction"),
    UPDATE_INSTRUCTOR_ACTION("UpdateInstructorAction"),
    CREATE_INSTRUCTOR_ACTION("CreateInstructorAction"),
    CREATE_FEEDBACK_SESSION_LOG_ACTION("CreateFeedbackSessionLogAction"),
    GET_FEEDBACK_SESSION_LOGS_ACTION("GetFeedbackSessionLogsAction"),
    QUERY_LOGS_ACTION("QueryLogsAction"),
    COMPILE_LOGS_ACTION("CompileLogsAction"),
    DATASTORE_BACKUP_ACTION("DatastoreBackupAction"),
    FEEDBACK_SESSION_OPENING_REMINDERS_ACTION("FeedbackSessionOpeningRemindersAction"),
    FEEDBACK_SESSION_CLOSED_REMINDERS_ACTION("FeedbackSessionClosedRemindersAction"),
    FEEDBACK_SESSION_CLOSING_REMINDERS_ACTION("FeedbackSessionClosingRemindersAction"),
    FEEDBACK_SESSION_PUBLISHED_REMINDERS_ACTION("FeedbackSessionPublishedRemindersAction"),
    FEEDBACK_SESSION_PUBLISHED_EMAIL_WORKER_ACTION("FeedbackSessionPublishedEmailWorkerAction"),
    FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_WORKER_ACTION("FeedbackSessionResendPublishedEmailWorkerAction"),
    FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_ACTION("FeedbackSessionRemindParticularUsersEmailWorkerAction"),
    FEEDBACK_SESSION_UNPUBLISHED_EMAIL_WORKER_ACTION("FeedbackSessionUnpublishedEmailWorkerAction"),
    INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_ACTION("InstructorCourseJoinEmailWorkerAction"),
    SEND_EMAIL_WORKER_ACTION("SendEmailWorkerAction"),
    STUDENT_COURSE_JOIN_EMAIL_WORKER_ACTION("StudentCourseJoinEmailWorkerAction");
    //CHECKSTYLE.ON:JavadocVariable

    private final String actionName;

    ActionNames(String s) {
        this.actionName = s;
    }

    @JsonValue
    public String getActionName() {
        return actionName;
    }
}
