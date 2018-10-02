package teammates.ui.controller;

// CHECKSTYLE.OFF:AvoidStarImport as there would be many (>100) import lines added if we were to import all of the ActionURIs
import static teammates.common.util.Const.ActionURIs.*;
// CHECKSTYLE.ON:AvoidStarImport

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import teammates.common.exception.PageNotFoundException;
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
        map(ADMIN_HOME_PAGE, AdminHomePageAction.class);
        map(ADMIN_ACCOUNT_DELETE, AdminAccountDeleteAction.class);
        map(ADMIN_ACTIVITY_LOG_PAGE, AdminActivityLogPageAction.class);
        map(ADMIN_ACCOUNT_DETAILS_PAGE, AdminAccountDetailsPageAction.class);
        map(ADMIN_ACCOUNT_MANAGEMENT_PAGE, AdminAccountManagementPageAction.class);
        map(ADMIN_EXCEPTION_TEST, AdminExceptionTestAction.class);
        map(ADMIN_INSTRUCTORACCOUNT_ADD, AdminInstructorAccountAddAction.class);
        map(ADMIN_SESSIONS_PAGE, AdminSessionsPageAction.class);
        map(ADMIN_SEARCH_PAGE, AdminSearchPageAction.class);
        map(ADMIN_STUDENT_GOOGLE_ID_RESET, AdminStudentGoogleIdResetAction.class);
        map(ADMIN_EMAIL_COMPOSE_PAGE, AdminEmailComposePageAction.class);
        map(ADMIN_EMAIL_COMPOSE_SAVE, AdminEmailComposeSaveAction.class);
        map(ADMIN_EMAIL_COMPOSE_SEND, AdminEmailComposeSendAction.class);
        map(ADMIN_EMAIL_IMAGE_UPLOAD, AdminEmailImageUploadAction.class);
        map(ADMIN_EMAIL_GROUP_RECEIVER_LIST_UPLOAD, AdminEmailGroupReceiverListUploadAction.class);
        map(ADMIN_EMAIL_CREATE_IMAGE_UPLOAD_URL, AdminEmailCreateImageUploadUrlAction.class);
        map(ADMIN_EMAIL_CREATE_GROUP_RECEIVER_LIST_UPLOAD_URL, AdminEmailCreateGroupReceiverListUploadUrlAction.class);
        map(ADMIN_EMAIL_SENT_PAGE, AdminEmailSentPageAction.class);
        map(ADMIN_EMAIL_TRASH_PAGE, AdminEmailTrashPageAction.class);
        map(ADMIN_EMAIL_TRASH_DELETE, AdminEmailTrashDeleteAction.class);
        map(ADMIN_EMAIL_DRAFT_PAGE, AdminEmailDraftPageAction.class);
        map(ADMIN_EMAIL_MOVE_TO_TRASH, AdminEmailTrashAction.class);
        map(ADMIN_EMAIL_MOVE_OUT_TRASH, AdminEmailTrashAction.class);
        map(ADMIN_EMAIL_LOG_PAGE, AdminEmailLogPageAction.class);

        map(INSTRUCTOR_COURSES_PAGE, InstructorCoursesPageAction.class);
        map(INSTRUCTOR_COURSE_STATS_PAGE, CourseStatsPageAction.class);
        map(INSTRUCTOR_COURSE_ADD, InstructorCourseAddAction.class);
        map(INSTRUCTOR_COURSE_DELETE, InstructorCourseDeleteAction.class);
        map(INSTRUCTOR_COURSE_ARCHIVE, InstructorCourseArchiveAction.class);
        map(INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_RESTORE, InstructorCourseRestoreSoftDeletedCourseAction.class);
        map(INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_RESTORE_ALL, InstructorCourseRestoreAllSoftDeletedCoursesAction.class);
        map(INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_DELETE, InstructorCourseDeleteSoftDeletedCourseAction.class);
        map(INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_DELETE_ALL, InstructorCourseDeleteAllSoftDeletedCoursesAction.class);
        map(INSTRUCTOR_COURSE_DETAILS_PAGE, InstructorCourseDetailsPageAction.class);
        map(INSTRUCTOR_COURSE_JOIN, InstructorCourseJoinAction.class);
        map(INSTRUCTOR_COURSE_JOIN_AUTHENTICATED, InstructorCourseJoinAuthenticatedAction.class);
        map(INSTRUCTOR_COURSE_REMIND, InstructorCourseRemindAction.class);
        map(INSTRUCTOR_COURSE_EDIT_PAGE, InstructorCourseEditPageAction.class);
        map(INSTRUCTOR_COURSE_EDIT_SAVE, InstructorCourseEditSaveAction.class);
        map(INSTRUCTOR_COURSE_INSTRUCTOR_ADD, InstructorCourseInstructorAddAction.class);
        map(INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE, InstructorCourseInstructorEditSaveAction.class);
        map(INSTRUCTOR_COURSE_INSTRUCTOR_DELETE, InstructorCourseInstructorDeleteAction.class);
        map(INSTRUCTOR_COURSE_ENROLL_PAGE, InstructorCourseEnrollPageAction.class);
        map(INSTRUCTOR_COURSE_ENROLL_AJAX_PAGE, InstructorCourseEnrollAjaxPageAction.class);
        map(INSTRUCTOR_COURSE_ENROLL_SAVE, InstructorCourseEnrollSaveAction.class);
        map(INSTRUCTOR_COURSE_STUDENT_DELETE, InstructorCourseStudentDeleteAction.class);
        map(INSTRUCTOR_COURSE_STUDENT_DELETE_ALL, InstructorCourseStudentDeleteAllAction.class);
        map(INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD, InstructorCourseStudentListDownloadAction.class);
        map(INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE, InstructorCourseStudentDetailsPageAction.class);
        map(INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT, InstructorCourseStudentDetailsEditPageAction.class);
        map(INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_SAVE, InstructorCourseStudentDetailsEditSaveAction.class);
        map(INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE, InstructorEditStudentFeedbackPageAction.class);
        map(INSTRUCTOR_EDIT_STUDENT_FEEDBACK_SAVE, InstructorEditStudentFeedbackSaveAction.class);
        map(INSTRUCTOR_EDIT_INSTRUCTOR_FEEDBACK_PAGE, InstructorEditInstructorFeedbackPageAction.class);
        map(INSTRUCTOR_EDIT_INSTRUCTOR_FEEDBACK_SAVE, InstructorEditInstructorFeedbackSaveAction.class);
        map(INSTRUCTOR_FEEDBACK_SESSIONS_PAGE, InstructorFeedbackSessionsPageAction.class);
        map(INSTRUCTOR_FEEDBACK_ADD, InstructorFeedbackAddAction.class);
        map(INSTRUCTOR_FEEDBACK_COPY, InstructorFeedbackCopyAction.class);
        map(INSTRUCTOR_FEEDBACK_DELETE, InstructorFeedbackDeleteAction.class);
        map(INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE, InstructorFeedbackEditCopyPageAction.class);
        map(INSTRUCTOR_FEEDBACK_EDIT_COPY, InstructorFeedbackEditCopyAction.class);
        map(INSTRUCTOR_FEEDBACK_EDIT_PAGE, InstructorFeedbackEditPageAction.class);
        map(INSTRUCTOR_FEEDBACK_EDIT_SAVE, InstructorFeedbackEditSaveAction.class);
        map(INSTRUCTOR_FEEDBACK_REMIND, InstructorFeedbackRemindAction.class);
        map(INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS_PAGE, InstructorFeedbackRemindParticularStudentsPageAction.class);
        map(INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS, InstructorFeedbackRemindParticularStudentsAction.class);
        map(INSTRUCTOR_FEEDBACK_PUBLISH, InstructorFeedbackPublishAction.class);
        map(INSTRUCTOR_FEEDBACK_RESEND_PUBLISHED_EMAIL_PAGE,
                InstructorFeedbackResendPublishedEmailPageAction.class);
        map(INSTRUCTOR_FEEDBACK_RESEND_PUBLISHED_EMAIL,
                InstructorFeedbackResendPublishedEmailAction.class);
        map(INSTRUCTOR_FEEDBACK_UNPUBLISH, InstructorFeedbackUnpublishAction.class);
        map(INSTRUCTOR_FEEDBACK_SOFT_DELETED_SESSION_RESTORE, InstructorFeedbackRestoreSoftDeletedSessionAction.class);
        map(INSTRUCTOR_FEEDBACK_SOFT_DELETED_SESSION_RESTORE_ALL,
                InstructorFeedbackRestoreAllSoftDeletedSessionsAction.class);
        map(INSTRUCTOR_FEEDBACK_SOFT_DELETED_SESSION_DELETE, InstructorFeedbackDeleteSoftDeletedSessionAction.class);
        map(INSTRUCTOR_FEEDBACK_SOFT_DELETED_SESSION_DELETE_ALL, InstructorFeedbackDeleteAllSoftDeletedSessionsAction.class);
        map(INSTRUCTOR_FEEDBACK_QUESTION_ADD, InstructorFeedbackQuestionAddAction.class);
        map(INSTRUCTOR_FEEDBACK_QUESTION_COPY_PAGE, InstructorFeedbackQuestionCopyPageAction.class);
        map(INSTRUCTOR_FEEDBACK_QUESTION_COPY, InstructorFeedbackQuestionCopyAction.class);
        map(INSTRUCTOR_FEEDBACK_QUESTION_EDIT, InstructorFeedbackQuestionEditAction.class);
        map(INSTRUCTOR_FEEDBACK_QUESTION_VISIBILITY_MESSAGE, InstructorFeedbackQuestionVisibilityMessageAction.class);
        map(INSTRUCTOR_FEEDBACK_RESULTS_PAGE, InstructorFeedbackResultsPageAction.class);
        map(INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD, InstructorFeedbackResultsDownloadAction.class);
        map(INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD, InstructorFeedbackResponseCommentAddAction.class);
        map(INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT, InstructorFeedbackResponseCommentEditAction.class);
        map(INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE, InstructorFeedbackResponseCommentDeleteAction.class);
        map(INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT, InstructorFeedbackPreviewAsStudentAction.class);
        map(INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR, InstructorFeedbackPreviewAsInstructorAction.class);
        map(INSTRUCTOR_FEEDBACK_STATS_PAGE, FeedbackSessionStatsPageAction.class);
        map(INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE, InstructorFeedbackSubmissionEditPageAction.class);
        map(INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_SAVE, InstructorFeedbackSubmissionEditSaveAction.class);
        map(INSTRUCTOR_FEEDBACK_TEMPLATE_QUESTION_ADD, InstructorFeedbackTemplateQuestionAddAction.class);
        map(INSTRUCTOR_HOME_PAGE, InstructorHomePageAction.class);
        map(INSTRUCTOR_SEARCH_PAGE, InstructorSearchPageAction.class);
        map(INSTRUCTOR_STUDENT_LIST_PAGE, InstructorStudentListPageAction.class);
        map(INSTRUCTOR_STUDENT_LIST_AJAX_PAGE, InstructorStudentListAjaxPageAction.class);
        map(INSTRUCTOR_STUDENT_RECORDS_PAGE, InstructorStudentRecordsPageAction.class);
        map(INSTRUCTOR_STUDENT_RECORDS_AJAX_PAGE, InstructorStudentRecordsAjaxPageAction.class);

        map(STUDENT_COURSE_DETAILS_PAGE, StudentCourseDetailsPageAction.class);
        map(STUDENT_COURSE_JOIN, StudentCourseJoinAction.class);
        map(STUDENT_COURSE_JOIN_NEW, StudentCourseJoinAction.class);
        map(STUDENT_COURSE_JOIN_AUTHENTICATED, StudentCourseJoinAuthenticatedAction.class);
        map(STUDENT_FEEDBACK_RESULTS_PAGE, StudentFeedbackResultsPageAction.class);
        map(STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE, StudentFeedbackSubmissionEditPageAction.class);
        map(STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE, StudentFeedbackSubmissionEditSaveAction.class);
        map(FEEDBACK_PARTICIPANT_FEEDBACK_RESPONSE_COMMENT_DELETE,
                FeedbackParticipantFeedbackResponseCommentDeleteAction.class);
        map(STUDENT_PROFILE_PAGE, StudentProfilePageAction.class);
        map(STUDENT_PROFILE_PICTURE, StudentProfilePictureAction.class);
        map(STUDENT_PROFILE_PICTURE_UPLOAD, StudentProfilePictureUploadAction.class);
        map(STUDENT_PROFILE_PICTURE_EDIT, StudentProfilePictureEditAction.class);
        map(STUDENT_PROFILE_CREATEUPLOADFORMURL, StudentProfileCreateFormUrlAction.class);
        map(STUDENT_PROFILE_EDIT_SAVE, StudentProfileEditSaveAction.class);
        map(STUDENT_HOME_PAGE, StudentHomePageAction.class);

        map(CREATE_IMAGE_UPLOAD_URL, CreateImageUploadUrlAction.class);
        map(IMAGE_UPLOAD, ImageUploadAction.class);

        map(ERROR_FEEDBACK_SUBMIT, ErrorUserReportLogAction.class);
    }

    /**
     * Returns the matching {@link Action} object for the URI in the {@code req}.
     */
    public Action getAction(HttpServletRequest req) {

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

    private static Action getAction(String uri) {
        Class<? extends Action> controllerClass = actionMappings.get(uri);

        if (controllerClass == null) {
            throw new PageNotFoundException(uri);
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
