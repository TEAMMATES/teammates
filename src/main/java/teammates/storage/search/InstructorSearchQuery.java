package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The {@link SearchQuery} object that defines how we query
 * {@link com.google.appengine.api.search.Document} for instructors.
 */
public class InstructorSearchQuery extends SearchQuery {

    /**
     * This constructor should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by amdin to
     * search instructors in the whole system.
     */
    public InstructorSearchQuery(String queryString) {
        super(queryString);
    }

    @Override
    protected String prepareVisibilityQueryString(List<InstructorAttributes> instructors) {
        return null; // method not used
    }

}
