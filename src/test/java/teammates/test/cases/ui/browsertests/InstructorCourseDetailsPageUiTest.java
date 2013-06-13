package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.test.driver.BackDoor;
import teammates.test.driver.EmailAccount;
import teammates.test.driver.TestProperties;
import teammates.test.driver.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsViewPage;

/**
 * Tests 'Course Details' view for Instructors.
 * SUT {@link InstructorCourseDetailsPage}. <br>
 * This class uses real user accounts alice.tmms, benny.tmms and charlier.tmms.
 */
//TODO: change to use a single real user account (alice.tmms?)
public class InstructorCourseDetailsPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static InstructorCourseDetailsPage detailsPage;
	private static DataBundle testData;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadTestData("/InstructorCourseDetailsPageUiTest.json");
		restoreTestDataOnServer(testData);
		browser = BrowserPool.getBrowser();
	}
	
	@Test 
	public void allTests() throws Exception{
		testConent();
		//No input validation required
		testLinks();
		testRemindAction();
		testDeleteAction();
	}
	
	public void testConent() throws Exception{
		
		______TS("content: no students");
		
		//TODO: implement this
		
		______TS("content: multiple students");
		
		Url detailsPageUrl = new Url(Common.PAGE_INSTRUCTOR_COURSE_DETAILS)
		.withUserId(testData.instructors.get("CCDetailsUiT.instr").googleId)
		.withCourseId(testData.courses.get("CCDetailsUiT.CS2104").id);
		
		detailsPage = loginAdminToPage(browser, detailsPageUrl, InstructorCourseDetailsPage.class);
		
		detailsPage.verifyHtml("/instructorCourseDetailsPage.html");
		
		______TS("content: sorting");
		
		detailsPage.sortByStatus()
			.verifyTablePattern(2, "{*}Joined{*}Joined{*}Yet to join{*}Yet to join");
		detailsPage.sortByStatus()
			.verifyTablePattern(2, "{*}Yet to join{*}Yet to join{*}Joined{*}Joined");
		
		
		detailsPage.sortByName()
			.verifyTablePattern(1, "{*}Alice Betsy{*}Benny Charles{*}Charlie Davis{*}Danny Engrid");
		detailsPage.sortByName()
			.verifyTablePattern(1, "{*}Danny Engrid{*}Charlie Davis{*}Benny Charles{*}Alice Betsy");
		
		
		detailsPage.sortByTeam()
			.verifyTablePattern(0, "{*}Team 1{*}Team 1{*}Team 2{*}Team 2");
		detailsPage.sortByTeam()
			.verifyTablePattern(0, "{*}Team 2{*}Team 2{*}Team 1{*}Team 1");
		
	}
	
	public void testLinks(){
		
		______TS("link: view");
		
		StudentAttributes alice = testData.students.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2104");
		InstructorCourseStudentDetailsViewPage studentDetailsPage = detailsPage.clickViewStudent(alice.name);
		studentDetailsPage.verifyIsCorrectPage(alice.email);
		detailsPage = studentDetailsPage.goToPreviousPage(InstructorCourseDetailsPage.class);
		
		______TS("link: edit");
		
		StudentAttributes charlie = testData.students.get("charlie.tmms@CCDetailsUiT.CS2104");
		InstructorCourseStudentDetailsEditPage studentEditPage = detailsPage.clickEditStudent(charlie.name);
		studentEditPage.verifyIsCorrectPage(charlie.email);
		detailsPage = studentEditPage.goToPreviousPage(InstructorCourseDetailsPage.class);
	}

	public void testRemindAction() {

		String studentName = testData.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
		String studentEmail = testData.students
				.get("benny.tmms@CCDetailsUiT.CS2104").email;
		String otherStudentEmail = testData.students
				.get("charlie.tmms@CCDetailsUiT.CS2104").email;
		String registeredStudentEmail = testData.students
				.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2104").email;
		String courseId = testData.courses.get("CCDetailsUiT.CS2104").id;
		boolean isEmailEnabled = !TestProperties.inst().isDevServer();


		______TS("action: remind single student");

		detailsPage.clickRemindStudentAndCancel(studentName);
		if (isEmailEnabled) {
			assertFalse(didStudentReceiveReminder(courseId, studentEmail));
		}
		

		detailsPage.clickRemindStudentAndConfirm(studentName);
		if (isEmailEnabled) {
			assertTrue(didStudentReceiveReminder(courseId, studentEmail));
		}
		
		// Hiding of the 'Send invite' link is already covered by content test.
		//  (i.e., they contain cases of both hidden and visible 'Send invite' links.

		______TS("action: remind all");

		//TODO: also check for click and cancel
		
		detailsPage.clickRemindAllAndConfirm();
		
		if (isEmailEnabled) {
			// verify an unregistered student received reminder
			assertTrue(didStudentReceiveReminder(courseId, otherStudentEmail));
			// verify a registered student did not receive a reminder
			assertFalse(didStudentReceiveReminder(courseId, registeredStudentEmail));
		}
	}

	public void testDeleteAction() throws Exception{
		
		______TS("action: delete");
		
		String studentName = testData.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
		String studentEmail = testData.students.get("benny.tmms@CCDetailsUiT.CS2104").email;
		String courseId = testData.courses.get("CCDetailsUiT.CS2104").id;
		
		detailsPage.clickDeleteAndCancel(studentName);
		assertNotNull(BackDoor.getStudent(courseId, studentEmail));

		detailsPage.clickDeleteAndConfirm(studentName)
			.verifyHtml("/instructorCourseDetailsStudentDeleteSuccessful.html");
	}
	
	private boolean didStudentReceiveReminder(String courseId, String studentEmail) {
		String studentPassword = TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS;
		String keyToSend = Common.encrypt(BackDoor.getKeyForStudent(courseId, studentEmail));
	
		waitFor(5000); //TODO: replace this with a more efficient check
		String keyReceivedInEmail = EmailAccount.getRegistrationKeyFromGmail(
				studentEmail, studentPassword, courseId);
		return (keyToSend.equals(keyReceivedInEmail));
	}

	@AfterClass
		public static void classTearDown() throws Exception {
			BrowserPool.release(browser);
		}
}