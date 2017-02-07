package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;

import com.google.appengine.api.search.Document;

/**
 * The {@link SearchQuery} object that defines how we query {@link Document} for instructors.
 */
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
