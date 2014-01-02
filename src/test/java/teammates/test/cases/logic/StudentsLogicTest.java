package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.storage.api.StudentsDb;
import teammates.storage.entity.Student;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.cases.storage.EvaluationsDbTest;
import teammates.test.driver.AssertHelper;

import com.google.appengine.api.datastore.KeyFactory;

public class StudentsLogicTest extends BaseComponentTestCase{
	
	//TODO: add missing test cases. Some of the test content can be transferred from LogicTest.
	
	protected static StudentsLogic studentsLogic = StudentsLogic.inst();
	protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();
	protected static AccountsLogic accountsLogic = AccountsLogic.inst();
	protected static CoursesLogic coursesLogic = CoursesLogic.inst();
	protected static EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
	private static DataBundle dataBundle = getTypicalDataBundle();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(StudentsLogic.class);
	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	public void testEnrollStudent() throws Exception {

		String instructorId = "instructorForEnrollTesting";
		String instructorCourse = "courseForEnrollTesting";
		
		//delete leftover data, if any
		accountsLogic.deleteAccountCascade(instructorId);
		coursesLogic.deleteCourseCascade(instructorCourse);
		
		//create fresh test data
		accountsLogic.createAccount(
				new AccountAttributes(instructorId, "ICET Instr Name", true,
						"instructor@icet.com", "National University of Singapore"));
		coursesLogic.createCourseAndInstructor(instructorId, instructorCourse, "Course for Enroll Testing");

		______TS("add student into empty course");

		StudentAttributes student1 = new StudentAttributes("t1|n|e@g|c", instructorCourse);

		// check if the course is empty
		assertEquals(0, studentsLogic.getStudentsForCourse(instructorCourse).size());

		// add a new student and verify it is added and treated as a new student
		StudentAttributes enrollmentResult = invokeEnrollStudent(student1);
		assertEquals(1, studentsLogic.getStudentsForCourse(instructorCourse).size());
		LogicTest.verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentAttributes.UpdateStatus.NEW);
		LogicTest.verifyPresentInDatastore(student1);

		______TS("add existing student");

