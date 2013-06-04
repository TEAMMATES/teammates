package teammates.test.cases.common;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.test.cases.BaseTestCase;

public class FeedbackSessionAttributesTest extends BaseTestCase {
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
	}
	
	@Test
	public void testValidate() {
		// TODO: follow test sequence similar to evalTest 
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
	}
}
