package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Url;
import teammates.test.cases.BaseTestCase;

public class UrlTest extends BaseTestCase {
    
    @Test
    public void testTrimTrailingSlash(){
        assertEquals("abc.com", Url.trimTrailingSlash("abc.com/"));
        assertEquals("abc.com", Url.trimTrailingSlash("abc.com/ "));
        assertEquals("abc.com", Url.trimTrailingSlash("abc.com"));
    }

}
