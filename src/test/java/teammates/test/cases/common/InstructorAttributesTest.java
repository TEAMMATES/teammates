package teammates.test.cases.common;

import static org.testng.AssertJUnit.*;
import static teammates.common.util.Const.EOL;
import static teammates.common.util.FieldValidator.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;

public class InstructorAttributesTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
	}

	@Test
	public void testValidate() {
		
		InstructorAttributes i = new InstructorAttributes();
		i.googleId = "valid.google.id";
		i.name = "valid name";
		i.email = "valid@email.com";
		i.courseId = "valid-course-id";
		
		assertEquals("valid value", true, i.isValid());
		
		i.googleId = "invalid@google@id";
		i.name = "";
		i.email = "invalid email";
		i.courseId = "";
		
		assertEquals("invalid value", false, i.isValid());
		String errorMessage = 
				String.format(GOOGLE_ID_ERROR_MESSAGE, i.googleId, REASON_INCORRECT_FORMAT) + EOL 
				+ String.format(COURSE_ID_ERROR_MESSAGE, i.courseId, REASON_EMPTY) + EOL 
				+ String.format(PERSON_NAME_ERROR_MESSAGE, i.name, REASON_EMPTY)+ EOL
				+ String.format(EMAIL_ERROR_MESSAGE, i.email, REASON_INCORRECT_FORMAT);  
		assertEquals("invalid value", errorMessage, StringHelper.toString(i.getInvalidityInfo()));
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
	public void testToString(){
		//TODO:implement this
	}
	
	@AfterClass
	public static void tearDown() {
		printTestClassFooter();
	}

}
