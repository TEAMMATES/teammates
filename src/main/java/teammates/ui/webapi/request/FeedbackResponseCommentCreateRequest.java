package teammates.ui.webapi.request;

/**
 * The create request of a feedback response comment.
 */
public class FeedbackResponseCommentCreateRequest extends FeedbackResponseCommentBasicRequest {

    public FeedbackResponseCommentCreateRequest(String commentText, String showCommentTo, String showGiverNameTo) {
        super(commentText, showCommentTo, showGiverNameTo);
    }
}
