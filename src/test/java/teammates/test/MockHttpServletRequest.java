package teammates.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import teammates.common.util.Const;

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
    private String body;

    public MockHttpServletRequest(String method, String requestUrl, Map<String, List<String>> headers) {
        this.cookies = new ArrayList<>();
        this.headers = headers;
        this.params = new HashMap<>();
        this.method = method;
        this.requestUrl = requestUrl;
    }

    public MockHttpServletRequest(String method, String requestUrl) {
        this(method, requestUrl, new HashMap<>());
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
        return new HttpSession() {
            @Override
            public long getCreationTime() {
                return 0;
            }

            @Override
            public String getId() {
                return "1234";
            }

            @Override
            public long getLastAccessedTime() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public void setMaxInactiveInterval(int interval) {
                // not used
            }

            @Override
            public int getMaxInactiveInterval() {
                return 0;
            }

            @Override
            public HttpSessionContext getSessionContext() {
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public Object getValue(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String[] getValueNames() {
                return new String[0];
            }

            @Override
            public void setAttribute(String name, Object value) {
                // not used
            }

            @Override
            public void putValue(String name, Object value) {
                // not used
            }

            @Override
            public void removeAttribute(String name) {
                // not used
            }

            @Override
            public void removeValue(String name) {
                // not used
            }

            @Override
            public void invalidate() {
                // not used
            }

            @Override
            public boolean isNew() {
                return false;
            }
        };
    }

    @Override
    public String changeSessionId() {
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
    public boolean authenticate(HttpServletResponse httpServletResponse) {
        return false;
    }

    @Override
    public void login(String s, String s1) {
        // not used
    }

    @Override
    public void logout() {
        // not used
    }

    @Override
    public Collection<Part> getParts() {
        return new ArrayList<>();
    }

    @Override
    public Part getPart(String s) {
        return null;
    }

    /**
     * Adds Part to the request.
     */
    public void addPart(String key, Part part) {
        // not used
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
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
    public long getContentLengthLong() {
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
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.params.keySet());
    }

    @Override
    public String[] getParameterValues(String s) {
        return this.params.getOrDefault(s, new ArrayList<>()).toArray(new String[0]);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
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
        byte[] bytes = this.body == null ? new byte[] {} : this.body.getBytes(Const.ENCODING);
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), Const.ENCODING));
    }

    public void setBody(String body) {
        this.body = body;
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
    public Enumeration<Locale> getLocales() {
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

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

}
