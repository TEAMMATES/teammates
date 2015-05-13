package teammates.storage.search;

import teammates.common.util.Const;

import com.google.appengine.api.search.Document;

/**
 * The SearchQuery object that defines how we query {@link Document} for response comments
 */
public class FeedbackResponseCommentSearchQuery extends BaseCommentSearchQuery {

    public FeedbackResponseCommentSearchQuery(String googleId, String queryString, String cursorString) {
        super(googleId, queryString, cursorString);
    }

    protected StringBuilder prepareVisibilityQueryString(String googleId) {
        StringBuilder courseIdLimit = super.prepareVisibilityQueryString(googleId);
        
        //TODO: verify section
        visibilityQueryString = Const.SearchDocumentField.COURSE_ID + ":" + courseIdLimit.toString();
        return null;
    }

}
