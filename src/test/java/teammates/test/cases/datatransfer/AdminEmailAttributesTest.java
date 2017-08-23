package teammates.test.cases.datatransfer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.testng.annotations.BeforeClass;
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
    private AdminEmailAttributes adminEmailAttributes;
    private List<String> addressReceiverListString = Arrays.asList("example1@test.com", "example2@test.com");
    private List<String> groupReceiverListFileKey = Arrays.asList("listfilekey", "listfilekey");
    private String subject = "subject of email";
    private Text content = new Text("valid email content");
    private Date date = new Date();

    @BeforeClass
    public void classSetup() {
        adminEmailAttributes = new AdminEmailAttributes(
                subject, addressReceiverListString, groupReceiverListFileKey, content, date);
        adminEmailAttributes.createDate = new Date();
    }

    @Test
    public void testValidate() throws Exception {

        // Valid input
        assertTrue("Valid input", adminEmailAttributes.isValid());
        List<String> errorList = adminEmailAttributes.getInvalidityInfo();
        assertTrue("Valid input should return an empty list of errors", errorList.isEmpty());

        // Content cannot be  empty
        Text emptyContent = new Text("");
        AdminEmailAttributes invalidAttributesContentEmpty = new AdminEmailAttributes(
                subject, addressReceiverListString, groupReceiverListFileKey, emptyContent, date);

        String expectedContentEmptyError = getPopulatedErrorMessage(
                FieldValidator.EMAIL_CONTENT_ERROR_MESSAGE, invalidAttributesContentEmpty.getContentValue(),
                FieldValidator.EMAIL_CONTENT_FIELD_NAME, FieldValidator.REASON_EMPTY,
                0);

        assertEquals("Invalid content input should return appropriate error string", expectedContentEmptyError,
                StringHelper.toString(invalidAttributesContentEmpty.getInvalidityInfo()));

        String expectedStringContentError = fieldValidator.getInvalidityInfoForEmailContent(emptyContent);
        assertEquals(FieldValidator.EMAIL_CONTENT_ERROR_MESSAGE, expectedStringContentError);

        assertFalse("Invalid input", invalidAttributesContentEmpty.isValid());

        // Subject cannot exceeds max length
        String veryLongSubj = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_SUBJECT_MAX_LENGTH + 1);
        AdminEmailAttributes invalidAttributesSubjectLength = new AdminEmailAttributes(
                veryLongSubj, addressReceiverListString, groupReceiverListFileKey, content, date);

        String expectedEmptySubjectLengthError = getPopulatedErrorMessage(
                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, invalidAttributesSubjectLength.getSubject(),
                FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);

        assertEquals("Invalid subject input should return appropriate error string",
                expectedEmptySubjectLengthError, StringHelper.toString(invalidAttributesSubjectLength.getInvalidityInfo()));

        // Subject must start with alphanumeric character
        String invalidSubjectChars = "_InvalidSubject";
        AdminEmailAttributes invalidAttributesSubjectChars = new AdminEmailAttributes(
                invalidSubjectChars, addressReceiverListString, groupReceiverListFileKey, content, date);

        String expectedSubjectCharsError = getPopulatedErrorMessage(
                FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidAttributesSubjectChars.getSubject(),
                FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR,
                FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);

        assertEquals("Invalid subject input should return appropriate error string",
                expectedSubjectCharsError, StringHelper.toString(invalidAttributesSubjectChars.getInvalidityInfo()));

        assertFalse("Invalid input", invalidAttributesSubjectChars.isValid());

        // Subject cannot contain vertical bar (|)
        String invalidSubjectWithBar = "Invalid|Subject";
        AdminEmailAttributes invalidAttributesSubjectWithBar = new AdminEmailAttributes(
                invalidSubjectWithBar, addressReceiverListString, groupReceiverListFileKey, content, date);

        String expectedSubjectWithBarError = getPopulatedErrorMessage(
                FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidAttributesSubjectWithBar.getSubject(),
                FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_CONTAINS_INVALID_CHAR,
                FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);

        assertEquals("Invalid subject input should return appropriate error string",
                expectedSubjectWithBarError, StringHelper.toString(invalidAttributesSubjectWithBar.getInvalidityInfo()));

        assertFalse("Invalid input", invalidAttributesSubjectWithBar.isValid());


        // Subject cannot contain percent sign(%)
        String invalidSubjectWithPercentSign = "Invalid%Subject";
        AdminEmailAttributes invalidAttributesSubjectWithPercentSign = new AdminEmailAttributes(
                invalidSubjectWithPercentSign, addressReceiverListString, groupReceiverListFileKey, content, date);

        String expectedSubjectWithPercentSignError = getPopulatedErrorMessage(
                FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidAttributesSubjectWithPercentSign.getSubject(),
                FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_CONTAINS_INVALID_CHAR,
                FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);

        assertEquals("Invalid subject input should return appropriate error string", expectedSubjectWithPercentSignError,
                StringHelper.toString(invalidAttributesSubjectWithPercentSign.getInvalidityInfo()));

        assertFalse("Invalid input", invalidAttributesSubjectWithPercentSign.isValid());

        // Valid subject
        invalidAttributesSubjectWithPercentSign.subject = subject;
        assertTrue("With objects subject change to valid", invalidAttributesSubjectWithPercentSign.isValid());

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
        String emailSubject = " subject to be sanitized by removing leading/trailing whitespace ";
        Text emailContent = new Text(" content to be sanitized by removing leading/trailing whitespace ");

        AdminEmailAttributes adminEmailAttributes = new AdminEmailAttributes(
                emailSubject, addressReceiverListString, groupReceiverListFileKey, emailContent, date);

        adminEmailAttributes.sanitizeForSaving();

        assertEquals("subject to be sanitized by removing leading/trailing whitespace",
                adminEmailAttributes.getSubject());

        assertEquals("content to be sanitized by removing leading/trailing whitespace",
                adminEmailAttributes.getContentValue());
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

}
