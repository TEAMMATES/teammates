package teammates.testing.concurrent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

/**
 * Tests to check that the functionality in the student's landing page are working fine
 * 
 * @author Shakthi
 *
 */

public class StudentHomePageFunctionalityTest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = Scenario.scenarioForPageVerification("target/test-classes/data/landing_page_testing.json");
	
	private static String TEST_STUDENT = scn.students.get(2).email;
	
	private static int FIRST_COURSE = 0;
	private static int SECOND_COURSE = 1;
	
	private static int FIRST_EVALUATION = 0;
	private static int SECOND_EVALUATION = 1;
	private static int THIRD_EVALUATION = 2;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== StudentLandingPageFunctionalityTest");
		bi = BrowserInstancePool.getBrowserInstance();
		
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
		
		bi.studentLogin(TEST_STUDENT, Config.inst().TEAMMATES_APP_PASSWD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.cleanupCourse(scn.course2.courseId);
		BrowserInstancePool.release(bi);
		System.out.println("StudentLandingPageFunctionalityTest ==========//");
	}
	
	//@Test
	public void testStudentJoinCourseLink() {
		System.out.println("StudentLandingPageFunctionalityTest: testStudentJoinCourseLink");
		
		bi.goToStudentHome();
		bi.waitAndClick(bi.studentJoinNewCourseLink);
		bi.verifyStudentCoursesPage();
	}
	
	@Test
	public void testViewCoursesLink() {
		System.out.println("StudentLandingPageFunctionalityTest: testViewCoursesLink");
		
		bi.goToStudentHome();
		bi.waitAndClick(bi.getStudentViewLink(FIRST_COURSE));
		bi.verifyStudentViewCourseDetailsPage();
		
		bi.goToStudentHome();
		bi.waitAndClick(bi.getStudentViewLink(SECOND_COURSE));
		bi.verifyStudentViewCourseDetailsPage();
	}
	
//	@Test
	public void testDoEvaluationLink() {
		System.out.println("StudentLandingPageFunctionalityTest: testDoEvaluationLink");
		
		bi.goToStudentHome();
		bi.waitAndClick(bi.getStudentDoEvaluationLink(FIRST_EVALUATION));
		bi.verifyStudentDoOrEditEvaluationPage();
	}
	
//	@Test
	public void testViewResultsLink() {
		System.out.println("StudentLandingPageFunctionalityTest: testViewResultsLink");
		
		// This should not be clickable
		bi.goToStudentHome();
		bi.waitAndClick(bi.getStudentViewResultsLink(FIRST_EVALUATION));
		bi.verifyStudentHomePage();
		
		bi.goToStudentHome();
		bi.waitAndClick(bi.getStudentViewResultsLink(SECOND_EVALUATION));

		bi.verifyStudentEvaluationResultsPage();
		
		// This should not be clickable
		bi.goToStudentHome();
		bi.waitAndClick(bi.getStudentViewResultsLink(THIRD_EVALUATION));
		bi.verifyStudentHomePage();
	}
	
	//@Test
	public void testEditEvaluationSubmissionLink() {
		System.out.println("StudentLandingPageFunctionalityTest: testEditEvaluationSubmissionLink");
		
		bi.goToStudentHome();
		bi.waitAndClick(bi.getStudentEditEvaluationSubmissionLink(FIRST_EVALUATION));
		bi.verifyStudentDoOrEditEvaluationPage();
		
		// This should not be clickable
		bi.goToStudentHome();
		bi.waitAndClick(bi.getStudentEditEvaluationSubmissionLink(SECOND_EVALUATION));
		bi.verifyStudentHomePage();

		// This should not be clickable
		bi.goToStudentHome();
		bi.waitAndClick(bi.getStudentEditEvaluationSubmissionLink(THIRD_EVALUATION));
		bi.verifyStudentHomePage();
	}
}