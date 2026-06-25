package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.ResponseInstructorCommentData;
import teammates.ui.request.ResponseInstructorCommentUpdateRequest;

/**
 * Updates a feedback response comment.
 */
public class UpdateResponseInstructorCommentAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID responseInstructorCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        ResponseInstructorComment comment = logic.getResponseInstructorComment(responseInstructorCommentId);
        if (comment == null) {
            throw new EntityNotFoundException("Feedback response comment is not found");
        }

        String courseId = comment.getFeedbackResponse().getFeedbackQuestion().getCourseId();

        Instructor instructor = getInstructorFromRequest(courseId);
        if (instructor == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }
        if (!comment.getGiverId().equals(instructor.getId())) {
            throw new UnauthorizedAccessException(
                    "Trying to update a feedback response comment not given by the instructor");
        }
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

        try {
            ResponseInstructorComment updatedResponseInstructorComment =
                    logic.updateResponseInstructorComment(responseInstructorCommentId, updateRequest, instructor);
            return new JsonResult(new ResponseInstructorCommentData(updatedResponseInstructorComment));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}
