package teammates.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public final class HttpRequestHelper {

    private HttpRequestHelper() {
        // utility class
    }

    /**
     * Returns the first value for the key in the parameter map, or null if key not found.
     *
     * @param paramMap A parameter map (e.g., the kind found in HttpServletRequests)
     */
    public static String getValueFromParamMap(Map<String, String[]> paramMap, String key) {
        return paramMap.getOrDefault(key, new String[] { null })[0];
    }

    public static String getRequestParametersAsString(HttpServletRequest req) {
        String requestParameters = ((Map<String, String[]>) req.getParameterMap()).entrySet().stream()
                .map(kv -> kv.getKey() + "::" + Arrays.stream(kv.getValue()).collect(Collectors.joining("//")))
                .collect(Collectors.joining(", "));

        return "{" + requestParameters + "}";
    }

    /**
     * Returns the URL used for the HTTP request but without the domain, e.g. "/page/studentHome?user=james"
     */
    public static String getRequestedUrl(HttpServletRequest req) {
        String link = req.getRequestURI();
        String query = req.getQueryString();

        if (query != null && !query.trim().isEmpty()) {
            return link + "?" + query;
        }
        return link;
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
