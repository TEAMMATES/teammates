package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;

public class InstructorSearchQuery extends SearchQuery {

    /**
     * This constructor should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by amdin to
     * search instructors in the whole system.
     * @param queryString
     * @return admin's InstructorSearchQuery with visibilityQueryString to be empty
     */
    public InstructorSearchQuery(String queryString) {
        super(queryString);
    }
    
    @Override
    protected String prepareVisibilityQueryString(List<InstructorAttributes> instructors) {
        return null; // method not used
    }
    
}
