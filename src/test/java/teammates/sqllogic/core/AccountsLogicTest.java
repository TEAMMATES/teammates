package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountsLogic}.
 */
public class AccountsLogicTest extends BaseTestCase {

    private AccountsLogic accountsLogic = AccountsLogic.inst();

    private AccountsDb accountsDb;

    private NotificationsLogic notificationsLogic;

    @BeforeMethod
    public void setUpMethod() {
        accountsDb = mock(AccountsDb.class);
        notificationsLogic = mock(NotificationsLogic.class);
        accountsLogic.initLogicDependencies(accountsDb, notificationsLogic);
    }

    @Test
    public void testUpdateReadNotifications_success() throws InvalidParametersException, EntityDoesNotExistException {
        Account account = generateTypicalAccount();
        Notification notification = generateTypicalNotification();
        String googleId = account.getGoogleId();
        UUID notificationId = notification.getId();

        when(accountsDb.getAccountByGoogleId(googleId)).thenReturn(account);
        when(notificationsLogic.getNotification(notificationId)).thenReturn(notification);

        List<UUID> readNotificationIds =
                accountsLogic.updateReadNotifications(googleId, notificationId, notification.getEndTime());

        verify(accountsDb, times(1)).getAccountByGoogleId(googleId);
        verify(notificationsLogic, times(1)).getNotification(notificationId);

        assertEquals(1, readNotificationIds.size());
        assertEquals(notificationId, readNotificationIds.get(0));

        List<ReadNotification> accountReadNotifications = account.getReadNotifications();
        assertEquals(1, accountReadNotifications.size());
        ReadNotification readNotification = accountReadNotifications.get(0);
        assertEquals(account, readNotification.getAccount());
        assertEquals(notification, readNotification.getNotification());

        List<ReadNotification> notificationReadNotifications = notification.getReadNotifications();
        assertEquals(1, notificationReadNotifications.size());
        readNotification = notificationReadNotifications.get(0);
        assertEquals(account, readNotification.getAccount());
        assertEquals(notification, readNotification.getNotification());
    }

    @Test
    public void testUpdateReadNotifications_accountDoesNotExist_throwEntityDoesNotExistException() {
        Account account = generateTypicalAccount();
        Notification notification = generateTypicalNotification();
        String googleId = account.getGoogleId();
        UUID notificationId = notification.getId();

        when(accountsDb.getAccountByGoogleId(googleId)).thenReturn(null);
        when(notificationsLogic.getNotification(notificationId)).thenReturn(notification);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.updateReadNotifications(googleId, notificationId, notification.getEndTime()));
        assertEquals("Trying to update the read notifications of a non-existent account.", ex.getMessage());
    }

    @Test
    public void testUpdateReadNotifications_notificationDoesNotExist_throwEntityDoesNotExistException() {
        Account account = generateTypicalAccount();
        Notification notification = generateTypicalNotification();
        String googleId = account.getGoogleId();
        UUID notificationId = notification.getId();

        when(accountsDb.getAccountByGoogleId(googleId)).thenReturn(account);
        when(notificationsLogic.getNotification(notificationId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.updateReadNotifications(googleId, notificationId, notification.getEndTime()));
        assertEquals("Trying to mark as read a notification that does not exist.", ex.getMessage());
    }

    @Test
    public void testUpdateReadNotifications_markExpiredNotificationAsRead_throwInvalidParametersException() {
        Account account = generateTypicalAccount();
        Notification notification = generateTypicalNotification();
        notification.setEndTime(Instant.parse("2012-01-01T00:00:00Z"));
        String googleId = account.getGoogleId();
        UUID notificationId = notification.getId();

        when(accountsDb.getAccountByGoogleId(googleId)).thenReturn(account);
        when(notificationsLogic.getNotification(notificationId)).thenReturn(notification);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> accountsLogic.updateReadNotifications(googleId, notificationId, notification.getEndTime()));
        assertEquals("Trying to mark an expired notification as read.", ex.getMessage());
    }

    @Test
    public void testGetReadNotificationsId_doesNotHaveReadNotifications_success() {
        Account account = generateTypicalAccount();
        String googleId = account.getGoogleId();
        when(accountsDb.getAccountByGoogleId(googleId)).thenReturn(account);

        List<UUID> readNotifications = accountsLogic.getReadNotificationsId(googleId);

        assertEquals(0, readNotifications.size());
    }

    @Test
    public void testGetReadNotificationsId_hasReadNotifications_success() {
        Account account = generateTypicalAccount();
        List<ReadNotification> readNotifications = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Notification notification = generateTypicalNotification();
            ReadNotification readNotification = new ReadNotification(account, notification);
            readNotifications.add(readNotification);
        }
        account.setReadNotifications(readNotifications);

        String googleId = account.getGoogleId();
        when(accountsDb.getAccountByGoogleId(googleId)).thenReturn(account);

        List<UUID> actualReadNotifications = accountsLogic.getReadNotificationsId(googleId);

        assertEquals(10, actualReadNotifications.size());

        for (int i = 0; i < 10; i++) {
            assertEquals(readNotifications.get(i).getNotification().getId(),
                    actualReadNotifications.get(i));
        }
    }

    private Account generateTypicalAccount() {
        return new Account("test-googleId", "test-name", "test@test.com");
    }

    private Notification generateTypicalNotification() {
        return new Notification(
                Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                "A deprecation note",
                "<p>Deprecation happens in three minutes</p>");
    }
}
