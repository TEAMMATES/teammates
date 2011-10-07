package teammates.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

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

	@BeforeClass
	public static void classSetup() throws IOException {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);

		// TMAPI.firstStudentDidNotSubmitFeedbacks(sc.students, sc.course.courseId,
		// sc.evaluation.name);
		// huy - I remove the function above
		TMAPI.studentsSubmitFeedbacks(
				sc.students.subList(1, sc.students.size() - 1), sc.course.courseId,
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
	public void testEditEmptyResult() throws Exception {

		// ..View results of a CLOSED evaluation:
		// Click Evaluation Tab
		waitAndClick(By.className("t_evaluations"));

		// click 'View Results':
		waitAndClick(By.className("t_eval_view"));

		// Submit without changing anything: failed
		System.out.println("testEditEmptyResult: submit without changing any data");

		// ..Edit a submitted evaluation record:
		System.out
				.println("testEditResultsByReviewer: Edit 1st empty evaluation record");
		// click 'Edit' 1st student:
		waitAndClick(By.id("editEvaluationResults0"));
		// try 0: do nothing

		// click submit:
		waitAndClick(By.id("button_editevaluationresultsbyreviewee"));
		waitForElementText(By.id("coordinatorEditEvaluationResultsStatusMessage"),
				"Please fill in all the relevant fields.");

		// try 1: fill in estimated contribution only
		Student s = sc.students.get(0);
		for (int i = 0; i < s.team.students.size(); i++) {
			selectDropdownByValue(By.id("points" + i), "80");
			//selenium.select("points" + i, "value=80");
		}
		waitAndClick(By.id("button_editevaluationresultsbyreviewee"));
		if (isElementPresent(By.id("statusMessage"))
				&& getElementText(By.id("statusMessage")).equals(
						"The particular evaluation results have been edited."))
			fail("Fail: JS did not check feedback not empty.");
		else
			waitForElementText(
					By.id("coordinatorEditEvaluationResultsStatusMessage"),
					"Please fill in all the relevant fields.");

		// try 2: fill in justifications only
		for (int i = 0; i < s.team.students.size(); i++) {
			// clean up contribution data added in try 1
			//selenium.select("points" + i, "value=-999");
			selectDropdownByValue(By.id("points" + i), "-999");
			wdFillString(By.name("justification" + i), String.format(
					"Test Justification:: Justification from %s to %s.", s.email,
					s.team.students.get(i).email));

		}
		waitAndClick(By.id("button_editevaluationresultsbyreviewee"));
		if (isElementPresent(By.id("statusMessage"))
				&& getElementText(By.id("statusMessage")).equals(
						"The particular evaluation results have been edited."))
			fail("Fail: JS did not check feedback not empty.");
		else
			waitForElementText(
					By.id("coordinatorEditEvaluationResultsStatusMessage"),
					"Please fill in all the relevant fields.");

		// try 3: fill in commentsToStudent only
		for (int i = 0; i < s.team.students.size(); i++) {
			// clean up contribution data added in try 2
			driver.findElement(By.name("justification" + i)).clear();
			wdFillString(By.name("commentstostudent" + i), String.format(
					"Test Comments:: CommentsToStudent from %s to %s.", s.email,
					s.team.students.get(i).email));

		}
		waitAndClick(By.id("button_editevaluationresultsbyreviewee"));
		if (isElementPresent(By.id("statusMessage"))
				&& getElementText(By.id("statusMessage")).equals(
						"The particular evaluation results have been edited."))
			fail("Fail: JS did not check feedback not empty.");
		else
			waitForElementText(
					By.id("coordinatorEditEvaluationResultsStatusMessage"),
					"Please fill in all the relevant fields.");

		// Submit with new data: successful
		System.out.println("testEditEmptyResult: submit with new data");
		for (int i = 0; i < s.team.students.size(); i++) {
			selenium.select("points" + i, "value=100");
			wdFillString(By.name("justification" + i), String.format(
					"Add:: Justification from %s to %s.", s.email,
					s.team.students.get(i).email));
			wdFillString(By.name("commentstostudent" + i), String.format(
					"Add:: Comments from %s to %s.", s.email,
					s.team.students.get(i).email));
		}

		// click 'submit':
		System.out.println("testEditResultsByReviewer: Submit Edited Feedbacks.");

		waitAndClick(By.id("button_editevaluationresultsbyreviewee"));// [submit]

		waitForElementText(By.id("statusMessage"),
				"The particular evaluation results have been edited.");

		// click 'Back':
		waitAndClick(By.id("button_back"));// [Back] to summary

		waitAndClick(By.id("button_back"));

	}

	@Test
	public void testEditResultsByReviewer() throws Exception {

		cout("testEditResultsByReviewer: ");

		// ..View results of a CLOSED evaluation:
		// Click Evaluation Tab
		waitAndClick(By.className("t_evaluations"));

		// click 'View Results':
		waitAndClick(By.className("t_eval_view"));

		// ..Edit a submitted evaluation record:
		System.out.println("testEditResultsByReviewer: Edit Reviewer Feedback");
		// click 'Edit' 1st student:
		waitAndClick(By.id("editEvaluationResults0"));

		// System.out.println("testEditResultsByReviewer: insert new feedbacks. what is "
		// + selenium.getCssCount(By.id("points0")));

		Student s = sc.students.get(0);
		for (int i = 0; i < s.team.students.size(); i++) {
			//selenium.select("points" + i, "value=30");
			selectDropdownByValue(By.id("points" + i), "30");
			wdFillString(By.name("justification" + i), String.format(
					"Edit:: Justification from %s to %s.", s.email,
					s.team.students.get(i).email));
			wdFillString(By.name("commentstostudent" + i), String.format(
					"Edit:: Comments from %s to %s.", s.email,
					s.team.students.get(i).email));
		}

		// click 'submit':
		System.out.println("testEditResultsByReviewer: Submit Edited Feedbacks.");

		waitAndClick(By.id("button_editevaluationresultsbyreviewee"));// [submit]
		waitForElementText(By.id("statusMessage"),
				"The particular evaluation results have been edited.");

		// ..Check content being updated:
		System.out
				.println("testEditResultsByReviewer: Check feedbacks have been updated");
		waitAndClick(By.id("button_edit"));
		for (int i = 0; i < s.team.students.size(); i++) {
			assertEquals(getDropdownSelectedValue(By.name("points" + i)), "30");

			assertEquals(getElementText(By.name("justification" + i)),
					String.format("Edit:: Justification from %s to %s.", s.email,
							s.team.students.get(i).email));
			assertEquals(getElementText(By.name("commentstostudent" + i)),
					String.format("Edit:: Comments from %s to %s.", s.email,
							s.team.students.get(i).email));
		}
		waitAndClick(By.id("button_back"));// [cancel]

		justWait();

		System.out.println("testEditResultsByReviewer: Click Next/Previous Button");
		// click 'Next':
		waitAndClick(By.id("button_next"));

		// click 'Edit' 2nd student:
		waitAndClick(By.id("button_edit"));

		// click 'Cancel':
		waitAndClick(By.id("button_back"));// [Cancel]

		// click 'Back':
		waitAndClick(By.id("button_back"));// [Back] to summary

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