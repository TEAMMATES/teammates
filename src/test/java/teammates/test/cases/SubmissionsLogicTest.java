package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.logic.EvaluationsLogic;
import teammates.logic.SubmissionsLogic;
import teammates.logic.automated.EvaluationOpeningRemindersServlet;
import teammates.storage.datastore.Datastore;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class SubmissionsLogicTest extends BaseTestCase{
	
	protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(EvaluationsLogic.class);
		Datastore.initialize();
	}
	
	@BeforeMethod
	public void caseSetUp() throws ServletException, IOException {
		helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
		setHelperTimeZone(helper);
		helper.setUp();
	}
	
	@Test
	public void testAddSubmissionsForIncomingMember() throws Exception {
		loginAsAdmin("admin.user");

		______TS("typical case");

		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();

		CourseAttributes course = dataBundle.courses.get("typicalCourse1");
		EvaluationAttributes evaluation1 = dataBundle.evaluations
				.get("evaluation1InCourse1");
		EvaluationAttributes evaluation2 = dataBundle.evaluations
				.get("evaluation2InCourse1");
		StudentAttributes student = dataBundle.students.get("student1InCourse1");

		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation1.name, "incoming@student.com", student.team);

		// We have a 5-member team and a 1-member team.
		// Therefore, we expect (5*5)+(1*1)=26 submissions.
		List<SubmissionAttributes> submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(26, submissions.size());
		
		// Check the same for the other evaluation, to detect any state leakage
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation2.name, "incoming@student.com", student.team);
		submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation2.name);
		assertEquals(26, submissions.size());
		
		______TS("moving to new team");
		
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation1.name, "incoming@student.com", "new team");
		//There should be one more submission now.
		submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(27, submissions.size());
		
		// Check the same for the other evaluation
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation2.name, "incoming@student.com", "new team");
		//There should be one more submission now.
		submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation2.name);
		assertEquals(27, submissions.size());

		//TODO: test invalid inputs

	}
	
	private void invokeAddSubmissionsForIncomingMember(String courseId,
			String evaluationName, String studentEmail, String newTeam)throws Exception {
		Method privateMethod = EvaluationsLogic.class.getDeclaredMethod(
				"addSubmissionsForIncomingMember", new Class[] { String.class,
						String.class, String.class, String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] {courseId,
				 evaluationName,  studentEmail, newTeam };
		privateMethod.invoke(EvaluationsLogic.inst(), params);
	}
	
	
	
	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(EvaluationOpeningRemindersServlet.class);
	}

	@AfterMethod
	public void caseTearDown() {
		helper.tearDown();
	}

}
