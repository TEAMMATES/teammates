package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.common.util.Const.EOL;
import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_EMPTY;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;
import static teammates.common.util.FieldValidator.REASON_TOO_LONG;
import static teammates.common.util.FieldValidator.STUDENT_ROLE_COMMENTS_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.TEAM_NAME_ERROR_MESSAGE;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Student;
import teammates.test.cases.BaseTestCase;

public class StudentAttributesTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
	}

	@Test
	public void testStudentConstructor() throws TeammatesException {
		String courseId = "anyCoursId";
		StudentAttributes invalidStudent;
		
		Student expected;
		StudentAttributes studentUnderTest;
		
		// FAIL : empty courseId
		invalidStudent = new StudentAttributes("team", "name", "e@e.com", "c", "");
		assertFalse(invalidStudent.isValid());
		assertEquals(
				String.format(COURSE_ID_ERROR_MESSAGE, invalidStudent.course, REASON_EMPTY), 
				invalidStudent.getInvalidityInfo().get(0));
	
		// FAIL : invalid courseId (contains space)
		invalidStudent = new StudentAttributes("team", "name", "e@e.com", "c", "Course Id with space");
		assertFalse(invalidStudent.isValid());
		assertEquals(
				String.format(COURSE_ID_ERROR_MESSAGE, invalidStudent.course, REASON_INCORRECT_FORMAT),
				invalidStudent.getInvalidityInfo().get(0));	
	
		// FAIL : empty name
		invalidStudent = new StudentAttributes("t1", "", "e@e.com", "c", courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(invalidStudent.getInvalidityInfo().get(0), 
				String.format(FieldValidator.PERSON_NAME_ERROR_MESSAGE, "",	FieldValidator.REASON_EMPTY));
		
		// FAIL : empty email
		invalidStudent = new StudentAttributes("t1", "n", "", "c", courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals( 
				String.format(EMAIL_ERROR_MESSAGE, "", REASON_EMPTY), 
				invalidStudent.getInvalidityInfo().get(0));
	
		// FAIL : team name too long
		String longTeamName = StringHelper.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);
		invalidStudent = new StudentAttributes(longTeamName, "name", "e@e.com", "c", courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(
				String.format(TEAM_NAME_ERROR_MESSAGE, longTeamName, REASON_TOO_LONG),
				invalidStudent.getInvalidityInfo().get(0));
		
		// FAIL : student name too long
		String longStudentName = StringHelper.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
		invalidStudent = new StudentAttributes("t1", longStudentName, "e@e.com", "c", courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(
				String.format(FieldValidator.PERSON_NAME_ERROR_MESSAGE, longStudentName,	FieldValidator.REASON_TOO_LONG),
				invalidStudent.getInvalidityInfo().get(0));
		
		// FAIL : invalid email
		invalidStudent = new StudentAttributes("t1", "name", "ee.com", "c", courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(
				String.format(EMAIL_ERROR_MESSAGE, "ee.com", REASON_INCORRECT_FORMAT), 
				invalidStudent.getInvalidityInfo().get(0));
		
		// FAIL : comment too long
		String longComment = StringHelper.generateStringOfLength(FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH + 1);
		invalidStudent = new StudentAttributes("t1", "name", "e@e.com", longComment, courseId);
		assertFalse(invalidStudent.isValid());
		assertEquals(
				String.format(STUDENT_ROLE_COMMENTS_ERROR_MESSAGE, longComment, REASON_TOO_LONG),
				invalidStudent.getInvalidityInfo().get(0));
		
		// Other invalid parameters cases are omitted because they are already
		// unit-tested in validate*() methods in Common.java
	
		// extra white space
		expected = generateTypicalStudentObject();
		studentUnderTest = new StudentAttributes("  team 1   ", "   name 1   ", "   email@email.com  ", "  comment 1  ", "courseId1");
		verifyStudentContent(expected, studentUnderTest.toEntity());
	
	}

	@Test
	public void testValidate() {
		
		StudentAttributes s = generateValidStudentAttributesObject();
		
		assertEquals("valid value", true, s.isValid());
		
		s.googleId = "invalid@google@id";
		s.name = "";
		s.email = "invalid email";
		s.course = "";
		s.comments = StringHelper.generateStringOfLength(FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH+1);
		s.team = StringHelper.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH+1);

		assertEquals("invalid value", false, s.isValid());
		String errorMessage = 
				"\"invalid@google@id\" is not acceptable to TEAMMATES as a Google ID because it is not in the correct format. A Google ID must be a valid id already registered with Google. It cannot be longer than 45 characters. It cannot be empty."+EOL
				+"\"\" is not acceptable to TEAMMATES as a Course ID because it is empty. A Course ID can contain letters, numbers, fullstops, hyphens, underscores, and dollar signs. It cannot be longer than 40 characters. It cannot be empty or contain spaces."+EOL
				+"\"invalid email\" is not acceptable to TEAMMATES as an email because it is not in the correct format. An email address contains some text followed by one '@' sign followed by some more text. It cannot be longer than 45 characters. It cannot be empty and it cannot have spaces."+EOL
				+"\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES as a team name because it is too long. The value of a team name should be no longer than 60 characters. It should not be empty."+EOL
				+"\""+s.comments+"\" is not acceptable to TEAMMATES as comments about a student enrolled in a course because it is too long. The value of comments about a student enrolled in a course should be no longer than 500 characters."+EOL
				+"\"\" is not acceptable to TEAMMATES as a person name because it is empty. The value of a person name should be no longer than 40 characters. It should not be empty.";
		assertEquals("invalid value", errorMessage, StringHelper.toString(s.getInvalidityInfo()));
	}

	@Test
	public void testGetValidityInfo(){
	    //already tested in testValidate() above
	}
	
	@Test
	public void testIsValid(){
	    //already tested in testValidate() above
	}
	
	
	@Test 
	public void testIsRegistered() throws Exception{
		StudentAttributes sd = new StudentAttributes("team 1", "name 1", "email@email.com", "comment 1", "course1");
		Student studentUnderTest = sd.toEntity();
		
		// Id is not given yet
		assertFalse(studentUnderTest.isRegistered());
		
		// Id given
		studentUnderTest.setGoogleId("name1");
		assertTrue(studentUnderTest.isRegistered());
	}
	
	@Test
	public void testToString(){
		//TODO: implement this method
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

	
	private StudentAttributes generateValidStudentAttributesObject() {
		StudentAttributes s;
		s = new StudentAttributes();
		s.googleId = "valid.google.id";
		s.name = "valid name";
		s.email = "valid@email.com";
		s.course = "valid-course-id";
		s.comments = "";
		s.team = "valid team";
		return s;
	}	

	@AfterClass
	public static void tearDown() {
		printTestClassFooter();
	}

}
