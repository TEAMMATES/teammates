package teammates.test.cases.datatransfer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

    private FieldValidator fieldValidator = new FieldValidator();
    private List<String> addressReceiverListString = Arrays.asList("example1@test.com", "example2@test.com");
    private List<String> groupReceiverListFileKey = Arrays.asList("listfilekey", "listfilekey");
    private String subject = "subject of email";
    private Text content = new Text("valid email content");
    private Date date = new Date();
    private AdminEmailAttributes adminEmailAttributes = new AdminEmailAttributes(
            subject, addressReceiverListString, groupReceiverListFileKey, content, date);

    @Test
    public void testValidate() throws Exception {
        ______TS("valid admin email");

        assertTrue("Valid input", adminEmailAttributes.isValid());
        List<String> errorList = adminEmailAttributes.getInvalidityInfo();
        assertTrue("Valid input should return an empty list of errors", errorList.isEmpty());

        ______TS("success: subject max length");

        String subjectMaxLenString = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);
        AdminEmailAttributes validAttributesSubjectLength = new AdminEmailAttributes(
                subjectMaxLenString, addressReceiverListString, groupReceiverListFileKey, content, date);

        assertTrue("Valid input", validAttributesSubjectLength.isValid());
        List<String> emailErrorList = validAttributesSubjectLength.getInvalidityInfo();
        assertTrue("Valid input should return an empty list of errors", emailErrorList.isEmpty());

        ______TS("failure: content cannot be empty");

        AdminEmailAttributes invalidAttributesContentEmpty = new AdminEmailAttributes(
                subject, addressReceiverListString, groupReceiverListFileKey, new Text(""), date);
        String expectedContentEmptyError = getPopulatedErrorMessage(
                FieldValidator.EMAIL_CONTENT_ERROR_MESSAGE, invalidAttributesContentEmpty.getContentValue(),
                FieldValidator.EMAIL_CONTENT_FIELD_NAME, FieldValidator.REASON_EMPTY, 0);
        assertEquals("Invalid content input should return appropriate error string", expectedContentEmptyError,
                StringHelper.toString(invalidAttributesContentEmpty.getInvalidityInfo()));
        assertFalse("Invalid input", invalidAttributesContentEmpty.isValid());

        ______TS("failure: subject cannot exceeds max length");

        AdminEmailAttributes attributes = adminEmailAttributes;

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
    public void testGetInvalidityInfoForEmailContent_null_throwException() {
        AdminEmailAttributes adminEmailAttributes = new AdminEmailAttributes(
                subject, addressReceiverListString, groupReceiverListFileKey, null, date);
        try {
            fieldValidator.getInvalidityInfoForEmailContent(adminEmailAttributes.content);
            signalFailureToDetectException("Did not throw the expected AssertionError for null Email Content");
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
    }

    @Test
    public void testGetInvalidityInfoForEmailSubject_null_throwException() {
        AdminEmailAttributes adminEmailAttributes = new AdminEmailAttributes(
                null, addressReceiverListString, groupReceiverListFileKey, content, date);
        try {
            fieldValidator.getInvalidityInfoForEmailSubject(adminEmailAttributes.subject);
            signalFailureToDetectException("Did not throw the expected AssertionError for null Email Subject");
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
    }

    @Test
    public void testGetIdentificationString() {
        assertEquals(date + "/" + subject, adminEmailAttributes.getIdentificationString());
    }

    @Test
    public void testToEntity() {
        AdminEmail adminEmail = adminEmailAttributes.toEntity();
        assertEquals(adminEmailAttributes.subject, adminEmail.getSubject());
        assertEquals(adminEmailAttributes.addressReceiver, adminEmail.getAddressReceiver());
        assertEquals(adminEmailAttributes.groupReceiver, adminEmail.getGroupReceiver());
        assertEquals(adminEmailAttributes.content, adminEmail.getContent());
        assertEquals(adminEmailAttributes.sendDate, adminEmail.getSendDate());
    }

    @Test
    public void testSanitizeForSaving() {
        String subjectWithWhitespaces = " subject to be sanitized by removing leading/trailing whitespace ";
        Text contentWithWhitespaces = new Text(" content to be sanitized by removing leading/trailing whitespace ");

        ______TS("valid sanitation of admin email");

        AdminEmailAttributes adminEmailAttributes = new AdminEmailAttributes(
                subjectWithWhitespaces, addressReceiverListString, groupReceiverListFileKey, contentWithWhitespaces, date);

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
        Calendar calendar = formatDateForAdminEmailAttributesTest(adminEmailAttributes.sendDate);
        String expectedDate = TimeHelper.formatTime12H(calendar.getTime());
        String actualDate = adminEmailAttributes.getSendDateForDisplay();
        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testCreateDateForDisplay() {
        adminEmailAttributes.createDate = new Date();
        Calendar calendar = formatDateForAdminEmailAttributesTest(adminEmailAttributes.createDate);
        String expectedDate = TimeHelper.formatTime12H(calendar.getTime());
        String actualDate = adminEmailAttributes.getCreateDateForDisplay();
        assertEquals(expectedDate, actualDate);
    }

    private Calendar formatDateForAdminEmailAttributesTest(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return TimeHelper.convertToUserTimeZone(calendar, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
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
