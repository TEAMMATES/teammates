package teammates.sqllogic.core;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Notification;

import java.util.ArrayList;
import java.util.List;
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

    private AccountsLogic() {
        // prevent initialization
    }

    void initLogicDependencies(AccountsDb accountsDb) {
        this.accountsDb = accountsDb;
    }

    public static AccountsLogic inst() {
        return instance;
    }

    /**
     * Gets ids of read notifications in an account.
     */
    public List<UUID> getReadNotificationsId(String googleId) {
        Account a = accountsDb.getAccountByGoogleId(googleId);
        List<UUID> readNotificationIds = new ArrayList<>();
        if (a != null) {
            readNotificationIds = a.getReadNotifications()
                    .stream()
                    .map(n -> n.getNotification().getNotificationId())
                    .collect(Collectors.toList());
        }
        return readNotificationIds;
    }
}
