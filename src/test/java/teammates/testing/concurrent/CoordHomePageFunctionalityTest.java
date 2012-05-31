package teammates.testing.concurrent;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Common;
import teammates.exception.NoAlertAppearException;
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
	static Scenario scn = Scenario.scenarioForPageVerification(Common.TEST_DATA_FOLDER+"landing_page_testing.json");
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordLandingPageFunctionalityTest");
		bi = BrowserInstancePool.getBrowserInstance();
		
		TMAPI.cleanupByCoordinator(scn.coordinator.username);

		// -----Course 1-----//
		TMAPI.createCourse(scn.course, scn.coordinator.username);
		TMAPI.enrollStudents(scn.course.courseId, scn.course.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		
		// ..evaluation 1 OPEN
		scn.evaluation.p2pcomments = "false";
		TMAPI.createEvaluation(scn.evaluation);
		System.out.println("Evaluation 1: "+scn.evaluation.name);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(scn.course.students, scn.course.courseId, scn.evaluation.name);

		// ..evaluation 2 PUBLISHED
		TMAPI.createEvaluation(scn.evaluation2);
		System.out.println("Evaluation 2: "+scn.evaluation2.name);
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
		System.out.println("Evaluation 3: "+scn.evaluation3.name);
		TMAPI.openEvaluation(scn.course2.courseId, scn.evaluation3.name);
		TMAPI.studentsSubmitFeedbacks(scn.course2.students, scn.course2.courseId, scn.evaluation3.name);
		TMAPI.closeEvaluation(scn.course2.courseId, scn.evaluation3.name);

		// ..evaluation 4 AWAITING
		TMAPI.createEvaluation(scn.evaluation4);
		System.out.println("Evaluation 4: "+scn.evaluation4.name);
		
		// ..evaluation 5 OPEN
		TMAPI.createEvaluation(scn.evaluation5);
		System.out.println("Evaluation 5: "+scn.evaluation5.name);
		TMAPI.openEvaluation(scn.course2.courseId, scn.evaluation5.name);
		
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
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
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.coordHomeAddNewCourseLink);
		bi.verifyCoordCoursesPage();
	}
	
	@Test
	public void testEnrollLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testEnrollLink");

		bi.goToCoordHome();

		bi.clickWithWait(bi.getCoordHomeCourseEnrollLinkLocator(scn.course.courseId));
		bi.verifyCoordCourseEnrollPage(scn.course.courseId);
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeCourseEnrollLinkLocator(scn.course2.courseId));
		bi.verifyCoordCourseEnrollPage(scn.course2.courseId);
	}
	
	@Test
	public void testViewCoursesLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testViewCoursesLink");

		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeCourseViewLinkLocator(scn.course.courseId));
		bi.verifyCoordCourseDetailsPage();
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeCourseViewLinkLocator(scn.course2.courseId));
		bi.verifyCoordCourseDetailsPage();
	}
	
	@Test
	public void testAddEvaluationLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testAddEvaluationLink");

		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeCourseAddEvaluationLinkLocator(scn.course.courseId));
		bi.verifyCoordEvaluationsPage();
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeCourseAddEvaluationLinkLocator(scn.course2.courseId));
		bi.verifyCoordEvaluationsPage();
	}
	
	@Test
	public void testDeleteCoursesLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testDeleteCoursesLink");

		bi.goToCoordHome();
		bi.clickCoordHomeCourseDeleteAndCancel(scn.course.courseId);
		
		bi.clickCoordHomeCourseDeleteAndCancel(scn.course2.courseId);
		
		/*
		 * NOTE: We could also do something like the below - WILL CHANGE AFTER CODE REVIEW, IF NEEDED
		 * 
		 * // Delete course
		 * bi.clickCoordHomeCourseDeleteAndConfirm(scn.course.courseId);
		 * bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_DELETED);
		 * assertFalse(bi.isHomeCoursePresent(scn.course.courseId, scn.course.courseName));
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
		 * assertFalse(bi.isCoordCoursePresent(scn.course.courseId, scn.course.courseName));
		 */
	}
	
	@Test
	public void testViewResultsLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testViewResultsLink");
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeEvaluationViewResultsLinkLocator(scn.course.courseId,scn.evaluation.name));
		bi.verifyCoordEvaluationResultsPage();
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeEvaluationViewResultsLinkLocator(scn.course.courseId,scn.evaluation2.name));
		bi.verifyCoordEvaluationResultsPage();
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeEvaluationViewResultsLinkLocator(scn.course2.courseId,scn.evaluation3.name));
		bi.verifyCoordEvaluationResultsPage();

		// This should not be clickable
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeEvaluationViewResultsLinkLocator(scn.course2.courseId,scn.evaluation4.name));
		bi.verifyCoordHomePage();
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeEvaluationViewResultsLinkLocator(scn.course2.courseId,scn.evaluation5.name));
		bi.verifyCoordEvaluationResultsPage();
	}
	
	@Test
	public void testEditEvaluationLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testEditEvaluationLink");

		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeEvaluationEditLinkLocator(scn.course.courseId,scn.evaluation.name));
		bi.verifyCoordEvaluationEditPage();
		
		// This should not be clickable
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeEvaluationEditLinkLocator(scn.course.courseId,scn.evaluation2.name));
		bi.verifyCoordHomePage();
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeEvaluationEditLinkLocator(scn.course2.courseId,scn.evaluation3.name));
		bi.verifyCoordEvaluationEditPage();
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeEvaluationEditLinkLocator(scn.course2.courseId,scn.evaluation4.name));
		bi.verifyCoordEvaluationEditPage();
		
		bi.goToCoordHome();
		bi.clickWithWait(bi.getCoordHomeEvaluationEditLinkLocator(scn.course2.courseId,scn.evaluation5.name));
		bi.verifyCoordEvaluationEditPage();
	}
	
	@Test
	public void testRemindEvaluationLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testRemindEvaluationLink");
		
		bi.goToCoordHome();
		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationRemindLinkLocator(scn.course.courseId,scn.evaluation.name));
		} catch (NoAlertAppearException e){
			assertTrue("Remind link unavailable on OPEN evaluation",false);
		}
		
		// This should not be clickable
		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationRemindLinkLocator(scn.course.courseId,scn.evaluation2.name));
			assertTrue("Remind link is available on PUBLISHED evaluation",false);
		} catch (NoAlertAppearException e){ }
		
		/*
		 * NOTE: We could also do something like the below - WILL CHANGE AFTER CODE REVIEW, IF NEEDED
		 * 
		 * bi.clickAndConfirm(bi.getCoordHomeEvaluationRemindLinkLocator(scn.course.courseId, scn.evaluation2.name));
		 * 
		 * // Confirm Email
		 * bi.waitForEmail();
		 * for (int i = 0; i < scn.students.size(); i++) {
		 *     assertEquals(scn.course.courseId, SharedLib.getEvaluationReminderFromGmail(scn.students.get(i).email, Config.inst().TEAMMATES_APP_PASSWD, scn.course.courseId, scn.evaluation2.name));
		 * }
		 * 
		 * However, due to efficiency considerations, we just click on the link, confirm that the pop-up appears and cancel the 
		 * Remind operation.
		 */
		
		// This should not be clickable
		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationRemindLinkLocator(scn.course2.courseId,scn.evaluation3.name));
			assertTrue("Remind link is available on CLOSED evaluation",false);
		} catch (NoAlertAppearException e){ }
		
		// This should not be clickable
		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationRemindLinkLocator(scn.course2.courseId,scn.evaluation4.name));
			assertTrue("Remind link is available on AWAITING evaluation",false);
		} catch (NoAlertAppearException e){ }
		
		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationRemindLinkLocator(scn.course2.courseId,scn.evaluation5.name));
		} catch (NoAlertAppearException e){
			assertTrue("Remind link unavailable on OPEN evaluation",false);
		}
	}
	
	@Test
	public void testPublishEvaluationLink() {
		System.out.println("CoordLandingPageFunctionalityTest: testPublishEvaluationLink");
		
		// This should not be clickable
		bi.goToCoordHome();
		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationPublishLinkLocator(scn.course.courseId,scn.evaluation.name));
			assertTrue("Publish link available on OPEN evaluation",false);
		} catch (NoAlertAppearException e){}
		
		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationUnpublishLinkLocator(scn.course.courseId,scn.evaluation2.name));
		} catch (NoAlertAppearException e){
			assertTrue("Unpublish link unavailable on PUBLISHED evaluation",false);
		}

		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationPublishLinkLocator(scn.course2.courseId,scn.evaluation3.name));
		} catch (NoAlertAppearException e){
			assertTrue("Publish link unavailable on CLOSED evaluation",false);
		}

		// This should not be clickable
		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationPublishLinkLocator(scn.course2.courseId,scn.evaluation4.name));
			assertTrue("Publish link available on AWAITING evaluation",false);
		} catch (NoAlertAppearException e){}
		
		/*
		 * NOTE: We could also do something like the below - WILL CHANGE AFTER CODE REVIEW, IF NEEDED
		 * 
		 * bi.clickAndConfirm(bi.getCoordHomeEvaluationUnpublishLinkLocator(scn.course.courseId, scn.evaluation3.name));
		 * bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_UNPUBLISHED);
		 * 
		 * // Check for status: PUBLISHED
		 * assertEquals(bi.EVAL_STATUS_CLOSED, bi.getHomeEvaluationStatus(scn.course.courseId, scn.evaluation3.name));
		 */

		// This should not be clickable
		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationPublishLinkLocator(scn.course2.courseId,scn.evaluation5.name));
			assertTrue("Publish link available on OPEN evaluation",false);
		} catch (NoAlertAppearException e){}
		
		/*
		 * NOTE: We could also do something like the below - WILL CHANGE AFTER CODE REVIEW, IF NEEDED
		 * 
		 * bi.clickAndConfirm(bi.getCoordHomeEvaluationPublish(scn.course.courseId, scn.evaluation3.name));
		 * bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_PUBLISHED);
		 * 
		 * // Check for status: PUBLISHED
		 * assertEquals(bi.EVAL_STATUS_PUBLISHED, bi.getHomeEvaluationStatus(scn.course.courseId, scn.evaluation3.name));
		 * 
		 * // Check if emails have been sent to all participants
		 * bi.waitForEmail();
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

		try{
			bi.clickAndConfirm(bi.getCoordHomeEvaluationDeleteLinkLocator(scn.course.courseId,scn.evaluation.name));
		} catch (NoAlertAppearException e){
			assertTrue("Delete link unavailable",false);
		}
		bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_EVALUATION_DELETED);

		// testing the delete link in two evaluations
		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationDeleteLinkLocator(scn.course.courseId,scn.evaluation2.name));
		} catch (NoAlertAppearException e){
			assertTrue("Delete link unavailable",false);
		}

		try{
			bi.clickAndCancel(bi.getCoordHomeEvaluationDeleteLinkLocator(scn.course2.courseId,scn.evaluation5.name));
		} catch (NoAlertAppearException e){
			assertTrue("Delete link unavailable",false);
		}
	}
}