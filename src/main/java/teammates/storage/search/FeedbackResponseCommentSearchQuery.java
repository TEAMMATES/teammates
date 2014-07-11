package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.QueryOptions;

public class FeedbackResponseCommentSearchQuery extends SearchQuery {
    public FeedbackResponseCommentSearchQuery(String googleId, String queryString, String cursorString){
        Cursor cursor = cursorString.isEmpty()
                ? Cursor.newBuilder().build()
                : Cursor.newBuilder().build(cursorString);
        
        QueryOptions options = QueryOptions.newBuilder()
                //.setLimit(10) TODO: impl pagination
                .setCursor(cursor)
                .build();
        setOptions(options);
        prepareVisibilityQueryString(googleId);
        setTextFilter(Const.SearchDocumentField.SEARCHABLE_TEXT, queryString);
    }

    private void prepareVisibilityQueryString(String googleId) {
        List<InstructorAttributes> instructorRoles = InstructorsLogic.inst().getInstructorsForGoogleId(googleId);
        StringBuilder courseIdLimit = new StringBuilder("");
        StringBuilder emailLimit = new StringBuilder("");
        String delim = "";
        for(InstructorAttributes ins:instructorRoles){
            courseIdLimit.append(delim).append(ins.courseId);
            emailLimit.append(delim).append(ins.email);
            delim = OR;
        }
        //TODO: verify section
        visibilityQueryString = Const.SearchDocumentField.COURSE_ID + ":" + courseIdLimit.toString()
                + AND + "(" + Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_GIVER_EMAIL + ":" + emailLimit.toString()
                        + OR + "(" + Const.SearchDocumentField.GIVER_EMAIL + ":" + emailLimit.toString() 
                                + AND + Const.SearchDocumentField.IS_VISIBLE_TO_GIVER + ":true)"
                        + OR + "(" + Const.SearchDocumentField.RECIPIENT_EMAIL + ":" + emailLimit.toString() 
                                + AND + Const.SearchDocumentField.IS_VISIBLE_TO_RECEIVER + ":true)"
                        + OR + Const.SearchDocumentField.IS_VISIBLE_TO_INSTRUCTOR + ":true)";
    }
}
