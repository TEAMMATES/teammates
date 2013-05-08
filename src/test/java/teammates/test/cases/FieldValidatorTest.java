package teammates.test.cases;

import static org.testng.AssertJUnit.*;
import org.testng.annotations.*;
import static teammates.common.FieldValidator.*;

import teammates.common.Common;
import teammates.common.FieldValidator;

public class FieldValidatorTest {
	public FieldValidator validator = new FieldValidator();
	
	@Test
	public void testInvalidStateInfo_PERSON_NAME() {

		testOnce_PERSON_NAME("valid: typical name",
				"Adam Smith", 
				"");
		
		testOnce_PERSON_NAME("valid: name with allowed symbols",
				"Dr. Amy-B s/o O'brien, 2nd (alias 'JB')", 
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
		testOnce_PERSON_NAME("invalid: unacceptable symbols",
				nameWithOneInvalidChar, 
				String.format(PERSON_NAME_ERROR_MESSAGE, nameWithOneInvalidChar, REASON_DISALLOWED_CHAR));
		
		String nameWithManyInvalidChars = " _adam$ * [first year]";
		testOnce_PERSON_NAME("invalid: unacceptable symbols",
				nameWithManyInvalidChars, 
				String.format(PERSON_NAME_ERROR_MESSAGE, nameWithManyInvalidChars, REASON_DISALLOWED_CHAR));
	}
	
	
	private void testOnce_PERSON_NAME(String description, String name,
			String expected) {
		assertEquals(description, 
				expected, 
				validator.getInvalidStateInfo(FieldType.PERSON_NAME, name));
	}
	

}
