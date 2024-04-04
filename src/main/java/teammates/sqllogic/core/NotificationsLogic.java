package teammates.sqllogic.core;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
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

    /**
     * Initialise dependencies for {@code NotificationLogic} object.
     */
    public void initLogicDependencies(NotificationsDb notificationsDb) {
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
        assert notification != null;

        if (!notification.isValid()) {
            throw new InvalidParametersException(notification.getInvalidityInfo());
        }
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
        Notification notification = notificationsDb.getNotification(notificationId);

        if (notification == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + Notification.class);
        }

        // evict managed entity to avoid auto-persist
        HibernateUtil.flushAndEvict(notification);

        notification.setStartTime(startTime);
        notification.setEndTime(endTime);
        notification.setStyle(style);
        notification.setTargetUser(targetUser);
        notification.setTitle(title);
        notification.setMessage(message);

        if (!notification.isValid()) {
            throw new InvalidParametersException(notification.getInvalidityInfo());
        }

        notificationsDb.updateNotification(notification);

        return notification;
    }

    /**
     * Deletes notification associated with the {@code notificationId}.
     *
     * <p>Fails silently if the notification doesn't exist.</p>
     */
    public void deleteNotification(UUID notificationId) {
        assert notificationId != null;

        Notification notification = getNotification(notificationId);
        notificationsDb.deleteNotification(notification);
    }

    /**
     * Gets all notifications.
     */
    public List<Notification> getAllNotifications() {
        return notificationsDb.getAllNotifications();
    }

    /**
     * Gets a list of notifications.
     *
     * @return a list of notifications with the specified {@code targetUser}.
     */
    public List<Notification> getActiveNotificationsByTargetUser(NotificationTargetUser targetUser) {
        assert targetUser != null;
        return notificationsDb.getActiveNotificationsByTargetUser(targetUser);
    }
}
