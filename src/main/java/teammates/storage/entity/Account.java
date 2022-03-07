package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
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

    /**
     * Serialized {@link teammates.common.datatransfer.ReadNotifications} stored as a string.
     *
     * @see teammates.common.datatransfer.attributes.AccountAttributes#getReadNotificationsCopy()
     */
    @Unindex
    private String readNotificationsAsText;

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
     * @param readNotificationsAsText The notifications that the user has read, stored as a JSON string.
     */
    public Account(String googleId, String name, String email, String readNotificationsAsText) {
        this.setGoogleId(googleId);
        this.setName(name);
        this.setEmail(email);
        this.setReadNotificationsAsText(readNotificationsAsText);
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

    public String getReadNotificationsAsText() {
        return readNotificationsAsText;
    }

    public void setReadNotificationsAsText(String readNotificationsAsText) {
        this.readNotificationsAsText = readNotificationsAsText;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
