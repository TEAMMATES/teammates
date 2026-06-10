package teammates.ui.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.visibility.CommentVisibilityType;

/**
 * The update request of a feedback response comment.
 */
public class ResponseInstructorCommentUpdateRequest extends ResponseInstructorCommentBasicRequest {

    @JsonCreator
    public ResponseInstructorCommentUpdateRequest(String commentText,
                                                List<CommentVisibilityType> showCommentTo,
                                                List<CommentVisibilityType> showGiverNameTo) {
        super(commentText, showCommentTo, showGiverNameTo);
    }
}
