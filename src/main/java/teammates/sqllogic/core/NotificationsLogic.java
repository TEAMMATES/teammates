package teammates.sqllogic.core;

import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.NotificationsDb;
import teammates.storage.sqlentity.Notification;

/**
 * Handles the logic related to notifications.
 */
public final class NotificationsLogic {

    private static final NotificationsLogic instance = new NotificationsLogic();

    private NotificationsDb notificationsDb;

    private NotificationsLogic() {
        // prevent initialization
    }

    public static NotificationsLogic inst() {
        return instance;
    }

    void initLogicDependencies(NotificationsDb notificationsDb) {
        this.notificationsDb = notificationsDb;
    }

    /**
     * Creates a notification.
     *
     * @return the created notification
     * @throws InvalidParametersException if the notification is not valid
     * @throws EntityAlreadyExistsException if the notification already exists in the database.
     */
    public Notification createNotification(Notification notification)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return notificationsDb.createNotification(notification);
    }

    /**
     * Gets notification associated with the {@code notificationId}.
     *
     * @return null if no match found.
     */
    public Notification getNotification(UUID notificationId) {
        assert notificationId != null;

        return notificationsDb.getNotification(notificationId);
    }

    /**
     * Updates/Creates the notification using {@link Notification}.
     *
     * @return updated notification
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if notification cannot be found with given Id
     */
    public Notification updateNotification(UUID notificationId, Instant startTime, Instant endTime,
                                           NotificationStyle style, NotificationTargetUser targetUser, String title,
                                           String message)
            throws InvalidParametersException, EntityDoesNotExistException {
        return notificationsDb.updateNotification(notificationId, startTime, endTime, style, targetUser, title, message);
    }
}
