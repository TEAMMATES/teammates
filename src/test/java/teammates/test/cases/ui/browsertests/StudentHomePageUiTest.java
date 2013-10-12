package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.TimeHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.LoginPage;
import teammates.test.pageobjects.StudentHelpPage;
import teammates.test.pageobjects.StudentHomePage;

/**
 * Covers Homepage and Login page for students. Some part of it is using a 
 * real Google account alice.tmms. <br> 
 * SUT: {@link StudentHelpPage} and {@link LoginPage} for students.
 */
public class StudentHomePageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static DataBundle testData;
	private StudentHomePage studentHome;
	private static FeedbackSessionAttributes gracedFeedbackSession;
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/StudentHomePageUiTest.json");
		restoreTestDataOnServer(testData);
		
		gracedFeedbackSession = BackDoor.getFeedbackSession("SHomeUiT.CS2104", "Graced Feedback Session");
		gracedFeedbackSession.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
		BackDoor.editFeedbackSession(gracedFeedbackSession);

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
		
		String unregUserId = TestProperties.inst().TEST_UNREG_ACCOUNT;
		String unregPassword = TestProperties.inst().TEST_UNREG_PASSWORD;
		BackDoor.deleteAccount(unregUserId); //delete account if it exists
		
		AppPage.logout(browser);
		studentHome = HomePage.getNewInstance(browser)
				.clickStudentLogin()
				.loginAsStudent(unregUserId, unregPassword);
		studentHome.verifyHtml("/studentHomeHTMLEmpty.html");
		
		______TS("login");
		
		studentHome = HomePage.getNewInstance(browser)
	    		.clickStudentLogin()
	    		.loginAsStudent(
	    				TestProperties.inst().TEST_STUDENT1_ACCOUNT, 
	    				TestProperties.inst().TEST_STUDENT1_PASSWORD);
			
		______TS("content: multiple courses");
		
		studentHome.verifyHtml("/studentHomeHTML.html");
		
		// TODO: test feedback session visibility
		// (i.e, same course but no qns for student to submit or view responses => NOT VISIBLE,
		// same course but closed, no submissions and will nvr have responses to view => NOT VISIBLE,
		// same course, all other conditions => VISIBLE, must test links)
	}


	private void testHelpLink() {
		
		______TS("link: help page");
		
		StudentHelpPage helpPage = studentHome.clickHelpLink();
		helpPage.closeCurrentWindowAndSwitchToParentWindow();
	}


	private void testViewTeamLinks() {
		// TODO: implement this
		
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