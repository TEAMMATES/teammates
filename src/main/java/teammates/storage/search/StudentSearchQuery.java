package teammates.storage.search;

import java.util.List;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.MatchScorer;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.SortOptions;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;

public class StudentSearchQuery extends SearchQuery {
    
    public StudentSearchQuery(List<InstructorAttributes> instructors, String queryString, String cursorString) {
        Cursor cursor = cursorString.isEmpty()
                ? Cursor.newBuilder().build()
                : Cursor.newBuilder().build(cursorString);
        SortOptions sortOptions = SortOptions.newBuilder()
                .setMatchScorer(MatchScorer.newBuilder())
                .build();
        QueryOptions options = QueryOptions.newBuilder()
                .setLimit(20)
                .setSortOptions(sortOptions)
                .setCursor(cursor)
                .build();
        setOptions(options);
        prepareVisibilityQueryString(instructors);
        setTextFilter(Const.SearchDocumentField.SEARCHABLE_TEXT, queryString);
    }
    
    /**
     * This constructor should be used by admin only since the searching does not restrict the 
     * visibility according to the logged-in user's google ID. This is used by amdin to
     * search students in the whole system.
     * @param queryString
     * @param cursorString
     * @return admin's StudentSearchQuery with visibilityQueryString to be empty
     */
    public StudentSearchQuery(String queryString, String cursorString) {
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
    
    private void prepareVisibilityQueryString(List<InstructorAttributes> instructors) {
        StringBuilder courseIdLimit = new StringBuilder("(");
        String delim = "";
        for(InstructorAttributes ins:instructors){
            courseIdLimit.append(delim).append(ins.courseId);
            delim = OR;
        }
        courseIdLimit.append(")");

        visibilityQueryString = Const.SearchDocumentField.COURSE_ID + ":"+ courseIdLimit.toString();
    }
}
