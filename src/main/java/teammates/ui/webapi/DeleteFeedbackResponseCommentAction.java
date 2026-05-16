package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.ResponseGiverType;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.Student;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.Intent;

/**
 * Deletes a feedback response comment.
 */
public class DeleteFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.REG_KEY;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackResponseCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        FeedbackResponseComment comment = logic.getFeedbackResponseComment(feedbackResponseCommentId);

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
            Student student = getStudentOfCourseFromRequest(courseId);

            gateKeeper.verifyAnswerableForStudent(question);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForStudentFeedbackSubmission(student, session);
            verifySessionOpenExceptForModeration(session, student);
            gateKeeper.verifyOwnership(comment,
                    question.getGiverType() == QuestionGiverType.TEAMS
                            ? new ResponseGiver(ResponseGiverType.TEAM, student.getTeamId())
                            : new ResponseGiver(ResponseGiverType.STUDENT, student.getId()));
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorAsFeedbackParticipant = getInstructorOfCourseFromRequest(courseId);

            gateKeeper.verifyAnswerableForInstructor(question);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);
            verifySessionOpenExceptForModeration(session, instructorAsFeedbackParticipant);
            gateKeeper.verifyOwnership(comment,
                    new ResponseGiver(ResponseGiverType.INSTRUCTOR, instructorAsFeedbackParticipant.getId()));
            break;
        case INSTRUCTOR_RESULT:
            gateKeeper.verifyLoggedInUserPrivileges(authContext);
            Instructor instructor = logic.getInstructorByGoogleId(courseId, authContext.id());
            if (instructor == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
            }
            if (comment.getGiver().equals(new ResponseGiver(ResponseGiverType.INSTRUCTOR, instructor.getId()))) {
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
        UUID feedbackResponseCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseComment comment = logic.getFeedbackResponseComment(feedbackResponseCommentId);

        JsonResult successfulJsonResult = new JsonResult("Successfully deleted feedback response comment.");

        if (comment == null) {
            return successfulJsonResult;
        }

        logic.deleteFeedbackResponseComment(feedbackResponseCommentId);

        return successfulJsonResult;
    }

}
