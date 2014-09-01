package teammates.storage.search;

import teammates.common.util.Const;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.QueryOptions;

public class InstructorSearchQuery extends SearchQuery {

    /**
     * This constructor should be used by admin only since the searching does not restrict the 
     * visibility according to the logged-in user's google ID. This is used by amdin to
     * search instructors in the whole system.
     * @param queryString
     * @param cursorString
     * @return admin's InstructorSearchQuery with visibilityQueryString to be empty
     */
    public InstructorSearchQuery(String queryString, String cursorString) {
        Cursor cursor = cursorString.isEmpty()
                ? Cursor.newBuilder().build()
                : Cursor.newBuilder().build(cursorString);
        QueryOptions options = QueryOptions.newBuilder()
                .setLimit(20)
                .setCursor(cursor)
                .build();
        setOptions(options);
        visibilityQueryString = "";
        setTextFilter(Const.SearchDocumentField.SEARCHABLE_TEXT, queryString);
    }
    
    
    
}
