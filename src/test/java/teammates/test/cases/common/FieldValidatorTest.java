package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static teammates.common.util.FieldValidator.*;


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.FieldValidator.FieldType;
import teammates.test.cases.BaseTestCase;

public class FieldValidatorTest extends BaseTestCase{
	public FieldValidator validator = new FieldValidator();
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
	}
	
	@Test
	public void testGetValidityInfoForSizeCappedNonEmptyString() {
		
		String typicalFieldName = "my field";
		int typicalLength = 25;
		
		try {
			validator.getValidityInfoForSizeCappedNonEmptyString(typicalFieldName, typicalLength, null);
			signalFailureToDetectException("not expected to be null");
		} catch (AssertionError e) {
			ignoreExpectedException(); 
		}
		
		try {
			validator.getValidityInfoForSizeCappedNonEmptyString(typicalFieldName, typicalLength, " abc ");
			signalFailureToDetectException("not expected to be untrimmed");
		} catch (AssertionError e) {
			ignoreExpectedException();
		}
		
		int maxLength = 50;
		assertEquals("valid: typical value", 
				"",
				validator.getValidityInfoForSizeCappedNonEmptyString(
						typicalFieldName, 
						maxLength, 
						"Dr. Amy-B s/o O'br, & 2nd \t \n (alias 'JB')"));
		
		assertEquals("valid: max length", 
				"",
				validator.getValidityInfoForSizeCappedNonEmptyString(
						typicalFieldName, 
						maxLength, 
						StringHelper.generateStringOfLength(maxLength)));
		
		String tooLongName = StringHelper.generateStringOfLength(maxLength+1);
		assertEquals("invalid: too long", 
				String.format(
						SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, 
						tooLongName, typicalFieldName,  REASON_TOO_LONG, typicalFieldName, maxLength),
				validator.getValidityInfoForSizeCappedNonEmptyString(
						typicalFieldName, 
						maxLength, 
						tooLongName));
		
		
		String emptyValue = "";
		assertEquals("invalid: empty", 
				String.format(
						SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, 
						emptyValue, typicalFieldName,  REASON_EMPTY, typicalFieldName, maxLength),
				validator.getValidityInfoForSizeCappedNonEmptyString(
						typicalFieldName, 
						maxLength, 
						emptyValue));
	}
	
	@Test
	public void testGetValidityInfoForSizeCappedPossiblyEmptyString() {
		
		String typicalFieldName = "my field";
		int typicalLength = 25;
		
		try {
			validator.getValidityInfoForSizeCappedNonEmptyString(typicalFieldName, typicalLength, null);
			signalFailureToDetectException("not expected to be null");
		} catch (AssertionError e) {
			ignoreExpectedException(); 
		}
		
		try {
			validator.getValidityInfoForSizeCappedNonEmptyString(typicalFieldName, typicalLength, " abc ");
			signalFailureToDetectException("not expected to be untrimmed");
		} catch (AssertionError e) {
			ignoreExpectedException();
		}
		
		int maxLength = 50;
		assertEquals("valid: typical value", 
				"",
				validator.getValidityInfoForSizeCappedPossiblyEmptyString(
						typicalFieldName, 
						maxLength, 
						"Dr. Amy-B s/o O'br, & 2nd \t \n (alias 'JB')"));
		
		assertEquals("valid: max length", 
				"",
				validator.getValidityInfoForSizeCappedPossiblyEmptyString(
						typicalFieldName, 
						maxLength, 
						StringHelper.generateStringOfLength(maxLength)));
		
		
		String emptyValue = "";
		assertEquals("valid: empty", 
				"",
				validator.getValidityInfoForSizeCappedPossiblyEmptyString(
						typicalFieldName, 
						maxLength, 
						emptyValue));
		
		String tooLongName = StringHelper.generateStringOfLength(maxLength+1);
		assertEquals("invalid: too long", 
				String.format(
						SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, 
						tooLongName, typicalFieldName,  REASON_TOO_LONG, typicalFieldName, maxLength),
				validator.getValidityInfoForSizeCappedPossiblyEmptyString(
						typicalFieldName, 
						maxLength, 
						tooLongName));
	}

	@Test
	public void testGetValidityInfo_PERSON_NAME() {
		
		//NOTE 1: The SUT's work is done mostly in testGetValidityInfoForSizeCappedNonEmptyString
		//  or testGetValidityInfoForSizeCappedPossiblyEmptyString methods
		//  which are already unit tested. Therefore, this method checks if the 
		//  max length and the field name are handled correctly by SUT.
		
		runGenericTestCasesForCappedSizeStringTypeField(
				FieldType.PERSON_NAME, 
				PERSON_NAME_MAX_LENGTH, 
				PERSON_NAME_ERROR_MESSAGE, 
				false);
	}
	
	@Test
	public void testGetValidityInfo_INSTITUTE_NAME() {
		
		//See NOTE 1.
		runGenericTestCasesForCappedSizeStringTypeField(
				FieldType.INSTITUTE_NAME, 
				INSTITUTE_NAME_MAX_LENGTH, 
				INSTITUTE_NAME_ERROR_MESSAGE, 
				false);
	}
	
	@Test
	public void testGetValidityInfo_COURSE_NAME() {
		
		//See NOTE 1.
		runGenericTestCasesForCappedSizeStringTypeField(
				FieldType.COURSE_NAME, 
				COURSE_NAME_MAX_LENGTH, 
				COURSE_NAME_ERROR_MESSAGE, 
				false);
	}
	
	@Test
	public void testGetValidityInfo_TEAM_NAME() {
		
		//See NOTE 1.
		runGenericTestCasesForCappedSizeStringTypeField(
				FieldType.TEAM_NAME, 
				TEAM_NAME_MAX_LENGTH, 
				TEAM_NAME_ERROR_MESSAGE, 
				false);
		
	}

	@Test
	public void testGetValidityInfo_EVALUATION_NAME() {
		
		//See NOTE 1.
		runGenericTestCasesForCappedSizeStringTypeField(
				FieldType.EVALUATION_NAME, 
				EVALUATION_NAME_MAX_LENGTH, 
				EVALUATION_NAME_ERROR_MESSAGE, 
				false);
	}
	
	@Test
	public void testGetValidityInfo_STUDENT_ROLE_COMMENTS() {
		
		//See NOTE 1.
		runGenericTestCasesForCappedSizeStringTypeField(
				FieldType.STUDENT_ROLE_COMMENTS, 
				STUDENT_ROLE_COMMENTS_MAX_LENGTH, 
				STUDENT_ROLE_COMMENTS_ERROR_MESSAGE, 
				true);
	}


	@Test
	public void testGetValidityInfo_GOOGLE_ID() {
		
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
		
		String maxLengthValue = StringHelper.generateStringOfLength(GOOGLE_ID_MAX_LENGTH);
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
	public void testGetValidityInfo_EMAIL() {
		
		verifyAssertError("null value", FieldType.EMAIL, null);
		verifyAssertError("white space value", FieldType.EMAIL, "  \t \n ");
		verifyAssertError("untrimmed value", FieldType.EMAIL, "  abc@gmail.com ");
		
		
		testOnce("valid: typical value, without field name", 
				FieldType.EMAIL, 
				"someone@yahoo.com", 
				"");
		
		testOnce("valid: typical value, with field name", 
				FieldType.EMAIL, 
				"student email",
				"someone@yahoo.com", 
				"");
		
		testOnce("valid: minimal", 
				FieldType.EMAIL, 
				"e@y", 
				"");
		
		String maxLengthValue = StringHelper.generateStringOfLength(EMAIL_MAX_LENGTH-6)+"@c.gov";
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
	public void testGetValidityInfo_COURSE_ID() {
		
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
		
		String maxLengthValue = StringHelper.generateStringOfLength(COURSE_ID_MAX_LENGTH);
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
	
	
	private void runGenericTestCasesForCappedSizeStringTypeField(
			FieldType fieldType, 
			int maxSize, 
			String errorMessageFormat, 
			boolean emptyStringAllowed) {
		
		String maxLengthValue = StringHelper.generateStringOfLength(maxSize);
		testOnce("valid: max length value", 
				fieldType, 
				maxLengthValue, 
				"");
		
		String tooLongValue = maxLengthValue + "x";
		testOnce("invalid: too long value, without fieldName parameter", 
				fieldType, 
				tooLongValue, 
				String.format(errorMessageFormat, tooLongValue, REASON_TOO_LONG));
		
		String emptyValue = "";
		testOnce("invalid: empty value, *with* fieldName parameter", 
				fieldType,
				"course name of the student",
				emptyValue, 
				emptyStringAllowed? "" : String.format(errorMessageFormat, emptyValue, REASON_EMPTY));
	}

	private void testOnce(String description, FieldType fieldType, String value, String expected) {
		assertEquals(description,expected, 
				validator.getInvalidityInfo(fieldType, value));
	}
	
	private void testOnce(String description, FieldType fieldType, String fieldName, String value, String expected) {
		if(!fieldName.isEmpty() && !expected.isEmpty()){
			expected = "Invalid "+ fieldName + ": " + expected;
		}
		assertEquals(description,expected, 
				validator.getInvalidityInfo(fieldType, fieldName, value));
	}

	private void verifyAssertError(String description, FieldType fieldType, String value) {
		String errorMessage = "Did not throw the expected AssertionError for "+ description;
		try {
			validator.getInvalidityInfo(fieldType, value);
			signalFailureToDetectException(errorMessage);
		} catch (AssertionError e) {
			ignoreExpectedException();
		}
	}
	
	@AfterClass
	public static void tearDown() {
		printTestClassFooter();
	}

}
