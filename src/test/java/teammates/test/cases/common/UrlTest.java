package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Sanitizer;
import teammates.common.util.Url;
import teammates.test.cases.BaseTestCase;

public class UrlTest extends BaseTestCase {
    
    @Test
    public void testTrimTrailingSlash(){
        assertEquals("abc.com", Url.trimTrailingSlash("abc.com/"));
        assertEquals("abc.com", Url.trimTrailingSlash("abc.com/ "));
        assertEquals("abc.com", Url.trimTrailingSlash("abc.com"));
    }

    @Test
    public void testToString() {
        
        ______TS("base URL with no path");
        
        Url url = new Url(null);
        assertEquals("", url.toString());
        assertEquals(Config.APP_URL, url.toAbsoluteString());
        
        ______TS("path with same base URL, will be trimmed");
        
        url = new Url(Config.APP_URL);
        assertEquals("", url.toString());
        assertEquals(Config.APP_URL, url.toAbsoluteString());
        
        ______TS("path with different base URL");
        
        url = new Url("/page");
        assertEquals("/page", url.toString());
        assertEquals(Config.APP_URL + "/page", url.toAbsoluteString());
    }
    
    @Test
    public void testGetParameter() {
        Url url = new Url("http://www.google.com/page?key1=value1&key2=value2&key1=newvalue1");
        assertEquals("value1", url.get("key1"));
        assertEquals("value2", url.get("key2"));
        assertEquals(null, url.get("y1"));
        assertEquals(null, url.get("key4"));
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
        url += "?key1=value1";
        assertEquals(url + "&key2=value2", Url.addParamToUrl(url, "key2", "value2"));
        url += "&key2=value2";
        assertEquals(url, Url.addParamToUrl(url, "key1", "newvalue1"));
        assertEquals(url, Url.addParamToUrl(url, "key2", "newvalue2"));
        assertEquals(url + "&key3=" + Sanitizer.sanitizeForUri("#& ?"), Url.addParamToUrl(url, "key3", "#& ?"));
        
        ______TS("in-place method withParam");
        
        Url newUrl = new Url("/page");
        newUrl.withParam("key1", "value1");
        assertEquals("/page?key1=value1", newUrl.toString());
        newUrl.withParam("key1", "newvalue1");
        assertEquals(Config.APP_URL + "/page?key1=value1", newUrl.toAbsoluteString());
    }
    
}
