package teammates.ui.webapi.action;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
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
     * Verifies that instructor can see the moderated question in moderation request.
     */
    protected void verifyInstructorCanSeeQuestionIfInModeration(FeedbackQuestionAttributes feedbackQuestion) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);

        if (!StringHelper.isEmpty(moderatedPerson) && !canInstructorSeeQuestion(feedbackQuestion)) {
            // should not moderate question which instructors cannot see
            throw new UnauthorizedAccessException("The question is not applicable for moderation");
        }
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
            if (userInfo == null) {
                throw new UnauthorizedAccessException("Instructor must login to access");
            }
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
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                    feedbackSession, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                    feedbackSession, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        } else {
            gateKeeper.verifySessionSubmissionPrivilegeForInstructor(feedbackSession, instructor);
        }
    }

    /**
     * Verifies that it is not a preview request.
     */
    protected void verifyNotPreview() {
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        if (!StringHelper.isEmpty(previewAsPerson)) {
            // should not view response under preview mode
            throw new UnauthorizedAccessException("Cannot get responses in preview request");
        }
    }

    /**
     * Verifies that the session is open for submission.
     *
     * <p>If it is moderation request, omit the check.
     */
    protected void verifySessionOpenExceptForModeration(FeedbackSessionAttributes feedbackSession) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);

        if (StringHelper.isEmpty(moderatedPerson) && !(feedbackSession.isOpened() || feedbackSession.isInGracePeriod())) {
            throw new UnauthorizedAccessException("The feedback session is not available for submission");
        }
    }

    /**
     * Gets the section of a recipient.
     */
    protected String getRecipientSection(
            String courseId, FeedbackParticipantType recipientType, String recipientIdentifier) {
        switch (recipientType) {
        case INSTRUCTORS:
        case SELF:
        case NONE:
            return Const.DEFAULT_SECTION;
        case TEAMS:
        case OWN_TEAM:
            return logic.getSectionForTeam(courseId, recipientIdentifier);
        case STUDENTS:
        case OWN_TEAM_MEMBERS:
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            StudentAttributes student = logic.getStudentForEmail(courseId, recipientIdentifier);
            return student == null ? Const.DEFAULT_SECTION : student.section;
        default:
            Assumption.fail("Unknown recipient type " + recipientType);
            return null;
        }
    }

    /**
     * Validates the response of the corresponding question.
     */
    protected void validResponseOfQuestion(FeedbackQuestionAttributes questionAttributes,
                                           FeedbackResponseAttributes responseToValidate) {
        List<String> questionSpecificErrors =
                responseToValidate.getResponseDetails().validateResponseDetails(questionAttributes);

        // validate the response itself
        if (!questionSpecificErrors.isEmpty()) {
            throw new InvalidHttpRequestBodyException(questionSpecificErrors.toString());
        }

        // validate responses of the question
        // TODO: implement this when other type of questions are integrated
    }

}
