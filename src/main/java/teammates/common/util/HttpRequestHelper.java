package teammates.common.util;

import java.util.Enumeration;
import java.util.Map;

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
        String[] values = paramMap.get(key);
        return values == null ? null : values[0];
    }

    /**
     * Returns all values for the key in the parameter map, or null if key not found.
     *
     * @param paramMap A parameter map (e.g., the kind found in HttpServletRequests)
     */
    public static String[] getValuesFromParamMap(Map<String, String[]> paramMap, String key) {
        String[] values = paramMap.get(key);
        return values == null ? null : values;
    }

    /**
     * Returns the first value for the key in the request's parameter map, or null if key not found.
     *
     * @param req An HttpServletRequest which contains the parameters map
     */
    @SuppressWarnings("unchecked")
    public static String getValueFromRequestParameterMap(HttpServletRequest req, String key) {
        return getValueFromParamMap(req.getParameterMap(), key);
    }

    /**
     * Returns all values for the key in the request's parameter map, or null if key not found.
     *
     * @param req An HttpServletRequest which contains the parameters map
     */
    @SuppressWarnings("unchecked")
    public static String[] getValuesFromRequestParameterMap(HttpServletRequest req, String key) {
        return getValuesFromParamMap(req.getParameterMap(), key);
    }

    /**
     * Gets the parameter map from HttpServletRequest.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String[]> getParameterMap(HttpServletRequest req) {
        return (Map<String, String[]>) req.getParameterMap();
    }

    //TODO: rename to a better name
    public static String printRequestParameters(HttpServletRequest request) {
        StringBuilder requestParameters = new StringBuilder();
        requestParameters.append('{');
        for (Enumeration<?> f = request.getParameterNames(); f.hasMoreElements();) {
            String param = f.nextElement().toString();
            requestParameters.append(param).append("::");
            String[] parameterValues = request.getParameterValues(param);
            for (String parameterValue : parameterValues) {
                requestParameters.append(parameterValue).append("//");
            }
            requestParameters.setLength(requestParameters.length() - 2);
            requestParameters.append(", ");
        }
        if (!"{".equals(requestParameters.toString())) {
            requestParameters.setLength(requestParameters.length() - 2);
        }
        return requestParameters.append("}").toString();
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

        for (Cookie cookie : existingCookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }

        return null;
    }

}
