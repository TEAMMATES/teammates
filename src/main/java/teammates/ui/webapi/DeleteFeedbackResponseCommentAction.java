package teammates.ui.webapi;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.request.Intent;

/**
 * Deletes a feedback response comment.
 */
class DeleteFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (frc == null) {
            return;
        }
        FeedbackSessionAttributes session = getNonNullFeedbackSession(frc.getFeedbackSessionName(), frc.getCourseId());
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(frc.getFeedbackQuestionId());

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        String courseId = frc.getCourseId();

        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes student = getStudentOfCourseFromRequest(courseId);

            gateKeeper.verifyAnswerableForStudent(question);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForStudentFeedbackSubmission(student, session);
            session = session.getCopyForStudent(student.getEmail());
            verifySessionOpenExceptForModeration(session);
            gateKeeper.verifyOwnership(frc,
                    question.getGiverType() == FeedbackParticipantType.TEAMS
                            ? student.getTeam() : student.getEmail());
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAsFeedbackParticipant = getInstructorOfCourseFromRequest(courseId);

            gateKeeper.verifyAnswerableForInstructor(question);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);
            session = session.getCopyForInstructor(instructorAsFeedbackParticipant.getEmail());
            verifySessionOpenExceptForModeration(session);
            gateKeeper.verifyOwnership(frc, instructorAsFeedbackParticipant.getEmail());
            break;
        case INSTRUCTOR_RESULT:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            if (instructor == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
            }
            if (frc.getCommentGiver().equals(instructor.getEmail())) { // giver, allowed by default
                return;
            }

            FeedbackResponseAttributes response = logic.getFeedbackResponse(frc.getFeedbackResponseId());
            gateKeeper.verifyAccessible(instructor, session, response.getGiverSection(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, response.getRecipientSection(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        logic.deleteFeedbackResponseComment(feedbackResponseCommentId);

        return new JsonResult("Successfully deleted feedback response comment.");
    }

}
