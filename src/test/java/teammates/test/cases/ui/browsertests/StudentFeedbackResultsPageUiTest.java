package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.test.driver.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentFeedbackResultsPage;

/**
 * Tests 'Feedback Results' view of students.
 * SUT: {@link StudentFeedbackResultsPage}.
 */
public class StudentFeedbackResultsPageUiTest extends BaseUiTestCase {

	private static DataBundle testData;
	private static Browser browser;
	private StudentFeedbackResultsPage resultsPage;
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/StudentFeedbackResultsPageUiTest.json");
		restoreTestDataOnServer(testData);

		browser = BrowserPool.getBrowser();		
	}
	
	@Test
	public void testAll() throws Exception {
			
		______TS("standard session results");
		
		resultsPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
		resultsPage.verifyHtml("/studentFeedbackResultsPageOpen.html");
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
	
	private StudentFeedbackResultsPage loginToStudentFeedbackSubmitPage(
			String studentName, String fsName) {
		Url editUrl = new Url(Common.PAGE_STUDENT_FEEDBACK_RESULTS)
				.withUserId(testData.students.get(studentName).googleId)
				.withCourseId(testData.feedbackSessions.get(fsName).courseId)
				.withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
		return loginAdminToPage(browser, editUrl,
				StudentFeedbackResultsPage.class);
	}

}