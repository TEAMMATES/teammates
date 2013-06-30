package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.FileHelper;
import teammates.common.StringHelper;
import teammates.common.TimeHelper;
import teammates.test.cases.BaseTestCase;

public class CommonTest extends BaseTestCase {
	
	@BeforeClass
	public static void setUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(Common.class);
	}

	@Test
	public void testGenerateStringOfLength() {
		
		assertEquals(5, StringHelper.generateStringOfLength(5).length());
		assertEquals(0, StringHelper.generateStringOfLength(0).length());
	}

	@Test
	public void testIsWhiteSpace() {
		
		assertEquals(true, StringHelper.isWhiteSpace(""));
		assertEquals(true, StringHelper.isWhiteSpace("       "));
		assertEquals(true, StringHelper.isWhiteSpace("\t\n\t"));
		assertEquals(true, StringHelper.isWhiteSpace(Common.EOL));
		assertEquals(true, StringHelper.isWhiteSpace(Common.EOL + "   "));
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

		String pageStr = FileHelper.readFile(Common.TEST_PAGES_FOLDER
				+ "/commonAssertRegexTestPage.html");

		String inputStr = FileHelper.readFile(Common.TEST_PAGES_FOLDER
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
		assertEquals("9", TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));
		
		c.set(Calendar.HOUR_OF_DAY, 14);
		assertEquals("14", TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));
		
		c.set(Calendar.HOUR_OF_DAY, 22);
		c.set(Calendar.MINUTE, 59);
		assertEquals("22", TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));
		
		//special cases that returns 24
		
		c.set(Calendar.HOUR_OF_DAY, 0);
		assertEquals("24", TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));
		
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		assertEquals("24", TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));
		
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
		assertEquals("", StringHelper.toString(strings, ""));
		assertEquals("", StringHelper.toString(strings, "<br>"));
		
		strings.add("aaa");
		assertEquals("aaa", StringHelper.toString(strings, ""));
		assertEquals("aaa", StringHelper.toString(strings, "\n"));
		assertEquals("aaa", StringHelper.toString(strings, "<br>"));
		
		strings.add("bbb");
		assertEquals("aaabbb", StringHelper.toString(strings, ""));
		assertEquals("aaa\nbbb", StringHelper.toString(strings, "\n"));
		assertEquals("aaa<br>bbb", StringHelper.toString(strings, "<br>"));
	}
	
	
	@Test
	public void testKeyEncryption() {
		String msg = "Test decryption";
		String decrptedMsg;
		
		decrptedMsg = StringHelper.decrypt(StringHelper.encrypt(msg));
		assertEquals(msg, decrptedMsg);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(Common.class);
	}

}