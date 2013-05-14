package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import java.util.HashMap;
import java.util.List;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.Emails;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.storage.datastore.Datastore;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.Gson;

public class BackDoorLogicTest extends BaseTestCase {
	Gson gson = Common.getTeammatesGson();
	private static DataBundle dataBundle = getTypicalDataBundle();

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(BackDoorLogic.class);
		Datastore.initialize();
	}

	@BeforeMethod
	public void caseSetUp() throws ServletException {
		dataBundle = getTypicalDataBundle();

		LocalTaskQueueTestConfig ltqtc = new LocalTaskQueueTestConfig();
		LocalUserServiceTestConfig lustc = new LocalUserServiceTestConfig();
		helper = new LocalServiceTestHelper(
				new LocalDatastoreServiceTestConfig(),
				new LocalMailServiceTestConfig(), lustc, ltqtc);
		setHelperTimeZone(helper);
		helper.setUp();
		System.out.println("case set up");
	}

	@Test
	public void testPersistDataBundle() throws Exception {

		BackDoorLogic logic = new BackDoorLogic();
		String jsonString = "";
		try {
			jsonString = Common.readFile(Common.TEST_DATA_FOLDER
					+ "/typicalDataBundle.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		loginAsAdmin("admin.user");
		
		DataBundle dataBundle = gson.fromJson(jsonString, DataBundle.class);
		// clean up the datastore first, to avoid clashes with existing data
		HashMap<String, InstructorAttributes> instructors = dataBundle.instructors;
		for (InstructorAttributes instructor : instructors.values()) {
			logic.deleteInstructor(instructor.courseId, instructor.googleId);
		}

		// try with empty dataBundle
		String status = logic.persistNewDataBundle(new DataBundle());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);

		// try with typical dataBundle
		logic.persistNewDataBundle(dataBundle);
		verifyPresentInDatastore(jsonString);

		// try again, should throw exception
		try {
			logic.persistNewDataBundle(dataBundle);
			Assert.fail();
		} catch (EntityAlreadyExistsException e) {
		}

		// try with null
		DataBundle nullDataBundle = null;
		try {
			logic.persistNewDataBundle(nullDataBundle);
			Assert.fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}

		// try with invalid parameters in an entity
		CourseAttributes invalidCourse = CourseAttributesTest.generateValidCourseAttributesObject();
		invalidCourse.id = "invalid id";
		dataBundle = new DataBundle();
		dataBundle.courses.put("invalid", invalidCourse);
		try {
			logic.persistNewDataBundle(dataBundle);
			Assert.fail();
		} catch (InvalidParametersException e) {
			assertTrue(e.getMessage().contains("not acceptable to TEAMMATES as a Course ID because it is not in the correct format"));
		}

		// Not checking for invalid values in other entities because they
		// should be checked at lower level methods
	}

	@Test
	public void testActivateReadyEvaluations() throws Exception {
		BackDoorLogic backdoor = new BackDoorLogic();
		loginAsAdmin("admin.user");
		restoreTypicalDataInDatastore();
		// ensure all existing evaluations are already activated.
		for (EvaluationAttributes e : dataBundle.evaluations.values()) {
			e.activated = true;
			backdoor.editEvaluation(e);
			assertTrue(backdoor.getEvaluation(e.course, e.name).getStatus() != EvalStatus.AWAITING);
		}
		List<MimeMessage> emailsSent = backdoor.activateReadyEvaluations();
		assertEquals(0, emailsSent.size());
		
		______TS("typical case, two evaluations activated");

		// Reuse an existing evaluation to create a new one that is ready to
		// activate. Put this evaluation in a negative time zone.
		EvaluationAttributes evaluation1 = dataBundle.evaluations
				.get("evaluation1InCourse1");
		String nameOfEvalInCourse1 = "new-eval-in-course-1-tARE";
		evaluation1.name = nameOfEvalInCourse1;

		evaluation1.activated = false;

		double timeZone = -1.0;
		evaluation1.timeZone = timeZone;

		evaluation1.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				0, timeZone);
		evaluation1.endTime = Common.getDateOffsetToCurrentTime(2);

		backdoor.createEvaluation(evaluation1);
		assertEquals("This evaluation is not ready to activate as expected "+ evaluation1.toString(),
				true, 
				backdoor.getEvaluation(evaluation1.course, evaluation1.name).toEntity().isReadyToActivate());

		// Create another evaluation in another course in similar fashion.
		// Put this evaluation in a positive time zone.
		// This one too is ready to activate.
		EvaluationAttributes evaluation2 = dataBundle.evaluations
				.get("evaluation1InCourse2");
		evaluation2.activated = false;
		String nameOfEvalInCourse2 = "new-evaluation-in-course-2-tARE";
		evaluation2.name = nameOfEvalInCourse2;

		timeZone = 2.0;
		evaluation2.timeZone = timeZone;

		evaluation2.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				0, timeZone);
		evaluation2.endTime = Common.getDateOffsetToCurrentTime(2);

		backdoor.createEvaluation(evaluation2);
		assertEquals("This evaluation is not ready to activate as expected "+ evaluation2.toString(),
				true, 
				backdoor.getEvaluation(evaluation2.course, evaluation2.name).toEntity().isReadyToActivate());
		
		// Create an orphan evaluation (this should be ignored by SUT)
		EvaluationAttributes orphan = new EvaluationAttributes();
		orphan.name = "Orphan Evaluation";
		orphan.course = "non-existent-course-BDLT";
		orphan.timeZone = evaluation2.timeZone;
		orphan.startTime = evaluation2.startTime;
		orphan.endTime = evaluation2.endTime;
		orphan.activated = evaluation2.activated;
		orphan.published = evaluation2.published;
		backdoor.createEvaluation(orphan);
		assertEquals("This evaluation is not ready to activate as expected "+ orphan.toString(),
				true, 
				backdoor.getEvaluation(orphan.course, orphan.name).toEntity().isReadyToActivate());

		emailsSent = backdoor.activateReadyEvaluations();
		int course1StudentCount = backdoor.getStudentListForCourse(
				evaluation1.course).size();
		int course2StudentCount = backdoor.getStudentListForCourse(
				evaluation2.course).size();

		assertEquals(course1StudentCount + course2StudentCount,
				emailsSent.size());

		// verify the two evaluations are marked as activated

		emailsSent = backdoor.activateReadyEvaluations();
		assertEquals(0, emailsSent.size());

	}

	@Test
	public void testSendRemindersForClosingEvaluations() throws Exception {
		BackDoorLogic backdoor = new BackDoorLogic();
		loginAsAdmin("admin.user");
		restoreTypicalDataInDatastore();
		List<MimeMessage> emailsSent = backdoor
				.sendRemindersForClosingEvaluations();
		assertEquals(0, emailsSent.size());

		// Reuse an existing evaluation to create a new one that is
		// closing in 24 hours.
		EvaluationAttributes evaluation1 = dataBundle.evaluations
				.get("evaluation1InCourse1");
		String nameOfEvalInCourse1 = "new-eval-in-course-1-tSRFCE";
		evaluation1.name = nameOfEvalInCourse1;

		evaluation1.activated = true;

		double timeZone = 0.0;
		evaluation1.timeZone = timeZone;
		evaluation1.startTime = Common.getDateOffsetToCurrentTime(-1);
		evaluation1.endTime = Common.getDateOffsetToCurrentTime(1);
		backdoor.createEvaluation(evaluation1);

		// Create another evaluation in another course in similar fashion.
		// This one too is closing in 24 hours.
		EvaluationAttributes evaluation2 = dataBundle.evaluations
				.get("evaluation1InCourse2");
		evaluation2.activated = true;
		String nameOfEvalInCourse2 = "new-evaluation-in-course-2-tARE";
		evaluation2.name = nameOfEvalInCourse2;

		evaluation2.timeZone = 0.0;

		evaluation2.startTime = Common.getDateOffsetToCurrentTime(-2);
		evaluation2.endTime = Common.getDateOffsetToCurrentTime(1);

		backdoor.createEvaluation(evaluation2);
		
		// Create an orphan evaluation (this should be ignored by SUT)
		EvaluationAttributes orphan = new EvaluationAttributes();
		orphan.name = "Orphan Evaluation";
		orphan.course = "non-existent-course-BDLT";
		orphan.timeZone = evaluation2.timeZone;
		orphan.startTime = evaluation2.startTime;
		orphan.endTime = evaluation2.endTime;
		orphan.activated = evaluation2.activated;
		orphan.published = evaluation2.published;
		backdoor.createEvaluation(orphan);
		
		emailsSent = backdoor.sendRemindersForClosingEvaluations();

		int course1StudentCount = backdoor.getStudentListForCourse(
				evaluation1.course).size();
		int course2StudentCount = backdoor.getStudentListForCourse(
				evaluation2.course).size();

		assertEquals(course1StudentCount + course2StudentCount,
				emailsSent.size());

		for (MimeMessage m : emailsSent) {
			String subject = m.getSubject();
			assertTrue(subject.contains(evaluation1.name)
					|| subject.contains(evaluation2.name));
			assertTrue(subject.contains(Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_CLOSING));
		}

	}

	private void verifyPresentInDatastore(String dataBundleJsonString)
			throws Exception {

		DataBundle data = gson.fromJson(dataBundleJsonString, DataBundle.class);
		HashMap<String, InstructorAttributes> instructors = data.instructors;
		for (InstructorAttributes expectedInstructor : instructors.values()) {
			LogicTest.verifyPresentInDatastore(expectedInstructor);
		}

		HashMap<String, CourseAttributes> courses = data.courses;
		for (CourseAttributes expectedCourse : courses.values()) {
			LogicTest.verifyPresentInDatastore(expectedCourse);
		}

		HashMap<String, StudentAttributes> students = data.students;
		for (StudentAttributes expectedStudent : students.values()) {
			LogicTest.verifyPresentInDatastore(expectedStudent);
		}

		HashMap<String, EvaluationAttributes> evaluations = data.evaluations;
		for (EvaluationAttributes expectedEvaluation : evaluations.values()) {
			LogicTest.verifyPresentInDatastore(expectedEvaluation);
		}

		HashMap<String, SubmissionAttributes> submissions = data.submissions;
		for (SubmissionAttributes expectedSubmission : submissions.values()) {
			LogicTest.verifyPresentInDatastore(expectedSubmission);
		}

	}

	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(BackDoorLogic.class);
	}

	@AfterMethod
	public void caseTearDown() {
		helper.tearDown();
		System.out.println("case torn down");
	}

}
