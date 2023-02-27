package teammates.sqllogic.core;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles operations related to accounts.
 *
 * @see Account
 * @see AccountsDb
 */
public class AccountsLogic {

    private static final AccountsLogic instance = new AccountsLogic();

    private AccountsDb accountsDb;

    private NotificationsLogic notificationsLogic;

    private AccountsLogic() {
        // prevent initialization
    }

    void initLogicDependencies(AccountsDb accountsDb, NotificationsLogic notificationsLogic) {
        this.accountsDb = accountsDb;
        this.notificationsLogic = notificationsLogic;
    }

    public static AccountsLogic inst() {
        return instance;
    }

    /**
     * Updates the readNotifications of an account.
     *
     * @param googleId google ID of the user who read the notification.
     * @param notificationId notification to be marked as read.
     * @param endTime the expiry time of the notification, i.e. notification will not be shown after this time.
     * @return the account attributes with updated read notifications.
     * @throws InvalidParametersException if the notification has expired.
     * @throws EntityDoesNotExistException if account or notification does not exist.
     */
    public List<UUID> updateReadNotifications(String googleId, UUID notificationId, Instant endTime)
            throws InvalidParametersException, EntityDoesNotExistException {
        Account account = accountsDb.getAccountByGoogleId(googleId);
        Notification notification = notificationsLogic.getNotification(notificationId);

        if (account == null) {
            throw new EntityDoesNotExistException("Trying to update the read notifications of a non-existent account.");
        }
        if (notification == null) {
            throw new EntityDoesNotExistException("Trying to mark as read a notification that does not exist.");
        }
        if (endTime.isBefore(Instant.now())) {
            throw new InvalidParametersException("Trying to mark an expired notification as read.");
        }

        List<ReadNotification> accountReadNotifications = account.getReadNotifications();
        List<ReadNotification> notificationReadNotifications = notification.getReadNotifications();

        ReadNotification readNotification = new ReadNotification(account, notification, Instant.now());
        accountReadNotifications.add(readNotification);
        notificationReadNotifications.add(readNotification);

        return accountReadNotifications.stream()
                .map(n -> n.getNotification().getNotificationId())
                .collect(Collectors.toList());
    }

    /**
     * Gets ids of read notifications in an account.
     */
    public List<UUID> getReadNotificationsId(String googleId) {
        return accountsDb.getAccountByGoogleId(googleId).getReadNotifications().stream()
                .map(n -> n.getNotification().getNotificationId())
                .collect(Collectors.toList());
    }
}
