package teammates.storage.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.SearchNotImplementedException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
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

    private static final int START_INDEX = 0;
    private static final int NUM_OF_RESULTS = 20;

    private HttpSolrClient client;

    public SearchManager(String searchServiceHost) {
        if (!StringHelper.isEmpty(searchServiceHost)) {
            this.client = new HttpSolrClient.Builder(searchServiceHost).build();
        }
    }

    /**
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public List<StudentAttributes> searchStudents(String queryString, List<InstructorAttributes> instructors)
            throws SearchNotImplementedException {
        if (client == null) {
            throw new SearchNotImplementedException();
        }

        QueryResponse response = null;
        SolrQuery studentQuery = new SolrQuery();

        String cleanQueryString = cleanSpecialChars(queryString);
        studentQuery.setQuery(cleanQueryString);

        studentQuery.setStart(START_INDEX);
        studentQuery.setRows(NUM_OF_RESULTS);

        if (instructors != null) {
            String filterQueryString = prepareFilterQueryString(instructors);
            studentQuery.addFilterQuery(filterQueryString);
        }

        try {
            response = client.query(STUDENT_COLLECTION_NAME, studentQuery);
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_SEARCH_DOCUMENT, queryString, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_SEARCH_DOCUMENT, queryString, e.getCause())
                    + TeammatesException.toStringWithStackTrace(e));
        }

        return StudentSearchDocument.fromResponse(response);
    }

    /**
     * Batch creates or updates search documents for the given students.
     */
    public void putStudentSearchDocuments(StudentAttributes... students) {
        if (client == null) {
            log.warning(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }

        List<SolrInputDocument> studentDocs = new ArrayList<>();

        for (StudentAttributes student : students) {
            SolrInputDocument studentDoc = new StudentSearchDocument(student).toDocument();
            studentDocs.add(studentDoc);
        }

        addDocumentsToCollection(studentDocs, STUDENT_COLLECTION_NAME);
    }

    /**
     * Removes student search documents based on the given keys.
     */
    public void deleteStudentSearchDocuments(String... keys) {
        if (client == null) {
            log.warning(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }

        List<String> batchQueryString = Arrays.asList(keys);
        if (batchQueryString.isEmpty()) {
            return;
        }

        try {
            client.deleteById(STUDENT_COLLECTION_NAME, batchQueryString);
            client.commit(STUDENT_COLLECTION_NAME);
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
    public List<InstructorAttributes> searchInstructors(String queryString) throws SearchNotImplementedException {
        if (client == null) {
            throw new SearchNotImplementedException();
        }

        QueryResponse response = null;
        SolrQuery instructorQuery = new SolrQuery();

        String cleanQueryString = cleanSpecialChars(queryString);
        instructorQuery.setQuery(cleanQueryString);

        instructorQuery.setStart(START_INDEX);
        instructorQuery.setRows(NUM_OF_RESULTS);

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
        if (client == null) {
            log.warning(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }

        List<SolrInputDocument> instructorDocs = new ArrayList<>();

        for (InstructorAttributes instructor : instructors) {
            SolrInputDocument instructorDoc = new InstructorSearchDocument(instructor).toDocument();
            instructorDocs.add(instructorDoc);
        }

        addDocumentsToCollection(instructorDocs, INSTRUCTOR_COLLECTION_NAME);
    }

    /**
     * Removes instructor search documents based on the given keys.
     */
    public void deleteInstructorSearchDocuments(String... keys) {
        if (client == null) {
            log.warning(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }

        List<String> batchQueryString = Arrays.asList(keys);
        if (batchQueryString.isEmpty()) {
            return;
        }

        try {
            client.deleteById(INSTRUCTOR_COLLECTION_NAME, batchQueryString);
            client.commit(INSTRUCTOR_COLLECTION_NAME);
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_DELETE_DOCUMENT, batchQueryString, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_DELETE_DOCUMENT, batchQueryString, e.getCause())
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

    private void addDocumentsToCollection(List<SolrInputDocument> docs, String collectionName) {
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

    private String prepareFilterQueryString(List<InstructorAttributes> instructors) {
        return instructors.stream()
                .filter(i -> i.privileges.getCourseLevelPrivileges()
                        .get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS))
                .map(ins -> ins.courseId).collect(Collectors.joining(" "));
    }

    private String cleanSpecialChars(String queryString) {
        // Solr special characters: + - && || ! ( ) { } [ ] ^ " ~ * ? : \ /
        String res = queryString.replace("\\", "\\\\")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("&&", "\\&&")
                .replace("||", "\\||")
                .replace("!", "\\!")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("^", "\\^")
                .replace("~", "\\~")
                .replace("?", "\\?")
                .replace(":", "\\:")
                .replace("/", "\\/");

        // imbalanced double quotes are invalid
        int count = StringUtils.countMatches(res, "\"");
        if (count % 2 == 1) {
            res = res.replace("\"", "\\\"");
        }

        // use exact match only when there's email-like input
        if (res.contains("@") && count == 0) {
            return "\"" + res + "\"";
        } else {
            return res;
        }
    }
}
