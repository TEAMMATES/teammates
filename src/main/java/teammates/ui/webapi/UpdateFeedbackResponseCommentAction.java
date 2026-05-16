package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.ResponseGiverType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.Intent;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates a feedback response comment.
 */
public class UpdateFeedbackResponseCommentAction extends BasicCommentSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.REG_KEY;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackResponseCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseComment feedbackResponseComment = logic.getFeedbackResponseComment(feedbackResponseCommentId);

        String courseId;

        if (feedbackResponseComment != null) {
            courseId = feedbackResponseComment.getFeedbackResponse().getFeedbackQuestion().getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback response comment is not found");
        }

        FeedbackResponse response = feedbackResponseComment.getFeedbackResponse();
        FeedbackQuestion question = response.getFeedbackQuestion();
        FeedbackSession session = question.getFeedbackSession();
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseFromRequest(courseId);
            if (student == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }

            gateKeeper.verifyAnswerableForStudent(question);
            verifySessionOpenExceptForModeration(session, student);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForStudentFeedbackSubmission(student, session);
            gateKeeper.verifyOwnership(feedbackResponseComment,
                    question.getGiverType() == QuestionGiverType.TEAMS
                            ? new ResponseGiver(ResponseGiverType.TEAM, student.getTeamId())
                            : new ResponseGiver(ResponseGiverType.STUDENT, student.getId()));
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorAsFeedbackParticipant = getInstructorOfCourseFromRequest(courseId);
            if (instructorAsFeedbackParticipant == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }

            gateKeeper.verifyAnswerableForInstructor(question);
            verifySessionOpenExceptForModeration(session, instructorAsFeedbackParticipant);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            verifyNotPreview();

            checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);
            gateKeeper.verifyOwnership(feedbackResponseComment,
                    new ResponseGiver(ResponseGiverType.INSTRUCTOR, instructorAsFeedbackParticipant.getId()));
            break;
        case INSTRUCTOR_RESULT:
            gateKeeper.verifyLoggedInUserPrivileges(authContext);
            Instructor instructor = logic.getInstructorByGoogleId(courseId, authContext.id());
            if (instructor == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
            }
            if (feedbackResponseComment.getGiver().equals(
                    new ResponseGiver(ResponseGiverType.INSTRUCTOR, instructor.getId()))) {
                return;
            }
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
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID feedbackResponseCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseComment feedbackResponseComment = logic.getFeedbackResponseComment(feedbackResponseCommentId);

        String courseId;

        if (feedbackResponseComment != null) {
            courseId = feedbackResponseComment.getFeedbackResponse().getFeedbackQuestion().getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback response comment is not found");
        }

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        ResponseGiver updater;

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseFromRequest(courseId);
            FeedbackQuestion question = feedbackResponseComment.getFeedbackResponse().getFeedbackQuestion();
            updater = question.getGiverType() == QuestionGiverType.TEAMS
                    ? new ResponseGiver(ResponseGiverType.TEAM, student.getTeamId())
                    : new ResponseGiver(ResponseGiverType.STUDENT, student.getId());
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorAsFeedbackParticipant = getInstructorOfCourseFromRequest(courseId);
            updater = new ResponseGiver(ResponseGiverType.INSTRUCTOR, instructorAsFeedbackParticipant.getId());
            break;
        case INSTRUCTOR_RESULT:
            Instructor instructor = logic.getInstructorByGoogleId(courseId, authContext.id());
            updater = new ResponseGiver(ResponseGiverType.INSTRUCTOR, instructor.getId());
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        FeedbackResponseCommentUpdateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentUpdateRequest.class);

        // Validate comment text
        String commentText = comment.getCommentText();
        if (commentText.trim().isEmpty()) {
            throw new InvalidHttpRequestBodyException(FEEDBACK_RESPONSE_COMMENT_EMPTY);
        }

        try {
            FeedbackResponseComment updatedFeedbackResponseComment =
                    logic.updateFeedbackResponseComment(feedbackResponseCommentId, comment, updater);
            String commentGiver = logic.resolveGiver(updatedFeedbackResponseComment.getGiver());
            String lastEditedBy = logic.resolveGiver(updatedFeedbackResponseComment.getLastEditedBy());
            return new JsonResult(new FeedbackResponseCommentData(
                    updatedFeedbackResponseComment, commentGiver, lastEditedBy));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}
