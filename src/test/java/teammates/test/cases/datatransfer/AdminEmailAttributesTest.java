package teammates.test.cases.datatransfer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.AdminEmail;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link AdminEmailAttributes}.
 */
public class AdminEmailAttributesTest extends BaseAttributesTest {

    private List<String> addressReceiverListString = Arrays.asList("example1@test.com", "example2@test.com");
    private List<String> groupReceiverListFileKey = Arrays.asList("listfilekey", "listfilekey");
    private String subject = "subject of email";
    private Text content = new Text("valid email content");
    private Instant date = Instant.now();
    private AdminEmailAttributes validAdminEmailAttributesObject = AdminEmailAttributes
            .builder(subject, addressReceiverListString, groupReceiverListFileKey, content)
            .build();

    @Test
    public void testBuilderWithDefaultValues() {
        ______TS("valid admin email");

        AdminEmailAttributes attributesWithDefaultValues = AdminEmailAttributes
                .builder(subject, addressReceiverListString, groupReceiverListFileKey, content)
                .build();

        ______TS("success: default values for optional params");

        assertEquals(Const.ParamsNames.ADMIN_EMAIL_ID, attributesWithDefaultValues.getEmailId());
        assertFalse("Default false for isInTrashBin", attributesWithDefaultValues.isInTrashBin);
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, attributesWithDefaultValues.getCreateDate());
        assertTrue("Valid input", attributesWithDefaultValues.isValid());
    }

    @Test
    public void testBuilderWithNullOptionalArguments() {
        AdminEmailAttributes attributesWithNullOptionalArguments = AdminEmailAttributes
                .builder(subject, addressReceiverListString, groupReceiverListFileKey, content)
                .withSendDate(null)
                .withCreateDate(null)
                .withEmailId(null)
                .build();

        ______TS("valid admin email");

        assertTrue("Valid input", attributesWithNullOptionalArguments.isValid());

        ______TS("success: default values for optional params");

        assertEquals(Const.ParamsNames.ADMIN_EMAIL_ID, attributesWithNullOptionalArguments.getEmailId());
        assertFalse("Default false for isInTrashBin", attributesWithNullOptionalArguments.isInTrashBin);
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, attributesWithNullOptionalArguments.getCreateDate());
        assertEquals(null, attributesWithNullOptionalArguments.getSendDate());
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = Const.StatusCodes.NULL_PARAMETER)
    public void testBuilderWithNullRequiredSubjectParam() {
        ______TS("failure: subject cannot be null)");

        AdminEmailAttributes
                .builder(null, addressReceiverListString, groupReceiverListFileKey, content)
                .build();
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = Const.StatusCodes.NULL_PARAMETER)
    public void testBuilderWithNullRequiredAddressReceiverParam() {
        ______TS("failure: addressReceiverListString cannot be null)");

        AdminEmailAttributes
                .builder(subject, null, groupReceiverListFileKey, content)
                .build();
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = Const.StatusCodes.NULL_PARAMETER)
    public void testBuilderWithNullRequiredGroupReceiverParam() {
        ______TS("failure: groupReceiverListFileKey cannot be null)");

        AdminEmailAttributes
                .builder(subject, addressReceiverListString, null, content)
                .build();
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = Const.StatusCodes.NULL_PARAMETER)
    public void testBuilderWithNullRequiredContentParam() {
        ______TS("failure: content cannot be null)");

        AdminEmailAttributes
                .builder(subject, addressReceiverListString, groupReceiverListFileKey, null)
                .build();
    }

    @Test
    public void testValueOf() {
        AdminEmail adminEmail = new AdminEmail(
                groupReceiverListFileKey, addressReceiverListString, subject, content, date);

        AdminEmailAttributes adminEmailAttributes = AdminEmailAttributes.valueOf(adminEmail);

        assertEquals(adminEmail.getGroupReceiver(), adminEmailAttributes.groupReceiver);
        assertEquals(adminEmail.getAddressReceiver(), adminEmailAttributes.addressReceiver);
        assertEquals(adminEmail.getSubject(), adminEmailAttributes.subject);
        assertEquals(adminEmail.getContent(), adminEmailAttributes.content);
        assertEquals(adminEmail.getSendDate(), adminEmailAttributes.sendDate);
        assertTrue("Valid input", adminEmailAttributes.isValid());
    }

    @Test
    public void testValidate() throws Exception {
        ______TS("valid admin email");

        assertTrue("Valid input", validAdminEmailAttributesObject.isValid());
        List<String> errorList = validAdminEmailAttributesObject.getInvalidityInfo();
        assertTrue("Valid input should return an empty list of errors", errorList.isEmpty());

        ______TS("success: subject max length");

        String subjectMaxLenString = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);
        AdminEmailAttributes validAttributesSubjectLength = AdminEmailAttributes
                .builder(subjectMaxLenString, addressReceiverListString, groupReceiverListFileKey, content)
                .withSendDate(date)
                .build();

        assertTrue("Valid input", validAttributesSubjectLength.isValid());
        List<String> emailErrorList = validAttributesSubjectLength.getInvalidityInfo();
        assertTrue("Valid input should return an empty list of errors", emailErrorList.isEmpty());

        ______TS("failure: content cannot be empty");

        AdminEmailAttributes invalidAttributesContentEmpty = AdminEmailAttributes
                .builder(subject, addressReceiverListString, groupReceiverListFileKey, new Text(""))
                .withSendDate(date)
                .build();
        String expectedContentEmptyError = getPopulatedErrorMessage(
                FieldValidator.EMAIL_CONTENT_ERROR_MESSAGE, invalidAttributesContentEmpty.getContentValue(),
                FieldValidator.EMAIL_CONTENT_FIELD_NAME, FieldValidator.REASON_EMPTY, 0);
        assertEquals("Invalid content input should return appropriate error string", expectedContentEmptyError,
                StringHelper.toString(invalidAttributesContentEmpty.getInvalidityInfo()));
        assertFalse("Invalid input", invalidAttributesContentEmpty.isValid());

        ______TS("failure: subject cannot exceeds max length");

        AdminEmailAttributes attributes = validAdminEmailAttributesObject;

        attributes.subject = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_SUBJECT_MAX_LENGTH + 1);
        assertEquals(attributes.subject.length(), 201);
        String expectedEmptySubjectLengthError = getInvalidityInfoForSubject(attributes.subject);

        assertEquals("Invalid subject input should return appropriate error string",
                expectedEmptySubjectLengthError, StringHelper.toString(attributes.getInvalidityInfo()));
        assertFalse("Invalid input", attributes.isValid());

        ______TS("failure: subject must start with alphanumeric character");

        attributes.subject = "_InvalidSubject";
        assertTrue(attributes.subject.startsWith("_"));
        String expectedSubjectCharsError = getInvalidityInfoForSubject(attributes.subject);

        assertEquals("Invalid subject input should return appropriate error string",
                expectedSubjectCharsError, StringHelper.toString(attributes.getInvalidityInfo()));
        assertFalse("Invalid input", attributes.isValid());

        ______TS("failure: subject cannot contain vertical bar (|)");

        attributes.subject = "Invalid|Subject";
        assertTrue(attributes.subject.contains("|"));
        String expectedSubjectWithBarError = getInvalidityInfoForSubject(attributes.subject);

        assertEquals("Invalid subject input should return appropriate error string",
                expectedSubjectWithBarError, StringHelper.toString(attributes.getInvalidityInfo()));
        assertFalse("Invalid input", attributes.isValid());

        ______TS("failure: subject cannot contain percent sign(%)");

        attributes.subject = "Invalid%Subject";
        assertTrue(attributes.subject.contains("%"));
        String expectedSubjectWithPercentError = getInvalidityInfoForSubject(attributes.subject);

        assertEquals("Invalid subject input should return appropriate error string", expectedSubjectWithPercentError,
                StringHelper.toString(attributes.getInvalidityInfo()));
        assertFalse("Invalid input", attributes.isValid());
    }

    @Test
    public void testGetIdentificationString() {
        assertEquals(validAdminEmailAttributesObject.sendDate + "/" + validAdminEmailAttributesObject.subject,
                validAdminEmailAttributesObject.getIdentificationString());
    }

    @Test
    public void testToEntity() {
        AdminEmail adminEmail = validAdminEmailAttributesObject.toEntity();
        assertEquals(validAdminEmailAttributesObject.subject, adminEmail.getSubject());
        assertEquals(validAdminEmailAttributesObject.addressReceiver, adminEmail.getAddressReceiver());
        assertEquals(validAdminEmailAttributesObject.groupReceiver, adminEmail.getGroupReceiver());
        assertEquals(validAdminEmailAttributesObject.content, adminEmail.getContent());
        assertEquals(validAdminEmailAttributesObject.sendDate, adminEmail.getSendDate());
    }

    @Test
    public void testSanitizeForSaving() {
        String subjectWithWhitespaces = " subject to be sanitized by removing leading/trailing whitespace ";
        Text contentWithWhitespaces = new Text(" content to be sanitized by removing leading/trailing whitespace ");

        ______TS("valid sanitation of admin email");

        AdminEmailAttributes adminEmailAttributes = AdminEmailAttributes
                .builder(subjectWithWhitespaces, addressReceiverListString, groupReceiverListFileKey, contentWithWhitespaces)
                .withSendDate(date)
                .build();

        ______TS("success: sanitized whitespace");

        adminEmailAttributes.sanitizeForSaving();
        assertEquals("subject to be sanitized by removing leading/trailing whitespace",
                adminEmailAttributes.getSubject());
        assertEquals("content to be sanitized by removing leading/trailing whitespace",
                adminEmailAttributes.getContentValue());

        ______TS("success: sanitized code block");

        adminEmailAttributes.content = new Text("<code>System.out.println(\"Hello World\");</code>");
        adminEmailAttributes.sanitizeForSaving();
        assertEquals("<code>System.out.println(&#34;Hello World&#34;);</code>", adminEmailAttributes.getContentValue());

        ______TS("success: sanitized superscript");

        adminEmailAttributes.content = new Text("f(x) = x<sup>2</sup>");
        adminEmailAttributes.sanitizeForSaving();
        assertEquals("f(x) &#61; x<sup>2</sup>", adminEmailAttributes.getContentValue());

        ______TS("success: sanitized chemical formula");

        adminEmailAttributes.content = new Text("<p>Chemical formula: C<sub>6</sub>H<sub>12</sub>O<sub>6</sub></p>");
        adminEmailAttributes.sanitizeForSaving();
        assertEquals("<p>Chemical formula: C<sub>6</sub>H<sub>12</sub>O<sub>6</sub></p>",
                adminEmailAttributes.getContentValue());

        ______TS("success: sanitized invalid closing tag");

        adminEmailAttributes.content = new Text("</td></option></div> invalid closing tags");
        adminEmailAttributes.sanitizeForSaving();
        assertEquals(" invalid closing tags", adminEmailAttributes.getContentValue());
    }

    @Test
    public void testSendDateForDisplay() {
        validAdminEmailAttributesObject.sendDate = Instant.now();
        String expectedDate = TimeHelper.formatDateTimeForDisplay(
                convertToAdminTime(validAdminEmailAttributesObject.sendDate));
        String actualDate = validAdminEmailAttributesObject.getSendDateForDisplay();
        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testCreateDateForDisplay() {
        validAdminEmailAttributesObject.createDate = Instant.now();
        String expectedDate = TimeHelper.formatDateTimeForDisplay(
                convertToAdminTime(validAdminEmailAttributesObject.createDate));
        String actualDate = validAdminEmailAttributesObject.getCreateDateForDisplay();
        assertEquals(expectedDate, actualDate);
    }

    private LocalDateTime convertToAdminTime(Instant date) {
        return TimeHelper.convertInstantToLocalDateTime(date, Const.SystemParams.ADMIN_TIME_ZONE);
    }

    private String getInvalidityInfoForSubject(String emailSubject) throws Exception {
        if (!Character.isLetterOrDigit(emailSubject.codePointAt(0))) {
            return getPopulatedErrorMessage(
                    FieldValidator.INVALID_NAME_ERROR_MESSAGE, emailSubject,
                    FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR,
                    FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);
        }
        if (!StringHelper.isMatching(emailSubject, FieldValidator.REGEX_NAME)) {
            return getPopulatedErrorMessage(
                    FieldValidator.INVALID_NAME_ERROR_MESSAGE, emailSubject,
                    FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_CONTAINS_INVALID_CHAR,
                    FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);
        }
        if (emailSubject.length() > FieldValidator.EMAIL_SUBJECT_MAX_LENGTH) {
            return getPopulatedErrorMessage(
                    FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, emailSubject,
                    FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                    FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);
        }
        return "";
    }

}
