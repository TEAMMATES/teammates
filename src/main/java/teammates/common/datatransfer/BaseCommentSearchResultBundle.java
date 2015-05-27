package teammates.common.datatransfer;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

public abstract class BaseCommentSearchResultBundle extends SearchResultBundle {

    public Cursor cursor = null;
    protected int numberOfResults = 0;

    public BaseCommentSearchResultBundle() {

    }
    
    public abstract BaseCommentSearchResultBundle fromResults(Results<ScoredDocument> results, String googleId);
    
    public int getResultSize() {
        return numberOfResults;
    }

}
