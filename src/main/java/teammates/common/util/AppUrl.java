package teammates.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a URL of the application. It provides utility methods to
 * manipulate the URL and its parameters.
 */
public class AppUrl {

    private final String baseUrl;
    private final String relativeUrl;
    private final String initialQuery;
    private final List<Entry<String, String>> additionalParams;

    public AppUrl(String urlString) {
        URI uri;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid app URL: " + urlString, e);
        }

        if (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme())) {
            throw new IllegalArgumentException("AppUrl must use http or https: " + urlString);
        }

        if (uri.getRawAuthority() == null) {
            throw new IllegalArgumentException("AppUrl must include an authority: " + urlString);
        }

        this.baseUrl = uri.getScheme() + "://" + uri.getRawAuthority();
        this.relativeUrl = Objects.requireNonNullElse(uri.getRawPath(), "");
        String rawQuery = uri.getRawQuery();
        this.initialQuery = rawQuery == null ? "" : "?" + rawQuery;
        this.additionalParams = Collections.emptyList();
    }

    private AppUrl(String baseUrl, String relativeUrl, String initialQuery,
                   List<Map.Entry<String, String>> additionalParams) {
        this.baseUrl = baseUrl;
        this.relativeUrl = relativeUrl;
        this.initialQuery = initialQuery;
        this.additionalParams = Collections.unmodifiableList(additionalParams);
    }

    /**
     * Creates an AppUrl from a base URL and a relative URL template that may contain path placeholders
     * (e.g. {@code {feedbackSessionId}}). Skips URI validation so placeholders are preserved for
     * later substitution via {@code with*()} methods.
     */
    public static AppUrl fromParts(String baseUrl, String relativeUrl) {
        return new AppUrl(baseUrl, relativeUrl, "", Collections.emptyList());
    }

    /**
     * Returns the first part of the URL, including the protocol and
     * authority (host name + port number if specified) but not the path.<br>
     * Example:
     * <ul>
     * <li><code>new AppUrl("http://localhost:8080/index.html").getBaseUrl()</code>
     * returns <code>http://localhost:8080</code></li>
     * <li><code>new AppUrl("https://teammatesv4.appspot.com/index.html").getBaseUrl()</code>
     * returns <code>https://teammatesv4.appspot.com</code></li>
     * </ul>
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    public AppUrl withParam(String paramName, String paramValue) {
        if (paramName == null || paramName.isEmpty() || paramValue == null || paramValue.isEmpty()) {
            return this;
        }

        List<Entry<String, String>> newParams = new ArrayList<>(additionalParams);
        newParams.add(new AbstractMap.SimpleEntry<>(paramName, paramValue));
        return new AppUrl(baseUrl, relativeUrl, initialQuery, newParams);
    }

    public AppUrl withAccountId(UUID accountId) {
        String newPath = relativeUrl.replace("{accountId}", accountId.toString());
        return new AppUrl(baseUrl, newPath, initialQuery, additionalParams);
    }

    public AppUrl withMasqueradeAccount(UUID accountId) {
        return withParam(Const.ParamsNames.MASQUERADE_ACCOUNT_ID, accountId.toString());
    }

    public AppUrl withKey(String key) {
        return withParam(Const.ParamsNames.KEY, key);
    }

    public AppUrl withIsCreatingAccount(String isCreatingAccount) {
        return withParam(Const.ParamsNames.IS_CREATING_ACCOUNT, isCreatingAccount);
    }

    public AppUrl withCourseId(String courseId) {
        String newPath = relativeUrl.replace("{courseId}", courseId);
        return new AppUrl(baseUrl, newPath, initialQuery, additionalParams);
    }

    public AppUrl withFeedbackSessionId(UUID feedbackSessionId) {
        String newPath = relativeUrl.replace("{feedbackSessionId}", feedbackSessionId.toString());
        return new AppUrl(baseUrl, newPath, initialQuery, additionalParams);
    }

    public AppUrl withUserId(UUID userId) {
        String newPath = relativeUrl.replace("{userId}", userId.toString());
        return new AppUrl(baseUrl, newPath, initialQuery, additionalParams);
    }

    public AppUrl withAccountVerificationRequestId(UUID accountVerificationRequestId) {
        String newPath = relativeUrl.replace("{accountVerificationRequestId}", accountVerificationRequestId.toString());
        return new AppUrl(baseUrl, newPath, initialQuery, additionalParams);
    }

    public AppUrl withEntityType(String entityType) {
        return withParam("entityType", entityType);
    }

    public AppUrl withPreviewAs(String previewAs) {
        return withParam("previewAs", previewAs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(relativeUrl);
        sb.append(initialQuery);

        for (int i = 0; i < additionalParams.size(); i++) {
            Entry<String, String> entry = additionalParams.get(i);
            if (i == 0 && initialQuery.isEmpty()) {
                sb.append('?');
            } else {
                sb.append('&');
            }
            sb.append(SanitizationHelper.sanitizeForUri(entry.getKey()))
                    .append('=')
                    .append(SanitizationHelper.sanitizeForUri(entry.getValue()));
        }

        return sb.toString();
    }

    /**
     * Returns the absolute version of the URL by appending the base URL
     * to the URL input.
     */
    public String toAbsoluteString() {
        return baseUrl + toString();
    }

}
