package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

/**
 * Loads all JavaScript unit tests (done in QUnit) into a browser window and
 * ensures all tests passed. This class is not using the PageObject pattern
 * because it is not a regular UI test.
 */
public class AllJsTests extends BaseUiTestCase{
	
	private static Browser browser;
	
	@BeforeClass
	public static void setUp() {
		printTestClassHeader();
		browser = BrowserPool.getBrowser();
	}

	@Test
	public void executeJsTests() throws IOException {
		/*
		 * Total cases has to be updated based on the number of new javascript
		 * tests added. This total case number should reflect the number of tests
		 * in AllJsUnitTests.html.
		 */
		int totalCases = 177;
		
		int totalCasesExpectedToPass = totalCases;
		
		print("Going to execute "+totalCasesExpectedToPass+" JavaScript Unit tests...");
		
		String workingDirectory = new File(".").getCanonicalPath();
		browser.driver.get("file:///"+workingDirectory+"/src/test/javascript/AllJsUnitTests.html");

		String expectedResultString = totalCasesExpectedToPass+" tests of "+totalCases+" passed";
		assertTrue(browser.driver.findElement(By.id("qunit-testresult")).getText().contains(expectedResultString));
		
		print("As expected, "+expectedResultString);

	}

	@AfterClass
	public static void tearDown() {
		BrowserPool.release(browser);
	}
}
