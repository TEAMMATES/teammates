package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.AdminEmail;

public class AdminEmailAttributes extends EntityAttributes<AdminEmail> {

    public static final Date DEFAULT_DATE = new Date();
    public static final boolean DEFAULT_IS_IN_TRASH_BIN = false;
    public static final String DEFAULT_EMAIL_ID = Const.ParamsNames.ADMIN_EMAIL_ID;

    // Required fields
    public List<String> addressReceiver;
    public List<String> groupReceiver;
    public String subject;
    public Date sendDate;
    public Text content;

    // Optional fields
    public Date createDate;
    public String emailId;
    public boolean isInTrashBin;

    /**
     * Creates a new AdminEmail with default values for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * <ul>
     * <li>{@code false} for {@code isInTrashBin}</li>
     * <li>{@code new Date()} for {@code createDate}</li>
     * <li>{@code Const.ParamsNames.ADMIN_EMAIL_ID} for {@code emailId}</li>
     * </ul>
     */
    AdminEmailAttributes(AdminEmailAttributesBuilder builder) {
        this.subject = builder.subject;
        this.addressReceiver = builder.addressReceiver;
        this.groupReceiver = builder.groupReceiver;
        this.content = builder.content;
        this.sendDate = builder.sendDate;

        this.createDate = builder.createDate;
        this.emailId = builder.emailId;
        this.isInTrashBin = builder.isInTrashBin;
    }

    public static AdminEmailAttributes valueOf(AdminEmail adminEmail) {
        return new AdminEmailAttributesBuilder(
                adminEmail.getSubject(),
                adminEmail.getAddressReceiver(),
                adminEmail.getGroupReceiver(),
                adminEmail.getContent(),
                adminEmail.getSendDate())
                .withCreateDate(adminEmail.getCreateDate())
                .withEmailId(adminEmail.getEmailId())
                .withIsInTrashBin(adminEmail.getIsInTrashBin())
                .build();
    }

    @Override
    public List<String> getInvalidityInfo() {

        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<>();

        addNonEmptyError(validator.getInvalidityInfoForEmailContent(content), errors);

        addNonEmptyError(validator.getInvalidityInfoForEmailSubject(subject), errors);

        return errors;
    }

    @Override
    public AdminEmail toEntity() {
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
        this.content = SanitizationHelper.sanitizeForRichText(content);
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

    public String getContentValue() {
        return this.content.getValue();
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

    public String getFirstAddressReceiver() {
        return this.addressReceiver.get(0);
    }

    public String getFirstGroupReceiver() {
        return getGroupReceiver().get(0);
    }

    public static class AdminEmailAttributesBuilder {
        // Required fields
        public List<String> addressReceiver;
        public List<String> groupReceiver;
        public String subject;
        public Date sendDate;
        public Text content;

        // Optional fields
        public Date createDate;
        public String emailId;
        public boolean isInTrashBin;

        public AdminEmailAttributesBuilder(String subject, List<String> addressReceiver, List<String> groupReceiver,
                                           Text content, Date sendDate) {
            this.addressReceiver = addressReceiver;
            this.groupReceiver = groupReceiver;
            this.subject = subject;
            this.content = content;
            this.sendDate = sendDate;

            this.createDate = DEFAULT_DATE;
            this.emailId = DEFAULT_EMAIL_ID;
            this.isInTrashBin = DEFAULT_IS_IN_TRASH_BIN;
        }

        public AdminEmailAttributesBuilder withCreateDate(Date createDate) {
            if (createDate != null) {
                this.createDate = createDate;
            }
            return this;
        }

        public AdminEmailAttributesBuilder withEmailId(String emailId) {
            if (emailId != null) {
                this.emailId = emailId;
            }
            return this;
        }

        public AdminEmailAttributesBuilder withIsInTrashBin(Boolean isInTrashBin) {
            if (isInTrashBin != null) {
                this.isInTrashBin = isInTrashBin;
            }
            return this;
        }

        public AdminEmailAttributes build() {
            return new AdminEmailAttributes(this);
        }
    }
}
