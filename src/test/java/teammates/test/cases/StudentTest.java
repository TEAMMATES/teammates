package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.storage.entity.Student;

public class StudentTest extends BaseTestCase {

	@BeforeClass
	public static void setUp() throws Exception {
		printTestClassHeader();
	}

	@Test
	public void testStudentConstructor() throws TeammatesException {
		String line;
		String courseId = "anyCoursId";
		StudentData invalidStudent;
		
		Student expected;
		StudentData studentUnderTest;
		
		// SUCCESS : normal input, using tab as separator
		expected = generateTypicalStudentObject();
		String enrollmentLine = "team 1\tname 1\temail@email.com\tcomment 1";
		studentUnderTest = new StudentData(enrollmentLine, courseId);
		verifyStudentContent(expected, studentUnderTest.toEntity());
		
		// SUCCESS : normal input, using '|' as separator
		enrollmentLine = "team 1|name 1|email@email.com|comment 1";
		studentUnderTest = new StudentData(enrollmentLine, courseId);
		verifyStudentContent(expected, studentUnderTest.toEntity());
		
		// SUCCESS : normal input, using both separators
		enrollmentLine = "team 1|name 1\temail@email.com|comment 1";
		studentUnderTest = new StudentData(enrollmentLine, courseId);
		verifyStudentContent(expected, studentUnderTest.toEntity());
		
		// FAIL : courseId is null
		line = "team|name|e@e.com|c";
		invalidStudent = new StudentData(line, null);
		assertFalse(invalidStudent.isValid());
		assertEquals(invalidStudent.getInvalidStateInfo(), StudentData.ERROR_FIELD_COURSE);
		
		// FAIL : empty courseId
		invalidStudent = new StudentData(line, "");
		assertFalse(invalidStudent.isValid());
		assertEquals(invalidStudent.getInvalidStateInfo(), StudentData.ERROR_FIELD_COURSE);

		// FAIL : invalid courseId
		invalidStudent = new StudentData(line, "Course Id with space");
		assertFalse(invalidStudent.isValid());
		assertEquals(invalidStudent.getInvalidStateInfo(), StudentData.ERROR_FIELD_COURSE);
		
		// FAIL : enroll line is null
		line = null;
		try {
			invalidStudent = new StudentData(line, courseId);
			Assert.fail();
		} catch (AssertionError ae) {
			assertEquals(ae.getMessage(), StudentData.ERROR_ENROLL_LINE_NULL);
		}
		
		// FAIL : enroll line is empty
		line = "";
		try {
			invalidStudent = new StudentData(line, courseId);
			Assert.fail();
		} catch (InvalidParametersException ipe) {
			assertEquals(ipe.getMessage(), StudentData.ERROR_ENROLL_LINE_EMPTY);
		}
		
		// FAIL : too few inputs in enroll line
		line = "a";
		try {
			invalidStudent = new StudentData(line, courseId);
			Assert.fail();
		} catch (InvalidParametersException ipe) {
			assertEquals(ipe.getMessage(), StudentData.ERROR_ENROLL_LINE_TOOFEWPARTS);
		}
		
		// FAIL : too few inputs in enroll line
		line = "a|b";
		try {
			invalidStudent = new StudentData(line, courseId);
			Assert.fail();
		} catch (InvalidParametersException ipe) {
			assertEquals(ipe.getMessage(), StudentData.ERROR_ENROLL_LINE_TOOFEWPARTS);
		}
		
		// FAIL : too many inputs in enroll line
		line = "p1|p2|p3|p4|p5";
		try {
			invalidStudent = new StudentData(line, courseId);
			Assert.fail();
		} catch (InvalidParametersException ipe) {
			assertEquals(ipe.getMessage(), StudentData.ERROR_ENROLL_LINE_TOOMANYPARTS);
		}

		// FAIL : empty name
		line = "t1| |e@e.com|c";
		invalidStudent = new StudentData(line, courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(invalidStudent.getInvalidStateInfo(), 
				String.format(FieldValidator.PERSON_NAME_ERROR_MESSAGE, "",	FieldValidator.REASON_EMPTY));
		
		// FAIL : empty email
		line = "t1|n||c";
		invalidStudent = new StudentData(line, courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(invalidStudent.getInvalidStateInfo(), StudentData.ERROR_FIELD_EMAIL);

		// FAIL : team name too long
		String longTeamName = Common.generateStringOfLength(StudentData.TEAM_NAME_MAX_LENGTH + 1);
		line = longTeamName + "|name|e@e.com|c";
		invalidStudent = new StudentData(line, courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(invalidStudent.getInvalidStateInfo(), StudentData.ERROR_TEAMNAME_TOOLONG);
		
		// FAIL : student name too long
		String longStudentName = Common.generateStringOfLength(StudentData.STUDENT_NAME_MAX_LENGTH + 1);
		line = "t1|" + longStudentName + "|e@e.com|c";
		invalidStudent = new StudentData(line, courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(invalidStudent.getInvalidStateInfo(), 
				String.format(FieldValidator.PERSON_NAME_ERROR_MESSAGE, longStudentName,	FieldValidator.REASON_TOO_LONG));
		
		// FAIL : invalid email
		line = "t1|name|ee.com|c";
		invalidStudent = new StudentData(line, courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(invalidStudent.getInvalidStateInfo(), StudentData.ERROR_FIELD_EMAIL);
		
		// FAIL : comment too long
		String longComment = Common.generateStringOfLength(StudentData.COMMENTS_MAX_LENGTH + 1);
		line = "t1|name|e@e.com|" + longComment;
		invalidStudent = new StudentData(line, courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(invalidStudent.getInvalidStateInfo(), StudentData.ERROR_COMMENTS_TOOLONG);

		// Other invalid parameters cases are omitted because they are already
		// unit-tested in validate*() methods in Common.java

		// extra white space
		expected = generateTypicalStudentObject();
		enrollmentLine = "  team 1   |   name 1   |   email@email.com  |  comment 1  ";
		studentUnderTest = new StudentData(enrollmentLine, "courseId1");
		verifyStudentContent(expected, studentUnderTest.toEntity());

		// comment left out
		expected = generateTypicalStudentObject();
		expected.setComments("");
		enrollmentLine = "  team 1   |   name 1   |   email@email.com ";
		studentUnderTest = new StudentData(enrollmentLine, "courseId1");
		verifyStudentContent(expected, studentUnderTest.toEntity());

		// team name left out
		expected = generateTypicalStudentObject();
		expected.setTeamName("");
		enrollmentLine = "|name 1|email@email.com|comment 1";
		studentUnderTest = new StudentData(enrollmentLine, "courseId1");
		verifyStudentContent(expected, studentUnderTest.toEntity());

	}
	
	@Test 
	public void testIsRegistered() throws Exception{
		StudentData sd = new StudentData("team 1|name 1|email@email.com|comment 1", "course1");
		Student studentUnderTest = sd.toEntity();
		
		// Id is not given yet
		assertFalse(studentUnderTest.isRegistered());
		
		// Id given
		studentUnderTest.setGoogleId("name1");
		assertTrue(studentUnderTest.isRegistered());
	}
	
	private Student generateTypicalStudentObject() {
		Student expected = new Student("email@email.com", "name 1",
				"googleId.1", "comment 1", "courseId1", "team 1");
		return expected;
	}

	private void verifyStudentContent(Student expected, Student actual) {
		assertEquals(expected.getTeamName(), actual.getTeamName());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getComments(), actual.getComments());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

}
