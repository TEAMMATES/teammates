package teammates.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for URL-related functions.
 */
public final class UrlHelper {

    public static final String DEFAULT_REDIRECT_URL = "/";

    private UrlHelper() {
        // utility class
    }

    /**
     * Encodes the given query parameter value to be safely included in a URL.
     */
    public static String encodeQueryParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }

    /**
     * Returns a relative URL from the given absolute URL.
     *
     * <p>
     * If the URL is not absolute, it returns the original URL.
     * If the URL doesn't have a path or is invalid, it returns the {@link #DEFAULT_REDIRECT_URL}.
     */
    public static String getRelativeUrl(String url) {
        if (StringHelper.isEmpty(url)) {
            return DEFAULT_REDIRECT_URL;
        }

        try {
            URI uri = new URI(url);
            String relativeUrl = uri.getPath();
            // Only preserve the query when there is a path.
            return StringHelper.isEmpty(relativeUrl)
                    ? DEFAULT_REDIRECT_URL
                    : relativeUrl + (uri.getQuery() != null ? "?" + uri.getQuery() : "");
        } catch (URISyntaxException e) {
            return DEFAULT_REDIRECT_URL;
        }
    }

    /**
     * Normalizes the given redirectUrl to a relative URL and checks if it is safe.
     *
     * <p>
     * If the provided redirectUrl is not safe, it returns the {@link #DEFAULT_REDIRECT_URL}.
     */
    public static String getSafeRelativeRedirectUrl(String redirectUrl) {
        String relativeUrl = getRelativeUrl(redirectUrl);
        return isSafeRelativeRedirectUrl(relativeUrl) ? relativeUrl : DEFAULT_REDIRECT_URL;
    }

    /**
     * Checks whether the given relative URL is safe to use as a redirect target.
     */
    public static boolean isSafeRelativeRedirectUrl(String url) {
        if (StringHelper.isEmpty(url)) {
            return false;
        }

        try {
            URI uri = new URI(url);
            if (uri.isAbsolute()) {
                return false;
            }

            return url.startsWith("/")
                    && !url.startsWith("//");
        } catch (URISyntaxException e) {
            return false;
        }
    }

}
