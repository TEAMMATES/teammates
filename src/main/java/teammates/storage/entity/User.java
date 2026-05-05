package teammates.storage.entity;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import teammates.common.datatransfer.UserType;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

/**
 * Represents a User.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Instructor.class, name = "instructor"),
        @JsonSubTypes.Type(value = Student.class, name = "student")
})
@Entity
@Table(name = "Users", uniqueConstraints = {
        @UniqueConstraint(name = "Unique email and courseId", columnNames = { "email", "courseId" })
})
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;

    @Column(insertable = false, updatable = false)
    private UUID accountId;

    @Column(nullable = false, insertable = false, updatable = false)
    private String courseId;

    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 512)
    private String regKey;

    @UpdateTimestamp
    private Instant updatedAt;

    protected User() {
        // required by Hibernate
    }

    protected User(Course course, String name, String email) {
        this.setId(UUID.randomUUID());
        this.setCourse(course);
        this.setName(name);
        this.setEmail(email);
        this.generateNewRegistrationKey();
    }

    /**
     * Gets the user type of the user.
     */
    public abstract UserType getUserType();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public UUID getAccountId() {
        return accountId;
    }

    /**
     * Sets the account of the user.
     */
    public void setAccount(Account account) {
        this.account = account;
        this.accountId = account == null ? null : account.getId();
    }

    public String getCourseId() {
        return courseId;
    }

    public Course getCourse() {
        return course;
    }

    /**
     * Sets a course as well as the courseId.
     */
    public void setCourse(Course course) {
        this.course = course;
        this.courseId = course == null ? null : course.getId();
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRegKey() {
        return this.regKey;
    }

    public void setRegKey(String regKey) {
        this.regKey = regKey;
    }

    /**
     * Generates a new registration key for the user.
     */
    public void generateNewRegistrationKey() {
        this.setRegKey(generateRegistrationKey());
    }

    /**
     * Returns unique registration key for the user.
     */
    private String generateRegistrationKey() {
        String uniqueId = this.email + '%' + this.course.getId();

        SecureRandom prng = new SecureRandom();

        return StringHelper.encrypt(uniqueId + "%" + prng.nextInt());
    }

    /**
     * Returns google id of the user if account is not null.
     */
    public String getGoogleId() {
        if (getAccount() != null) {
            return getAccount().getGoogleId();
        }

        return null;
    }

    /**
     * Sets google id of account if account and googleId provided is not null.
     */
    public void setGoogleId(String googleId) {
        if (googleId != null && getAccount() != null) {
            getAccount().setGoogleId(googleId);
        }
    }

    public boolean isRegistered() {
        return this.account != null || this.accountId != null;
    }
}
