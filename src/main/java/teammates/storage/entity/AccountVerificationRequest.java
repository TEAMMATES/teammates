package teammates.storage.entity;

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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Entity for AccountVerificationRequests.
 */
@Entity
@Table(name = "AccountVerificationRequests")
public class AccountVerificationRequest extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "instituteId", nullable = false)
    private Institute institute;

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID instituteId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountVerificationRequestStatus status;

    @Column(columnDefinition = "TEXT")
    private String comments;

    private Instant createdDemoCourseAt;

    @UpdateTimestamp
    private Instant updatedAt;

    protected AccountVerificationRequest() {
        // required by Hibernate
    }

    public AccountVerificationRequest(String email, String name, AccountVerificationRequestStatus status, String comments) {
        this.setId(UUID.randomUUID());
        this.setEmail(email);
        this.setName(name);
        this.setStatus(status);
        this.setComments(comments);
        this.setCreatedAt(Instant.now());
        this.setCreatedDemoCourseAt(null);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(getEmail()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(getName()), errors);

        return errors;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Account getAccount() {
        return this.account;
    }

    /**
     * Sets the account associated with this account request.
     */
    public void setAccount(Account account) {
        this.account = account;
        this.accountId = account == null ? null : account.getId();
    }

    public UUID getAccountId() {
        return this.accountId;
    }

    public AccountVerificationRequestStatus getStatus() {
        return this.status;
    }

    public void setStatus(AccountVerificationRequestStatus status) {
        this.status = status;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Instant getCreatedDemoCourseAt() {
        return this.createdDemoCourseAt;
    }

    public void setCreatedDemoCourseAt(Instant createdDemoCourseAt) {
        this.createdDemoCourseAt = createdDemoCourseAt;
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

        if (!(o instanceof AccountVerificationRequest other)) {
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
        return "AccountVerificationRequest [id=" + id + ", name=" + name + ", email="
                + email + ", instituteId=" + instituteId + ", accountId=" + accountId
                + ", status=" + status + ", comments=" + comments
                + ", createdDemoCourseAt=" + createdDemoCourseAt
                + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }

}
