package teammates.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Holds {@link HttpServletRequest}-related helper functions.
 */
public final class HttpRequestHelper {

    private HttpRequestHelper() {
        // utility class
    }

    /**
     * Returns the bearer credential from an {@code Authorization} header field-value, or {@code null} if the value
     * is not a Bearer access token per OAuth 2.0 Bearer Token Usage (RFC 6750) and HTTP Authentication (RFC 7235):
     * {@code Bearer} scheme name (case-insensitive), then {@code 1*SP} (ASCII space only, not HTAB), then the token.
     */
    public static String parseBearerTokenFromAuthorizationHeader(String authorizationHeaderValue) {
        if (authorizationHeaderValue == null) {
            return null;
        }
        int schemeLen = Const.HttpAuthScheme.BEARER_SCHEME.length();
        if (authorizationHeaderValue.length() <= schemeLen) {
            return null;
        }
        if (!authorizationHeaderValue.regionMatches(true, 0, Const.HttpAuthScheme.BEARER_SCHEME, 0, schemeLen)) {
            return null;
        }
        if (authorizationHeaderValue.charAt(schemeLen) != ' ') {
            return null;
        }
        int i = schemeLen;
        while (i < authorizationHeaderValue.length() && authorizationHeaderValue.charAt(i) == ' ') {
            i++;
        }
        if (i >= authorizationHeaderValue.length()) {
            return null;
        }
        char c = authorizationHeaderValue.charAt(i);
        if (c != ' ' && Character.isWhitespace(c)) {
            return null;
        }
        return authorizationHeaderValue.substring(i).trim();
    }

    /**
     * Gets the parameters of the given HTTP request as key-value (possibly multi-values) mapping.
     */
    static Map<String, Object> getRequestParameters(HttpServletRequest req) {
        Map<String, Object> params = new HashMap<>();
        req.getParameterMap().forEach((key, values) -> {
            if (values.length == 1) {
                params.put(key, values[0]);
            } else {
                params.put(key, values);
            }
        });
        return params;
    }

    /**
     * Gets the headers of the given HTTP request as key-value (possibly multi-values) mapping.
     */
    static Map<String, Object> getRequestHeaders(HttpServletRequest req) {
        Map<String, Object> headers = new HashMap<>();
        Collections.list(req.getHeaderNames()).stream()
                // Do not include cookie header/secret keys in production for privacy reasons
                .filter(headerName -> Config.IS_DEV_SERVER || !Const.HeaderNames.COOKIE_KEY.equalsIgnoreCase(headerName))
                .filter(headerName -> Config.IS_DEV_SERVER || !Const.HeaderNames.BACKDOOR_KEY.equalsIgnoreCase(headerName))
                .filter(headerName -> Config.IS_DEV_SERVER || !Const.HeaderNames.CSRF_KEY.equalsIgnoreCase(headerName))
                .filter(headerName -> Config.IS_DEV_SERVER || !Const.HeaderNames.AUTHORIZATION_KEY.equalsIgnoreCase(headerName))
                .forEach(headerName -> {
                    List<String> headerValues = Collections.list(req.getHeaders(headerName));
                    if (headerValues.size() == 1) {
                        headers.put(headerName, headerValues.get(0));
                    } else {
                        headers.put(headerName, headerValues.toArray(new String[0]));
                    }
                });

        return headers;
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
