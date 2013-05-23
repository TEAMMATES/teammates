package teammates.test.cases.common;

import static org.testng.AssertJUnit.*;
import static teammates.common.Common.EOL;
import static teammates.common.FieldValidator.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.test.cases.BaseTestCase;

public class CourseAttributesTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
	}

	@Test
	public void testValidate() {
		
		CourseAttributes c = generateValidCourseAttributesObject();
		
		assertEquals("valid value", true, c.isValid());
		
		
		String veryLongId = Common.generateStringOfLength(COURSE_ID_MAX_LENGTH+1);
		String emptyName = "";
		c.id = veryLongId;
		c.name = emptyName;
		
		assertEquals("invalid value", false, c.isValid());
		String errorMessage = 
				String.format(COURSE_ID_ERROR_MESSAGE, c.id, REASON_TOO_LONG) + EOL + 
				String.format(COURSE_NAME_ERROR_MESSAGE, c.name, REASON_EMPTY);
		assertEquals("invalid value", errorMessage, Common.toString(c.getInvalidityInfo()));
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
		CourseAttributes c = generateValidCourseAttributesObject();
		assertEquals("valid value", "[CourseAttributes] id: valid-id-$_abc name: valid-name", c.toString());
	}
	
	public static CourseAttributes generateValidCourseAttributesObject() {
		CourseAttributes c;
		c = new CourseAttributes();
		c.id = "valid-id-$_abc";
		c.name = "valid-name";
		return c;
	}

	@AfterClass
	public static void tearDown() {
		printTestClassFooter();
	}

}
