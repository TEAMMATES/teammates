package teammates.storage.api;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    final String title = "title";
    final String titleDiff = "title_diff";
    final String message = "message";
    final String messageDiff = "message_diff";
    final NotificationType type = NotificationType.DEPRECATION;
    final NotificationType typeDiff = NotificationType.MAINTENANCE;
    final NotificationTargetUser targetUser = NotificationTargetUser.STUDENT;
    final NotificationTargetUser targetUserDiff = NotificationTargetUser.GENERAL;
    final Instant startTime = Instant.now();
    final Instant startTimeDiff = startTime.plusSeconds(600);
    final Instant endTime = startTime.plusSeconds(3600);
    final Instant endTimeDiff = endTime.plusSeconds(600);

    final String nonExistentId = "invalid_notification_id";

    private final NotificationsDb notificationsDb = NotificationsDb.inst();

    @Test
    public void testGetNotification() throws Exception {
        NotificationAttributes n = createNewNotification();

        ______TS("typical success case");
        NotificationAttributes retrieved = notificationsDb.getNotification(n.getNotificationId());
        assertNotNull(retrieved);

        ______TS("expect null for non-existent account");
        retrieved = notificationsDb.getNotification(nonExistentId);
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

        Map<String, Boolean> occurances = new HashMap<String, Boolean>();
        // Create 10 new notifications and set the occurance to be false.
        for (int i = 0; i < numOfNotifications; i++) {
            NotificationAttributes n = createNewNotification();
            occurances.put(n.getNotificationId(), false);
        }

        List<NotificationAttributes> retrieved = notificationsDb.getAllNotifications();

        assertNotNull(retrieved);
        // Make sure the same 10 notifications are retrieved.
        assertEquals(numOfNotifications, retrieved.size());
        retrieved.forEach(n -> {
            assertNotNull(n);
            assertTrue(occurances.containsKey(n.getNotificationId()));
            assertFalse(occurances.get(n.getNotificationId()));
            occurances.put(n.getNotificationId(), true);

            // delete created account
            notificationsDb.deleteNotification(n.getNotificationId());
        });
    }

    @Test
    public void testGetActiveNotificationsByTargetUser() throws Exception {
        // TODO: to be implemented
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
                notificationsDb.updateNotification(NotificationAttributes.updateOptionsBuilder(nonExistentId)
                        .withTitle(title)
                        .withMessage(message)
                        .withType(type)
                        .withTargetUser(targetUser)
                        .withStartTime(startTime)
                        .withEndTime(endTime)
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
        assertEquals(titleDiff, n.getTitle());
        assertEquals(messageDiff, n.getMessage());
        assertEquals(typeDiff, n.getType());
        assertEquals(targetUserDiff, n.getTargetUser());
        assertEquals(startTimeDiff, n.getStartTime());
        assertEquals(endTimeDiff, n.getEndTime());
        assertFalse(n.isShown());
        notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle(title)
                        .withMessage(message)
                        .withType(type)
                        .withTargetUser(targetUser)
                        .withStartTime(startTime)
                        .withEndTime(endTime)
                        .withShown()
                        .build());

        n = notificationsDb.getNotification(n.getNotificationId());
        assertEquals(title, n.getTitle());
        assertEquals(message, n.getMessage());
        assertEquals(type, n.getType());
        assertEquals(targetUser, n.getTargetUser());
        assertEquals(startTime, n.getStartTime());
        assertEquals(endTime, n.getEndTime());
        assertTrue(n.isShown());

        notificationsDb.deleteNotification(n.getNotificationId());
    }

    @Test
    public void testUpdateNotification_singleFieldUpdate_shouldUpdateSuccessfully() throws Exception {
        NotificationAttributes n = createNewNotification();

        ______TS("success: single field - title");
        assertEquals(title, notificationsDb.getNotification(n.getNotificationId()).getTitle());
        assertEquals(titleDiff, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle(titleDiff)
                        .build()).getTitle());
        assertEquals(titleDiff, notificationsDb.getNotification(n.getNotificationId()).getTitle());

        ______TS("success: single field - message");
        assertEquals(message, notificationsDb.getNotification(n.getNotificationId()).getMessage());
        assertEquals(messageDiff, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withMessage(messageDiff)
                        .build()).getMessage());
        assertEquals(messageDiff, notificationsDb.getNotification(n.getNotificationId()).getMessage());

        ______TS("success: single field - type");
        assertEquals(type, notificationsDb.getNotification(n.getNotificationId()).getType());
        assertEquals(typeDiff, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withType(typeDiff)
                        .build()).getType());
        assertEquals(typeDiff, notificationsDb.getNotification(n.getNotificationId()).getType());

        ______TS("success: single field - targetUser");
        assertEquals(targetUser, notificationsDb.getNotification(n.getNotificationId()).getTargetUser());
        assertEquals(targetUserDiff, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTargetUser(targetUserDiff)
                        .build()).getTargetUser());
        assertEquals(targetUserDiff, notificationsDb.getNotification(n.getNotificationId()).getTargetUser());

        ______TS("success: single field - startTime");
        assertEquals(startTime, notificationsDb.getNotification(n.getNotificationId()).getStartTime());
        assertEquals(startTimeDiff, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withStartTime(startTimeDiff)
                        .build()).getStartTime());
        assertEquals(startTimeDiff, notificationsDb.getNotification(n.getNotificationId()).getStartTime());

        ______TS("success: single field - endTime");
        assertEquals(endTime, notificationsDb.getNotification(n.getNotificationId()).getEndTime());
        assertEquals(endTimeDiff, notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withEndTime(endTimeDiff)
                        .build()).getEndTime());
        assertEquals(endTimeDiff, notificationsDb.getNotification(n.getNotificationId()).getEndTime());

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
        notificationsDb.deleteNotification(nonExistentId);

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
                .withTitle(title)
                .withMessage(message)
                .withType(type)
                .withTargetUser(targetUser)
                .withStartTime(startTime)
                .withEndTime(endTime)
                .build();
    }

    private NotificationAttributes createNewDiffNotification()
            throws EntityAlreadyExistsException, InvalidParametersException {
        return notificationsDb.createEntity(getNewDiffNotificationAttributes());
    }

    private NotificationAttributes getNewDiffNotificationAttributes() {
        return NotificationAttributes.builder(UUID.randomUUID().toString())
                .withTitle(titleDiff)
                .withMessage(messageDiff)
                .withType(typeDiff)
                .withTargetUser(targetUserDiff)
                .withStartTime(startTimeDiff)
                .withEndTime(endTimeDiff)
                .build();
    }
}
