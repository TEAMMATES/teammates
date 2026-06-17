package teammates.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for URL-related functions.
 */
public final class UrlHelper {

    private static final String DEFAULT_REDIRECT_URL = "/";

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
     * Returns a safe redirect URL.
     * If the provided nextUrl is not safe, it returns the {@link #DEFAULT_REDIRECT_URL}.
     */
    public static String getSafeRedirectUrl(String nextUrl) {
        return isSafeRedirectUrl(nextUrl) ? nextUrl : DEFAULT_REDIRECT_URL;
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
            if (!uri.isAbsolute()) {
                return isSafeRelativeRedirectUrl(url);
            }

            return isAllowedAbsoluteRedirectUrl(uri);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static boolean isSafeRelativeRedirectUrl(String url) {
        return url.startsWith("/")
                && !url.startsWith("//");
    }

    private static boolean isAllowedAbsoluteRedirectUrl(URI uri) throws URISyntaxException {
        String scheme = uri.getScheme();
        if (scheme == null
                || !"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            return false;
        }

        URI frontendUri = new URI(Config.APP_FRONTEND_URL);
        return isSameOrigin(uri, frontendUri);
    }

    private static boolean isSameOrigin(URI firstUri, URI secondUri) {
        return firstUri.getScheme().equalsIgnoreCase(secondUri.getScheme())
                && firstUri.getHost() != null
                && firstUri.getHost().equalsIgnoreCase(secondUri.getHost())
                && getPort(firstUri) == getPort(secondUri);
    }

    private static int getPort(URI uri) {
        if (uri.getPort() != -1) {
            return uri.getPort();
        }

        return "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
    }

}
