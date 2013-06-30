package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.Url;
import teammates.common.datatransfer.DataBundle;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

/**
 * Loads the Mashup page for the tester to do a visual inspection.
 */
public class MashupPageUiTest extends BaseUiTestCase {
	private static Browser browser;


	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		DataBundle testData = loadDataBundle("/MashupPageUiTest.json");
		restoreTestDataOnServer(testData);
		browser = BrowserPool.getBrowser();
	}

	@Test
	public void loadWebpageCompilation() throws Exception {
		AppPage page = loginAdmin(browser);
		page.navigateTo(new Url(Common.PAGE_MASHUP));
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		//We do not release the browser instance here because we want the tester
		//  to see the loaded page.
	}

}