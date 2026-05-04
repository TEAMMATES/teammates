package teammates.logic.core;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.logic.entity.Account;
import teammates.logic.entity.Notification;
import teammates.logic.entity.ReadNotification;
import teammates.storage.api.NotificationsDb;

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

        validateNotification(notification);

        if (notificationsDb.getNotification(notification.getId()) != null) {
            throw new EntityAlreadyExistsException(
                    String.format(Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS, notification.toString()));
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

        notification.setStartTime(startTime);
        notification.setEndTime(endTime);
        notification.setStyle(style);
        notification.setTargetUser(targetUser);
        notification.setTitle(title);
        notification.setMessage(message);

        validateNotification(notification);

        return notification;
    }

    /**
     * Deletes notification associated with the {@code notificationId}.
     *
     * <p>Fails silently if the notification doesn't exist.</p>
     */
    public void deleteNotification(UUID notificationId) {
        Notification notification = getNotification(notificationId);
        if (notification == null) {
            return;
        }
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

    /**
     * Returns active unread notifications for the specified {@code targetUsers} and {@code accountId}.
     */
    public List<Notification> getUnreadActiveNotificationsByTargetUser(
            List<NotificationTargetUser> targetUsers, UUID accountId, Instant now) {
        assert targetUsers != null;
        assert accountId != null;
        return notificationsDb.getUnreadActiveNotificationsByTargetUser(targetUsers, accountId, now);
    }

    /**
     * Gets a list of notifications that have been read by the account with {@code accountId}.
     */
    public List<ReadNotification> getReadNotificationsByAccountId(UUID accountId) {
        assert accountId != null;
        return notificationsDb.getReadNotificationsByAccountId(accountId);
    }

    /**
     * Creates a read notification for the account with {@code accountId} and the notification with {@code notificationId}.
     */
    public ReadNotification createReadNotification(UUID accountId, UUID notificationId) {
        assert accountId != null;
        assert notificationId != null;

        Account account = HibernateUtil.getReference(Account.class, accountId);
        Notification notification = HibernateUtil.getReference(Notification.class, notificationId);

        ReadNotification readNotification = new ReadNotification(account, notification);

        return notificationsDb.createReadNotification(readNotification);
    }

    /**
     * Deletes a read notification associated with the {@code readNotificationId}.
     *
     * <p>Fails silently if the read notification doesn't exist.</p>
     */
    public void deleteReadNotification(UUID readNotificationId) {
        ReadNotification readNotification = notificationsDb.getReadNotification(readNotificationId);
        if (readNotification == null) {
            return;
        }

        notificationsDb.deleteReadNotification(readNotification);
    }

    private void validateNotification(Notification notification) throws InvalidParametersException {
        if (!notification.isValid()) {
            throw new InvalidParametersException(notification.getInvalidityInfo());
        }
    }
}
