package teammates.common.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Url class represents a URL string.
 * It provides methods to manipulate the URL string and extract values from it.
 */
public class Url {

    private final String baseUrl;
    private final String relativeUrl;
    private String query;

    public Url(String urlString) {
        // parse and validate the urlString with the built-in URL object
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Assumption.fail("MalformedURLException for [" + urlString + "]: " + e.getMessage());
        }

        this.baseUrl = url.getProtocol() + "://" + url.getAuthority();
        this.relativeUrl = StringHelper.convertToEmptyStringIfNull(url.getPath());
        String query = url.getQuery();
        this.query = query == null ? "" : "?" + query;
    }

    /**
      * Returns the relative part (path) of the URL, after the
      * authority (host name + port number if specified) but before the query.<br>
      * Example:
      * <ul>
      * <li><code>new Url("http://localhost:8080/index.jsp").getRelativeUrl()</code>
      * returns <code>/index.jsp</code></li>
      * <li><code>new Url("http://google.com").getRelativeUrl()</code>
      * returns <i>[empty string]</i></li>
      * <li><code>new Url("https://teammatesv4.appspot.com/page/studentHomePage?user=abc").getRelativeUrl()</code>
      * returns <code>/page/studentHomePage</code></li>
      * </ul>
      */
    public String getRelativeUrl() {
        return relativeUrl;
    }

    /**
      * Returns the first part of the URL, including the protocol and
      * authority (host name + port number if specified) but not the path.<br>
      * Example:
      * <ul>
      * <li><code>new Url("http://localhost:8080/index.jsp").getBaseUrl()</code>
      * returns <code>http://localhost:8080</code></li>
      * <li><code>new Url("https://teammatesv4.appspot.com/index.jsp").getBaseUrl()</code>
      * returns <code>https://teammatesv4.appspot.com</code></li>
      * </ul>
      */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns the value of the {@code parameterName} parameter. Null if no such parameter.
     */
    public String get(String parameterName) {
        /*
         * Regex meaning: from the start of the string, try to find either:
         * 1. "?" followed by "{parameterName}="
         * 2. Any amount of any character followed by "&{parameterName}="
         * followed by as many characters as possible until the first & is found.
         * Returns the first occurrence if found, null otherwise.
         */
        String keyValuePairRegex = "^(\\?|.*&)" + parameterName + "=([^&]*).*";
        return query.matches(keyValuePairRegex) ? query.replaceFirst(keyValuePairRegex, "$2") : null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Url> T withParam(String paramName, String paramValue) {
        query = addParamToUrl(query, paramName, paramValue);
        return (T) this;
    }

    /**
     * Returns the URL with the specified key-value pair parameter added.
     * The parameter will also be sanitized according to URL specification.
     * Unchanged if either the key or value is null, or the key already exists<br>
     * Example:
     * <ul>
     * <li><code>addParam("index.jsp","action","add")</code> returns
     * <code>index.jsp?action=add</code></li>
     * <li><code>addParam("index.jsp?action=add","courseid","cs1101")</code>
     * returns <code>index.jsp?action=add&courseid=cs1101</code></li>
     * <li><code>addParam("index.jsp","message",null)</code> returns
     * <code>index.jsp</code></li>
     * </ul>
     */
    public static String addParamToUrl(String url, String key, String value) {
        if (key == null || key.isEmpty() || value == null || value.isEmpty()
                || url.contains("?" + key + "=") || url.contains("&" + key + "=")) {
            // return the url if any of the key or the value is null or empty
            // or if the key is already included in the url
            return url;
        }
        return url + (url.contains("?") ? "&" : "?") + key + "=" + SanitizationHelper.sanitizeForUri(value);
    }

    public static String trimTrailingSlash(String url) {
        return url.trim().replaceAll("/(?=$)", "");
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
