package teammates.common.util;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link FieldValidator}.
 */
public class FieldValidatorTest extends BaseTestCase {

    @Test
    public void testGetValidityInfoForNonHtmlField_cleanInput_returnEmptyString() {
        String clean = "Valid clean input with no special HTML characters";
        String testFieldName = "Inconsequential test field name";
        String actual = FieldValidator.getValidityInfoForNonHtmlField(testFieldName, clean);
        assertEquals("Valid clean input with no special HTML characters should return empty string", "",
                     actual);
    }

    @Test
    public void testGetValidityInfoForNonHtmlField_sanitizedInput_returnEmptyString() {
        String sanitizedInput = "Valid sanitized input &lt; &gt; &quot; &#x2f; &#39; &amp;";
        String testFieldName = "Inconsequential test field name";
        String actual = FieldValidator.getValidityInfoForNonHtmlField(testFieldName, sanitizedInput);
        assertEquals("Valid sanitized input should return empty string", "", actual);
    }

    @Test
    public void testGetValidityInfoForNonHtmlField_unsanitizedInput_returnErrorString() {
        String unsanitizedInput = "Invalid unsanitized input <>\"/'&";
        String testFieldName = "Inconsequential test field name";
        String actual = FieldValidator.getValidityInfoForNonHtmlField(testFieldName, unsanitizedInput);
        assertEquals("Invalid unsanitized input should return error string",
                     "The provided Inconsequential test field name is not acceptable to TEAMMATES as it "
                         + "cannot contain the following special html characters in brackets: (< > \" / ' &)",
                     actual);
    }

    @Test
    public void testGetValidityInfoForSizeCappedPossiblyEmptyString() {

        String typicalFieldName = "my field";
        int maxLength = 50;
        assertEquals("valid: typical value",
                "",
                FieldValidator.getValidityInfoForSizeCappedPossiblyEmptyString(
                        typicalFieldName,
                        maxLength,
                        "Dr. Amy-B s/o O'br, & 2nd \t \n (alias 'JB')"));

        assertEquals("valid: max length",
                "",
                FieldValidator.getValidityInfoForSizeCappedPossiblyEmptyString(
                        typicalFieldName,
                        maxLength,
                        StringHelperExtension.generateStringOfLength(maxLength)));

        String emptyValue = "";
        assertEquals("valid: empty",
                "",
                FieldValidator.getValidityInfoForSizeCappedPossiblyEmptyString(
                        typicalFieldName,
                        maxLength,
                        emptyValue));

        String untrimmedValue = " abc ";
        assertEquals("invalid: untrimmed",
                     "The provided my field is not acceptable to TEAMMATES as it contains only whitespace or "
                         + "contains extra spaces at the beginning or at the end of the text.",
                     FieldValidator.getValidityInfoForSizeCappedPossiblyEmptyString(typicalFieldName, maxLength,
                                                                               untrimmedValue));

        String tooLongName = StringHelperExtension.generateStringOfLength(maxLength + 1);
        assertEquals("invalid: too long",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES "
                         + "as a/an my field because it is too long. The value of a/an my field should be no "
                         + "longer than 50 characters.",
                     FieldValidator.getValidityInfoForSizeCappedPossiblyEmptyString(typicalFieldName, maxLength,
                                                                               tooLongName));
    }

    @Test
    public void testGetValidityInfoForAllowedName() {

        ______TS("null value");

        String typicalFieldName = "name field";
        int typicalLength = 25;

        assertThrows(AssertionError.class, () ->
                FieldValidator.getValidityInfoForAllowedName(typicalFieldName, typicalLength, null));

        ______TS("typical success case");

        int maxLength = 50;
        assertEquals("valid: typical length with valid characters",
                "",
                FieldValidator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        "Ýàn-B. s/o O'br, &2\t\n(~!@#$^*+_={}[]\\:;\"<>?)"));

        ______TS("failure: invalid characters");

