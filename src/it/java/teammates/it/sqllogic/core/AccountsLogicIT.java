package teammates.it.sqllogic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.core.AccountsLogic;
import teammates.sqllogic.core.NotificationsLogic;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;

/**
 * SUT: {@link AccountsLogic}.
 */
public class AccountsLogicIT extends BaseTestCaseWithSqlDatabaseAccess {

    private AccountsLogic accountsLogic = AccountsLogic.inst();
    private NotificationsLogic notificationsLogic = NotificationsLogic.inst();

    private AccountsDb accountsDb = AccountsDb.inst();

    @Test
    public void testUpdateReadNotifications()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        ______TS("success: mark notification as read");
        Account account = new Account("google-id", "name", "email@teammates.com");
        Notification notification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        accountsDb.createAccount(account);
        notificationsLogic.createNotification(notification);

        String googleId = account.getGoogleId();
        UUID notificationId = notification.getId();
        accountsLogic.updateReadNotifications(googleId, notificationId, notification.getEndTime());

        Account actualAccount = accountsDb.getAccountByGoogleId(googleId);
        List<ReadNotification> accountReadNotifications = actualAccount.getReadNotifications();
        assertEquals(1, accountReadNotifications.size());
        assertSame(actualAccount, accountReadNotifications.get(0).getAccount());
        assertSame(notification, accountReadNotifications.get(0).getNotification());
    }
}
