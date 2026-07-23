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
     * Checks whether the given redirect URL is safe to use as a relative redirect target, and returns it if it is.
     *
     * <p>
     * If the provided redirectUrl is not safe, or isn't relative, it returns the {@link #DEFAULT_REDIRECT_URL}.
     */
    public static String getSafeRelativeRedirectUrl(String redirectUrl) {
        return isSafeRelativeRedirectUrl(redirectUrl) ? redirectUrl : DEFAULT_REDIRECT_URL;
    }

    /**
     * Checks whether the given relative URL is safe to use as a redirect target.
     *
     * <p>
     * Rejects absolute URLs.
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
