package teammates.logic.core;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.NotificationDb;

/**
 * Handles the logic related to notifications.
 */
public final class NotificationLogic {

    private static final NotificationLogic instance = new NotificationLogic();

    private final NotificationDb notificationDb = NotificationDb.inst();

    private NotificationLogic() {
        // prevent initialization
    }

    public static NotificationLogic inst() {
        return instance;
    }

    /**
     * Gets notification associated with the {@code notificationId}.
     *
     * @return null if no match found.
     */
    public NotificationAttributes getNotification(String notificationId) {
        return notificationDb.getNotification(notificationId);
    }

    /**
     * Creates a notification.
     *
     * @return the created notification
     * @throws InvalidParametersException if the notification is not valid
     * @throws EntityAlreadyExistsException if the notification already exists in the database.
     */
    NotificationAttributes createNotification(NotificationAttributes notification)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return notificationDb.createEntity(notification);
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
        return notificationDb.updateNotification(updateOptions);
    }

    /**
     * Deletes notification associated with the {@code notificationId}.
     *
     * <p>Fails silently if the notification doesn't exist.</p>
     */
    public void deleteNotification(String notificationId) {
        notificationDb.deleteNotification(notificationId);
    }

}
