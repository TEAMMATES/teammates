package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link AppUrl}.
 */
public class AppUrlTest extends BaseTestCase {

    @Test
    public void testToString() {
        ______TS("web URL with no relative path");

        AppUrl url = new AppUrl("http://www.google.com");
        assertEquals("", url.toString());
        assertEquals("http://www.google.com", url.toAbsoluteString());

        ______TS("typical web URL");

        url = new AppUrl("http://www.google.com/page?key1=value1");
        assertEquals("/page?key1=value1", url.toString());
        assertEquals("http://www.google.com/page?key1=value1", url.toAbsoluteString());

        ______TS("malformed URL: no protocol");

        assertThrows(AssertionError.class, () -> new AppUrl("www.google.com/page"));

        ______TS("malformed URL: unknown protocol");

        assertThrows(AssertionError.class, () -> new AppUrl("randomprotocol://www.google.com/page"));

    }

    @Test
    public void testAppendParameters() {

        ______TS("static method addParamToUrl");

        String url = "http://www.google.com";
        assertEquals(url, AppUrl.addParamToUrl(url, null, "value"));
        assertEquals(url, AppUrl.addParamToUrl(url, "", "value"));
        assertEquals(url, AppUrl.addParamToUrl(url, "key", null));
        assertEquals(url, AppUrl.addParamToUrl(url, "key", ""));
        assertEquals(url + "?key1=value1", AppUrl.addParamToUrl(url, "key1", "value1"));
        url = "http://www.google.com?key1=value1";
        assertEquals(url + "&key2=value2", AppUrl.addParamToUrl(url, "key2", "value2"));
        url = "http://www.google.com?key1=value1&key2=value2";
        assertEquals(url, AppUrl.addParamToUrl(url, "key1", "newvalue1"));
        assertEquals(url, AppUrl.addParamToUrl(url, "key2", "newvalue2"));
        assertEquals(url + "&key3=" + SanitizationHelper.sanitizeForUri("#& ?"), AppUrl.addParamToUrl(url, "key3", "#& ?"));

        ______TS("in-place method withParam");

        AppUrl newUrl = new AppUrl("http://www.google.com/page");
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

        assertThrows(AssertionError.class, () -> new AppUrl("file:///C:/path/to/file.ext"));

    }

}
