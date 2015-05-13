package teammates.common.datatransfer;

import com.google.appengine.api.search.Cursor;

public class BaseCommentSearchResultBundle extends SearchResultBundle {

    public Cursor cursor = null;
    protected int numberOfResults = 0;

    public BaseCommentSearchResultBundle() {

    }

    public int getResultSize() {
        return numberOfResults;
    }

}
