package teammates.test.cases.datatransfer;

import static teammates.common.datatransfer.attributes.AdminEmailAttributes.AdminEmailAttributesBuilder;

import java.util.Arrays;
import java.util.Collections;
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
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link AdminEmailAttributes}.
 */
public class AdminEmailAttributesTest extends BaseTestCase {

    private static final Date DEFAULT_DATE = AdminEmailAttributes.DEFAULT_DATE;
    private static final boolean DEFAULT_IS_IN_TRASH_BIN = AdminEmailAttributes.DEFAULT_IS_IN_TRASH_BIN;
    private static final String DEFAULT_EMAIL_ID = AdminEmailAttributes.DEFAULT_EMAIL_ID;
    private String subject;
    private List<String> addressReceiverListString;
    private List<String> groupReceiverListFileKey;
    private Text content;
    private Date sendDate;
    private Date createDate;
    private String emailId;
    private boolean isInTrashBin;

    @BeforeClass
    public void classSetup() {
        subject = Const.ParamsNames.ADMIN_EMAIL_SUBJECT;
        content = new Text(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        emailId = null;
        isInTrashBin = false;
        sendDate = new Date();
        createDate = new Date();
        addressReceiverListString =
                Arrays.asList("example1@test.com", "example2@test.com");
        groupReceiverListFileKey =
                Collections.singletonList(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);
    }

    @Test
    public void testValidate() throws Exception {
        AdminEmailAttributes validAttributes = createValidAdminEmailAttributesObject();
        assertTrue("valid value", validAttributes.isValid());

        AdminEmailAttributes invalidAttributes = createInvalidAdminEmailAttributesObject();

        String expectedError =
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_CONTENT_ERROR_MESSAGE, invalidAttributes.getContentValue(),
                        FieldValidator.EMAIL_CONTENT_FIELD_NAME, FieldValidator.REASON_EMPTY) + Const.EOL
                + getPopulatedErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, invalidAttributes.getSubject(),
                        FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                        FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);

        assertFalse("all valid values", invalidAttributes.isValid());
        assertEquals("all valid values", expectedError,
                StringHelper.toString(invalidAttributes.getInvalidityInfo()));

    }

    @Test
    public void testBuilderWithRequiredValues() {
        AdminEmailAttributes attributes = new AdminEmailAttributesBuilder(
                subject, addressReceiverListString, groupReceiverListFileKey, content, sendDate)
                .build();
        assertEquals(subject, attributes.getSubject());
        assertEquals(addressReceiverListString, attributes.getAddressReceiver());
        assertEquals(groupReceiverListFileKey, attributes.getGroupReceiver());
        assertEquals(content, attributes.content);
        assertEquals(sendDate, attributes.getSendDate());
    }

    @Test
    public void testBuilderWithDefaultOptionalValues() {
        AdminEmailAttributes attributes = new AdminEmailAttributesBuilder(
                subject, addressReceiverListString, groupReceiverListFileKey, content, sendDate)
                .build();
        assertEquals(DEFAULT_EMAIL_ID, attributes.getEmailId());
        assertEquals(DEFAULT_IS_IN_TRASH_BIN, attributes.getIsInTrashBin());
        assertEquals(DEFAULT_DATE, attributes.getCreateDate());
    }

    @Test
    public void testBuilderWithNullArguments() {
        AdminEmailAttributes attributes = new AdminEmailAttributesBuilder(
                null, null, null, null, null)
                .withCreateDate(null)
                .withEmailId(null)
                .withIsInTrashBin(null)
                .build();
        // No default values for required params
        assertNull(attributes.getSubject());
        assertNull(attributes.getAddressReceiver());
        assertNull(attributes.getGroupReceiver());
        assertNull(attributes.content);
        assertNull(attributes.getSendDate());

        // Check default values for optional params
        assertEquals(DEFAULT_EMAIL_ID, attributes.getEmailId());
        assertEquals(DEFAULT_IS_IN_TRASH_BIN, attributes.getIsInTrashBin());
        assertEquals(DEFAULT_DATE, attributes.getCreateDate());
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
    public void testSanitizeForSaving() {
        AdminEmailAttributes actualAdminEmail = createValidAdminEmailAttributesObject();
        AdminEmailAttributes expectedAdminEmail = createValidAdminEmailAttributesObject();
        actualAdminEmail.sanitizeForSaving();

        assertEquals(SanitizationHelper.sanitizeTextField(expectedAdminEmail.subject), actualAdminEmail.subject);
        assertEquals(SanitizationHelper.sanitizeForRichText(expectedAdminEmail.content), actualAdminEmail.content);
    }

    @Test
    public void testGetValidityInfo() {
        //already tested in testValidate() above
    }

    private AdminEmailAttributes createInvalidAdminEmailAttributesObject() {
        String veryLongSubj = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_SUBJECT_MAX_LENGTH + 1);
        Text emptyContent = new Text("");

        return new AdminEmailAttributesBuilder(
                veryLongSubj, addressReceiverListString, groupReceiverListFileKey, emptyContent, new Date())
                .build();
    }

    private AdminEmailAttributes createValidAdminEmailAttributesObject() {
        return new AdminEmailAttributesBuilder(
                subject, addressReceiverListString, groupReceiverListFileKey, content, sendDate)
                .withCreateDate(createDate)
                .withEmailId(emailId)
                .withIsInTrashBin(isInTrashBin)
                .build();
    }

}
