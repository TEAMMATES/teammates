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

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.User;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountsLogic}.
 */
public class AccountsLogicTest extends BaseTestCase {

    private AccountsLogic accountsLogic = AccountsLogic.inst();

    private AccountsDb accountsDb;

    private NotificationsLogic notificationsLogic;

    private UsersLogic usersLogic;

    private CoursesLogic coursesLogic;

    @BeforeMethod
    public void setUpMethod() {
        accountsDb = mock(AccountsDb.class);
        notificationsLogic = mock(NotificationsLogic.class);
        usersLogic = mock(UsersLogic.class);
        coursesLogic = mock(CoursesLogic.class);
        accountsLogic.initLogicDependencies(accountsDb, notificationsLogic, usersLogic, coursesLogic);
    }

    @Test
    public void testDeleteAccount_accountExists_success() {
        Account account = getTypicalAccount();

        when(accountsDb.getAccount(account.getId())).thenReturn(account);

        accountsLogic.deleteAccount(account.getId());

        verify(accountsDb, times(1)).deleteAccount(account);
    }

    @Test
    public void testDeleteAccountCascade_accountExists_success() {
        Account account = getTypicalAccount();
        String accountId = account.getId().toString();
        List<User> users = new ArrayList<>();

        for (int i = 0; i < 2; ++i) {
            users.add(getTypicalInstructor());
            users.add(getTypicalStudent());
        }

        when(usersLogic.getAllUsersByAccountId(accountId)).thenReturn(users);
        when(accountsDb.getAccount(account.getId())).thenReturn(account);

        accountsLogic.deleteAccountCascade(accountId);

        for (User user : users) {
            verify(usersLogic, times(1)).deleteUser(user);
        }
        verify(accountsDb, times(1)).deleteAccount(account);
    }

    @Test
    public void testUpdateReadNotifications_shouldReturnCorrectReadNotificationId_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Account account = getTypicalAccount();
        Notification notification = getTypicalNotificationWithId();
        String accountId = account.getId().toString();
        UUID notificationId = notification.getId();

        when(accountsDb.getAccount(account.getId())).thenReturn(account);
        when(notificationsLogic.getNotification(notificationId)).thenReturn(notification);

        List<UUID> readNotificationIds = accountsLogic.updateReadNotifications(accountId, notificationId,
                notification.getEndTime());

        verify(accountsDb, times(1)).getAccount(account.getId());
        verify(notificationsLogic, times(1)).getNotification(notificationId);

        assertEquals(1, readNotificationIds.size());
        assertEquals(notificationId, readNotificationIds.get(0));
    }

    @Test
    public void testUpdateReadNotifications_shouldAddReadNotificationToAccount_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Account account = getTypicalAccount();
        Notification notification = getTypicalNotificationWithId();
        String accountId = account.getId().toString();
        UUID notificationId = notification.getId();

        when(accountsDb.getAccount(account.getId())).thenReturn(account);
        when(notificationsLogic.getNotification(notificationId)).thenReturn(notification);

        accountsLogic.updateReadNotifications(accountId, notificationId, notification.getEndTime());

        verify(accountsDb, times(1)).getAccount(account.getId());
        verify(notificationsLogic, times(1)).getNotification(notificationId);

        List<ReadNotification> accountReadNotifications = account.getReadNotifications();
        assertEquals(1, accountReadNotifications.size());
        ReadNotification readNotification = accountReadNotifications.get(0);
        assertSame(account, readNotification.getAccount());
        assertSame(notification, readNotification.getNotification());
    }

    @Test
    public void testUpdateReadNotifications_accountDoesNotExist_throwEntityDoesNotExistException() {
        Account account = getTypicalAccount();
        Notification notification = getTypicalNotificationWithId();
        String accountId = account.getId().toString();
        UUID notificationId = notification.getId();

        when(accountsDb.getAccount(account.getId())).thenReturn(null);
        when(notificationsLogic.getNotification(notificationId)).thenReturn(notification);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.updateReadNotifications(accountId, notificationId, notification.getEndTime()));
        assertEquals("Trying to update the read notifications of a non-existent account.", ex.getMessage());
    }

    @Test
    public void testUpdateReadNotifications_notificationDoesNotExist_throwEntityDoesNotExistException() {
        Account account = getTypicalAccount();
        Notification notification = getTypicalNotificationWithId();
        String accountId = account.getId().toString();
        UUID notificationId = notification.getId();

        when(accountsDb.getAccount(account.getId())).thenReturn(account);
        when(notificationsLogic.getNotification(notificationId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.updateReadNotifications(accountId, notificationId, notification.getEndTime()));
        assertEquals("Trying to mark as read a notification that does not exist.", ex.getMessage());
    }

    @Test
    public void testUpdateReadNotifications_markExpiredNotificationAsRead_throwInvalidParametersException() {
        Account account = getTypicalAccount();
        Notification notification = getTypicalNotificationWithId();
        notification.setEndTime(Instant.parse("2012-01-01T00:00:00Z"));
        String accountId = account.getId().toString();
        UUID notificationId = notification.getId();

        when(accountsDb.getAccount(account.getId())).thenReturn(account);
        when(notificationsLogic.getNotification(notificationId)).thenReturn(notification);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> accountsLogic.updateReadNotifications(accountId, notificationId, notification.getEndTime()));
        assertEquals("Trying to mark an expired notification as read.", ex.getMessage());
    }

    @Test
    public void testGetReadNotificationsId_doesNotHaveReadNotifications_success() {
        Account account = getTypicalAccount();
        String accountId = account.getId().toString();
        when(accountsDb.getAccount(account.getId())).thenReturn(account);

        List<UUID> readNotifications = accountsLogic.getReadNotificationsId(accountId);

        assertEquals(0, readNotifications.size());
    }

    @Test
    public void testGetReadNotificationsId_hasReadNotifications_success() {
        Account account = getTypicalAccount();
        List<ReadNotification> readNotifications = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Notification notification = getTypicalNotificationWithId();
            ReadNotification readNotification = new ReadNotification(account, notification);
            readNotifications.add(readNotification);
        }
        account.setReadNotifications(readNotifications);

        String accountId = account.getId().toString();
        when(accountsDb.getAccount(account.getId())).thenReturn(account);

        List<UUID> actualReadNotifications = accountsLogic.getReadNotificationsId(accountId);

        assertEquals(10, actualReadNotifications.size());

        for (int i = 0; i < 10; i++) {
            assertEquals(readNotifications.get(i).getNotification().getId(),
                    actualReadNotifications.get(i));
        }
    }
}
