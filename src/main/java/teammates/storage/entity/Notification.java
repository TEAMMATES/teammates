package teammates.storage.entity;

import java.time.Instant;
import java.util.UUID;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Translate;

/**
 * Represents a unique notification in the system.
 */
@Entity
@Index
public class Notification extends BaseEntity {

    @Id
    private String notificationId;

    @Translate(InstantTranslatorFactory.class)
    private Instant startTime;

    @Translate(InstantTranslatorFactory.class)
    private Instant endTime;

    private String type;

    private String targetUser;

    private String title;

    private String message;

    private boolean shown;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant updatedAt;

    @SuppressWarnings("unused")
    private Notification() {
        // required by Objectify
    }

    /**
     * Instantiates a new notification, with ID randomly generated and time fields filled automatically.
     *
     * @param startTime start time for the notification to be shown to users
     * @param endTime notifications are hidden from users after endTime
     * @param type type of the notification (e.g. maintainance, depreciation, etc.)
     * @param targetUser student or instructor
     * @param title title of the notification
     * @param message message body of the notification
     */
    public Notification(Instant startTime, Instant endTime, String type, String targetUser,
            String title, String message) {
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setType(type);
        this.setTargetUser(targetUser);
        this.setTitle(title);
        this.setMessage(message);
        this.setCreatedAt(Instant.now());

        UUID uuid = UUID.randomUUID();
        this.notificationId = uuid.toString();
        this.shown = false;
    }

    /**
     * Instantiates a new notification, with all fields passed in as parameters.
     * This is mainly for conversion from attributes to entity.
     */
    public Notification(String notificationId, Instant startTime, Instant endTime, String type, String targetUser,
            String title, String message, boolean shown, Instant createdAt, Instant updatedAt) {
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setType(type);
        this.setTargetUser(targetUser);
        this.setTitle(title);
        this.setMessage(message);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);

        this.notificationId = notificationId;
        this.shown = shown;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShown() {
        return shown;
    }

    /**
     * Sets the notification as shown to the user.
     * Only allowed to change value from false to true.
     */
    public void setShown() {
        this.shown = true;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Updates the updatedAt timestamp when saving.
     */
    @OnSave
    public void updateLastUpdateTimestamp() {
        this.setUpdatedAt(Instant.now());
    }
}
