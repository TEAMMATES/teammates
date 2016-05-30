package teammates.test.cases.common;

// CHECKSTYLE.OFF:AvoidStarImport as we want to perform tests on everything from FieldValidator
import static teammates.common.util.FieldValidator.*;
// CHECKSTYLE.ON:AvoidStarImport

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;

public class FieldValidatorTest extends BaseTestCase {
    public FieldValidator validator = new FieldValidator();
    
    @BeforeClass
    public static void setupClass() {
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
        
        String tooLongName = StringHelper.generateStringOfLength(maxLength + 1);
        assertEquals("invalid: too long",
                String.format(
                        SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                        tooLongName, typicalFieldName, REASON_TOO_LONG, typicalFieldName, maxLength),
                validator.getValidityInfoForSizeCappedNonEmptyString(
                        typicalFieldName,
                        maxLength,
                        tooLongName));
        
        
        String emptyValue = "";
        assertEquals("invalid: empty",
                String.format(
                        SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                        emptyValue, typicalFieldName, REASON_EMPTY, typicalFieldName, maxLength),
                validator.getValidityInfoForSizeCappedNonEmptyString(
                        typicalFieldName,
                        maxLength,
                        emptyValue));
        
        String untrimmedValue = " abc ";
        assertEquals("invalid: untrimmed",
                String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, typicalFieldName),
                validator.getValidityInfoForSizeCappedNonEmptyString(
                        typicalFieldName,
                        maxLength,
                        untrimmedValue));
    }

    @Test
    public void testGetValidityInfoForNonHtmlField_cleanInput_returnEmptyString() {
        String clean = "Valid clean input with no special HTML characters";
        String testFieldName = "Inconsequential test field name";
        String actual = validator.getValidityInfoForNonHtmlField(testFieldName, clean);
        assertEquals("Valid clean input with no special HTML characters should return empty string", "",
                     actual);
    }

    @Test
    public void testGetValidityInfoForNonHtmlField_sanitizedInput_returnEmptyString() {
        String sanitizedInput = "Valid sanitized input &lt; &gt; &quot; &#x2f; &#39; &amp;";
        String testFieldName = "Inconsequential test field name";
        String actual = validator.getValidityInfoForNonHtmlField(testFieldName, sanitizedInput);
        assertEquals("Valid sanitized input should return empty string", "", actual);
    }
    
    @Test
    public void testGetValidityInfoForNonHtmlField_unsanitizedInput_returnErrorString() {
        String unsanitizedInput = "Invalid unsanitized input <>\\/'&";
        String testFieldName = "Inconsequential test field name";
        String actual = validator.getValidityInfoForNonHtmlField(testFieldName, unsanitizedInput);
        assertEquals("Invalid unsanitized input should return error string",
                     String.format(NON_HTML_FIELD_ERROR_MESSAGE, testFieldName), actual);
    }

    public void testGetValidityInfoForSizeCappedPossiblyEmptyString() {
        
        String typicalFieldName = "my field";
        int typicalLength = 25;
        
        try {
            validator.getValidityInfoForSizeCappedNonEmptyString(typicalFieldName, typicalLength, null);
            signalFailureToDetectException("not expected to be null");
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
        
        String untrimmedValue = " abc ";
        assertEquals("invalid: untrimmed",
                String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, typicalFieldName),
                validator.getValidityInfoForSizeCappedPossiblyEmptyString(
                        typicalFieldName,
                        maxLength,
                        untrimmedValue));
        
        String tooLongName = StringHelper.generateStringOfLength(maxLength + 1);
        assertEquals("invalid: too long",
                String.format(
                        SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE,
                        tooLongName, typicalFieldName, REASON_TOO_LONG, typicalFieldName, maxLength),
                validator.getValidityInfoForSizeCappedPossiblyEmptyString(
                        typicalFieldName,
                        maxLength,
                        tooLongName));
    }
    
    @Test
    public void testGetValidityInfoForAllowedName() {
        
        ______TS("null value");
        
        String typicalFieldName = "name field";
        int typicalLength = 25;
        
        try {
            validator.getValidityInfoForAllowedName(typicalFieldName, typicalLength, null);
            signalFailureToDetectException("not expected to be null");
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
        
        ______TS("typical success case");
        
        int maxLength = 50;
        assertEquals("valid: typical length with valid characters",
                "",
                validator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        "Ýàn-B. s/o O'br, &2\t\n(~!@#$^*+_={}[]\\:;\"<>?)"));
        
        ______TS("failure: invalid characters");
        
        String nameContainInvalidChars = "Dr. Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals("invalid: typical length with invalid characters",
                     String.format(INVALID_NAME_ERROR_MESSAGE, Sanitizer.sanitizeForHtml(nameContainInvalidChars),
                                   typicalFieldName, REASON_CONTAINS_INVALID_CHAR, typicalFieldName),
                     validator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameContainInvalidChars));
        
        ______TS("failure: starts with non-alphanumeric character");
        
        String nameStartedWithNonAlphaNumChar = "!Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals("invalid: typical length with invalid characters",
                     String.format(INVALID_NAME_ERROR_MESSAGE,
                                   Sanitizer.sanitizeForHtml(nameStartedWithNonAlphaNumChar),
                                   typicalFieldName, REASON_START_WITH_NON_ALPHANUMERIC_CHAR, typicalFieldName),
                     validator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameStartedWithNonAlphaNumChar));
        
        ______TS("failure: starts with curly braces but contains invalid char");
        
        String nameStartedWithBracesButHasInvalidChar = "{Amy} -Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals("invalid: typical length with invalid characters",
                     String.format(INVALID_NAME_ERROR_MESSAGE,
                                   Sanitizer.sanitizeForHtml(nameStartedWithBracesButHasInvalidChar),
                                   typicalFieldName, REASON_CONTAINS_INVALID_CHAR, typicalFieldName),
                     validator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameStartedWithBracesButHasInvalidChar));
        
        ______TS("failure: starts with opening curly bracket but dose not have closing bracket");
        
        String nameStartedWithCurlyBracketButHasNoEnd = "{Amy -Bén s/o O'&|% 2\t\n (~!@#$^*+_={[]\\:;\"<>?)";
        assertEquals("invalid: typical length started with non-alphanumeric character",
                     String.format(INVALID_NAME_ERROR_MESSAGE,
                                   Sanitizer.sanitizeForHtml(nameStartedWithCurlyBracketButHasNoEnd),
                                   typicalFieldName, REASON_START_WITH_NON_ALPHANUMERIC_CHAR, typicalFieldName),
                     validator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameStartedWithCurlyBracketButHasNoEnd));
        
        ______TS("success: with opening and closing curly braces");
        
        assertEquals("valid: max length",
                "",
                validator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        "{last name} first name"));
        
        ______TS("success: max length");
        
        assertEquals("valid: max length",
                "",
                validator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        StringHelper.generateStringOfLength(maxLength)));
        
        ______TS("failure: too long");
        
        String tooLongName = StringHelper.generateStringOfLength(maxLength + 1);
        assertEquals("invalid: too long",
                String.format(
                        SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                        tooLongName, typicalFieldName, REASON_TOO_LONG, typicalFieldName, maxLength),
                validator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        tooLongName));
        
        ______TS("failure: empty string");
        
        String emptyValue = "";
        assertEquals("invalid: empty",
                String.format(
                        SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                        emptyValue, typicalFieldName, REASON_EMPTY, typicalFieldName, maxLength),
                validator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        emptyValue));
        
        ______TS("failure: untrimmed value");
        
        String untrimmedValue = " abc ";
        assertEquals("invalid: untrimmed",
                String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, typicalFieldName),
                validator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        untrimmedValue));
    }

    @Test
    public void testGetInvalidityInfoForPersonName_invalid_returnSpecificErrorString() {
        String invalidPersonName = "";
        String actual = validator.getInvalidityInfoForPersonName(invalidPersonName);
        assertEquals("Invalid person name (empty) should return error message that is specific to person name",
                     String.format(PERSON_NAME_ERROR_MESSAGE, invalidPersonName, REASON_EMPTY), actual);
    }

    @Test
    public void testGetInvalidityInfoForInstituteName_invalid_returnSpecificErrorString() {
        String invalidInstituteName = StringHelper.generateStringOfLength(INSTITUTE_NAME_MAX_LENGTH + 1);
        String actual = validator.getInvalidityInfoForInstituteName(invalidInstituteName);
        assertEquals("Invalid institute name (too long) should return error message that is specific to institute name",
                     String.format(INSTITUTE_NAME_ERROR_MESSAGE, invalidInstituteName, REASON_TOO_LONG),
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForNationality_invalid_returnSpecificErrorString() {
        String invalidNationality = "{ Invalid Char Nationality";
        String actual = validator.getInvalidityInfoForNationality(invalidNationality);
        assertEquals("Invalid nationality (invalid char) should return error string that is specific to nationality",
                      String.format(INVALID_NAME_ERROR_MESSAGE, invalidNationality, NATIONALITY_FIELD_NAME,
                                    REASON_START_WITH_NON_ALPHANUMERIC_CHAR, NATIONALITY_FIELD_NAME),
                      actual);
    }

    @Test
    public void testGetInvalidityInfoForTeamName_invalid_returnSpecificErrorString() {
        String invalidTeamName = "";
        String actual = validator.getInvalidityInfoForTeamName(invalidTeamName);
        assertEquals("Invalid team name (empty) should return error message that is specific to team name",
                     String.format(TEAM_NAME_ERROR_MESSAGE, invalidTeamName, REASON_EMPTY), actual);
    }

    @Test
    public void testGetInvalidityInfoForSectionName_invalid_returnSpecificErrorString() {
        String invalidSectionName = "Percent Symbol % Section";
        String actual = validator.getInvalidityInfoForSectionName(invalidSectionName);
        assertEquals("Invalid section name (invalid char) should return error string that is specific to section name",
                     String.format(INVALID_NAME_ERROR_MESSAGE, invalidSectionName, SECTION_NAME_FIELD_NAME,
                                   REASON_CONTAINS_INVALID_CHAR, SECTION_NAME_FIELD_NAME),
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForCourseName_invalid_returnSpecificErrorString() {
        String invalidCourseName = "Vertical Bar | Course";
        String actual = validator.getInvalidityInfoForCourseName(invalidCourseName);
        assertEquals("Invalid course name (invalid char) should return error string that is specific to course name",
                     String.format(INVALID_NAME_ERROR_MESSAGE, invalidCourseName, COURSE_NAME_FIELD_NAME,
                                   REASON_CONTAINS_INVALID_CHAR, COURSE_NAME_FIELD_NAME),
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForFeedbackSessionName_invalid_returnSpecificErrorString() {
        String invalidSessionName = StringHelper.generateStringOfLength(FEEDBACK_SESSION_NAME_MAX_LENGTH + 1);
        String actual = validator.getInvalidityInfoForFeedbackSessionName(invalidSessionName);
        assertEquals("Invalid feedback session name (too long) should return error message specfic to feedback session name",
                     String.format(FEEDBACK_SESSION_NAME_ERROR_MESSAGE, invalidSessionName,
                                   REASON_TOO_LONG),
                     actual);
    }

    @Test
    public void invalidityInfoFor_validGender_returnEmptyString() {
        String validGender = "other";
        String actual = validator.getInvalidityInfoForGender(validGender);
        assertEquals("Valid gender should return empty string", "", actual);
    }

    @Test
    public void invalidityInfoFor_invalidGender_returnErrorString() {
        String invalidGender = "alpha male";
        String actual = validator.getInvalidityInfoForGender(invalidGender);
        assertEquals("Invalid gender should return appropriate error stirng",
                     String.format(GENDER_ERROR_MESSAGE, invalidGender),
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_null_throwException() {
        String errorMessageForNullGoogleId = "Did not throw the expected AssertionError for null value";
        try {
            validator.getInvalidityInfoForGoogleId(null);
            signalFailureToDetectException(errorMessageForNullGoogleId);
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_untrimmedGmailDomain_throwException() {
        String errorMessageForUntrimmedEmailDomain = "Did not throw the expected AssertionError for Google ID "
                                                     + "with untrimmed GMail domain (i.e., @gmail.com)";
        try {
            validator.getInvalidityInfoForGoogleId("abc@GMAIL.com");
            signalFailureToDetectException(errorMessageForUntrimmedEmailDomain);
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_valid_returnEmptyString() {
        String typicalId = "valid9.Goo-gle.id_";
        assertEquals("Valid Google ID (typical) should return empty string", "",
                     validator.getInvalidityInfoForGoogleId(typicalId));

        String shortId = "e";
        assertEquals("Valid Google ID (short) should return empty string", "",
                     validator.getInvalidityInfoForGoogleId(shortId));

        String emailAsId = "someone@yahoo.com";
        assertEquals("Valid Google ID (typical email) should return empty string", "",
                     validator.getInvalidityInfoForGoogleId(emailAsId));
    
        String shortEmailAsId = "e@y";
        assertEquals("Valid Google ID (short email) should return empty string", "",
                     validator.getInvalidityInfoForGoogleId(shortEmailAsId));

        String maxLengthId = StringHelper.generateStringOfLength(GOOGLE_ID_MAX_LENGTH);
        assertEquals("Valid Google ID (max length) should return empty string", "",
                     validator.getInvalidityInfoForGoogleId(maxLengthId));
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_invalid_returnErrorString() {
        String emptyId = "";
        assertEquals("Invalid Google ID (empty) should return appropriate error message",
                     validator.getInvalidityInfoForGoogleId(emptyId),
                     String.format(GOOGLE_ID_ERROR_MESSAGE, emptyId, REASON_EMPTY));

        String whitespaceId = "     ";
        assertEquals("Invalid Google ID (contains whitespaces only) should return appropriate error message",
                     validator.getInvalidityInfoForGoogleId(whitespaceId),
                     String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, GOOGLE_ID_FIELD_NAME));

        String untrimmedId = "  googleIdWithSpacesAround    ";
        assertEquals("Invalid Google ID (leading/trailing whitespaces) should return appropriate error message",
                     validator.getInvalidityInfoForGoogleId(untrimmedId),
                     String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, GOOGLE_ID_FIELD_NAME));

        String tooLongId = StringHelper.generateStringOfLength(GOOGLE_ID_MAX_LENGTH + 1);
        assertEquals("Invalid Google ID (too long) should return appropriate error message",
                     validator.getInvalidityInfoForGoogleId(tooLongId),
                     String.format(GOOGLE_ID_ERROR_MESSAGE, tooLongId, REASON_TOO_LONG));

        String idWithSpaces = "invalid google id with spaces";
        assertEquals("Invalid Google ID (with spaces) should return appropriate error message",
                     validator.getInvalidityInfoForGoogleId(idWithSpaces),
                     String.format(GOOGLE_ID_ERROR_MESSAGE, idWithSpaces, REASON_INCORRECT_FORMAT));

        String idWithInvalidHtmlChar = "invalid google id with HTML/< special characters";
        assertEquals("Invalid Google ID (contains HTML characters) should return appropriate error message",
                     validator.getInvalidityInfoForGoogleId(idWithInvalidHtmlChar),
                     String.format(GOOGLE_ID_ERROR_MESSAGE, Sanitizer.sanitizeForHtml(idWithInvalidHtmlChar),
                                   REASON_INCORRECT_FORMAT));
    }

    @Test
    public void testGetValidityInfoInstructorRole() {
        
        verifyAssertError("not null", FieldType.INTRUCTOR_ROLE, null);
        
        testOnce("typical case", FieldType.INTRUCTOR_ROLE, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER, "");
        testOnce("typical case", FieldType.INTRUCTOR_ROLE, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER, "");
        testOnce("typical case", FieldType.INTRUCTOR_ROLE, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR, "");
        testOnce("typical case", FieldType.INTRUCTOR_ROLE, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER, "");
        testOnce("typical case", FieldType.INTRUCTOR_ROLE, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM, "");
        String emptyValue = "";
        testOnce("empty value", FieldType.INTRUCTOR_ROLE, emptyValue, String.format(INSTRUCTOR_ROLE_ERROR_MESSAGE, emptyValue, REASON_EMPTY));
        String invalidValue = "invalid value";
        testOnce(invalidValue, FieldType.INTRUCTOR_ROLE, invalidValue, String.format(INSTRUCTOR_ROLE_ERROR_MESSAGE,
                invalidValue, INSTRUCTOR_ROLE_ERROR_REASON_NOT_MATCHING));
    }
    
    @Test
    public void testGetInvalidityInfoForEmail_null_throwException() {
        String errorMessage = "Did not throw the expected AssertionError for null email";
        try {
            validator.getInvalidityInfoForEmail(null);
            signalFailureToDetectException(errorMessage);
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
    }

    @Test
    public void testGetInvalidityInfoForEmail_valid_returnEmptyString() {
        String typicalEmail = "someone@yahoo.com";
        assertEquals("Valid email (typical) should return empty string", "",
                     validator.getInvalidityInfoForEmail(typicalEmail));

        String shortEmail = "e@y";
        assertEquals("Valid email (short) should return empty string", "",
                     validator.getInvalidityInfoForEmail(shortEmail));

        String maxLengthEmail = StringHelper.generateStringOfLength(EMAIL_MAX_LENGTH - 6) + "@c.gov";
        assertEquals("Valid email (max-length) should return empty string", "",
                     validator.getInvalidityInfoForEmail(maxLengthEmail));
    }

    @Test
    public void testGetInvalidityInfoForEmail_invalid_returnErrorString() {
        String emptyEmail = "";
        assertEquals("Invalid email (empty) should return appropriate error string",
                     String.format(EMAIL_ERROR_MESSAGE, emptyEmail, REASON_EMPTY),
                     validator.getInvalidityInfoForEmail(emptyEmail));

        String untrimmedEmail = "  untrimmed@email.com  ";
        assertEquals("Invalid email (leading/trailing spaces) should return appropriate error string",
                     String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, EMAIL_FIELD_NAME),
                     validator.getInvalidityInfoForEmail(untrimmedEmail));

        String whitespaceEmail = "    ";
        assertEquals("Invalid email (only whitespaces) should return appropriate error string",
                     String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, EMAIL_FIELD_NAME),
                     validator.getInvalidityInfoForEmail(whitespaceEmail));

        String tooLongEmail = StringHelper.generateStringOfLength(EMAIL_MAX_LENGTH + 1) + "@c.gov";
        assertEquals("Invalid email (leading/trailing spaces) should return appropriate error string",
                     String.format(EMAIL_ERROR_MESSAGE, tooLongEmail, REASON_TOO_LONG),
                     validator.getInvalidityInfoForEmail(tooLongEmail));

        String emailWithSpaceAfterAtSymbol = "woMAN@com. sg";
        assertEquals("Invalid email (space character after '@') should return appropriate error string",
                     String.format(EMAIL_ERROR_MESSAGE, emailWithSpaceAfterAtSymbol, REASON_INCORRECT_FORMAT),
                     validator.getInvalidityInfoForEmail(emailWithSpaceAfterAtSymbol));

        String emailWithSpaceBeforeAtSymbol = "man woman@com.sg";
        assertEquals("Invalid email (space character before '@') should return appropriate error string",
                     String.format(EMAIL_ERROR_MESSAGE, emailWithSpaceBeforeAtSymbol, REASON_INCORRECT_FORMAT),
                     validator.getInvalidityInfoForEmail(emailWithSpaceBeforeAtSymbol));

        String emailWithMultipleAtSymbol = "man@woman@com.lk";
        assertEquals("Invalid email (multiple '@' characters) should return appropriate error string",
                     String.format(EMAIL_ERROR_MESSAGE, emailWithMultipleAtSymbol, REASON_INCORRECT_FORMAT),
                     validator.getInvalidityInfoForEmail(emailWithMultipleAtSymbol));
    }

    @Test
    public void testGetValidityInfoCourseId() {
        
        verifyAssertError("null value", FieldType.COURSE_ID, null);
        
        
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
                String.format(COURSE_ID_ERROR_MESSAGE, emptyValue, REASON_EMPTY));
        
        String untrimmedValue = " $cs1101-sem1.2_ ";
        testOnce("invalid: untrimmed",
                FieldType.COURSE_ID,
                untrimmedValue,
                String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, "course ID"));
        
        String whitespaceOnlyValue = "    ";
        testOnce("invalid: whitespace only",
                FieldType.COURSE_ID,
                whitespaceOnlyValue,
                String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, "course ID"));
        
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
    
    @Test
    public void testRegexName() {
        ______TS("success: typical name");
        String name = "Benny Charlés";
        assertTrue(StringHelper.isMatching(name, REGEX_NAME));
        
        ______TS("success: name begins with accented characters");
        name = "Ýàn-B. s/o O'br, &2(~!@#$^*+_={}[]\\:;\"<>?)";
        assertTrue(StringHelper.isMatching(name, REGEX_NAME));
        
        ______TS("failure: name begins with non-alphanumeric character");
        name = "~Amy-Ben. s/o O'br, &2(~!@#$^*+_={}[]\\:;\"<>?)";
        assertFalse(StringHelper.isMatching(name, REGEX_NAME));
        
        ______TS("failure: name contains invalid character");
        name = "Amy-B. s/o O'br, %|&2(~!@#$^*+_={}[]\\:;\"<>?)";
        assertFalse(StringHelper.isMatching(name, REGEX_NAME));
    }
    
    @Test
    public void testRegexEmail() {
        ______TS("success: typical email");
        String email = "john@email.com";
        assertTrue(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("success: minimum allowed email format");
        email = "a@e";
        assertTrue(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("success: all allowed special characters");
        email = "a!#$%&'*/=?^_`{}~@e";
        assertTrue(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("failure: invalid starting character");
        email = "$john@email.com";
        assertFalse(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("failure: two consecutive dots in local part");
        email = "john..dot@email.com";
        assertFalse(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("failure: invalid characters in domain part");
        email = "john@e&email.com";
        assertFalse(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("failure: invalid ending character in domain part");
        email = "john@email.com3";
        assertFalse(StringHelper.isMatching(email, REGEX_EMAIL));
    }
    
    @Test
    public void testRegexCourseId() {
        ______TS("success: typical course ID");
        String courseId = "CS101";
        assertTrue(StringHelper.isMatching(courseId, REGEX_COURSE_ID));
        
        ______TS("success: course ID with all accepted symbols");
        courseId = "CS101-B.$";
        assertTrue(StringHelper.isMatching(courseId, REGEX_COURSE_ID));
        
        ______TS("failure: contains invalid character");
        courseId = "CS101+B";
        assertFalse(StringHelper.isMatching(courseId, REGEX_COURSE_ID));
    }
    
    @Test
    public void testRegexSampleCourseId() {
        ______TS("success: typical sample course ID");
        String courseId = "CS101-demo3";
        assertTrue(StringHelper.isMatching(courseId, REGEX_SAMPLE_COURSE_ID));
        
        ______TS("failure: non-demo course ID");
        courseId = "CS101";
        assertFalse(StringHelper.isMatching(courseId, REGEX_SAMPLE_COURSE_ID));
    }
    
    @Test
    public void testRegexGoogleIdNonEmail() {
        ______TS("success: typical google id");
        String googleId = "teammates.instr";
        assertTrue(StringHelper.isMatching(googleId, REGEX_GOOGLE_ID_NON_EMAIL));
        
        ______TS("success: google id with all accepted characters");
        googleId = "teammates.new_instr-3";
        assertTrue(StringHelper.isMatching(googleId, REGEX_GOOGLE_ID_NON_EMAIL));
        
        ______TS("failure: is email");
        googleId = "teammates.instr@email.com";
        assertFalse(StringHelper.isMatching(googleId, REGEX_GOOGLE_ID_NON_EMAIL));
        
        ______TS("failure: contains invalid character");
        googleId = "teammates.$instr";
        assertFalse(StringHelper.isMatching(googleId, REGEX_GOOGLE_ID_NON_EMAIL));
    }

    private void testOnce(String description, FieldType fieldType, String value, String expected) {
        assertEquals(description, expected,
                validator.getInvalidityInfo(fieldType, value));
    }

    private void verifyAssertError(String description, FieldType fieldType, String value) {
        String errorMessage = "Did not throw the expected AssertionError for " + description;
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
