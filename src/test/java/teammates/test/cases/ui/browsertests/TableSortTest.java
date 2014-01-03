package teammates.test.cases.ui.browsertests;

import java.io.File;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Url;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

/** Covers the table sorting functionality
 */
public class TableSortTest extends BaseUiTestCase {
	private static Browser browser;
	private static AppPage page;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		browser = BrowserPool.getBrowser();		
		page = AppPage.getNewPageInstance(browser).navigateTo(new Url(getPath()));
	}
	

	@Test
	public void testTableSortingID() throws Exception{
		
		verifySortingOrder(By.id("button_sortid"),
				
				"-13.5",
				"-2",
				"-1.3",
				"-0.001",
				"0",
				"1",
				"2",
				"3",
				"10.01",
				"10.3",
				"10.35",
				"10.7",
				"15",
				"24",
				"33");
		
	}
	
	@Test
	public void testTableSortingName() throws Exception{
		
		verifySortingOrder(By.id("button_sortname"),
				
				"Ang Ji Kai",
				"Chin Yong Wei",
				"Chong Kok Wei",
				"Hou GuoChen",
				"Le Minh Khue",
				"Loke Yan Hao",
				"Luk Ming Kit",
				"Phan Thi Quynh Trang",
				"Shawn Teo Chee Yong",
				"Shum Chee How",
				"Sim ShengMing, Eugene",
				"Tan Guo Wei",
				"Teo Yock Swee Terence",
				"Yen Zi Shyun",
				"Zhang HaoQiang");

	}
	
	@Test
	public void testTableSortingDate() throws Exception{
		
		verifySortingOrder(	By.id("button_sortdate"),
				
				"04 May 2010",
				"21 August 2010",
				"06 April 2011",
				"14 May 2011",
				"12 December 2011",
				"01 January 2012",
				"02 January 2012",
				"01 February 2012",
				"03 February 2012",
				"05 March 2012",
				"10 May 2012",
				"25 July 2012",
				"17 September 2012",
				"01 January 2013",
				"05 June 2013");
	}


	@Test
	public void testTableSortingDiff() throws Exception{
		
		verifySortingOrder(By.id("button_sortDiff"),
				
				"-99%",
				"-20%",
				"-10%",
				"-2%",
				"-1%",
				"0%",
				"+1%",
				"+2%",
				"+3%",
				"+5%",
				"+25%",
				"+30%",
				"+99%",
				"N/A",
				"N/A");

	}
	
	@Test
	public void testTableSortingPoint() throws Exception{
		verifySortingOrder(By.id("button_sortPoint"),
		
				"E -99%",
				"E -21%",
				"E -10%",
				"E -4%",
				"E -2%",
				"E 0%",
				"E 0%",
				"E +5%",
				"E +20%",
				"E +20%",
				"E +99%",
				"N/S",
				"N/S",
				"N/S",
				"N/A");
	}
	
	private void verifySortingOrder(By sortIcon, String... values) {
		//check if the rows match the given order of values
		page.click(sortIcon);
		String searchString = "";
		for (int i = 0; i < values.length; i++) {
			searchString += values[i]+"{*}";
		}
		page.verifyContains(searchString);
		
		//click the sort icon again and check for the reverse order
		page.click(sortIcon);
		searchString = "";
		for (int i = values.length; i > 0; i--) {
			searchString += values[i-1]+"{*}";
		}
		page.verifyContains(searchString);
	}


	private static String getPath() throws Exception{
		String workingDirectory = new File(".").getCanonicalPath();
		return "file:///"+workingDirectory+"/src/test/resources/pages/tableSort.html";
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		BrowserPool.release(browser);
	}
}