		// Verify it was not added
		enrollmentResult = invokeEnrollStudent(student1);
		LogicTest.verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentAttributes.UpdateStatus.UNMODIFIED);
		assertEquals(1, studentsLogic.getStudentsForCourse(instructorCourse).size());

		______TS("add student into non-empty course");
		StudentAttributes student2 = new StudentAttributes("t1|n2|e2@g|c", instructorCourse);
		enrollmentResult = invokeEnrollStudent(student2);
		LogicTest.verifyEnrollmentResultForStudent(student2, enrollmentResult,
				StudentAttributes.UpdateStatus.NEW);
		
		//add some more students to the same course (we add more than one 
		//  because we can use them for testing cascade logic later in this test case)
		invokeEnrollStudent(new StudentAttributes("t2|n3|e3@g|c", instructorCourse));
		invokeEnrollStudent(new StudentAttributes("t2|n4|e4@g|c", instructorCourse));
		assertEquals(4, studentsLogic.getStudentsForCourse(instructorCourse).size());
		
		______TS("modify info of existing student");
		//add some more details to the student
		student1.googleId = "googleId";
		studentsLogic.updateStudentCascade(student1.email, student1);
		
		//add a new evaluations (to check the cascade logic of the SUT)
		EvaluationAttributes e = EvaluationsDbTest.generateTypicalEvaluation();
		e.courseId = instructorCourse;
		evaluationsLogic.createEvaluationCascadeWithoutSubmissionQueue(e);

		// verify it was treated as modified in datastore and existing
		// values not specified in enroll action (e.g, id) prevail
		student1.name = student1.name+"y";
		enrollmentResult = invokeEnrollStudent(student1);
		LogicTest.verifyEnrollmentResultForStudent(student1, enrollmentResult,
				StudentAttributes.UpdateStatus.MODIFIED);
		LogicTest.verifyPresentInDatastore(student1);
		
		//verify cascade logic
		verifyCascasedToSubmissions(instructorCourse);

		______TS("add student without team");

		StudentAttributes student4 = new StudentAttributes("|n6|e6@g", instructorCourse);
		enrollmentResult = invokeEnrollStudent(student4);
		assertEquals(5, studentsLogic.getStudentsForCourse(instructorCourse).size());
		LogicTest.verifyEnrollmentResultForStudent(student4, enrollmentResult,
				StudentAttributes.UpdateStatus.NEW);
		
		verifyCascasedToSubmissions(instructorCourse);
		
		______TS("error during enrollment");

		StudentAttributes student5 = new StudentAttributes("|n6|e6@g@", instructorCourse);
		enrollmentResult = invokeEnrollStudent(student5);
		assertEquals (StudentAttributes.UpdateStatus.ERROR, enrollmentResult.updateStatus);
		assertEquals(5, studentsLogic.getStudentsForCourse(instructorCourse).size());
		
	}

	@Test
	public void testUpdateStudentCascade() throws Exception {
			
		______TS("typical edit");

		restoreTypicalDataInDatastore();
		dataBundle = getTypicalDataBundle();

		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		LogicTest.verifyPresentInDatastore(student1InCourse1);
		String originalEmail = student1InCourse1.email;
		student1InCourse1.name = student1InCourse1.name + "x";
		student1InCourse1.googleId = student1InCourse1.googleId + "x";
		student1InCourse1.comments = student1InCourse1.comments + "x";
		student1InCourse1.email = student1InCourse1.email + "x";
		student1InCourse1.team = "Team 1.2"; // move to a different team

		// take a snapshot of submissions before
		List<SubmissionAttributes> submissionsBeforeEdit = submissionsLogic.getSubmissionsForCourse(student1InCourse1.course);

		// verify student details changed correctly
		studentsLogic.updateStudentCascade(originalEmail, student1InCourse1);
		LogicTest.verifyPresentInDatastore(student1InCourse1);

		// take a snapshot of submissions after the edit
		List<SubmissionAttributes> submissionsAfterEdit = submissionsLogic.getSubmissionsForCourse(student1InCourse1.course);
		
		// We moved a student from a 4-person team to an existing 1-person team.
		// We have 2 evaluations in the course.
		// Therefore, submissions that will be deleted = 7*2 = 14
		//              submissions that will be added = 3*2
		assertEquals(submissionsBeforeEdit.size() - 14  + 6,
				submissionsAfterEdit.size()); 
		
		// verify new submissions were created to match new team structure
		LogicTest.verifySubmissionsExistForCurrentTeamStructureInAllExistingEvaluations(submissionsAfterEdit,
				student1InCourse1.course);

		______TS("check for KeepExistingPolicy : change email only");
		
		// create an empty student and then copy course and email attributes
		StudentAttributes copyOfStudent1 = new StudentAttributes();
		copyOfStudent1.course = student1InCourse1.course;
		originalEmail = student1InCourse1.email;

		String newEmail = student1InCourse1.email + "y";
		student1InCourse1.email = newEmail;
		copyOfStudent1.email = newEmail;

		studentsLogic.updateStudentCascade(originalEmail, copyOfStudent1);
		LogicTest.verifyPresentInDatastore(student1InCourse1);

		______TS("check for KeepExistingPolicy : change nothing");	
		
		originalEmail = student1InCourse1.email;
		copyOfStudent1.email = null;
		studentsLogic.updateStudentCascade(originalEmail, copyOfStudent1);
		LogicTest.verifyPresentInDatastore(copyOfStudent1);
		
		______TS("non-existent student");
		
		try {
			studentsLogic.updateStudentCascade("non-existent@email", student1InCourse1);
			signalFailureToDetectException();
		} catch (EntityDoesNotExistException e) {
			assertEquals(StudentsDb.ERROR_UPDATE_NON_EXISTENT_STUDENT
					+ student1InCourse1.course + "/" + "non-existent@email",
					e.getMessage());
		}

		______TS("check for InvalidParameters");
		try {
			copyOfStudent1.email = "invalid email";
			studentsLogic.updateStudentCascade(originalEmail, copyOfStudent1);
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			AssertHelper.assertContains(FieldValidator.REASON_INCORRECT_FORMAT,
					e.getMessage());
		}
	}
	
	@Test
	public void testKeyGeneration() {
		long key = 5;
		String longKey = KeyFactory.createKeyString(
				Student.class.getSimpleName(), key);
		long reverseKey = KeyFactory.stringToKey(longKey).getId();
		assertEquals(key, reverseKey);
		assertEquals("Student", KeyFactory.stringToKey(longKey).getKind());
	}
	
	@Test
	public void testEnrollLinesChecking() throws Exception {
		String info;
		String enrollLines;
		String courseId = "CourseID";
		coursesLogic.createCourse(courseId, "CourseName");
		
		List<String> invalidInfo;
		List<String> expectedInvalidInfo = new ArrayList<String>();
		
		______TS("enrollLines with invalid parameters");
		String invalidTeamName = StringHelper.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);
		String invalidStudentName = StringHelper.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
		String invalidEmail = "invalid_email.com";
		
		String lineWithInvalidTeamName = invalidTeamName + "| John | john@email.com";
		String lineWithInvalidStudentName = "Team 1 |" + invalidStudentName + "| student@email.com";
		String lineWithInvalidEmail = "Team 1 | James |" + invalidEmail;
		String lineWithInvalidStudentNameAndEmail = "Team 2 |" + invalidStudentName + "|" + invalidEmail;
		String lineWithInvalidTeamNameAndEmail = invalidTeamName + "| Paul |" + invalidEmail;
		String lineWithInvalidTeamNameAndStudentNameAndEmail = invalidTeamName + "|" + invalidStudentName + "|" + invalidEmail;;
		
		enrollLines = lineWithInvalidTeamName + Const.EOL + lineWithInvalidStudentName + Const.EOL +
					lineWithInvalidEmail + Const.EOL + lineWithInvalidStudentNameAndEmail + Const.EOL +
					lineWithInvalidTeamNameAndEmail + Const.EOL + lineWithInvalidTeamNameAndStudentNameAndEmail;
		
		invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

		expectedInvalidInfo.clear();
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidTeamName, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamName, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidStudentName, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidStudentName, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidEmail, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidEmail, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidStudentNameAndEmail, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidStudentNameAndEmail, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidTeamNameAndEmail, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamNameAndEmail, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidTeamNameAndStudentNameAndEmail, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamNameAndStudentNameAndEmail, info));
		
		for (int i = 0; i < invalidInfo.size(); i++) {
			assertEquals(expectedInvalidInfo.get(i), invalidInfo.get(i));
		}
		
		______TS("enrollLines with too few or extra parameters");
		String lineWithNoEmailInput = "Team 4 | StudentWithNoEmailInput";
		String lineWithExtraParameters = "Team 4 | StudentWithExtraParameters | student@email.com | comment | extra_parameter";
		
		enrollLines = lineWithNoEmailInput + Const.EOL + lineWithExtraParameters;
		
		invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

		expectedInvalidInfo.clear();
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithNoEmailInput, StudentAttributes.ERROR_ENROLL_LINE_TOOFEWPARTS));
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithExtraParameters, StudentAttributes.ERROR_ENROLL_LINE_TOOMANYPARTS));
		
		for (int i = 0; i < invalidInfo.size(); i++) {
			assertEquals(expectedInvalidInfo.get(i), invalidInfo.get(i));
		}
		
		______TS("enrollLines with some empty fields");
		String lineWithTeamNameEmpty = "    | StudentWithTeamFieldEmpty | student@email.com";
		String lineWithStudentNameEmpty = "Team 5 |  | no_name@email.com";
		String lineWithEmailEmpty = "Team 5 | StudentWithEmailFieldEmpty | |";
		
		enrollLines = lineWithTeamNameEmpty + Const.EOL + lineWithStudentNameEmpty + Const.EOL + lineWithEmailEmpty;

		invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);
		expectedInvalidInfo.clear();
		info = StringHelper.toString((new StudentAttributes(lineWithStudentNameEmpty, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithStudentNameEmpty, info));
		info = StringHelper.toString((new StudentAttributes(lineWithEmailEmpty, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithEmailEmpty, info));

		for (int i = 0; i < invalidInfo.size(); i++) {
			assertEquals(expectedInvalidInfo.get(i), invalidInfo.get(i));
		}

		______TS("enrollLines with correct input");
		String lineWithCorrectInput = "Team 3 | Mary | mary@email.com";
		String lineWithCorrectInputWithComment = "Team 4 | Benjamin | benjamin@email.com | Foreign student";
		
		enrollLines = lineWithCorrectInput + Const.EOL + lineWithCorrectInputWithComment;
		
		invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

		assertEquals(0, invalidInfo.size());
		
		______TS("enrollLines with only whitespaces");
		String lineWithOnlyTab = "\t";
		String lineWithOnlySpaces = "    ";
		String lineWithEmptyLength = "";
		
		enrollLines = lineWithOnlyTab + Const.EOL + lineWithOnlySpaces + Const.EOL + lineWithEmptyLength;
		
		invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);

		assertEquals(0, invalidInfo.size());
		
		______TS("enrollLines with a mix of all above cases");
		enrollLines = lineWithInvalidTeamName + Const.EOL + lineWithInvalidTeamNameAndStudentNameAndEmail + Const.EOL + lineWithExtraParameters + Const.EOL +
				lineWithTeamNameEmpty + Const.EOL + lineWithCorrectInput + Const.EOL + lineWithOnlyTab;

		invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);
		
		expectedInvalidInfo.clear();
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidTeamName, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamName, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidTeamNameAndStudentNameAndEmail, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamNameAndStudentNameAndEmail, info));
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithExtraParameters, StudentAttributes.ERROR_ENROLL_LINE_TOOMANYPARTS));
		info = StringHelper.toString((new StudentAttributes(lineWithTeamNameEmpty, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithTeamNameEmpty, info));
		info = StringHelper.toString((new StudentAttributes(lineWithCorrectInput, courseId)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithCorrectInput, info));
		
		for (int i = 0; i < invalidInfo.size(); i++) {
			assertEquals(expectedInvalidInfo.get(i), invalidInfo.get(i));
		}
		
	}

	private static StudentAttributes invokeEnrollStudent(StudentAttributes student)
			throws Exception {
		Method privateMethod = StudentsLogic.class.getDeclaredMethod("enrollStudent",
				new Class[] { StudentAttributes.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { student };
		return (StudentAttributes) privateMethod.invoke(StudentsLogic.inst(), params);
	}
	
	@SuppressWarnings("unchecked")
	private static List<String> invokeGetInvalidityInfoInEnrollLines(String lines, String courseID)
			throws Exception {
		Method privateMethod = StudentsLogic.class.getDeclaredMethod("getInvalidityInfoInEnrollLines",
									new Class[] { String.class, String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { lines, courseID };
		return (List<String>) privateMethod.invoke(StudentsLogic.inst(), params);
	}
		
	private void verifyCascasedToSubmissions(String instructorCourse)
			throws EntityDoesNotExistException {
		LogicTest.verifySubmissionsExistForCurrentTeamStructureInAllExistingEvaluations(
				submissionsLogic.getSubmissionsForCourse(instructorCourse),
				instructorCourse);
	}

	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(StudentsLogic.class);
	}

}
