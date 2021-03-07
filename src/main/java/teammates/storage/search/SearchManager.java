package teammates.storage.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.OperationResult;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.PutResponse;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchQueryException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.common.util.retry.RetryManager;
import teammates.common.util.retry.RetryableTaskThrows;

/**
 * Manages {@link Document} and {@link Index} in the Datastore for use of search functions.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/java/search/">https://cloud.google.com/appengine/docs/java/search/</a>
 */
public final class SearchManager {

    private static final String SEARCH_INDEX_STUDENT = "student";
    private static final String SEARCH_INDEX_INSTRUCTOR = "instructor";
    private static final String ERROR_NON_TRANSIENT_BACKEND_ISSUE =
            "Failed to put document(s) %s into search index %s due to non-transient backend issue: ";
    private static final String ERROR_MAXIMUM_RETRIES_EXCEEDED =
            "Failed to put document(s) %s into search index %s after maximum retries: %s: ";
    private static final Logger log = Logger.getLogger();
    private static final ThreadLocal<Map<String, Index>> PER_THREAD_INDICES_TABLE = new ThreadLocal<>();

    private static final RetryManager RM = new RetryManager(8);

    /**
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public StudentSearchResultBundle searchStudents(String queryString, List<InstructorAttributes> instructors) {
        StudentSearchQuery query = instructors == null ? new StudentSearchQuery(queryString)
                : new StudentSearchQuery(instructors, queryString);
        Results<ScoredDocument> scoredDocuments = searchDocuments(SEARCH_INDEX_STUDENT, query);
        if (instructors == null) {
            return StudentSearchDocument.fromResults(scoredDocuments);
        }
        return StudentSearchDocument.fromResults(scoredDocuments, instructors);
    }

    /**
     * Batch creates or updates search documents for the given students.
     */
    public void putStudentSearchDocuments(StudentAttributes... students) {
        List<SearchDocument> studentDocuments = new ArrayList<>();
        for (StudentAttributes student : students) {
            studentDocuments.add(new StudentSearchDocument(student));
        }
        putDocuments(SEARCH_INDEX_STUDENT, studentDocuments.toArray(new SearchDocument[0]));
    }

    /**
     * Removes student search documents based on the given keys.
     */
    public void deleteStudentSearchDocuments(String... keys) {
        deleteDocuments(SEARCH_INDEX_STUDENT, keys);
    }

    /**
     * Searches for instructors.
     */
    public InstructorSearchResultBundle searchInstructors(String queryString) {
        InstructorSearchQuery query = new InstructorSearchQuery(queryString);
        Results<ScoredDocument> scoredDocuments = searchDocuments(SEARCH_INDEX_INSTRUCTOR, query);
        return InstructorSearchDocument.fromResults(scoredDocuments);
    }

    /**
     * Batch creates or updates search documents for the given instructors.
     */
    public void putInstructorSearchDocuments(InstructorAttributes... instructors) {
        List<SearchDocument> instructorDocuments = new ArrayList<>();
        for (InstructorAttributes instructor : instructors) {
            if (instructor.key != null) {
                instructorDocuments.add(new InstructorSearchDocument(instructor));
            }
        }
        putDocuments(SEARCH_INDEX_INSTRUCTOR, instructorDocuments.toArray(new SearchDocument[0]));
    }

    /**
     * Removes instructor search documents based on the given keys.
     */
    public void deleteInstructorSearchDocuments(String... keys) {
        deleteDocuments(SEARCH_INDEX_INSTRUCTOR, keys);
    }

