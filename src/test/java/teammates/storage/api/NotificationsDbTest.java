package teammates.storage.api;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.NotificationType;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link NotificationsDb}.
 */
public class NotificationsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    // Two sets of attribute values for testing.
    static final String TITLE = "title";
    static final String TITLE_DIFF = "title_diff";
    static final String MESSAGE = "message";
    static final String MESSAGE_DIFF = "message_diff";
    static final NotificationType TYPE = NotificationType.DEPRECATION;
    static final NotificationType TYPE_DIFF = NotificationType.MAINTENANCE;
    static final NotificationTargetUser TARGET_USER = NotificationTargetUser.STUDENT;
    static final NotificationTargetUser TARGET_USER_DIFF = NotificationTargetUser.INSTRUCTOR;
    static final Instant START_TIME = Instant.now();
    static final Instant START_TIME_DIFF = START_TIME.plusSeconds(600);
    static final Instant END_TIME = START_TIME.plusSeconds(3600);
    static final Instant END_TIME_DIFF = END_TIME.plusSeconds(600);

    static final String NON_EXISTENT_ID = "invalid_notification_id";

    private final NotificationsDb notificationsDb = NotificationsDb.inst();

    @Test
    public void testGetNotification() throws Exception {
        NotificationAttributes n = createNewNotification();

        ______TS("typical success case");
        NotificationAttributes retrieved = notificationsDb.getNotification(n.getNotificationId());
        assertNotNull(retrieved);

        ______TS("expect null for non-existent account");
        retrieved = notificationsDb.getNotification(NON_EXISTENT_ID);
        assertNull(retrieved);

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> notificationsDb.getNotification(null));

        // delete created account
        notificationsDb.deleteNotification(n.getNotificationId());
    }

    @Test
    public void testGetAllNotifications() throws Exception {
        ______TS("typical success case");
        final int numOfNotifications = 10;

        Set<String> notificationIds = new HashSet<>();
        // Create 10 new notifications and set the occurance to be false.
        for (int i = 0; i < numOfNotifications; i++) {
            NotificationAttributes n = createNewNotification();
            notificationIds.add(n.getNotificationId());
        }

        List<NotificationAttributes> retrieved = notificationsDb.getAllNotifications();

        assertNotNull(retrieved);
        // Make sure the same 10 notifications are retrieved.
        assertEquals(numOfNotifications, retrieved.size());
        retrieved.forEach(n -> {
            assertNotNull(n);
            assertTrue(notificationIds.contains(n.getNotificationId()));
            notificationIds.remove(n.getNotificationId());

            // delete created account
            notificationsDb.deleteNotification(n.getNotificationId());
        });
    }

    @Test
    public void testGetActiveNotificationsByTargetUser() throws Exception {
        // Conditions for this API: endTime > now, startTime < now, targetUser == specified target user
        ______TS("typical success case");
        final int numOfNotStartedNotification = 5;
        final int numOfOtherOngoingNotification = 5;
        final int numOfExpectedOngoingNotification = 10;
        final int numOfExpiredNotification = 5;

        Set<String> allNotificationIds = new HashSet<>();
        Set<String> expectedNotificationIds = new HashSet<>();

        // Add some notifications that are not started yet. They should not be returned.
        for (int i = 0; i < numOfNotStartedNotification; i++) {
            allNotificationIds.add(notificationsDb.createEntity(NotificationAttributes.builder(UUID.randomUUID().toString())
                    .withTitle(TITLE)
                    .withMessage(MESSAGE)
                    .withType(TYPE)
                    .withTargetUser(TARGET_USER)
                    .withStartTime(Instant.now().plusSeconds(3600))
                    .withEndTime(Instant.now().plusSeconds(4200))
                    .build()).getNotificationId());
        }

        // Add some notifications that have expired. They should not be returned.
        for (int i = 0; i < numOfExpiredNotification; i++) {
            allNotificationIds.add(notificationsDb.createEntity(NotificationAttributes.builder(UUID.randomUUID().toString())
                    .withTitle(TITLE)
                    .withMessage(MESSAGE)
                    .withType(TYPE)
                    .withTargetUser(TARGET_USER)
                    .withStartTime(Instant.now().minusSeconds(4200))
                    .withEndTime(Instant.now().minusSeconds(3600))
                    .build()).getNotificationId());
        }

        // Add some ongoing notifications but not the target user we are interested. They should not be returned.
        for (int i = 0; i < numOfOtherOngoingNotification; i++) {
            allNotificationIds.add(notificationsDb.createEntity(NotificationAttributes.builder(UUID.randomUUID().toString())
                    .withTitle(TITLE)
                    .withMessage(MESSAGE)
                    .withType(TYPE)
                    .withTargetUser(TARGET_USER_DIFF)
                    .withStartTime(Instant.now().minusSeconds(3600))
                    .withEndTime(Instant.now().plusSeconds(3600))
                    .build()).getNotificationId());
        }

        // Create 2 at a time, one with expected target user and one with GENERAL
        // All of these created notifications should be returned
        for (int i = 0; i < numOfExpectedOngoingNotification; i += 2) {
            NotificationAttributes n = notificationsDb.createEntity(
                    NotificationAttributes.builder(UUID.randomUUID().toString())
                            .withTitle(TITLE)
                            .withMessage(MESSAGE)
                            .withType(TYPE)
                            .withTargetUser(TARGET_USER)
                            .withStartTime(Instant.now().minusSeconds(3600))
                            .withEndTime(Instant.now().plusSeconds(3600))
                            .build());

            allNotificationIds.add(n.getNotificationId());
            expectedNotificationIds.add(n.getNotificationId());

            n = notificationsDb.createEntity(NotificationAttributes.builder(UUID.randomUUID().toString())
                    .withTitle(TITLE)
                    .withMessage(MESSAGE)
                    .withType(TYPE)
                    .withTargetUser(NotificationTargetUser.GENERAL)
                    .withStartTime(Instant.now().minusSeconds(3600))
                    .withEndTime(Instant.now().plusSeconds(3600))
                    .build());

            allNotificationIds.add(n.getNotificationId());
            expectedNotificationIds.add(n.getNotificationId());
        }

        List<NotificationAttributes> retrieved = notificationsDb.getActiveNotificationsByTargetUser(TARGET_USER);

        assertNotNull(retrieved);
        assertEquals(numOfExpectedOngoingNotification, retrieved.size());
        retrieved.forEach(n -> {
            assertNotNull(n);
            assertTrue(expectedNotificationIds.contains(n.getNotificationId()));
            expectedNotificationIds.remove(n.getNotificationId());
        });

        // delete created account
        allNotificationIds.forEach(notificationsDb::deleteNotification);
    }

    @Test
    public void testCreateNotification() throws Exception {
        ______TS("typical success case");
        NotificationAttributes n = createNewNotification();

        ______TS("failure: duplicate notification with the same ID");
        assertThrows(EntityAlreadyExistsException.class, () -> notificationsDb.createEntity(n));

        notificationsDb.deleteNotification(n.getNotificationId());

        ______TS("failure: invalid non-null parameters");
        n.setTitle("");
        assertThrows(InvalidParametersException.class, () -> notificationsDb.createEntity(n));

        notificationsDb.deleteNotification(n.getNotificationId());

        ______TS("failure: null parameters");
        assertThrows(AssertionError.class, () -> notificationsDb.createEntity(null));
    }

    // TODO: for extension, some fields are not allowed to be updated after shown is true
    @Test
    public void testUpdateNotification() throws Exception {
        ______TS("failure: update non-existent notification");
        assertThrows(EntityDoesNotExistException.class, () ->
                notificationsDb.updateNotification(NotificationAttributes.updateOptionsBuilder(NON_EXISTENT_ID)
                        .withTitle(TITLE)
                        .withMessage(MESSAGE)
                        .withType(TYPE)
                        .withTargetUser(TARGET_USER)
                        .withStartTime(START_TIME)
                        .withEndTime(END_TIME)
                        .build()));

        ______TS("failure: invalid non-null parameters");
        // Empty title is used here, which triggers InvalidParametersException
        NotificationAttributes n = createNewNotification();
        assertThrows(InvalidParametersException.class, () ->
                notificationsDb.updateNotification(NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle("")
                        .build()));

        notificationsDb.deleteNotification(n.getNotificationId());

        ______TS("failure: null update options");
        assertThrows(AssertionError.class, () -> notificationsDb.updateNotification(null));
    }

    @Test
    public void testUpdateNotification_bulkUpdate_shouldUpdateSuccessfully() throws Exception {
        ______TS("success: bulk update");
        // We will try to revert back to the original attribute to test bulk update
        // Note: Update of shown is not revertable so not to check it here
        NotificationAttributes n = createNewDiffNotification();
        assertEquals(TITLE_DIFF, n.getTitle());
        assertEquals(MESSAGE_DIFF, n.getMessage());
        assertEquals(TYPE_DIFF, n.getType());
        assertEquals(TARGET_USER_DIFF, n.getTargetUser());
        assertEquals(START_TIME_DIFF, n.getStartTime());
        assertEquals(END_TIME_DIFF, n.getEndTime());
        assertFalse(n.isShown());
        notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle(TITLE)
                        .withMessage(MESSAGE)
                        .withType(TYPE)
                        .withTargetUser(TARGET_USER)
                        .withStartTime(START_TIME)
                        .withEndTime(END_TIME)
                        .withShown()
                        .build());

        n = notificationsDb.getNotification(n.getNotificationId());
        assertEquals(TITLE, n.getTitle());
        assertEquals(MESSAGE, n.getMessage());
        assertEquals(TYPE, n.getType());
        assertEquals(TARGET_USER, n.getTargetUser());
        assertEquals(START_TIME, n.getStartTime());
        assertEquals(END_TIME, n.getEndTime());
        assertTrue(n.isShown());

        notificationsDb.deleteNotification(n.getNotificationId());
    }

    @Test
    public void testUpdateNotification_singleFieldUpdate_shouldUpdateSuccessfully() throws Exception {
        NotificationAttributes n = createNewNotification();

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

        notificationsDb.deleteNotification(n.getNotificationId());
    }

    @Test
    public void testDeleteNotification() throws Exception {
        ______TS("silent deletion of non-existant notification");
        notificationsDb.deleteNotification(NON_EXISTENT_ID);

        NotificationAttributes n = createNewNotification();
        ______TS("typical success case");
        assertNotNull(notificationsDb.getNotification(n.getNotificationId()));
        notificationsDb.deleteNotification(n.getNotificationId());
        assertNull(notificationsDb.getNotification(n.getNotificationId()));

        ______TS("silent deletion of the same notification twice");
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

    private NotificationAttributes createNewNotification()
            throws EntityAlreadyExistsException, InvalidParametersException {
        return notificationsDb.createEntity(getNewNotificationAttributes());
    }

    private NotificationAttributes getNewNotificationAttributes() {
        return NotificationAttributes.builder(UUID.randomUUID().toString())
                .withTitle(TITLE)
                .withMessage(MESSAGE)
                .withType(TYPE)
                .withTargetUser(TARGET_USER)
                .withStartTime(START_TIME)
                .withEndTime(END_TIME)
                .build();
    }

    private NotificationAttributes createNewDiffNotification()
            throws EntityAlreadyExistsException, InvalidParametersException {
        return notificationsDb.createEntity(getNewDiffNotificationAttributes());
    }

    private NotificationAttributes getNewDiffNotificationAttributes() {
        return NotificationAttributes.builder(UUID.randomUUID().toString())
                .withTitle(TITLE_DIFF)
                .withMessage(MESSAGE_DIFF)
                .withType(TYPE_DIFF)
                .withTargetUser(TARGET_USER_DIFF)
                .withStartTime(START_TIME_DIFF)
                .withEndTime(END_TIME_DIFF)
                .build();
    }
}
