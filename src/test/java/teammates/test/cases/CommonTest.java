package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Calendar;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;

public class CommonTest extends BaseTestCase {
	
	@BeforeClass
	public static void setUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(Common.class);
	}

	@Test
	public void testGenerateStringOfLength() {
		
		assertEquals(5, Common.generateStringOfLength(5).length());
		assertEquals(0, Common.generateStringOfLength(0).length());
	}

	@Test
	public void testIsWhiteSpace() {
		
		assertEquals(true, Common.isWhiteSpace(""));
		assertEquals(true, Common.isWhiteSpace("       "));
		assertEquals(true, Common.isWhiteSpace("\t\n\t"));
		assertEquals(true, Common.isWhiteSpace(Common.EOL));
		assertEquals(true, Common.isWhiteSpace(Common.EOL + "   "));
	}
	
	@Test
	public void testSanitizeGoogleId() {

		assertEquals("big-small.20_12", Common.sanitizeGoogleId(" big-small.20_12 @Gmail.COM \t\n"));
		assertEquals("user@hotmail.com", Common.sanitizeGoogleId(" user@hotmail.com \t\n"));
	}
	
	@Test
	public void testIsValidGoogleId() {

		assertEquals(true, Common.isValidGoogleId("   Hello.12-3_4  \t\n  "));
		assertEquals(true, Common.isValidGoogleId("  user@hotmail.com  \t\n"));
		assertEquals(false, Common.isValidGoogleId(" HI@GMAIL.com \t\n "));
		assertEquals(false, Common.isValidGoogleId("/wrong!user\\"));
		assertEquals(false, Common.isValidGoogleId("\t\n\t"));
	}

	@Test
	public void testAssertContains() {
		
		BaseTestCase.assertContains("404 Page Not Found",
				"Error: 404 Page Not Found. Check the URL.");
		BaseTestCase.assertContains("Fails on checking assert contains",
				"404 Page Not Found",
				"Error: 404 Page Not Found. Check the URL.");
	}

	@Test
	public void testAssertContainsRegex() throws Exception {
		
		BaseTestCase.assertContainsRegex("404 Page Not Found",
				"Error: 404 Page Not Found. Check the URL.");
		BaseTestCase.assertContainsRegex("Fails on checking assert contains regex",
				"404 Page Not Found",
				"Error: 404 Page Not Found. Check the URL.");

		String pageStr = Common.readFile(Common.TEST_PAGES_FOLDER
				+ "/commonAssertRegexTestPage.html");

		String inputStr = Common.readFile(Common.TEST_PAGES_FOLDER
				+ "/commonAssertRegexTestPart.html");

		BaseTestCase.assertContainsRegex(inputStr, pageStr);
		BaseTestCase.assertContainsRegex("Fails on checking assert contains regex",
				inputStr, pageStr);

		BaseTestCase.assertContainsRegex(
				"<div>{*}</div><p>!@#$%^&*(){}_+[]</p>",
				"<html><body><div>Testing</div><p>!@#$%^&*(){}_+[]</p><a href='index.html'>HOME</a></body></html>");
		BaseTestCase.assertContainsRegex("Fails on checking assert contains regex",
				"<div>{*}</div>",
				"<html><body><div>Testing</div><a href='index.html'>HOME</a></body></html>");
		
		BaseTestCase.assertContainsRegex(
				"<html>\n\t<body>\n\t\t<div{*}>Hello world!</div>\n\t</body>\n\t</html>",
				"<html><body><div style=\"display:none\">Hello world!</div></body></html>");
		BaseTestCase.assertContainsRegex("Fails on checking assert contains regex",
				"<html>\n\t<body>\n\t\t<div{*}>Hello world!</div>\n\t</body>\n\t</html>",
				"<html><body><div style=\"display:none\">Hello world!</div></body></html>");
	}
	
	@Test
	public void testFormatTimeForEvaluation(){
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.HOUR_OF_DAY, 9);
		assertEquals("9", Common.convertToOptionValueInTimeDropDown(c.getTime()));
		
		c.set(Calendar.HOUR_OF_DAY, 14);
		assertEquals("14", Common.convertToOptionValueInTimeDropDown(c.getTime()));
		
		c.set(Calendar.HOUR_OF_DAY, 22);
		c.set(Calendar.MINUTE, 59);
		assertEquals("22", Common.convertToOptionValueInTimeDropDown(c.getTime()));
		
		//special cases that returns 24
		
		c.set(Calendar.HOUR_OF_DAY, 0);
		assertEquals("24", Common.convertToOptionValueInTimeDropDown(c.getTime()));
		
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		assertEquals("24", Common.convertToOptionValueInTimeDropDown(c.getTime()));
		
	}
	
	@Test
	public void testTrimTrailingSlash(){
		assertEquals("abc.com", Common.trimTrailingSlash("abc.com/"));
		assertEquals("abc.com", Common.trimTrailingSlash("abc.com/ "));
		assertEquals("abc.com", Common.trimTrailingSlash("abc.com"));
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(Common.class);
	}

}