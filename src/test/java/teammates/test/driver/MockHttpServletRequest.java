package teammates.test.driver;

import java.io.BufferedReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Mocks {@link HttpServletRequest} for testing purpose.
 *
 * <p>Only important methods are modified here; everything else are auto-generated.
 */
public class MockHttpServletRequest implements HttpServletRequest {

    private List<Cookie> cookies;
    private Map<String, List<String>> headers;
    private Map<String, List<String>> params;
    private String method;
    private String requestUrl;
    private String requestedSessionId;

    public MockHttpServletRequest(String method, String requestUrl) {
        this.cookies = new ArrayList<>();
        this.headers = new HashMap<>();
        this.params = new HashMap<>();
        this.method = method;
        this.requestUrl = requestUrl;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return this.cookies.toArray(new Cookie[0]);
    }

    /**
     * Adds cookie to the request.
     */
    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    @Override
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        List<String> headerValues = this.headers.getOrDefault(s, new ArrayList<>());
        return headerValues.isEmpty() ? null : headerValues.get(0);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        return Collections.enumeration(this.headers.getOrDefault(s, new ArrayList<>()));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(this.headers.keySet());
    }

    /**
     * Adds header to the request.
     */
    public void addHeader(String key, String value) {
        this.headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    @Override
    public int getIntHeader(String s) {
        return 0;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return this.requestedSessionId;
    }

    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    @Override
    public String getRequestURI() {
        return requestUrl;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(requestUrl);
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) {
        // not used
    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() {
        return null;
    }

    @Override
    public String getParameter(String s) {
        String[] paramValues = getParameterValues(s);
        return paramValues.length == 0 ? null : paramValues[0];
    }

    @Override
    public Enumeration getParameterNames() {
        return Collections.enumeration(this.params.keySet());
    }

    @Override
    public String[] getParameterValues(String s) {
        return this.params.getOrDefault(s, new ArrayList<>()).toArray(new String[0]);
    }

    @Override
    public Map getParameterMap() {
        Map<String, String[]> result = new HashMap<>();
        this.params.forEach((key, values) -> result.put(key, values.toArray(new String[0])));
        return result;
    }

    /**
     * Adds key-value parameter to the request.
     */
    public void addParam(String key, String value) {
        this.params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {
        // not used
    }

    @Override
    public void removeAttribute(String s) {
        // not used
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return new RequestDispatcher() {
            @Override
            public void forward(ServletRequest request, ServletResponse response) {
                // not used
            }

            @Override
            public void include(ServletRequest request, ServletResponse response) {
                // not used
            }
        };
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

}
