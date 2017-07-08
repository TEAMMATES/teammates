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
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link AdminEmailAttributes}.
 */
public class AdminEmailAttributesTest extends BaseTestCase {

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

        String veryLongSubj = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_SUBJECT_MAX_LENGTH + 1);
        Text emptyContent = new Text("");
        AdminEmailAttributes invalidAttributes = new AdminEmailAttributesBuilder(
                veryLongSubj, addressReceiverListString, groupReceiverListFileKey, emptyContent, new Date())
                .build();

        String expectedError =
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_CONTENT_ERROR_MESSAGE, invalidAttributes.getContentValue(),
                        String.valueOf(emptyContent), FieldValidator.REASON_EMPTY) + Const.EOL
                + getPopulatedErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, veryLongSubj,
                        FieldValidator.EMAIL_SUBJECT_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                        FieldValidator.EMAIL_SUBJECT_MAX_LENGTH);

        assertFalse("all valid values", invalidAttributes.isValid());
        assertEquals("all valid values", expectedError,
                StringHelper.toString(invalidAttributes.getInvalidityInfo()));

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
