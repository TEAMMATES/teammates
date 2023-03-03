package teammates.it.sqllogic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
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
        UUID notificationId = notification.getNotificationId();
        accountsLogic.updateReadNotifications(googleId, notificationId, notification.getEndTime());

        HibernateUtil.flushSession();

        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ReadNotification> cq = cb.createQuery(ReadNotification.class);
        Root<ReadNotification> rootEntry = cq.from(ReadNotification.class);
        CriteriaQuery<ReadNotification> all = cq.select(rootEntry);

        TypedQuery<ReadNotification> allQuery = session.createQuery(all);
        System.out.println(allQuery.getResultStream().findFirst().get().getId());
//        System.out.println(account.getReadNotifications().get(0).getId());

//        ReadNotification r = HibernateUtil.getCurrentSession().get(ReadNotification.class, )

//        account = null;
//        Account actualAccount = accountsDb.getAccountByGoogleId(googleId);
//        notification = null;
//        Notification actualNotification = notificationsLogic.getNotification(notificationId);
//
//        List<ReadNotification> accountReadNotifications = actualAccount.getReadNotifications();
//        assertEquals(1, accountReadNotifications.size());
//        verifyEquals(actualAccount, accountReadNotifications.get(0).getAccount());
//        verifyEquals(actualNotification, accountReadNotifications.get(0).getNotification());
//
//        List<ReadNotification> notificationReadNotifications = actualNotification.getReadNotifications();
//        assertEquals(1, notificationReadNotifications.size());
//        verifyEquals(actualAccount, notificationReadNotifications.get(0).getAccount());
//        verifyEquals(actualNotification, notificationReadNotifications.get(0).getNotification());
    }

    private Account generateTypicalAccount() {
        return new Account("test-googleId", "test-name", "test@test.com");
    }

    private Notification generateTypicalNotificationWithId() {
        Notification notification = new Notification(
                Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                "A deprecation note",
                "<p>Deprecation happens in three minutes</p>");
        notification.setNotificationId(UUID.fromString("00000001-0000-1000-0000-000000000000"));
        return notification;
    }
}
