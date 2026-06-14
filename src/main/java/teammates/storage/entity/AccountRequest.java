package teammates.storage.entity;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

/**
 * Entity for AccountRequests.
 */
@Entity
@Table(name = "AccountRequests",
        uniqueConstraints = {
                @UniqueConstraint(name = "Unique registration key", columnNames = "registrationKey"),
        })
public class AccountRequest extends BaseEntity {
    @Id
    private UUID id;

    private String registrationKey;

    private String name;

    private String email;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "instituteId", nullable = false)
    private Institute institute;

    @Column(name = "instituteId", nullable = false, insertable = false, updatable = false)
    private UUID instituteId;

    @Enumerated(EnumType.STRING)
    private AccountRequestStatus status;

    @Column(columnDefinition = "TEXT")
    private String comments;

    private Instant registeredAt;

    @UpdateTimestamp
    private Instant updatedAt;

    protected AccountRequest() {
        // required by Hibernate
    }

    public AccountRequest(String email, String name, AccountRequestStatus status, String comments) {
        this.setId(UUID.randomUUID());
        this.setEmail(email);
        this.setName(name);
        this.setStatus(status);
        this.setComments(comments);
        this.generateNewRegistrationKey();
        this.setCreatedAt(Instant.now());
        this.setRegisteredAt(null);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(getEmail()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(getName()), errors);

        return errors;
    }

    /**
     * Generates a new registration key for the account request.
     */
    public void generateNewRegistrationKey() {
        this.setRegistrationKey(generateRegistrationKey());
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

    public Institute getInstitute() {
        return this.institute;
    }

    /**
     * Sets the institute of the account request.
     */
    public void setInstitute(Institute institute) {
        this.institute = institute;
        this.instituteId = institute == null ? null : institute.getId();
    }

    public UUID getInstituteId() {
        return this.instituteId;
    }

    public AccountRequestStatus getStatus() {
        return this.status;
    }

    public void setStatus(AccountRequestStatus status) {
        this.status = status;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AccountRequest other)) {
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
        return "AccountRequest [id=" + id + ", registrationKey=" + registrationKey + ", name=" + name + ", email="
                + email + ", instituteId=" + instituteId
                + ", status=" + status + ", comments=" + comments
                + ", registeredAt=" + registeredAt + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }

}
