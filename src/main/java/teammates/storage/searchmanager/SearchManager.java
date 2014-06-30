package teammates.storage.searchmanager;

import java.util.logging.Logger;

import teammates.common.util.Config;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;

import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

/**
 * Represents the manager for index.
 * Codes reference:
 * https://developers.google.com/appengine/docs/java/search/
 */
public class SearchManager {
    private static final String ERROR_BACKEND_FAILED_TO_PUT_DOCUMENT = "Failed to put document into search index due to non-transient backend issue.";
    private static final String ERROR_EXCEED_DURATION_FAILED_TO_PUT_DOCUMENT = "Operation did not succeed in time to put document %s into search index %s";
    private static final Logger log = Utils.getLogger();
    
    public static void putDocument(String indexName, Document document){
        int elapsedTime = 0;
        boolean isSuccessful = tryPutDocument(indexName, document);
        while (!isSuccessful
                && (elapsedTime < Config.PERSISTENCE_CHECK_DURATION)) {
            ThreadHelper.waitBriefly();
            //retry putting the document
            isSuccessful = tryPutDocument(indexName, document);
            //check before incrementing to avoid boundary case problem
            if (!isSuccessful) {
                elapsedTime += ThreadHelper.WAIT_DURATION;
            }
        }
        if (elapsedTime == Config.PERSISTENCE_CHECK_DURATION) {
            log.severe(String.format(ERROR_EXCEED_DURATION_FAILED_TO_PUT_DOCUMENT, document, indexName));
        }
    }
    
    private static boolean tryPutDocument(String indexName, Document document){
        Index index = getIndex(indexName);
        try {
            index.put(document);
        } catch (PutException e) {
            //if it's a transient error, it can be re-tried
            if(StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())){
                return false;
            } else {
                log.severe(ERROR_BACKEND_FAILED_TO_PUT_DOCUMENT+ " e:\n" + e.getStackTrace());
            }
        }
        return true;
    }
    
    public static Document getDocument(String indexName, String documentId){
        return getIndex(indexName).get(documentId);
    }
    
    public static Results<ScoredDocument> searchDocuments(String indexName, Query query){
        return getIndex(indexName).search(query);
    }
    
    public static void deleteDocument(String indexName, String documentId){
        getIndex(indexName).delete(documentId);
    }
    
    public static void deleteDocuments(String indexName, String[] documentIds){
        getIndex(indexName).delete(documentIds);
    }
    
    public static Index getIndex(String indexName) {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build(); 
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
        return index;
    }
}
