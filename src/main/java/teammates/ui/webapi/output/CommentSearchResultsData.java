package teammates.ui.webapi.output;

import java.util.List;

/**
 * API output format for comment search results.
 */
public class CommentSearchResultsData extends ApiOutput {
    private final List<CommentSearchResultData> searchResult;

    public CommentSearchResultsData(List<CommentSearchResultData> searchResult) {
        this.searchResult = searchResult;
    }
}
