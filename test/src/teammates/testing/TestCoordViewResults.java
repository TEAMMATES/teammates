package teammates.testing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

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
		cout("Test: View Report Summary by Reviewer");

		System.out.println("testViewSummaryByReviewer: ");
		// Click Evaluation Tab
		waitAndClick(By.className("t_evaluations"));

		// click 'View Results':
		waitAndClick(By.className("t_eval_view"));

		System.out.println("testViewSummaryByReviewer: View individual record");
		// click 'View' 1st student:
		waitAndClick(By.id("viewEvaluationResults0"));

		// click 'Next':
		waitAndClick(By.id("button_next"));

		// click 'Previous':
		waitAndClick(By.id("button_previous"));

		// click 'Back':
		waitAndClick(By.id("button_back"));

		System.out.println("testViewSummaryByReviewer: Sorting summary list");
		waitAndClick(By.id("button_sortname"));
		waitAndClick(By.id("button_sortsubmitted"));
		waitAndClick(By.id("button_sortteamname"));

		// click 'Back':
		waitAndClick(By.id("button_back"));
	}

	@Test
	public void testViewSummaryByReviewee() throws Exception {

		System.out.println("testViewSummaryByReviewee: ");
		// click 'View Results':
		waitAndClick(By.className("t_eval_view"));

		// click 'Reviewee' Radio Button:
		waitAndClick(By.id("radio_reviewee"));

		System.out
				.println("testViewSummaryByReviewee: Test View Individual Record");
		// click 'View' 1st student:
		waitAndClick(By.id("viewEvaluationResults0"));

		// click 'Previous':
		waitAndClick(By.id("button_previous"));

		// click 'Next':
		waitAndClick(By.id("button_next"));

		// click 'Back':
		waitAndClick(By.id("button_back"));

		System.out.println("testViewSummaryByReviewee: Sorting Summary List");
		waitAndClick(By.id("button_sortname"));
		waitAndClick(By.id("button_sortaverage"));
		waitAndClick(By.id("button_sortdiff"));
		waitAndClick(By.id("button_sortteamname"));

		// click 'Back':
		waitAndClick(By.id("button_back"));

	}

	@Test
	public void testViewDetailByReviewer() throws Exception {
		// click 'View Results':
		waitAndClick(By.className("t_eval_view"));

		// click 'Detail':
		waitAndClick(By.id("radio_detail"));

		// click 'Back to Top':
		waitAndClick(By.id("button_top"));

		// click 'Back':
		waitAndClick(By.id("button_back"));

	}

	@Test
	public void testViewDetailByReviewee() throws Exception {
		// click 'View Results':
		waitAndClick(By.className("t_eval_view"));

		// click 'Detail' and 'Reviewee':
		waitAndClick(By.id("radio_reviewee"));
		waitAndClick(By.id("radio_detail"));

		// click 'Back to Top':
		waitAndClick(By.id("button_top"));

		// click 'Back':
		waitAndClick(By.id("button_back"));

	}

	@Test
	public void testPublishButton() throws Exception {

		// click 'View Results':
		waitAndClick(By.className("t_eval_view"));

		System.out.println("testPublishButton: Publish Button");
		// click 'Publish' and select 'No':
		waitAndClickAndCancel(By.id("button_publish"));

		// check 'button_publish' remain [Publish]
		assertEquals("Publish", getElementValue(By.id("button_publish")));

		// click 'Publish' button and select 'Yes':
		waitAndClickAndConfirm(By.id("button_publish"));

		System.out.println("testPublishButton: Unpublish Button");
		// click 'Unpublish' button and select 'No':
		waitAndClickAndCancel(By.id("button_publish"));

		// check 'button_publish' update to [Unpublish]
		assertEquals("Unpublish", getElementValue(By.id("button_publish")));

		// click 'Unpublish' button and select 'Yes':
		waitAndClickAndConfirm(By.id("button_publish"));

	}

}