package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import java.time.Instant;
import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Notification;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code NotificationsDb}.
 */
public class NotificationsDbTest extends BaseTestCase {

    private NotificationsDb notificationsDb = NotificationsDb.inst();

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateNotification_notificationDoesNotExist_success()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Notification newNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");

        notificationsDb.createNotification(newNotification);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newNotification));
    }

    @Test
    public void testCreateNotification_endTimeIsBeforeStartTime_throwsInvalidParametersException() {
        Notification invalidNotification = new Notification(Instant.parse("2011-02-01T00:00:00Z"),
                Instant.parse("2011-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(invalidNotification), never());
    }

    @Test
    public void testCreateNotification_emptyTitle_throwsInvalidParametersException() {
        Notification invalidNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "", "<p>Deprecation happens in three minutes</p>");

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(invalidNotification), never());
    }

    @Test
    public void testCreateNotification_emptyMessage_throwsInvalidParametersException() {
        Notification invalidNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "");

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(invalidNotification), never());
    }

    @Test
    public void testGetNotification_success() throws EntityAlreadyExistsException, InvalidParametersException {
        Notification notification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        notification.setNotificationId(UUID.randomUUID());
        mockHibernateUtil.when(() ->
                HibernateUtil.get(Notification.class, notification.getNotificationId())).thenReturn(notification);

        Notification actualNotification = notificationsDb.getNotification(notification.getNotificationId());

        assertEquals(notification, actualNotification);
    }

    @Test
    public void testGetNotification_entityDoesNotExist() throws EntityAlreadyExistsException, InvalidParametersException {
        UUID nonExistentId = UUID.fromString("00000000-0000-1000-0000-000000000000");
        mockHibernateUtil.when(() -> HibernateUtil.get(Notification.class, nonExistentId)).thenReturn(null);

        Notification actualNotification = notificationsDb.getNotification(nonExistentId);

        assertNull(actualNotification);
    }

    @Test
    public void testUpdateNotification_success()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Notification notification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        notification.setNotificationId(UUID.randomUUID());
        notificationsDb.createNotification(notification);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(notification));
        mockHibernateUtil.when(() -> HibernateUtil.get(Notification.class, notification.getNotificationId()))
                .thenReturn(notification);

        Instant newStartTime = Instant.parse("2012-01-01T00:00:00Z");
        Instant newEndTime = Instant.parse("2098-01-01T00:00:00Z");
        NotificationStyle newStyle = NotificationStyle.DARK;
        NotificationTargetUser newTargetUser = NotificationTargetUser.INSTRUCTOR;
        String newTitle = "An updated deprecation note";
        String newMessage = "<p>Deprecation happens in three seconds</p>";
        Notification updatedNotification = notificationsDb.updateNotification(notification.getNotificationId(),
                newStartTime, newEndTime, newStyle, newTargetUser, newTitle, newMessage);

        mockHibernateUtil.verify(() -> HibernateUtil.get(Notification.class, notification.getNotificationId()));

        assertEquals(notification.getNotificationId(), updatedNotification.getNotificationId());
        assertEquals(newStartTime, updatedNotification.getStartTime());
        assertEquals(newEndTime, updatedNotification.getEndTime());
        assertEquals(newStyle, updatedNotification.getStyle());
        assertEquals(newTargetUser, updatedNotification.getTargetUser());
        assertEquals(newTitle, updatedNotification.getTitle());
        assertEquals(newMessage, updatedNotification.getMessage());
    }

    @Test
    public void testUpdateNotification_invalidNonNullParameter_endTimeBeforeStartTime()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Notification notification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        notification.setNotificationId(UUID.randomUUID());
        notificationsDb.createNotification(notification);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(notification));
        mockHibernateUtil.when(() -> HibernateUtil.get(Notification.class, notification.getNotificationId()))
                .thenReturn(notification);

        assertThrows(InvalidParametersException.class, () -> notificationsDb.updateNotification(
                notification.getNotificationId(), Instant.parse("2011-01-01T00:00:01Z"),
                Instant.parse("2011-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>"));
    }

    @Test
    public void testUpdateNotification_invalidNonNullParameter_emptyTitle()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Notification notification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        notification.setNotificationId(UUID.randomUUID());
        notificationsDb.createNotification(notification);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(notification));
        mockHibernateUtil.when(() -> HibernateUtil.get(Notification.class, notification.getNotificationId()))
                .thenReturn(notification);

        assertThrows(InvalidParametersException.class, () -> notificationsDb.updateNotification(
                notification.getNotificationId(), Instant.parse("2011-01-01T00:00:01Z"),
                Instant.parse("2011-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "", "<p>Deprecation happens in three minutes</p>"));
    }

    @Test
    public void testUpdateNotification_invalidNonNullParameter_emptyMessage()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Notification notification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        notification.setNotificationId(UUID.randomUUID());
        notificationsDb.createNotification(notification);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(notification));
        mockHibernateUtil.when(() -> HibernateUtil.get(Notification.class, notification.getNotificationId()))
                .thenReturn(notification);

        assertThrows(InvalidParametersException.class, () -> notificationsDb.updateNotification(
                notification.getNotificationId(), Instant.parse("2011-01-01T00:00:01Z"),
                Instant.parse("2011-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "An updated deprecation note", ""));
    }

    @Test
    public void testUpdateNotification_entityDoesNotExist()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Notification notification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        notification.setNotificationId(UUID.randomUUID());
        notificationsDb.createNotification(notification);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(notification));
        mockHibernateUtil.when(() -> HibernateUtil.get(Notification.class, notification.getNotificationId()))
                .thenReturn(notification);

        UUID nonExistentId = UUID.fromString("00000000-0000-1000-0000-000000000000");

        assertThrows(EntityDoesNotExistException.class, () -> notificationsDb.updateNotification(nonExistentId,
                Instant.parse("2012-01-01T00:00:00Z"), Instant.parse("2098-01-01T00:00:00Z"), NotificationStyle.DARK,
                NotificationTargetUser.INSTRUCTOR, "An updated deprecation note",
                "<p>Deprecation happens in three seconds</p>"));
    }
}
