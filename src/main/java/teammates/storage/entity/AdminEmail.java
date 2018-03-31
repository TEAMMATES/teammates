package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

import teammates.common.util.TimeHelper;

/**
 * Represents emails composed by Admin.
 */
@Entity
@Index
public class AdminEmail extends BaseEntity {

    @Id
    private Long emailId;

    //this stores the address string eg."example1@test.com,example2@test.com...."
    private List<String> addressReceiver = new ArrayList<>();

    //this stores the blobkey string of the email list file uploaded to Google Cloud Storage
    private List<String> groupReceiver = new ArrayList<>();

    private String subject;

    //For draft emails,this is null. For sent emails, this is not null;
    private Date sendDate;

    private Date createDate;

    @Unindex
    private Text content;

    private boolean isInTrashBin;

    @SuppressWarnings("unused")
    private AdminEmail() {
        // required by Objectify
    }

    /**
     * Instantiates a new AdminEmail.
     * @param subject
     *          email subject
     * @param content
     *          html email content
     */
    public AdminEmail(List<String> addressReceiver, List<String> groupReceiver, String subject,
                      Text content, Instant sendDate) {
        this.emailId = null;
        this.addressReceiver = addressReceiver == null ? new ArrayList<String>() : addressReceiver;
        this.groupReceiver = groupReceiver == null ? new ArrayList<String>() : groupReceiver;
        this.subject = subject;
        this.content = content;
        this.sendDate = TimeHelper.convertInstantToDate(sendDate);
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

    public void setSendDate(Instant sendDate) {
        this.sendDate = TimeHelper.convertInstantToDate(sendDate);
    }

    public String getEmailId() {
        return emailId == null ? null : Key.create(AdminEmail.class, this.emailId).toWebSafeString();
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

    public Instant getSendDate() {
        return TimeHelper.convertDateToInstant(this.sendDate);
    }

    public Text getContent() {
        return this.content;
    }

    public boolean getIsInTrashBin() {
        return this.isInTrashBin;
    }

    public Instant getCreateDate() {
        return TimeHelper.convertDateToInstant(this.createDate);
    }
}
