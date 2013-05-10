package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.common.FieldValidator.*;

import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.FieldValidator.FieldType;

public class FieldValidatorTest {
	public FieldValidator validator = new FieldValidator();
	
	@Test
	public void testInvalidStateInfo_PERSON_NAME() {
		
		/* This method is used to cover getInvalidStateInfo_NAME_STRING(String, String, int) 
		which is reused in several other methods. */

		verifyAssertError("null value", FieldType.PERSON_NAME, null);
		verifyAssertError("white space value", FieldType.PERSON_NAME, "  \t ");
		verifyAssertError("untrimmed value", FieldType.PERSON_NAME, "  abc ");
		
		testOnce("valid: typical value", 
				FieldType.PERSON_NAME, 
				"Adam Smith", 
				"");
		
		testOnce("valid: name with allowed symbols", 
				FieldType.PERSON_NAME, 
				"Dr. Amy-B s/o O'br, & 2nd \t (alias 'JB')", 
				"");
		
		String maxLengthName = Common.generateStringOfLength(PERSON_NAME_MAX_LENGTH);
		testOnce("valid: max length value", 
				FieldType.PERSON_NAME, 
				maxLengthName, 
				"");

		String emptyName = "";
		testOnce("invalid: empty string", 
				FieldType.PERSON_NAME, 
				emptyName, 
				String.format(PERSON_NAME_ERROR_MESSAGE, emptyName,	REASON_EMPTY));
		
		
		String tooLongName = maxLengthName + "x";
		testOnce("invalid: too long", 
				FieldType.PERSON_NAME, 
				tooLongName, 
				String.format(PERSON_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
	}
	
	@Test
	public void testInvalidStateInfo_INSTITUTE_NAME() {
		
		//Testing intensity is less here because the code indirectly executed by 
		// this method is already covered in another test method.

		// test one valid case, with field name
		testOnce("valid: typical name", FieldType.INSTITUTE_NAME, 
				"Instructor's institute name",
				"National University of Singapore", 
				"");
		
		//test one invalid case, with field name
		String tooLongName = Common.generateStringOfLength(INSTITUTE_NAME_MAX_LENGTH+1);
		testOnce("invalid: too long", FieldType.INSTITUTE_NAME,
				"Instructor's institute name",
				tooLongName, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
	}
	
	@Test
	public void testInvalidStateInfo_COURSE_NAME() {
		
		//Testing intensity is less here because the code indirectly executed by 
		// this method is already covered in another test method.

		// test one valid case
		testOnce("valid: typical name", 
				FieldType.COURSE_NAME,
				"Software Engineering - '15 Summer (tutorial)", 
				"");
		
		//test one invalid case
		String tooLongName = Common.generateStringOfLength(COURSE_NAME_MAX_LENGTH+1);
		testOnce("invalid: too long", 
				FieldType.COURSE_NAME,
				tooLongName, 
				String.format(COURSE_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
	}
	
	@Test
	public void testInvalidStateInfo_EVALUATION_NAME() {
		
		//Testing intensity is less here because the code indirectly executed by 
		// this method is already covered in another test method.
		
		// test one valid case
		testOnce("valid: typical name", FieldType.EVALUATION_NAME,
				"First Peer Evaluation - 1 (trial)", 
				"");
		
		//test one invalid case
		String tooLongName = Common.generateStringOfLength(EVALUATION_NAME_MAX_LENGTH+1);
		testOnce("invalid: too long", FieldType.EVALUATION_NAME,
				tooLongName, 
				String.format(EVALUATION_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
	}


	@Test
	public void testInvalidStateInfo_GOOGLE_ID() {
		
		verifyAssertError("null value", FieldType.GOOGLE_ID, null);
		verifyAssertError("white space value", FieldType.GOOGLE_ID, "  \t ");
		verifyAssertError("untrimmed value", FieldType.GOOGLE_ID, "  abc ");
		verifyAssertError("contains '@gmail.com'", FieldType.GOOGLE_ID, "abc@GMAIL.com");
		
		
		testOnce("valid: typical value", 
				FieldType.GOOGLE_ID, 
				"valid9.Goo-gle.id_", 
				"");
		
		testOnce("valid: minimal non-email id accepted", 
				FieldType.GOOGLE_ID, 
				"e", 
				"");
		
		testOnce("valid: typical email address used as id", 
				FieldType.GOOGLE_ID, 
				"someone@yahoo.com", "");
		
		testOnce("valid: minimal email id accepted as id", 
				FieldType.GOOGLE_ID, 
				"e@y", 
				"");
		
		String maxLengthValue = Common.generateStringOfLength(GOOGLE_ID_MAX_LENGTH);
		testOnce("valid: max length", 
				FieldType.GOOGLE_ID, 
				maxLengthValue, 
				"");

		String emptyValue = "";
		testOnce("invalid: empty string", 
				FieldType.GOOGLE_ID, 
				emptyValue,
				String.format(GOOGLE_ID_ERROR_MESSAGE, emptyValue,	REASON_EMPTY));
		
		String tooLongValue = maxLengthValue + "x";
		testOnce("invalid: too long", 
				FieldType.GOOGLE_ID, 
				tooLongValue, 
				String.format(GOOGLE_ID_ERROR_MESSAGE, tooLongValue, REASON_TOO_LONG));
		
		String valueWithDisallowedChar = "man woman";
		testOnce("invalid: disallowed char (space)", 
				FieldType.GOOGLE_ID, 
				valueWithDisallowedChar, 
				String.format(GOOGLE_ID_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
		valueWithDisallowedChar = "man/woman";
		testOnce("invalid: disallowed char", 
				FieldType.GOOGLE_ID, 
				valueWithDisallowedChar, 
				String.format(GOOGLE_ID_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
	}
	
	@Test
	public void testInvalidStateInfo_EMAIL() {
		
		verifyAssertError("null value", FieldType.EMAIL, null);
		verifyAssertError("white space value", FieldType.EMAIL, "  \t \n ");
		verifyAssertError("untrimmed value", FieldType.EMAIL, "  abc@gmail.com ");
		
		
		testOnce("valid: typical value", 
				FieldType.EMAIL, 
				"someone@yahoo.com", 
				"");
		
		testOnce("valid: minimal", 
				FieldType.EMAIL, 
				"e@y", 
				"");
		
		String maxLengthValue = Common.generateStringOfLength(EMAIL_MAX_LENGTH-6)+"@c.gov";
		testOnce("valid: max length", 
				FieldType.EMAIL, 
				maxLengthValue, 
				"");

		String emptyValue = "";
		testOnce("invalid: empty string", 
				FieldType.EMAIL, 
				emptyValue, 
				String.format(EMAIL_ERROR_MESSAGE, emptyValue,	REASON_EMPTY));
		
		String tooLongValue = maxLengthValue + "x";
		testOnce("invalid: too long", 
				FieldType.EMAIL, 
				tooLongValue, 
				String.format(EMAIL_ERROR_MESSAGE, tooLongValue, REASON_TOO_LONG));
		
		String valueWithDisallowedChar = "woMAN@com. sg";
		testOnce("invalid: disallowed char (space) after @", 
				FieldType.EMAIL, 
				valueWithDisallowedChar, 
				String.format(EMAIL_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
		valueWithDisallowedChar = "man woman@com.sg";
		testOnce("invalid: disallowed char (space)", 
				FieldType.EMAIL, 
				valueWithDisallowedChar, 
				String.format(EMAIL_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
		valueWithDisallowedChar = "man@woman@com.lk";
		testOnce("invalid: multiple '@' signs", 
				FieldType.EMAIL, 
				valueWithDisallowedChar, 
				String.format(EMAIL_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
	}
	
	@Test
	public void testInvalidStateInfo_COURSE_ID() {
		
		verifyAssertError("null value", FieldType.COURSE_ID, null);
		verifyAssertError("white space value", FieldType.COURSE_ID, "  \t \n ");
		verifyAssertError("untrimmed value", FieldType.COURSE_ID, "  abc@gmail.com ");
		
		
		testOnce("valid: typical value", 
				FieldType.COURSE_ID, 
				"$cs1101-sem1.2_", 
				"");
		
		testOnce("valid: minimal", 
				FieldType.COURSE_ID, 
				"c", 
				"");
		
		String maxLengthValue = Common.generateStringOfLength(COURSE_ID_MAX_LENGTH);
		testOnce("valid: max length", 
				FieldType.COURSE_ID, 
				maxLengthValue, 
				"");

		String emptyValue = "";
		testOnce("invalid: empty string", 
				FieldType.COURSE_ID, 
				emptyValue, 
				String.format(COURSE_ID_ERROR_MESSAGE, emptyValue,	REASON_EMPTY));
		
		String tooLongValue = maxLengthValue + "x";
		testOnce("invalid: too long", 
				FieldType.COURSE_ID, 
				tooLongValue, 
				String.format(COURSE_ID_ERROR_MESSAGE, tooLongValue, REASON_TOO_LONG));
		
		String valueWithDisallowedChar = "my course id";
		testOnce("invalid: disallowed char (space)", 
				FieldType.COURSE_ID, 
				valueWithDisallowedChar, 
				String.format(COURSE_ID_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
		valueWithDisallowedChar = "cour@s*hy#";
		testOnce("invalid: multiple disallowed chars", 
				FieldType.COURSE_ID, 
				valueWithDisallowedChar, 
				String.format(COURSE_ID_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
	}
	
	private void testOnce(String description, FieldType fieldType, String value, String expected) {
		assertEquals(description,expected, 
				validator.getInvalidStateInfo(fieldType, value));
	}
	
	private void testOnce(String description, FieldType fieldType, String fieldName, String value, String expected) {
		if(!fieldName.isEmpty() && !expected.isEmpty()){
			expected = "Invalid "+ fieldName + ": " + expected;
		}
		assertEquals(description,expected, 
				validator.getInvalidStateInfo(fieldType, fieldName, value));
	}

	private void verifyAssertError(String description, FieldType fieldType, String value) {
		String errorMessage = "Did not throw the expected AssertionError for "+ description;
		try {
			validator.getInvalidStateInfo(fieldType, value);
			throw new RuntimeException(errorMessage);
		} catch (AssertionError e) {
			assertTrue(true); //expected exception
		}
	}
	

}
