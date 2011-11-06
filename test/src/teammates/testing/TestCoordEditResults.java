package teammates.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

/**
 * Case description: 4 students in a course, only 3 submitted evaluations on
 * time. The evaluation is closed and ready for view
 * 
 * Test: 1.edit empty evaluation record 2.edit submitted evaluation results
 * 3.publish evaluation results after editing
 * 
 * @author xialin
 * 
 */

public class TestCoordEditResults extends BaseTest {

	private final static int firstRow = 0;

	@BeforeClass
	public static void classSetup() throws IOException {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(sc.students.subList(1,
				 sc.students.size() - 1), sc.course.courseId,sc.evaluation.name);
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation.name);

		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	@Test
	public void testEditEmptyResult() throws Exception {

		// ..View results of a CLOSED evaluation:
		clickEvaluationTab();

		clickEvaluationViewResults(firstRow);

		// Submit without changing anything: failed
		cout("testEditEmptyResult: submit without changing any data");

		// ..Edit a submitted evaluation record:
		cout("testEditResultsByReviewer: Edit 1st empty evaluation record");
		// click 'Edit' 1st student:
		clickReviewerSummaryEdit(firstRow);
		// try 0: do nothing

		// click submit:
		waitAndClick(resultEditButton);
		waitForElementText(editEvaluationResultsStatusMessage, "Please fill in all the relevant fields.");

		Student s = sc.students.get(0);
		// try 1: fill in estimated contribution only
		for (int i = 0; i < s.team.students.size(); i++) {
			setSubmissionPoint(i, "80");
		}
		waitAndClick(resultEditButton);
		waitForElementText(statusMessage, "The particular evaluation results have been edited.");

		waitAndClick(resultIndividualEditButton);
		// try 2: fill in justifications only
		for (int i = 0; i < s.team.students.size(); i++) {
			// clean up contribution data added in try 1
			setSubmissionPoint(i, "-999");
			setSubmissionJustification(i, String.format("Edit:: Justification from %s to %s.", s.email, s.team.students.get(i).email));
			
		}
		waitAndClick(resultEditButton);
		waitForElementText(editEvaluationResultsStatusMessage, "Please fill in all the relevant fields.");

		// try 3: fill in commentsToStudent only
		for (int i = 0; i < s.team.students.size(); i++) {
			// clean up contribution data added in try 2
			setSubmissionJustification(i, null);
			setSubmissionComments( i, String.format("Edit:: Comments from %s to %s.", s.email, s.team.students.get(i).email));
		}
		waitAndClick(resultEditButton);
		waitForElementText(editEvaluationResultsStatusMessage, "Please fill in all the relevant fields.");

		// Submit with new data: successful
		System.out.println("testEditEmptyResult: submit with new data");
		for (int i = 0; i < s.team.students.size(); i++) {
			setSubmissionPoint(i, "100");
			setSubmissionJustification(i, String.format("Edit:: Justification from %s to %s.", s.email, s.team.students.get(i).email));
			setSubmissionComments( i, String.format("Edit:: Comments from %s to %s.", s.email, s.team.students.get(i).email));
		}

		// click 'submit':
		cout("testEditResultsByReviewer: Submit Edited Feedbacks.");

		waitAndClick(resultEditButton);
		waitForElementText(statusMessage, "The particular evaluation results have been edited.");

		// click 'Back':
		waitAndClick(resultBackButton);// [Back] to summary

		// click 'Back':
		waitAndClick(resultBackButton);

	}

	@Test
	public void testEditResultsByReviewer() throws Exception {

		cout("TestCoordResultsEdit: testEditResultsByReviewer");

		// ..View results of a CLOSED evaluation:
		gotoEvaluations();

		clickEvaluationViewResults(0);

		// ..Edit a submitted evaluation record:
		System.out.println("testEditResultsByReviewer: Edit Reviewer Feedback");
		// click 'Edit' 1st student:
		clickReviewerSummaryEdit(0);

		Student s = sc.students.get(0);
		for (int i = 0; i < s.team.students.size(); i++) {
			// selenium.select("points" + i, "value=30");
			setSubmissionPoint(i, "30");
			setSubmissionJustification(i, String.format(
					"Edit:: Justification from %s to %s.", s.email,
					s.team.students.get(i).email));
			setSubmissionComments(i, String.format(
					"Edit:: Comments from %s to %s.", s.email,
					s.team.students.get(i).email));
		}

		// click 'submit':
		System.out
				.println("testEditResultsByReviewer: Submit Edited Feedbacks.");

		waitAndClick(resultEditButton);// [submit]
		waitForElementText(statusMessage,
				"The particular evaluation results have been edited.");

		// ..Check content being updated:
		System.out
				.println("testEditResultsByReviewer: Check feedbacks have been updated");
		waitAndClick(resultIndividualEditButton);
		for (int i = 0; i < s.team.students.size(); i++) {
			assertEquals(getDropdownSelectedValue(getSubmissionPoint(i)), "30");

			assertEquals(getElementText(getSubmissionJustification(i)),
					String.format("Edit:: Justification from %s to %s.",
							s.email, s.team.students.get(i).email));
			assertEquals(getElementText(getSubmissionComments(i)),
					String.format("Edit:: Comments from %s to %s.", s.email,
							s.team.students.get(i).email));
		}
		waitAndClick(resultEditCancelButton);// [cancel]

		justWait();

		System.out
				.println("testEditResultsByReviewer: Click Next/Previous Button");
		// click 'Next':
		waitAndClick(resultNextButton);

		// click 'Edit' 2nd student:
		waitAndClick(resultIndividualEditButton);

		// click 'Cancel':
		waitAndClick(resultEditCancelButton);// [Cancel]

		// click 'Back':
		waitAndClick(resultBackButton);// [Back] to summary

		// click 'Back':
		waitAndClick(resultBackButton);
	}

	@Test
	public void testPublishButton() throws Exception {

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