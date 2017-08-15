package teammates.test.cases.datatransfer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.storage.entity.AdminEmail;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link AdminEmailAttributes}.
 */
public class AdminEmailAttributesTest extends BaseAttributesTest{

    private String subject = Const.ParamsNames.ADMIN_EMAIL_SUBJECT;
    private Text content = new Text(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
    private Date sendDate = new Date();
    private List<String> addressReceiverListString = Arrays.asList("example1@test.com", "example2@test.com");
    private List<String> groupReceiverListFileKey =
            Collections.singletonList(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);

    @Test
    public void testValidate() throws Exception {
        AdminEmailAttributes validAttributes = createValidAdminEmailAttributesObject();
        assertTrue("valid value", validAttributes.isValid());

        AdminEmailAttributes invalidAttributes = createInvalidAdminEmailAttributesObject();

        String expectedError =
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_CONTENT_ERROR_MESSAGE, invalidAttributes.getContentValue(),
                        FieldValidator.EMAIL_CONTENT_FIELD_NAME, FieldValidator.REASON_EMPTY,
                        FieldValidator.EMAIL_SUBJECT_MAX_LENGTH) + Const.EOL
                        + getPopulatedErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, invalidAttributes.getSubject(),
                        FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                        FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);

        assertFalse("all valid values", invalidAttributes.isValid());
        assertEquals("all valid values", expectedError,
                StringHelper.toString(invalidAttributes.getInvalidityInfo()));

    }

    @Test
    public void testGetIdentificationString() {
        AdminEmailAttributes adminEmail = createValidAdminEmailAttributesObject();
        assertEquals(this.sendDate + "/" + this.subject, adminEmail.getIdentificationString());
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
        String veryLongSubj = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_SUBJECT_MAX_LENGTH + 1);
        Text emptyContent = new Text("");

        return new AdminEmailAttributes(
                veryLongSubj, addressReceiverListString, groupReceiverListFileKey, emptyContent, new Date());

    }

    private AdminEmailAttributes createValidAdminEmailAttributesObject() {
        return new AdminEmailAttributes(subject, addressReceiverListString, groupReceiverListFileKey, content, sendDate);

    }
}
