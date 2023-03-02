package teammates.storage.sqlentity;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a User.
 */
@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;

    @Column(nullable = false, insertable = false, updatable = false)
    private String courseId;

    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teamId")
    private Team team;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String regKey;

    @UpdateTimestamp
    private Instant updatedAt;

    protected User() {
        // required by Hibernate
    }

    public User(Course course, String name, String email) {
        this.setId(UUID.randomUUID());
        this.setCourseId(course.getId());
        this.setCourse(course);
        this.setTeam(null);
        this.setName(name);
        this.setEmail(email);
        this.setRegKey(generateRegistrationKey());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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
     * Returns unique registration key for the student/instructor.
     */
    private String generateRegistrationKey() {
        String uniqueId = this.email + '%' + this.course.getId();

        SecureRandom prng = new SecureRandom();

        return StringHelper.encrypt(uniqueId + "%" + prng.nextInt());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            User otherUser = (User) other;
            return Objects.equals(this.getId(), otherUser.getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
