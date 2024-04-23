package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import teammates.storage.sqlapi.NotificationsDb;
import teammates.storage.sqlentity.Notification;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link NotificationsLogic}.
 */
public class NotificationsLogicTest extends BaseTestCase {

    private NotificationsLogic notificationsLogic = NotificationsLogic.inst();

    private NotificationsDb notificationsDb;

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        notificationsDb = mock(NotificationsDb.class);
        notificationsLogic.initLogicDependencies(notificationsDb);
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void tearDownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateNotification_endTimeIsBeforeStartTime_throwsInvalidParametersException()
            throws EntityAlreadyExistsException {
        Notification invalidNotification = new Notification(Instant.parse("2011-02-01T00:00:00Z"),
                Instant.parse("2011-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");

        assertThrows(InvalidParametersException.class, () -> notificationsLogic.createNotification(invalidNotification));
        verify(notificationsDb, never()).createNotification(invalidNotification);
    }

    @Test
    public void testCreateNotification_emptyTitle_throwsInvalidParametersException() throws EntityAlreadyExistsException {
        Notification invalidNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "", "<p>Deprecation happens in three minutes</p>");

        assertThrows(InvalidParametersException.class, () -> notificationsLogic.createNotification(invalidNotification));
        verify(notificationsDb, never()).createNotification(invalidNotification);
    }

    @Test
    public void testCreateNotification_emptyMessage_throwsInvalidParametersException() throws EntityAlreadyExistsException {
        Notification invalidNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "");

        assertThrows(InvalidParametersException.class, () -> notificationsLogic.createNotification(invalidNotification));
        verify(notificationsDb, never()).createNotification(invalidNotification);
    }

    @Test
    public void testUpdateNotification_entityAlreadyExists_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Notification notification = getTypicalNotificationWithId();
        UUID notificationId = notification.getId();

        when(notificationsDb.getNotification(notificationId)).thenReturn(notification);

        Instant newStartTime = Instant.parse("2012-01-01T00:00:00Z");
        Instant newEndTime = Instant.parse("2098-01-01T00:00:00Z");
        NotificationStyle newStyle = NotificationStyle.DARK;
        NotificationTargetUser newTargetUser = NotificationTargetUser.INSTRUCTOR;
        String newTitle = "An updated deprecation note";
        String newMessage = "<p>Deprecation happens in three seconds</p>";

        Notification updatedNotification = notificationsLogic.updateNotification(notificationId, newStartTime,
                newEndTime, newStyle, newTargetUser, newTitle, newMessage);

        verify(notificationsDb, times(1)).getNotification(notificationId);

        assertEquals(notificationId, updatedNotification.getId());
        assertEquals(newStartTime, updatedNotification.getStartTime());
        assertEquals(newEndTime, updatedNotification.getEndTime());
        assertEquals(newStyle, updatedNotification.getStyle());
        assertEquals(newTargetUser, updatedNotification.getTargetUser());
        assertEquals(newTitle, updatedNotification.getTitle());
        assertEquals(newMessage, updatedNotification.getMessage());
    }

    @Test
    public void testUpdateNotification_invalidNonNullParameter_endTimeBeforeStartTime() {
        Notification notification = getTypicalNotificationWithId();
        UUID notificationId = notification.getId();

        when(notificationsDb.getNotification(notificationId)).thenReturn(notification);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> notificationsLogic.updateNotification(notificationId, Instant.parse("2011-01-01T00:00:01Z"),
                Instant.parse("2011-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>"));

        assertEquals("The time when the notification will expire for this notification cannot be earlier than "
                + "the time when the notification will be visible.", ex.getMessage());
    }

    @Test
    public void testUpdateNotification_invalidNonNullParameter_emptyTitle() {
        Notification notification = getTypicalNotificationWithId();
        UUID notificationId = notification.getId();

        when(notificationsDb.getNotification(notificationId)).thenReturn(notification);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> notificationsLogic.updateNotification(notificationId, Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "", "<p>Deprecation happens in three minutes</p>"));

        assertEquals("The field 'notification title' is empty.", ex.getMessage());
    }

    @Test
    public void testUpdateNotification_invalidNonNullParameter_emptyMessage() {
        Notification notification = getTypicalNotificationWithId();
        UUID notificationId = notification.getId();

        when(notificationsDb.getNotification(notificationId)).thenReturn(notification);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> notificationsLogic.updateNotification(notificationId, Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "An updated deprecation note", ""));

        assertEquals("The field 'notification message' is empty.", ex.getMessage());
    }

    @Test
    public void testUpdateNotification_entityDoesNotExist() {
        Notification notification = getTypicalNotificationWithId();
        UUID notificationId = notification.getId();

        when(notificationsDb.getNotification(notificationId)).thenReturn(notification);

        UUID nonExistentId = UUID.fromString("00000000-0000-1000-0000-000000000000");

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> notificationsLogic.updateNotification(nonExistentId, Instant.parse("2012-01-01T00:00:00Z"),
                        Instant.parse("2098-01-01T00:00:00Z"), NotificationStyle.DARK,
                        NotificationTargetUser.INSTRUCTOR, "An updated deprecation note",
                        "<p>Deprecation happens in three seconds</p>"));

        assertEquals("Trying to update non-existent Entity: " + Notification.class, ex.getMessage());
    }
}
