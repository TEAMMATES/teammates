package teammates.test.cases.datatransfer;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.storage.entity.AdminEmail;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link AdminEmailAttributes}.
 */
public class AdminEmailAttributesTest extends BaseTestCase {

    private List<String> addressReceiverListString = Arrays.asList("example1@test.com", "example2@test.com");
    private List<String> groupReceiverListFileKey = Arrays.asList("listfilekey", "listfilekey");
    private String subject = "subject of email";
    private Text content = new Text("valid email content");
    private Date date = new Date();

    @Test
    public void testBuilderWithRequiredValues() {
        AdminEmailAttributes attributes = AdminEmailAttributes
                .builder(subject, addressReceiverListString, groupReceiverListFileKey, content, date)
                .build();
        assertEquals(subject, attributes.getSubject());
        assertEquals(addressReceiverListString, attributes.getAddressReceiver());
        assertEquals(groupReceiverListFileKey, attributes.getGroupReceiver());
        assertEquals(content, attributes.content);
        assertEquals(date, attributes.getSendDate());
    }

    @Test
    public void testBuilderWithOptionalDefaultValues() {
        AdminEmailAttributes attributes = AdminEmailAttributes
                .builder(subject, addressReceiverListString, groupReceiverListFileKey, content, date)
                .build();
        assertEquals(Const.ParamsNames.ADMIN_EMAIL_ID, attributes.getEmailId());
        assertEquals(false, attributes.getIsInTrashBin());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, attributes.getCreateDate());
    }

    @Test
    public void testBuilderWithNullArguments() {
        AdminEmailAttributes attributes = AdminEmailAttributes
                .builder(null, null, null, null, null)
                .withCreateDate(null)
                .withEmailId(null)
                .withIsInTrashBin(null)
                .build();
        // No default values for required params
        assertNull(attributes.subject);
        assertNull(attributes.addressReceiver);
        assertNull(attributes.groupReceiver);
        assertNull(attributes.content);
        assertNull(attributes.sendDate);

        // Check default values for optional params
        assertEquals(Const.ParamsNames.ADMIN_EMAIL_ID, attributes.getEmailId());
        assertEquals(false, attributes.getIsInTrashBin());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, attributes.getCreateDate());
    }

    @Test
    public void testToEntity() {
        AdminEmailAttributes adminEmailAttributes = createValidAdminEmailAttributesObject();
        AdminEmail adminEmail = adminEmailAttributes.toEntity();
        assertEquals(adminEmailAttributes.addressReceiver, adminEmail.getAddressReceiver());
        assertEquals(adminEmailAttributes.groupReceiver, adminEmail.getGroupReceiver());
        assertEquals(adminEmailAttributes.content, adminEmail.getContent());
        assertEquals(adminEmailAttributes.sendDate, adminEmail.getSendDate());
    }

    private AdminEmailAttributes createValidAdminEmailAttributesObject() {
        return AdminEmailAttributes
                .builder(subject, addressReceiverListString, groupReceiverListFileKey, content, date)
                .withCreateDate(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP)
                .withEmailId(Const.ParamsNames.ADMIN_EMAIL_ID)
                .withIsInTrashBin(false)
                .build();
    }

}
