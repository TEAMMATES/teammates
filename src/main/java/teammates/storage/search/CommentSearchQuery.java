package teammates.storage.search;

import com.google.appengine.api.search.QueryOptions;

public class CommentSearchQuery extends SearchQuery {
    public CommentSearchQuery(QueryOptions options, String googleId, String queryString) {
        super(options);
        visibilityQueryString = "whoCanSee:" + googleId;
        setTextFilter("searchableText", queryString);
    }
}
