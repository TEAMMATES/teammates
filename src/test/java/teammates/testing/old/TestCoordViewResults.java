package teammates.testing.old;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import teammates.testing.lib.TMAPI;

/**
 * Setup: evaluation ready for view Test: 1.summary list by reviewer 2.summary
 * list by reviewee 3.detail list by reviewer 4.detail list by reviewee
 * 
 * @author xialin
 * 
 */

public class TestCoordViewResults extends BaseTest {

	@BeforeClass
	public static void classSetup() throws IOException {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(sc.students, sc.course.courseId,
				sc.evaluation.name);
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation.name);

		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	@Test
	public void testViewSummaryByReviewer() throws Exception {
		cout("TestCoordResultsView: View Report Summary by Reviewer");

		// Click Evaluation Tab
		gotoEvaluations();

		// click 'View Results':
		clickEvaluationViewResults(0);

		// click 'View' 1st student:
		clickReviewerSummaryView(0);

		// click 'Next':
		waitAndClick(resultNextButton);

		// click 'Previous':
		waitAndClick(resultPreviousButton);

		// click 'Back':
		waitAndClick(resultBackButton);

		waitAndClick(resultStudentSorting);
		waitAndClick(resultSubmittedSorting);
		waitAndClick(resultTeamSorting);

		// click 'Back':
		waitAndClick(resultBackButton);
	}

	@Test
	public void testViewSummaryByReviewee() throws Exception {
		cout("TestCoordResultsView: testViewSummaryByReviewee ");
		// Click Evaluation Tab
		gotoEvaluations();
		
		// click 'View Results':
		clickEvaluationViewResults(0);

		// click 'Reviewee' Radio Button:
		waitAndClick(resultRevieweeRadio);

		// click 'View' 1st student:
		clickReviewerSummaryView(0);

		// click 'Previous':
		waitAndClick(resultPreviousButton);

		// click 'Next':
		waitAndClick(resultNextButton);

		// click 'Back':
		waitAndClick(resultBackButton);

		waitAndClick(resultStudentSorting);
		waitAndClick(resultClaimedSorting);
		waitAndClick(resultDifferenceSorting);
		waitAndClick(resultTeamSorting);

		// click 'Back':
		waitAndClick(resultBackButton);

	}

	@Test
	public void testViewDetailByReviewer() throws Exception {
		cout("TestCoordResultsView: test view detail by reviewer");
		// Click Evaluation Tab
		gotoEvaluations();
		
		// click 'View Results':
		clickEvaluationViewResults(0);

		// click 'Detail':
		waitAndClick(resultDetailRadio);

		// click 'Back to Top':
		waitAndClick(resultTopButton);

		// click 'Back':
		waitAndClick(resultBackButton);
	}

	@Test
	public void testViewDetailByReviewee() throws Exception {
		cout("TestCoordResultsView: test view detail by reviewee");
		// Click Evaluation Tab
		gotoEvaluations();
		
		// click 'View Results':
		clickEvaluationViewResults(0);

		// click 'Detail' and 'Reviewee':
		waitAndClick(resultRevieweeRadio);
		waitAndClick(resultDetailRadio);

		// click 'Back to Top':
		waitAndClick(resultTopButton);

		// click 'Back':
		waitAndClick(resultBackButton);

	}

	@Test
	public void testPublishButton() throws Exception {
		cout("TestCoordResultsView: test publish");
		// Click Evaluation Tab
		gotoEvaluations();
		
		// click 'View Results':
		clickEvaluationViewResults(0);

		System.out.println("testPublishButton: Publish Button");
		// click 'Publish' and select 'No':
		waitAndClickAndCancel(resultPublishButton);

		// check 'button_publish' remain [Publish]
		assertEquals("Publish", getElementValue(resultPublishButton));

		// click 'Publish' button and select 'Yes':
		waitAndClickAndConfirm(resultPublishButton);

		System.out.println("testPublishButton: Unpublish Button");
		// click 'Unpublish' button and select 'No':
		waitAndClickAndCancel(resultPublishButton);

		// check 'button_publish' update to [Unpublish]
		assertEquals("Unpublish", getElementValue(resultPublishButton));

		// click 'Unpublish' button and select 'Yes':
		waitAndClickAndConfirm(resultPublishButton);
	}
}