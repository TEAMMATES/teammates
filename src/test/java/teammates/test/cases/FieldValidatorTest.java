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
		
		testOnce_PERSON_NAME("valid: typical value",
				"Adam Smith", 
				"");
		
		testOnce_PERSON_NAME("valid: name with allowed symbols",
				"Dr. Amy-B s/o O'br, & 2nd \t (alias 'JB')", 
				"");
		
		String maxLengthName = Common.generateStringOfLength(PERSON_NAME_MAX_LENGTH);
		testOnce_PERSON_NAME("valid: max length value", 
				maxLengthName, 
				"");

		String emptyName = "";
		testOnce_PERSON_NAME("invalid: empty string",
				emptyName, 
				String.format(PERSON_NAME_ERROR_MESSAGE, emptyName,	REASON_EMPTY));
		
		
		String tooLongName = maxLengthName + "x";
		testOnce_PERSON_NAME("invalid: too long",
				tooLongName, 
				String.format(PERSON_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
	}
	
	@Test
	public void testInvalidStateInfo_INSTITUTE_NAME() {
		
		//Testing intensity is less here because the code indirectly executed by 
		// this method is already covered in another test method.

		// test one valid case
		testOnce_INSTITUTE_NAME("valid: typical name",
				"National University of Singapore", 
				"");
		
		//test one invalid case
		String tooLongName = Common.generateStringOfLength(INSTITUTE_NAME_MAX_LENGTH+1);
		testOnce_INSTITUTE_NAME("invalid: too long",
				tooLongName, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
	}
	
	@Test
	public void testInvalidStateInfo_COURSE_NAME() {
		
		//Testing intensity is less here because the code indirectly executed by 
		// this method is already covered in another test method.

		// test one valid case
		testOnce(FieldType.COURSE_NAME, "valid: typical name",
				"Software Engineering - '15 Summer (tutorial)", 
				"");
		
		//test one invalid case
		String tooLongName = Common.generateStringOfLength(COURSE_NAME_MAX_LENGTH+1);
		testOnce(FieldType.COURSE_NAME, "invalid: too long",
				tooLongName, 
				String.format(COURSE_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
	}


	@Test
	public void testInvalidStateInfo_GOOGLE_ID() {
		
		verifyAssertError("null value", FieldType.GOOGLE_ID, null);
		verifyAssertError("white space value", FieldType.GOOGLE_ID, "  \t ");
		verifyAssertError("untrimmed value", FieldType.GOOGLE_ID, "  abc ");
		verifyAssertError("contains '@gmail.com'", FieldType.GOOGLE_ID, "abc@GMAIL.com");
		
		
		testOnce_GOOGLE_ID("valid: typical value",
				"valid9.Goo-gle.id_", 
				"");
		
		testOnce_GOOGLE_ID("valid: minimal non-email id accepted",
				"e", 
				"");
		
		testOnce_GOOGLE_ID("valid: typical email address used as id",
				"someone@yahoo.com", 
				"");
		
		testOnce_GOOGLE_ID("valid: minimal email id accepted as id",
				"e@y", 
				"");
		
		String maxLengthValue = Common.generateStringOfLength(GOOGLE_ID_MAX_LENGTH);
		testOnce_GOOGLE_ID("valid: max length", 
				maxLengthValue, 
				"");

		String emptyValue = "";
		testOnce_GOOGLE_ID("invalid: empty string",
				emptyValue, 
				String.format(GOOGLE_ID_ERROR_MESSAGE, emptyValue,	REASON_EMPTY));
		
		String tooLongValue = maxLengthValue + "x";
		testOnce_GOOGLE_ID("invalid: too long",
				tooLongValue, 
				String.format(GOOGLE_ID_ERROR_MESSAGE, tooLongValue, REASON_TOO_LONG));
		
		String valueWithDisallowedChar = "man woman";
		testOnce_GOOGLE_ID("invalid: disallowed char (space)",
				valueWithDisallowedChar, 
				String.format(GOOGLE_ID_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
		valueWithDisallowedChar = "man/woman";
		testOnce_GOOGLE_ID("invalid: disallowed char",
				valueWithDisallowedChar, 
				String.format(GOOGLE_ID_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
	}
	
	@Test
	public void testInvalidStateInfo_EMAIL() {
		
		verifyAssertError("null value", FieldType.EMAIL, null);
		verifyAssertError("white space value", FieldType.EMAIL, "  \t \n ");
		verifyAssertError("untrimmed value", FieldType.EMAIL, "  abc@gmail.com ");
		
		
		testOnce_EMAIL("valid: typical value",
				"someone@yahoo.com", 
				"");
		
		testOnce_EMAIL("valid: minimal",
				"e@y", 
				"");
		
		String maxLengthValue = Common.generateStringOfLength(EMAIL_MAX_LENGTH-6)+"@c.gov";
		testOnce_EMAIL("valid: max length", 
				maxLengthValue, 
				"");

		String emptyValue = "";
		testOnce_EMAIL("invalid: empty string",
				emptyValue, 
				String.format(EMAIL_ERROR_MESSAGE, emptyValue,	REASON_EMPTY));
		
		String tooLongValue = maxLengthValue + "x";
		testOnce_EMAIL("invalid: too long",
				tooLongValue, 
				String.format(EMAIL_ERROR_MESSAGE, tooLongValue, REASON_TOO_LONG));
		
		String valueWithDisallowedChar = "woMAN@com. sg";
		testOnce_EMAIL("invalid: disallowed char (space) after @",
				valueWithDisallowedChar, 
				String.format(EMAIL_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
		valueWithDisallowedChar = "man woman@com.sg";
		testOnce_EMAIL("invalid: disallowed char (space)",
				valueWithDisallowedChar, 
				String.format(EMAIL_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
		valueWithDisallowedChar = "man@woman@com.lk";
		testOnce_EMAIL("invalid: multiple '@' signs",
				valueWithDisallowedChar, 
				String.format(EMAIL_ERROR_MESSAGE, valueWithDisallowedChar, REASON_INCORRECT_FORMAT));
		
	}
	
	private void testOnce_INSTITUTE_NAME(String description, String name,
			String expected) {
		testOnce(FieldType.INSTITUTE_NAME, description, name, expected);
	}
	
	private void testOnce_PERSON_NAME(String description, String name,
			String expected) {
		testOnce(FieldType.PERSON_NAME, description, name, expected);
	}
	
	private void testOnce_GOOGLE_ID(String description, String id,
			String expected) {
		testOnce(FieldType.GOOGLE_ID, description, id, expected);
	}
	
	private void testOnce_EMAIL(String description, String id,
			String expected) {
		testOnce(FieldType.EMAIL, description, id, expected);
	}

	private void testOnce(FieldType fieldType, String description, String value, String expected) {
		assertEquals(description,expected, 
				validator.getInvalidStateInfo(fieldType, value));
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
