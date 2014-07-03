package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;

import com.google.appengine.api.search.QueryOptions;

public class CommentSearchQuery extends SearchQuery {
    public CommentSearchQuery(QueryOptions options, String googleId, String queryString) {
        setOptions(options);
        prepareVisibilityQueryString(googleId);
        setTextFilter(Const.SearchDocumentField.SEARCHABLE_TEXT, queryString);
    }
    
    private void prepareVisibilityQueryString(String googleId){
        List<InstructorAttributes> instructorRoles = InstructorsLogic.inst().getInstructorsForGoogleId(googleId);
        StringBuilder courseIdLimit = new StringBuilder("");
        StringBuilder giverEmailLimit = new StringBuilder("");
        String delim = "";
        for(InstructorAttributes ins:instructorRoles){
            courseIdLimit.append(delim).append(ins.courseId);
            giverEmailLimit.append(delim).append(ins.email);
            delim = OR;
        }
        visibilityQueryString = Const.SearchDocumentField.COURSE_ID + ":" + courseIdLimit.toString()
                + AND + "(" + Const.SearchDocumentField.GIVER_EMAIL + ":" + giverEmailLimit.toString() 
                        + OR + Const.SearchDocumentField.IS_VISIBLE_TO_INSTRUCTOR + ":true)";
    }
}
