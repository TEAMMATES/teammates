package teammates.common.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Represents a URL of the application. It provides utility methods to
 * manipulate the URL and its parameters.
 */
public class AppUrl {

    private final String baseUrl;
    private final String relativeUrl;
    private String query;

    public AppUrl(String urlString) {
        // parse and validate the urlString with the built-in URL object
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            assert false : "MalformedURLException for [" + urlString + "]: " + e.getMessage();
        }

        this.baseUrl = url.getProtocol() + "://" + url.getAuthority();
        this.relativeUrl = StringHelper.convertToEmptyStringIfNull(url.getPath());
        String query = url.getQuery();
        this.query = query == null ? "" : "?" + query;
        assert urlString.startsWith("http"); // must either be http or https
    }

    /**
     * Returns the first part of the URL, including the protocol and
     * authority (host name + port number if specified) but not the path.<br>
     * Example:
     * <ul>
     * <li><code>new Url("http://localhost:8080/index.html").getBaseUrl()</code>
     * returns <code>http://localhost:8080</code></li>
     * <li><code>new Url("https://teammatesv4.appspot.com/index.html").getBaseUrl()</code>
     * returns <code>https://teammatesv4.appspot.com</code></li>
     * </ul>
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    public AppUrl withParam(String paramName, String paramValue) {
        query = addParamToUrl(query, paramName, paramValue);
        return this;
    }

    /**
     * Returns the URL with the specified key-value pair parameter added.
     * The parameter will also be sanitized according to URL specification.
     * Unchanged if either the key or value is null, or the key already exists<br>
     * Example:
     * <ul>
     * <li><code>addParam("index.html","action","add")</code> returns
     * <code>index.html?action=add</code></li>
     * <li><code>addParam("index.html?action=add","courseid","cs1101")</code>
     * returns <code>index.html?action=add&courseid=cs1101</code></li>
     * <li><code>addParam("index.html","message",null)</code> returns
     * <code>index.html</code></li>
     * </ul>
     */
    static String addParamToUrl(String url, String key, String value) {
        if (key == null || key.isEmpty() || value == null || value.isEmpty()
                || url.contains("?" + key + "=") || url.contains("&" + key + "=")) {
            // return the url if any of the key or the value is null or empty
            // or if the key is already included in the url
            return url;
        }
        return url + (url.contains("?") ? "&" : "?") + key + "=" + SanitizationHelper.sanitizeForUri(value);
    }

    public AppUrl withAccountId(UUID accountId) {
        return withParam(Const.ParamsNames.ACCOUNT_ID, accountId.toString());
    }

    public AppUrl withMasqueradeAccount(UUID accountId) {
        return withParam(Const.ParamsNames.MASQUERADE_ACCOUNT_ID, accountId.toString());
    }

    public AppUrl withRegistrationKey(String key) {
        return withParam(Const.ParamsNames.REGKEY, key);
    }

    public AppUrl withIsCreatingAccount(String isCreatingAccount) {
        return withParam(Const.ParamsNames.IS_CREATING_ACCOUNT, isCreatingAccount);
    }

    public AppUrl withCourseId(String courseId) {
        return withParam(Const.ParamsNames.COURSE_ID, courseId);
    }

    public AppUrl withFeedbackSessionId(UUID feedbackSessionId) {
        return withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId.toString());
    }

    public AppUrl withUserId(UUID userId) {
        return withParam(Const.ParamsNames.USER_ID, userId.toString());
    }

    public AppUrl withEntityType(String entityType) {
        return withParam(Const.ParamsNames.ENTITY_TYPE, entityType);
    }

    public AppUrl withPreviewAs(String previewAs) {
        return withParam(Const.ParamsNames.PREVIEWAS, previewAs);
    }

    @Override
    public String toString() {
        return relativeUrl + query;
    }

    /**
     * Returns the absolute version of the URL by appending the base URL
     * to the URL input.
     */
    public String toAbsoluteString() {
        return baseUrl + toString();
    }

}
