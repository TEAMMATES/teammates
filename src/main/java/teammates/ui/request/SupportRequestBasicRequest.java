package teammates.ui.request;

import teammates.common.datatransfer.SupportRequestStatus;
import teammates.common.datatransfer.SupportRequestType;

/**
 * The basic request for a support request.
 */
public class SupportRequestBasicRequest extends BasicRequest {
    private String name;
    private String email;
    private long createdAt;
    private long updatedAt;
    private SupportRequestType type;
    private String message;
    private SupportRequestStatus status;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public SupportRequestType getType() {
        return this.type;
    }

    public void setType(SupportRequestType type) {
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SupportRequestStatus getStatus() {
        return this.status;
    }

    public void setStatus(SupportRequestStatus status) {
        this.status = status;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(name != null, "Support request name cannot be null");
        assertTrue(email != null, "Support request email cannot be null");
        assertTrue(createdAt > 0L, "Created at should be greater than zero");
        assertTrue(updatedAt > 0L, "Updated at should be greater than zero");
        assertTrue(type != null, "Support request type cannot be null");
        assertTrue(message != null, "Support request message cannot be null");
        assertTrue(status != null, "Support request status cannot be null");
    }
}
