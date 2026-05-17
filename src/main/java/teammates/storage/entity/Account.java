package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents a unique account in the system.
 */
@Entity
@Table(name = "Accounts")
public class Account extends BaseEntity {
    @Id
    private UUID id;

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

    @UpdateTimestamp
    private Instant updatedAt;

    protected Account() {
        // required by Hibernate
    }

    public Account(String googleId, String name, String email) {
        this.setId(UUID.randomUUID());
        this.setGoogleId(googleId);
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
                + ", readNotifications=" + readNotifications + ", createdAt=" + getCreatedAt()
                + ",updatedAt=" + updatedAt + "]";
    }
}
