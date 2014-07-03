package teammates.storage.search;

import teammates.common.util.Const;

import com.google.appengine.api.search.QueryOptions;

public class CommentSearchQuery extends SearchQuery {
    public CommentSearchQuery(QueryOptions options, String googleId, String queryString) {
        setOptions(options);
        visibilityQueryString = "whoCanSee:" + googleId;
        setTextFilter(Const.SearchDocumentField.SEARCHABLE_TEXT, queryString);
    }
}
