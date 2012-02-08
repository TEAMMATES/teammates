package teammates.testing.concurrent;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.BaseTest2;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

public class CoordEvaluationListTest extends BaseTest2 {
	static BrowserInstance bi;
	static Scenario scn = setupNewScenarioInstance("scenario");

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordEvaluationAddCaseSensitivityTest");
		bi = BrowserInstancePool.request();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);

		// first evaluation status: pending
		TMAPI.createEvaluation(scn.evaluation);

		// second evaluation status: open
		TMAPI.createEvaluation(scn.evaluation2);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation2.name);

		// third evaluation status: closed
		TMAPI.createEvaluation(scn.evaluation3);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation3.name);
		TMAPI.studentsSubmitFeedbacks(scn.students, scn.course.courseId, scn.evaluation3.name);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation3.name);

		// forth evaluation status: pending
		TMAPI.createEvaluation(scn.evaluation4);

		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);

	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);

		BrowserInstancePool.release(bi);
		System.out.println("CoordEvaluationAddCaseSensitivityTest ==========//");
	}

	@Test
	public void testEvaluationListLinks() throws Exception {

		testCoordRemindEvaluationSuccessful();
		testCoordPublishEvaluationSuccessful();
		testCoordUnpublishEvaluationSuccessful();
		testCoordDeleteEvaluation();
		testCoordAddDeletedEvaluation();

	}

	// TODO: testCoordSortEvaluationListByCourseIDSuccessful

	// TODO: testCoordSortEvaluationListByEvaluationNameSuccessful

	// testCoordRemindEvaluationSuccessful
	public void testCoordRemindEvaluationSuccessful() throws Exception {
		bi.clickEvaluationTab();
		bi.justWait();
		bi.clickAndConfirmEvaluationRemind(scn.course.courseId, scn.evaluation2.name);

		// Confirm Email
		bi.justWait();
		for (int i = 0; i < scn.students.size(); i++) {
			assertEquals(scn.course.courseId, 
					SharedLib.getEvaluationReminderFromGmail(scn.students.get(i).email, Config.inst().TEAMMATES_APP_PASSWD, scn.course.courseId, scn.evaluation2.name));
		}

	}

	// testCoordPublishEvaluationSuccessful
	public void testCoordPublishEvaluationSuccessful() throws Exception {
		System.out.println("testCoordPublishEvaluationSuccessful");

		bi.clickEvaluationTab();
		bi.clickEvaluationPublish(scn.course.courseId, scn.evaluation3.name);

		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_PUBLISHED);

		// Check for status: PUBLISHED
		assertEquals(bi.EVAL_STATUS_PUBLISHED, bi.getEvaluationStatus(scn.course.courseId, scn.evaluation3.name));

		// Check if emails have been sent to all participants
		bi.waitAWhile(5000);
		for (Student s : scn.students) {
			System.out.println("Checking " + s.email);
			assertTrue(bi.checkResultEmailsSent(s.email, s.password, scn.course.courseId, scn.evaluation3.name));
		}
	}

	// testCoordUnpublishEvaluationSuccessful
	public void testCoordUnpublishEvaluationSuccessful() throws Exception {
		System.out.println("testCoordUnpublishEvaluationSuccessful");
		bi.gotoEvaluations();

		bi.clickEvaluationUnpublish(scn.course.courseId, scn.evaluation3.name);

		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_UNPUBLISHED);

		// Check for status: PUBLISHED
		assertEquals(bi.EVAL_STATUS_CLOSED, bi.getEvaluationStatus(scn.course.courseId, scn.evaluation3.name));
	}

	// testCoordDeleteEvaluationSuccessful
	public void testCoordDeleteEvaluation() throws Exception {
		System.out.println("Deleting evaluation.");

		bi.clickEvaluationTab();
		bi.clickAndConfirmEvaluationDelete(scn.course.courseId, scn.evaluation4.name);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_DELETED);
	}

	public void testCoordAddDeletedEvaluation() throws Exception {
		System.out.println("Adding deleted evaluation.");
		bi.clickEvaluationTab();

		bi.addEvaluation(scn.evaluation4);
		bi.justWait();

		bi.verifyEvaluationAdded(scn.course.courseId, scn.evaluation4.name, "AWAITING", "0 / " + scn.students.size());
	}

}
