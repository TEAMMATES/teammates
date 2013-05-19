package teammates.test.cases;

import static org.testng.AssertJUnit.*;
import static teammates.common.Common.EOL;
import static teammates.common.FieldValidator.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class StudentAttributesTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
	}

	@Test
	public void testValidate() {
		
		StudentAttributes s = generateValidStudentAttributesObject();
		
		assertEquals("valid value", true, s.isValid());
		
		s.id = "invalid@google@id";
		s.name = "";
		s.email = "invalid email";
		s.course = "";
		s.comments = Common.generateStringOfLength(FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH+1);
		s.team = Common.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH+1);

		assertEquals("invalid value", false, s.isValid());
		String errorMessage = 
				"\"invalid@google@id\" is not acceptable to TEAMMATES as a Google ID because it is not in the correct format. A Google ID must be a valid id already registered with Google. It cannot be longer than 45 characters. It cannot be empty."+EOL
				+"\"\" is not acceptable to TEAMMATES as a Course ID because it is empty. A Course ID can contain letters, numbers, fullstops, hyphens, underscores, and dollar signs. It cannot be longer than 40 characters. It cannot be empty or contain spaces."+EOL
				+"\"invalid email\" is not acceptable to TEAMMATES as an email because it is not in the correct format. An email address contains some text followed by one '@' sign followed by some more text. It cannot be longer than 45 characters. It cannot be empty and it cannot have spaces."+EOL
				+"\"aaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES as a team name because it is too long. The value of a team name should be no longer than 25 characters."+EOL
				+"\""+s.comments+"\" is not acceptable to TEAMMATES as comments about a student enrolled in a course because it is too long. The value of comments about a student enrolled in a course should be no longer than 500 characters."+EOL
				+"\"\" is not acceptable to TEAMMATES as a person name because it is empty. The value of a person name should be no longer than 40 characters. It should not be empty.";
		assertEquals("invalid value", errorMessage, Common.toString(s.getInvalidStateInfo()));
	}

	@Test
	public void testGetValidityInfo(){
	    //already tested in testValidate() above
	}
	
	@Test
	public void testIsValid(){
	    //already tested in testValidate() above
	}
	
	//TODO: unit test StudentAttributes(String enrollLine, String courseId)
	
	@Test
	public void testToString(){
		//TODO:
	}
	
	private StudentAttributes generateValidStudentAttributesObject() {
		StudentAttributes s;
		s = new StudentAttributes();
		s.id = "valid.google.id";
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
