package teammates.test.pageobjects;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import teammates.common.util.StringHelper;

public class PageDataTest {
    
    @Test
    public void testTruncate() {
        AssertJUnit.assertEquals("1234567...", StringHelper.truncate("1234567890xxxx", 10));
        AssertJUnit.assertEquals("1234567890", StringHelper.truncate("1234567890", 10));
        AssertJUnit.assertEquals("123456789", StringHelper.truncate("123456789", 10));
    }

}
