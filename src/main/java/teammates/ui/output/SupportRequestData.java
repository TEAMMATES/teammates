package teammates.ui.output;

import teammates.common.datatransfer.SupportRequestStatus;
import teammates.common.datatransfer.SupportRequestType;
import teammates.common.datatransfer.attributes.SupportRequestAttributes;

/**
 * The API output format of a support request.
 */
public class SupportRequestData extends ApiOutput{
    private String id;
    private String name;
    private String email;
    private long createdAt;
    private long updatedAt;
    private SupportRequestType type;
    private String message;
    private SupportRequestStatus status;

    public SupportRequestData(SupportRequestAttributes supportRequestAttributes) {
        this.id = supportRequestAttributes.getId();
        this.name = supportRequestAttributes.getName();
        this.email = supportRequestAttributes.getEmail();
        this.createdAt = supportRequestAttributes.getCreatedAt().toEpochMilli();
        this.updatedAt = supportRequestAttributes.getUpdatedAt().toEpochMilli();
        this.type = supportRequestAttributes.getType();
        this.message = supportRequestAttributes.getMessage();
        this.status = supportRequestAttributes.getStatus();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public long getUpdatedAt() {
        return this.updatedAt;
    }

    public SupportRequestType getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public SupportRequestStatus getStatus() {
        return this.status;
    }
}
