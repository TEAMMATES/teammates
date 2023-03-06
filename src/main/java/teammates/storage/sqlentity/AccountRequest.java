package teammates.storage.sqlentity;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entity for AccountRequests.
 */
@Entity
@Table(name = "AccountRequests",
        uniqueConstraints = {
                @UniqueConstraint(name = "Unique registration key", columnNames = "registrationKey"),
                @UniqueConstraint(name = "Unique name and institute", columnNames = {"email", "institute"})
        })
public class AccountRequest extends BaseEntity {
    @Id
    private UUID id;

    private String registrationKey;

    private String name;

    private String email;

    private String institute;

    private Instant registeredAt;

    @UpdateTimestamp
    private Instant updatedAt;

    protected AccountRequest() {
        // required by Hibernate
    }

    public AccountRequest(String email, String name, String institute) {
        this.setId(UUID.randomUUID());
        this.setEmail(email);
        this.setName(name);
        this.setInstitute(institute);
        this.setRegistrationKey(generateRegistrationKey());
        this.setCreatedAt(Instant.now());
        this.setRegisteredAt(null);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(getEmail()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(getName()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForInstituteName(getInstitute()), errors);

        return errors;
    }

    /**
     * Generate unique registration key for the account request.
     * The key contains random elements to avoid being guessed.
     */
    private String generateRegistrationKey() {
        String uniqueId = String.valueOf(getId());
        SecureRandom prng = new SecureRandom();

        return StringHelper.encrypt(uniqueId + prng.nextInt());
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRegistrationKey() {
        return this.registrationKey;
    }

    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = SanitizationHelper.sanitizeName(name);
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = SanitizationHelper.sanitizeEmail(email);
    }

    public String getInstitute() {
        return this.institute;
    }

    public void setInstitute(String institute) {
        this.institute = SanitizationHelper.sanitizeTitle(institute);
    }

    public Instant getRegisteredAt() {
        return this.registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
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
            AccountRequest otherAccountRequest = (AccountRequest) other;
            return Objects.equals(this.getId(), otherAccountRequest.getId());
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
        return "AccountRequest [id=" + id + ", registrationKey=" + registrationKey + ", name=" + name + ", email="
                + email + ", institute=" + institute + ", registeredAt=" + registeredAt + ", createdAt=" + getCreatedAt()
                + ", updatedAt=" + updatedAt + "]";
    }

    public String getRegistrationUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withIsCreatingAccount("true")
                .withRegistrationKey(this.getRegistrationKey())
                .toAbsoluteString();
    }
}
