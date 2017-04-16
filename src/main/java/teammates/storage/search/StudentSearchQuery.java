package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

/**
 * The {@link SearchQuery} object that defines how we query
 * {@link com.google.appengine.api.search.Document} for students.
 */
public class StudentSearchQuery extends SearchQuery {

    public StudentSearchQuery(List<InstructorAttributes> instructors, String queryString) {
        super(instructors, queryString);
    }

    /**
     * This constructor should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by amdin to
     * search students in the whole system.
     */
    public StudentSearchQuery(String queryString) {
        super(queryString);
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

        return Const.SearchDocumentField.COURSE_ID + ":" + courseIdLimit.toString();
    }

}
