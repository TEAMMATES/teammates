package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity for email templates configurable by an admin.
 */
@Entity
@Table(name = "EmailTemplates",
        uniqueConstraints = {
                @UniqueConstraint(name = "Unique template key", columnNames = "templateKey"),
        })
public class EmailTemplate extends BaseEntity {
    public static final int TEMPLATE_KEY_MAX_LENGTH = 100;

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String templateKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @UpdateTimestamp
    private Instant updatedAt;

    protected EmailTemplate() {
        // required by Hibernate
    }

    public EmailTemplate(String templateKey, String subject, String body) {
        this.setId(UUID.randomUUID());
        this.setTemplateKey(templateKey);
        this.setSubject(subject);
        this.setBody(body);
        this.setCreatedAt(Instant.now());
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        if (templateKey == null || templateKey.isBlank()) {
            addNonEmptyError("Template key cannot be empty.", errors);
        } else if (templateKey.length() > TEMPLATE_KEY_MAX_LENGTH) {
            addNonEmptyError("Template key cannot be longer than " + TEMPLATE_KEY_MAX_LENGTH + " characters.", errors);
        }
        if (subject == null || subject.isBlank()) {
            addNonEmptyError("Email template subject cannot be empty.", errors);
        }
        if (body == null || body.isBlank()) {
            addNonEmptyError("Email template body cannot be empty.", errors);
        }

        return errors;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTemplateKey() {
        return this.templateKey;
    }

    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            EmailTemplate otherEmailTemplate = (EmailTemplate) other;
            return Objects.equals(this.getId(), otherEmailTemplate.getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public String toString() {
        return "EmailTemplate [id=" + id + ", templateKey=" + templateKey + ", subject=" + subject
                + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }
}
