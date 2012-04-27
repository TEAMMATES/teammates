package teammates.testing.concurrent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

/**
 * Tests to check that the functionality in the coordinator's landing page are working fine
 * 
 * @author Shakthi
 *
 */

public class CoordHomePageFunctionalityTest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = Scenario.scenarioForPageVerification("target/test-classes/data/landing_page_testing.json");
	
	private static int FIRST_COURSE = 0;
	private static int SECOND_COURSE = 1;
	
	private static int FIRST_EVALUATION = 0;
	private static int SECOND_EVALUATION = 1;
	private static int THIRD_EVALUATION = 2;
	private static int FOURTH_EVALUATION = 3;
	private static int FIFTH_EVALUATION = 4;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordLandingPageFunctionalityTest");
		bi = BrowserInstancePool.request();
		
		TMAPI.cleanupByCoordinator(scn.coordinator.username);

		// -----Course 1-----//
		TMAPI.createCourse(scn.course, scn.coordinator.username);
		TMAPI.enrollStudents(scn.course.courseId, scn.course.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		
		// ..evaluation 1 OPEN
		scn.evaluation.p2pcomments = "false";
		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(scn.course.students, scn.course.courseId, scn.evaluation.name);

		// ..evaluation 2 PUBLISHED
		TMAPI.createEvaluation(scn.evaluation2);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation2.name);
		TMAPI.studentsSubmitFeedbacks(scn.students.subList(1, scn.students.size() - 1), scn.course.courseId, scn.evaluation2.name);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation2.name);
		TMAPI.publishEvaluation(scn.course.courseId, scn.evaluation2.name);

		// -----Course 2-----//
		TMAPI.createCourse(scn.course2, scn.coordinator.username);
		TMAPI.enrollStudents(scn.course2.courseId, scn.course2.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course2.courseId);

		// ..evaluation 3 CLOSED
		TMAPI.createEvaluation(scn.evaluation3);
		TMAPI.openEvaluation(scn.course2.courseId, scn.evaluation3.name);
		TMAPI.studentsSubmitFeedbacks(scn.course2.students, scn.course2.courseId, scn.evaluation3.name);
		TMAPI.closeEvaluation(scn.course2.courseId, scn.evaluation3.name);

		// ..evaluation 4 AWAITING
		TMAPI.createEvaluation(scn.evaluation4);
		
		// ..evaluation 5 OPEN
		TMAPI.createEvaluation(scn.evaluation5);
		TMAPI.openEvaluation(scn.course2.courseId, scn.evaluation5.name);
		
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.cleanupCourse(scn.course2.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordLandingPageFunctionalityTest ==========//");
	}
	
	@Test
	public void testCoordAddCourseLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testCoordAddCourseLink");
		
		bi.waitAndClick(bi.coordAddNewCourseLink);
		bi.verifyCoordCoursesPage();
	}
	
	@Test
	public void testEnrolLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testEnrolLink");

		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordEnrolLink(FIRST_COURSE));
		bi.verifyCoordEnrolPage();
		
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordEnrolLink(SECOND_COURSE));
		bi.verifyCoordEnrolPage();
	}
	
	@Test
	public void testViewCoursesLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testViewCoursesLink");

		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordViewLink(FIRST_COURSE));
		bi.justWait();
		bi.verifyCoordViewCourseDetailsPage();
		
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordViewLink(SECOND_COURSE));
		bi.justWait();
		bi.verifyCoordViewCourseDetailsPage();
	}
	
	@Test
	public void testAddEvaluationLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testAddEvaluationLink");

		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordAddEvaluationLink(FIRST_COURSE));
		bi.justWait();
		bi.verifyCoordEvaluationsPage();
		
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordAddEvaluationLink(SECOND_COURSE));
		bi.justWait();
		bi.verifyCoordEvaluationsPage();
	}
	
	@Test
	public void testDeleteCoursesLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testDeleteCoursesLink");

		bi.goToCoordHome();
		bi.justWait();
		bi.clickAndCancelCoordCourseDelete(bi.getCoordDeleteLink(FIRST_COURSE));
		
		bi.goToCoordHome();
		bi.justWait();
		bi.clickAndCancelCoordCourseDelete(bi.getCoordDeleteLink(SECOND_COURSE));
		
		/*
		 * NOTE: We could also do something like the below - WILL CHANGE AFTER CODE REVIEW, IF NEEDED
		 * 
		 * // Delete course
		 * bi.waitForElementPresent(bi.getCourseID(scn.course.courseId));
		 * bi.clickAndConfirmCourseDelete(scn.course.courseId);
		 * bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_DELETED);
		 * assertFalse(bi.isCoordCoursePresent(scn.course.courseId, scn.course.courseName));
		 * 
		 * //Check that the evaluation has also been deleted
		 * bi.goToCoordEvaluations();
		 * assertFalse(bi.isEvaluationPresent(scn.course.courseId, scn.evaluation.name));
		 * 
		 * bi.logout();
		 * 
		 * // Check that the course has been deleted from student page
		 * bi.studentLogin(scn.students.get(FIRST_STUDENT).email, Config.inst().TEAMMATES_APP_PASSWD);
		 * bi.clickCoursesTab();
		 * bi.justWait();
		 * assertFalse(bi.isCoordCoursePresent(scn.course.courseId, scn.course.courseName));
		 */
	}
	
	@Test
	public void testViewResultsLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testViewResultsLink");
		
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordViewResultsLink(FIRST_EVALUATION));
		bi.justWait();
		bi.verifyCoordEvaluationResultsPage();
		
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordViewResultsLink(SECOND_EVALUATION));
		bi.justWait();
		bi.verifyCoordEvaluationResultsPage();
		
		// This should not be clickable
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordViewResultsLink(THIRD_EVALUATION));
		bi.justWait();
		bi.verifyCoordHomePage();
		
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordViewResultsLink(FOURTH_EVALUATION));
		bi.justWait();
		bi.verifyCoordEvaluationResultsPage();
		
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordViewResultsLink(FIFTH_EVALUATION));
		bi.justWait();
		bi.verifyCoordEvaluationResultsPage();
	}
	
	@Test
	public void testEditEvaluationLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testEditEvaluationLink");

		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordEditEvaluationLink(FIRST_EVALUATION));
		bi.justWait();
		bi.verifyCoordEditEvaluationPage();
		
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordEditEvaluationLink(SECOND_EVALUATION));
		bi.justWait();
		bi.verifyCoordEditEvaluationPage();
		
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordEditEvaluationLink(THIRD_EVALUATION));
		bi.justWait();
		bi.verifyCoordEditEvaluationPage();
		
		// This should not be clickable
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordEditEvaluationLink(FOURTH_EVALUATION));
		bi.justWait();
		bi.verifyCoordHomePage();
		
		bi.goToCoordHome();
		bi.waitAndClick(bi.getCoordEditEvaluationLink(FIFTH_EVALUATION));
		bi.justWait();
		bi.verifyCoordEditEvaluationPage();
	}
	
	@Test
	public void testRemindEvaluationLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testRemindEvaluationLink");
		
		bi.goToCoordHome();
		bi.justWait();
		bi.clickAndCancelCoordEvaluationRemind(bi.getCoordRemindEvaluationLink(FIRST_EVALUATION));
		
		bi.goToCoordHome();
		bi.justWait();
		bi.clickAndCancelCoordEvaluationRemind(bi.getCoordRemindEvaluationLink(SECOND_EVALUATION));
		
		/*
		 * NOTE: We could also do something like the below - WILL CHANGE AFTER CODE REVIEW, IF NEEDED
		 * 
		 * bi.clickAndConfirmEvaluationRemind(scn.course.courseId, scn.evaluation2.name);
		 * 
		 * // Confirm Email
		 * bi.justWait();
		 * for (int i = 0; i < scn.students.size(); i++) {
		 *     assertEquals(scn.course.courseId, SharedLib.getEvaluationReminderFromGmail(scn.students.get(i).email, Config.inst().TEAMMATES_APP_PASSWD, scn.course.courseId, scn.evaluation2.name));
		 * }
		 * 
		 * However, due to efficiency considerations, we just click on the link, confirm that the pop-up appears and cancel the 
		 * Remind operation.
		 */
		
		// This should not be clickable
		bi.goToCoordHome();
		bi.clickAndCancelCoordEvaluationRemind(bi.getCoordRemindEvaluationLink(THIRD_EVALUATION));
		bi.verifyCoordHomePage();
		
		// This should not be clickable
		bi.goToCoordHome();
		bi.clickAndCancelCoordEvaluationRemind(bi.getCoordRemindEvaluationLink(FOURTH_EVALUATION));
		bi.verifyCoordHomePage();
		
		// This should not be clickable
		bi.goToCoordHome();
		bi.clickAndCancelCoordEvaluationRemind(bi.getCoordRemindEvaluationLink(FIFTH_EVALUATION));
		bi.verifyCoordHomePage();
	}
	
	@Test
	public void testPublishEvaluationLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testPublishEvaluationLink");
		
		// This should not be clickable
		bi.goToCoordHome();
		bi.justWait();
		bi.waitAndClick(bi.getCoordPublishEvaluationLink(FIRST_EVALUATION));
		bi.verifyCoordHomePage();
		
		// This should not be clickable
		bi.goToCoordHome();
		bi.justWait();
		bi.waitAndClick(bi.getCoordPublishEvaluationLink(SECOND_EVALUATION));
		bi.verifyCoordHomePage();
		
		// This should not be clickable
		bi.goToCoordHome();
		bi.justWait();
		bi.waitAndClick(bi.getCoordPublishEvaluationLink(THIRD_EVALUATION));
		bi.verifyCoordHomePage();
		
		bi.goToCoordHome();
		bi.justWait();
		bi.clickAndCancelCoordEvaluationUnpublish(bi.getCoordPublishEvaluationLink(FOURTH_EVALUATION));
		
		/*
		 * NOTE: We could also do something like the below - WILL CHANGE AFTER CODE REVIEW, IF NEEDED
		 * 
		 * bi.clickAndConfirmEvaluationUnpublish(scn.course.courseId, scn.evaluation3.name);
		 * bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_UNPUBLISHED);
		 * 
		 * // Check for status: PUBLISHED
		 * assertEquals(bi.EVAL_STATUS_CLOSED, bi.getEvaluationStatus(scn.course.courseId, scn.evaluation3.name));
		 */
		
		bi.goToCoordHome();
		bi.clickAndCancelCoordEvaluationPublish(bi.getCoordPublishEvaluationLink(FIFTH_EVALUATION));
		
		/*
		 * NOTE: We could also do something like the below - WILL CHANGE AFTER CODE REVIEW, IF NEEDED
		 * 
		 * bi.clickAndConfirmEvaluationPublish(scn.course.courseId, scn.evaluation3.name);
		 * bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_PUBLISHED);
		 * 
		 * // Check for status: PUBLISHED
		 * assertEquals(bi.EVAL_STATUS_PUBLISHED, bi.getEvaluationStatus(scn.course.courseId, scn.evaluation3.name));
		 * 
		 * // Check if emails have been sent to all participants
		 * bi.waitAWhile(5000);
		 * for (Student s : scn.students) {
		 *     System.out.println("Checking " + s.email);
		 *     assertTrue(bi.checkResultEmailsSent(s.email, s.password, scn.course.courseId, scn.evaluation3.name));
		 * }
		 */
	}
	
	@Test
	public void testDeleteEvaluationLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testDeleteEvaluationLink");
		
		bi.goToCoordHome();
		
		bi.clickAndCancelCoordEvaluationDelete(bi.getCoordDeleteEvaluationLink(FIRST_EVALUATION));
		bi.justWait();
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_DELETED);

		// Due to the manner in which IDs are being assigned to evaluations in the home page, the ID of the originally 2nd
		// evaluation (and resp. 3rd, 4th and 5th evaluations) changes to 1st (and resp. 2nd, 3rd and 4th) after the previous
		// delete operation.
		// Hence, we use FIRST_EVALUATION, SECOND_EVALUATION, THIRD_EVALUATION and FOURTH_EVALUATION to refer to the original
		// 2nd, 3rd, 4th and 5th evaluations respectively in the below code.
		
		// testing the delete link in two evaluations
		bi.goToCoordHome();
		bi.justWait();
		bi.clickAndCancelCoordEvaluationDelete(bi.getCoordDeleteEvaluationLink(FIRST_EVALUATION));
		
		
		bi.goToCoordHome();
		bi.justWait();
		bi.clickAndCancelCoordEvaluationDelete(bi.getCoordDeleteEvaluationLink(FOURTH_EVALUATION));
	}
}