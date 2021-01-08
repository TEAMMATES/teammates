package teammates.ui.webapi;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * The basic action for feedback submission.
 */
abstract class BasicFeedbackSubmissionAction extends Action {

    /**
     * Checks whether instructors can see the question.
     */
    boolean canInstructorSeeQuestion(FeedbackQuestionAttributes feedbackQuestion) {
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
    void verifyInstructorCanSeeQuestionIfInModeration(FeedbackQuestionAttributes feedbackQuestion) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);

        if (!StringHelper.isEmpty(moderatedPerson) && !canInstructorSeeQuestion(feedbackQuestion)) {
            // should not moderate question which instructors cannot see
            throw new UnauthorizedAccessException("The question is not applicable for moderation", true);
        }
    }

    /**
     * Gets the student involved in the submission process.
     */
    StudentAttributes getStudentOfCourseFromRequest(String courseId) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            return logic.getStudentForEmail(courseId, moderatedPerson);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            return logic.getStudentForEmail(courseId, previewAsPerson);
        } else {
            return getUnregisteredStudent().orElseGet(() -> {
                gateKeeper.verifyLoggedInUserPrivileges();
                return logic.getStudentForGoogleId(courseId, userInfo.getId());
            });
        }
    }

    /**
     * Checks the access control for student feedback submission.
     */
    void checkAccessControlForStudentFeedbackSubmission(
            StudentAttributes student, FeedbackSessionAttributes feedbackSession) {
        if (student == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges();
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()), feedbackSession,
                    student.getSection(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges();
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()), feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            if (!StringHelper.isEmpty(student.googleId)) {
                if (userInfo == null) {
                    // Student is associated to a google ID; even if registration key is passed, do not allow access
                    throw new UnauthorizedAccessException("Login is required to access this feedback session");
                } else if (!userInfo.id.equals(student.googleId)) {
                    // Logged in student is not the same as the student registered for the given key, do not allow access
                    throw new UnauthorizedAccessException("You are not authorized to access this feedback session");
                }
            }
            gateKeeper.verifyAccessible(student, feedbackSession);
        }
    }

    /**
     * Gets the instructor involved in the submission process.
     */
    InstructorAttributes getInstructorOfCourseFromRequest(String courseId) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            return logic.getInstructorForEmail(courseId, moderatedPerson);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            return logic.getInstructorForEmail(courseId, previewAsPerson);
        } else {
            gateKeeper.verifyLoggedInUserPrivileges();
            return logic.getInstructorForGoogleId(courseId, userInfo.getId());
        }
    }

    /**
     * Checks the access control for instructor feedback submission.
     */
    void checkAccessControlForInstructorFeedbackSubmission(
            InstructorAttributes instructor, FeedbackSessionAttributes feedbackSession) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges();
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges();
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            gateKeeper.verifySessionSubmissionPrivilegeForInstructor(feedbackSession, instructor);
        }
    }

    /**
     * Verifies that it is not a preview request.
     */
    void verifyNotPreview() {
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        if (!StringHelper.isEmpty(previewAsPerson)) {
            // should not view response under preview mode
            throw new UnauthorizedAccessException("You are not allowed to see responses when previewing", true);
        }
    }

    /**
     * Verifies that the session is open for submission.
     *
     * <p>If it is moderation request, omit the check.
     */
    void verifySessionOpenExceptForModeration(FeedbackSessionAttributes feedbackSession) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);

        if (StringHelper.isEmpty(moderatedPerson) && !(feedbackSession.isOpened() || feedbackSession.isInGracePeriod())) {
            throw new UnauthorizedAccessException("The feedback session is not available for submission", true);
        }
    }

    /**
     * Gets the section of a recipient.
     */
    String getRecipientSection(
            String courseId, FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            String recipientIdentifier) {
        switch (recipientType) {
        case SELF:
            switch (giverType) {
            case INSTRUCTORS:
            case SELF:
                return Const.DEFAULT_SECTION;
            case TEAMS:
                return logic.getSectionForTeam(courseId, recipientIdentifier);
            case STUDENTS:
                StudentAttributes student = logic.getStudentForEmail(courseId, recipientIdentifier);
                return student == null ? Const.DEFAULT_SECTION : student.section;
            default:
                Assumption.fail("Invalid giver type " + giverType + " for recipient type " + recipientType);
                return null;
            }
        case INSTRUCTORS:
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

}
