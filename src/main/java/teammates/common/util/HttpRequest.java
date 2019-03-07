package teammates.common.util;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * A utility class to execute an HTTP request and return the response.
 */
public final class HttpRequest {

    private HttpRequest() {
        // Utility class
        // Intentional private constructor to prevent instantiation.
    }

    /**
     * Returns the HTTP response string.
     * @param uri The URI containing the request URL and request parameters.
     * @return the HTTP response string after executing the HTTP request
     */
    public static String execute(URI uri) throws IOException, NullPointerException {
        HttpUriRequest request = new HttpGet(uri);
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String response = EntityUtils.toString(entity, "UTF-8");

        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return response;
        } else {
            throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), response);
        }
    }
}
