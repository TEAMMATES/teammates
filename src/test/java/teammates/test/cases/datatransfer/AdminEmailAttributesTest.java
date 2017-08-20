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

    private FieldValidator fieldValidator;
    private AdminEmailAttributes adminEmailAttributes;
    private List<String> addressReceiverListString;
    private List<String> groupReceiverListFileKey;
    private String subject;
    private Text content;
    private Date date;

    @BeforeClass
    public void classSetup() {
        addressReceiverListString = Arrays.asList("example1@test.com", "example2@test.com");
        groupReceiverListFileKey = Arrays.asList("listfilekey", "listfilekey");
        subject = "subject of email";
        content = new Text("valid email content");
        date = new Date();

        fieldValidator = new FieldValidator();

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

        // Content empty
        Text emptyContent = new Text("");
        AdminEmailAttributes invalidAttributesContentEmpty = new AdminEmailAttributes(
                subject, addressReceiverListString, groupReceiverListFileKey, emptyContent, date);

        String expectedContentLengthError = getPopulatedErrorMessage(
                FieldValidator.EMAIL_CONTENT_ERROR_MESSAGE, invalidAttributesContentEmpty.getContentValue(),
                FieldValidator.EMAIL_CONTENT_FIELD_NAME, FieldValidator.REASON_EMPTY,
                0);

        assertEquals("Invalid content input should return appropriate error string", expectedContentLengthError,
                StringHelper.toString(invalidAttributesContentEmpty.getInvalidityInfo()));

        String expectedStringContentError = fieldValidator.getInvalidityInfoForEmailContent(emptyContent);
        assertEquals(FieldValidator.EMAIL_CONTENT_ERROR_MESSAGE, expectedStringContentError);

        assertFalse("Invalid input", invalidAttributesContentEmpty.isValid());

        // Subject exceeds max length
        String veryLongSubj = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_SUBJECT_MAX_LENGTH + 1);
        AdminEmailAttributes invalidAttributesSubjectLength = new AdminEmailAttributes(
                veryLongSubj, addressReceiverListString, groupReceiverListFileKey, content, date);

        String expectedEmptySubjectLengthError = getPopulatedErrorMessage(
                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, invalidAttributesSubjectLength.getSubject(),
                FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);

        assertEquals("Invalid subject input should return appropriate error string",
                expectedEmptySubjectLengthError, StringHelper.toString(invalidAttributesSubjectLength.getInvalidityInfo()));

        // Subject Must start with alphanumeric character, cannot contain vertical bar(|) or percent sign(%).
        String invalidSubjectChars = "%Invalid%Subject|";
        AdminEmailAttributes invalidAttributesSubjectChars = new AdminEmailAttributes(
                invalidSubjectChars, addressReceiverListString, groupReceiverListFileKey, content, date);

        String expectedError =
                "\"" + invalidSubjectChars + "\" is not acceptable to TEAMMATES as a/an email subject because "
                        + "it starts with a non-alphanumeric character. All email subject must start with an "
                        + "alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%).";

        assertEquals("Invalid subject input should return appropriate error string",
                expectedError, StringHelper.toString(invalidAttributesSubjectChars.getInvalidityInfo()));

        assertFalse("Invalid input", invalidAttributesSubjectChars.isValid());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAttributesForNull() throws Exception {
        AdminEmailAttributes invalidAttributesNullSubject = new AdminEmailAttributes(
                null, addressReceiverListString, groupReceiverListFileKey, content, date);
        assertNull(invalidAttributesNullSubject.getSubject());
        assertEquals(invalidAttributesNullSubject.getSubject(), null);

        AdminEmailAttributes invalidAttributesNullContent = new AdminEmailAttributes(
                subject, addressReceiverListString, groupReceiverListFileKey, null, date);
        assertNull(invalidAttributesNullContent.getContentValue());
        assertFalse("Invalid input", invalidAttributesNullContent.isValid());
    }

    @Test
    public void testSendDateForDisplay() {
        Calendar calendar = formatDate(adminEmailAttributes.sendDate);
        String expectedDate = TimeHelper.formatTime12H(calendar.getTime());
        String actualDate = adminEmailAttributes.getSendDateForDisplay();

        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testCreateDateForDisplay() {
        Calendar calendar = formatDate(adminEmailAttributes.createDate);
        String expectedDate = TimeHelper.formatTime12H(calendar.getTime());
        String actualDate = adminEmailAttributes.getCreateDateForDisplay();

        assertEquals(expectedDate, actualDate);
    }

    /**
     * Format Date for tests in this class.
     */
    public Calendar formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return TimeHelper.convertToUserTimeZone(calendar, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
    }

    @Test
    public void testGetIdentificationString() {
        assertEquals(date + "/" + "subject of email", adminEmailAttributes.getIdentificationString());
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

}
