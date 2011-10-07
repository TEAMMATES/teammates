package teammates.testing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

/**
 * Case description: After student submit feedbacks, he/she edits it again while
 * evaluation is OPEN
 * 
 * Test: 1.test student edit feedbacks
 * 
 * @author xialin
 * 
 */

public class TestStudentEditFeedbacks extends BaseTest {

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

		setupSelenium();

	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	@Test
	public void testEditFeedbacks() throws Exception {

		// student login
		Student s = sc.students.get(0);
		studentLogin(s.email, s.password);

		// Click Evaluation Tab
		waitAndClick(By.className("t_evaluations"));

		// click [Edit] first evaluation (OPEN):
		waitAndClick(By.id("editEvaluation0"));

		// click [Cancel]
		waitAndClick(By.id("button_back"));

		// click [Edit] first evaluation (OPEN):
		waitAndClick(By.id("editEvaluation0"));

		// edit contents
		for (int i = 0; i < s.team.students.size(); i++) {
			Select select = new Select(driver.findElement(By.id("points" + i)));
			select.selectByValue("80");
			// selenium.select("points" + i, "value=80");

			wdFillString(By.name("justification" + i), String.format(
					"Student Edit:: Justification from %s to %s.", s.email,
					s.team.students.get(i).email));
			wdFillString(By.name("commentstostudent" + i), String.format(
					"Student Edit:: Comments from %s to %s.", s.email,
					s.team.students.get(i).email));
		}
		// click [Submit]
		waitAndClick(By.id("submitEvaluation"));

		waitForElementText(By.id("statusMessage"),
				"The evaluation has been submitted.");

		// check feedbacks updated:
		System.out.println("testEditFeedbacks: Check feedbacks have been updated");
		waitAndClick(By.id("editEvaluation0"));
		for (int i = 0; i < s.team.students.size(); i++) {
			assertEquals(getDropdownSelectedValue(By.id("points" + i)), "80");
			assertEquals(getElementValue(By.name("justification" + i)),
					String.format("Student Edit:: Justification from %s to %s.", s.email,
							s.team.students.get(i).email));
			assertEquals(getElementValue(By.name("commentstostudent" + i)),
					String.format("Student Edit:: Comments from %s to %s.", s.email,
							s.team.students.get(i).email));
		}
		waitAndClick(By.id("button_back"));// [cancel]

	}

}