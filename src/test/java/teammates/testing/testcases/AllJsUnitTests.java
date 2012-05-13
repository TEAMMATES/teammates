package teammates.testing.testcases;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;


import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

public class AllJsUnitTests extends BaseTestCase{
	
	//private static WebDriver driver;
	private static BrowserInstance bi;
	
	@BeforeClass
	public static void setUp() {
		printTestClassHeader("AllJsUnitTests");
//		System.out.println("Initializing Selenium");
//		driver = new FirefoxDriver();
		bi = BrowserInstancePool.getBrowserInstance();
	}

	

	@Test
	public void executeJsTests() throws IOException {
		 
		int totalCases = 43; 
		//one case is expected to fail due to javaScript syntax issues
		int totalCasesExpectedToPass = totalCases-1;
		
		System.out.println("Going to execute "+totalCasesExpectedToPass+" JavaScript Unit tests...");
		
		String workingDirectory = new File(".").getCanonicalPath();
		bi.goToUrl("file:///"+workingDirectory+"/src/test/java/teammates/testing/testcases/AllJsUnitTests.html");
		
//		driver.get("file:///"+workingDirectory+"/src/test/java/teammates/testing/testcases/AllJsUnitTests.html");		
		
		//wait until test result appear on the page, or timesout after 5 sec
//		(new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
//			public Boolean apply(WebDriver d) {
//				WebElement testResult = driver.findElement(By.id("qunit-testresult"));
//				return (testResult!=null);
//			}
//		});

		bi.waitForElementPresent(By.id("qunit-testresult"));
//		WebElement testResult = driver.findElement(By.id("qunit-testresult"));
		String expectedResultString = totalCasesExpectedToPass+" tests of "+totalCases+" passed";
		assertTrue(bi.getElementText(By.id("qunit-testresult")).contains(expectedResultString));
		System.out.println("As expected, "+expectedResultString);

	}

	@AfterClass
	public static void tearDown() {
//		bi.goToUrl(Config.inst().TEAMMATES_URL);
		BrowserInstancePool.release(bi);
		printTestClassFooter("AllJsUnitTests");
	}
}
