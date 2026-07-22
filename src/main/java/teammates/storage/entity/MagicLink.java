package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents a passwordless login magic link.
 */
@Entity
@Table(name = "MagicLinks")
public class MagicLink extends BaseEntity {

    @Id
    private UUID id;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @UpdateTimestamp
    private Instant updatedAt;

    protected MagicLink() {
        // required by Hibernate
    }

    public MagicLink(String email, String tokenHash, Instant now) {
        this.setId(UUID.randomUUID());
        this.setEmail(email);
        this.setTokenHash(tokenHash);
        this.setExpiresAt(now.plus(Const.MAGIC_LINK_VALIDITY_PERIOD));
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(email), errors);

        return errors;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = SanitizationHelper.sanitizeEmail(email);
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = SanitizationHelper.sanitizeTextField(tokenHash);
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns true if the magic link expires at or before {@code now}.
     */
    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }

    /**
     * Returns true if the magic link can still be used.
     */
    public boolean isUsable(Instant now) {
        return !isExpired(now);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MagicLink other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "MagicLink [id=" + id + ", email=" + email + ", expiresAt=" + expiresAt
                + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }
}
