package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

/**
 * The {@link SearchQuery} object that defines how we query
 * {@link com.google.appengine.api.search.Document} for response comments.
 */
public class FeedbackResponseCommentSearchQuery extends SearchQuery {

    public FeedbackResponseCommentSearchQuery(List<InstructorAttributes> instructors, String queryString) {
        super(instructors, queryString);
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
