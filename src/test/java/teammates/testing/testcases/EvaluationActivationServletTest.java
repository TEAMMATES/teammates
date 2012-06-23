package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.BackDoorLogic;
import teammates.Datastore;
import teammates.EvaluationActivationServlet;
import teammates.api.Common;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.EvaluationData.EvalStatus;
import teammates.datatransfer.StudentData;
import teammates.manager.Evaluations;

import com.google.appengine.api.taskqueue.dev.QueueStateInfo.TaskStateInfo;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public class EvaluationActivationServletTest extends BaseTestCase {

	private LocalServiceTestHelper helper;

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(EvaluationActivationServlet.class);
		setLogLevelOfClass(Evaluations.class, Level.FINE);
		Datastore.initialize();
	}

	@Before
	public void caseSetUp() throws ServletException, IOException {

		LocalTaskQueueTestConfig ltqtc = new LocalTaskQueueTestConfig();
		setEmailQueuePath(ltqtc);
		LocalUserServiceTestConfig localUserServiceTestConfig = new LocalUserServiceTestConfig();
		helper = new LocalServiceTestHelper(
				new LocalDatastoreServiceTestConfig(),
				new LocalMailServiceTestConfig(), localUserServiceTestConfig,
				ltqtc);
		setHelperTimeZone(helper);
		helper.setUp();
	}

	@Test
	public void testActivateReadyEvaluations() throws Exception {
		// ensure no emails in the queue
		assertEquals(0, getNumberOfEmailTasksInQueue());

		______TS("No evaluations activated");
		// ensure no existing evaluations are ready to be activated
		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();
		BackDoorLogic backdoor = new BackDoorLogic();
		for (EvaluationData e : dataBundle.evaluations.values()) {
			e.activated = true;
			backdoor.editEvaluation(e);
			assertTrue(backdoor.getEvaluation(e.course, e.name).getStatus() != EvalStatus.AWAITING);
		}
		EvaluationActivationServlet activator = new EvaluationActivationServlet();
		assertEquals(0, getNumberOfEmailTasksInQueue());

		______TS("typical case, two evaluations activated");
		// Reuse an existing evaluation to create a new one that is ready to 
		//  activate. Put this evaluation in a negative time zone.
		EvaluationData evaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		evaluation.activated = false;
		double timeZone = -1.0;
		int oneSecondInMs = 1000;
		String nameOfEvalInCourse1 = "new-evaluation-in-course-1-tARE";
		evaluation.name = nameOfEvalInCourse1;
		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(-oneSecondInMs, timeZone);
		evaluation.timeZone = timeZone; 
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
		timeZone = 2.0;
		String nameOfEvalInCourse2 = "new-evaluation-in-course-2";
		evaluation.name = nameOfEvalInCourse2;
		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(-oneSecondInMs, timeZone);
		evaluation.timeZone = timeZone;
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
		evaluation.timeZone = 0;
		evaluation.startTime = Common.getMsOffsetToCurrentTime(oneSecondInMs);
		backdoor.createEvaluation(evaluation);

		//Activate evaluations.
		activator.activateReadyEvaluations();

		//Check the number of emails created.
		assertEquals(course1.studentsTotal + course2.studentsTotal,
				getNumberOfEmailTasksInQueue());
		
		//Ensure they are sent to the right students in course 1.
		List<StudentData> studentsInCourse1 = backdoor.getStudentListForCourse(course1.id);
		for(StudentData s: studentsInCourse1){
			verifyActivationEmailToStudent(s, nameOfEvalInCourse1);
		}
		//Ensure they are sent to the right students in course 2.
		List<StudentData> studentsInCourse2 = backdoor.getStudentListForCourse(course2.id);
		for(StudentData s: studentsInCourse2){
			verifyActivationEmailToStudent(s, nameOfEvalInCourse2);
		}
		
	}

	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(EvaluationActivationServlet.class);
		setLogLevelOfClass(Evaluations.class, Level.WARNING);
	}

	@After
	public void caseTearDown() {
		helper.tearDown();
	}
	
	private void verifyActivationEmailToStudent(StudentData student, String evalName) {
		List<TaskStateInfo> taskInfoList = getTasksInQueue("email-queue");
		for (TaskStateInfo tsi : taskInfoList) {
			String emailTaskBody = tsi.getBody();
			System.out.println("email task: "+emailTaskBody);
			if (emailTaskBody.contains("operation=informstudentsofevaluationopening&email="
					+ student.email.replace("@", "%40"))
					&& emailTaskBody.contains("courseid=" + student.course)
					&& emailTaskBody.contains(evalName)) {
				return;
			}
		}
		fail();
	}

}
