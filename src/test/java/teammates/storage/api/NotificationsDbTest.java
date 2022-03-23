package teammates.storage.api;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link NotificationsDb}.
 */
public class NotificationsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    static final String NON_EXISTENT_ID = "invalid_notification_id";

    private final NotificationsDb notificationsDb = NotificationsDb.inst();
    private final Map<String, NotificationAttributes> typicalNotifications = getTypicalDataBundle().notifications;

    // Notification attributes will be assigned with value at setup()
    private NotificationAttributes n;
    private Set<String> expectedIds = new HashSet<>();

    @BeforeMethod
    public void setup(Method method) throws Exception {
        switch (method.getName()) {
        case "testGetNotification":
        case "testUpdateNotification":
        case "testUpdateNotification_bulkUpdate_shouldUpdateSuccessfully":
        case "testUpdateNotification_singleFieldUpdate_shouldUpdateSuccessfully":
        case "testDeleteNotification":
            n = notificationsDb.createEntity(typicalNotifications.get("notification1"));
            break;
        case "testGetAllNotifications":
            for (NotificationAttributes n : typicalNotifications.values()) {
                notificationsDb.createEntity(n);
                expectedIds.add(n.getNotificationId());
            }
            break;
        case "testGetActiveNotificationsByTargetUser":
        case "testCreateNotification":
        case "testHasExistingEntities":
            // Do nothing - leave the initialization to the test method.
            break;
        default:
            // All test methods should be covered above, so this should not be reached.
            assert false;
            break;
        }
    }

    /**
     * Removes all notifications created by each test.
     */
    @AfterMethod
    public void cleanUp() {
        List<NotificationAttributes> retrieved = notificationsDb.getAllNotifications();
        assertNotNull(retrieved);

        ______TS("clean up: delete " + retrieved.size() + " notifications");
        retrieved.forEach(n -> notificationsDb.deleteNotification(n.getNotificationId()));

        n = null;
        expectedIds.clear();
    }

    @Test
    public void testGetNotification() throws Exception {
        ______TS("typical success case");
        NotificationAttributes retrieved = notificationsDb.getNotification(n.getNotificationId());
        assertNotNull(retrieved);

        ______TS("expect null for non-existent account");
        retrieved = notificationsDb.getNotification(NON_EXISTENT_ID);
        assertNull(retrieved);

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> notificationsDb.getNotification(null));
    }

    @Test
    public void testGetAllNotifications() throws Exception {
        ______TS("typical success case");
        List<NotificationAttributes> retrieved = notificationsDb.getAllNotifications();

        // Verifies that what was retrieved is exactly the same as what was created.
        this.verifyNotifications(retrieved, expectedIds);
    }

    @Test
    public void testGetActiveNotificationsByTargetUser() throws Exception {
        // Conditions for this API: endTime > now, startTime < now, targetUser == specified target user
        ______TS("typical success case");
        final int numOfNotStartedNotification = 5;
        final int numOfOtherOngoingNotification = 5;
        final int numOfExpectedOngoingNotification = 10;
        final int numOfExpiredNotification = 5;

        final NotificationTargetUser expectedTargetUser = NotificationTargetUser.STUDENT;
        final NotificationTargetUser otherTargetUser = NotificationTargetUser.INSTRUCTOR;

        // Add some notifications that are not started yet. They should not be returned.
        for (int i = 0; i < numOfNotStartedNotification; i++) {
            n = getNewNotificationAttributes();
            n.setStartTime(Instant.now().plusSeconds(3600));
            n.setEndTime(Instant.now().plusSeconds(4200));
            notificationsDb.createEntity(n);
        }

        // Add some notifications that have expired. They should not be returned.
        for (int i = 0; i < numOfExpiredNotification; i++) {
            n = getNewNotificationAttributes();
            n.setStartTime(Instant.now().minusSeconds(4200));
            n.setEndTime(Instant.now().minusSeconds(3600));
            notificationsDb.createEntity(n);
        }

        // Add some ongoing notifications but not the target user we are interested. They should not be returned.
        for (int i = 0; i < numOfOtherOngoingNotification; i++) {
            n = getNewNotificationAttributes();
            n.setStartTime(Instant.now().minusSeconds(3600));
            n.setEndTime(Instant.now().plusSeconds(3600));
            n.setTargetUser(otherTargetUser);
            notificationsDb.createEntity(n);
        }

        // Create 2 at a time, one with expected target user and one with GENERAL
        // All of these created notifications should be returned
        for (int i = 0; i < numOfExpectedOngoingNotification; i += 2) {
            n = getNewNotificationAttributes();
            n.setStartTime(Instant.now().minusSeconds(3600));
            n.setEndTime(Instant.now().plusSeconds(3600));
            n.setTargetUser(expectedTargetUser);
            expectedIds.add(notificationsDb.createEntity(n).getNotificationId());

            n = getNewNotificationAttributes();
            n.setStartTime(Instant.now().minusSeconds(3600));
            n.setEndTime(Instant.now().plusSeconds(3600));
            n.setTargetUser(NotificationTargetUser.GENERAL);
            expectedIds.add(notificationsDb.createEntity(n).getNotificationId());
        }

        List<NotificationAttributes> retrieved = notificationsDb.getActiveNotificationsByTargetUser(expectedTargetUser);

        // Verifies that what was retrieved is exactly the same as what was expected.
        this.verifyNotifications(retrieved, expectedIds);
    }

    @Test
    public void testCreateNotification() throws Exception {
        ______TS("typical success case");
        NotificationAttributes n1 = createNewNotification();

        ______TS("failure: duplicate notification with the same ID");
        assertThrows(EntityAlreadyExistsException.class, () -> notificationsDb.createEntity(n1));

        ______TS("failure: invalid non-null parameters");
        NotificationAttributes n2 = getNewNotificationAttributes();
        n2.setTitle("");
        assertThrows(InvalidParametersException.class, () -> notificationsDb.createEntity(n2));

        ______TS("failure: null parameters");
        assertThrows(AssertionError.class, () -> notificationsDb.createEntity(null));
    }

    // TODO: for extension, some fields are not allowed to be updated after shown is true
    @Test
    public void testUpdateNotification() throws Exception {
        ______TS("failure: update non-existent notification");
        assertThrows(EntityDoesNotExistException.class, () ->
                notificationsDb.updateNotification(NotificationAttributes.updateOptionsBuilder(NON_EXISTENT_ID)
                        .withTitle("title")
                        .build()));

        ______TS("failure: invalid non-null parameters");
        // Empty title is used here, which triggers InvalidParametersException
        assertThrows(InvalidParametersException.class, () ->
                notificationsDb.updateNotification(NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle("")
                        .build()));

        ______TS("failure: null update options");
        assertThrows(AssertionError.class, () -> notificationsDb.updateNotification(null));
    }

    @Test
    public void testUpdateNotification_bulkUpdate_shouldUpdateSuccessfully() throws Exception {
        ______TS("success: bulk update");
        // Try to update to another set of values, currently n's attributes are from notification1
        NotificationAttributes original = typicalNotifications.get("notification1");
        NotificationAttributes updated = typicalNotifications.get("notification2");

        assertEquals(original.getTitle(), n.getTitle());
        assertEquals(original.getMessage(), n.getMessage());
        assertEquals(original.getType(), n.getType());
        assertEquals(original.getTargetUser(), n.getTargetUser());
        assertEquals(original.getStartTime(), n.getStartTime());
        assertEquals(original.getEndTime(), n.getEndTime());
        assertFalse(n.isShown());

        notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle(updated.getTitle())
                        .withMessage(updated.getMessage())
                        .withType(updated.getType())
                        .withTargetUser(updated.getTargetUser())
                        .withStartTime(updated.getStartTime())
                        .withEndTime(updated.getEndTime())
                        .withShown()
                        .build());

        n = notificationsDb.getNotification(n.getNotificationId());
        assertEquals(updated.getTitle(), n.getTitle());
        assertEquals(updated.getMessage(), n.getMessage());
        assertEquals(updated.getType(), n.getType());
        assertEquals(updated.getTargetUser(), n.getTargetUser());
        assertEquals(updated.getStartTime(), n.getStartTime());
        assertEquals(updated.getEndTime(), n.getEndTime());
        assertTrue(n.isShown());
    }

    @Test
    public void testUpdateNotification_singleFieldUpdate_shouldUpdateSuccessfully() throws Exception {
        ______TS("success: single field - title");
        // Try to update to another set of values, currently n's attributes are from notification1
        NotificationAttributes original = typicalNotifications.get("notification1");
        NotificationAttributes updated = typicalNotifications.get("notification2");

        assertEquals(original.getTitle(), notificationsDb.getNotification(n.getNotificationId()).getTitle());
        assertEquals(updated.getTitle(), notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle(updated.getTitle())
                        .build()).getTitle());
        assertEquals(updated.getTitle(), notificationsDb.getNotification(n.getNotificationId()).getTitle());

        ______TS("success: single field - message");
        assertEquals(original.getMessage(), notificationsDb.getNotification(n.getNotificationId()).getMessage());
        assertEquals(updated.getMessage(), notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withMessage(updated.getMessage())
                        .build()).getMessage());
        assertEquals(updated.getMessage(), notificationsDb.getNotification(n.getNotificationId()).getMessage());

        ______TS("success: single field - type");
        assertEquals(original.getType(), notificationsDb.getNotification(n.getNotificationId()).getType());
        assertEquals(updated.getType(), notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withType(updated.getType())
                        .build()).getType());
        assertEquals(updated.getType(), notificationsDb.getNotification(n.getNotificationId()).getType());

        ______TS("success: single field - targetUser");
        assertEquals(original.getTargetUser(), notificationsDb.getNotification(n.getNotificationId()).getTargetUser());
        assertEquals(updated.getTargetUser(), notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTargetUser(updated.getTargetUser())
                        .build()).getTargetUser());
        assertEquals(updated.getTargetUser(), notificationsDb.getNotification(n.getNotificationId()).getTargetUser());

        ______TS("success: single field - startTime");
        assertEquals(original.getStartTime(), notificationsDb.getNotification(n.getNotificationId()).getStartTime());
        assertEquals(updated.getStartTime(), notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withStartTime(updated.getStartTime())
                        .build()).getStartTime());
        assertEquals(updated.getStartTime(), notificationsDb.getNotification(n.getNotificationId()).getStartTime());

        ______TS("success: single field - endTime");
        assertEquals(original.getEndTime(), notificationsDb.getNotification(n.getNotificationId()).getEndTime());
        assertEquals(updated.getEndTime(), notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withEndTime(updated.getEndTime())
                        .build()).getEndTime());
        assertEquals(updated.getEndTime(), notificationsDb.getNotification(n.getNotificationId()).getEndTime());

        ______TS("success: single field - shown");
        assertFalse(notificationsDb.getNotification(n.getNotificationId()).isShown());
        assertTrue(notificationsDb.updateNotification(
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withShown()
                        .build()).isShown());
        assertTrue(notificationsDb.getNotification(n.getNotificationId()).isShown());
    }

    @Test
    public void testDeleteNotification() throws Exception {
        ______TS("silent deletion of non-existant notification");
        notificationsDb.deleteNotification(NON_EXISTENT_ID);

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
    }

    private NotificationAttributes createNewNotification()
            throws EntityAlreadyExistsException, InvalidParametersException {
        return notificationsDb.createEntity(getNewNotificationAttributes());
    }

    private NotificationAttributes getNewNotificationAttributes() {
        NotificationAttributes typical = typicalNotifications.get("notification1");
        return NotificationAttributes.builder(UUID.randomUUID().toString())
                .withTitle(typical.getTitle())
                .withMessage(typical.getMessage())
                .withType(typical.getType())
                .withTargetUser(typical.getTargetUser())
                .withStartTime(typical.getStartTime())
                .withEndTime(typical.getEndTime())
                .build();
    }

    /**
     * Verifies that the retrieved notifications' IDs match the expected set of IDs.
     */
    private void verifyNotifications(List<NotificationAttributes> retrieved, Set<String> ids) throws Exception {
        assertNotNull(retrieved);
        assertEquals(ids.size(), retrieved.size());

        Set<String> recordedNotifications = new HashSet<>();
        retrieved.forEach(n -> {
            // Checks that each notification retrieved is in the set and appears only once.
            assertNotNull(n);
            assertTrue(ids.contains(n.getNotificationId()));
            assertFalse(recordedNotifications.contains(n.getNotificationId()));
            recordedNotifications.add(n.getNotificationId());
        });
    }
}
