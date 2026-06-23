package teammates.ui.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The basic request of modifying a feedback response comment.
 */
class ResponseInstructorCommentBasicRequest extends BasicRequest {

    private String commentText;

    @JsonCreator
    ResponseInstructorCommentBasicRequest(String commentText) {
        this.commentText = commentText;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(commentText != null, "Comment Text cannot be null");
        validateTrue(!commentText.trim().isEmpty(), "Comment Text cannot be empty");
    }

    public String getCommentText() {
        return commentText;
    }
}
