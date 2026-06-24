package teammates.ui.request;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The create request of a feedback response comment.
 */
public class ResponseInstructorCommentCreateRequest extends ResponseInstructorCommentBasicRequest {

    @JsonCreator
    public ResponseInstructorCommentCreateRequest(String commentText) {
        super(commentText);
    }
}
