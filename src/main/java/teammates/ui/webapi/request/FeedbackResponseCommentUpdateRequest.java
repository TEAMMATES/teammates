package teammates.ui.webapi.request;

import java.util.List;

import teammates.ui.webapi.output.CommentVisibilityType;

/**
 * The update request of a feedback response comment.
 */
public class FeedbackResponseCommentUpdateRequest extends FeedbackResponseCommentBasicRequest {

    public FeedbackResponseCommentUpdateRequest(String commentText,
                                                List<CommentVisibilityType> showCommentTo,
                                                List<CommentVisibilityType> showGiverNameTo) {
        super(commentText, showCommentTo, showGiverNameTo);
    }
}
