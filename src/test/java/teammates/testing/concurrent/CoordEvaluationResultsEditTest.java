package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

public class CoordEvaluationResultsEditTest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	private final static int FIRST_STUDENT = 0;

	@BeforeClass
	public static void classSetup() throws IOException {
		System.out.println("========== CoordEvaluationResultsEditTest");
		bi = BrowserInstancePool.request();

		TMAPI.cleanupCourse(scn.course.courseId);

		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(scn.students.subList(1, scn.students.size() - 1), scn.course.courseId, scn.evaluation.name);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation.name);
		
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);

		BrowserInstancePool.release(bi);
		System.out.println("CoordEvaluationResultsEditTest ==========//");
	}

	@Test
	public void CoordEditResults() throws Exception {
		
		testCoordEditEmptyResultSuccessful();
		testCoordEditResultsByReviewerSuccessful();
		
	}

	public void testCoordEditEmptyResultSuccessful() throws Exception {

		bi.clickEvaluationTab();
		bi.clickEvaluationViewResults(scn.course.courseId, scn.evaluation.name);

		// TODO: Submit without changing anything: failed
		System.out.println("testEditEmptyResult: submit without changing any data");

		// ..Edit a submitted evaluation record:
		System.out.println("testEditResultsByReviewer: Edit 1st empty evaluation record");

		bi.clickReviewerSummaryEdit(FIRST_STUDENT);

		// try 0: do nothing
		bi.waitAndClick(bi.resultEditButton);
		bi.waitForElementText(bi.editEvaluationResultsStatusMessage, "Please fill in all the relevant fields.");

		// try 1: fill in estimated contribution only
		Student s = scn.students.get(FIRST_STUDENT);
		for (int i = 0; i < s.team.students.size(); i++) {
			bi.setSubmissionPoint(i, "80");
		}
		bi.waitAndClick(bi.resultEditButton);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_RESULTS_EDITED);

		// try 2: fill in \\Justification\\s only
		bi.waitAndClick(bi.resultIndividualEditButton);
		for (int i = 0; i < s.team.students.size(); i++) {
			// clean up contribution data added in try 1
			bi.setSubmissionPoint(i, "-999");
			bi.setSubmissionJustification(i, String.format("Edit:: \\Justification\\ from %s's email (%s) to %s.", s.name, s.email, s.team.students.get(i).email));

		}
		bi.waitAndClick(bi.resultEditButton);
		bi.waitForElementText(bi.editEvaluationResultsStatusMessage, "Please fill in all the relevant fields.");

		// try 3: fill in commentsToStudent only
		for (int i = 0; i < s.team.students.size(); i++) {
			// clean up contribution data added in try 2
			bi.setSubmissionJustification(i, null);
			bi.setSubmissionComments( i, String.format("Edit:: \\\\Comments\\\\ from %s's email (%s) to %s.", s.name, s.email, s.team.students.get(i).email));
		}
		bi.waitAndClick(bi.resultEditButton);
		bi.waitForElementText(bi.editEvaluationResultsStatusMessage, "Please fill in all the relevant fields.");

		// Submit with new data: successful
		for (int i = 0; i < s.team.students.size(); i++) {
			bi.setSubmissionPoint(i, "100");
			bi.setSubmissionJustification(i, String.format("Edit:: \\Justification\\ from %s's email (%s) to %s.", s.name, s.email, s.team.students.get(i).email));
			bi.setSubmissionComments( i, String.format("Edit:: \\\\Comments\\\\ from %s's email (%s) to %s.", s.name, s.email, s.team.students.get(i).email));
		}
		bi.waitAndClick(bi.resultEditButton);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_RESULTS_EDITED);

		// click 'Back':
		bi.waitAndClick(bi.resultBackButton);// [Back] to summary

		// click 'Back':
		bi.waitAndClick(bi.resultBackButton);
	}

	public void testCoordEditResultsByReviewerSuccessful() throws Exception {

		System.out.println("TestCoordResultsEdit: testEditResultsByReviewer");

		bi.gotoEvaluations();

		bi.clickEvaluationViewResults(scn.course.courseId, scn.evaluation.name);

		// click 'Edit' 1st student:
		bi.clickReviewerSummaryEdit(FIRST_STUDENT);

		Student s = scn.students.get(FIRST_STUDENT);
		for (int i = 0; i < s.team.students.size(); i++) {
			bi.setSubmissionPoint(i, "30");
			bi.setSubmissionJustification(i, String.format("Edit:: \\Justification\\ from %s's email (%s) to %s.", s.name, s.email, s.team.students.get(i).email));
			bi.setSubmissionComments(i, String.format("Edit:: \\\\Comments\\\\ from %s's email (%s) to %s.", s.name, s.email, s.team.students.get(i).email));
		}

		// click 'submit':
		bi.waitAndClick(bi.resultEditButton);// [submit]
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_RESULTS_EDITED);

		// ..Check content being updated:
		//TODO: check other report has been updated as well: summary/detail/individual
		
		bi.waitAndClick(bi.resultIndividualEditButton);
		for (int i = 0; i < s.team.students.size(); i++) {
			assertEquals(bi.getDropdownSelectedValue(bi.getSubmissionPoint(i)), "30");
			assertEquals(bi.getElementText(bi.getSubmissionJustification(i)), String.format("Edit:: \\Justification\\ from %s's email (%s) to %s.", s.name, s.email, s.team.students.get(i).email));
			assertEquals(bi.getElementText(bi.getSubmissionComments(i)), String.format("Edit:: \\\\Comments\\\\ from %s's email (%s) to %s.", s.name, s.email, s.team.students.get(i).email));
		}
		bi.waitAndClick(bi.resultEditCancelButton);// [cancel]

		bi.justWait();

		bi.waitAndClick(bi.resultNextButton);

		// click 'Edit' 2nd student:
		bi.waitAndClick(bi.resultIndividualEditButton);

		// click 'Cancel':
		bi.waitAndClick(bi.resultEditCancelButton);// [Cancel]

		// click 'Back':
		bi.waitAndClick(bi.resultBackButton);// [Back] to summary

		// click 'Back':
		bi.waitAndClick(bi.resultBackButton);
	}

}
