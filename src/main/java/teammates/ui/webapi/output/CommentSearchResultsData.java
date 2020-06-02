package teammates.ui.webapi.output;

import java.util.List;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;

/**
 * API output format for comment search results.
 */
//TODO: Remove warning suppression
@SuppressWarnings("PMD")
public class CommentSearchResultsData extends ApiOutput {
    private List<CommentSearchResultData> searchResult;

    public CommentSearchResultsData(FeedbackResponseCommentSearchResultBundle bundle) {
        //TODO: Transform bundle into list of CommentSearchResultData
    }
}
