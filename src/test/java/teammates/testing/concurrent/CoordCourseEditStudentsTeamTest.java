package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

/**
 * Special test case
 * Scenario: students move/drop team before/after evaluation submission
 * 
 * Condition: 6 students enrolled. 2 teams. 1 evaluation created. all students have submit evaluation forms
 * Action: 
 * 1 coordinator move Danny to team A
 * 2 coordinator create evaluation2
 * 3 students submit evaluation2
 * 4 coordinator close evaluation. review evaluation 1 and 2
 * 5 coordinator delete Benny
 * 6 coordinator release result
 * 7 all students should be able to see the results, including Benny and Danny.
 * 
 * TODO: details not checked
 * 
 * */
public class CoordCourseEditStudentsTeamTest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = setupNewScenarioInstance("scenario");
	
	static int FIRST_STUDENT = 0;
	static Student ALICE = scn.students.get(0);
	static Student BENNY = scn.students.get(1);
	static Student CHARLIE = scn.students.get(2);
	static Student DANNY = scn.students.get(3);

	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.getBrowserInstance();
		

		TMAPI.cleanupCourse(scn.course.courseId);

		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);

		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(scn.students, scn.course.courseId, scn.evaluation.name);

		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if(bi.isElementPresent(bi.logoutTab)) {
			bi.logout();
		}
		TMAPI.cleanupCourse(scn.course.courseId);
		BrowserInstancePool.release(bi);
	}
	
	@Test 
	public void testCoordEditStudents() throws Exception{
		testMoveStudentAfterEvaluation();
		testDropStudentAfterEvaluation();
	}

	//TODO: change team before evaluation close

	// change team after evaluation close
	
	public void testMoveStudentAfterEvaluation() throws Exception {
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation.name);

		swapTeam();
		// Mass Edit Student
		bi.goToCourses();
		bi.enrollStudents(scn.students, scn.course.courseId);

		SharedLib.tprintln("Creating second evaluation.");
		bi.goToEvaluation();
		bi.addEvaluation(scn.evaluation2);
		
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation2.name);
		TMAPI.studentsSubmitFeedbacks(scn.students, scn.course.courseId, scn.evaluation2.name);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation2.name);

		// Coordinator verify
		bi.clickEvaluationTab();
		// Verify First Evaluation
		bi.clickCoordEvaluationViewResults(scn.course.courseId, scn.evaluation.name);
		bi.clickWithWait(bi.resultBackButton);

		// Verify Second Evaluation
		bi.clickCoordEvaluationViewResults(scn.course.courseId, scn.evaluation2.name);
		bi.waitForElementPresent(bi.resultDetailRadio);

		WebElement htmldiv = bi.getDriver().findElement(By.id("coordinatorEvaluationSummaryTable"));
		assertEquals(5, htmldiv.findElements(By.tagName("tr")).size());
	}

	//TODO: drop team before evaluation close

	// drop team after evaluation close
	
	public void testDropStudentAfterEvaluation() {
		System.out.println("delete Alice");
		
		bi.goToCourses();
		bi.clickCoordCourseView(scn.course.courseId);
		
		assertEquals(4, bi.countCourseDetailStudents());
		
		bi.clickCoordCourseDetailStudentDeleteAndConfirm(scn.students.get(FIRST_STUDENT).name);
		
		assertEquals(3, bi.countCourseDetailStudents());

		// Verify Report
		bi.goToEvaluation();
		// Verify Coordinator View
		bi.clickCoordEvaluationViewResults(scn.course.courseId, scn.evaluation.name);
		bi.clickCoordReviewerSummaryView(FIRST_STUDENT);
		
		bi.goToEvaluation();
		// Verify Coordinator View
		bi.clickCoordEvaluationViewResults(scn.course.courseId, scn.evaluation2.name);
		bi.clickCoordReviewerSummaryView(FIRST_STUDENT);
		
		
		// Publish Evaluation
		bi.goToEvaluation();
		bi.clickCoordEvaluationPublishAndConfirm(scn.course.courseId, scn.evaluation.name);
		bi.clickCoordEvaluationPublishAndConfirm(scn.course.courseId, scn.evaluation2.name);

		bi.logout();
		
		// Verify Student View (using Charlie account)
		bi.loginStudent(CHARLIE.email, Config.inst().TEAMMATES_APP_PASSWD);
		
		bi.clickEvaluationTab();
		bi.studentClickEvaluationViewResults(scn.course.courseId, scn.evaluation.name);
		
		bi.clickEvaluationTab();
		bi.studentClickEvaluationViewResults(scn.course.courseId, scn.evaluation2.name);
//		bi.waitAndClick(bi.studentEvaluationBackButton);
	}

	/**
	 * Alice and Benny in Team 1
	 * Charlie and Danny in Team 2
	 * Swap Benny and Charlie's team
	 * */
	private static void swapTeam() {
		BENNY.teamName = CHARLIE.teamName;
		CHARLIE.teamName = ALICE.teamName;

		ALICE.team.students.remove(BENNY);
		DANNY.team.students.remove(CHARLIE);
		ALICE.team.students.add(CHARLIE);
		DANNY.team.students.add(BENNY);

		BENNY.team = CHARLIE.team;
		CHARLIE.team = ALICE.team;

		System.out.println(ALICE.team.toString());
		System.out.println(DANNY.team.toString());
	}
}
