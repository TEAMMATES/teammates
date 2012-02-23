package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;
import static teammates.testing.lib.Utils.tprintln;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

public class CoordEvaluationResultsViewTest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");

	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.request();
		TMAPI.cleanupCourse(scn.course.courseId);

		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(scn.students, scn.course.courseId, scn.evaluation.name);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation.name);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		TMAPI.cleanupCourse(scn.course.courseId);
		BrowserInstancePool.release(bi);
	}
	
	@Test
	public void testCoordViewEvaluationResults() throws Exception {
		System.out.println("========== TestCoordCourse");

		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);

		testCoordViewSummaryByReviewerSuccessful();
		testCoordViewSummaryByRevieweeSuccessful();
		testCoordViewDetailByRevieweeSuccessful();
		testCoordViewDetailByReviewerSuccessful();
		testCoordClickPublishButtonSuccessful();

		bi.logout();

		System.out.println("TestCoordCourse ==========//");
	}

	//testCoordViewSummaryByReviewerSuccessful
	public void testCoordViewSummaryByReviewerSuccessful() throws Exception {
		tprintln("TestCoordResultsView: View Report Summary by Reviewer");

		// Click Evaluation Tab
		bi.gotoEvaluations();

		// click 'View Results':
		bi.clickEvaluationViewResults(scn.course.courseId, scn.evaluation.name);

		// click 'View' 1st student:
		bi.clickReviewerSummaryView(0);

		// click 'Next':
		bi.waitAndClick(bi.resultNextButton);

		// click 'Previous':
		bi.waitAndClick(bi.resultPreviousButton);

		// click 'Back':
		bi.waitAndClick(bi.resultBackButton);

		bi.waitAndClick(bi.resultStudentSorting);
		bi.waitAndClick(bi.resultSubmittedSorting);
		bi.waitAndClick(bi.resultTeamSorting);

		// click 'Back':
		bi.waitAndClick(bi.resultBackButton);
	}

	//testCoordViewSummaryByRevieweeSuccessful
	public void testCoordViewSummaryByRevieweeSuccessful() throws Exception {
		tprintln("TestCoordResultsView: testViewSummaryByReviewee ");
		
		// Click Evaluation Tab
		bi.gotoEvaluations();

		// click 'View Results':
		bi.clickEvaluationViewResults(scn.course.courseId, scn.evaluation.name);

		// click 'Reviewee' Radio Button:
		bi.waitAndClick(bi.resultRevieweeRadio);

		// click 'View' 1st student:
		bi.clickReviewerSummaryView(0);

		// click 'Previous':
		bi.waitAndClick(bi.resultPreviousButton);

		// click 'Next':
		bi.waitAndClick(bi.resultNextButton);

		// click 'Back':
		bi.waitAndClick(bi.resultBackButton);

		bi.waitAndClick(bi.resultStudentSorting);
		bi.waitAndClick(bi.resultClaimedSorting);
		bi.waitAndClick(bi.resultDifferenceSorting);
		bi.waitAndClick(bi.resultTeamSorting);

		// click 'Back':
		bi.waitAndClick(bi.resultBackButton);

	}

	//testCoordViewDetailByReviewerSuccessful
	public void testCoordViewDetailByReviewerSuccessful() throws Exception {
		tprintln("TestCoordResultsView: test view detail by reviewer");
		// Click Evaluation Tab
		bi.gotoEvaluations();

		// click 'View Results':
		bi.clickEvaluationViewResults(scn.course.courseId, scn.evaluation.name);

		// click 'Detail':
		bi.waitAndClick(bi.resultDetailRadio);

		// click 'Back to Top':
		bi.waitAndClick(bi.resultTopButton);

		// click 'Back':
		bi.waitAndClick(bi.resultBackButton);
	}

	//testCoordViewDetailByRevieweeSuccessful
	public void testCoordViewDetailByRevieweeSuccessful() throws Exception {
		tprintln("TestCoordResultsView: test view detail by reviewee");
		// Click Evaluation Tab
		bi.gotoEvaluations();

		// click 'View Results':
		bi.clickEvaluationViewResults(scn.course.courseId, scn.evaluation.name);

		// click 'Detail' and 'Reviewee':
		bi.waitAndClick(bi.resultRevieweeRadio);
		bi.waitAndClick(bi.resultDetailRadio);

		// click 'Back to Top':
		bi.waitAndClick(bi.resultTopButton);

		// click 'Back':
		bi.waitAndClick(bi.resultBackButton);

	}

	public void testCoordClickPublishButtonSuccessful() throws Exception {
		tprintln("TestCoordResultsView: test publish");
		// Click Evaluation Tab
		bi.gotoEvaluations();

		// click 'View Results':
		bi.clickEvaluationViewResults(scn.course.courseId, scn.evaluation.name);

		System.out.println("testPublishButton: Publish Button");
		// click 'Publish' and select 'No':
		bi.waitAndClickAndCancel(bi.resultPublishButton);

		// check 'button_publish' remain [Publish]
		assertEquals("Publish", bi.getElementValue(bi.resultPublishButton));

		// click 'Publish' button and select 'Yes':
		bi.waitAndClickAndConfirm(bi.resultPublishButton);

		System.out.println("testPublishButton: Unpublish Button");
		// click 'Unpublish' button and select 'No':
		bi.waitAndClickAndCancel(bi.resultPublishButton);

		// check 'button_publish' update to [Unpublish]
		assertEquals("Unpublish", bi.getElementValue(bi.resultPublishButton));

		// click 'Unpublish' button and select 'Yes':
		bi.waitAndClickAndConfirm(bi.resultPublishButton);
	}
	
}
