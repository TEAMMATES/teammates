package teammates.storage.sqlentity;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

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

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
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
}
