package teammates.sqllogic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;

/**
 * Handles operations related to accounts.
 *
 * @see Account
 * @see AccountsDb
 */
public final class AccountsLogic {

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
     * Gets an account.
     */
    public Account getAccount(UUID id) {
        assert id != null;
        return accountsDb.getAccount(id);
    }

    /**
     * Gets an account by googleId.
     */
    public Account getAccountForGoogleId(String googleId) {
        assert googleId != null;

        return accountsDb.getAccountByGoogleId(googleId);
    }

    /**
     * Gets accounts associated with email.
     */
    public List<Account> getAccountsForEmail(String email) {
        assert email != null;

        return accountsDb.getAccountsByEmail(email);
    }

    /**
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException   if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the
     *                                      database.
     */
    public Account createAccount(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert account != null;
        return accountsDb.createAccount(account);
    }

    /**
     * Updates the readNotifications of an account.
     *
     * @param googleId       google ID of the user who read the notification.
     * @param notificationId ID of notification to be marked as read.
     * @param endTime        the expiry time of the notification, i.e. notification
     *                       will not be shown after this time.
     * @return the account with updated read notifications.
     * @throws InvalidParametersException  if the notification has expired.
     * @throws EntityDoesNotExistException if account or notification does not
     *                                     exist.
     */
    public List<UUID> updateReadNotifications(String googleId, UUID notificationId, Instant endTime)
            throws InvalidParametersException, EntityDoesNotExistException {
        Account account = accountsDb.getAccountByGoogleId(googleId);
        if (account == null) {
            throw new EntityDoesNotExistException("Trying to update the read notifications of a non-existent account.");
        }

        Notification notification = notificationsLogic.getNotification(notificationId);
        if (notification == null) {
            throw new EntityDoesNotExistException("Trying to mark as read a notification that does not exist.");
        }
        if (endTime.isBefore(Instant.now())) {
            throw new InvalidParametersException("Trying to mark an expired notification as read.");
        }

        ReadNotification readNotification = new ReadNotification(account, notification);
        account.addReadNotification(readNotification);

        return account.getReadNotifications().stream()
                .map(n -> n.getNotification().getId())
                .collect(Collectors.toList());
    }

    /**
     * Gets ids of read notifications in an account.
     */
    public List<UUID> getReadNotificationsId(String googleId) {
        return accountsDb.getAccountByGoogleId(googleId).getReadNotifications().stream()
                .map(n -> n.getNotification().getId())
                .collect(Collectors.toList());
    }
}
