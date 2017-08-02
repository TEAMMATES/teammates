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
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.common.util.retry.RetryManager;
import teammates.common.util.retry.RetryableTaskThrows;

/**
 * Manages {@link Document} and {@link Index} in the Datastore for use of search functions.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/java/search/">https://cloud.google.com/appengine/docs/java/search/</a>
 */
public final class SearchManager {

    private static final String ERROR_NON_TRANSIENT_BACKEND_ISSUE =
            "Failed to put document(s) %s into search index %s due to non-transient backend issue: ";
    private static final String ERROR_MAXIMUM_RETRIES_EXCEEDED =
            "Failed to put document(s) %s into search index %s after maximum retries: %s: ";
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
        } catch (PutException e) {
            log.severe(String.format(ERROR_NON_TRANSIENT_BACKEND_ISSUE, document, indexName)
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (MaximumRetriesExceededException e) {
            log.severe(String.format(ERROR_MAXIMUM_RETRIES_EXCEEDED, document, indexName, e.finalMessage)
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

    /**
     * Tries putting a document, handling transient errors by retrying with exponential backoff.
     *
     * @throws PutException if a non-transient error is encountered.
     * @throws MaximumRetriesExceededException with final {@link OperationResult}'s message as final message,
     *         if operation fails after maximum retries.
     */
    private static void putDocumentWithRetry(String indexName, final Document document)
            throws PutException, MaximumRetriesExceededException {
        final Index index = getIndex(indexName);

        /*
         * The GAE Search API signals put document failure in two ways: it either
         * returns a PutResponse containing an OperationResult with a non-OK StatusCode, or
         * throws a PutException that also contains an embedded OperationResult.
         * We handle both ways by examining the OperationResult to determine what kind of error it is. If it is
         * transient, we use RetryManager to retry the operation; if it is
         * non-transient, we do not retry but throw a PutException upwards instead.
         */
        RM.runUntilSuccessful(new RetryableTaskThrows<PutException>("Put document") {

            private OperationResult lastResult;

            @Override
            public void run() {
                try {
                    PutResponse response = index.put(document);
                    lastResult = response.getResults().get(0);

                } catch (PutException e) {
                    lastResult = e.getOperationResult();
                }
            }

            @Override
            public boolean isSuccessful() throws PutException {
                // Update the final message to be shown if the task fails after maximum retries
                finalMessage = lastResult.getMessage();

                if (StatusCode.OK.equals(lastResult.getCode())) {
                    return true;
                } else if (StatusCode.TRANSIENT_ERROR.equals(lastResult.getCode())) {
                    // A transient error can be retried
                    return false;
                } else {
                    // A non-transient error signals that the operation should not be retried
                    throw new PutException(lastResult);
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
    private static void putDocumentsWithRetry(String indexName, final List<Document> documents)
            throws PutException, MaximumRetriesExceededException {
        final Index index = getIndex(indexName);

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
            public void run() throws PutException {
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
