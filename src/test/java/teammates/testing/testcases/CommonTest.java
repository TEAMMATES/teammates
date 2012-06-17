package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;

public class CommonTest extends BaseTestCase {
	@BeforeClass
	public static void setUp() throws Exception {
		printTestClassHeader();
		turnLogginUp(Common.class);
	}

	@Test
	public void testGenerateStringOfLength() {
		printTestCaseHeader();
		assertEquals(5, Common.generateStringOfLength(5).length());
		assertEquals(0, Common.generateStringOfLength(0).length());
	}

	@Test
	public void testIsWhiteSpace() {
		printTestCaseHeader();
		assertEquals(true, Common.isWhiteSpace(""));
		assertEquals(true, Common.isWhiteSpace("       "));
		assertEquals(true, Common.isWhiteSpace("\t\n\t"));
		assertEquals(true, Common.isWhiteSpace(Common.EOL));
		assertEquals(true, Common.isWhiteSpace(Common.EOL + "   "));
	}

	@Test
	public void testAssertContains() {
		printTestCaseHeader();
		BaseTestCase.assertContains("404 Page Not Found",
				"Error: 404 Page Not Found. Check the URL.");
		BaseTestCase.assertContains("Fails on checking assert contains",
				"404 Page Not Found",
				"Error: 404 Page Not Found. Check the URL.");
	}

	@Test
	public void testAssertContainsRegex() throws Exception {
		printTestCaseHeader();
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

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(Common.class);
	}

}