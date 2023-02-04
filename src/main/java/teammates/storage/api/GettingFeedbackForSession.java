package teammates.storage.api;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;

public class GettingFeedbackForSession {
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSession(
        String courseId, String feedbackSessionName) {
    assert courseId != null;
    assert feedbackSessionName != null;

    return makeAttributes(getFeedbackResponseCommentsForSession(courseId, feedbackSessionName));
}

    private List<FeedbackResponseCommentAttributes> makeAttributes(
            List<FeedbackResponseCommentAttributes> feedbackResponseCommentsForSession) {
        return null;
    }
    
}
