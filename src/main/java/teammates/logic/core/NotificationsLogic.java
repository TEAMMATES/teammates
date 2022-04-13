package teammates.logic.core;

import java.util.List;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.NotificationsDb;

/**
 * Handles the logic related to notifications.
 */
public final class NotificationsLogic {

    private static final NotificationsLogic instance = new NotificationsLogic();

    private final NotificationsDb notificationsDb = NotificationsDb.inst();

    private NotificationsLogic() {
        // prevent initialization
    }

    public static NotificationsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        // No dependency to other logic class
    }

    /**
     * Gets notification associated with the {@code notificationId}.
     *
     * @return null if no match found.
     */
    public NotificationAttributes getNotification(String notificationId) {
        return notificationsDb.getNotification(notificationId);
    }

    /**
     * Gets all notifications.
     */
    public List<NotificationAttributes> getAllNotifications() {
        return notificationsDb.getAllNotifications();
    }

    /**
     * Gets a list of notifications.
     *
     * @return a list of notifications with the specified {@code targetUser}.
     */
    public List<NotificationAttributes> getActiveNotificationsByTargetUser(NotificationTargetUser targetUser) {
        return notificationsDb.getActiveNotificationsByTargetUser(targetUser);
    }

    /**
     * Creates a notification.
     *
     * @return the created notification
     * @throws InvalidParametersException if the notification is not valid
     * @throws EntityAlreadyExistsException if the notification already exists in the database.
     */
    public NotificationAttributes createNotification(NotificationAttributes notification)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return notificationsDb.createEntity(notification);
    }

    /**
     * Updates/Creates the notification using {@link NotificationAttributes.UpdateOptions}.
     *
     * @return updated notification
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if notification cannot be found with given Id
     */
    public NotificationAttributes updateNotification(NotificationAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        return notificationsDb.updateNotification(updateOptions);
    }

    /**
     * Deletes notification associated with the {@code notificationId}.
     *
     * <p>Fails silently if the notification doesn't exist.</p>
     */
    public void deleteNotification(String notificationId) {
        notificationsDb.deleteNotification(notificationId);
    }

    /**
     * Checks if a notification associated with {@code notificationId} exists.
     */
    public boolean doesNotificationExists(String notificationId) {
        return notificationsDb.doesNotificationExists(notificationId);
    }

}
