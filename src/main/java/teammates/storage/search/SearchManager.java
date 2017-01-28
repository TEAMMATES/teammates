package teammates.storage.search;

import java.util.HashMap;
import java.util.Map;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Logger;
import teammates.common.util.ThreadHelper;

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

/**
 * Represents the search manager for index.
 * Codes reference:
 * https://developers.google.com/appengine/docs/java/search/
 */
public final class SearchManager {
    
    private static final String ERROR_NON_TRANSIENT_BACKEND_ISSUE =
            "Failed to put document %s into search index %s due to non-transient backend issue: ";
    private static final String ERROR_EXCEED_DURATION =
            "Operation did not succeed in time: putting document %s into search index %s.";
    private static final Logger log = Logger.getLogger();
    private static final ThreadLocal<Map<String, Index>> PER_THREAD_INDICES_TABLE = new ThreadLocal<Map<String, Index>>();
    
    private SearchManager() {
        // utility class
    }
    
    /**
     * Creates or updates the search document for the given document and index
     */
    public static void putDocument(String indexName, Document document) {
        int elapsedTime = 0;
        boolean isSuccessful = tryPutDocument(indexName, document);
        while (!isSuccessful && elapsedTime < Config.PERSISTENCE_CHECK_DURATION) {
            ThreadHelper.waitBriefly();
            // retry putting the document
            isSuccessful = tryPutDocument(indexName, document);
            // check before incrementing to avoid boundary case problem
            if (!isSuccessful) {
                elapsedTime += ThreadHelper.WAIT_DURATION;
            }
        }
        if (elapsedTime >= Config.PERSISTENCE_CHECK_DURATION) {
            log.severe(String.format(ERROR_EXCEED_DURATION, document, indexName));
        }
    }
    
    private static boolean tryPutDocument(String indexName, Document document) {
        Index index = getIndex(indexName);
        try {
            PutResponse result = index.put(document);
            return result.getResults().get(0).getCode() == StatusCode.OK;
        } catch (PutException e) {
            // if it's a transient error in the server, it can be re-tried
            if (!StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
                log.severe(String.format(ERROR_NON_TRANSIENT_BACKEND_ISSUE, document, indexName)
                           + TeammatesException.toStringWithStackTrace(e));
            }
            return false;
        }
    }
    
    /**
     * Gets document for index and the documentId.
     */
    public static Document getDocument(String indexName, String documentId) {
        return getIndex(indexName).get(documentId);
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
    
    /**
     * Deletes documents by documentIds.
     */
    public static void deleteDocuments(String indexName, String[] documentIds) {
        getIndex(indexName).deleteAsync(documentIds);
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
