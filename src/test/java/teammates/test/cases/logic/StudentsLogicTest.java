package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertFalse;
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
import teammates.common.datatransfer.StudentEnrollDetails;
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
	
	/*
	 * NOTE: enrollStudents() tested in SubmissionsAdjustmentTest.
	 * This is because it uses Task Queues for scheduling and therefore has to be
	 * tested in a separate class.
	 */
	
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

		StudentAttributes student1 = new StudentAttributes("n|e@g|t1|c", instructorCourse, new Integer[]{2,0,1,3});

		// check if the course is empty
		assertEquals(0, studentsLogic.getStudentsForCourse(instructorCourse).size());

		// add a new student and verify it is added and treated as a new student
		StudentEnrollDetails enrollmentResult = invokeEnrollStudent(student1);
		assertEquals(1, studentsLogic.getStudentsForCourse(instructorCourse).size());
		LogicTest.verifyEnrollmentDetailsForStudent(student1, null, enrollmentResult,
				StudentAttributes.UpdateStatus.NEW);
		LogicTest.verifyPresentInDatastore(student1);

		______TS("add existing student");

		// Verify it was not added
		enrollmentResult = invokeEnrollStudent(student1);
		LogicTest.verifyEnrollmentDetailsForStudent(student1, null, enrollmentResult,
				StudentAttributes.UpdateStatus.UNMODIFIED);
		assertEquals(1, studentsLogic.getStudentsForCourse(instructorCourse).size());

		______TS("add student into non-empty course");
		StudentAttributes student2 = new StudentAttributes("t1|n2|e2@g|c", instructorCourse, null);
		enrollmentResult = invokeEnrollStudent(student2);
		LogicTest.verifyEnrollmentDetailsForStudent(student2, null, enrollmentResult,
				StudentAttributes.UpdateStatus.NEW);
		
		//add some more students to the same course (we add more than one 
		//  because we can use them for testing cascade logic later in this test case)
		invokeEnrollStudent(new StudentAttributes("t2|n3|e3@g|c", instructorCourse, null));
		invokeEnrollStudent(new StudentAttributes("t2|n4|e4@g", instructorCourse, new Integer[]{0,1,2,-1}));
		assertEquals(4, studentsLogic.getStudentsForCourse(instructorCourse).size());
		
		______TS("modify info of existing student");
		//add some more details to the student
		student1.googleId = "googleId";
		studentsLogic.updateStudentCascade(student1.email, student1);
		
		______TS("add evaluation and modify team of existing student" +
				"(to check the cascade logic of the SUT)");
		EvaluationAttributes e = EvaluationsDbTest.generateTypicalEvaluation();
		e.courseId = instructorCourse;
		evaluationsLogic.createEvaluationCascadeWithoutSubmissionQueue(e);

		//add some more details to the student
		String oldTeam = student2.team;
		student2.team = "t2";
		
		//Save student structure before changing of teams
		//This allows for verification of existence of old submissions 
		List<StudentAttributes> studentDetailsBeforeModification = studentsLogic
				.getStudentsForCourse(instructorCourse);
		
		enrollmentResult = invokeEnrollStudent(student2);
		LogicTest.verifyPresentInDatastore(student2);
		LogicTest.verifyEnrollmentDetailsForStudent(student2, oldTeam, enrollmentResult,
				StudentAttributes.UpdateStatus.MODIFIED);
		
		//verify that submissions have not been adjusted
		//i.e no new submissions have been added
		List<SubmissionAttributes> student2Submissions = submissionsLogic
				.getSubmissionsForEvaluation(instructorCourse, e.name);
		
		for (SubmissionAttributes submission : student2Submissions) {
			boolean isStudent2Participant = (submission.reviewee == student2.email) ||
										   (submission.reviewer == student2.email);
			boolean isNewTeam = submission.team == student2.team;
			
			assertFalse(isNewTeam && isStudent2Participant);
		}
		
		//also, verify that the datastore still has the old team structure
		LogicTest.verifySubmissionsExistForCurrentTeamStructureInEvaluation(e.name,
				studentDetailsBeforeModification, submissionsLogic.getSubmissionsForCourse(instructorCourse));

		______TS("error during enrollment");

		StudentAttributes student5 = new StudentAttributes("|n6|e6@g@", instructorCourse, null);
		enrollmentResult = invokeEnrollStudent(student5);
		assertEquals (StudentAttributes.UpdateStatus.ERROR, enrollmentResult.updateStatus);
		assertEquals(4, studentsLogic.getStudentsForCourse(instructorCourse).size());
		
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
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidTeamName, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamName, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidStudentName, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidStudentName, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidEmail, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidEmail, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidStudentNameAndEmail, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidStudentNameAndEmail, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidTeamNameAndEmail, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamNameAndEmail, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidTeamNameAndStudentNameAndEmail, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
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
		info = StringHelper.toString((new StudentAttributes(lineWithTeamNameEmpty, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithTeamNameEmpty, info));
		info = StringHelper.toString((new StudentAttributes(lineWithStudentNameEmpty, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithStudentNameEmpty, info));
		info = StringHelper.toString((new StudentAttributes(lineWithEmailEmpty, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
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
		// not tested as enroll lines must be trimmed before passing to the method
		
		______TS("enrollLines with a mix of all above cases");
		enrollLines = lineWithInvalidTeamName + Const.EOL + lineWithInvalidTeamNameAndStudentNameAndEmail + Const.EOL + lineWithExtraParameters + Const.EOL +
				lineWithTeamNameEmpty + Const.EOL + lineWithCorrectInput + Const.EOL + "\t";

		invalidInfo = invokeGetInvalidityInfoInEnrollLines(enrollLines, courseId);
		
		expectedInvalidInfo.clear();
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidTeamName, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamName, info));
		info = StringHelper.toString((new StudentAttributes(lineWithInvalidTeamNameAndStudentNameAndEmail, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithInvalidTeamNameAndStudentNameAndEmail, info));
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithExtraParameters, StudentAttributes.ERROR_ENROLL_LINE_TOOMANYPARTS));
		info = StringHelper.toString((new StudentAttributes(lineWithTeamNameEmpty, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithTeamNameEmpty, info));
		info = StringHelper.toString((new StudentAttributes(lineWithCorrectInput, courseId, null)).getInvalidityInfo(), "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
		expectedInvalidInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, lineWithCorrectInput, info));
		
		for (int i = 0; i < invalidInfo.size(); i++) {
			assertEquals(expectedInvalidInfo.get(i), invalidInfo.get(i));
		}
		
	}
	
	@Test
	public void testGetColumnOrder() throws Exception {
		
		______TS("typical case: comment column included, with tab as delimiter");
		
		String header = "NAME \t Email \t teams \t Comments";
		Integer[] order = invokeGetColumnOrder(header);
		
		assertEquals(new Integer(2), order[StudentAttributes.ARG_INDEX_TEAM]);
		assertEquals(new Integer(0), order[StudentAttributes.ARG_INDEX_NAME]);
		assertEquals(new Integer(1), order[StudentAttributes.ARG_INDEX_EMAIL]);
		assertEquals(new Integer(3), order[StudentAttributes.ARG_INDEX_COMMENT]);
		
		______TS("typical case: no comment column, with pipe symbol as delimiter");
		
		header = "Email | NAMES | Teams";
		order = invokeGetColumnOrder(header);
		
		assertEquals(new Integer(2), order[StudentAttributes.ARG_INDEX_TEAM]);
		assertEquals(new Integer(1), order[StudentAttributes.ARG_INDEX_NAME]);
		assertEquals(new Integer(0), order[StudentAttributes.ARG_INDEX_EMAIL]);
		assertEquals(new Integer(-1), order[StudentAttributes.ARG_INDEX_COMMENT]);
		
		______TS("failure case: not header row");
		
		header = "name 1 | team 1 | email@email.com";
		order = invokeGetColumnOrder(header);
		
		assertEquals(null, order);
	}

	private static StudentEnrollDetails invokeEnrollStudent(StudentAttributes student)
			throws Exception {
		Method privateMethod = StudentsLogic.class.getDeclaredMethod("enrollStudent",
				new Class[] { StudentAttributes.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { student };
		return (StudentEnrollDetails) privateMethod.invoke(StudentsLogic.inst(), params);
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
	
	private static Integer[] invokeGetColumnOrder(String row)
			throws Exception {
		Method privateMethod = StudentsLogic.class.getDeclaredMethod("getColumnOrder",
				new Class[] { String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { row };
		return (Integer[]) privateMethod.invoke(StudentsLogic.inst(), params);
	}
		
	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(StudentsLogic.class);
	}

}
