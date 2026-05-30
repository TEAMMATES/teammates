package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.ResponseInstructorCommentData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.ResponseInstructorCommentUpdateRequest;

/**
 * Updates a feedback response comment.
 */
public class UpdateResponseInstructorCommentAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID responseInstructorCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        ResponseInstructorComment comment = logic.getResponseInstructorComment(responseInstructorCommentId);
        if (comment == null) {
            throw new EntityNotFoundException("Feedback response comment is not found");
        }

        String courseId = comment.getFeedbackResponse().getFeedbackQuestion().getCourseId();

        FeedbackResponse response = comment.getFeedbackResponse();
        FeedbackQuestion question = response.getFeedbackQuestion();
        FeedbackSession session = question.getFeedbackSession();

        Instructor instructor = getInstructorFromRequest(courseId);
        if (instructor == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }
        if (comment.getGiver().equals(new ResponseGiver(instructor))) {
            return;
        }
        gateKeeper.verifyAccessible(instructor, session, response.getGiver().getSectionName(),
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        gateKeeper.verifyAccessible(instructor, session, response.getRecipient().getSectionName(),
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID responseInstructorCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        Class<ResponseInstructorCommentUpdateRequest> requestClass = ResponseInstructorCommentUpdateRequest.class;
        ResponseInstructorCommentUpdateRequest updateRequest = getAndValidateRequestBody(requestClass);

        ResponseInstructorComment comment = logic.getResponseInstructorComment(responseInstructorCommentId);
        if (comment == null) {
            throw new EntityNotFoundException("Feedback response comment is not found");
        }

        String courseId = comment.getFeedbackResponse().getFeedbackQuestion().getCourseId();
        Instructor instructor = getInstructorFromRequest(courseId);
        ResponseGiver updater = new ResponseGiver(instructor);

        try {
            ResponseInstructorComment updatedResponseInstructorComment =
                    logic.updateResponseInstructorComment(responseInstructorCommentId, updateRequest, updater);
            return new JsonResult(new ResponseInstructorCommentData(updatedResponseInstructorComment));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}
