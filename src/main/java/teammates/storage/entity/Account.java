package teammates.storage.entity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Translate;
import com.googlecode.objectify.annotation.Unindex;

/**
 * Represents a unique user in the system.
 */
@Entity
@Index
public class Account extends BaseEntity {

    @Id
    private String googleId;

    private String name;

    private String email;

    private String description;

    @Unindex
    @Serialize
    private Map<String, Instant> readNotifications;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @SuppressWarnings("unused")
    private Account() {
        // required by Objectify
    }

    /**
     * Instantiates a new account.
     *
     * @param googleId the Google ID of the user.
     * @param name The name of the user.
     * @param email The official email of the user.
     * @param readNotifications The notifications that the user has read, stored in a map of ID to end time.
     */
    public Account(String googleId, String name, String email, String description, Map<String, Instant> readNotifications) {
        this.setGoogleId(googleId);
        this.setName(name);
        this.setEmail(email);
        this.setDescription(description);
        this.setReadNotifications(readNotifications);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the account's read notifications map.
     * Returns an empty map if the account does not yet have the readNotifications attribute.
     */
    public Map<String, Instant> getReadNotifications() {
        if (readNotifications == null) {
            return new HashMap<>();
        }
        return readNotifications;
    }

    public void setReadNotifications(Map<String, Instant> readNotifications) {
        this.readNotifications = readNotifications;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
