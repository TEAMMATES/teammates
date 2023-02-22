package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Notification;

/**
 * Handles CRUD operations for notifications.
 *
 * @see Notification
 */
public final class NotificationsDb extends EntitiesDb<Notification> {

    private static final NotificationsDb instance = new NotificationsDb();

    private NotificationsDb() {
        // prevent initialization
    }

    public static NotificationsDb inst() {
        return instance;
    }

    /**
     * Creates a notification.
     */
    public Notification createNotification(Notification notification)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert notification != null;

        if (!notification.isValid()) {
            throw new InvalidParametersException(notification.getInvalidityInfo());
        }

        persist(notification);
        return notification;
    }

    /**
     * Gets a notification by its unique ID.
     */
    public Notification getNotification(UUID notificationId) {
        assert notificationId != null;

        return HibernateUtil.get(Notification.class, notificationId);
    }

    /**
     * Updates a notification with {@link Notification}.
     */
    public Notification updateNotification(UUID notificationId, Instant startTime, Instant endTime,
                                           NotificationStyle style, NotificationTargetUser targetUser, String title,
                                           String message)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert notificationId != null;

        Notification notification = getNotification(notificationId);

        if (notification == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        notification.setStartTime(startTime);
        notification.setEndTime(endTime);
        notification.setStyle(style);
        notification.setTargetUser(targetUser);
        notification.setTitle(title);
        notification.setMessage(message);

        if (!notification.isValid()) {
            throw new InvalidParametersException(notification.getInvalidityInfo());
        }

        return notification;
    }

    /**
     * Deletes a notification.
     *
     * <p>Fails silently if notification is null.
     */
    public void deleteNotification(Notification notification) {
        if (notification != null) {
            delete(notification);
        }
    }
}
