package teammates.testing.concurrent;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

/**
 * Page under test: Coordinator Add Course
 * Object under test: Delete link in course list
 * */
public class CoordCourseDeleteUITest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	static final int FIRST_STUDENT = 0;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordCourseListTest");
		bi = BrowserInstancePool.getBrowserInstance();
		
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
		bi.gotoCourses();
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordCourseListTest ==========//");
	}
	
	
	@Test
	public void testCoordDeleteCourseSuccessful() {
		System.out.println("TestCoordDeleteCourseSuccessful:");
		// Delete course
		bi.waitForElementPresent(bi.getCourseIDCellLocatorByCourseId(scn.course.courseId));
		bi.clickAndConfirmCourseDelete(scn.course.courseId);
		bi.waitForTextInElement(bi.statusMessage, BrowserInstance.MESSAGE_COURSE_DELETED);
		assertFalse(bi.isCoursePresent(scn.course.courseId, scn.course.courseName));
		
		//Check that the evaluation has also been deleted
		bi.gotoEvaluations();
		assertFalse(bi.isEvaluationPresent(scn.course.courseId, scn.evaluation.name));
		
		bi.logout();
		
		// Check that the course has been deleted from student page 
		bi.studentLogin(scn.students.get(FIRST_STUDENT).email, Config.inst().TEAMMATES_APP_PASSWD);
		bi.clickCourseTab();

		assertFalse(bi.isCoursePresent(scn.course.courseId, scn.course.courseName));
		
	}
}
