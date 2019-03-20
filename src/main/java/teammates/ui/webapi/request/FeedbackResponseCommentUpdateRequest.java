package teammates.ui.webapi.request;

/**
 * The update request of a feedback response comment.
 */
public class FeedbackResponseCommentUpdateRequest extends FeedbackResponseCommentBasicRequest {

    public FeedbackResponseCommentUpdateRequest(String commentText, String showCommentTo, String showGiverNameTo) {
        super(commentText, showCommentTo, showGiverNameTo);
    }
}
