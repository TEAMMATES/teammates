package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.driver.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.StudentHelpPage;
import teammates.test.pageobjects.StudentHomePage;

/**
 * Covers Homepage and Login page for students. Some part of it is using a 
 * real Google account alice.tmms. <br> 
 * SUT: {@link StudentHelpPage}.
 */
public class StudentHomePageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static DataBundle testData;
	private StudentHomePage studentHome;
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadTestData("/StudentHomePageUiTest.json");
		restoreTestDataOnServer(testData);
		browser = BrowserPool.getBrowser();
	}


	@Test	
	public void allTests() throws Exception{
		testContentAndLogin();
		
		testHelpLink();
		testViewTeamLinks();
		testSubmissionLinks();
		
		//no input validation in this page
		
		testJoinAction();
		
		testResultsLinks(); //doing this last because it depends on the 'testJoinAction()'
	}


	private void testContentAndLogin() throws Exception {
		
		______TS("content: no courses, 'welcome stranger' message");
		
		Url studentHomeUrl = new Url(Common.PAGE_STUDENT_HOME).withUserId("SHPUiT.stranger");
		studentHome = loginAdminToPage(browser, studentHomeUrl, StudentHomePage.class);
		studentHome.verifyHtml("/studentHomeHTMLEmpty.html");
		
		______TS("login");
		
		studentHome = HomePage.getNewInstance(browser)
	    		.clickStudentLogin()
	    		.loginAsStudent(
	    				"alice.tmms", 
	    				TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS);
			
		______TS("content: multiple courses");
		
		studentHome.verifyHtml("/studentHomeHTML.html");
	}


	private void testHelpLink() {
		
		______TS("link: help page");
		
		StudentHelpPage helpPage = studentHome.clickHelpLink();
		helpPage.closeCurrentWindowAndSwitchToParentWindow();
	}


	private void testViewTeamLinks() {
		// TODO:
		
	}


	private void testSubmissionLinks() {
		// TODO: check for disabling of links, both submit and edit links
		
	}


	private void testJoinAction() throws Exception {
		
		______TS("fail: invalid key");
		
		studentHome.fillKey("ThisIsAnInvalidKey");
		studentHome.clickJoinButton()
			.verifyHtml("/studentHomeInvalidKey.html");
		
		______TS("joining the first course");
	
		String courseId = testData.courses.get("SHomeUiT.CS2104").id;
		String studentEmail = testData.students.get("alice.tmms@SHomeUiT.CS2104").email;
		
		studentHome.fillKey(BackDoor.getKeyForStudent(courseId, studentEmail));
		studentHome.clickJoinButton()
			.verifyHtml("/studentHomeJoined.html");
	}


	private void testResultsLinks() {
		// TODO: check disabling of links too
		
	}


	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
}