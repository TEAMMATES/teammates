package teammates.storage.sqlapi;

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
public class NotificationsDb extends EntitiesDb<Notification> {

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

        notification.sanitizeForSaving();
        if (!notification.isValid()) {
            throw new InvalidParametersException(notification.getInvalidityInfo());
        }

        if (getNotification(notification.getNotificationId().toString()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS,
                    notification.toString()));
        }

        persist(notification);
        return notification;
    }

    /**
     * Gets a notification by its unique ID.
     */
    public Notification getNotification(String notificationId) {
        assert notificationId != null;

        return HibernateUtil.getSessionFactory().getCurrentSession().get(Notification.class, notificationId);
    }

    /**
     * Updates a notification with {@link Notification}.
     */
    public Notification updateNotification(Notification notification)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert notification != null;

        notification.sanitizeForSaving();

        if (!notification.isValid()) {
            throw new InvalidParametersException(notification.getInvalidityInfo());
        }

        if (getNotification(notification.getNotificationId().toString()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(notification);
    }

    /**
     * Deletes a notification by its unique ID.
     *
     * <p>Fails silently if there is no such notification.
     */
    public void deleteNotification(String notificationId) {
        assert notificationId != null;

        Notification notification = getNotification(notificationId);
        if (notification != null) {
            delete(notification);
        }
    }
}