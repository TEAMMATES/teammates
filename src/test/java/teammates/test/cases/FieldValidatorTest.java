package teammates.test.cases;

import static org.testng.AssertJUnit.*;
import org.testng.annotations.*;
import static teammates.common.FieldValidator.*;

import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.FieldValidator.FieldType;

public class FieldValidatorTest {
	public FieldValidator validator = new FieldValidator();
	
	@Test
	public void testInvalidStateInfo_PERSON_NAME() {

		testOnce_PERSON_NAME("valid: typical name",
				"Adam Smith", 
				"");
		
		testOnce_PERSON_NAME("valid: name with allowed symbols",
				"Dr. Amy-B s/o O'br, & 2nd \t (alias 'JB')", 
				"");
		
		String maxLengthName = Common.generateStringOfLength(PERSON_NAME_MAX_LENGTH);
		testOnce_PERSON_NAME("valid: max length name", 
				maxLengthName, 
				"");

		testOnce_PERSON_NAME("valid: max length name with whitespace padding", 
				" \t  " + maxLengthName + "  ", 
				"");

		String emptyName = "";
		testOnce_PERSON_NAME("invalid: empty string",
				emptyName, 
				String.format(PERSON_NAME_ERROR_MESSAGE, emptyName,	REASON_EMPTY));
		
		String whiteSpaceName = "  \t ";
		testOnce_PERSON_NAME("invalid: white space only",
				whiteSpaceName, 
				String.format(PERSON_NAME_ERROR_MESSAGE, whiteSpaceName,REASON_EMPTY));
		
		String tooLongName = maxLengthName + "x";
		testOnce_PERSON_NAME("invalid: too long",
				tooLongName, 
				String.format(PERSON_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
		String nameWithOneInvalidChar = " adam * ";
		testOnce_PERSON_NAME("invalid: disallowed symbol",
				nameWithOneInvalidChar, 
				String.format(PERSON_NAME_ERROR_MESSAGE, nameWithOneInvalidChar, REASON_DISALLOWED_CHAR));
		
		String nameWithManyInvalidChars = " _adam$ * [first year]";
		testOnce_PERSON_NAME("invalid: many disallowed symbols",
				nameWithManyInvalidChars, 
				String.format(PERSON_NAME_ERROR_MESSAGE, nameWithManyInvalidChars, REASON_DISALLOWED_CHAR));
	}
	
	@Test
	public void testInvalidStateInfo_INSTITUTE_NAME() {
		testOnce_INSTITUTE_NAME("valid: typical name",
				"National University of Singapore", 
				"");
		
		testOnce_INSTITUTE_NAME("valid: typical name with allowed symbols",
				"3rd Nat. & Univer-sity/city, (S'pore)", 
				"");
		
		String maxLengthName = Common.generateStringOfLength(INSTITUTE_NAME_MAX_LENGTH);
		testOnce_INSTITUTE_NAME("valid: max length name", 
				maxLengthName, 
				"");

		testOnce_INSTITUTE_NAME("valid: max length name with whitespace padding", 
				" \t  " + maxLengthName + "  ", 
				"");
		
		String emptyName = "";
		testOnce_INSTITUTE_NAME("invalid: empty string",
				emptyName, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, emptyName,	REASON_EMPTY));
		
		String whiteSpaceName = "  \t ";
		testOnce_INSTITUTE_NAME("invalid: white space only",
				whiteSpaceName, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, whiteSpaceName,REASON_EMPTY));
		
		String tooLongName = maxLengthName + "x";
		testOnce_INSTITUTE_NAME("invalid: too long",
				tooLongName, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
		String nameWithOneInvalidChar = " ^ uni ";
		testOnce_INSTITUTE_NAME("invalid: one disallowed symbol",
				nameWithOneInvalidChar, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, nameWithOneInvalidChar, REASON_DISALLOWED_CHAR));
		
		String nameWithManyInvalidChars = " _uni$ * #private @city";
		testOnce_INSTITUTE_NAME("invalid: many disallowed symbols",
				nameWithManyInvalidChars, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, nameWithManyInvalidChars, REASON_DISALLOWED_CHAR));
	}
	
	
	private void testOnce_INSTITUTE_NAME(String description, String name,
			String expected) {
		testOnce(FieldType.INSTITUTE_NAME, description, name, expected);
	}
	
	private void testOnce_PERSON_NAME(String description, String name,
			String expected) {
		testOnce(FieldType.PERSON_NAME, description, name, expected);
	}

	private void testOnce(FieldType fieldType, String description, String name, String expected) {
		assertEquals(description,expected, 
				validator.getInvalidStateInfo(fieldType, name));
	}
	

}
