package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.test.cases.BaseTestCase;

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
	public void testAssertContains() {
		
		assertContains("404 Page Not Found",
				"Error: 404 Page Not Found. Check the URL.");
		assertContains("Fails on checking assert contains",
				"404 Page Not Found",
				"Error: 404 Page Not Found. Check the URL.");
	}

	@Test
	public void testAssertContainsRegex() throws Exception {
		
		assertContainsRegex("404 Page Not Found",
				"Error: 404 Page Not Found. Check the URL.");
		assertContainsRegex("Fails on checking assert contains regex",
				"404 Page Not Found",
				"Error: 404 Page Not Found. Check the URL.");

		String pageStr = Common.readFile(Common.TEST_PAGES_FOLDER
				+ "/commonAssertRegexTestPage.html");

		String inputStr = Common.readFile(Common.TEST_PAGES_FOLDER
				+ "/commonAssertRegexTestPart.html");

		assertContainsRegex(inputStr, pageStr);
		assertContainsRegex("Fails on checking assert contains regex",
				inputStr, pageStr);

		assertContainsRegex(
				"<div>{*}</div><p>!@#$%^&*(){}_+[]</p>",
				"<html><body><div>Testing</div><p>!@#$%^&*(){}_+[]</p><a href='index.html'>HOME</a></body></html>");
		assertContainsRegex("Fails on checking assert contains regex",
				"<div>{*}</div>",
				"<html><body><div>Testing</div><a href='index.html'>HOME</a></body></html>");
		
		assertContainsRegex(
				"<html>\n\t<body>\n\t\t<div{*}>Hello world!</div>\n\t</body>\n\t</html>",
				"<html><body><div style=\"display:none\">Hello world!</div></body></html>");
		assertContainsRegex("Fails on checking assert contains regex",
				"<html>\n\t<body>\n\t\t<div{*}>Hello world!</div>\n\t</body>\n\t</html>",
				"<html><body><div style=\"display:none\">Hello world!</div></body></html>");
	}
	
	@Test
	public void testFormatTimeForEvaluation(){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
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
	
	@Test 
	public void testToStringForStringLists(){
		ArrayList<String> strings = new ArrayList<String>();
		assertEquals("", Common.toString(strings, ""));
		assertEquals("", Common.toString(strings, "<br>"));
		
		strings.add("aaa");
		assertEquals("aaa", Common.toString(strings, ""));
		assertEquals("aaa", Common.toString(strings, "\n"));
		assertEquals("aaa", Common.toString(strings, "<br>"));
		
		strings.add("bbb");
		assertEquals("aaabbb", Common.toString(strings, ""));
		assertEquals("aaa\nbbb", Common.toString(strings, "\n"));
		assertEquals("aaa<br>bbb", Common.toString(strings, "<br>"));
	}
	
	
	@Test
	public void testKeyEncryption() {
		String msg = "Test decryption";
		String decrptedMsg;
		
		decrptedMsg = Common.decrypt(Common.encrypt(msg));
		assertEquals(msg, decrptedMsg);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(Common.class);
	}

}