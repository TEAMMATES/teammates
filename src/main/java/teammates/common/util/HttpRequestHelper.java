package teammates.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Holds {@link HttpServletRequest}-related helper functions.
 */
public final class HttpRequestHelper {

    private HttpRequestHelper() {
        // utility class
    }

    /**
     * Gets the parameters of the given HTTP request as key-value (possibly multi-values) mapping string.
     */
    public static String getRequestParametersAsString(HttpServletRequest req) {
        return getDisplayedJsonInOneLine(req.getParameterMap());
    }

    /**
     * Gets the headers of the given HTTP request as key-value (possibly multi-values) mapping string.
     */
    public static String getRequestHeadersAsString(HttpServletRequest req) {
        Map<String, String[]> headers = new HashMap<>();
        Collections.list(req.getHeaderNames()).stream()
                // Do not include cookie header in production for privacy reasons
                .filter(headerName -> Config.isDevServer() || !"cookie".equalsIgnoreCase(headerName))
                .forEach(headerName -> {
                    headers.put(headerName,
                            Collections.list(req.getHeaders(headerName)).toArray(new String[0]));
                });

        return getDisplayedJsonInOneLine(headers);
    }

    private static String getDisplayedJsonInOneLine(Map<String, String[]> map) {
        Map<String, Object> transformed = new HashMap<>();
        map.forEach((key, values) -> {
            if (values.length != 0) {
                transformed.put(key, values.length == 1 ? values[0] : values);
            }
        });
        return JsonUtils.toCompactJson(transformed);
    }

    /**
     * Returns the cookie value, or null if said cookie does not exist.
     */
    public static String getCookieValueFromRequest(HttpServletRequest req, String cookieName) {
        Cookie[] existingCookies = req.getCookies();

        if (existingCookies == null) {
            return null;
        }

        return Arrays.stream(existingCookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the request body payload.
     */
    public static String getRequestBody(HttpServletRequest req) {
        try (BufferedReader br = req.getReader()) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            return "";
        }
    }

}
