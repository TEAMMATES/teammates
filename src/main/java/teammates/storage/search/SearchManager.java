package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.SearchNotImplementedException;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;

/**
 * Acts as a proxy to search service.
 */
public final class SearchManager {

    private static final Logger log = Logger.getLogger();

    private String searchServiceHost;

    public SearchManager(String searchServiceHost) {
        this.searchServiceHost = searchServiceHost;
    }

    private boolean isSearchServiceActive() {
        return !StringHelper.isEmpty(searchServiceHost);
    }

    /**
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public StudentSearchResultBundle searchStudents(String queryString, List<InstructorAttributes> instructors)
            throws SearchNotImplementedException {
        if (!isSearchServiceActive()) {
            throw new SearchNotImplementedException();
        }
        // TODO
        throw new SearchNotImplementedException();
    }

    /**
     * Batch creates or updates search documents for the given students.
     */
    public void putStudentSearchDocuments(StudentAttributes... students) {
        if (!isSearchServiceActive()) {
            log.severe("Search service is not implemented");
            return;
        }
        // TODO
    }

    /**
     * Removes student search documents based on the given keys.
     */
    public void deleteStudentSearchDocuments(String... keys) {
        if (!isSearchServiceActive()) {
            log.severe("Search service is not implemented");
            return;
        }
        // TODO
    }

    /**
     * Searches for instructors.
     */
    public InstructorSearchResultBundle searchInstructors(String queryString) throws SearchNotImplementedException {
        if (!isSearchServiceActive()) {
            throw new SearchNotImplementedException();
        }
        // TODO
        throw new SearchNotImplementedException();
    }

    /**
     * Batch creates or updates search documents for the given instructors.
     */
    public void putInstructorSearchDocuments(InstructorAttributes... instructors) {
        if (!isSearchServiceActive()) {
            log.severe("Search service is not implemented");
            return;
        }
        // TODO
    }

    /**
     * Removes instructor search documents based on the given keys.
     */
    public void deleteInstructorSearchDocuments(String... keys) {
        if (!isSearchServiceActive()) {
            log.severe("Search service is not implemented");
            return;
        }
        // TODO
    }

}
