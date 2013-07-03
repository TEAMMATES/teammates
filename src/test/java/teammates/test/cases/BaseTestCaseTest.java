package teammates.test.cases;

import org.testng.annotations.Test;

import teammates.common.util.FileHelper;
import teammates.test.driver.TestProperties;

public class BaseTestCaseTest extends BaseTestCase {

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
	
		String pageStr = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER
				+ "/commonAssertRegexTestPage.html");
	
		String inputStr = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER
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

}
