package teammates.storage.api;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.NotificationType;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.Notification;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link NotificationsDb}.
 */
public class NotificationsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    private final NotificationsDb notificationsDb = NotificationsDb.inst();

    @Test
    public void testGetNotification() throws Exception {
        NotificationAttributes n = createNewNotification();

        ______TS("typical success case");
        NotificationAttributes retrieved = notificationsDb.getNotification(n.getNotificationId());
        assertNotNull(retrieved);

        ______TS("expect null for non-existent account");
        retrieved = notificationsDb.getNotification("non.existent");
        assertNull(retrieved);

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> notificationsDb.getNotification(null));

        // delete created account
        notificationsDb.deleteNotification(n.getNotificationId());
    }

    @Test
    public void testCreateNotification() throws Exception {
        ______TS("typical success case");
        String uuid = UUID.randomUUID().toString();
        NotificationAttributes n = NotificationAttributes.builder(uuid)
                .withTitle("title")
                .withMessage("demo message")
                .withType(NotificationType.DEPRECATION)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withStartTime(Instant.now())
                .withEndTime(Instant.now().plusSeconds(3600))
                .build();

        notificationsDb.createEntity(n);

        ______TS("duplicate notification, creation fail");
        NotificationAttributes dup = NotificationAttributes.builder(uuid)
                .withTitle("title2")
                .withMessage("demo message2")
                .withType(NotificationType.MAINTENANCE)
                .withTargetUser(NotificationTargetUser.STUDENT)
                .withStartTime(Instant.now())
                .withEndTime(Instant.now().plusSeconds(3600))
                .build();

        assertThrows(EntityAlreadyExistsException.class, () -> notificationsDb.createEntity(dup));

        ______TS("failure: null parameters");
        assertThrows(AssertionError.class, () -> notificationsDb.createEntity(null));
    }

    // TODO: for extension, some fields are not allowed to be updated after shown is true
    @Test
    public void testUpdateNotification() throws Exception {
        Instant timeNow = Instant.now();
        NotificationAttributes n = notificationsDb.createEntity(NotificationAttributes.builder(UUID.randomUUID().toString())
                .withTitle("title")
                .withMessage("message")
                .withType(NotificationType.DEPRECATION)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withStartTime(timeNow)
                .withEndTime(timeNow.plusSeconds(3600))
                .build());

        ______TS("success: single field - title");
        assertEquals("title", notificationsDb.getNotification(n.getNotificationId()).getTitle());
        assertEquals("diff title", notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle("diff title")
                        .build()).getTitle());
        assertEquals("diff title", notificationsDb.getNotification(n.getNotificationId()).getTitle());

        ______TS("success: single field - message");
        assertEquals("message", notificationsDb.getNotification(n.getNotificationId()).getMessage());
        assertEquals("diff message", notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withMessage("diff message")
                        .build()).getMessage());
        assertEquals("diff message", notificationsDb.getNotification(n.getNotificationId()).getMessage());

        ______TS("success: single field - type");
        assertEquals(NotificationType.DEPRECATION, notificationsDb.getNotification(n.getNotificationId()).getType());
        assertEquals(NotificationType.MAINTENANCE, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withType(NotificationType.MAINTENANCE)
                        .build()).getType());
        assertEquals(NotificationType.MAINTENANCE, notificationsDb.getNotification(n.getNotificationId()).getType());

        ______TS("success: single field - targetUser");
        assertEquals(NotificationTargetUser.GENERAL, notificationsDb.getNotification(n.getNotificationId()).getTargetUser());
        assertEquals(NotificationTargetUser.STUDENT, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTargetUser(NotificationTargetUser.STUDENT)
                        .build()).getTargetUser());
        assertEquals(NotificationTargetUser.STUDENT, notificationsDb.getNotification(n.getNotificationId()).getTargetUser());

        ______TS("success: single field - startTime");
        assertEquals(timeNow, notificationsDb.getNotification(n.getNotificationId()).getStartTime());
        assertEquals(timeNow.plusSeconds(600), notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withStartTime(timeNow.plusSeconds(600))
                        .build()).getStartTime());
        assertEquals(timeNow.plusSeconds(600), notificationsDb.getNotification(n.getNotificationId()).getStartTime());

        ______TS("success: single field - endTime");
        assertEquals(timeNow.plusSeconds(3600), notificationsDb.getNotification(n.getNotificationId()).getEndTime());
        assertEquals(timeNow.plusSeconds(4200), notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withEndTime(timeNow.plusSeconds(4200))
                        .build()).getEndTime());
        assertEquals(timeNow.plusSeconds(4200), notificationsDb.getNotification(n.getNotificationId()).getEndTime());

        ______TS("success: single field - shown");
        assertFalse(notificationsDb.getNotification(n.getNotificationId()).isShown());
        assertTrue(notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withShown()
                        .build()).isShown());
        assertTrue(notificationsDb.getNotification(n.getNotificationId()).isShown());

        ______TS("success: bulk update");
        // We will try to revert back to the original attribute to test bulk update
        // Note: Update of shown is not revertable so not to check it here
        notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle("title")
                        .withMessage("message")
                        .withType(NotificationType.DEPRECATION)
                        .withTargetUser(NotificationTargetUser.GENERAL)
                        .withStartTime(timeNow)
                        .withEndTime(timeNow.plusSeconds(3600))
                        .build());
        assertEquals("title", notificationsDb.getNotification(n.getNotificationId()).getTitle());
        assertEquals("message", notificationsDb.getNotification(n.getNotificationId()).getMessage());
        assertEquals(NotificationType.DEPRECATION, notificationsDb.getNotification(n.getNotificationId()).getType());
        assertEquals(NotificationTargetUser.GENERAL, notificationsDb.getNotification(n.getNotificationId()).getTargetUser());
        assertEquals(timeNow, notificationsDb.getNotification(n.getNotificationId()).getStartTime());
        assertEquals(timeNow.plusSeconds(3600), notificationsDb.getNotification(n.getNotificationId()).getEndTime());
        assertTrue(notificationsDb.getNotification(n.getNotificationId()).isShown());
    }

    @Test
    public void testDeleteNotification() throws Exception {
        ______TS("silent deletion of non-existant notification");
        notificationsDb.deleteNotification("non.existent");

        NotificationAttributes n = createNewNotification();
        ______TS("typical success case");
        assertNotNull(notificationsDb.getNotification(n.getNotificationId()));
        notificationsDb.deleteNotification(n.getNotificationId());
        assertNull(notificationsDb.getNotification(n.getNotificationId()));

        ______TS("silent deletion of the same key twice");
        notificationsDb.deleteNotification(n.getNotificationId());

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> notificationsDb.deleteNotification(null));
    }

    @Test
    public void testHasExistingEntities() throws Exception {
        NotificationAttributes n = getNewNotificationAttributes();

        ______TS("false before entity creation");
        assertEquals(false, notificationsDb.hasExistingEntities(n));

        ______TS("true after entity creation");
        notificationsDb.createEntity(n);
        assertEquals(true, notificationsDb.hasExistingEntities(n));

        notificationsDb.deleteNotification(n.getNotificationId());
    }

    @Test
    public void testMakeAttributes() {
        ______TS("typical success conversion");
        Notification entity = new Notification(UUID.randomUUID().toString(), Instant.now(), Instant.now().plusSeconds(3600),
                NotificationType.DEPRECATION, NotificationTargetUser.GENERAL, "title", "demo message",
                false, Instant.now(), Instant.now());
        NotificationAttributes attributes = notificationsDb.makeAttributes(entity);
        assertEquals(entity.getNotificationId(), attributes.getNotificationId());
        assertEquals(entity.getStartTime(), attributes.getStartTime());
        assertEquals(entity.getEndTime(), attributes.getEndTime());
        assertEquals(entity.getType(), attributes.getType());
        assertEquals(entity.getTargetUser(), attributes.getTargetUser());
        assertEquals(entity.getTitle(), attributes.getTitle());
        assertEquals(entity.getMessage(), attributes.getMessage());
        assertEquals(entity.getCreatedAt(), attributes.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), attributes.getUpdatedAt());
        assertEquals(entity.isShown(), attributes.isShown());
    }

    private NotificationAttributes createNewNotification()
            throws EntityAlreadyExistsException, InvalidParametersException {
        return notificationsDb.createEntity(getNewNotificationAttributes());
    }

    private NotificationAttributes getNewNotificationAttributes() {
        return NotificationAttributes.builder(UUID.randomUUID().toString())
                .withTitle("title")
                .withMessage("demo message")
                .withType(NotificationType.DEPRECATION)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withStartTime(Instant.now())
                .withEndTime(Instant.now().plusSeconds(3600))
                .build();
    }
}
