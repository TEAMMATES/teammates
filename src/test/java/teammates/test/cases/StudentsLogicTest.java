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
import teammates.logic.StudentsLogic;
import teammates.logic.SubmissionsLogic;
import teammates.logic.api.Logic;
import teammates.logic.automated.EvaluationOpeningRemindersServlet;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.storage.datastore.Datastore;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class StudentsLogicTest extends BaseTestCase{
	
	protected static StudentsLogic studentsLogic = StudentsLogic.inst();
	
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
	public void testEnrollStudent() throws Exception {

		Logic logic = new Logic();

		restoreTypicalDataInDatastore();

		String instructorId = "instructorForEnrollTesting";
		String instructorCourse = "courseForEnrollTesting";
		String instructorName = "ICET Name";
		String instructorEmail = "instructor@icet.com";
		String instructorInstitute = "National University of Singapore";
		loginAsAdmin("admin.user");
		logic.createAccount(instructorId, instructorName, true, instructorEmail, instructorInstitute);
		logic.deleteInstructor(instructorCourse, instructorId);
		logic.createInstructorAccount(instructorId, instructorCourse, instructorName, instructorEmail, instructorInstitute);
		String courseId = "courseForEnrollTest";
		logic.createCourseAndInstructor(instructorId, courseId, "Course for Enroll Testing");

		______TS("add student into empty course");

		StudentAttributes student1 = new StudentAttributes("t|n|e@g|c", courseId);

		// check if the course is empty
		assertEquals(0, logic.getStudentsForCourse(courseId).size());

		// add a new student and verify it is added and treated as a new student
		StudentAttributes enrollmentResult = invokeEnrollStudent(student1);
		assertEquals(1, logic.getStudentsForCourse(courseId).size());
		LogicTest.verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentAttributes.UpdateStatus.NEW);
		LogicTest.verifyPresentInDatastore(student1);

		______TS("add existing student");

		// Verify it was not added
		enrollmentResult = invokeEnrollStudent(student1);
		LogicTest.verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentAttributes.UpdateStatus.UNMODIFIED);

		______TS("modify info of existing student");

		// verify it was treated as modified
		StudentAttributes student2 = dataBundle.students.get("student1InCourse1");
		student2.name = student2.name + "y";
		StudentAttributes studentToEnroll = new StudentAttributes(student2.id, student2.email,
				student2.name, student2.comments, student2.course,
				student2.team);
		enrollmentResult = invokeEnrollStudent(studentToEnroll);
		LogicTest.verifyEnrollmentResultForStudent(studentToEnroll, enrollmentResult,
				StudentAttributes.UpdateStatus.MODIFIED);
		// check if the student is actually modified in datastore and existing
		// values not specified in enroll action (e.g, id) prevail
		LogicTest.verifyPresentInDatastore(student2);

		______TS("add student into non-empty course");

		StudentAttributes student3 = new StudentAttributes("t3|n3|e3@g|c3", courseId);
		enrollmentResult = invokeEnrollStudent(student3);
		assertEquals(2, logic.getStudentsForCourse(courseId).size());
		LogicTest.verifyEnrollmentResultForStudent(student3, enrollmentResult,
				StudentAttributes.UpdateStatus.NEW);

		______TS("add student without team");

		StudentAttributes student4 = new StudentAttributes("|n4|e4@g", courseId);
		enrollmentResult = invokeEnrollStudent(student4);
		assertEquals(3, logic.getStudentsForCourse(courseId).size());
		LogicTest.verifyEnrollmentResultForStudent(student4, enrollmentResult,
				StudentAttributes.UpdateStatus.NEW);
	}

	private static StudentAttributes invokeEnrollStudent(StudentAttributes student)
			throws Exception {
		Method privateMethod = StudentsLogic.class.getDeclaredMethod("enrollStudent",
				new Class[] { StudentAttributes.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { student };
		return (StudentAttributes) privateMethod.invoke(StudentsLogic.inst(), params);
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
