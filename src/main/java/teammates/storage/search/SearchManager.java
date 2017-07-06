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
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;
import teammates.common.util.retry.RetryManager;
import teammates.common.util.retry.RetryableTaskReturnsThrows;

/**
 * Manages {@link Document} and {@link Index} in the Datastore for use of search functions.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/java/search/">https://cloud.google.com/appengine/docs/java/search/</a>
 */
public final class SearchManager {

    private static final String ERROR_NON_TRANSIENT_BACKEND_ISSUE =
            "Failed to put document %s into search index %s due to non-transient backend issue: ";
    private static final Logger log = Logger.getLogger();
    private static final ThreadLocal<Map<String, Index>> PER_THREAD_INDICES_TABLE = new ThreadLocal<>();

    private static final RetryManager RM = new RetryManager(8);

    private SearchManager() {
        // utility class
    }

    /**
     * Creates or updates the search document for the given document and index.
     */
    public static void putDocument(String indexName, Document document) {
        try {
            putDocumentWithRetry(indexName, document);
        } catch (TeammatesException | AssertionError e) {
            log.severe(String.format(ERROR_NON_TRANSIENT_BACKEND_ISSUE, document, indexName)
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

    /**
     * Tries putting a document, handling transient errors by retrying with exponential backoff.
     *
     * @throws TeammatesException if a non-transient error is encountered.
     * @throws AssertionError if the operation fails after maximum retries.
     */
    private static void putDocumentWithRetry(String indexName, final Document document)
            throws TeammatesException, AssertionError {
        final Index index = getIndex(indexName);

        /**
         * The GAE Search API signals put document failure in two ways: it either
         * returns a {@link PutResponse} containing an {@link OperationResult} with a non-OK {@link StatusCode}, or
         * throws a {@link PutException} that also contains an embedded {@link OperationResult}.
         * We handle both ways by examining the {@link OperationResult} to determine what kind of error it is. If it is
         * transient, we use {@link RetryManager} to retry the operation; if it is
         * non-transient, we do not retry but throw a {@link TeammatesException} upwards instead.
         */
        RM.runUntilSuccessful(new RetryableTaskReturnsThrows<OperationResult, TeammatesException>("Put document") {
            @Override
            public OperationResult run() {
                try {
                    PutResponse response = index.put(document);
                    return response.getResults().get(0);

                } catch (PutException e) {
                    return e.getOperationResult();
                }
            }

            public boolean isSuccessful(OperationResult result) throws TeammatesException {
                if (StatusCode.OK.equals(result.getCode())) {
                    return true;
                } else if (StatusCode.TRANSIENT_ERROR.equals(result.getCode())) {
                    // A transient error can be retried
                    return false;
                } else {
                    // A non-transient error signals that the operation should not be retried
                    throw new TeammatesException(result.getMessage());
                }
            }
        });
    }

    /**
     * Batch creates or updates the search documents for the given documents and index.
     */
    public static void putDocuments(String indexName, List<Document> documents) {
        try {
            putDocumentsWithRetry(indexName, documents);
        } catch (TeammatesException | AssertionError e) {
            log.severe(String.format(ERROR_NON_TRANSIENT_BACKEND_ISSUE, documents, indexName)
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

    /**
     * Tries putting multiple documents, handling transient errors by retrying with exponential backoff.
     *
     * @throws TeammatesException when only non-transient errors are encountered.
     * @throws AssertionError if the operation fails after maximum retries.
     */
    private static void putDocumentsWithRetry(String indexName, final List<Document> documents)
            throws TeammatesException, AssertionError {
        final Index index = getIndex(indexName);

        /**
         * The GAE Search API allows batch putting a {@link List} of {@link Document}s.
         * Results for each document are reported via a {@link List} of {@link OperationResult}s.
         * We use {@link RetryManager} to retry putting a list of documents, with each retry re-putting only
         * the documents that failed in the previous retry.
         * If we encounter one or more transient errors, we retry the operation.
         * If all results are non-transient errors, we give up and throw a {@link TeammatesException} upwards.
         */
        RM.runUntilSuccessful(new RetryableTaskReturnsThrows<List<Document>, TeammatesException>("Put documents") {
            @Override
            public List<Document> run() throws TeammatesException {
                List<Document> documentsToPut;
                if (getResult() == null) {
                    // Initial run; put given documents
                    documentsToPut = documents;
                } else {
                    // Put failed documents from previous run
                    documentsToPut = getResult();
                }

                try {
                    PutResponse response = index.put(documentsToPut);
                    List<OperationResult> results = response.getResults();

                    List<Document> failedDocuments = new ArrayList<>();
                    for (int i = 0; i < documentsToPut.size(); i++) {
                        if (!StatusCode.OK.equals(results.get(i).getCode())) {
                            failedDocuments.add(documentsToPut.get(i));
                        }
                    }

                    return failedDocuments;

                } catch (PutException e) {
                    List<OperationResult> results = e.getResults();
                    boolean hasTransientError = false;

                    List<Document> failedDocuments = new ArrayList<>();
                    for (int i = 0; i < documentsToPut.size(); i++) {
                        StatusCode code = results.get(i).getCode();
                        if (!StatusCode.OK.equals(code)) {
                            failedDocuments.add(documentsToPut.get(i));
                            if (StatusCode.TRANSIENT_ERROR.equals(code)) {
                                hasTransientError = true;
                            }
                        }
                    }

                    if (failedDocuments.isEmpty() || hasTransientError) {
                        // If there is at least one transient error, continue retrying
                        return failedDocuments;
                    } else {
                        // If all errors are non-transient, do not continue retrying
                        throw new TeammatesException(e);
                    }
                }
            }

            @Override
            public boolean isSuccessful(List<Document> failedDocuments) {
                return failedDocuments.isEmpty();
            }
        });
    }

    /**
     * Searches document by the given query.
     */
    public static Results<ScoredDocument> searchDocuments(String indexName, Query query) {
        return getIndex(indexName).search(query);
    }

    /**
     * Deletes document by documentId.
     */
    public static void deleteDocument(String indexName, String documentId) {
        getIndex(indexName).deleteAsync(documentId);
    }

    private static Index getIndex(String indexName) {
        Map<String, Index> indicesTable = getIndicesTable();
        Index index = indicesTable.get(indexName);
        if (index == null) {
            IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
            index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
            indicesTable.put(indexName, index);
        }
        return index;
    }

    private static Map<String, Index> getIndicesTable() {
        Map<String, Index> indicesTable = PER_THREAD_INDICES_TABLE.get();
        if (indicesTable == null) {
            indicesTable = new HashMap<>();
            PER_THREAD_INDICES_TABLE.set(indicesTable);
        }
        return indicesTable;
    }

}
