package teammates.common.exception;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

/**
 * Exception thrown when error is encountered while performing search.
 */
public class SearchServiceException extends Exception {
    private final int statusCode;

    public SearchServiceException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public SearchServiceException(IOException e, int scBadGateway) {
        super(e);
        this.statusCode = scBadGateway;
    }

    public SearchServiceException(SolrServerException e, int scBadGateway) {
        super(e);
        this.statusCode = scBadGateway;
    }

    public SearchServiceException(String string, int i) {
        super(string);
        this.statusCode = i;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
