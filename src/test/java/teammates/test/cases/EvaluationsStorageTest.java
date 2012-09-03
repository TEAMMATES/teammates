package teammates.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.EvaluationData.EvalStatus;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.logic.automated.EvaluationOpeningRemindersServlet;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.storage.api.EvaluationsStorage;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Submission;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class EvaluationsStorageTest extends BaseTestCase{
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(EvaluationsStorage.class);
		Datastore.initialize();
	}
	
	@Before
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
		for (EvaluationData e : dataBundle.evaluations.values()) {
			e.activated = true;
			backdoor.editEvaluation(e);
			assertTrue(backdoor.getEvaluation(e.course, e.name).getStatus() != EvalStatus.AWAITING);
		}
		assertEquals(0, EvaluationsStorage.inst().getEvaluationsDb().getReadyEvaluations().size());

		______TS("typical case, two evaluations activated");
		// Reuse an existing evaluation to create a new one that is ready to
		// activate. Put this evaluation in a negative time zone.
		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
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
		CourseData course1 = backdoor.getCourseDetails(evaluation.course);
		assertEquals(0, course1.unregisteredTotal);

		// Create another evaluation in another course in similar fashion.
		// Put this evaluation in a positive time zone.
		// This one too is ready to activate.
		evaluation = dataBundle.evaluations.get("evaluation1InCourse1OfCoord2");
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
		CourseData course2 = backdoor.getCourseDetails(evaluation.course);
		assertEquals(0, course2.unregisteredTotal);

		// Create another evaluation not ready to be activated yet.
		// Put this evaluation in same time zone.
		evaluation = dataBundle.evaluations.get("evaluation1InCourse1OfCoord2");
		evaluation.activated = false;
		evaluation.name = "new evaluation - start time in future";

		timeZone = 0.0;
		evaluation.timeZone = timeZone;

		int oneSecondInMs = 1000;
		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				oneSecondInMs, timeZone);
		backdoor.createEvaluation(evaluation);

		// verify number of ready evaluations.
		assertEquals(2, EvaluationsStorage.inst().getEvaluationsDb().getReadyEvaluations().size());

		// Other variations of ready/not-ready states should be checked at
		// Evaluation level
	}
	
	@Test
	public void testDeleteSubmissionsForOutgoingMember() throws Exception {
		loginAsAdmin("admin.user");

		______TS("typical case");

		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();

		CourseData course = dataBundle.courses.get("course1OfCoord1");
		EvaluationData evaluation1 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		EvaluationData evaluation2 = dataBundle.evaluations
				.get("evaluation2InCourse1OfCoord1");
		StudentData student = dataBundle.students.get("student1InCourse1");

		// We have a 4-member team and a 1-member team.
		// Therefore, we expect (4*4)+(1*1)=17 submissions.
		List<SubmissionData> submissions = EvaluationsStorage.inst()
				.getSubmissionsDb().getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(17, submissions.size());

		EvaluationsStorage.inst().getSubmissionsDb().deleteSubmissionsForOutgoingMember(course.id, evaluation1.name, student.email, student.team);
		
		// We have a 3-member team and a 1-member team.
		// Therefore, we expect (3*3)+(1*1)=10 submissions.
		submissions = EvaluationsStorage.inst().getSubmissionsDb().getSubmissionsForEvaluation(course.id,
				evaluation1.name);
		assertEquals(10, submissions.size());

		// check the same for the other evaluation, to detect state leakage
		EvaluationsStorage.inst().getSubmissionsDb().deleteSubmissionsForOutgoingMember(course.id, evaluation2.name, student.email, student.team);
		
		submissions = EvaluationsStorage.inst().getSubmissionsDb().getSubmissionsForEvaluation(course.id,
				evaluation2.name);
		assertEquals(10, submissions.size());

		// verify the student is no longer included in submissions
		for (SubmissionData s : submissions) {
			assertTrue(!s.reviewee.equals(student.email));
			assertTrue(!s.reviewer.equals(student.email));
		}

		______TS("only one student in team");

		StudentData loneStudent = dataBundle.students.get("student5InCourse1");
		EvaluationsStorage.inst().getSubmissionsDb().deleteSubmissionsForOutgoingMember(course.id, evaluation1.name, loneStudent.email, loneStudent.team);
		// We expect one fewer submissions than before.
		submissions = EvaluationsStorage.inst().getSubmissionsDb().getSubmissionsForEvaluation(course.id,
				evaluation1.name);
		assertEquals(9, submissions.size());

		EvaluationsStorage.inst().getSubmissionsDb().deleteSubmissionsForOutgoingMember(course.id, evaluation2.name, loneStudent.email, loneStudent.team);
		submissions = EvaluationsStorage.inst().getSubmissionsDb().getSubmissionsForEvaluation(course.id,
				evaluation2.name);
		assertEquals(9, submissions.size());

		// TODO: test for invalid inputs
	}
	
	@Test
	public void testAddSubmissionsForIncomingMember() throws Exception {
		loginAsAdmin("admin.user");

		______TS("typical case");

		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();

		CourseData course = dataBundle.courses.get("course1OfCoord1");
		EvaluationData evaluation1 = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		EvaluationData evaluation2 = dataBundle.evaluations
				.get("evaluation2InCourse1OfCoord1");
		StudentData student = dataBundle.students.get("student1InCourse1");

		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation1.name, "incoming@student.com", student.team);

		// We have a 5-member team and a 1-member team.
		// Therefore, we expect (5*5)+(1*1)=26 submissions.
		List<SubmissionData> submissions = EvaluationsStorage.inst()
				.getSubmissionsDb().getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(26, submissions.size());
		
		// Check the same for the other evaluation, to detect any state leakage
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation2.name, "incoming@student.com", student.team);
		submissions = EvaluationsStorage.inst()
				.getSubmissionsDb().getSubmissionsForEvaluation(course.id, evaluation2.name);
		assertEquals(26, submissions.size());
		
		______TS("moving to new team");
		
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation1.name, "incoming@student.com", "new team");
		//There should be one more submission now.
		submissions = EvaluationsStorage.inst()
				.getSubmissionsDb().getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(27, submissions.size());
		
		// Check the same for the other evaluation
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation2.name, "incoming@student.com", "new team");
		//There should be one more submission now.
		submissions = EvaluationsStorage.inst()
				.getSubmissionsDb().getSubmissionsForEvaluation(course.id, evaluation2.name);
		assertEquals(27, submissions.size());

		//TODO: test invalid inputs

	}
	
	private void invokeAddSubmissionsForIncomingMember(String courseId,
			String evaluationName, String studentEmail, String newTeam)throws Exception {
		Method privateMethod = EvaluationsStorage.class.getDeclaredMethod(
				"addSubmissionsForIncomingMember", new Class[] { String.class,
						String.class, String.class, String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] {courseId,
				 evaluationName,  studentEmail, newTeam };
		privateMethod.invoke(EvaluationsStorage.inst(), params);
	}
	
	
	
	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(EvaluationOpeningRemindersServlet.class);
	}

	@After
	public void caseTearDown() {
		helper.tearDown();
	}

}
