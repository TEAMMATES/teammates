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
        final String TITLE = "title";
        final String TITLE_DIFF = "title_diff";
        final String MESSAGE = "message";
        final String MESSAGE_DIFF = "message_diff";
        final NotificationType TYPE = NotificationType.DEPRECATION;
        final NotificationType TYPE_DIFF = NotificationType.MAINTENANCE;
        final NotificationTargetUser TARGET_USER = NotificationTargetUser.STUDENT;
        final NotificationTargetUser TARGET_USER_DIFF = NotificationTargetUser.GENERAL;
        final Instant START_TIME = Instant.now();
        final Instant START_TIME_DIFF = START_TIME.plusSeconds(600);
        final Instant END_TIME = START_TIME.plusSeconds(3600);
        final Instant END_TIME_DIFF = END_TIME.plusSeconds(600);

        NotificationAttributes n = notificationsDb.createEntity(NotificationAttributes.builder(UUID.randomUUID().toString())
                .withTitle(TITLE)
                .withMessage(MESSAGE)
                .withType(TYPE)
                .withTargetUser(TARGET_USER)
                .withStartTime(START_TIME)
                .withEndTime(END_TIME)
                .build());

        ______TS("success: single field - title");
        assertEquals(TITLE, notificationsDb.getNotification(n.getNotificationId()).getTitle());
        assertEquals(TITLE_DIFF, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle(TITLE_DIFF)
                        .build()).getTitle());
        assertEquals(TITLE_DIFF, notificationsDb.getNotification(n.getNotificationId()).getTitle());

        ______TS("success: single field - message");
        assertEquals(MESSAGE, notificationsDb.getNotification(n.getNotificationId()).getMessage());
        assertEquals(MESSAGE_DIFF, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withMessage(MESSAGE_DIFF)
                        .build()).getMessage());
        assertEquals(MESSAGE_DIFF, notificationsDb.getNotification(n.getNotificationId()).getMessage());

        ______TS("success: single field - type");
        assertEquals(TYPE, notificationsDb.getNotification(n.getNotificationId()).getType());
        assertEquals(TYPE_DIFF, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withType(TYPE_DIFF)
                        .build()).getType());
        assertEquals(TYPE_DIFF, notificationsDb.getNotification(n.getNotificationId()).getType());

        ______TS("success: single field - targetUser");
        assertEquals(TARGET_USER, notificationsDb.getNotification(n.getNotificationId()).getTargetUser());
        assertEquals(TARGET_USER_DIFF, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTargetUser(TARGET_USER_DIFF)
                        .build()).getTargetUser());
        assertEquals(TARGET_USER_DIFF, notificationsDb.getNotification(n.getNotificationId()).getTargetUser());

        ______TS("success: single field - startTime");
        assertEquals(START_TIME, notificationsDb.getNotification(n.getNotificationId()).getStartTime());
        assertEquals(START_TIME_DIFF, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withStartTime(START_TIME_DIFF)
                        .build()).getStartTime());
        assertEquals(START_TIME_DIFF, notificationsDb.getNotification(n.getNotificationId()).getStartTime());

        ______TS("success: single field - endTime");
        assertEquals(END_TIME, notificationsDb.getNotification(n.getNotificationId()).getEndTime());
        assertEquals(END_TIME_DIFF, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withEndTime(END_TIME_DIFF)
                        .build()).getEndTime());
        assertEquals(END_TIME_DIFF, notificationsDb.getNotification(n.getNotificationId()).getEndTime());

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
        n = notificationsDb.getNotification(n.getNotificationId());
        assertEquals(TITLE_DIFF, n.getTitle());
        assertEquals(MESSAGE_DIFF, n.getMessage());
        assertEquals(TYPE_DIFF, n.getType());
        assertEquals(TARGET_USER_DIFF, n.getTargetUser());
        assertEquals(START_TIME_DIFF, n.getStartTime());
        assertEquals(END_TIME_DIFF, n.getEndTime());
        assertTrue(n.isShown());
        notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle(TITLE)
                        .withMessage(MESSAGE)
                        .withType(TYPE)
                        .withTargetUser(TARGET_USER)
                        .withStartTime(START_TIME)
                        .withEndTime(END_TIME)
                        .build());

        n = notificationsDb.getNotification(n.getNotificationId());
        assertEquals(TITLE, n.getTitle());
        assertEquals(MESSAGE, n.getMessage());
        assertEquals(TYPE, n.getType());
        assertEquals(TARGET_USER, n.getTargetUser());
        assertEquals(START_TIME, n.getStartTime());
        assertEquals(END_TIME, n.getEndTime());
        assertTrue(n.isShown());
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
