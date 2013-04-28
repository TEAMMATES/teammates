package teammates.test.cases;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.io.File;
import java.io.IOException;

import org.openqa.selenium.By;

import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;

public class AllJsTests extends BaseTestCase{
	
	//private static WebDriver driver;
	private static BrowserInstance bi;
	
	@BeforeClass
	public static void setUp() {
		printTestClassHeader();
		bi = BrowserInstancePool.getBrowserInstance();
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
		bi.goToUrl("file:///"+workingDirectory+"/src/test/javascript/AllJsUnitTests.html");

		bi.waitForElementPresent(By.id("qunit-testresult"));
		String expectedResultString = totalCasesExpectedToPass+" tests of "+totalCases+" passed";
		assertTrue(bi.getElementText(By.id("qunit-testresult")).contains(expectedResultString));
		
		print("As expected, "+expectedResultString);

	}

	@AfterClass
	public static void tearDown() {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
}
