package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.AdminEmail;

import com.google.appengine.api.datastore.Text;

public class AdminEmailAttributes extends EntityAttributes {

    public String emailId;
    public List<String> addressReceiver;
    public List<String> groupReceiver;
    public String subject;
    public Date sendDate;
    public Date createDate;
    public Text content;
    public boolean isInTrashBin;

    public AdminEmailAttributes(AdminEmail ae) {
        this.emailId = ae.getEmailId();
        this.addressReceiver = ae.getAddressReceiver();
        this.groupReceiver = ae.getGroupReceiver();
        this.subject = ae.getSubject();
        this.sendDate = ae.getSendDate();
        this.createDate = ae.getCreateDate();
        this.content = ae.getContent();
        this.isInTrashBin = ae.getIsInTrashBin();
    }

    public AdminEmailAttributes(String subject,
                                List<String> addressReceiver,
                                List<String> groupReceiver,
                                Text content,
                                Date sendDate) {
        this.subject = subject;
        this.addressReceiver = addressReceiver;
        this.groupReceiver = groupReceiver;
        this.content = content;
        this.sendDate = sendDate;
    }

    @Override
    public List<String> getInvalidityInfo() {

        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;

        error = validator.getInvalidityInfoForEmailContent(content);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getInvalidityInfoForEmailSubject(subject);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        return errors;
    }

    @Override
    public Object toEntity() {
        return new AdminEmail(addressReceiver, groupReceiver, subject, content, sendDate);
    }

    @Override
    public String getIdentificationString() {
        return this.sendDate + "/" + this.subject;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Admin Email";
    }

    @Override
    public String getBackupIdentifier() {
        return "Admin Email";
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, AdminEmail.class);
    }

    @Override
    public void sanitizeForSaving() {
        this.subject = SanitizationHelper.sanitizeTextField(subject);
        this.content = new Text(SanitizationHelper.sanitizeForHtml(content.getValue()));
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

    public Date getCreateDate() {
        return this.createDate;
    }

    public Text getContent() {
        return this.content;
    }

    public boolean getIsInTrashBin() {
        return this.isInTrashBin;
    }

    public String getSendDateForDisplay() {
        if (this.sendDate == null) {
            return "Draft";
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(this.sendDate);
        cal = TimeHelper.convertToUserTimeZone(cal, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);

        return TimeHelper.formatTime12H(cal.getTime());
    }

    public String getCreateDateForDisplay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.createDate);
        cal = TimeHelper.convertToUserTimeZone(cal, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);

        return TimeHelper.formatTime12H(cal.getTime());
    }

    public String getContentForDisplay() {
        return SanitizationHelper.desanitizeFromHtml(this.getContent().getValue());
    }

    public String getFirstAddressReceiver() {
        return this.addressReceiver.get(0);
    }

    public String getFirstGroupReceiver() {
        return getGroupReceiver().get(0);
    }
}
