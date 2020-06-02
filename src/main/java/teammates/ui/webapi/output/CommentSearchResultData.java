package teammates.ui.webapi.output;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;

/**
 * API output format for a comment search result.
 */
public class CommentSearchResultData extends SessionResultsData {
    private final FeedbackSessionData feedbackSession;

    public CommentSearchResultData(FeedbackSessionResultsBundle bundle) {
        super(bundle);
        feedbackSession = new FeedbackSessionData(bundle.feedbackSession);
    }
}
