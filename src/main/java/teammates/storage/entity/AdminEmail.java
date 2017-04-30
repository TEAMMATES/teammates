package teammates.storage.entity;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * Represents emails composed by Admin.
 */
@PersistenceCapable
public class AdminEmail extends Entity {

    /**
     * The name of the primary key of this entity type.
     */
    @NotPersistent
    public static final String PRIMARY_KEY_NAME = getFieldWithPrimaryKeyAnnotation(AdminEmail.class);

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String emailId;

    @Persistent
    //this stores the address string eg."example1@test.com,example2@test.com...."
    private List<String> addressReceiver;

    @Persistent
    //this stores the blobkey string of the email list file uploaded to Google Cloud Storage
    private List<String> groupReceiver;

    @Persistent
    private String subject;

    @Persistent
    //For draft emails,this is null. For sent emails, this is not null;
    private Date sendDate;

    @Persistent
    private Date createDate;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Text content;

    @Persistent
    private boolean isInTrashBin;

    /**
     * Instantiates a new AdminEmail.
     * @param subject
     *          email subject
     * @param content
     *          html email content
     */
    public AdminEmail(List<String> addressReceiver, List<String> groupReceiver, String subject,
                      Text content, Date sendDate) {
        this.emailId = null;
        this.addressReceiver = addressReceiver;
        this.groupReceiver = groupReceiver;
        this.subject = subject;
        this.content = content;
        this.sendDate = sendDate;
        this.createDate = new Date();
        this.isInTrashBin = false;
    }

    public void setAddressReceiver(List<String> receiver) {
        this.addressReceiver = receiver;
    }

    public void setGroupReceiver(List<String> receiver) {
        this.groupReceiver = receiver;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(Text content) {
        this.content = content;
    }

    public void setIsInTrashBin(boolean isInTrashBin) {
        this.isInTrashBin = isInTrashBin;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public List<String> getAddressReceiver() {
        return this.addressReceiver;
    }

    public List<String> getGroupReceiver() {
        return this.groupReceiver;
    }

    public String getSubject() {
        return this.subject;
    }

    public Date getSendDate() {
        return this.sendDate;
    }

    public Text getContent() {
        return this.content;
    }

    public boolean getIsInTrashBin() {
        return this.isInTrashBin;
    }

    public Date getCreateDate() {
        return this.createDate;
    }
}
