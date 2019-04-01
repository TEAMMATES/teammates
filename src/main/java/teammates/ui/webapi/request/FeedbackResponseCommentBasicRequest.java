package teammates.ui.webapi.request;

import javax.annotation.Nullable;

/**
 * The basic request of modifying a feedback response comment.
 */
public class FeedbackResponseCommentBasicRequest extends BasicRequest {

    private String commentText;
    @Nullable
    private String showCommentTo;
    @Nullable
    private String showGiverNameTo;

    public FeedbackResponseCommentBasicRequest(String commentText, String showCommentTo, String showGiverNameTo) {
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
