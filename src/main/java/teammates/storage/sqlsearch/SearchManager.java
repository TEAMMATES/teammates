package teammates.storage.sqlsearch;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import teammates.common.exception.SearchServiceException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.storage.sqlentity.BaseEntity;

/**
 * Acts as a proxy to search service.
 *
 * @param <T> Type of entity to be returned
 */
abstract class SearchManager<T extends BaseEntity> {

    private static final Logger log = Logger.getLogger();

    private static final String ERROR_DELETE_DOCUMENT = "Failed to delete document(s) %s in Solr. Root cause: %s ";
    private static final String ERROR_SEARCH_DOCUMENT = "Failed to search for document(s) %s from Solr. Root cause: %s ";
    private static final String ERROR_SEARCH_NOT_IMPLEMENTED = "Search service is not implemented";
    private static final String ERROR_PUT_DOCUMENT = "Failed to put document %s into Solr. Root cause: %s ";
    private static final String ERROR_RESET_COLLECTION = "Failed to reset collections. Root cause: %s ";

    private static final int START_INDEX = 0;
    private static final int NUM_OF_RESULTS = Const.SEARCH_QUERY_SIZE_LIMIT;

    private final HttpSolrClient client;
    private final boolean isResetAllowed;

    SearchManager(String searchServiceHost, boolean isResetAllowed) {
        this.isResetAllowed = Config.IS_DEV_SERVER && isResetAllowed;

        if (StringHelper.isEmpty(searchServiceHost)) {
            this.client = null;
        } else {
            this.client = new HttpSolrClient.Builder(searchServiceHost)
                    .withConnectionTimeout(2000) // timeout for connecting to Solr server
                    .withSocketTimeout(5000) // timeout for reading data
                    .build();
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

    QueryResponse performQuery(SolrQuery query) throws SearchServiceException {
        if (client == null) {
            throw new SearchServiceException("Full-text search is not available.", HttpStatus.SC_NOT_IMPLEMENTED);
        }

        try {
            return client.query(getCollectionName(), query);
        } catch (SolrServerException e) {
            Throwable rootCause = e.getRootCause();
            log.severe(String.format(ERROR_SEARCH_DOCUMENT, query.getQuery(), rootCause), e);
            if (rootCause instanceof SocketTimeoutException) {
                throw new SearchServiceException("A timeout was reached while processing your request. "
                        + "Please try again later.", e, HttpStatus.SC_GATEWAY_TIMEOUT);
            } else {
                throw new SearchServiceException("An error has occurred while performing search. "
                        + "Please try again later.", e, HttpStatus.SC_BAD_GATEWAY);
            }
        } catch (IOException e) {
            log.severe(String.format(ERROR_SEARCH_DOCUMENT, query.getQuery(), e.getCause()), e);
            throw new SearchServiceException("An error has occurred while performing search. "
                    + "Please try again later.", e, HttpStatus.SC_BAD_GATEWAY);
        }
    }

    abstract String getCollectionName();

    abstract SearchDocument<T> createDocument(T entity);

    /**
     * Creates or updates search document for the given entity.
     */
    public void putDocument(T entity) throws SearchServiceException {
        if (client == null) {
            log.warning(ERROR_SEARCH_NOT_IMPLEMENTED);
            return;
        }

        if (entity == null) {
            return;
        }

        Map<String, Object> searchableFields = createDocument(entity).getSearchableFields();
        SolrInputDocument document = new SolrInputDocument();
        searchableFields.forEach((key, value) -> document.addField(key, value));

        try {
            client.add(getCollectionName(), Collections.singleton(document));
            client.commit(getCollectionName());
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_PUT_DOCUMENT, document, e.getRootCause()), e);
            throw new SearchServiceException(e, HttpStatus.SC_BAD_GATEWAY);
        } catch (IOException e) {
            log.severe(String.format(ERROR_PUT_DOCUMENT, document, e.getCause()), e);
            throw new SearchServiceException(e, HttpStatus.SC_BAD_GATEWAY);
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
            log.severe(String.format(ERROR_DELETE_DOCUMENT, keys, e.getRootCause()), e);
        } catch (IOException e) {
            log.severe(String.format(ERROR_DELETE_DOCUMENT, keys, e.getCause()), e);
        }
    }

    /**
     * Resets the data for all collections if, and only if called during component
     * tests.
     */
    public void resetCollections() {
        if (client == null || !isResetAllowed) {
            return;
        }

        try {
            client.deleteByQuery(getCollectionName(), "*:*");
            client.commit(getCollectionName());
        } catch (SolrServerException e) {
            log.severe(String.format(ERROR_RESET_COLLECTION, e.getRootCause()), e);
        } catch (IOException e) {
            log.severe(String.format(ERROR_RESET_COLLECTION, e.getCause()), e);
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

    abstract T getEntityFromDocument(SolrDocument document);

    abstract void sortResult(List<T> result);

    List<T> convertDocumentToEntities(List<SolrDocument> documents) {
        if (documents == null) {
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>();

        for (SolrDocument document : documents) {
            T entity = getEntityFromDocument(document);

            // Entity will be null if document corresponds to entity in datastore
            if (entity == null) {
                // search engine out of sync as SearchManager may fail to delete documents
                // the chance is low and it is generally not a big problem

                // these lines below are commented out as they interfere with the dual db search,
                // and cause unwanted deletions, please refer to the following PR for more details
                // [PR](https://github.com/TEAMMATES/teammates/pull/12838)

                // String id = (String) document.getFirstValue("id");
                // deleteDocuments(Collections.singletonList(id));
                continue;
            }
            result.add(entity);
        }
        sortResult(result);

        return result;
    }

}
