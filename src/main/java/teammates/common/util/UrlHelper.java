package teammates.common.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for URL-related functions.
 */
public final class UrlHelper {

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
     * Sanitize the given URL to prevent HTTP response splitting.
     */
    public static String getSanitizedRedirectUrl(String url) {
        return url.replace("\r\n", "");
    }

}
