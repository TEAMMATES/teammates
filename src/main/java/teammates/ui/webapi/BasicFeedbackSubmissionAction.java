package teammates.ui.webapi;

import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * The basic action for feedback submission.
 */
abstract class BasicFeedbackSubmissionAction extends Action {
    /**
     * Checks whether instructors can see the question.
     */
    boolean canInstructorSeeQuestion(FeedbackQuestion feedbackQuestion) {
        boolean isGiverVisibleToInstructor =
                feedbackQuestion.getShowGiverNameTo().contains(ViewerType.INSTRUCTORS);
        boolean isRecipientVisibleToInstructor =
                feedbackQuestion.getShowRecipientNameTo().contains(ViewerType.INSTRUCTORS);
        boolean isResponseVisibleToInstructor =
                feedbackQuestion.getShowResponsesTo().contains(ViewerType.INSTRUCTORS);
        return isResponseVisibleToInstructor && isGiverVisibleToInstructor && isRecipientVisibleToInstructor;
    }

    /**
     * Verifies that instructor can see the moderated question in moderation request.
     */
    void verifyInstructorCanSeeQuestionIfInModeration(FeedbackQuestion feedbackQuestion)
            throws UnauthorizedAccessException {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);

        if (!StringHelper.isEmpty(moderatedPerson) && !canInstructorSeeQuestion(feedbackQuestion)) {
            // should not moderate question which instructors cannot see
            throw new UnauthorizedAccessException("The question is not applicable for moderation", true);
        }
    }

    /**
     * Gets the student of the course for submission.
     *
     * <p>This includes the student being moderated or previewed, if applicable.
     */
    Student getStudentOfCourseForSubmission(String courseId, boolean isPreviewAllowed) {
        UUID moderatedPerson = getNullableUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        UUID previewAsPerson = getNullableUuidRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (moderatedPerson != null) {
            return logic.getStudentOfCourse(courseId, moderatedPerson);
        } else if (previewAsPerson != null && isPreviewAllowed) {
            return logic.getStudentOfCourse(courseId, previewAsPerson);
        } else {
            return getStudentFromRequest(courseId);
        }
    }

    /**
     * Gets the instructor of the course for submission.
     *
     * <p>This includes the instructor being moderated or previewed, if applicable.
     */
    Instructor getInstructorOfCourseForSubmission(String courseId, boolean isPreviewAllowed) {
        UUID moderatedPerson = getNullableUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        UUID previewAsPerson = getNullableUuidRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (moderatedPerson != null) {
            return logic.getInstructorOfCourse(courseId, moderatedPerson);
        } else if (previewAsPerson != null && isPreviewAllowed) {
            return logic.getInstructorOfCourse(courseId, previewAsPerson);
        } else {
            return getInstructorFromRequest(courseId);
        }
    }

    /**
     * Gets the student of the course for result.
     *
     * <p>This includes the student being previewed, if applicable.
     */
    Student getStudentOfCourseForResult(String courseId) {
        UUID previewAsPerson = getNullableUuidRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (previewAsPerson != null) {
            return logic.getStudentOfCourse(courseId, previewAsPerson);
        } else {
            return getStudentFromRequest(courseId);
        }
    }

    /**
     * Gets the instructor of the course for result.
     *
     * <p>This includes the instructor being previewed, if applicable.
     */
    Instructor getInstructorOfCourseForResult(String courseId) {
        UUID previewAsPerson = getNullableUuidRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (previewAsPerson != null) {
            return logic.getInstructorOfCourse(courseId, previewAsPerson);
        } else {
            return getInstructorFromRequest(courseId);
        }
    }

    /**
     * Checks the access control for student feedback submission.
     */
    void checkAccessControlForStudentFeedbackSubmission(Student student, FeedbackSession feedbackSession)
            throws UnauthorizedAccessException {
        if (student == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(requestContext);
            gateKeeper.verifyInstructorHasPrivilegeForSection(requestContext, feedbackSession.getCourseId(),
                    student.getSectionName(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            checkAccessControlForPreview(feedbackSession);
        } else {
            gateKeeper.verifyStudentInCourse(requestContext, feedbackSession.getCourseId());
            if (!feedbackSession.isVisible()) {
                throw new UnauthorizedAccessException("This feedback session is not yet visible.", true);
            }
        }
    }

    /**
     * Checks the access control for student feedback result.
     */
    void checkAccessControlForStudentFeedbackResult(
            Student student, FeedbackSession feedbackSession) throws UnauthorizedAccessException {
        if (student == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
        }

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyStudentInCourse(requestContext, feedbackSession.getCourseId());
            if (!feedbackSession.isVisible()) {
                throw new UnauthorizedAccessException("This feedback session is not yet visible.", true);
            }
        } else {
            checkAccessControlForPreview(feedbackSession);
        }
    }

    /**
     * Checks the access control for instructor feedback submission.
     */
    void checkAccessControlForInstructorFeedbackSubmission(
            Instructor instructor, FeedbackSession feedbackSession) throws UnauthorizedAccessException {
        if (instructor == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(requestContext);
            gateKeeper.verifyInstructorHasPrivilege(requestContext, feedbackSession.getCourseId(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(requestContext);
            gateKeeper.verifyInstructorHasPrivilege(requestContext, feedbackSession.getCourseId(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            gateKeeper.verifySessionSubmissionPrivilegeForInstructor(feedbackSession, instructor);
        }
    }

    /**
     * Checks the access control for instructor feedback result.
     */
    void checkAccessControlForInstructorFeedbackResult(
            Instructor instructor, FeedbackSession feedbackSession) throws UnauthorizedAccessException {
        if (instructor == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyInstructorHasPrivilege(requestContext, feedbackSession.getCourseId(),
                    Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
        } else {
            checkAccessControlForPreview(feedbackSession);
        }
    }

    private void checkAccessControlForPreview(FeedbackSession feedbackSession)
            throws UnauthorizedAccessException {
        gateKeeper.verifyLoggedInUserPrivileges(requestContext);
        gateKeeper.verifyInstructorHasPrivilege(requestContext, feedbackSession.getCourseId(),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    /**
     * Verifies that the session is open for submission.
     *
     * <p>If it is moderation request, omit the check.
     */
    void verifySessionOpenExceptForModeration(FeedbackSession feedbackSession, User user)
            throws UnauthorizedAccessException {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        Instant deadlineExtension = logic.getDeadlineForUser(feedbackSession, user);
        if (StringHelper.isEmpty(moderatedPerson) && !(feedbackSession.isOpenedGivenExtendedDeadline(deadlineExtension)
                || feedbackSession.isInGracePeriodGivenExtendedDeadline(deadlineExtension))) {
            throw new UnauthorizedAccessException("The feedback session is not available for submission", true);
        }
    }
}
