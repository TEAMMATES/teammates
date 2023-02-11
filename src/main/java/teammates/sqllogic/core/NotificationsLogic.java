package teammates.sqllogic.core;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.NotificationsDb;
import teammates.storage.sqlentity.Notification;

/**
 * Handles the logic related to notifications.
 */
public final class NotificationsLogic {

    private static final NotificationsLogic instance = new NotificationsLogic();

    private final NotificationsDb notificationsDb = NotificationsDb.inst();

    private  NotificationsLogic() {
        // prevent initialization
    }

    public static NotificationsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        // No dependency to other logic class
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
}
