package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;

public class StudentSearchQuery extends SearchQuery {
    
    public StudentSearchQuery(List<InstructorAttributes> instructors, String queryString, String cursorString) {
        super(instructors, queryString, cursorString);
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
        super(queryString, cursorString);
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
