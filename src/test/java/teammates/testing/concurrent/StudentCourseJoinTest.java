package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

public class StudentCourseJoinTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== StudentCourseJoinTest");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		TMAPI.cleanupCourse(scn.course.courseId);

		BrowserInstancePool.release(bi);
		System.out.println("StudentCourseJoinTest ==========//");
	}

	@Test
	public void testCoordRemindStudentsJoinCourseSuccessful() throws Exception {
		testCoordRemindAllStudentsSuccessful();
		testStudentsJoinCourseSuccessful();
	}

	public void testCoordRemindAllStudentsSuccessful() throws Exception {
		System.out.println("testCoordRemindAllStudentsSuccessful");

		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);

		bi.goToCourses();
		bi.clickCoordCourseView(scn.course.courseId);

		bi.clickAndConfirm(bi.remindStudentsButton);

		assertEquals(bi.MESSAGE_ENROLL_REMIND_TO_JOIN, bi.getElementText(bi.statusMessage));

		// Collect keys
		System.out.println("Collecting registration keys.");
		
		for (int i = 0; i < scn.students.size(); i++) {
			// FIXME: Incorrect method used, it is to click on Course View Details, not on students details
			// Currently there is not yet any method to click the student details.
			bi.clickCoordCourseView(i);
			bi.waitForElementPresent(bi.studentDetailKey);
			scn.students.get(i).courseKey = bi.getElementText(bi.studentDetailKey);
			bi.clickWithWait(bi.studentDetailBackButton);
		}

		// Write key back to json file
		scn.toJSONFile("target/test-classes/data/scenario.json.ext");

		// Reserve more time to send email
		bi.waitForEmail();

		for (int i = 0; i < scn.students.size(); i++) {
			assertEquals(scn.students.get(i).courseKey,
							SharedLib.getRegistrationKeyFromGmail(scn.students.get(i).email, Config.inst().TEAMMATES_APP_PASSWD, scn.course.courseId));
		}

		// TODO: remove email-related testing into a single test case
		bi.logout();
	}

	public void testStudentsJoinCourseSuccessful() throws Exception {
		System.out.println("testStudentsJoinCourseSuccessful");
		for (Student s : scn.students) {
			bi.studentLogin(s.email, Config.inst().TEAMMATES_APP_PASSWD);

			// Try a wrong course key
			bi.fillString(bi.studentInputRegKey, "totally_wrong_key");
			bi.click(bi.studentJoinCourseButton);
			bi.waitForTextInElement(bi.statusMessage, bi.ERROR_STUDENT_JOIN_COURSE);

			if (bi.studentCountTotalCourses() == 2) {
				// This time the correct one
				bi.waitForElementPresent(bi.studentInputRegKey);
				bi.fillString(bi.studentInputRegKey, s.courseKey);
				System.out.println("key for " + s.name + " : " + s.courseKey);
				bi.click(bi.studentJoinCourseButton);
				bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_STUDENT_JOIN_COURSE);
			}

			bi.logout();
		}

		// Verify number of unregistered student
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
		assertEquals(0, bi.getCourseUnregisteredStudents(scn.course.courseId));
	}

}
