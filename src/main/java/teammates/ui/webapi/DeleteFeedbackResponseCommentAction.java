package teammates.ui.webapi;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.request.Intent;

/**
 * Deletes a feedback response comment.
 */
public class DeleteFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        FeedbackResponseComment comment = sqlLogic.getFeedbackResponseComment(feedbackResponseCommentId);

        String courseId;
        if (comment != null) {
            courseId = comment.getFeedbackResponse().getFeedbackQuestion().getCourseId();
        } else {
            return;
        }

        FeedbackQuestion question = comment.getFeedbackResponse().getFeedbackQuestion();
        FeedbackSession session = question.getFeedbackSession();
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getSqlStudentOfCourseFromRequest(courseId);

            gateKeeper.verifyAnswerableForStudent(question);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForStudentFeedbackSubmission(student, session);
            session = session.getCopyForUser(student.getEmail());
            verifySessionOpenExceptForModeration(session, student);
            gateKeeper.verifyOwnership(comment,
                    question.getGiverType() == FeedbackParticipantType.TEAMS
                            ? student.getTeamName() : student.getEmail());
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorAsFeedbackParticipant = getSqlInstructorOfCourseFromRequest(courseId);

            gateKeeper.verifyAnswerableForInstructor(question);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);
            session = session.getCopyForUser(instructorAsFeedbackParticipant.getEmail());
            verifySessionOpenExceptForModeration(session, instructorAsFeedbackParticipant);
            gateKeeper.verifyOwnership(comment, instructorAsFeedbackParticipant.getEmail());
            break;
        case INSTRUCTOR_RESULT:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            if (instructor == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
            }
            if (comment.getGiver().equals(instructor.getEmail())) { // giver, allowed by default
                return;
            }

            FeedbackResponse response = comment.getFeedbackResponse();
            gateKeeper.verifyAccessible(instructor, session, response.getGiverSection().getName(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, response.getRecipientSection().getName(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

    }

    @Override
    public JsonResult execute() {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseComment comment = sqlLogic.getFeedbackResponseComment(feedbackResponseCommentId);

        JsonResult successfulJsonResult = new JsonResult("Successfully deleted feedback response comment.");

        if (comment == null) {
            return successfulJsonResult;
        }

        sqlLogic.deleteFeedbackResponseComment(feedbackResponseCommentId);

        return successfulJsonResult;
    }

}
