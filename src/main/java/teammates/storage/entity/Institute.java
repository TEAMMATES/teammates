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
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents an institute, identified by its name and country.
 */
@Entity
@Table(name = "Institutes", uniqueConstraints = {
        @UniqueConstraint(name = "Unique name and country", columnNames = { "name", "country" })
})
public class Institute extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    @OneToMany(mappedBy = "institute")
    private Set<Course> courses = new HashSet<>();

    @OneToMany(mappedBy = "institute")
    private Set<AccountVerificationRequest> accountVerificationRequests = new HashSet<>();

    @UpdateTimestamp
    private Instant updatedAt;

    protected Institute() {
        // required by Hibernate
    }

    public Institute(String name, String country) {
        this.setId(UUID.randomUUID());
        this.setName(name);
        this.setCountry(country);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForInstituteName(getName()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForCountry(getCountry()), errors);

        return errors;
    }

    /**
     * Adds a course to the Institute.
     */
    public void addCourse(Course course) {
        this.courses.add(course);
        course.setInstitute(this);
    }

    /**
     * Adds an account request to the Institute.
     */
    public void addAccountVerificationRequest(AccountVerificationRequest accountVerificationRequest) {
        this.accountVerificationRequests.add(accountVerificationRequest);
        accountVerificationRequest.setInstitute(this);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = SanitizationHelper.sanitizeTitle(name);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim().toUpperCase();
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    public Set<AccountVerificationRequest> getAccountVerificationRequests() {
        return accountVerificationRequests;
    }

    public void setAccountVerificationRequests(Set<AccountVerificationRequest> accountVerificationRequests) {
        this.accountVerificationRequests = accountVerificationRequests;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Institute other)) {
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
        return "Institute [id=" + id + ", name=" + name + ", country=" + country
                + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }
}
