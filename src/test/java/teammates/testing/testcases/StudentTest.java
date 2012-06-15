package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.api.InvalidParametersException;
import teammates.api.TeammatesException;
import teammates.persistent.Student;

public class StudentTest extends BaseTestCase {
	@BeforeClass
	public static void setUp() throws Exception {
		printTestClassHeader();
		turnLogginUp(Student.class);
	}

	@Test
	public void testStudentConstructor() throws TeammatesException {
		printTestCaseHeader();
		// null parameters
		verifyExceptionForStudentCreation("null line", null, "anyCoursId",
				Common.ERRORCODE_NULL_PARAMETER);
		verifyExceptionForStudentCreation("for null courseId", "any line",
				null, Common.ERRORCODE_NULL_PARAMETER);

		// empty line or courseId
		verifyExceptionForStudentCreation("for empty line", "", "anyCoursId",
				Common.ERRORCODE_EMPTY_STRING);
		verifyExceptionForStudentCreation("for empty courseId", "any line", "",
				Common.ERRORCODE_EMPTY_STRING);

		Student expected;

		// normal input, using tab as separator
		expected = generateTypicalStudentObject();
		String enrollmentLine = "team 1\tname 1\temail@email.com\tcomment 1";
		verifyStudentContent(expected, new Student(enrollmentLine, "courseId1"));
		// normal input, using '|' as separator
		enrollmentLine = "team 1|name 1|email@email.com|comment 1";
		verifyStudentContent(expected, new Student(enrollmentLine, "courseId1"));
		// normal input, using both separators
		enrollmentLine = "team 1|name 1\temail@email.com|comment 1";
		verifyStudentContent(expected, new Student(enrollmentLine, "courseId1"));

		// invalid courseId
		verifyExceptionForStudentCreation("invalid coursId [has a space]",
				"team|name|e@e.com|c", "Cours Id with space",
				Common.ERRORCODE_INVALID_CHARS);

		// wrong number of parameters in the line
		verifyExceptionForStudentCreation("only one parameters", "a",
				"anyCoursId", Common.ERRORCODE_INCORRECTLY_FORMATTED_STRING);
		verifyExceptionForStudentCreation("only two parameters", "a|b",
				"anyCoursId", Common.ERRORCODE_INCORRECTLY_FORMATTED_STRING);
		verifyExceptionForStudentCreation("more than 4 parameters",
				"p1|p2|p3|p4|p5", "anyCoursId",
				Common.ERRORCODE_INCORRECTLY_FORMATTED_STRING);

		// empty values for compulsory attributes in the line
		verifyExceptionForStudentCreation("empty name", "t1|  |e@e.com|c",
				"anyCoursId", Common.ERRORCODE_EMPTY_STRING);
		verifyExceptionForStudentCreation("empty email", "t1|n||c",
				"anyCoursId", Common.ERRORCODE_EMPTY_STRING);

		// invalid values for attributes in the line
		String longTeamName = Common
				.generateStringOfLength(Common.TEAM_NAME_MAX_LENGTH + 1);
		verifyExceptionForStudentCreation("invalid team name [too long]",
				longTeamName + "|name|e@e.com|c", "anyCoursId",
				Common.ERRORCODE_STRING_TOO_LONG);
		String longStudentName = Common
				.generateStringOfLength(Common.STUDENT_NAME_MAX_LENGTH + 1);
		verifyExceptionForStudentCreation("invalid student name [too long]",
				"t1|" + longStudentName + "|e@e.com|c", "anyCoursId",
				Common.ERRORCODE_STRING_TOO_LONG);
		verifyExceptionForStudentCreation("invalid email [no '@']",
				"t1|n|ee.com|c", "anyCoursId", Common.ERRORCODE_INVALID_EMAIL);
		String longComment = Common
				.generateStringOfLength(Common.COMMENT_MAX_LENGTH + 1);
		verifyExceptionForStudentCreation("invalid comment [too long]",
				"t|name|e@e.com|" + longComment, "anyCoursId",
				Common.ERRORCODE_STRING_TOO_LONG);

		// Other invalid parameters cases are omitted because they are already
		// unit-tested in validate*() methods in Common.java

		// extra white space
		expected = generateTypicalStudentObject();
		enrollmentLine = "  team 1   |   name 1   |   email@email.com  |  comment 1  ";
		verifyStudentContent(expected, new Student(enrollmentLine, "courseId1"));

		// comment left out
		expected = generateTypicalStudentObject();
		expected.setComments("");
		enrollmentLine = "  team 1   |   name 1   |   email@email.com ";
		verifyStudentContent(expected, new Student(enrollmentLine, "courseId1"));

		// team name left out
		expected = generateTypicalStudentObject();
		expected.setTeamName("");
		enrollmentLine = "|name 1|email@email.com|comment 1";
		verifyStudentContent(expected, new Student(enrollmentLine, "courseId1"));

	}

	private Student generateTypicalStudentObject() {
		Student expected = new Student("email@email.com", "name 1",
				"comment 1", "courseId1", "team 1");
		return expected;
	}

	private void verifyExceptionForStudentCreation(String testCaseDesc,
			String line, String courseId, String errorCode) {
		try {
			new Student(line, courseId);
			Assert.fail("Did not throw exception for " + testCaseDesc);
		} catch (InvalidParametersException e) {
			assertEquals("Wrong error code for " + testCaseDesc, errorCode,
					e.errorCode);
		}
	}

	private void verifyStudentContent(Student expected, Student actual) {
		assertEquals(expected.getTeamName(), actual.getTeamName());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getComments(), actual.getComments());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(Student.class);
	}

}
