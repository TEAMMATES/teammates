package teammates.storage.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
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

    private static final Logger log = Logger.getLogger();

    private static final String ERROR_DELETE_DOCUMENT =
            "Failed to delete document(s) %s in Solr. Root cause: %s ";
    private static final String ERROR_SEARCH_DOCUMENT =
            "Failed to search for document(s) %s from Solr. Root cause: %s ";
    private static final String ERROR_SEARCH_NOT_IMPLEMENTED =
            "Search service is not implemented";
    private static final String ERROR_PUT_DOCUMENT =
            "Failed to put document(s) %s into Solr. Root cause: %s ";
    private static final String STUDENT_COLLECTION_NAME = "students";
    private static final String INSTRUCTOR_COLLECTION_NAME = "instructors";

    private static final String ID = "id";

    private String searchServiceHost;
    private HttpSolrClient client;

    public SearchManager(String searchServiceHost) {
        this.searchServiceHost = searchServiceHost;
    }

    private boolean isSearchServiceActive() {
        return !StringHelper.isEmpty(searchServiceHost);
    }

    private HttpSolrClient getSolrClient() {
        if (client == null) {
            client = new HttpSolrClient.Builder(searchServiceHost).build();
        }
        return client;
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

        SolrClient client = getSolrClient();
        QueryResponse response = null;
        SolrQuery studentQuery = new SolrQuery();
        studentQuery.setQuery(queryString);

        try {
            response = client.query(STUDENT_COLLECTION_NAME, studentQuery);
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_SEARCH_DOCUMENT, queryString, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_SEARCH_DOCUMENT, queryString, e.getCause())
                    + TeammatesException.toStringWithStackTrace(e));
        }

        return StudentSearchDocument.fromResponse(response, instructors);
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

        SolrClient client = getSolrClient();
        String batchQueryString = ID + ":" + prepareBatchQueryString(keys);

        try {
            client.deleteByQuery(STUDENT_COLLECTION_NAME, batchQueryString);
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_DELETE_DOCUMENT, batchQueryString, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_DELETE_DOCUMENT, batchQueryString, e.getCause())
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

    /**
     * Searches for instructors.
     */
    public InstructorSearchResultBundle searchInstructors(String queryString) throws SearchNotImplementedException {
        if (!isSearchServiceActive()) {
            throw new SearchNotImplementedException();
        }

        SolrClient client = getSolrClient();
        QueryResponse response = null;
        SolrQuery instructorQuery = new SolrQuery();
        instructorQuery.setQuery(queryString);

        try {
            response = client.query(INSTRUCTOR_COLLECTION_NAME, instructorQuery);
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_SEARCH_DOCUMENT, queryString, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_SEARCH_DOCUMENT, queryString, e.getCause())
                    + TeammatesException.toStringWithStackTrace(e));
        }

        return InstructorSearchDocument.fromResponse(response);
    }

    /**
     * Batch creates or updates search documents for the given instructors.
     */
    public void putInstructorSearchDocuments(InstructorAttributes... instructors) {
        if (!isSearchServiceActive()) {
            log.severe(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }

        SolrClient client = getSolrClient();
        List<SolrInputDocument> instructorDocs = new ArrayList<>();

        for (InstructorAttributes instructor : instructors) {
            SolrInputDocument instructorDoc = new InstructorSearchDocument(instructor).build();
            instructorDocs.add(instructorDoc);
        }

        addDocumentsToCollection(client, instructorDocs, INSTRUCTOR_COLLECTION_NAME);
    }

    /**
     * Removes instructor search documents based on the given keys.
     */
    public void deleteInstructorSearchDocuments(String... keys) {
        if (!isSearchServiceActive()) {
            log.severe(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }

        SolrClient client = getSolrClient();
        String batchQueryString = ID + ":" + prepareBatchQueryString(keys);

        try {
            client.deleteByQuery(INSTRUCTOR_COLLECTION_NAME, batchQueryString);
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_DELETE_DOCUMENT, batchQueryString, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_DELETE_DOCUMENT, batchQueryString, e.getCause())
                    + TeammatesException.toStringWithStackTrace(e));
        }
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

    private String prepareBatchQueryString(String... keys) {
        String or = " OR ";
        StringBuilder sb = new StringBuilder("(" + keys[0]);

        for (int i = 1; i < keys.length; i++) {
            sb.append(or).append(keys[i]);
        }

        return sb.toString() + ")";
    }
}
