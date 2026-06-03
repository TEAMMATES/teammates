package teammates.ui.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.ui.output.CommentVisibilityType;

/**
 * The create request of a feedback response comment.
 */
public class ResponseInstructorCommentCreateRequest extends ResponseInstructorCommentBasicRequest {

    @JsonCreator
    public ResponseInstructorCommentCreateRequest(String commentText,
                                                List<CommentVisibilityType> showCommentTo,
                                                List<CommentVisibilityType> showGiverNameTo) {
        super(commentText, showCommentTo, showGiverNameTo);
    }
}
