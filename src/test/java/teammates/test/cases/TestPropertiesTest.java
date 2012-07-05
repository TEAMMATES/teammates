package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.Test;

import teammates.test.driver.TestProperties;

public class TestPropertiesTest extends BaseTestCase {
	@Test
	public void testExtractVersionNumber(){
		assertEquals("4.18", TestProperties.extractVersionNumber("abc<version>4-18</version>xyz"));
	}
	
	@Test 
	public void testConstruction(){
		assertTrue(null!=TestProperties.inst().TEAMMATES_VERSION); 
	}
}
