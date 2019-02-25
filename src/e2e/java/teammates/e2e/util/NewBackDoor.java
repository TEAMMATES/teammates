package teammates.e2e.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
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
import teammates.common.util.SanitizationHelper;

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
     * Executes POST request with the given {@code relativeUrl}.
     *
     * @return The content of the HTTP response
     */
    public static String executePostRequest(String relativeUrl) {
        return executeRequest(HttpPost.METHOD_NAME, relativeUrl);
    }

    /**
     * Executes DELETE request with the given {@code relativeUrl}.
     *
     * @return The content of the HTTP response
     */
    public static String executeDeleteRequest(String relativeUrl) {
        return executeRequest(HttpDelete.METHOD_NAME, relativeUrl);
    }

    /**
     * Executes HTTP request with the given {@code method} and {@code relativeUrl}.
     *
     * @return The content of the HTTP response
     */
    public static String executeRequest(String method, String relativeUrl) {
        String url = TestProperties.TEAMMATES_URL + Const.ResourceURIs.URI_PREFIX + relativeUrl;
        // http://localhost:8080/webapi/relativeUrl
        // relativeUrl = backdoor?action=CreateFeedbackAction&params=information
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

    /**
     * Deletes an account from datastore.
     */
    public static String deleteAccount(String googleId) {
        Map<String, String> params = createParamMap("DeleteStudentAction");

        params.put(Const.ParamsNames.STUDENT_ID, googleId);
        return executeDeleteRequest("/backdoor?".concat(encodeParameters(params)));
    }

    /**
     * Deletes a feedback session from the datastore.
     */
    public static String deleteFeedbackSession(String feedbackSessionName, String courseId) {
        Map<String, String> params = createParamMap("DeleteFeedbackSessionAction");

        params.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        params.put(Const.ParamsNames.COURSE_ID, courseId);
        return executeDeleteRequest("/backdoor?".concat(encodeParameters(params)));
    }

    /**
     * Gets a feedback session from the datastore.
     */
    public static String getFeedbackSession(String courseId, String feedbackSessionName) {
        Map<String, String> params = createParamMap("GetFeedbackSessionAction");

        params.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        params.put(Const.ParamsNames.COURSE_ID, courseId);

        return executePostRequest( "/backdoor?".concat(encodeParameters(params)));
    }

    private static Map<String, String> createParamMap(String operation) {
        Map<String, String> map = new HashMap<>();
        map.put(Const.ParamsNames.BACKDOOR_OPERATION, operation);

        return map;
    }

    private static String encodeParameters(Map<String, String> map) {
        StringBuilder dataStringBuilder = new StringBuilder();
        map.forEach((key, value) -> dataStringBuilder.append(key + "=" + SanitizationHelper.sanitizeForUri(value) + "&"));
        return dataStringBuilder.toString();
    }

    private static void addAuthKeys(HttpRequestBase request) {
        request.addHeader("Backdoor-Key", TestProperties.BACKDOOR_KEY);
        request.addHeader("CSRF-Key", TestProperties.CSRF_KEY);
    }

}
