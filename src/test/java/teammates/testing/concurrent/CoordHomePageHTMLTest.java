package teammates.testing.concurrent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Common;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

/**
 * Test to verify the source HTML of the coordinator landing page
 * 
 * @author Shakthi
 *
 */

public class CoordHomePageHTMLTest extends TestCase {
	static Scenario scn = Scenario.scenarioForPageVerification(Common.TEST_DATA_FOLDER+"landing_page_testing.json");
	static BrowserInstance bi;

	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.getBrowserInstance();
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.cleanupCourse(scn.course2.courseId);

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
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.cleanupCourse(scn.course2.courseId);
		if (bi.isElementPresent(bi.logoutTab))
			bi.logout();

		BrowserInstancePool.release(bi);
	}

	@Test
	public void verifyCoordLandingPageSuccessful() throws Exception {
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"coordHome.html");
	}
}
