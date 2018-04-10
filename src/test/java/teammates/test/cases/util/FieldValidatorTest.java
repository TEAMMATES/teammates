package teammates.test.cases.util;

// CHECKSTYLE.OFF:AvoidStarImport as we want to perform tests on everything from FieldValidator
import static teammates.common.util.FieldValidator.*;
//CHECKSTYLE.ON:AvoidStarImport

import java.time.Instant;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.FieldValidatorExtension;
import teammates.test.driver.StringHelperExtension;
import teammates.test.driver.TimeHelperExtension;

/**
 * SUT: {@link FieldValidator}.
 */
public class FieldValidatorTest extends BaseTestCase {

    private FieldValidator validator = new FieldValidator();

    @Test
    public void testGetValidityInfoForSizeCappedNonEmptyString() {

        String typicalFieldName = "my field";
        int typicalLength = 25;

        try {
            FieldValidatorExtension.getValidityInfoForSizeCappedNonEmptyString(typicalFieldName,
                    typicalLength, null);
            signalFailureToDetectException("not expected to be null");
        } catch (AssertionError e) {
            ignoreExpectedException();
        }

        int maxLength = 50;
        assertEquals("valid: typical value",
                "",
                FieldValidatorExtension.getValidityInfoForSizeCappedNonEmptyString(
                        typicalFieldName,
                        maxLength,
                        "Dr. Amy-B s/o O'br, & 2nd \t \n (alias 'JB')"));

        assertEquals("valid: max length",
                "",
                FieldValidatorExtension.getValidityInfoForSizeCappedNonEmptyString(
                        typicalFieldName,
                        maxLength,
                        StringHelperExtension.generateStringOfLength(maxLength)));

        String tooLongName = StringHelperExtension.generateStringOfLength(maxLength + 1);
        assertEquals("invalid: too long",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES "
                         + "as a/an my field because it is too long. The value of a/an my field should be no "
                         + "longer than 50 characters. It should not be empty.",
                     FieldValidatorExtension.getValidityInfoForSizeCappedNonEmptyString(typicalFieldName,
                             maxLength, tooLongName));

        String emptyValue = "";
        assertEquals("invalid: empty",
                     "The field 'my field' is empty. The value of a/an my field should be no longer "
                         + "than 50 characters. It should not be empty.",
                     FieldValidatorExtension.getValidityInfoForSizeCappedNonEmptyString(typicalFieldName,
                             maxLength, emptyValue));

        String untrimmedValue = " abc ";
        assertEquals("invalid: untrimmed",
                     "The provided my field is not acceptable to TEAMMATES as it contains only whitespace or "
                         + "contains extra spaces at the beginning or at the end of the text.",
                     FieldValidatorExtension.getValidityInfoForSizeCappedNonEmptyString(typicalFieldName,
                             maxLength, untrimmedValue));
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
        String unsanitizedInput = "Invalid unsanitized input <>\"/'&";
        String testFieldName = "Inconsequential test field name";
        String actual = validator.getValidityInfoForNonHtmlField(testFieldName, unsanitizedInput);
        assertEquals("Invalid unsanitized input should return error string",
                     "The provided Inconsequential test field name is not acceptable to TEAMMATES as it "
                         + "cannot contain the following special html characters in brackets: (&lt; &gt; &quot; "
                         + "&#x2f; &#39; &amp;)",
                     actual);
    }

    @Test
    public void testGetValidityInfoForSizeCappedPossiblyEmptyString() {

        String typicalFieldName = "my field";
        int typicalLength = 25;

        try {
            FieldValidatorExtension.getValidityInfoForSizeCappedNonEmptyString(typicalFieldName,
                    typicalLength, null);
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
                        StringHelperExtension.generateStringOfLength(maxLength)));

        String emptyValue = "";
        assertEquals("valid: empty",
                "",
                validator.getValidityInfoForSizeCappedPossiblyEmptyString(
                        typicalFieldName,
                        maxLength,
                        emptyValue));

