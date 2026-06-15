package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.Provider;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents a unique account in the system.
 */
@Entity
@Table(name = "Accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_provider_subject_tenant",
                        columnNames = {"provider", "subject", "tenantId"}),
        }
)
public class Account extends BaseEntity {
    public static final String NO_TENANT = "__NO_TENANT__";

    @Id
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String tenantId;

    @NaturalId
    private String googleId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "account")
    private Set<ReadNotification> readNotifications = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Instructor> instructors = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Student> students = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<AccountVerificationRequest> accountVerificationRequests = new HashSet<>();

    @UpdateTimestamp
    private Instant updatedAt;

    protected Account() {
        // required by Hibernate
    }

    public Account(String googleId, Provider provider, String subject, String tenantId, String name, String email) {
        this.setId(UUID.randomUUID());
        this.setGoogleId(googleId);
        this.setProvider(provider);
        this.setSubject(subject);
        this.setTenantId(tenantId);
        this.setName(name);
        this.setEmail(email);
    }

    /**
     * Add a read notification to this account.
     */
    public void addReadNotification(ReadNotification readNotification) {
        readNotifications.add(readNotification);
        readNotification.setAccount(this);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = SanitizationHelper.sanitizeSubject(subject);
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = normalizeTenantId(tenantId);
    }

    /**
     * Normalizes the tenant ID, returning a default value if the input is null.
     */
    public static String normalizeTenantId(String tenantId) {
        String sanitizedTenantId = SanitizationHelper.sanitizeTenantId(tenantId);
        return sanitizedTenantId == null ? NO_TENANT : sanitizedTenantId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = SanitizationHelper.sanitizeName(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = SanitizationHelper.sanitizeEmail(email);
    }

    public Set<ReadNotification> getReadNotifications() {
        return readNotifications;
    }

    public void setReadNotifications(Set<ReadNotification> readNotifications) {
        this.readNotifications = readNotifications;
    }

    public Set<Instructor> getInstructors() {
        return instructors;
    }

    public Set<Student> getStudents() {
        return students;
    }

    /**
     * Add an account verification request to this account.
     */
    public void addAccountVerificationRequest(AccountVerificationRequest accountVerificationRequest) {
        accountVerificationRequests.add(accountVerificationRequest);
        accountVerificationRequest.setAccount(this);
    }

    public Set<AccountVerificationRequest> getAccountVerificationRequests() {
        return accountVerificationRequests;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForGoogleId(googleId), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(name), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(email), errors);

        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Account other)) {
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
        return "Account [id=" + id + ", googleId=" + googleId + ", name=" + name + ", email=" + email
                + ", createdAt=" + getCreatedAt() + ",updatedAt=" + updatedAt + "]";
    }
}
