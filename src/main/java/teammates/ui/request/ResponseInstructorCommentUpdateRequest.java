package teammates.ui.request;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The update request of a feedback response comment.
 */
public class ResponseInstructorCommentUpdateRequest extends ResponseInstructorCommentBasicRequest {

    @JsonCreator
    public ResponseInstructorCommentUpdateRequest(String commentText) {
        super(commentText);
    }
}
