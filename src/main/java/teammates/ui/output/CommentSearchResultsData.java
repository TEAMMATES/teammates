package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;

/**
 * API output format for comment search results.
 */
public class CommentSearchResultsData extends ApiOutput {
    private final List<CommentSearchResultData> searchResults = new ArrayList<>();

    public CommentSearchResultsData(FeedbackResponseCommentSearchResultBundle bundle) {
        bundle.sessions.forEach((key, value) -> searchResults.add(new CommentSearchResultData(value, bundle)));
    }

    public List<CommentSearchResultData> getSearchResults() {
        return searchResults;
    }
}
