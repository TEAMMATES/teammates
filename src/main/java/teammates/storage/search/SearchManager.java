package teammates.storage.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.PutResponse;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Logger;
import teammates.common.util.ThreadHelper;

/**
 * Manages {@link Document} and {@link Index} in the Datastore for use of search functions.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/java/search/">https://cloud.google.com/appengine/docs/java/search/</a>
 */
public final class SearchManager {

    private static final String ERROR_NON_TRANSIENT_BACKEND_ISSUE =
            "Failed to put document %s into search index %s due to non-transient backend issue: ";
    private static final String ERROR_EXCEED_DURATION =
            "Operation did not succeed in time: putting document %s into search index %s.";
    private static final Logger log = Logger.getLogger();
    private static final ThreadLocal<Map<String, Index>> PER_THREAD_INDICES_TABLE = new ThreadLocal<Map<String, Index>>();
    private static final int MAX_RETRIES = 3;

    private SearchManager() {
        // utility class
    }

    /**
     * Creates or updates the search document for the given document and index.
     */
    public static void putDocument(String indexName, Document document) {
        Index index = getIndex(indexName);

        int delay = 2;
        for (int attempts = 0; attempts < MAX_RETRIES; attempts++) {
            try {
                PutResponse result = index.put(document);

                if (Config.PERSISTENCE_CHECK_DURATION == 0) {
                    continue;
                }

                int elapsedTime = 0;
                boolean isSuccessful = result.getResults().get(0).getCode() == StatusCode.OK;
                while (!isSuccessful && elapsedTime < Config.PERSISTENCE_CHECK_DURATION) {
                    ThreadHelper.waitBriefly();
                    // retry putting the document
                    result = index.put(document);
                    isSuccessful = result.getResults().get(0).getCode() == StatusCode.OK;
                    // check before incrementing to avoid boundary case problem
                    if (!isSuccessful) {
                        elapsedTime += ThreadHelper.WAIT_DURATION;
                    }
                }
                if (elapsedTime >= Config.PERSISTENCE_CHECK_DURATION) {
                    log.info(String.format(ERROR_EXCEED_DURATION, document, indexName));
                }

            } catch (PutException e) {
                if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
                    // if it's a transient error in the server, it can be retried
                    ThreadHelper.waitFor(delay * 1000);
                    delay *= 2; // use exponential backoff
                    continue;
                } else {
                    log.severe(String.format(ERROR_NON_TRANSIENT_BACKEND_ISSUE, document, indexName)
                            + TeammatesException.toStringWithStackTrace(e));
                    break;
                }
            }
        }
    }

    /**
     * Batch creates or updates the search documents for the given documents and index.
     */
    public static void putDocuments(String indexName, List<Document> documents) {
        Index index = getIndex(indexName);

        int delay = 2;
        for (int attempts = 0; attempts < MAX_RETRIES; attempts++) {
            try {
                List<Document> failedDocuments = putDocuments(index, documents);
                boolean isSuccessful = failedDocuments.isEmpty();

                if (Config.PERSISTENCE_CHECK_DURATION == 0) {
                    continue;
                }

                int elapsedTime = 0;
                while (!isSuccessful && elapsedTime < Config.PERSISTENCE_CHECK_DURATION) {
                    ThreadHelper.waitBriefly();
                    isSuccessful = putDocuments(index, failedDocuments).isEmpty();

                    // check before incrementing to avoid boundary case problem
                    if (!isSuccessful) {
                        elapsedTime += ThreadHelper.WAIT_DURATION;
                    }
                }
                if (elapsedTime >= Config.PERSISTENCE_CHECK_DURATION) {
                    log.info(String.format(ERROR_EXCEED_DURATION, documents, indexName));
                }

            } catch (PutException e) {
                if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
                    // if it's a transient error in the server, it can be retried
                    ThreadHelper.waitFor(delay * 1000);
                    delay *= 2; // use exponential backoff
                    continue;
                } else {
                    log.severe(String.format(ERROR_NON_TRANSIENT_BACKEND_ISSUE, documents, indexName)
                            + TeammatesException.toStringWithStackTrace(e));
                    break;
                }
            }
        }
    }

    /**
     * Puts {@code documents} in {@code index}.
     *
     * @return list of documents have not been put successfully
     */
    private static List<Document> putDocuments(Index index, List<Document> documents) {
        PutResponse result = index.put(documents);
        List<Document> failedDocuments = new ArrayList<Document>();
        for (int i = 0; i < documents.size(); i++) {
            boolean isSuccessful = result.getResults().get(i).getCode() == StatusCode.OK;
            if (!isSuccessful) {
                failedDocuments.add(documents.get(i));
            }
        }
        return failedDocuments;
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
            indicesTable = new HashMap<String, Index>();
            PER_THREAD_INDICES_TABLE.set(indicesTable);
        }
        return indicesTable;
    }

}
