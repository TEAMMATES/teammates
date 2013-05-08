package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;

import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.logic.EvaluationsLogic;
import teammates.logic.automated.EvaluationOpeningRemindersServlet;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Submission;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class EvaluationsLogicTest extends BaseTestCase{
	
	private static final EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
	
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
	public void testGetReadyEvaluations() throws Exception {

		loginAsAdmin("admin.user");

		______TS("no evaluations activated");
		// ensure there are no existing evaluations ready for activation
		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();
		BackDoorLogic backdoor = new BackDoorLogic();
		for (EvaluationAttributes e : dataBundle.evaluations.values()) {
			e.activated = true;
			backdoor.editEvaluation(e);
			assertTrue(backdoor.getEvaluation(e.course, e.name).getStatus() != EvalStatus.AWAITING);
		}
		assertEquals(0, evaluationsLogic.getReadyEvaluations().size());

		______TS("typical case, two evaluations activated");
		// Reuse an existing evaluation to create a new one that is ready to
		// activate. Put this evaluation in a negative time zone.
		EvaluationAttributes evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1");
		String nameOfEvalInCourse1 = "new-evaluation-in-course-1-tGRE";
		evaluation.name = nameOfEvalInCourse1;

		evaluation.activated = false;

		double timeZone = -1.0;
		evaluation.timeZone = timeZone;

		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(0,
				timeZone);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(2);

		backdoor.createEvaluation(evaluation);

		// Verify that there are no unregistered students.
		// TODO: this should be removed after absorbing registration reminder
		// into the evaluation opening alert.
		CourseDetailsBundle course1 = backdoor.getCourseDetails(evaluation.course);
		assertEquals(0, course1.unregisteredTotal);

		// Create another evaluation in another course in similar fashion.
		// Put this evaluation in a positive time zone.
		// This one too is ready to activate.
		evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
		evaluation.activated = false;
		String nameOfEvalInCourse2 = "new-evaluation-in-course-2-tGRE";
		evaluation.name = nameOfEvalInCourse2;

		timeZone = 2.0;
		evaluation.timeZone = timeZone;

		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(0,
				timeZone);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(2);

		backdoor.createEvaluation(evaluation);

		// Verify that there are no unregistered students
		// TODO: this should be removed after absorbing registration reminder
		// into the evaluation opening alert.
		CourseDetailsBundle course2 = backdoor.getCourseDetails(evaluation.course);
		assertEquals(0, course2.unregisteredTotal);

		// Create another evaluation not ready to be activated yet.
		// Put this evaluation in same time zone.
		evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
		evaluation.activated = false;
		evaluation.name = "new evaluation - start time in future";

		timeZone = 0.0;
		evaluation.timeZone = timeZone;

		int oneSecondInMs = 1000;
		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				oneSecondInMs, timeZone);
		backdoor.createEvaluation(evaluation);

		// verify number of ready evaluations.
		assertEquals(2, evaluationsLogic.getReadyEvaluations().size());

		// Other variations of ready/not-ready states should be checked at
		// Evaluation level
	}
	
	@Test
	public void testDeleteSubmissionsForOutgoingMember() throws Exception {
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

		// We have a 4-member team and a 1-member team.
		// Therefore, we expect (4*4)+(1*1)=17 submissions.
		List<SubmissionAttributes> submissions = evaluationsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(17, submissions.size());

		evaluationsLogic.deleteSubmissionsForOutgoingMember(course.id, evaluation1.name, student.email, student.team);
		
		// We have a 3-member team and a 1-member team.
		// Therefore, we expect (3*3)+(1*1)=10 submissions.
		submissions = evaluationsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(10, submissions.size());

		// check the same for the other evaluation, to detect state leakage
		evaluationsLogic.deleteSubmissionsForOutgoingMember(course.id, evaluation2.name, student.email, student.team);
		
		submissions = evaluationsLogic.getSubmissionsForEvaluation(course.id, evaluation2.name);
		assertEquals(10, submissions.size());

		// verify the student is no longer included in submissions
		for (SubmissionAttributes s : submissions) {
			assertTrue(!s.reviewee.equals(student.email));
			assertTrue(!s.reviewer.equals(student.email));
		}

		______TS("only one student in team");

		StudentAttributes loneStudent = dataBundle.students.get("student5InCourse1");
		evaluationsLogic.deleteSubmissionsForOutgoingMember(course.id, evaluation1.name, loneStudent.email, loneStudent.team);
		// We expect one fewer submissions than before.
		submissions = evaluationsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(9, submissions.size());

		evaluationsLogic.deleteSubmissionsForOutgoingMember(course.id, evaluation2.name, loneStudent.email, loneStudent.team);
		submissions = evaluationsLogic.getSubmissionsForEvaluation(course.id, evaluation2.name);
		assertEquals(9, submissions.size());

		// TODO: test for invalid inputs
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
		List<SubmissionAttributes> submissions = evaluationsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(26, submissions.size());
		
		// Check the same for the other evaluation, to detect any state leakage
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation2.name, "incoming@student.com", student.team);
		submissions = evaluationsLogic.getSubmissionsForEvaluation(course.id, evaluation2.name);
		assertEquals(26, submissions.size());
		
		______TS("moving to new team");
		
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation1.name, "incoming@student.com", "new team");
		//There should be one more submission now.
		submissions = evaluationsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(27, submissions.size());
		
		// Check the same for the other evaluation
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation2.name, "incoming@student.com", "new team");
		//There should be one more submission now.
		submissions = evaluationsLogic.getSubmissionsForEvaluation(course.id, evaluation2.name);
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