        String nameContainInvalidChars = "Dr. Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals("invalid: typical length with invalid characters",
                     "\"Dr. Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)\" is "
                         + "not acceptable to TEAMMATES as a/an name field because it contains invalid "
                         + "characters. A/An name field must start with an alphanumeric character, and cannot "
                         + "contain any vertical bar (|) or percent sign (%).",
                     FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameContainInvalidChars));

        ______TS("failure: starts with non-alphanumeric character");

        String nameStartedWithNonAlphaNumChar = "!Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals("invalid: typical length with invalid characters",
                     "\"!Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)\" is not "
                         + "acceptable to TEAMMATES as a/an name field because it starts with a "
                         + "non-alphanumeric character. A/An name field must start with an alphanumeric "
                         + "character, and cannot contain any vertical bar (|) or percent sign (%).",
                     FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameStartedWithNonAlphaNumChar));

        ______TS("failure: starts with curly braces but contains invalid char");

        String nameStartedWithBracesButHasInvalidChar = "{Amy} -Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals("invalid: typical length with invalid characters",
                     "\"{Amy} -Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)\" is not "
                         + "acceptable to TEAMMATES as a/an name field because it contains invalid "
                         + "characters. A/An name field must start with an alphanumeric character, and cannot "
                         + "contain any vertical bar (|) or percent sign (%).",
                     FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameStartedWithBracesButHasInvalidChar));

        ______TS("failure: starts with opening curly bracket but dose not have closing bracket");

        String nameStartedWithCurlyBracketButHasNoEnd = "{Amy -Bén s/o O'&|% 2\t\n (~!@#$^*+_={[]\\:;\"<>?)";
        assertEquals("invalid: typical length started with non-alphanumeric character",
                     "\"{Amy -Bén s/o O'&|% 2\t\n (~!@#$^*+_={[]\\:;\"<>?)\" is not "
                         + "acceptable to TEAMMATES as a/an name field because it starts with a "
                         + "non-alphanumeric character. A/An name field must start with an alphanumeric "
                         + "character, and cannot contain any vertical bar (|) or percent sign (%).",
                     FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                                                             nameStartedWithCurlyBracketButHasNoEnd));

        ______TS("success: with opening and closing curly braces");

        assertEquals("valid: max length",
                "",
                FieldValidator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        "{last name} first name"));

        ______TS("success: max length");

        assertEquals("valid: max length",
                "",
                FieldValidator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        StringHelperExtension.generateStringOfLength(maxLength)));

        ______TS("failure: too long");

        String tooLongName = StringHelperExtension.generateStringOfLength(maxLength + 1);
        assertEquals("invalid: too long",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES "
                         + "as a/an name field because it is too long. The value of a/an name field should "
                         + "be no longer than 50 characters. It should not be empty.",
                     FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength, tooLongName));

        ______TS("failure: empty string");

        String emptyValue = "";
        assertEquals("invalid: empty",
                     "The field 'name field' is empty. The value of a/an name field should be no longer "
                         + "than 50 characters. It should not be empty.",
                     FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength, emptyValue));

        ______TS("failure: untrimmed value");

        String untrimmedValue = " abc ";
        assertEquals("invalid: untrimmed",
                     "The provided name field is not acceptable to TEAMMATES as it contains only whitespace "
                         + "or contains extra spaces at the beginning or at the end of the text.",
                     FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength, untrimmedValue));
    }

    @Test
    public void testGetInvalidityInfoForPersonName_invalid_returnSpecificErrorString() {
        String invalidPersonName = "";
        String actual = FieldValidator.getInvalidityInfoForPersonName(invalidPersonName);
        assertEquals("Invalid person name (empty) should return error message that is specific to person name",
                     "The field 'person name' is empty. The value of a/an person name should be no longer "
                         + "than 100 characters. It should not be empty.",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForInstituteName_invalid_returnSpecificErrorString() {
        String invalidInstituteName = StringHelperExtension.generateStringOfLength(
                                                                FieldValidator.INSTITUTE_NAME_MAX_LENGTH + 1);
        String actual = FieldValidator.getInvalidityInfoForInstituteName(invalidInstituteName);
        assertEquals("Invalid institute name (too long) should return error message that is specific to institute name",
                     "\"" + invalidInstituteName + "\" is not "
                         + "acceptable to TEAMMATES as a/an institute name because it is too long. The value "
                         + "of a/an institute name should be no longer than 128 characters. It should not be empty.",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForNationality_invalid_returnSpecificErrorString() {
        String invalidNationality = "{ Invalid Char Nationality";
        String actual = FieldValidator.getInvalidityInfoForNationality(invalidNationality);
        assertEquals("Invalid nationality (invalid char) should return error string that is specific to nationality",
                     String.format(FieldValidator.NATIONALITY_ERROR_MESSAGE, invalidNationality), actual);

        invalidNationality = "<script> alert('hi!'); </script>";
        actual = FieldValidator.getInvalidityInfoForNationality(invalidNationality);
        assertEquals("Unsanitized, invalid nationality should return sanitized error string",
                     String.format(FieldValidator.NATIONALITY_ERROR_MESSAGE, invalidNationality), actual);
    }

    @Test
    public void testGetInvalidityInfoForNationality_valid_returnEmptyString() {
        String validNationality = "New Zealander";
        String actual = FieldValidator.getInvalidityInfoForNationality(validNationality);
        assertEquals("Valid nationality should return empty string", "", actual);
    }

    @Test
    public void testGetInvalidityInfoForTeamName_invalid_returnSpecificErrorString() {
        String invalidTeamName = "";
        String actual = FieldValidator.getInvalidityInfoForTeamName(invalidTeamName);
        assertEquals("Invalid team name (empty) should return error message that is specific to team name",
                     "The field 'team name' is empty. The value of a/an team name should be no longer "
                         + "than 60 characters. It should not be empty.",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForSectionName_invalid_returnSpecificErrorString() {
        String invalidSectionName = "Percent Symbol % Section";
        String actual = FieldValidator.getInvalidityInfoForSectionName(invalidSectionName);
        assertEquals("Invalid section name (invalid char) should return error string that is specific to section name",
                     "\"Percent Symbol % Section\" is not acceptable to TEAMMATES as a/an section name "
                         + "because it contains invalid characters. A/An section name must start with an "
                         + "alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%).",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForCourseName_invalid_returnSpecificErrorString() {
        String invalidCourseName = "Vertical Bar | Course";
        String actual = FieldValidator.getInvalidityInfoForCourseName(invalidCourseName);
        assertEquals("Invalid course name (invalid char) should return error string that is specific to course name",
                     "\"Vertical Bar | Course\" is not acceptable to TEAMMATES as a/an course name because "
                         + "it contains invalid characters. A/An course name must start with an alphanumeric "
                         + "character, and cannot contain any vertical bar (|) or percent sign (%).",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForFeedbackSessionName_invalid_returnSpecificErrorString() {
        String invalidSessionName = StringHelperExtension.generateStringOfLength(
                                                              FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH + 1);
        String actual = FieldValidator.getInvalidityInfoForFeedbackSessionName(invalidSessionName);
        assertEquals("Invalid feedback session name (too long) should return error message specific to feedback "
                         + "session name",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" "
                         + "is not acceptable to TEAMMATES as a/an feedback session name because it is too long. "
                         + "The value of a/an feedback session name should be no longer than 64 characters. "
                         + "It should not be empty.",
                     actual);
    }

    @Test
    public void testGetInvalidityInfoForAccountRequestInstituteName_valid_returnEmptyString() {
        String validAccountRequestInstituteName = "National University of Singapore";
        String actual = FieldValidator.getInvalidityInfoForAccountRequestInstituteName(validAccountRequestInstituteName);
        assertEquals("", actual);
    }

    @Test
    public void testGetInvalidityInfoForAccountRequestInstituteName_invalid_returnErrorString() {
        String invalidAccountRequestInstituteName = StringHelperExtension.generateStringOfLength(
                FieldValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_MAX_LENGTH + 1);
        String actual = FieldValidator.getInvalidityInfoForAccountRequestInstituteName(invalidAccountRequestInstituteName);
        assertEquals("\"" + invalidAccountRequestInstituteName + "\" is not acceptable to TEAMMATES "
                        + "as a/an university/school/institute name because it is too long. "
                        + "The value of a/an university/school/institute name "
                        + "should be no longer than 60 characters. It should not be empty.",
                actual);

        invalidAccountRequestInstituteName = "National University of Singapore%";
        actual = FieldValidator.getInvalidityInfoForAccountRequestInstituteName(invalidAccountRequestInstituteName);
        assertEquals("\"" + invalidAccountRequestInstituteName + "\" is not acceptable to TEAMMATES "
                        + "as a/an university/school/institute name because it contains invalid characters. "
                        + "A/An university/school/institute name must start with an alphanumeric character, "
                        + "and cannot contain any vertical bar (|) or percent sign (%).",
                actual);
    }

    @Test
    public void testGetInvalidityInfoForAccountRequestCountryName_valid_returnEmptyString() {
        String validAccountRequestCountryName = "Singapore";
        String actual = FieldValidator.getInvalidityInfoForAccountRequestCountryName(validAccountRequestCountryName);
        assertEquals("", actual);
    }

    @Test
    public void testGetInvalidityInfoForAccountRequestCountryName_invalid_returnErrorString() {
        String invalidAccountRequestCountryName = StringHelperExtension.generateStringOfLength(
                FieldValidator.ACCOUNT_REQUEST_COUNTRY_NAME_MAX_LENGTH + 1);
        String actual = FieldValidator.getInvalidityInfoForAccountRequestCountryName(invalidAccountRequestCountryName);
        assertEquals("\"" + invalidAccountRequestCountryName + "\" is not acceptable to TEAMMATES "
                        + "as a/an country name because it is too long. "
                        + "The value of a/an country name "
                        + "should be no longer than 60 characters. It should not be empty.",
                actual);
    }

    @Test
    public void testGetInvalidityInfoForAccountRequestHomePageUrl_valid_returnEmptyString() {
        String validAccountRequestHomePageUrl = StringHelperExtension.generateStringOfLength(
                FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH);
        String actual = FieldValidator.getInvalidityInfoForAccountRequestHomePageUrl(validAccountRequestHomePageUrl);
        assertEquals("", actual);

        validAccountRequestHomePageUrl = "";
        actual = FieldValidator.getInvalidityInfoForAccountRequestHomePageUrl(validAccountRequestHomePageUrl);
        assertEquals("", actual);
    }

    @Test
    public void testGetInvalidityInfoForAccountRequestHomePageUrl_invalid_returnErrorString() {
        String invalidAccountRequestHomePageUrl = StringHelperExtension.generateStringOfLength(
                FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH + 1);
        String actual = FieldValidator.getInvalidityInfoForAccountRequestHomePageUrl(invalidAccountRequestHomePageUrl);
        assertEquals("\"" + invalidAccountRequestHomePageUrl + "\" is not acceptable to TEAMMATES "
                        + "as a/an url because it is too long. "
                        + "The value of a/an url should be no longer than 500 characters.",
                actual);
    }

    @Test
    public void testGetInvalidityInfoForAccountRequestComments_valid_returnEmptyString() {
        String validAccountRequestComments = StringHelperExtension.generateStringOfLength(
                FieldValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH);
        String actual = FieldValidator.getInvalidityInfoForAccountRequestComments(validAccountRequestComments);
        assertEquals("", actual);

        validAccountRequestComments = "";
        actual = FieldValidator.getInvalidityInfoForAccountRequestComments(validAccountRequestComments);
        assertEquals("", actual);
    }

    @Test
    public void testGetInvalidityInfoForAccountRequestComments_invalid_returnErrorString() {
        String invalidAccountRequestComments = StringHelperExtension.generateStringOfLength(
                FieldValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH + 1);
        String actual = FieldValidator.getInvalidityInfoForAccountRequestComments(invalidAccountRequestComments);
        assertEquals("\"" + invalidAccountRequestComments + "\" is not acceptable to TEAMMATES "
                        + "as a/an comment/query because it is too long. "
                        + "The value of a/an comment/query should be no longer than 1000 characters.",
                actual);
    }

    @Test
    public void testGetInvalidityInfoForRole_null_throwException() {
        assertThrows(AssertionError.class, () -> FieldValidator.getInvalidityInfoForRole(null));
    }

    @Test
    public void testGetInvalidityInfoForRole_valid_returnEmptyString() {
        String validRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String actual = FieldValidator.getInvalidityInfoForRole(validRole);
        assertEquals("Valid role should return empty string", "", actual);
    }

    @Test
    public void testGetInvalidityInfoForRole_invalid_returnErrorString() {
        String invalidRole = "student leader";
        String actual = FieldValidator.getInvalidityInfoForRole(invalidRole);
        assertEquals("Invalid role should return appropriate error string",
                String.format(FieldValidator.ROLE_ERROR_MESSAGE, invalidRole),
                actual);

        invalidRole = "<script> alert('hi!'); </script>";
        actual = FieldValidator.getInvalidityInfoForRole(invalidRole);
        assertEquals("Unsanitized, invalid role should return appropriate error string",
                String.format(FieldValidator.ROLE_ERROR_MESSAGE, invalidRole), actual);
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_null_throwException() {
        assertThrows(AssertionError.class, () -> FieldValidator.getInvalidityInfoForGoogleId(null));
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_valid_returnEmptyString() {
        String typicalId = "valid9.Goo-gle.id_";
        assertEquals("Valid Google ID (typical) should return empty string", "",
                     FieldValidator.getInvalidityInfoForGoogleId(typicalId));

        String shortId = "e";
        assertEquals("Valid Google ID (short) should return empty string", "",
                     FieldValidator.getInvalidityInfoForGoogleId(shortId));

        String emailAsId = "someone@yahoo.com";
        assertEquals("Valid Google ID (typical email) should return empty string", "",
                     FieldValidator.getInvalidityInfoForGoogleId(emailAsId));

        String shortEmailAsId = "e@y.c";
        assertEquals("Valid Google ID (short email) should return empty string", "",
                     FieldValidator.getInvalidityInfoForGoogleId(shortEmailAsId));

        String maxLengthId = StringHelperExtension.generateStringOfLength(FieldValidator.GOOGLE_ID_MAX_LENGTH);
        assertEquals("Valid Google ID (max length) should return empty string", "",
                     FieldValidator.getInvalidityInfoForGoogleId(maxLengthId));
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_invalid_returnErrorString() {
        String emptyId = "";
        assertEquals("Invalid Google ID (empty) should return appropriate error message",
                     "The field 'Google ID' is empty. A Google ID must be a valid id "
                         + "already registered with Google. It cannot be longer than "
                         + "254 characters, cannot be empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForGoogleId(emptyId));

        String whitespaceId = "     ";
        assertEquals("Invalid Google ID (contains whitespaces only) should return appropriate error message",
                     FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.GOOGLE_ID_FIELD_NAME),
                     FieldValidator.getInvalidityInfoForGoogleId(whitespaceId));

        String untrimmedId = "  googleIdWithSpacesAround    ";
        assertEquals("Invalid Google ID (leading/trailing whitespaces) should return appropriate error message",
                     FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.GOOGLE_ID_FIELD_NAME),
                     FieldValidator.getInvalidityInfoForGoogleId(untrimmedId));

        String tooLongId = StringHelperExtension.generateStringOfLength(FieldValidator.GOOGLE_ID_MAX_LENGTH + 1);
        assertEquals("Invalid Google ID (too long) should return appropriate error message",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaa\" is not acceptable to TEAMMATES as a/an Google ID because it is too "
                         + "long. A Google ID must be a valid id already registered with Google. It cannot "
                         + "be longer than 254 characters, cannot be empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForGoogleId(tooLongId));

        String idWithSpaces = "invalid google id with spaces";
        assertEquals("Invalid Google ID (with spaces) should return appropriate error message",
                     "\"invalid google id with spaces\" is not acceptable to TEAMMATES as a/an Google ID "
                         + "because it is not in the correct format. A Google ID must be a valid id already "
                         + "registered with Google. It cannot be longer than 254 characters, cannot be empty "
                         + "and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForGoogleId(idWithSpaces));

        String idWithInvalidHtmlChar = "invalid google id with HTML/< special characters";
        assertEquals("Invalid Google ID (contains HTML characters) should return appropriate error message",
                     "\"invalid google id with HTML/< special characters\" is not acceptable to "
                         + "TEAMMATES as a/an Google ID because it is not in the correct format. A Google ID "
                         + "must be a valid id already registered with Google. It cannot be longer than 254 "
                         + "characters, cannot be empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForGoogleId(idWithInvalidHtmlChar));
    }

    @Test
    public void testGetInvalidityInfoForEmail_null_throwException() {
        assertThrows(AssertionError.class, () -> FieldValidator.getInvalidityInfoForEmail(null));
    }

    @Test
    public void testGetInvalidityInfoForEmail_valid_returnEmptyString() {
        String typicalEmail = "someone@yahoo.com";
        assertEquals("Valid email (typical) should return empty string", "",
                     FieldValidator.getInvalidityInfoForEmail(typicalEmail));

        String shortEmail = "e@y.c";
        assertEquals("Valid email (short) should return empty string", "",
                     FieldValidator.getInvalidityInfoForEmail(shortEmail));

        String maxLengthEmail = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_MAX_LENGTH - 6) + "@c.gov";
        assertEquals("Valid email (max-length) should return empty string", "",
                     FieldValidator.getInvalidityInfoForEmail(maxLengthEmail));
    }

    @Test
    public void testGetInvalidityInfoForEmail_invalid_returnErrorString() {
        String emptyEmail = "";
        assertEquals("Invalid email (empty) should return appropriate error string",
                     "The field 'email' is empty. An email address contains some text followed by one "
                         + "'@' sign followed by some more text, and should end with a top level domain address like "
                         + ".com. It cannot be longer than 254 characters, cannot be empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForEmail(emptyEmail));

        String untrimmedEmail = "  untrimmed@email.com  ";
        assertEquals("Invalid email (leading/trailing spaces) should return appropriate error string",
                     FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.EMAIL_FIELD_NAME),
                     FieldValidator.getInvalidityInfoForEmail(untrimmedEmail));

        String whitespaceEmail = "    ";
        assertEquals("Invalid email (only whitespaces) should return appropriate error string",
                     FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.EMAIL_FIELD_NAME),
                     FieldValidator.getInvalidityInfoForEmail(whitespaceEmail));

        String tooLongEmail = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_MAX_LENGTH + 1) + "@c.gov";
        assertEquals("Invalid email (too long) should return appropriate error string",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                         + "aaaaaaaa@c.gov\" is not acceptable to TEAMMATES as a/an email because it is too "
                         + "long. An email address contains some text followed by one '@' sign followed by "
                         + "some more text, and should end with a top level domain address like .com. "
                         + "It cannot be longer than 254 characters, cannot be empty and "
                         + "cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForEmail(tooLongEmail));

        String emailWithSpaceAfterAtSymbol = "woMAN@com. sg";
        assertEquals("Invalid email (space character after '@') should return appropriate error string",
                     "\"woMAN@com. sg\" is not acceptable to TEAMMATES as a/an email because it is not in "
                         + "the correct format. An email address contains some text followed by one '@' sign "
                         + "followed by some more text, and should end with a top level domain address like .com. "
                         + "It cannot be longer than 254 characters, cannot be "
                         + "empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForEmail(emailWithSpaceAfterAtSymbol));

        String emailWithSpaceBeforeAtSymbol = "man woman@com.sg";
        assertEquals("Invalid email (space character before '@') should return appropriate error string",
                     "\"man woman@com.sg\" is not acceptable to TEAMMATES as a/an email because it "
                         + "is not in the correct format. An email address contains some text followed by "
                         + "one '@' sign followed by some more text, and should end with a top level domain address "
                         + "like .com. It cannot be longer than 254 "
                         + "characters, cannot be empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForEmail(emailWithSpaceBeforeAtSymbol));

        String emailWithMultipleAtSymbol = "man@woman@com.lk";
        assertEquals("Invalid email (multiple '@' characters) should return appropriate error string",
                     "\"man@woman@com.lk\" is not acceptable to TEAMMATES as a/an email because it is not "
                         + "in the correct format. An email address contains some text followed by one '@' "
                         + "sign followed by some more text, and should end with a top level domain address like .com. "
                         + "It cannot be longer than 254 characters, "
                         + "cannot be empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForEmail(emailWithMultipleAtSymbol));
    }

    @Test
    public void testGetInvalidityInfoForCourseId_null_throwException() {
        assertThrows(AssertionError.class, () -> FieldValidator.getInvalidityInfoForCourseId(null));
    }

    @Test
    public void testGetInvalidityInfoForCourseId_valid_returnEmptyString() {
        String typicalCourseId = "cs1101-sem1.2_";
        assertEquals("Valid Course ID (typical) should return empty string", "",
                     FieldValidator.getInvalidityInfoForCourseId(typicalCourseId));

        String shortCourseId = "c";
        assertEquals("Valid Course ID (short) should return empty string", "",
                     FieldValidator.getInvalidityInfoForCourseId(shortCourseId));

        String maxLengthCourseId = StringHelperExtension.generateStringOfLength(
                FieldValidator.COURSE_ID_MAX_LENGTH);
        assertEquals("Valid Course ID (max length) should return empty string", "",
                     FieldValidator.getInvalidityInfoForCourseId(maxLengthCourseId));
    }

    @Test
    public void testGetInvalidityInfoForCourseId_invalid_returnErrorString() {
        String emptyCourseId = "";
        assertEquals("Invalid Course ID (empty) should return appropriate error string",
                     "The field 'course ID' is empty. A course ID can contain letters, numbers, "
                         + "fullstops, hyphens, underscores, and dollar signs. It cannot be "
                         + "longer than 64 characters, cannot be empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForCourseId(emptyCourseId));

        String untrimmedCourseId = " $cs1101-sem1.2_ ";
        assertEquals("Invalid Course ID (untrimmed) should return appropriate error string",
                     FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.COURSE_ID_FIELD_NAME),
                     FieldValidator.getInvalidityInfoForCourseId(untrimmedCourseId));

        String whitespaceOnlyCourseId = "    ";
        assertEquals("Invalid Course ID (whitespace only) should return appropriate error string",
                     FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.COURSE_ID_FIELD_NAME),
                     FieldValidator.getInvalidityInfoForCourseId(whitespaceOnlyCourseId));

        String tooLongCourseId = StringHelperExtension.generateStringOfLength(
                                                           FieldValidator.COURSE_ID_MAX_LENGTH + 1);
        assertEquals("Invalid Course ID (too long) should return appropriate error string",
                     "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" "
                         + "is not acceptable to TEAMMATES as a/an course ID because it is too long. "
                         + "A course ID can contain letters, numbers, fullstops, hyphens, underscores, "
                         + "and dollar signs. It cannot be longer than 64 characters, "
                         + "cannot be empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForCourseId(tooLongCourseId));

        String courseIdWithSpaces = "my course id with spaces";
        assertEquals("Invalid Course ID (contains spaces) should return appropriate error string",
                     "\"my course id with spaces\" is not acceptable to TEAMMATES as a/an course ID because "
                         + "it is not in the correct format. A course ID can contain letters, numbers, "
                         + "fullstops, hyphens, underscores, and dollar signs. It cannot be longer than 64 "
                         + "characters, cannot be empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForCourseId(courseIdWithSpaces));

        String courseIdWithInvalidChar = "cour@s*hy#";
        assertEquals("Invalid Course ID (invalid char) should return appropriate error string",
                     "\"cour@s*hy#\" is not acceptable to TEAMMATES as a/an course ID because it is not in "
                         + "the correct format. A course ID can contain letters, numbers, fullstops, "
                         + "hyphens, underscores, and dollar signs. It cannot be longer than 64 characters, "
                         + "cannot be empty and cannot contain spaces.",
                     FieldValidator.getInvalidityInfoForCourseId(courseIdWithInvalidChar));
    }

    @Test
    public void testGetInvalidityInfoForTimeForSessionStartAndEnd_valid_returnEmptyString() {
        Instant sessionStart = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        Instant sessionEnd = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        assertEquals("", FieldValidator.getInvalidityInfoForTimeForSessionStartAndEnd(sessionStart, sessionEnd));
    }

    @Test
    public void testGetInvalidityInfoForTimeForSessionStartAndEnd_invalid_returnErrorString() {
        Instant sessionStart = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        Instant sessionEnd = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        assertEquals("The end time for this feedback session cannot be earlier than the start time.",
                     FieldValidator.getInvalidityInfoForTimeForSessionStartAndEnd(sessionStart, sessionEnd));
    }

    @Test
    public void testGetInvalidityInfoForTimeForVisibilityStartAndSessionStart_valid_returnEmptyString() {
        Instant visibilityStart = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        Instant sessionStart = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        assertEquals("",
                     FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndSessionStart(
                         visibilityStart, sessionStart));
    }

    @Test
    public void testGetInvalidityInfoForTimeForVisibilityStartAndSessionStart_invalid_returnErrorString() {
        Instant visibilityStart = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        Instant sessionStart = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        assertEquals("The start time for this feedback session cannot be earlier than the time when the "
                         + "session will be visible.",
                     FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndSessionStart(
                         visibilityStart, sessionStart));
    }

    @Test
    public void testGetInvalidityInfoForTimeForVisibilityStartAndResultsPublish_valid_returnEmptyString() {
        Instant visibilityStart = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        Instant resultsPublish = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        assertEquals("",
                     FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(
                         visibilityStart, resultsPublish));
    }

    @Test
    public void testGetInvalidityInfoForTimeForVisibilityStartAndResultsPublish_invalid_returnErrorString() {
        Instant visibilityStart = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        Instant resultsPublish = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        assertEquals("The time when the results will be visible for this feedback session cannot be "
                         + "earlier than the time when the session will be visible.",
                     FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(
                         visibilityStart, resultsPublish));
    }

    @Test
    public void testGetInvalidityInfoForTimeForSessionEndAndExtendedDeadlines_valid_returnEmptyString() {
        Instant sessionEnd = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        Map<String, Instant> extendedDeadlines = new HashMap<>();
        extendedDeadlines.put("participant@email.com", TimeHelperExtension.getInstantHoursOffsetFromNow(1));
        assertEquals("",
                FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
                        sessionEnd, extendedDeadlines));
    }

    @Test
    public void testGetInvalidityInfoForTimeForSessionEndAndExtendedDeadlines_invalid_returnErrorString() {
        ______TS("extended deadline earlier than the end time");
        Instant sessionEnd = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        Map<String, Instant> extendedDeadlines = new HashMap<>();
        extendedDeadlines.put("participant@email.com", TimeHelperExtension.getInstantHoursOffsetFromNow(-1));
        assertEquals("The extended deadlines for this feedback session cannot be earlier than or at the same time as "
                        + "the end time.",
                FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
                        sessionEnd, extendedDeadlines));

        ______TS("extended deadline at the same time as the end time");
        extendedDeadlines.put("participant@email.com", sessionEnd);
        assertEquals("The extended deadlines for this feedback session cannot be earlier than or at the same time as "
                        + "the end time.",
                FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
                        sessionEnd, extendedDeadlines));
    }

    @Test
    public void testGetInvalidityInfoForTimeForNotificationStartAndEnd_valid_returnEmptyString() {
        Instant notificationStart = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);
        Instant notificationEnd = TimeHelperExtension.getInstantHoursOffsetFromNow(1);

        assertEquals("",
                FieldValidator.getInvalidityInfoForTimeForNotificationStartAndEnd(
                        notificationStart, notificationEnd));
    }

    @Test
    public void testGetInvalidityInfoForTimeForNotificationStartAndEnd_inValid_returnErrorString() {
        Instant notificationStart = TimeHelperExtension.getInstantHoursOffsetFromNow(1);
        Instant notificationEnd = TimeHelperExtension.getInstantHoursOffsetFromNow(-1);

        assertEquals("The time when the notification will expire for this notification cannot be earlier "
                        + "than the time when the notification will be visible.",
                FieldValidator.getInvalidityInfoForTimeForNotificationStartAndEnd(
                        notificationStart, notificationEnd));
    }

    @Test
    public void testGetInvalidityInfoForNotificationTitle_valid_returnEmptyString() {
        assertEquals("", FieldValidator.getInvalidityInfoForNotificationTitle("valid title"));
    }

    @Test
    public void testGetInvalidityInfoForNotificationTitle_inValid_returnErrorString() {
        ______TS("Empty notification title");
        assertEquals("The field 'notification title' is empty.",
                FieldValidator.getInvalidityInfoForNotificationTitle(""));

        ______TS("Notification title exceeds maximum length");
        String invalidNotificationTitle = StringHelperExtension.generateStringOfLength(
                FieldValidator.NOTIFICATION_TITLE_MAX_LENGTH + 1);
        assertEquals("\"" + invalidNotificationTitle + "\" is not acceptable to TEAMMATES as a/an "
                        + "notification title because it is too long. "
                        + "The value of a/an notification title should be no longer than "
                        + FieldValidator.NOTIFICATION_TITLE_MAX_LENGTH
                        + " characters. It should not be empty.",
                FieldValidator.getInvalidityInfoForNotificationTitle(invalidNotificationTitle));
    }

    @Test
    public void testGetInvalidityInfoForNotificationBody_valid_returnEmptyString() {
        assertEquals("", FieldValidator.getInvalidityInfoForNotificationBody("valid body"));
    }

    @Test
    public void testGetInvalidityInfoForNotificationBody_inValid_returnErrorString() {
        assertEquals("The field 'notification message' is empty.",
                FieldValidator.getInvalidityInfoForNotificationBody(""));
    }

    @Test
    public void testGetInvalidityInfoForNotificationStyle_valid_returnEmptyString() {
        assertEquals("", FieldValidator.getInvalidityInfoForNotificationStyle("SUCCESS"));
    }

    @Test
    public void testGetInvalidityInfoForNotificationStyle_inValid_returnErrorString() {
        String invalidStyle = "invalid style";
        assertEquals("\"" + invalidStyle + "\" is not an accepted notification style to TEAMMATES. ",
                FieldValidator.getInvalidityInfoForNotificationStyle(invalidStyle));
    }

    @Test
    public void testGetInvalidityInfoForNotificationTargetUser_valid_returnEmptyString() {
        assertEquals("", FieldValidator.getInvalidityInfoForNotificationTargetUser("GENERAL"));
    }

    @Test
    public void testGetInvalidityInfoForNotificationTargetUser_inValid_returnErrorString() {
        String invalidUser = "invalid user";
        assertEquals("\"" + invalidUser + "\" is not an accepted notification target user to TEAMMATES. ",
                FieldValidator.getInvalidityInfoForNotificationTargetUser(invalidUser));
    }

    @Test
    public void testRegexName() {
        ______TS("success: typical name");
        String name = "Benny Charlés";
        assertTrue(StringHelper.isMatching(name, FieldValidator.REGEX_NAME));

        ______TS("success: name begins with accented characters");
        name = "Ýàn-B. s/o O'br, &2(~!@#$^*+_={}[]\\:;\"<>?)";
        assertTrue(StringHelper.isMatching(name, FieldValidator.REGEX_NAME));

        ______TS("failure: name begins with non-alphanumeric character");
        name = "~Amy-Ben. s/o O'br, &2(~!@#$^*+_={}[]\\:;\"<>?)";
        assertFalse(StringHelper.isMatching(name, FieldValidator.REGEX_NAME));

        ______TS("failure: name contains invalid character");
        name = "Amy-B. s/o O'br, %|&2(~!@#$^*+_={}[]\\:;\"<>?)";
        assertFalse(StringHelper.isMatching(name, FieldValidator.REGEX_NAME));
    }

    @Test
    public void testRegexEmail() {
        ______TS("success: typical email");
        String email = "john@email.com";
        assertTrue(StringHelper.isMatching(email, FieldValidator.REGEX_EMAIL));

        ______TS("failure: no top level domain");
        email = "a@e";
        assertFalse(StringHelper.isMatching(email, FieldValidator.REGEX_EMAIL));

        ______TS("success: minimum allowed email format");
        email = "a@e.c";
        assertTrue(StringHelper.isMatching(email, FieldValidator.REGEX_EMAIL));

        ______TS("success: all allowed special characters");
        email = "a!#$%&'*/=?^_`{}~@e.c";
        assertTrue(StringHelper.isMatching(email, FieldValidator.REGEX_EMAIL));

        ______TS("failure: invalid starting character");
        email = "$john@email.com";
        assertFalse(StringHelper.isMatching(email, FieldValidator.REGEX_EMAIL));

        ______TS("failure: two consecutive dots in local part");
        email = "john..dot@email.com";
        assertFalse(StringHelper.isMatching(email, FieldValidator.REGEX_EMAIL));

        ______TS("failure: invalid characters in domain part");
        email = "john@e&email.com";
        assertFalse(StringHelper.isMatching(email, FieldValidator.REGEX_EMAIL));

        ______TS("failure: invalid ending character in domain part");
        email = "john@email.com3";
        assertFalse(StringHelper.isMatching(email, FieldValidator.REGEX_EMAIL));
    }

    @Test
    public void testRegexCourseId() {
        ______TS("success: typical course ID");
        String courseId = "CS101";
        assertTrue(StringHelper.isMatching(courseId, FieldValidator.REGEX_COURSE_ID));

        ______TS("success: course ID with all accepted symbols");
        courseId = "CS101-B.$";
        assertTrue(StringHelper.isMatching(courseId, FieldValidator.REGEX_COURSE_ID));

        ______TS("failure: contains invalid character");
        courseId = "CS101+B";
        assertFalse(StringHelper.isMatching(courseId, FieldValidator.REGEX_COURSE_ID));
    }

    @Test
    public void testRegexGoogleIdNonEmail() {
        ______TS("success: typical google id");
        String googleId = "teammates.instr";
        assertTrue(StringHelper.isMatching(googleId, FieldValidator.REGEX_GOOGLE_ID_NON_EMAIL));

        ______TS("success: google id with all accepted characters");
        googleId = "teammates.new_instr-3";
        assertTrue(StringHelper.isMatching(googleId, FieldValidator.REGEX_GOOGLE_ID_NON_EMAIL));

        ______TS("failure: is email");
        googleId = "teammates.instr@email.com";
        assertFalse(StringHelper.isMatching(googleId, FieldValidator.REGEX_GOOGLE_ID_NON_EMAIL));

        ______TS("failure: contains invalid character");
        googleId = "teammates.$instr";
        assertFalse(StringHelper.isMatching(googleId, FieldValidator.REGEX_GOOGLE_ID_NON_EMAIL));
    }

}
