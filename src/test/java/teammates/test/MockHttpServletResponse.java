package teammates.test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

/**
 * Mocks {@link HttpServletResponse} for testing purpose.
 *
 * <p>Only important methods are modified here; everything else are auto-generated.
 */
public class MockHttpServletResponse implements HttpServletResponse {

    private int statusCode = HttpStatus.SC_OK;
    private String redirectUrl;
    private List<Cookie> cookies = new ArrayList<>();

    @Override
    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) {
        // not used
    }

    @Override
    public void sendError(int sc) {
        // not used
    }

    @Override
    public void sendRedirect(String location) {
        this.redirectUrl = location;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public void setDateHeader(String name, long date) {
        // not used
    }

    @Override
    public void addDateHeader(String name, long date) {
        // not used
    }

    @Override
    public void setHeader(String name, String value) {
        // not used
    }

    @Override
    public void addHeader(String name, String value) {
        // not used
    }

    @Override
    public void setIntHeader(String name, int value) {
        // not used
    }

    @Override
    public void addIntHeader(String name, int value) {
        // not used
    }

    @Override
    public void setStatus(int sc) {
        this.statusCode = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.statusCode = sc;
    }

    @Override
    public int getStatus() {
        return this.statusCode;
    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return null;
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(System.out);
    }

    @Override
    public void setCharacterEncoding(String charset) {
        // not used
    }

    @Override
    public void setContentLength(int len) {
        // not used
    }

    @Override
    public void setContentLengthLong(long l) {
        // not used
    }

    @Override
    public void setContentType(String type) {
        // not used
    }

    @Override
    public void setBufferSize(int size) {
        // not used
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() {
        // not used
    }

    @Override
    public void resetBuffer() {
        // not used
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {
        // not used
    }

    @Override
    public void setLocale(Locale loc) {
        // not used
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    public List<Cookie> getCookies() {
        return this.cookies;
    }

}
