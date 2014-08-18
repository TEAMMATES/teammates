package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.QueryOptions;

/**
 * The SearchQuery object that defines how we query {@link Document} for response comments
 */
public class FeedbackResponseCommentSearchQuery extends SearchQuery {
    public FeedbackResponseCommentSearchQuery(String googleId, String queryString, String cursorString){
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

    private void prepareVisibilityQueryString(String googleId) {
        List<InstructorAttributes> instructorRoles = InstructorsLogic.inst().getInstructorsForGoogleId(googleId);
        StringBuilder courseIdLimit = new StringBuilder("(");
        String delim = "";
        for(InstructorAttributes ins:instructorRoles){
            courseIdLimit.append(delim).append(ins.courseId);
            delim = OR;
        }
        courseIdLimit.append(")");
        
        //TODO: verify section
        visibilityQueryString = Const.SearchDocumentField.COURSE_ID + ":" + courseIdLimit.toString();
    }
}
