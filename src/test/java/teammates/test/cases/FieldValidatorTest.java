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

		verifyAssertError("null value", FieldType.PERSON_NAME, null);
		verifyAssertError("white space value", FieldType.PERSON_NAME, "  \t ");
		verifyAssertError("untrimmed value", FieldType.PERSON_NAME, "  abc ");
		
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

		String emptyName = "";
		testOnce_PERSON_NAME("invalid: empty string",
				emptyName, 
				String.format(PERSON_NAME_ERROR_MESSAGE, emptyName,	REASON_EMPTY));
		
		
		String tooLongName = maxLengthName + "x";
		testOnce_PERSON_NAME("invalid: too long",
				tooLongName, 
				String.format(PERSON_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
		String nameWithOneInvalidChar = "adam *";
		testOnce_PERSON_NAME("invalid: disallowed symbol",
				nameWithOneInvalidChar, 
				String.format(PERSON_NAME_ERROR_MESSAGE, nameWithOneInvalidChar, REASON_DISALLOWED_CHAR));
		
		String nameWithManyInvalidChars = "_adam$ * [first year]";
		testOnce_PERSON_NAME("invalid: many disallowed symbols",
				nameWithManyInvalidChars, 
				String.format(PERSON_NAME_ERROR_MESSAGE, nameWithManyInvalidChars, REASON_DISALLOWED_CHAR));
	}
	
	@Test
	public void testInvalidStateInfo_INSTITUTE_NAME() {
		
		verifyAssertError("null value", FieldType.INSTITUTE_NAME, null);
		verifyAssertError("white space value", FieldType.INSTITUTE_NAME, "  \t ");
		verifyAssertError("untrimmed value", FieldType.INSTITUTE_NAME, "  abc ");
		
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

		String emptyName = "";
		testOnce_INSTITUTE_NAME("invalid: empty string",
				emptyName, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, emptyName,	REASON_EMPTY));
		
		String tooLongName = maxLengthName + "x";
		testOnce_INSTITUTE_NAME("invalid: too long",
				tooLongName, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, tooLongName, REASON_TOO_LONG));
		
		String nameWithOneInvalidChar = "^ uni";
		testOnce_INSTITUTE_NAME("invalid: one disallowed symbol",
				nameWithOneInvalidChar, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, nameWithOneInvalidChar, REASON_DISALLOWED_CHAR));
		
		String nameWithManyInvalidChars = "_uni$ * #private @city";
		testOnce_INSTITUTE_NAME("invalid: many disallowed symbols",
				nameWithManyInvalidChars, 
				String.format(INSTITUTE_NAME_ERROR_MESSAGE, nameWithManyInvalidChars, REASON_DISALLOWED_CHAR));
	}

	@Test
	public void testInvalidStateInfo_GOOGLE_ID() {
		
		verifyAssertError("null value", FieldType.GOOGLE_ID, null);
		verifyAssertError("white space value", FieldType.GOOGLE_ID, "  \t ");
		verifyAssertError("untrimmed value", FieldType.GOOGLE_ID, "  abc ");
		verifyAssertError("contains '@gmail.com'", FieldType.GOOGLE_ID, "abc@GMAIL.com");
		
		
		testOnce_GOOGLE_ID("valid: typical id",
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
				String.format(GOOGLE_ID_ERROR_MESSAGE, valueWithDisallowedChar, REASON_DISALLOWED_CHAR));
		
		valueWithDisallowedChar = "man/woman";
		testOnce_GOOGLE_ID("invalid: disallowed char",
				valueWithDisallowedChar, 
				String.format(GOOGLE_ID_ERROR_MESSAGE, valueWithDisallowedChar, REASON_DISALLOWED_CHAR));
		
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
