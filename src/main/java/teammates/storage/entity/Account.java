package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;

/**
 * Represents a unique user in the system.
 */
@Entity
@Index
public class Account extends BaseEntity {

    @Id
    private String googleId;

    private String name;

    private boolean isInstructor;

    private String email;

    private String institute;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @SuppressWarnings("unused")
    private Account() {
        // required by Objectify
    }

    /**
     * Instantiates a new account.
     *
     * @param googleId
     *            the Google ID of the user.
     * @param name
     *            The name of the user.
     * @param isInstructor
     *            Does this account has instructor privileges?
     * @param email
     *            The official email of the user.
     * @param institute
     *            The university/school/institute e.g., "Abrons State University, Alaska"
     */
    public Account(String googleId, String name, boolean isInstructor,
            String email, String institute) {
        this.setGoogleId(googleId);
        this.setName(name);
        this.setIsInstructor(isInstructor);
        this.setEmail(email);
        this.setInstitute(institute);
        this.setCreatedAt(Instant.now());
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInstructor() {
        return isInstructor;
    }

    public void setIsInstructor(boolean accountIsInstructor) {
        this.isInstructor = accountIsInstructor;
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
}
