package teammates.common.datatransfer;

import com.google.appengine.api.search.Cursor;

/**
 * The basic search result bundle object.
 */
public class SearchResultBundle {
    
    public Cursor cursor;
    public int numberOfResults;
    
    protected SearchResultBundle() {
        // prevents instantiation; to be instantiated as children classes
    }
    
}
