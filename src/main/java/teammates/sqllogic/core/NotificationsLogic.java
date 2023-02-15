package teammates.sqllogic.core;

import java.util.UUID;

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
}
