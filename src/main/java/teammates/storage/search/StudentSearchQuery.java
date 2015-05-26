package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.QueryOptions;

public class StudentSearchQuery extends SearchQuery {
    
    public StudentSearchQuery(String googleId, String queryString, String cursorString) {
        Cursor cursor = cursorString.isEmpty()
                ? Cursor.newBuilder().build()
                : Cursor.newBuilder().build(cursorString);
        QueryOptions options = QueryOptions.newBuilder()
                .setLimit(20)
                .setCursor(cursor)
                .build();
        setOptions(options);
        prepareVisibilityQueryString(googleId);
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
    
    private void prepareVisibilityQueryString(String googleId){
        List<InstructorAttributes> instructorRoles = InstructorsLogic.inst().getInstructorsForGoogleId(googleId);
        
        StringBuilder courseIdLimit = new StringBuilder("(");
        String delim = "";
        for(InstructorAttributes ins:instructorRoles){
            courseIdLimit.append(delim).append(ins.courseId);
            delim = OR;
        }
        courseIdLimit.append(")");

        visibilityQueryString = Const.SearchDocumentField.COURSE_ID + ":"+ courseIdLimit.toString();
    }
}
