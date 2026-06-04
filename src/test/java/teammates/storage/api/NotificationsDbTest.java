package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.storage.entity.Account;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.test.GroupNames;

/**
 * Tests for {@link NotificationsDb}.
 */
public class NotificationsDbTest extends BaseDbTestcase {
    private final NotificationsDb notificationsDb = NotificationsDb.inst();

    @Test(groups = GroupNames.DB)
    public void getNotification_notificationExists_returnsNotification() {
        UUID notificationId = given.notification("notification");
        persistGivenData(given);

        Notification actual = inTransaction(() -> notificationsDb.getNotification(notificationId));

        assertNotNull(actual);
        assertEquals(notificationId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getNotification_notificationDoesNotExist_returnsNull() {
        given.notification("different-notification");
        persistGivenData(given);

        Notification actual = inTransaction(
                () -> notificationsDb.getNotification(given.uuid("non-existent-notification")));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void persistNotification_notificationIsNew_notificationIsPersisted() {
        UUID notificationId = given.uuid("notification");
        Notification notification = buildDefaultNotification(notificationId);

        Notification actual = inTransaction(() -> notificationsDb.persistNotification(notification));

        assertEquals(notificationId, actual.getId());
        verifyPresentInDatabase(Notification.class, notificationId);
    }

    @Test(groups = GroupNames.DB)
    public void removeNotification_notificationExists_notificationIsRemoved() {
        UUID notificationId = given.notification("notification");
        persistGivenData(given);

        inTransaction(() -> notificationsDb.removeNotification(notificationsDb.getNotification(notificationId)));

        verifyAbsentInDatabase(Notification.class, notificationId);
    }

    @Test(groups = GroupNames.DB)
    public void getNotificationsByTargetUsers_activeOnly_returnsActiveMatchingNotificationsInStartTimeOrder() {
        Instant now = Instant.now();
        UUID activeGeneralNotificationId1 = given.notification("active-general-notification-1",
                n -> n.startTime(now.minus(2, ChronoUnit.HOURS)).endTime(now.plus(1, ChronoUnit.HOURS)).forGeneral());
        UUID activeGeneralNotificationId2 = given.notification("active-general-notification-2",
                n -> n.startTime(now.minus(1, ChronoUnit.HOURS)).endTime(now.plus(1, ChronoUnit.HOURS)).forGeneral());
        given.notification("expired-general-notification", n -> n.expired().forGeneral());
        given.notification("yet-to-be-shown-general-notification", n -> n.yetToBeShown().forGeneral());
        given.notification("active-student-notification", n -> n.active().forStudent());
        persistGivenData(given);

        List<Notification> actual = inTransaction(() -> notificationsDb.getNotificationsByTargetUsers(
                List.of(NotificationTargetUser.GENERAL), true));

        assertEquals(List.of(activeGeneralNotificationId1, activeGeneralNotificationId2),
                actual.stream().map(Notification::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getNotificationsByTargetUsers_notActiveOnly_returnsMatchingNotificationsRegardlessOfTime() {
        UUID activeInstructorNotificationId = given.notification("active-instructor-notification",
                n -> n.active().forInstructor());
        UUID expiredInstructorNotificationId = given.notification("expired-instructor-notification",
                n -> n.expired().forInstructor());
        given.notification("active-student-notification", n -> n.active().forStudent());
        persistGivenData(given);

        List<Notification> actual = inTransaction(() -> notificationsDb.getNotificationsByTargetUsers(
                List.of(NotificationTargetUser.INSTRUCTOR), false));

        assertEquals(List.of(expiredInstructorNotificationId, activeInstructorNotificationId),
                actual.stream().map(Notification::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void persistReadNotification_readNotificationIsNew_readNotificationIsPersisted() {
        UUID accountId = given.account("account");
        UUID notificationId = given.notification("notification");
        persistGivenData(given);
        UUID readNotificationId = given.uuid("read-notification");

        ReadNotification actual = inTransaction(() -> {
            Account account = getEntity(Account.class, accountId);
            Notification notification = getEntity(Notification.class, notificationId);
            ReadNotification readNotification = buildDefaultReadNotification(
                    account, notification, readNotificationId);
            return notificationsDb.persistReadNotification(readNotification);
        });

        assertEquals(readNotificationId, actual.getId());
        verifyPresentInDatabase(ReadNotification.class, readNotificationId);
    }

    @Test(groups = GroupNames.DB)
    public void getReadNotificationsByAccountId_readNotificationsExist_returnsReadNotificationsForAccount() {
        UUID accountId = given.account("account");
        UUID readNotificationId1 = given.readNotification("read-notification-1",
                rn -> rn.account("account").notification("notification-1"));
        UUID readNotificationId2 = given.readNotification("read-notification-2",
                rn -> rn.account("account").notification("notification-2"));
        given.readNotification("another-account-read-notification", rn -> rn.account("another-account"));
        persistGivenData(given);

        List<ReadNotification> actual = inTransaction(() -> notificationsDb.getReadNotificationsByAccountId(accountId));

        assertEquals(Set.of(readNotificationId1, readNotificationId2),
                actual.stream().map(ReadNotification::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void deleteReadNotification_readNotificationExists_readNotificationIsRemoved() {
        UUID readNotificationId = given.readNotification("read-notification");
        persistGivenData(given);

        inTransaction(() -> notificationsDb.deleteReadNotification(
                notificationsDb.getReadNotification(readNotificationId)));

        verifyAbsentInDatabase(ReadNotification.class, readNotificationId);
    }

    private static Notification buildDefaultNotification(UUID notificationId) {
        Instant now = Instant.now();
        Notification notification = new Notification(
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.HOURS),
                NotificationStyle.INFO,
                NotificationTargetUser.GENERAL,
                "Notification Title",
                "<p>Notification Message</p>");
        notification.setId(notificationId);
        return notification;
    }

    private static ReadNotification buildDefaultReadNotification(
            Account account, Notification notification, UUID readNotificationId) {
        assertNotNull(account);
        assertNotNull(notification);
        ReadNotification readNotification = new ReadNotification();
        readNotification.setId(readNotificationId);
        readNotification.setAccount(account);
        notification.addReadNotification(readNotification);
        return readNotification;
    }
}
