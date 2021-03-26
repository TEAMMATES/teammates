package teammates.storage.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.SearchNotImplementedException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;

/**
 * Acts as a proxy to search service.
 */
public final class SearchManager {

    private static final String ERROR_SEARCH_NOT_IMPLEMENTED =
            "Search service is not implemented";
    private static final String ERROR_PUT_DOCUMENT =
            "Failed to put document(s) %s into Solr. Root cause: %s ";
    private static final String STUDENT_COLLECTION_NAME = "students";
    private static final Logger log = Logger.getLogger();

    private String searchServiceHost;

    public SearchManager(String searchServiceHost) {
        this.searchServiceHost = searchServiceHost;
    }

    private boolean isSearchServiceActive() {
        return !StringHelper.isEmpty(searchServiceHost);
    }

    private HttpSolrClient getSolrClient() {
        return new HttpSolrClient.Builder(searchServiceHost).build();
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
            log.severe(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }

        SolrClient client = getSolrClient();

        List<SolrInputDocument> studentDocs = new ArrayList<>();

        for (StudentAttributes student : students) {
            SolrInputDocument studentDoc = new StudentSearchDocument(student).build();
            studentDocs.add(studentDoc);
        }

        addDocumentsToCollection(client, studentDocs, STUDENT_COLLECTION_NAME);
    }

    /**
     * Removes student search documents based on the given keys.
     */
    public void deleteStudentSearchDocuments(String... keys) {
        if (!isSearchServiceActive()) {
            log.severe(ERROR_SEARCH_NOT_IMPLEMENTED);
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
            log.severe(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }
        // TODO
    }

    /**
     * Removes instructor search documents based on the given keys.
     */
    public void deleteInstructorSearchDocuments(String... keys) {
        if (!isSearchServiceActive()) {
            log.severe(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }
        // TODO
    }

    private void addDocumentsToCollection(SolrClient client, List<SolrInputDocument> docs,
                                          String collectionName) {
        try {
            client.add(collectionName, docs);
            client.commit(collectionName);
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_PUT_DOCUMENT, docs, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_PUT_DOCUMENT, docs, e.getCause())
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

}
