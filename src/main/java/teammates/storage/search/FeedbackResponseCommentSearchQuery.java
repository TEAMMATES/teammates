package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;

import com.google.appengine.api.search.Document;

/**
 * The SearchQuery object that defines how we query {@link Document} for response comments
 */
public class FeedbackResponseCommentSearchQuery extends SearchQuery {
    
    public FeedbackResponseCommentSearchQuery(List<InstructorAttributes> instructors, String queryString,
                                              String cursorString) {
        super(instructors, queryString, cursorString);
    }

    @Override
    protected String prepareVisibilityQueryString(List<InstructorAttributes> instructors) {
        StringBuilder courseIdLimit = new StringBuilder("(");
        String delim = "";
        for (InstructorAttributes ins : instructors) {
            courseIdLimit.append(delim).append(ins.courseId);
            delim = OR;
        }
        courseIdLimit.append(')');
        
        //TODO: verify section
        return Const.SearchDocumentField.COURSE_ID + ":" + courseIdLimit.toString();
    }
    
}
