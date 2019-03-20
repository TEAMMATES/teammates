package teammates.ui.webapi.request;

import javax.annotation.Nullable;

/**
 * The update request of a feedback response comment.
 */
public class FeedbackResponseCommentUpdateRequest extends BasicRequest {

    private String commentText;
    @Nullable
    private String showCommentTo;
    @Nullable
    private String showGiverNameTo;

    public FeedbackResponseCommentUpdateRequest(String commentText, String showCommentTo, String showGiverNameTo) {
        this.commentText = commentText;
        this.showCommentTo = showCommentTo;
        this.showGiverNameTo = showGiverNameTo;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getShowCommentTo() {
        return showCommentTo;
    }

    public String getShowGiverNameTo() {
        return showGiverNameTo;
    }

    @Override
    public void validate() {
        // TODO decide what to validate
    }

}
