package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.EvaluationsLogic;
import teammates.logic.SubmissionsLogic;
import teammates.logic.api.Logic;
import teammates.logic.automated.EvaluationOpeningRemindersServlet;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.storage.datastore.Datastore;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class SubmissionsLogicTest extends BaseTestCase{
	
	protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();
	
	private static DataBundle dataBundle = getTypicalDataBundle();
	
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
	public void testGetSubmissionsForEvaluation() throws Exception {

		DataBundle dataBundle = getTypicalDataBundle();
		Logic logic = new Logic();
		
		restoreTypicalDataInDatastore();

		______TS("typical case");

		loginAsAdmin("admin.user");

		EvaluationAttributes evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1");
		// reuse this evaluation data to create a new one
		evaluation.name = "new evaluation";
		logic.createEvaluation(evaluation);

		HashMap<String, SubmissionAttributes> submissions = invokeGetSubmissionsForEvaluation(
				evaluation.course, evaluation.name);
		// Team 1.1 has 4 students, Team 1.2 has only 1 student.
		// There should be 4*4+1=17 submissions.
		assertEquals(17, submissions.keySet().size());
		// verify they all belong to this evaluation
		for (String key : submissions.keySet()) {
			assertEquals(evaluation.course, submissions.get(key).course);
			assertEquals(evaluation.name, submissions.get(key).evaluation);
		}
		
		______TS("orphan submissions");
		
		// move student from Team 1.1 to Team 1.2
		StudentAttributes student = dataBundle.students.get("student1InCourse1");
		student.team = "Team 1.2";
		logic.updateStudent(student.email, student);

		// Now, team 1.1 has 3 students, team 1.2 has 2 student.
		// There should be 3*3+2*2=13 submissions if no orphans are returned.
		submissions = invokeGetSubmissionsForEvaluation(evaluation.course,
				evaluation.name);
		assertEquals(13, submissions.keySet().size());
		
		// Check if the returned submissions match the current team structure
		List<StudentAttributes> students = logic
				.getStudentsForCourse(evaluation.course);
		LogicTest.verifySubmissionsExistForCurrentTeamStructureInEvaluation(
				evaluation.name, students, new ArrayList<SubmissionAttributes>(
						submissions.values()));

		______TS("evaluation in empty class");
		
		logic.createAccount("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
		logic.createCourseAndInstructor("instructor1", "course1", "Course 1");
		evaluation.course = "course1";
		logic.createEvaluation(evaluation);

		submissions = invokeGetSubmissionsForEvaluation(evaluation.course,
				evaluation.name);
		assertEquals(0, submissions.keySet().size());

		______TS("non-existent course/evaluation");

		String methodName = "getSubmissionsForEvaluation";
		Class<?>[] paramTypes = new Class[] { String.class, String.class };
		
		assertEquals(0, invokeGetSubmissionsForEvaluation(evaluation.course, "non-existent").size());
		assertEquals(0, invokeGetSubmissionsForEvaluation("non-existent", evaluation.name).size());

		// no need to check for invalid parameters as it is a private method
	}
	
	@Test
	public void testEditSubmission() throws Exception {

		restoreTypicalDataInDatastore();
		Logic logic = new Logic();

		String methodName = "editSubmission";
		Class<?>[] paramTypes = new Class<?>[] { SubmissionAttributes.class };
		SubmissionAttributes s = new SubmissionAttributes();
		s.course = "idOfTypicalCourse1";
		s.evaluation = "evaluation1 In Course1";
		s.reviewee = "student1InCourse1@gmail.com";
		s.reviewer = "student1InCourse1@gmail.com";
		Object[] params = new Object[] { s };

		// ensure the evaluation is open
		loginAsAdmin("admin.user");
		EvaluationAttributes evaluation = logic.getEvaluation(s.course, s.evaluation);
//		assertEquals(EvalStatus.OPEN, evaluation.getStatus());
//		logoutUser();



		// close the evaluation
//		loginAsAdmin("admin.user");
		evaluation.endTime = Common.getDateOffsetToCurrentTime(-1);
		assertEquals(EvalStatus.CLOSED, evaluation.getStatus());
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		backDoorLogic.editEvaluation(evaluation);
		logoutUser();

		// verify reviewer cannot edit anymore but instructor can


		______TS("typical case");

		restoreTypicalDataInDatastore();

		SubmissionAttributes sub1 = dataBundle.submissions
				.get("submissionFromS1C1ToS2C1");

		LogicTest.alterSubmission(sub1);

		submissionsLogic.updateSubmission(sub1);

		LogicTest.verifyPresentInDatastore(sub1);

		______TS("null parameter");
		
		// private method, not tested.

		______TS("non-existent evaluation");

		sub1.evaluation = "non-existent";
		
		try {
			submissionsLogic.updateSubmission(sub1);
			Assert.fail();
		} catch (EntityDoesNotExistException e) {
			//expected.
		}

	}

	
	@SuppressWarnings("unchecked")
	private static HashMap<String, SubmissionAttributes> invokeGetSubmissionsForEvaluation(
			String courseId, String evaluationName) throws Exception {
		Method privateMethod = SubmissionsLogic.class.getDeclaredMethod(
				"getSubmissionsForEvaluationAsMap", new Class[] { String.class,
						String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { courseId, evaluationName };
		return (HashMap<String, SubmissionAttributes>) privateMethod.invoke(SubmissionsLogic.inst(),
				params);
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
