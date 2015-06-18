package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static teammates.common.util.FieldValidator.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Assumption;
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
        
        String untrimmedValue = " abc ";
        assertEquals("invalid: untrimmed", 
                String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, typicalFieldName),
                validator.getValidityInfoForSizeCappedNonEmptyString(
                        typicalFieldName, 
                        maxLength, 
                        untrimmedValue));
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
                     validator.getValidityInfoForAllowedName(typicalFieldName,  maxLength, 
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
        
        String tooLongName = StringHelper.generateStringOfLength(maxLength+1);
        assertEquals("invalid: too long", 
                String.format(
                        SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, 
                        tooLongName, typicalFieldName,  REASON_TOO_LONG, typicalFieldName, maxLength),
                validator.getValidityInfoForAllowedName(
                        typicalFieldName, 
                        maxLength, 
                        tooLongName));
        
        ______TS("failure: empty string");
        
        String emptyValue = "";
        assertEquals("invalid: empty", 
                String.format(
                        SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, 
                        emptyValue, typicalFieldName,  REASON_EMPTY, typicalFieldName, maxLength),
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
    public void testGetValidityInfo_PERSON_NAME() {
        
        //NOTE 1: The SUT's work is done mostly in testGetValidityInfoForSizeCappedNonEmptyString
        //  , testGetValidityInfoForSizeCappedPossiblyEmptyString or testGetValidityInfoForAllowedName
        // methods which are already unit tested. Therefore, this method checks if the 
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
    public void testGetValidityInfo_STUDENT_ROLE_COMMENTS() {
        
        //See NOTE 1.
        runGenericTestCasesForCappedSizeStringTypeField(
                FieldType.STUDENT_ROLE_COMMENTS, 
                STUDENT_ROLE_COMMENTS_MAX_LENGTH, 
                STUDENT_ROLE_COMMENTS_ERROR_MESSAGE, 
                true);
    }
    
    @Test
    public void testGetValidityInfo_COUNTRY() {
        runGenericTestCasesForCappedSizeStringTypeField(
                FieldType.NATIONALITY,
                NATIONALITY_MAX_LENGTH,
                NATIONALITY_ERROR_MESSAGE,
                false);
    }
    
    @Test
    public void testGetValidityInfo_GENDER() {
        verifyAssertError("null value", FieldType.GENDER, null);
        
        String validInput = "male";
        String invalidInput = "random_value";
        String emptyInput = "";
        
        testOnce("valid: accepted gender value",
                FieldType.GENDER,
                validInput,
                "");
        
        testOnce("invalid: randomn gender value",
                FieldType.GENDER,
                invalidInput,
                String.format(FieldValidator.GENDER_ERROR_MESSAGE, invalidInput));
        
        testOnce("invalid: empty string",
                FieldType.GENDER,
                emptyInput,
                String.format(FieldValidator.GENDER_ERROR_MESSAGE, emptyInput));
    }


    @Test
    public void testGetValidityInfo_GOOGLE_ID() {
        
        verifyAssertError("null value", FieldType.GOOGLE_ID, null);
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
                String.format(GOOGLE_ID_ERROR_MESSAGE, emptyValue,    REASON_EMPTY));
        
        String untrimmedValue = " e@email.com ";
        testOnce("invalid: untrimmed", 
                FieldType.GOOGLE_ID, 
                untrimmedValue,
                String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, "googleID"));
        
        String whitespaceOnlyValue = "    ";
        testOnce("invalid: whitespace only", 
                FieldType.GOOGLE_ID, 
                whitespaceOnlyValue,
                String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, "googleID"));
        
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
                String.format(GOOGLE_ID_ERROR_MESSAGE, Sanitizer.sanitizeForHtml(valueWithDisallowedChar),
                              REASON_INCORRECT_FORMAT));
        
    }
    
    @Test
    public void TestGetValidityInfo_INSTRUCTOR_ROLE() {
        
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
    public void testGetValidityInfo_EMAIL() {
        
        verifyAssertError("null value", FieldType.EMAIL, null);
        
        
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
                String.format(EMAIL_ERROR_MESSAGE, emptyValue,    REASON_EMPTY));
        
        String untrimmedValue = " e@email.com ";
        testOnce("invalid: untrimmed", 
                FieldType.EMAIL, 
                untrimmedValue,
                String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, "email"));
        
        String whitespaceOnlyValue = "    ";
        testOnce("invalid: whitespace only", 
                FieldType.EMAIL, 
                whitespaceOnlyValue,
                String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, "email"));
        
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
                String.format(COURSE_ID_ERROR_MESSAGE, emptyValue,    REASON_EMPTY));
        
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
    public void test_REGEX_NAME() throws Exception {
        ______TS("success: typical name");
        String name = "Benny Charlés";
        Assumption.assertTrue(StringHelper.isMatching(name, REGEX_NAME));
        
        ______TS("success: name begins with accented characters");
        name = "Ýàn-B. s/o O'br, &2(~!@#$^*+_={}[]\\:;\"<>?)";
        Assumption.assertTrue(StringHelper.isMatching(name, REGEX_NAME));
        
        ______TS("failure: name begins with non-alphanumeric character");
        name = "~Amy-Ben. s/o O'br, &2(~!@#$^*+_={}[]\\:;\"<>?)";
        Assumption.assertFalse(StringHelper.isMatching(name, REGEX_NAME));
        
        ______TS("failure: name contains invalid character");
        name = "Amy-B. s/o O'br, %|&2(~!@#$^*+_={}[]\\:;\"<>?)";
        Assumption.assertFalse(StringHelper.isMatching(name, REGEX_NAME));
    }
    
    @Test
    public void test_REGEX_EMAIL() throws Exception {
        ______TS("success: typical email");
        String email = "john@email.com";
        Assumption.assertTrue(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("success: minimum allowed email format");
        email = "a@e";
        Assumption.assertTrue(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("success: all allowed special characters");
        email = "a!#$%&'*/=?^_`{}~@e";
        Assumption.assertTrue(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("failure: invalid starting character");
        email = "$john@email.com";
        Assumption.assertFalse(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("failure: two consecutive dots in local part");
        email = "john..dot@email.com";
        Assumption.assertFalse(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("failure: invalid characters in domain part");
        email = "john@e&email.com";
        Assumption.assertFalse(StringHelper.isMatching(email, REGEX_EMAIL));
        
        ______TS("failure: invalid ending character in domain part");
        email = "john@email.com3";
        Assumption.assertFalse(StringHelper.isMatching(email, REGEX_EMAIL));
    }
    
    @Test
    public void test_REGEX_COURSE_ID() throws Exception {
        ______TS("success: typical course ID");
        String courseId = "CS101";
        Assumption.assertTrue(StringHelper.isMatching(courseId, REGEX_COURSE_ID));
        
        ______TS("success: course ID with all accepted symbols");
        courseId = "CS101-B.$";
        Assumption.assertTrue(StringHelper.isMatching(courseId, REGEX_COURSE_ID));
        
        ______TS("failure: contains invalid character");
        courseId = "CS101+B";
        Assumption.assertFalse(StringHelper.isMatching(courseId, REGEX_COURSE_ID));
    }
    
    @Test
    public void test_REGEX_SAMPLE_COURSE_ID() throws Exception {
        ______TS("success: typical sample course ID");
        String courseId = "CS101-demo3";
        Assumption.assertTrue(StringHelper.isMatching(courseId, REGEX_SAMPLE_COURSE_ID));
        
        ______TS("failure: non-demo course ID");
        courseId = "CS101";
        Assumption.assertFalse(StringHelper.isMatching(courseId, REGEX_SAMPLE_COURSE_ID));
    }
    
    @Test
    public void test_REGEX_GOOGLE_ID_NON_EMAIL() throws Exception {
        ______TS("success: typical google id");
        String googleId = "teammates.instr";
        Assumption.assertTrue(StringHelper.isMatching(googleId, REGEX_GOOGLE_ID_NON_EMAIL));
        
        ______TS("success: google id with all accepted characters");
        googleId = "teammates.new_instr-3";
        Assumption.assertTrue(StringHelper.isMatching(googleId, REGEX_GOOGLE_ID_NON_EMAIL));
        
        ______TS("failure: is email");
        googleId = "teammates.instr@email.com";
        Assumption.assertFalse(StringHelper.isMatching(googleId, REGEX_GOOGLE_ID_NON_EMAIL));
        
        ______TS("failure: contains invalid character");
        googleId = "teammates.$instr";
        Assumption.assertFalse(StringHelper.isMatching(googleId, REGEX_GOOGLE_ID_NON_EMAIL));
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
