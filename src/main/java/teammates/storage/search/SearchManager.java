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

import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.SearchNotImplementedException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;

/**
 * Acts as a proxy to search service.
 *
 * @param <T> type of entity to be returned
 */
abstract class SearchManager<T extends EntityAttributes<?>> {

    private static final Logger log = Logger.getLogger();

    private static final String ERROR_DELETE_DOCUMENT =
            "Failed to delete document(s) %s in Solr. Root cause: %s ";
    private static final String ERROR_SEARCH_DOCUMENT =
            "Failed to search for document(s) %s from Solr. Root cause: %s ";
    private static final String ERROR_SEARCH_NOT_IMPLEMENTED =
            "Search service is not implemented";
    private static final String ERROR_PUT_DOCUMENT =
            "Failed to put document(s) %s into Solr. Root cause: %s ";
    private static final String ERROR_RESET_COLLECTION =
            "Failed to reset collections. Root cause: %s ";
    private static final String STUDENT_COLLECTION_NAME = "students";
    private static final String INSTRUCTOR_COLLECTION_NAME = "instructors";

    private static final int START_INDEX = 0;
    private static final int NUM_OF_RESULTS = 20;

    private final HttpSolrClient client;
    private final boolean isResetAllowed;

    SearchManager(String searchServiceHost, boolean isResetAllowed) {
        this.isResetAllowed = Config.isDevServer() && isResetAllowed;

        if (StringHelper.isEmpty(searchServiceHost)) {
            this.client = null;
        } else {
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

    abstract String getCollectionName();

    abstract SolrInputDocument createDocument(T attribute);

    /**
     * Batch creates or updates search documents for the given entities.
     */
    public void putDocuments(List<T> attributes) {
        if (client == null) {
            log.warning(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }

        List<SolrInputDocument> documents = new ArrayList<>();

        for (T attribute : attributes) {
            documents.add(createDocument(attribute));
        }

        try {
            client.add(getCollectionName(), documents);
            client.commit(getCollectionName());
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_PUT_DOCUMENT, documents, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_PUT_DOCUMENT, documents, e.getCause())
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

    /**
     * Removes search documents based on the given keys.
     */
    public void deleteDocuments(List<String> keys) {
        if (client == null) {
            log.warning(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }

        if (keys.isEmpty()) {
            return;
        }

        try {
            client.deleteById(getCollectionName(), keys);
            client.commit(getCollectionName());
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_DELETE_DOCUMENT, keys, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_DELETE_DOCUMENT, keys, e.getCause())
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
     * Resets the data for all collections if, and only if called during component tests.
     */
    public void resetCollections() {
        if (!isResetAllowed) {
            return;
        }

        try {
            client.deleteByQuery(INSTRUCTOR_COLLECTION_NAME, "*:*");
            client.deleteByQuery(STUDENT_COLLECTION_NAME, "*:*");
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_RESET_COLLECTION, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_RESET_COLLECTION, e.getCause())
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
        String htmlTagStripPattern = "<[^>]*>";

        // Solr special characters: + - && || ! ( ) { } [ ] ^ " ~ * ? : \ /
        String res = queryString.replaceAll(htmlTagStripPattern, "")
                .replace("\\", "\\\\")
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
            res = res.replace("\"", "");
        }

        // use exact match only when there's email-like input
        if (res.contains("@") && count == 0) {
            return "\"" + res + "\"";
        } else {
            return res;
        }
    }
}
