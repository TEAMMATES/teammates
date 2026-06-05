package teammates.test.scenariobuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.storage.entity.Notification;

/**
 * Builder for Notification entities used in test scenarios.
 */
public final class GivenNotification extends GivenBase<Notification> {
    public GivenNotification(GivenData given, UUID notificationId) {
        super(given);
        this.entity = defaultNotification(notificationId);
    }

    /**
     * Sets the start time for the notification.
     */
    public GivenNotification startTime(Instant startTime) {
        entity.setStartTime(startTime);
        return this;
    }

    /**
     * Sets the end time for the notification.
     */
    public GivenNotification endTime(Instant endTime) {
        entity.setEndTime(endTime);
        return this;
    }

    /**
     * Sets the notification time range to one that has already expired.
     */
    public GivenNotification expired() {
        Instant now = Instant.now();
        return startTime(now.minus(3, ChronoUnit.HOURS)).endTime(now.minus(1, ChronoUnit.HOURS));
    }

    /**
     * Sets the notification time range to one that is currently active.
     */
    public GivenNotification active() {
        Instant now = Instant.now();
        return startTime(now.minus(1, ChronoUnit.HOURS)).endTime(now.plus(1, ChronoUnit.HOURS));
    }

    /**
     * Sets the notification time range to one that has not started yet.
     */
    public GivenNotification yetToBeShown() {
        Instant now = Instant.now();
        return startTime(now.plus(1, ChronoUnit.HOURS)).endTime(now.plus(3, ChronoUnit.HOURS));
    }

    /**
     * Sets the style for the notification.
     */
    public GivenNotification style(NotificationStyle style) {
        entity.setStyle(style);
        return this;
    }

    /**
     * Sets the target user for the notification.
     */
    public GivenNotification targetUser(NotificationTargetUser targetUser) {
        entity.setTargetUser(targetUser);
        return this;
    }

    /**
     * Sets the notification target user to instructors.
     */
    public GivenNotification forInstructor() {
        return targetUser(NotificationTargetUser.INSTRUCTOR);
    }

    /**
     * Sets the notification target user to students.
     */
    public GivenNotification forStudent() {
        return targetUser(NotificationTargetUser.STUDENT);
    }

    /**
     * Sets the notification target user to general users.
     */
    public GivenNotification forGeneral() {
        return targetUser(NotificationTargetUser.GENERAL);
    }

    /**
     * Sets the title for the notification.
     */
    public GivenNotification title(String title) {
        entity.setTitle(title);
        return this;
    }

    /**
     * Sets the message for the notification.
     */
    public GivenNotification message(String message) {
        entity.setMessage(message);
        return this;
    }

    @Override
    void ensureConsistent() {
        // No mandatory relationships
    }

    /**
     * Generates a default alias for a notification.
     */
    public static String getDefaultAlias() {
        return "default";
    }

    private Notification defaultNotification(UUID notificationId) {
        Instant now = Instant.now();
        Notification notification = new Notification(
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.HOURS),
                NotificationStyle.INFO,
                NotificationTargetUser.GENERAL,
                "title:" + notificationId.toString(),
                "<p>message:" + notificationId.toString() + "</p>");
        notification.setId(notificationId);
        return notification;
    }
}
