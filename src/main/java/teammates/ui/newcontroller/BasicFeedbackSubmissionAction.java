package teammates.ui.newcontroller;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * The basic action for feedback submission.
 */
public abstract class BasicFeedbackSubmissionAction extends Action {

    /**
     * Checks whether instructors can see the question.
     */
    protected boolean canInstructorSeeQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        boolean isGiverVisibleToInstructor =
                feedbackQuestion.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isRecipientVisibleToInstructor =
                feedbackQuestion.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isResponseVisibleToInstructor =
                feedbackQuestion.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS);
        return isResponseVisibleToInstructor && isGiverVisibleToInstructor && isRecipientVisibleToInstructor;
    }

    /**
     * Gets the student involved in the submission process.
     */
    protected StudentAttributes getStudentOfCourseFromRequest(String courseId) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            return logic.getStudentForEmail(courseId, moderatedPerson);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            return logic.getStudentForEmail(courseId, previewAsPerson);
        } else {
            return getUnregisteredStudent().orElseGet(() -> logic.getStudentForGoogleId(courseId, userInfo.getId()));
        }
    }

    /**
     * Checks the access control for student feedback submission.
     */
    protected void checkAccessControlForStudentFeedbackSubmission(
            StudentAttributes student, FeedbackSessionAttributes feedbackSession) {
        if (student == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Student does not exist"));
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()), feedbackSession,
                    student.getSection(),
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()), feedbackSession,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        } else {
            gateKeeper.verifyAccessible(student, feedbackSession);
        }
    }

    /**
     * Gets the instructor involved in the submission process.
     */
    protected InstructorAttributes getInstructorOfCourseFromRequest(String courseId) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            return logic.getInstructorForEmail(courseId, moderatedPerson);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            return logic.getInstructorForEmail(courseId, previewAsPerson);
        } else {
            return logic.getInstructorForGoogleId(courseId, userInfo.getId());
        }
    }

    /**
     * Checks the access control for instructor feedback submission.
     */
    protected void checkAccessControlForInstructorFeedbackSubmission(
            InstructorAttributes instructor, FeedbackSessionAttributes feedbackSession) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            gateKeeper.verifyAccessible(instructor, feedbackSession,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyAccessible(instructor, feedbackSession,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        } else {
            gateKeeper.verifySessionSubmissionPrivilegeForInstructor(feedbackSession, instructor);
        }
    }
}
