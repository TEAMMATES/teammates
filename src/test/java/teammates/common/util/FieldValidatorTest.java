package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        assertEquals("",
                actual, "Valid clean input with no special HTML characters should return empty string");
    }

    @Test
    public void testGetValidityInfoForNonHtmlField_sanitizedInput_returnEmptyString() {
        String sanitizedInput = "Valid sanitized input &lt; &gt; &quot; &#x2f; &#39; &amp;";
        String testFieldName = "Inconsequential test field name";
        String actual = FieldValidator.getValidityInfoForNonHtmlField(testFieldName, sanitizedInput);
        assertEquals("",
                actual, "Valid sanitized input should return empty string");
    }

    @Test
    public void testGetValidityInfoForNonHtmlField_unsanitizedInput_returnErrorString() {
        String unsanitizedInput = "Invalid unsanitized input <>\"/'&";
        String testFieldName = "Inconsequential test field name";
        String actual = FieldValidator.getValidityInfoForNonHtmlField(testFieldName, unsanitizedInput);
        assertEquals("The provided Inconsequential test field name is not acceptable to TEAMMATES as it "
                + "cannot contain the following special html characters in brackets: (< > \" / ' &)",
                actual, "Invalid unsanitized input should return error string");
    }

    @Test
    public void testGetValidityInfoForSizeCappedPossiblyEmptyString() {

        String typicalFieldName = "my field";
        int maxLength = 50;
        assertEquals("",
                FieldValidator.getValidityInfoForSizeCappedPossiblyEmptyString(
                        typicalFieldName,
                        maxLength,
                        "Dr. Amy-B s/o O'br, & 2nd \t \n (alias 'JB')"),
                "valid: typical value");

        assertEquals("",
                FieldValidator.getValidityInfoForSizeCappedPossiblyEmptyString(
                        typicalFieldName,
                        maxLength,
                        StringHelperExtension.generateStringOfLength(maxLength)),
                "valid: max length");

        String emptyValue = "";
        assertEquals("",
                FieldValidator.getValidityInfoForSizeCappedPossiblyEmptyString(
                        typicalFieldName,
                        maxLength,
                        emptyValue),
                "valid: empty");

        String untrimmedValue = " abc ";
        assertEquals("The provided my field is not acceptable to TEAMMATES as it contains only whitespace or "
                + "contains extra spaces at the beginning or at the end of the text.",
                FieldValidator.getValidityInfoForSizeCappedPossiblyEmptyString(typicalFieldName, maxLength,
                        untrimmedValue),
                "invalid: untrimmed");

        String tooLongName = StringHelperExtension.generateStringOfLength(maxLength + 1);
        assertEquals("\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES "
                + "as a/an my field because it is too long. The value of a/an my field should be no "
                + "longer than 50 characters.",
                FieldValidator.getValidityInfoForSizeCappedPossiblyEmptyString(typicalFieldName, maxLength,
                        tooLongName),
                "invalid: too long");
    }

    @Test
    public void testGetValidityInfoForAllowedName() {

        ______TS("null value");

        String typicalFieldName = "name field";
        int typicalLength = 25;

        assertThrows(AssertionError.class,
                () -> FieldValidator.getValidityInfoForAllowedName(typicalFieldName, typicalLength, null));

        ______TS("typical success case");

        int maxLength = 50;
        assertEquals("",
                FieldValidator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        "Ýàn-B. s/o O'br, &2\t\n(~!@#$^*+_={}[]\\:;\"<>?)"),
                "valid: typical length with valid characters");

        ______TS("failure: invalid characters");

        String nameContainInvalidChars = "Dr. Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals(
                "\"Dr. Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)\" is "
                        + "not acceptable to TEAMMATES as a/an name field because it contains invalid "
                        + "characters. A/An name field must start with an alphanumeric character, and cannot "
                        + "contain any vertical bar (|) or percent sign (%).",
                FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                        nameContainInvalidChars),
                "invalid: typical length with invalid characters");

        ______TS("failure: starts with non-alphanumeric character");

        String nameStartedWithNonAlphaNumChar = "!Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals(
                "\"!Amy-Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)\" is not "
                        + "acceptable to TEAMMATES as a/an name field because it starts with a "
                        + "non-alphanumeric character. A/An name field must start with an alphanumeric "
                        + "character, and cannot contain any vertical bar (|) or percent sign (%).",
                FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                        nameStartedWithNonAlphaNumChar),
                "invalid: typical length with invalid characters");

        ______TS("failure: starts with curly braces but contains invalid char");

        String nameStartedWithBracesButHasInvalidChar = "{Amy} -Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)";
        assertEquals(
                "\"{Amy} -Bén s/o O'&|% 2\t\n (~!@#$^*+_={}[]\\:;\"<>?)\" is not "
                        + "acceptable to TEAMMATES as a/an name field because it contains invalid "
                        + "characters. A/An name field must start with an alphanumeric character, and cannot "
                        + "contain any vertical bar (|) or percent sign (%).",
                FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                        nameStartedWithBracesButHasInvalidChar),
                "invalid: typical length with invalid characters");

        ______TS("failure: starts with opening curly bracket but dose not have closing bracket");

        String nameStartedWithCurlyBracketButHasNoEnd = "{Amy -Bén s/o O'&|% 2\t\n (~!@#$^*+_={[]\\:;\"<>?)";
        assertEquals(
                "\"{Amy -Bén s/o O'&|% 2\t\n (~!@#$^*+_={[]\\:;\"<>?)\" is not "
                        + "acceptable to TEAMMATES as a/an name field because it starts with a "
                        + "non-alphanumeric character. A/An name field must start with an alphanumeric "
                        + "character, and cannot contain any vertical bar (|) or percent sign (%).",
                FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength,
                        nameStartedWithCurlyBracketButHasNoEnd),
                "invalid: typical length started with non-alphanumeric character");

        ______TS("success: with opening and closing curly braces");

        assertEquals(
                "",
                FieldValidator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        "{last name} first name"),
                "valid: max length");

        ______TS("success: max length");

        assertEquals(
                "",
                FieldValidator.getValidityInfoForAllowedName(
                        typicalFieldName,
                        maxLength,
                        StringHelperExtension.generateStringOfLength(maxLength)),
                "valid: max length");

        ______TS("failure: too long");

        String tooLongName = StringHelperExtension.generateStringOfLength(maxLength + 1);
        assertEquals(
                "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES "
                        + "as a/an name field because it is too long. The value of a/an name field should "
                        + "be no longer than 50 characters. It should not be empty.",
                FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength, tooLongName),
                "invalid: too long");

        ______TS("failure: empty string");

        String emptyValue = "";
        assertEquals(
                "The field 'name field' is empty. The value of a/an name field should be no longer "
                        + "than 50 characters. It should not be empty.",
                FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength, emptyValue),
                "invalid: empty");

        ______TS("failure: untrimmed value");

        String untrimmedValue = " abc ";
        assertEquals(
                "The provided name field is not acceptable to TEAMMATES as it contains only whitespace "
                        + "or contains extra spaces at the beginning or at the end of the text.",
                FieldValidator.getValidityInfoForAllowedName(typicalFieldName, maxLength, untrimmedValue),
                "invalid: untrimmed");
    }

    @Test
    public void testGetInvalidityInfoForPersonName_invalid_returnSpecificErrorString() {
        String invalidPersonName = "";
        String actual = FieldValidator.getInvalidityInfoForPersonName(invalidPersonName);
        assertEquals(
                "The field 'person name' is empty. The value of a/an person name should be no longer "
                        + "than 100 characters. It should not be empty.",
                actual,
                "Invalid person name (empty) should return error message that is specific to person name");

    }

    @Test
    public void testGetInvalidityInfoForInstituteName_invalid_returnSpecificErrorString() {
        String invalidInstituteName = StringHelperExtension.generateStringOfLength(
                FieldValidator.INSTITUTE_NAME_MAX_LENGTH + 1);
        String actual = FieldValidator.getInvalidityInfoForInstituteName(invalidInstituteName);
        String expectedTemplate = "\"%s\" is not "
                + "acceptable to TEAMMATES as a/an institute name because it is too long. The value "
                + "of a/an institute name should be no longer than 128 characters. It should not be empty.";
        String expected = String.format(expectedTemplate, invalidInstituteName);
        assertEquals(expected, actual,
                "Invalid institute name (too long) should return error message that is specific to institute name");
    }

    @Test
    public void testGetInvalidityInfoForTeamName_invalid_returnSpecificErrorString() {
        String invalidTeamName = "";
        String actual = FieldValidator.getInvalidityInfoForTeamName(invalidTeamName);
        assertEquals(
                "The field 'team name' is empty. The value of a/an team name should be no longer "
                        + "than 60 characters. It should not be empty.",
                actual,
                "Invalid team name (empty) should return error message that is specific to team name");
    }

    @Test
    public void testGetInvalidityInfoForSectionName_invalid_returnSpecificErrorString() {
        String invalidSectionName = "Percent Symbol % Section";
        String actual = FieldValidator.getInvalidityInfoForSectionName(invalidSectionName);
        assertEquals(
                "\"Percent Symbol % Section\" is not acceptable to TEAMMATES as a/an section name "
                        + "because it contains invalid characters. A/An section name must start with an "
                        + "alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%).",
                actual,
                "Invalid section name (invalid char) should return error string that is specific to section name");
    }

    @Test
    public void testGetInvalidityInfoForCourseName_invalid_returnSpecificErrorString() {
        String invalidCourseName = "Vertical Bar | Course";
        String actual = FieldValidator.getInvalidityInfoForCourseName(invalidCourseName);
        assertEquals(
                "\"Vertical Bar | Course\" is not acceptable to TEAMMATES as a/an course name because "
                        + "it contains invalid characters. A/An course name must start with an alphanumeric "
                        + "character, and cannot contain any vertical bar (|) or percent sign (%).",
                actual,
                "Invalid course name (invalid char) should return error string that is specific to course name");
    }

    @Test
    public void testGetInvalidityInfoForFeedbackSessionName_invalid_returnSpecificErrorString() {
        String invalidSessionName = StringHelperExtension.generateStringOfLength(
                FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH + 1);
        String actual = FieldValidator.getInvalidityInfoForFeedbackSessionName(invalidSessionName);
        assertEquals(
                "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" "
                        + "is not acceptable to TEAMMATES as a/an feedback session name because it is too long. "
                        + "The value of a/an feedback session name should be no longer than 64 characters. "
                        + "It should not be empty.",
                actual,
                "Invalid feedback session name (too long) should return error message specific to feedback"
                        + " session name");
    }

    @Test
    public void testGetInvalidityInfoForRole_null_throwException() {
        assertThrows(AssertionError.class, () -> FieldValidator.getInvalidityInfoForRole(null));
    }

    @Test
    public void testGetInvalidityInfoForRole_valid_returnEmptyString() {
        String validRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String actual = FieldValidator.getInvalidityInfoForRole(validRole);
        assertEquals("", actual, "Valid role should return empty string");
    }

    @Test
    public void testGetInvalidityInfoForRole_invalid_returnErrorString() {
        String invalidRole = "student leader";
        String actual = FieldValidator.getInvalidityInfoForRole(invalidRole);
        assertEquals(String.format(FieldValidator.ROLE_ERROR_MESSAGE, invalidRole), actual,
                "Invalid role should return appropriate error string");

        invalidRole = "<script> alert('hi!'); </script>";
        actual = FieldValidator.getInvalidityInfoForRole(invalidRole);
        assertEquals(String.format(FieldValidator.ROLE_ERROR_MESSAGE, invalidRole), actual,
                "Unsanitized, invalid role should return appropriate error string");
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_null_throwException() {
        assertThrows(AssertionError.class, () -> FieldValidator.getInvalidityInfoForGoogleId(null));
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_valid_returnEmptyString() {
        String typicalId = "valid9.Goo-gle.id_";
        assertEquals("", FieldValidator.getInvalidityInfoForGoogleId(typicalId),
                "Valid Google ID (typical) should return empty string");

        String shortId = "e";
        assertEquals("", FieldValidator.getInvalidityInfoForGoogleId(shortId),
                "Valid Google ID (short) should return empty string");

        String emailAsId = "someone@yahoo.com";
        assertEquals("", FieldValidator.getInvalidityInfoForGoogleId(emailAsId),
                "Valid Google ID (typical email) should return empty string");

        String shortEmailAsId = "e@y.c";
        assertEquals("", FieldValidator.getInvalidityInfoForGoogleId(shortEmailAsId),
                "Valid Google ID (short email) should return empty string");

        String maxLengthId = StringHelperExtension.generateStringOfLength(FieldValidator.GOOGLE_ID_MAX_LENGTH);
        assertEquals("", FieldValidator.getInvalidityInfoForGoogleId(maxLengthId),
                "Valid Google ID (max length) should return empty string");
    }

    @Test
    public void testGetInvalidityInfoForGoogleId_invalid_returnErrorString() {
        String emptyId = "";
        assertEquals(
                "The field 'Google ID' is empty. A Google ID must be a valid id "
                        + "already registered with Google. It cannot be longer than "
                        + "254 characters, cannot be empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForGoogleId(emptyId),
                "Invalid Google ID (empty) should return appropriate error message");

        String whitespaceId = "     ";
        assertEquals(
                FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.GOOGLE_ID_FIELD_NAME),
                FieldValidator.getInvalidityInfoForGoogleId(whitespaceId),
                "Invalid Google ID (contains whitespaces only) should return appropriate error message");

        String untrimmedId = "  googleIdWithSpacesAround    ";
        assertEquals(
                FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.GOOGLE_ID_FIELD_NAME),
                FieldValidator.getInvalidityInfoForGoogleId(untrimmedId),
                "Invalid Google ID (leading/trailing whitespaces) should return appropriate error message");

        String tooLongId = StringHelperExtension.generateStringOfLength(FieldValidator.GOOGLE_ID_MAX_LENGTH + 1);
        assertEquals(
                "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "aaaaaaaa\" is not acceptable to TEAMMATES as a/an Google ID because it is too "
                        + "long. A Google ID must be a valid id already registered with Google. It cannot "
                        + "be longer than 254 characters, cannot be empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForGoogleId(tooLongId),
                "Invalid Google ID (too long) should return appropriate error message");

        String idWithSpaces = "invalid google id with spaces";
        assertEquals(
                "\"invalid google id with spaces\" is not acceptable to TEAMMATES as a/an Google ID "
                        + "because it is not in the correct format. A Google ID must be a valid id already "
                        + "registered with Google. It cannot be longer than 254 characters, cannot be empty "
                        + "and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForGoogleId(idWithSpaces),
                "Invalid Google ID (with spaces) should return appropriate error message");

        String idWithInvalidHtmlChar = "invalid google id with HTML/< special characters";
        assertEquals(
                "\"invalid google id with HTML/< special characters\" is not acceptable to "
                        + "TEAMMATES as a/an Google ID because it is not in the correct format. A Google ID "
                        + "must be a valid id already registered with Google. It cannot be longer than 254 "
                        + "characters, cannot be empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForGoogleId(idWithInvalidHtmlChar),
                "Invalid Google ID (contains HTML characters) should return appropriate error message");
    }

    @Test
    public void testGetInvalidityInfoForEmail_null_throwException() {
        assertThrows(AssertionError.class, () -> FieldValidator.getInvalidityInfoForEmail(null));
    }

    @Test
    public void testGetInvalidityInfoForEmail_valid_returnEmptyString() {
        String typicalEmail = "someone@yahoo.com";
        assertEquals("",
                FieldValidator.getInvalidityInfoForEmail(typicalEmail),
                "Valid email (typical) should return empty string");

        String shortEmail = "e@y.c";
        assertEquals("",
                FieldValidator.getInvalidityInfoForEmail(shortEmail),
                "Valid email (short) should return empty string");

        String maxLengthEmail = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_MAX_LENGTH - 6)
                + "@c.gov";
        assertEquals("",
                FieldValidator.getInvalidityInfoForEmail(maxLengthEmail),
                "Valid email (max-length) should return empty string");
    }

    @Test
    public void testGetInvalidityInfoForEmail_invalid_returnErrorString() {
        String emptyEmail = "";
        assertEquals(
                "The field 'email' is empty. An email address contains some text followed by one "
                        + "'@' sign followed by some more text, and should end with a top level domain address like "
                        + ".com. It cannot be longer than 254 characters, cannot be empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForEmail(emptyEmail),
                "Invalid email (empty) should return appropriate error string");

        String untrimmedEmail = "  untrimmed@email.com  ";
        assertEquals(
                FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.EMAIL_FIELD_NAME),
                FieldValidator.getInvalidityInfoForEmail(untrimmedEmail),
                "Invalid email (leading/trailing spaces) should return appropriate error string");

        String whitespaceEmail = "    ";
        assertEquals(
                FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.EMAIL_FIELD_NAME),
                FieldValidator.getInvalidityInfoForEmail(whitespaceEmail),
                "Invalid email (only whitespaces) should return appropriate error string");

        String tooLongEmail = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_MAX_LENGTH + 1)
                + "@c.gov";
        assertEquals(
                "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "aaaaaaaa@c.gov\" is not acceptable to TEAMMATES as a/an email because it is too "
                        + "long. An email address contains some text followed by one '@' sign followed by "
                        + "some more text, and should end with a top level domain address like .com. "
                        + "It cannot be longer than 254 characters, cannot be empty and "
                        + "cannot contain spaces.",
                FieldValidator.getInvalidityInfoForEmail(tooLongEmail),
                "Invalid email (too long) should return appropriate error string");

        String emailWithSpaceAfterAtSymbol = "woMAN@com. sg";
        assertEquals(
                "\"woMAN@com. sg\" is not acceptable to TEAMMATES as a/an email because it is not in "
                        + "the correct format. An email address contains some text followed by one '@' sign "
                        + "followed by some more text, and should end with a top level domain address like .com. "
                        + "It cannot be longer than 254 characters, cannot be "
                        + "empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForEmail(emailWithSpaceAfterAtSymbol),
                "Invalid email (space character after '@') should return appropriate error string");

        String emailWithSpaceBeforeAtSymbol = "man woman@com.sg";
        assertEquals(
                "\"man woman@com.sg\" is not acceptable to TEAMMATES as a/an email because it "
                        + "is not in the correct format. An email address contains some text followed by "
                        + "one '@' sign followed by some more text, and should end with a top level domain address "
                        + "like .com. It cannot be longer than 254 "
                        + "characters, cannot be empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForEmail(emailWithSpaceBeforeAtSymbol),
                "Invalid email (space character before '@') should return appropriate error string");

        String emailWithMultipleAtSymbol = "man@woman@com.lk";
        assertEquals(
                "\"man@woman@com.lk\" is not acceptable to TEAMMATES as a/an email because it is not "
                        + "in the correct format. An email address contains some text followed by one '@' "
                        + "sign followed by some more text, and should end with a top level domain address like .com. "
                        + "It cannot be longer than 254 characters, "
                        + "cannot be empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForEmail(emailWithMultipleAtSymbol),
                "Invalid email (multiple '@' characters) should return appropriate error string");
    }

    @Test
    public void testGetInvalidityInfoForCourseId_null_throwException() {
        assertThrows(AssertionError.class, () -> FieldValidator.getInvalidityInfoForCourseId(null));
    }

    @Test
    public void testGetInvalidityInfoForCourseId_valid_returnEmptyString() {
        String typicalCourseId = "cs1101-sem1.2_";
        assertEquals("",
                FieldValidator.getInvalidityInfoForCourseId(typicalCourseId),
                "Valid Course ID (typical) should return empty string");

        String shortCourseId = "c";
        assertEquals("",
                FieldValidator.getInvalidityInfoForCourseId(shortCourseId),
                "Valid Course ID (short) should return empty string");

        String maxLengthCourseId = StringHelperExtension.generateStringOfLength(
                FieldValidator.COURSE_ID_MAX_LENGTH);
        assertEquals("",
                FieldValidator.getInvalidityInfoForCourseId(maxLengthCourseId),
                "Valid Course ID (max length) should return empty string");
    }

    @Test
    public void testGetInvalidityInfoForCourseId_invalid_returnErrorString() {
        String emptyCourseId = "";
        assertEquals(
                "The field 'course ID' is empty. A course ID can contain letters, numbers, "
                        + "fullstops, hyphens, underscores, and dollar signs. It cannot be "
                        + "longer than 64 characters, cannot be empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForCourseId(emptyCourseId),
                "Invalid Course ID (empty) should return appropriate error string");

        String untrimmedCourseId = " $cs1101-sem1.2_ ";
        assertEquals(
                FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.COURSE_ID_FIELD_NAME),
                FieldValidator.getInvalidityInfoForCourseId(untrimmedCourseId),
                "Invalid Course ID (untrimmed) should return appropriate error string");

        String whitespaceOnlyCourseId = "    ";
        assertEquals(
                FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace(
                        "${fieldName}", FieldValidator.COURSE_ID_FIELD_NAME),
                FieldValidator.getInvalidityInfoForCourseId(whitespaceOnlyCourseId),
                "Invalid Course ID (whitespace only) should return appropriate error string");

        String tooLongCourseId = StringHelperExtension.generateStringOfLength(
                FieldValidator.COURSE_ID_MAX_LENGTH + 1);
        assertEquals(
                "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" "
                        + "is not acceptable to TEAMMATES as a/an course ID because it is too long. "
                        + "A course ID can contain letters, numbers, fullstops, hyphens, underscores, "
                        + "and dollar signs. It cannot be longer than 64 characters, "
                        + "cannot be empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForCourseId(tooLongCourseId),
                "Invalid Course ID (too long) should return appropriate error string");

        String courseIdWithSpaces = "my course id with spaces";
        assertEquals(
                "\"my course id with spaces\" is not acceptable to TEAMMATES as a/an course ID because "
                        + "it is not in the correct format. A course ID can contain letters, numbers, "
                        + "fullstops, hyphens, underscores, and dollar signs. It cannot be longer than 64 "
                        + "characters, cannot be empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForCourseId(courseIdWithSpaces),
                "Invalid Course ID (contains spaces) should return appropriate error string");

        String courseIdWithInvalidChar = "cour@s*hy#";
        assertEquals(
                "\"cour@s*hy#\" is not acceptable to TEAMMATES as a/an course ID because it is not in "
                        + "the correct format. A course ID can contain letters, numbers, fullstops, "
                        + "hyphens, underscores, and dollar signs. It cannot be longer than 64 characters, "
                        + "cannot be empty and cannot contain spaces.",
                FieldValidator.getInvalidityInfoForCourseId(courseIdWithInvalidChar),
                "Invalid Course ID (invalid char) should return appropriate error string");
    }

    @Test
    public void testGetInvalidityInfoForNewStartTime_valid_returnEmptyString() {
        Instant earliestSessionStart = TimeHelperExtension
                .getInstantHoursOffsetFromNow(-1)
                .truncatedTo(ChronoUnit.HOURS);
        assertEquals("",
                FieldValidator.getInvalidityInfoForNewStartTime(earliestSessionStart, Const.DEFAULT_TIME_ZONE));

        Instant latestSessionStart = TimeHelperExtension
                .getInstantDaysOffsetFromNow(90)
                .truncatedTo(ChronoUnit.HOURS);
        assertEquals("", FieldValidator.getInvalidityInfoForNewStartTime(latestSessionStart, Const.DEFAULT_TIME_ZONE));
    }

    @Test
    public void testGetInvalidityInfoForNewStartTime_invalid_returnErrorString() {
        Instant threeHoursBeforeNowRounded = TimeHelperExtension
                .getInstantHoursOffsetFromNow(-3)
                .truncatedTo(ChronoUnit.HOURS);
        assertEquals("The start time for this feedback session cannot be earlier than 2 hours before now.",
                FieldValidator.getInvalidityInfoForNewStartTime(threeHoursBeforeNowRounded, Const.DEFAULT_TIME_ZONE));

        Instant thirteenMonthsFromNow = TimeHelperExtension
                .getInstantMonthsOffsetFromNow(13, Const.DEFAULT_TIME_ZONE)
                .truncatedTo(ChronoUnit.HOURS);
        assertEquals("The start time for this feedback session cannot be later than 12 months from now.",
                FieldValidator.getInvalidityInfoForNewStartTime(thirteenMonthsFromNow, Const.DEFAULT_TIME_ZONE));

        Instant notAtHourMark = TimeHelperExtension
                .getInstantHoursOffsetFromNow(1)
                .truncatedTo(ChronoUnit.HOURS)
                .plus(Duration.ofMinutes(30));
        assertEquals("The start time for this feedback session must be at exact hour mark.",
                FieldValidator.getInvalidityInfoForNewStartTime(notAtHourMark, Const.DEFAULT_TIME_ZONE));
    }

    @Test
    public void testGetInvalidityInfoForNewEndTime_valid_returnEmptyString() {
        Instant earliestSessionEnd = TimeHelperExtension
                .getInstantHoursOffsetFromNow(0)
                .truncatedTo(ChronoUnit.HOURS);
        assertEquals("", FieldValidator.getInvalidityInfoForNewEndTime(earliestSessionEnd, Const.DEFAULT_TIME_ZONE));

        Instant latestSessionEnd = TimeHelperExtension
                .getInstantDaysOffsetFromNow(180)
                .truncatedTo(ChronoUnit.HOURS);
        assertEquals("", FieldValidator.getInvalidityInfoForNewEndTime(latestSessionEnd, Const.DEFAULT_TIME_ZONE));
    }

    @Test
    public void testGetInvalidityInfoForNewEndTime_invalid_returnErrorString() {
        Instant twoHoursBeforeNowRounded = TimeHelperExtension
                .getInstantHoursOffsetFromNow(-2)
                .truncatedTo(ChronoUnit.HOURS);
        assertEquals("The end time for this feedback session cannot be earlier than 1 hour before now.",
                FieldValidator.getInvalidityInfoForNewEndTime(twoHoursBeforeNowRounded, Const.DEFAULT_TIME_ZONE));

        Instant thirteenMonthsFromNow = TimeHelperExtension
                .getInstantMonthsOffsetFromNow(13, Const.DEFAULT_TIME_ZONE)
                .truncatedTo(ChronoUnit.HOURS);
        assertEquals("The end time for this feedback session cannot be later than 12 months from now.",
                FieldValidator.getInvalidityInfoForNewEndTime(thirteenMonthsFromNow,
                        Const.DEFAULT_TIME_ZONE));

        Instant notAtHourMark = TimeHelperExtension
                .getInstantHoursOffsetFromNow(1)
                .truncatedTo(ChronoUnit.HOURS)
                .plus(Duration.ofMinutes(30));
        assertEquals("The end time for this feedback session must be at exact hour mark.",
                FieldValidator.getInvalidityInfoForNewEndTime(notAtHourMark, Const.DEFAULT_TIME_ZONE));
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
    public void testGetInvalidityInfoForTimeForNewVisibilityStart_valid_returnEmptyString() {
        Instant sessionStart = TimeHelperExtension.getInstantDaysOffsetFromNow(1);
        Instant visibilityStart = sessionStart.plus(Duration.ofDays(29));
        assertEquals("", FieldValidator.getInvalidityInfoForTimeForNewVisibilityStart(
                visibilityStart, sessionStart));
    }

    @Test
    public void testGetInvalidityInfoForTimeForNewVisibilityStart_invalid_returnErrorString() {
        Instant sessionStart = TimeHelperExtension.getInstantDaysOffsetFromNow(1);
        Instant thirtyOneDaysBeforeSessionStart = TimeHelperExtension.getInstantDaysOffsetFromNow(-31);
        assertEquals("The time when the session will be visible for this feedback session cannot be "
                + "earlier than 30 days before start time.",
                FieldValidator.getInvalidityInfoForTimeForNewVisibilityStart(
                        thirtyOneDaysBeforeSessionStart, sessionStart));
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
    public void testGetInvalidityInfoForOidcIssuer_valid_returnEmptyString() {
        assertEquals("", FieldValidator.getInvalidityInfoForOidcIssuer("https://accounts.google.com", false));
        assertEquals("", FieldValidator.getInvalidityInfoForOidcIssuer("teammates-dev", true));
    }

    @Test
    public void testGetInvalidityInfoForOidcIssuer_inValid_returnErrorString() {
        String emptyIssuer = "";
        assertEquals("\"" + emptyIssuer + "\" is not an accepted OIDC issuer to TEAMMATES. ",
                FieldValidator.getInvalidityInfoForOidcIssuer(emptyIssuer, false));

        String maliciousIssuer = "https://attacker.accounts.google.com";
        assertEquals("\"" + maliciousIssuer + "\" is not an accepted OIDC issuer to TEAMMATES. ",
                FieldValidator.getInvalidityInfoForOidcIssuer(maliciousIssuer, false));

        String devServerIssuer = "teammates-dev";
        assertEquals("\"" + devServerIssuer + "\" is not an accepted OIDC issuer to TEAMMATES. ",
                FieldValidator.getInvalidityInfoForOidcIssuer(devServerIssuer, false));
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
