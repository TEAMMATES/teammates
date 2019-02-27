package teammates.e2e.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import teammates.common.util.Const;

/**
 * Used to create API calls to the back-end without going through the UI.
 *
 * <p>Note that this will replace {@link BackDoor} once the front-end migration is complete.
 */
public final class NewBackDoor {

    private NewBackDoor() {
        // Utility class
    }

    /**
     * Executes GET request with the given {@code relativeUrl}.
     *
     * @return The content of the HTTP response
     */
    public static String executeGetRequest(String relativeUrl) {
        return executeRequest(HttpGet.METHOD_NAME, relativeUrl);
    }

    /**
     * Executes HTTP request with the given {@code method} and {@code relativeUrl}.
     *
     * @return The content of the HTTP response
     */
    private static String executeRequest(String method, String relativeUrl) {
        String url = TestProperties.TEAMMATES_URL + Const.ResourceURIs.URI_PREFIX + relativeUrl;

        HttpRequestBase request;
        switch (method) {
        case HttpGet.METHOD_NAME:
            request = new HttpGet(url);
            break;
        case HttpPost.METHOD_NAME:
            request = new HttpPost(url);
            break;
        case HttpPut.METHOD_NAME:
            request = new HttpPut(url);
            break;
        case HttpDelete.METHOD_NAME:
            request = new HttpDelete(url);
            break;
        default:
            throw new RuntimeException("Unaccepted HTTP method: " + method);
        }

        addAuthKeys(request);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                    return br.lines().collect(Collectors.joining(System.lineSeparator()));
                }
            }
            return null;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addAuthKeys(HttpRequestBase request) {
        request.addHeader("Backdoor-Key", TestProperties.BACKDOOR_KEY);
        request.addHeader("CSRF-Key", TestProperties.CSRF_KEY);
    }

}
