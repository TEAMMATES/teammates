package teammates.test.cases.datatransfer;

import static teammates.common.datatransfer.attributes.AdminEmailAttributes.AdminEmailAttributesBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.storage.entity.AdminEmail;

/**
 * SUT: {@link AdminEmailAttributesTest}.
 */
public class AdminEmailAttributesTest extends BaseAttributesTest {

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
        emailId = Const.ParamsNames.ADMIN_EMAIL_ID;
        isInTrashBin = false;
        sendDate = new Date();
        createDate = new Date();
        addressReceiverListString = new ArrayList<>();
        groupReceiverListFileKey = new ArrayList<>();
        addressReceiverListString.add(Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS);
        groupReceiverListFileKey.add(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);
    }

    @Override
    @Test
    public void testToEntity() {
        AdminEmailAttributes adminEmailAttributes = createValidAdminEmailAttributesObject();
        AdminEmail expectedAdminEmail = new AdminEmail(
                        adminEmailAttributes.getAddressReceiver(), adminEmailAttributes.getGroupReceiver(),
                        adminEmailAttributes.getSubject(), adminEmailAttributes.content, adminEmailAttributes.sendDate);
        AdminEmail actualAdminEmail = AdminEmailAttributes.valueOf(expectedAdminEmail).toEntity();

        assertEquals(expectedAdminEmail.getSubject(), actualAdminEmail.getSubject());
        assertEquals(expectedAdminEmail.getAddressReceiver(), actualAdminEmail.getAddressReceiver());
        assertEquals(expectedAdminEmail.getGroupReceiver(), actualAdminEmail.getGroupReceiver());
        assertEquals(expectedAdminEmail.getContent(), actualAdminEmail.getContent());
        assertEquals(expectedAdminEmail.getSendDate(), actualAdminEmail.getSendDate());
    }

    private AdminEmailAttributes createValidAdminEmailAttributesObject() {
        return new AdminEmailAttributesBuilder(
                subject, addressReceiverListString, groupReceiverListFileKey, content, sendDate)
                .withIsInTrashBin(isInTrashBin)
                .withCreateDate(createDate)
                .withEmailId(emailId)
                .build();
    }

}