    /**
     * Puts document(s) into the search engine.
     */
    private void putDocuments(String indexName, SearchDocument... documents) {
        List<Document> searchDocuments = new ArrayList<>();
        for (SearchDocument document : documents) {
            try {
                searchDocuments.add(document.build());
            } catch (Exception e) {
                log.severe("Fail to build search document in " + indexName + " for " + document);
            }
        }
        try {
            putDocumentsWithRetry(indexName, searchDocuments);
        } catch (PutException e) {
            log.severe(String.format(ERROR_NON_TRANSIENT_BACKEND_ISSUE, documents, indexName)
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (MaximumRetriesExceededException e) {
            Object failedDocuments = e.finalData;
            log.severe(String.format(ERROR_MAXIMUM_RETRIES_EXCEEDED, failedDocuments, indexName, e.finalMessage)
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

    /**
     * Tries putting multiple documents, handling transient errors by retrying with exponential backoff.
     *
     * @throws PutException when only non-transient errors are encountered.
     * @throws MaximumRetriesExceededException with list of failed {@link Document}s as final data and
     *         final {@link OperationResult}'s message as final message, if operation fails after maximum retries.
     */
    private void putDocumentsWithRetry(String indexName, List<Document> documents)
            throws MaximumRetriesExceededException {
        Index index = getIndex(indexName);

        /*
         * The GAE Search API allows batch putting a List of Documents.
         * Results for each document are reported via a List of OperationResults.
         * We use RetryManager to retry putting a List of Documents, with each retry re-putting only
         * the documents that failed in the previous retry.
         * If we encounter one or more transient errors, we retry the operation.
         * If all results are non-transient errors, we give up and throw a PutException upwards.
         */
        RM.runUntilSuccessful(new RetryableTaskThrows<PutException>("Put documents") {

            private List<Document> documentsToPut = documents;
            private List<OperationResult> lastResults;
            private List<String> lastIds;

            @Override
            public void run() {
                try {
                    PutResponse response = index.put(documentsToPut);
                    lastResults = response.getResults();
                    lastIds = response.getIds();

                } catch (PutException e) {
                    lastResults = e.getResults();
                    lastIds = e.getIds();
                }
            }

            @Override
            public boolean isSuccessful() {
                boolean hasTransientError = false;

                List<Document> failedDocuments = new ArrayList<>();
                for (int i = 0; i < documentsToPut.size(); i++) {
                    StatusCode code = lastResults.get(i).getCode();
                    if (!StatusCode.OK.equals(code)) {
                        failedDocuments.add(documentsToPut.get(i));
                        if (StatusCode.TRANSIENT_ERROR.equals(code)) {
                            hasTransientError = true;
                        }
                    }
                }

                // Update the list of documents to be put during the next retry
                documentsToPut = failedDocuments;

                // Update the final message and data to be shown if the task fails after maximum retries
                finalMessage = lastResults.get(0).getMessage();
                finalData = documentsToPut;

                if (documentsToPut.isEmpty()) {
                    return true;
                } else if (hasTransientError) {
                    // If there is at least one transient error, continue retrying
                    return false;
                } else {
                    // If all errors are non-transient, do not continue retrying
                    throw new PutException(lastResults.get(0), lastResults, lastIds);
                }
            }
        });
    }

    /**
     * Searches document by the given query.
     */
    private Results<ScoredDocument> searchDocuments(String indexName, SearchQuery query) {
        try {
            if (query.getFilterSize() > 0) {
                return getIndex(indexName).search(query.toQuery());
            }
            return null;
        } catch (SearchQueryException e) {
            log.info("Unsupported query for this query string: " + query.toString());
            return null;
        }
    }

    /**
     * Deletes document by documentId.
     */
    private void deleteDocuments(String indexName, String... documentIds) {
        try {
            getIndex(indexName).deleteAsync(documentIds);
        } catch (Exception e) {
            log.info("Unable to delete document in the index: " + indexName
                    + " with document Ids " + String.join(", ", documentIds));
        }
    }

    private Index getIndex(String indexName) {
        Map<String, Index> indicesTable = getIndicesTable();
        Index index = indicesTable.get(indexName);
        if (index == null) {
            IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
            index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
            indicesTable.put(indexName, index);
        }
        return index;
    }

    private Map<String, Index> getIndicesTable() {
        Map<String, Index> indicesTable = PER_THREAD_INDICES_TABLE.get();
        if (indicesTable == null) {
            indicesTable = new HashMap<>();
            PER_THREAD_INDICES_TABLE.set(indicesTable);
        }
        return indicesTable;
    }

}
