package teammates.common.util;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * A utility class to execute an HTTP request and return the response.
 */
public final class HttpRequest {

    private static final int TIMEOUT_IN_MS = 30000;

    private HttpRequest() {
        // Utility class
        // Intentional private constructor to prevent instantiation.
    }

    /**
     * Executes a HTTP GET request and returns the response string.
     * @param uri The URI containing the request URL and request parameters.
     * @return the HTTP response string after executing the GET request
     */
    public static String executeGetRequest(URI uri) throws IOException {
        HttpUriRequest request = new HttpGet(uri);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(TIMEOUT_IN_MS).build();

        HttpResponse httpResponse = HttpClientBuilder.create()
                                                     .setDefaultRequestConfig(requestConfig)
                                                     .build()
                                                     .execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String response = EntityUtils.toString(entity, Const.ENCODING);

        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return response;
        } else {
            throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), response);
        }
    }
}
