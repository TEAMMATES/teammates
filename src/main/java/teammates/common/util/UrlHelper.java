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

    private static final int HTTPS_PORT = 443;
    private static final int HTTP_PORT = 80;

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
        if (url == null) {
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
        return isSafeRedirectUrl(relativeUrl) ? relativeUrl : DEFAULT_REDIRECT_URL;
    }

    /**
     * Checks whether the given URL is safe to use as a redirect target.
     */
    public static boolean isSafeRedirectUrl(String url) {
        if (url == null) {
            return false;
        }

        try {
            URI uri = new URI(url);
            if (uri.isAbsolute()) {
                return isSafeAbsoluteRedirectUrl(uri);
            }

            return isSafeRelativeRedirectUrl(url);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static boolean isSafeRelativeRedirectUrl(String url) {
        return url.startsWith("/")
                && !url.startsWith("//");
    }

    private static boolean isSafeAbsoluteRedirectUrl(URI uri) throws URISyntaxException {
        if (!isHttp(uri) && !isHttps(uri)) {
            return false;
        }

        URI frontendUri = new URI(Config.APP_FRONTEND_URL);
        return isSameOrigin(uri, frontendUri);
    }

    private static boolean isSameOrigin(URI firstUri, URI secondUri) {
        boolean isSameScheme = firstUri.getScheme().equalsIgnoreCase(secondUri.getScheme());
        boolean isSameHost = firstUri.getHost() != null
                && firstUri.getHost().equalsIgnoreCase(secondUri.getHost());
        boolean isSamePort = getPort(firstUri) == getPort(secondUri);

        return isSameScheme && isSameHost && isSamePort;
    }

    private static int getPort(URI uri) {
        if (uri.getPort() != -1) {
            return uri.getPort();
        }

        return isHttps(uri) ? HTTPS_PORT : HTTP_PORT;
    }

    private static boolean isHttp(URI uri) {
        return "http".equalsIgnoreCase(uri.getScheme());
    }

    private static boolean isHttps(URI uri) {
        return "https".equalsIgnoreCase(uri.getScheme());
    }

}
