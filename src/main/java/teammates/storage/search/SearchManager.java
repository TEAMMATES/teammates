package teammates.storage.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.exception.SearchNotImplementedException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
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

    SolrQuery getBasicQuery(String queryString) {
        SolrQuery query = new SolrQuery();

        String cleanQueryString = cleanSpecialChars(queryString);
        query.setQuery(cleanQueryString);

        query.setStart(START_INDEX);
        query.setRows(NUM_OF_RESULTS);

        return query;
    }

    QueryResponse performQuery(SolrQuery query) throws SearchNotImplementedException {
        if (client == null) {
            throw new SearchNotImplementedException();
        }

        QueryResponse response = null;

        try {
            response = client.query(getCollectionName(), query);
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_SEARCH_DOCUMENT, query.getQuery(), e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_SEARCH_DOCUMENT, query.getQuery(), e.getCause())
                    + TeammatesException.toStringWithStackTrace(e));
        }

        return response;
    }

    abstract String getCollectionName();

    abstract SearchDocument<T> createDocument(T attribute);

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
            if (attribute == null) {
                continue;
            }
            Map<String, Object> searchableFields = createDocument(attribute).getSearchableFields();
            SolrInputDocument document = new SolrInputDocument();
            searchableFields.forEach((key, value) -> document.addField(key, value));
            documents.add(document);
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
     * Resets the data for all collections if, and only if called during component tests.
     */
    public void resetCollections() {
        if (client == null || !isResetAllowed) {
            return;
        }

        try {
            client.deleteByQuery(getCollectionName(), "*:*");
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_RESET_COLLECTION, e.getRootCause())
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe(String.format(ERROR_RESET_COLLECTION, e.getCause())
                    + TeammatesException.toStringWithStackTrace(e));
        }
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

    abstract T getAttributeFromDocument(SolrDocument document);

    abstract void sortResult(List<T> result);

    List<T> convertDocumentToAttributes(QueryResponse response) {
        if (response == null) {
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>();

        for (SolrDocument document : response.getResults()) {
            T attribute = getAttributeFromDocument(document);
            if (attribute == null) {
                // search engine out of sync as SearchManager may fail to delete documents
                // the chance is low and it is generally not a big problem
                String id = (String) document.getFirstValue("id");
                deleteDocuments(Collections.singletonList(id));
                continue;
            }
            result.add(attribute);
        }
        sortResult(result);

        return result;
    }

}
