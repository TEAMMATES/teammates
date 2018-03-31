package teammates.test.cases.util;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.Url;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.UrlExtension;

/**
 * SUT: {@link Url},
 *      {@link AppUrl}.
 */
public class UrlTest extends BaseTestCase {

    @Test
    public void testTrimTrailingSlash() {
        assertEquals("abc.com", Url.trimTrailingSlash("abc.com/"));
        assertEquals("abc.com", Url.trimTrailingSlash("abc.com/ "));
        assertEquals("abc.com", Url.trimTrailingSlash("abc.com"));
    }

    @Test
    public void testToString() {

        ______TS("local file URL (no clear distinction on its base URL)");

        Url url = new Url("file:///C:/path/to/file.ext");
        assertEquals("/C:/path/to/file.ext", url.toString());
        assertEquals("file:///C:/path/to/file.ext", url.toAbsoluteString());

        ______TS("web URL with no relative path");

        url = new Url("http://www.google.com");
        assertEquals("", url.toString());
        assertEquals("http://www.google.com", url.toAbsoluteString());

        ______TS("typical web URL");

        url = new Url("http://www.google.com/page?key1=value1");
        assertEquals("/page?key1=value1", url.toString());
        assertEquals("http://www.google.com/page?key1=value1", url.toAbsoluteString());

        ______TS("malformed URL: no protocol");

        try {
            url = new Url("www.google.com/page");
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            ignoreExpectedException();
        }

        ______TS("malformed URL: unknown protocol");

        try {
            url = new Url("randomprotocol://www.google.com/page");
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            ignoreExpectedException();
        }

    }

    @Test
    public void testGetParameter() {
        Url url = new Url("http://www.google.com/page?key1=value1&key2=value2&key1=newvalue1");
        assertEquals("value1", url.get("key1"));
        assertEquals("value2", url.get("key2"));
        assertNull(url.get("y1"));
        assertNull(url.get("key4"));
    }

    @Test
    public void testAppendParameters() {

        ______TS("static method addParamToUrl");

        String url = "http://www.google.com";
        assertEquals(url, Url.addParamToUrl(url, null, "value"));
        assertEquals(url, Url.addParamToUrl(url, "", "value"));
        assertEquals(url, Url.addParamToUrl(url, "key", null));
        assertEquals(url, Url.addParamToUrl(url, "key", ""));
        assertEquals(url + "?key1=value1", Url.addParamToUrl(url, "key1", "value1"));
        url = "http://www.google.com?key1=value1";
        assertEquals(url + "&key2=value2", Url.addParamToUrl(url, "key2", "value2"));
        url = "http://www.google.com?key1=value1&key2=value2";
        assertEquals(url, Url.addParamToUrl(url, "key1", "newvalue1"));
        assertEquals(url, Url.addParamToUrl(url, "key2", "newvalue2"));
        assertEquals(url + "&key3=" + SanitizationHelper.sanitizeForUri("#& ?"), Url.addParamToUrl(url, "key3", "#& ?"));

        ______TS("in-place method withParam");

        Url newUrl = new Url("http://www.google.com/page");
        newUrl.withParam("key1", "value1");
        assertEquals("/page?key1=value1", newUrl.toString());
        newUrl.withParam("key1", "newvalue1");
        assertEquals("http://www.google.com/page?key1=value1", newUrl.toAbsoluteString());
    }

    @Test
    public void testAppUrlAssertion() {

        ______TS("typical non-empty case");

        AppUrl url = new AppUrl("http://www.google.com/page?key1=value1");
        assertEquals("/page?key1=value1", url.toString());
        assertEquals("http://www.google.com/page?key1=value1", url.toAbsoluteString());

        ______TS("empty path case");

        url = new AppUrl("http://www.google.com");
        assertEquals("", url.toString());
        assertEquals("http://www.google.com", url.toAbsoluteString());

        ______TS("malformed URL: not http(s)");

        try {
            url = new AppUrl("file:///C:/path/to/file.ext");
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            ignoreExpectedException();
        }

    }

    @Test
    public void testGetRelativePath() throws Exception {

        ______TS("web URL with no relative path");

        String url = "http://www.google.com";
        assertEquals("", UrlExtension.getRelativePath(url));

        ______TS("typical web URL");

        url = "http://www.google.com/page?key1=value1";
        assertEquals("/page?key1=value1", UrlExtension.getRelativePath(url));

    }

}
