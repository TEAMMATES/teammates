package teammates.test.cases.testdriver;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;

import teammates.test.cases.BaseTestCase;
import teammates.test.driver.TestProperties;

public class TestPropertiesTest extends BaseTestCase {
    @Test
    public void testExtractVersionNumber(){
        AssertJUnit.assertEquals("4.18", TestProperties.extractVersionNumber("abc<version>4-18</version>xyz"));
    }
    
    @Test 
    public void testConstruction(){
        AssertJUnit.assertTrue(null!=TestProperties.inst().TEAMMATES_VERSION); 
    }
}
