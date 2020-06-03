package teammates.ui.webapi.output;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * API output format for a comment search result.
 */
public class CommentSearchResultData extends SessionResultsData {
    private final FeedbackSessionData feedbackSession;

    public CommentSearchResultData(FeedbackSessionAttributes session,
                                   FeedbackResponseCommentSearchResultBundle bundle) {
        super();
        feedbackSession = new FeedbackSessionData(session);
        // TODO:
        //  1) Build QuestionOutput,
        //  2) Build List<ResponseOutput> add to QuestionOutput,
        //  3) Build List<CommentOutput> pass to constructor of ResponseOutput
    }

    public FeedbackSessionData getFeedbackSession() {
        return feedbackSession;
    }
}
