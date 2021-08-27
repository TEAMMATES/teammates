package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;

/**
 * Represents an AccountRequest entity.
 */
@Entity
@Index
public class AccountRequest extends BaseEntity {

    @Id
    private String email;

    private String registrationKey;

    private String name;

    private String institute;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant deletedAt;

    @SuppressWarnings("unused")
    private AccountRequest() {
        // required by Objectify
    }

    public AccountRequest(String email, String registrationKey, String name, String institute,
            Instant createdAt, Instant deletedAt) {
        this.setEmail(email);
        this.setRegistrationKey(registrationKey);
        this.setName(name);
        this.setInstitute(institute);

        if (createdAt == null) {
            this.setCreatedAt(Instant.now());
        } else {
            this.setCreatedAt(createdAt);
        }
        this.setDeletedAt(deletedAt);
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
        this.email = email;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

}
