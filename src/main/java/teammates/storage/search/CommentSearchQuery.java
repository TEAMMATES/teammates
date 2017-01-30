package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;

import com.google.appengine.api.search.Document;

/**
 * The SearchQuery object that defines how we query {@link Document} for student comments
 */
public class CommentSearchQuery extends SearchQuery {
    
    public CommentSearchQuery(List<InstructorAttributes> instructors, String queryString) {
        super(instructors, queryString);
    }
    
    @Override
    protected String prepareVisibilityQueryString(List<InstructorAttributes> instructors) {
        StringBuilder courseIdLimit = new StringBuilder("(");
        StringBuilder giverEmailLimit = new StringBuilder("(");
        String delim = "";
        for (InstructorAttributes ins : instructors) {
            courseIdLimit.append(delim).append(ins.courseId);
            giverEmailLimit.append(delim).append(ins.email);
            delim = OR;
        }
        courseIdLimit.append(')');
        giverEmailLimit.append(')');
        return Const.SearchDocumentField.COURSE_ID + ":" + courseIdLimit.toString()
                + AND + "(" + Const.SearchDocumentField.GIVER_EMAIL + ":" + giverEmailLimit.toString()
                          + OR + Const.SearchDocumentField.IS_VISIBLE_TO_INSTRUCTOR + ":true)";
    }
    
}
