package teammates.storage.entity;

import java.security.SecureRandom;
import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;

import teammates.common.util.StringHelper;

/**
 * Represents an AccountRequest entity.
 */
@Entity
@Index
public class AccountRequest extends BaseEntity {

    @Id
    private String id;

    private String registrationKey;

    private String email;

    private String name;

    private String institute;

    @Translate(InstantTranslatorFactory.class)
    private Instant registeredAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @SuppressWarnings("unused")
    private AccountRequest() {
        // required by Objectify
    }

    public AccountRequest(String email, String name, String institute) {
        this.setEmail(email);
        this.setName(name);
        this.setInstitute(institute);
        this.setId(generateId(email, institute));
        this.setRegistrationKey(generateRegistrationKey());
        this.setCreatedAt(Instant.now());
        this.setRegisteredAt(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute.trim();
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Generates an unique ID for the account request.
     */
    public static String generateId(String email, String institute) {
        // Format: email%institute e.g., adam@gmail.com%TEAMMATES_TEST_INSTITUTE
        return email + '%' + institute;
    }

    /**
     * Generate unique registration key for the account request.
     * The key contains random elements to avoid being guessed.
     */
    private String generateRegistrationKey() {
        String uniqueId = getId();
        SecureRandom prng = new SecureRandom();

        return StringHelper.encrypt(uniqueId + prng.nextInt());
    }
}
