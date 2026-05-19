package teammates.common.util;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link Url}, {@link AppUrl}.
 */
public class UrlTest extends BaseTestCase {

    @Test
    public void testToString() {

        ______TS("local file URL (no clear distinction on its base URL)");

        Url url = new Url("file:///C:/path/to/file.ext");
        Assertions.assertEquals("/C:/path/to/file.ext", url.toString());
        Assertions.assertEquals("file:///C:/path/to/file.ext", url.toAbsoluteString());

        ______TS("web URL with no relative path");

        url = new Url("http://www.google.com");
        Assertions.assertEquals("", url.toString());
        Assertions.assertEquals("http://www.google.com", url.toAbsoluteString());

        ______TS("typical web URL");

        url = new Url("http://www.google.com/page?key1=value1");
        Assertions.assertEquals("/page?key1=value1", url.toString());
        Assertions.assertEquals("http://www.google.com/page?key1=value1", url.toAbsoluteString());

        ______TS("malformed URL: no protocol");

        Assertions.assertThrows(AssertionError.class, () -> new Url("www.google.com/page"));

        ______TS("malformed URL: unknown protocol");

        Assertions.assertThrows(AssertionError.class, () -> new Url("randomprotocol://www.google.com/page"));

    }

    @Test
    public void testAppendParameters() {

        ______TS("static method addParamToUrl");

        String url = "http://www.google.com";
        Assertions.assertEquals(url, Url.addParamToUrl(url, null, "value"));
        Assertions.assertEquals(url, Url.addParamToUrl(url, "", "value"));
        Assertions.assertEquals(url, Url.addParamToUrl(url, "key", null));
        Assertions.assertEquals(url, Url.addParamToUrl(url, "key", ""));
        Assertions.assertEquals(url + "?key1=value1", Url.addParamToUrl(url, "key1", "value1"));
        url = "http://www.google.com?key1=value1";
        Assertions.assertEquals(url + "&key2=value2", Url.addParamToUrl(url, "key2", "value2"));
        url = "http://www.google.com?key1=value1&key2=value2";
        Assertions.assertEquals(url, Url.addParamToUrl(url, "key1", "newvalue1"));
        Assertions.assertEquals(url, Url.addParamToUrl(url, "key2", "newvalue2"));
        Assertions.assertEquals(url + "&key3=" + SanitizationHelper.sanitizeForUri("#& ?"), Url.addParamToUrl(url, "key3", "#& ?"));

        ______TS("in-place method withParam");

        Url newUrl = new Url("http://www.google.com/page");
        newUrl.withParam("key1", "value1");
        Assertions.assertEquals("/page?key1=value1", newUrl.toString());
        newUrl.withParam("key1", "newvalue1");
        Assertions.assertEquals("http://www.google.com/page?key1=value1", newUrl.toAbsoluteString());
    }

    @Test
    public void testAppUrlAssertion() {

        ______TS("typical non-empty case");

        AppUrl url = new AppUrl("http://www.google.com/page?key1=value1");
        Assertions.assertEquals("/page?key1=value1", url.toString());
        Assertions.assertEquals("http://www.google.com/page?key1=value1", url.toAbsoluteString());

        ______TS("empty path case");

        url = new AppUrl("http://www.google.com");
        Assertions.assertEquals("", url.toString());
        Assertions.assertEquals("http://www.google.com", url.toAbsoluteString());

        ______TS("malformed URL: not http(s)");

        Assertions.assertThrows(AssertionError.class, () -> new AppUrl("file:///C:/path/to/file.ext"));

    }

}
