package teammates.test.cases;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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
		 
		int totalCases = 109; 
		
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
