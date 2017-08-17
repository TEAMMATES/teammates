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
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.AdminEmail;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link AdminEmailAttributes}.
 */
public class AdminEmailAttributesTest extends BaseAttributesTest {

    private AdminEmailAttributes emailAttributes;

    @BeforeClass
    public void classSetup() {
        emailAttributes = createValidAdminEmailAttributesObject();
        emailAttributes.createDate = new Date();
    }

    @Test
    public void testValidate() throws Exception {
        AdminEmailAttributes validAttributes = createValidAdminEmailAttributesObject();
        assertTrue("Valid input", validAttributes.isValid());

        List<String> actual = validAttributes.getInvalidityInfo();
        assertTrue("Valid input should return an empty list", actual.isEmpty());

        AdminEmailAttributes invalidAttributes = createInvalidAdminEmailAttributesObject();

        String expectedError =
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_CONTENT_ERROR_MESSAGE, invalidAttributes.getContentValue(),
                        FieldValidator.EMAIL_CONTENT_FIELD_NAME, FieldValidator.REASON_EMPTY,
                        0) + Const.EOL
                + getPopulatedErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, invalidAttributes.getSubject(),
                        FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                        FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);

        assertFalse("Valid input", invalidAttributes.isValid());
        assertEquals("Invalid input should return appropriate error string", expectedError,
                StringHelper.toString(invalidAttributes.getInvalidityInfo()));
    }

    @Test
    public void testGetValidityInfo() {
        //already tested in testValidate() above
    }

    @Test
    public void testSendDateForDisplay() {
        Calendar calendar = formatDate(emailAttributes.sendDate);
        String dateForDisplay = TimeHelper.formatTime12H(calendar.getTime());
        assertEquals(dateForDisplay, emailAttributes.getSendDateForDisplay());
    }

    @Test
    public void testCreateDateForDisplay() {
        Calendar calendar = formatDate(emailAttributes.createDate);
        String dateForDisplay = TimeHelper.formatTime12H(calendar.getTime());
        assertEquals(dateForDisplay, emailAttributes.getCreateDateForDisplay());
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
        AdminEmailAttributes adminEmail = createValidAdminEmailAttributesObject();
        assertEquals(new Date() + "/" + "subject of email", adminEmail.getIdentificationString());
    }

    @Test
    public void testGetEntityTypeAsString() {
        AdminEmailAttributes adminEmail = createValidAdminEmailAttributesObject();
        assertEquals("Admin Email", adminEmail.getEntityTypeAsString());
    }

    @Test
    public void testGetBackupIdentifier() {
        AdminEmailAttributes adminEmail = createValidAdminEmailAttributesObject();
        assertEquals("Admin Email", adminEmail.getBackupIdentifier());
    }

    @Test
    public void testToEntity() {
        AdminEmailAttributes adminEmailAttributes = createValidAdminEmailAttributesObject();
        AdminEmail adminEmail = adminEmailAttributes.toEntity();

        assertEquals(adminEmailAttributes.subject, adminEmail.getSubject());
        assertEquals(adminEmailAttributes.addressReceiver, adminEmail.getAddressReceiver());
        assertEquals(adminEmailAttributes.groupReceiver, adminEmail.getGroupReceiver());
        assertEquals(adminEmailAttributes.content, adminEmail.getContent());
        assertEquals(adminEmailAttributes.sendDate, adminEmail.getSendDate());
    }

    @Test
    public void testSanitizeForSaving() {
        AdminEmailAttributes actualAdminEmail = createValidAdminEmailAttributesObject();
        AdminEmailAttributes expectedAdminEmail = createValidAdminEmailAttributesObject();
        actualAdminEmail.sanitizeForSaving();

        assertEquals(SanitizationHelper.sanitizeTextField(expectedAdminEmail.subject), actualAdminEmail.subject);
        assertEquals(SanitizationHelper.sanitizeForRichText(expectedAdminEmail.content), actualAdminEmail.content);
    }

    private AdminEmailAttributes createInvalidAdminEmailAttributesObject() {
        List<String> addressReceiverListString = Arrays.asList("example1@test.com", "example2@test.com");
        List<String> groupReceiverListFileKey = Arrays.asList("listfilekey", "listfilekey");
        String veryLongSubj = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_SUBJECT_MAX_LENGTH + 1);
        Text emptyContent = new Text("");

        return new AdminEmailAttributes(
                veryLongSubj, addressReceiverListString, groupReceiverListFileKey, emptyContent, new Date());
    }

    private AdminEmailAttributes createValidAdminEmailAttributesObject() {
        List<String> addressReceiverListString = Arrays.asList("example1@test.com", "example2@test.com");
        List<String> groupReceiverListFileKey = Arrays.asList("listfilekey", "listfilekey");
        String validSubject = "subject of email";
        Text content = new Text("valid email content");

        return new AdminEmailAttributes(
                validSubject, addressReceiverListString, groupReceiverListFileKey, content, new Date());
    }

}