        String untrimmedValue = " abc ";
        assertEquals("invalid: untrimmed",
                     "The provided my field is not acceptable to TEAMMATES as it contains only whitespace or "
                         + "contains extra spaces at the beginning or at the end of the text.",
                     validator.getValidityInfoForSizeCappedPossiblyEmptyString(typicalFieldName, maxLength,
                                                                               untrimmedValue));

        String tooLongName = StringHelperExtension.generateStringOfLength(maxLength + 1);
        assertEquals("invalid: too long",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES "
                         + "as a/an my field because it is too long. The value of a/an my field should be no "
                         + "longer than 50 characters.",
                     validator.getValidityInfoForSizeCappedPossiblyEmptyString(typicalFieldName, maxLength,
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
                     "\"Dr. Amy-Bén s&#x2f;o O&#39;&amp;|% 2\t\n (~!@#$^*+_={}[]\\:;&quot;&lt;&gt;?)\" is "
                         + "not acceptable to TEAMMATES as a/an name field because it contains invalid "
                         + "characters. A/An name field must start with an alphanumeric character, and cannot "
                         + "contain any vertical bar (|) or percent sign (%).",
                     validator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameContainInvalidChars));

        ______TS("failure: starts with non-alphanumeric character");

        String nameStartedWithNonAlphaNumChar = "!Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals("invalid: typical length with invalid characters",
                     "\"!Amy-Bén s&#x2f;o O&#39;&amp;|% 2\t\n (~!@#$^*+_={}[]\\:;&quot;&lt;&gt;?)\" is not "
                         + "acceptable to TEAMMATES as a/an name field because it starts with a "
                         + "non-alphanumeric character. A/An name field must start with an alphanumeric "
                         + "character, and cannot contain any vertical bar (|) or percent sign (%).",
                     validator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameStartedWithNonAlphaNumChar));

        ______TS("failure: starts with curly braces but contains invalid char");

        String nameStartedWithBracesButHasInvalidChar = "{Amy} -Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals("invalid: typical length with invalid characters",
                     "\"{Amy} -Bén s&#x2f;o O&#39;&amp;|% 2\t\n (~!@#$^*+_={}[]\\:;&quot;&lt;&gt;?)\" is not "
                         + "acceptable to TEAMMATES as a/an name field because it contains invalid "
                         + "characters. A/An name field must start with an alphanumeric character, and cannot "
                         + "contain any vertical bar (|) or percent sign (%).",
                     validator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameStartedWithBracesButHasInvalidChar));

        ______TS("failure: starts with opening curly bracket but dose not have closing bracket");

        String nameStartedWithCurlyBracketButHasNoEnd = "{Amy -Bén s/o O'&|% 2\t\n (~!@#$^*+_={[]\\:;\"<>?)";
        assertEquals("invalid: typical length started with non-alphanumeric character",
                     "\"{Amy -Bén s&#x2f;o O&#39;&amp;|% 2\t\n (~!@#$^*+_={[]\\:;&quot;&lt;&gt;?)\" is not "
                         + "acceptable to TEAMMATES as a/an name field because it starts with a "
                         + "non-alphanumeric character. A/An name field must start with an alphanumeric "
                         + "character, and cannot contain any vertical bar (|) or percent sign (%).",
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
                        StringHelperExtension.generateStringOfLength(maxLength)));

        ______TS("failure: too long");

        String tooLongName = StringHelperExtension.generateStringOfLength(maxLength + 1);
        assertEquals("invalid: too long",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES "
                         + "as a/an name field because it is too long. The value of a/an name field should "
                         + "be no longer than 50 characters. It should not be empty.",
                     validator.getValidityInfoForAllowedName(typicalFieldName, maxLength, tooLongName));

        ______TS("failure: empty string");

        String emptyValue = "";
        assertEquals("invalid: empty",
                     "The field 'name field' is empty. The value of a/an name field should be no longer "
                         + "than 50 characters. It should not be empty.",
                     validator.getValidityInfoForAllowedName(typicalFieldName, maxLength, emptyValue));

        ______TS("failure: untrimmed value");

        String untrimmedValue = " abc ";
        assertEquals("invalid: untrimmed",
                     "The provided name field is not acceptable to TEAMMATES as it contains only whitespace "
                         + "or contains extra spaces at the beginning or at the end of the text.",
                     validator.getValidityInfoForAllowedName(typicalFieldName, maxLength, untrimmedValue));
    }

    @Test
    public void testGetInvalidityInfoForPersonName_invalid_returnSpecificErrorString() {
        String invalidPersonName = "";
        String actual = validator.getInvalidityInfoForPersonName(invalidPersonName);
        assertEquals("Invalid person name (empty) should return error message that is specific to person name",
                     "The field 'person name' is empty. The value of a/an person name should be no longer "
                         + "than 100 characters. It should not be empty.",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForInstituteName_invalid_returnSpecificErrorString() {
        String invalidInstituteName = StringHelperExtension.generateStringOfLength(INSTITUTE_NAME_MAX_LENGTH + 1);
        String actual = validator.getInvalidityInfoForInstituteName(invalidInstituteName);
        assertEquals("Invalid institute name (too long) should return error message that is specific to institute name",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not "
                         + "acceptable to TEAMMATES as a/an institute name because it is too long. The value "
                         + "of a/an institute name should be no longer than 64 characters. It should not be empty.",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForNationality_invalid_returnSpecificErrorString() {
        String invalidNationality = "{ Invalid Char Nationality";
        String actual = validator.getInvalidityInfoForNationality(invalidNationality);
        assertEquals("Invalid nationality (invalid char) should return error string that is specific to nationality",
                     String.format(NATIONALITY_ERROR_MESSAGE, invalidNationality), actual);

        invalidNationality = "<script> alert('hi!'); </script>";
        actual = validator.getInvalidityInfoForNationality(invalidNationality);
        assertEquals("Unsanitized, invalid nationality should return sanitized error string",
                String.format(NATIONALITY_ERROR_MESSAGE, SanitizationHelper.sanitizeForHtml(invalidNationality)), actual);
    }

    @Test
    public void testGetInvalidityInfoForNationality_valid_returnEmptyString() {
        String validNationality = "New Zealander";
        String actual = validator.getInvalidityInfoForNationality(validNationality);
        assertEquals("Valid nationality should return empty string", "", actual);
    }

    @Test
    public void testGetInvalidityInfoForTeamName_invalid_returnSpecificErrorString() {
        String invalidTeamName = "";
        String actual = validator.getInvalidityInfoForTeamName(invalidTeamName);
        assertEquals("Invalid team name (empty) should return error message that is specific to team name",
                     "The field 'team name' is empty. The value of a/an team name should be no longer "
                         + "than 60 characters. It should not be empty.",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForSectionName_invalid_returnSpecificErrorString() {
        String invalidSectionName = "Percent Symbol % Section";
        String actual = validator.getInvalidityInfoForSectionName(invalidSectionName);
        assertEquals("Invalid section name (invalid char) should return error string that is specific to section name",
                     "\"Percent Symbol % Section\" is not acceptable to TEAMMATES as a/an section name "
                         + "because it contains invalid characters. A/An section name must start with an "
                         + "alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%).",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForCourseName_invalid_returnSpecificErrorString() {
        String invalidCourseName = "Vertical Bar | Course";
        String actual = validator.getInvalidityInfoForCourseName(invalidCourseName);
        assertEquals("Invalid course name (invalid char) should return error string that is specific to course name",
                     "\"Vertical Bar | Course\" is not acceptable to TEAMMATES as a/an course name because "
                         + "it contains invalid characters. A/An course name must start with an alphanumeric "
                         + "character, and cannot contain any vertical bar (|) or percent sign (%).",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForFeedbackSessionName_invalid_returnSpecificErrorString() {
        String invalidSessionName = StringHelperExtension.generateStringOfLength(FEEDBACK_SESSION_NAME_MAX_LENGTH + 1);
        String actual = validator.getInvalidityInfoForFeedbackSessionName(invalidSessionName);
        assertEquals("Invalid feedback session name (too long) should return error message specific to feedback "
                         + "session name",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES as a/an "
                         + "feedback session name because it is too long. The value of a/an feedback session "
                         + "name should be no longer than 38 characters. It should not be empty.",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForEmailSubject_invalid_returnSpecificErrorString() {
        String invalidEmailSubject = "";
        String actual = validator.getInvalidityInfoForEmailSubject(invalidEmailSubject);
        assertEquals("Invalid email subject (empty) should return error message that is specific to email subject",
                     "The field 'email subject' is empty. The value of a/an email subject should be no longer than "
                         + "200 characters. It should not be empty.",
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
        assertEquals("Invalid gender should return appropriate error string",
                     String.format(GENDER_ERROR_MESSAGE, invalidGender),
                     actual);

        invalidGender = "<script> alert('hi!'); </script>";
        actual = validator.getInvalidityInfoForGender(invalidGender);
        assertEquals("Unsanitized, invalid gender should return appropriate error string",
                String.format(GENDER_ERROR_MESSAGE, SanitizationHelper.sanitizeForHtml(invalidGender)),
                actual);
    }

    @Test
    public void testGetInvalidityInfoForRole_null_throwException() {
        String errorMessageForNullRole = "Did not throw the expected AssertionError for null value";
        try {
            validator.getInvalidityInfoForRole(null);
            signalFailureToDetectException(errorMessageForNullRole);
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
    }

    @Test
    public void testGetInvalidityInfoForRole_valid_returnEmptyString() {
        String validRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String actual = validator.getInvalidityInfoForRole(validRole);
        assertEquals("Valid role should return empty string", "", actual);
    }

    @Test
    public void testGetInvalidityInfoForRole_invalid_returnErrorString() {
        String invalidRole = "student leader";
        String actual = validator.getInvalidityInfoForRole(invalidRole);
        assertEquals("Invalid role should return appropriate error string",
                String.format(ROLE_ERROR_MESSAGE, invalidRole),
                actual);

        invalidRole = "<script> alert('hi!'); </script>";
        actual = validator.getInvalidityInfoForRole(invalidRole);
        assertEquals("Unsanitized, invalid role should return appropriate error string",
                String.format(ROLE_ERROR_MESSAGE, SanitizationHelper.sanitizeForHtml(invalidRole)),
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

        String maxLengthId = StringHelperExtension.generateStringOfLength(GOOGLE_ID_MAX_LENGTH);
        assertEquals("Valid Google ID (max length) should return empty string", "",
                     validator.getInvalidityInfoForGoogleId(maxLengthId));
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_invalid_returnErrorString() {
        String emptyId = "";
        assertEquals("Invalid Google ID (empty) should return appropriate error message",
                     "The field 'Google ID' is empty. A Google ID must be a valid id "
                         + "already registered with Google. It cannot be longer than "
                         + "254 characters, cannot be empty and cannot contain spaces.",
                     validator.getInvalidityInfoForGoogleId(emptyId));

        String whitespaceId = "     ";
        assertEquals("Invalid Google ID (contains whitespaces only) should return appropriate error message",
                     WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", GOOGLE_ID_FIELD_NAME),
                     validator.getInvalidityInfoForGoogleId(whitespaceId));

        String untrimmedId = "  googleIdWithSpacesAround    ";
        assertEquals("Invalid Google ID (leading/trailing whitespaces) should return appropriate error message",
                     WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", GOOGLE_ID_FIELD_NAME),
                     validator.getInvalidityInfoForGoogleId(untrimmedId));

        String tooLongId = StringHelperExtension.generateStringOfLength(GOOGLE_ID_MAX_LENGTH + 1);
        assertEquals("Invalid Google ID (too long) should return appropriate error message",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaa\" is not acceptable to TEAMMATES as a/an Google ID because it is too "
                         + "long. A Google ID must be a valid id already registered with Google. It cannot "
                         + "be longer than 254 characters, cannot be empty and cannot contain spaces.",
                     validator.getInvalidityInfoForGoogleId(tooLongId));

        String idWithSpaces = "invalid google id with spaces";
        assertEquals("Invalid Google ID (with spaces) should return appropriate error message",
                     "\"invalid google id with spaces\" is not acceptable to TEAMMATES as a/an Google ID "
                         + "because it is not in the correct format. A Google ID must be a valid id already "
                         + "registered with Google. It cannot be longer than 254 characters, cannot be empty "
                         + "and cannot contain spaces.",
                     validator.getInvalidityInfoForGoogleId(idWithSpaces));

        String idWithInvalidHtmlChar = "invalid google id with HTML/< special characters";
        assertEquals("Invalid Google ID (contains HTML characters) should return appropriate error message",
                     "\"invalid google id with HTML&#x2f;&lt; special characters\" is not acceptable to "
                         + "TEAMMATES as a/an Google ID because it is not in the correct format. A Google ID "
                         + "must be a valid id already registered with Google. It cannot be longer than 254 "
                         + "characters, cannot be empty and cannot contain spaces.",
                     validator.getInvalidityInfoForGoogleId(idWithInvalidHtmlChar));
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

        String maxLengthEmail = StringHelperExtension.generateStringOfLength(EMAIL_MAX_LENGTH - 6) + "@c.gov";
        assertEquals("Valid email (max-length) should return empty string", "",
                     validator.getInvalidityInfoForEmail(maxLengthEmail));
    }

    @Test
    public void testGetInvalidityInfoForEmail_invalid_returnErrorString() {
        String emptyEmail = "";
        assertEquals("Invalid email (empty) should return appropriate error string",
                     "The field 'email' is empty. An email address contains some text followed by one "
                         + "'@' sign followed by some more text. It cannot be longer than 254 "
                         + "characters, cannot be empty and cannot contain spaces.",
                     validator.getInvalidityInfoForEmail(emptyEmail));

        String untrimmedEmail = "  untrimmed@email.com  ";
        assertEquals("Invalid email (leading/trailing spaces) should return appropriate error string",
                     WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", EMAIL_FIELD_NAME),
                     validator.getInvalidityInfoForEmail(untrimmedEmail));

        String whitespaceEmail = "    ";
        assertEquals("Invalid email (only whitespaces) should return appropriate error string",
                     WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", EMAIL_FIELD_NAME),
                     validator.getInvalidityInfoForEmail(whitespaceEmail));

        String tooLongEmail = StringHelperExtension.generateStringOfLength(EMAIL_MAX_LENGTH + 1) + "@c.gov";
        assertEquals("Invalid email (too long) should return appropriate error string",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaa@c.gov\" is not acceptable to TEAMMATES as a/an email because it is too "
                         + "long. An email address contains some text followed by one '@' sign followed by "
                         + "some more text. It cannot be longer than 254 characters, cannot be empty and "
                         + "cannot contain spaces.",
                     validator.getInvalidityInfoForEmail(tooLongEmail));

        String emailWithSpaceAfterAtSymbol = "woMAN@com. sg";
        assertEquals("Invalid email (space character after '@') should return appropriate error string",
                     "\"woMAN@com. sg\" is not acceptable to TEAMMATES as a/an email because it is not in "
                         + "the correct format. An email address contains some text followed by one '@' sign "
                         + "followed by some more text. It cannot be longer than 254 characters, cannot be "
                         + "empty and cannot contain spaces.",
                     validator.getInvalidityInfoForEmail(emailWithSpaceAfterAtSymbol));

        String emailWithSpaceBeforeAtSymbol = "man woman@com.sg";
        assertEquals("Invalid email (space character before '@') should return appropriate error string",
                     "\"man woman@com.sg\" is not acceptable to TEAMMATES as a/an email because it "
                         + "is not in the correct format. An email address contains some text followed by "
                         + "one '@' sign followed by some more text. It cannot be longer than 254 "
                         + "characters, cannot be empty and cannot contain spaces.",
                     validator.getInvalidityInfoForEmail(emailWithSpaceBeforeAtSymbol));

        String emailWithMultipleAtSymbol = "man@woman@com.lk";
        assertEquals("Invalid email (multiple '@' characters) should return appropriate error string",
                     "\"man@woman@com.lk\" is not acceptable to TEAMMATES as a/an email because it is not "
                         + "in the correct format. An email address contains some text followed by one '@' "
                         + "sign followed by some more text. It cannot be longer than 254 characters, "
                         + "cannot be empty and cannot contain spaces.",
                     validator.getInvalidityInfoForEmail(emailWithMultipleAtSymbol));
    }

    @Test
    public void testGetInvalidityInfoForEmailContent_null_throwException() {
        String errorMessage = "Did not throw the expected AssertionError for null Email Content";
        try {
            validator.getInvalidityInfoForEmailContent(null);
            signalFailureToDetectException(errorMessage);
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
    }

    @Test
    public void testGetInvalidityInfoForEmailContent_invalid_returnEmptyString() {
        Text emptyEmailContent = new Text("");
        assertEquals("Valid Email Content should return empty string",
                     EMAIL_CONTENT_ERROR_MESSAGE,
                     validator.getInvalidityInfoForEmailContent(emptyEmailContent));
    }

    @Test
    public void testGetInvalidityInfoForEmailContent_valid_returnEmptyString() {
        Text validEmailContent = new Text("Hello! I'm a Email Content.");
        assertEquals("Valid Email Content should return empty string", "",
                     validator.getInvalidityInfoForEmailContent(validEmailContent));
    }

    @Test
    public void testGetInvalidityInfoForCourseId_null_throwException() {
        String errorMessage = "Did not throw the expected AssertionError for null Course ID";
        try {
            validator.getInvalidityInfoForCourseId(null);
            signalFailureToDetectException(errorMessage);
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
    }

    @Test
    public void testGetInvalidityInfoForCourseId_valid_returnEmptyString() {
        String typicalCourseId = "cs1101-sem1.2_";
        assertEquals("Valid Course ID (typical) should return empty string", "",
                     validator.getInvalidityInfoForCourseId(typicalCourseId));

        String shortCourseId = "c";
        assertEquals("Valid Course ID (short) should return empty string", "",
                     validator.getInvalidityInfoForCourseId(shortCourseId));

        String maxLengthCourseId = StringHelperExtension.generateStringOfLength(COURSE_ID_MAX_LENGTH);
        assertEquals("Valid Course ID (max length) should return empty string", "",
                     validator.getInvalidityInfoForCourseId(maxLengthCourseId));
    }

    @Test
    public void testGetInvalidityInfoForCourseId_invalid_returnErrorString() {
        String emptyCourseId = "";
        assertEquals("Invalid Course ID (empty) should return appropriate error string",
                     "The field 'course ID' is empty. A course ID can contain letters, numbers, "
                         + "fullstops, hyphens, underscores, and dollar signs. It cannot be "
                         + "longer than 40 characters, cannot be empty and cannot contain spaces.",
                     validator.getInvalidityInfoForCourseId(emptyCourseId));

        String untrimmedCourseId = " $cs1101-sem1.2_ ";
        assertEquals("Invalid Course ID (untrimmed) should return appropriate error string",
                     WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", COURSE_NAME_FIELD_NAME),
                     validator.getInvalidityInfoForCourseId(untrimmedCourseId));

        String whitespaceOnlyCourseId = "    ";
        assertEquals("Invalid Course ID (whitespace only) should return appropriate error string",
                     WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", COURSE_NAME_FIELD_NAME),
                     validator.getInvalidityInfoForCourseId(whitespaceOnlyCourseId));

        String tooLongCourseId = StringHelperExtension.generateStringOfLength(COURSE_ID_MAX_LENGTH + 1);
        assertEquals("Invalid Course ID (too long) should return appropriate error string",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES as a/an "
                         + "course ID because it is too long. A course ID can contain letters, numbers, "
                         + "fullstops, hyphens, underscores, and dollar signs. It cannot be longer than 40 "
                         + "characters, cannot be empty and cannot contain spaces.",
                     validator.getInvalidityInfoForCourseId(tooLongCourseId));

        String courseIdWithSpaces = "my course id with spaces";
        assertEquals("Invalid Course ID (contains spaces) should return appropriate error string",
                     "\"my course id with spaces\" is not acceptable to TEAMMATES as a/an course ID because "
                         + "it is not in the correct format. A course ID can contain letters, numbers, "
                         + "fullstops, hyphens, underscores, and dollar signs. It cannot be longer than 40 "
                         + "characters, cannot be empty and cannot contain spaces.",
                     validator.getInvalidityInfoForCourseId(courseIdWithSpaces));

        String courseIdWithInvalidChar = "cour@s*hy#";
        assertEquals("Invalid Course ID (invalid char) should return appropriate error string",
                     "\"cour@s*hy#\" is not acceptable to TEAMMATES as a/an course ID because it is not in "
                         + "the correct format. A course ID can contain letters, numbers, fullstops, "
                         + "hyphens, underscores, and dollar signs. It cannot be longer than 40 characters, "
                         + "cannot be empty and cannot contain spaces.",
                     validator.getInvalidityInfoForCourseId(courseIdWithInvalidChar));
    }

    @Test
    public void testGetInvalidityInfoForTimeForSessionStartAndEnd_valid_returnEmptyString() {
        Instant sessionStart = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        Instant sessionEnd = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        assertEquals("", validator.getInvalidityInfoForTimeForSessionStartAndEnd(sessionStart, sessionEnd));
    }

    @Test
    public void testGetInvalidityInfoForTimeForSessionStartAndEnd_invalid_returnErrorString() {
        Instant sessionStart = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        Instant sessionEnd = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        assertEquals("The end time for this feedback session cannot be earlier than the start time.",
                     validator.getInvalidityInfoForTimeForSessionStartAndEnd(sessionStart, sessionEnd));
    }

    @Test
    public void testGetInvalidityInfoForTimeForVisibilityStartAndSessionStart_valid_returnEmptyString() {
        Instant visibilityStart = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        Instant sessionStart = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        assertEquals("",
                     validator.getInvalidityInfoForTimeForVisibilityStartAndSessionStart(
                         visibilityStart, sessionStart));
    }

    @Test
    public void testGetInvalidityInfoForTimeForVisibilityStartAndSessionStart_invalid_returnErrorString() {
        Instant visibilityStart = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        Instant sessionStart = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        assertEquals("The start time for this feedback session cannot be earlier than the time when the "
                         + "session will be visible.",
                     validator.getInvalidityInfoForTimeForVisibilityStartAndSessionStart(
                         visibilityStart, sessionStart));
    }

    @Test
    public void testGetInvalidityInfoForTimeForVisibilityStartAndResultsPublish_valid_returnEmptyString() {
        Instant visibilityStart = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        Instant resultsPublish = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        assertEquals("",
                     validator.getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(
                         visibilityStart, resultsPublish));
    }

    @Test
    public void testGetInvalidityInfoForTimeForVisibilityStartAndResultsPublish_invalid_returnErrorString() {
        Instant visibilityStart = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        Instant resultsPublish = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        assertEquals("The time when the results will be visible for this feedback session cannot be "
                         + "earlier than the time when the session will be visible.",
                     validator.getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(
                         visibilityStart, resultsPublish));
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

}
