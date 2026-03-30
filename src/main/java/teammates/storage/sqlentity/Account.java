package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents a unique account in the system (profile data). External login identities
 * are stored in {@link AccountIdentity}.
 */
@Entity
@Table(name = "Accounts")
public class Account extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<AccountIdentity> identities = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ReadNotification> readNotifications = new ArrayList<>();

    @UpdateTimestamp
    private Instant updatedAt;

    protected Account() {
        // required by Hibernate
    }

    public Account(String name, String email) {
        this.setId(UUID.randomUUID());
        this.setName(name);
        this.setEmail(email);
    }

    /**
     * Add a read notification to this account.
     */
    public void addReadNotification(ReadNotification readNotification) {
        readNotifications.add(readNotification);
    }

    /**
     * Associates an identity with this account (typically before persisting the account).
     */
    public void addIdentity(AccountIdentity identity) {
        identities.add(identity);
        identity.setAccount(this);
    }

    public UUID getId() {
        return id;
    }

    /**
     * Returns the internal account id as a string (same value as used in sessions and APIs).
     */
    public String getAccountId() {
        return getId().toString();
    }

    public void setId(UUID id) {
        this.id = id;
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

    public List<AccountIdentity> getIdentities() {
        return identities;
    }

    public void setIdentities(List<AccountIdentity> identities) {
        this.identities = identities;
    }

    public List<ReadNotification> getReadNotifications() {
        return readNotifications;
    }

    public void setReadNotifications(List<ReadNotification> readNotifications) {
        this.readNotifications = readNotifications;
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

        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(name), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(email), errors);

        return errors;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            Account otherAccount = (Account) other;
            return Objects.equals(this.getId(), otherAccount.getId());
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
        return "Account [id=" + id + ", name=" + name + ", email=" + email
                + ", readNotifications=" + readNotifications + ", createdAt=" + getCreatedAt()
                + ",updatedAt=" + updatedAt + "]";
    }
}
