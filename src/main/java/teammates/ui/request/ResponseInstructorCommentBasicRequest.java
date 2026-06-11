package teammates.ui.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.visibility.CommentVisibilityType;
import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The basic request of modifying a feedback response comment.
 */
class ResponseInstructorCommentBasicRequest extends BasicRequest {

    private String commentText;

    private List<CommentVisibilityType> showCommentTo;
    private List<CommentVisibilityType> showGiverNameTo;

    @JsonCreator
    ResponseInstructorCommentBasicRequest(String commentText,
                                        List<CommentVisibilityType> showCommentTo,
                                        List<CommentVisibilityType> showGiverNameTo) {
        this.commentText = commentText;
        this.showCommentTo = showCommentTo;
        this.showGiverNameTo = showGiverNameTo;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(commentText != null, "Comment Text cannot be null");
        validateTrue(!commentText.trim().isEmpty(), "Comment Text cannot be empty");
    }

    public String getCommentText() {
        return commentText;
    }

    public List<CommentVisibilityType> getShowCommentTo() {
        return showCommentTo;
    }

    public List<CommentVisibilityType> getShowGiverNameTo() {
        return showGiverNameTo;
    }
}
