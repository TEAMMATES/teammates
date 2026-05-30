package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates a feedback response comment.
 */
public class UpdateFeedbackResponseCommentAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackResponseCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseComment feedbackResponseComment = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (feedbackResponseComment == null) {
            throw new EntityNotFoundException("Feedback response comment is not found");
        }

        String courseId = feedbackResponseComment.getFeedbackResponse().getFeedbackQuestion().getCourseId();

        FeedbackResponse response = feedbackResponseComment.getFeedbackResponse();
        FeedbackQuestion question = response.getFeedbackQuestion();
        FeedbackSession session = question.getFeedbackSession();

        Instructor instructor = getInstructorFromRequest(courseId);
        if (instructor == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }
        if (feedbackResponseComment.getGiver().equals(new ResponseGiver(instructor))) {
            return;
        }
        gateKeeper.verifyAccessible(instructor, session, response.getGiver().getSectionName(),
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        gateKeeper.verifyAccessible(instructor, session, response.getRecipient().getSectionName(),
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID feedbackResponseCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        FeedbackResponseCommentUpdateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentUpdateRequest.class);

        FeedbackResponseComment feedbackResponseComment = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (feedbackResponseComment == null) {
            throw new EntityNotFoundException("Feedback response comment is not found");
        }

        String courseId = feedbackResponseComment.getFeedbackResponse().getFeedbackQuestion().getCourseId();
        Instructor instructor = getInstructorFromRequest(courseId);
        ResponseGiver updater = new ResponseGiver(instructor);

        try {
            FeedbackResponseComment updatedFeedbackResponseComment =
                    logic.updateFeedbackResponseComment(feedbackResponseCommentId, comment, updater);
            return new JsonResult(new FeedbackResponseCommentData(updatedFeedbackResponseComment));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}
