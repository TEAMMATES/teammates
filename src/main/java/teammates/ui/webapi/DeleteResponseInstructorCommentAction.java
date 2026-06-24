package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Deletes a feedback response comment.
 */
public class DeleteResponseInstructorCommentAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID responseInstructorCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        ResponseInstructorComment comment = logic.getResponseInstructorComment(responseInstructorCommentId);
        if (comment == null) {
            return;
        }

        String courseId = comment.getFeedbackResponse().getFeedbackQuestion().getCourseId();
        Instructor instructor = getInstructorFromRequest(courseId);
        if (instructor == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }

        if (!comment.getGiverId().equals(instructor.getId())) {
            throw new UnauthorizedAccessException(
                    "Trying to delete a feedback response comment not given by the instructor");
        }
    }

    @Override
    public JsonResult execute() {
        UUID responseInstructorCommentId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        logic.deleteResponseInstructorComment(responseInstructorCommentId);

        return new JsonResult("Successfully deleted feedback response comment.");
    }

}
