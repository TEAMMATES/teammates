package teammates.common.datatransfer.attributes;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.NotificationType;
import teammates.storage.entity.Notification;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link NotificationAttributes}.
 */
public class NotificationAttributesTest extends BaseTestCase {
    private static final Instant startTime1 = Instant.now().plusSeconds(3600);
    private static final Instant endTime1 = Instant.now().plusSeconds(7200);
    private static final Instant startTime2 = Instant.now().plusSeconds(1000);
    private static final Instant endTime2 = Instant.now().plusSeconds(10000);

    @Test
    public void testValueOf_withAllFieldPopulatedNotificationAttributes_shouldGenerateAttributesCorrectly() {
        Notification notification = new Notification("valid-notification-id",
                startTime1, endTime1,
                NotificationType.DEPRECATION, NotificationTargetUser.INSTRUCTOR,
                "valid notification title", "valid notification message", false,
                Instant.now(), Instant.now());
        NotificationAttributes nfa = NotificationAttributes.valueOf(notification);
        assertEquals(notification.getNotificationId(), nfa.getNotificationId());
        assertEquals(notification.getStartTime(), nfa.getStartTime());
        assertEquals(notification.getEndTime(), nfa.getEndTime());
        assertEquals(notification.getType(), nfa.getType());
        assertEquals(notification.getTargetUser(), nfa.getTargetUser());
        assertEquals(notification.getTitle(), nfa.getTitle());
        assertEquals(notification.getMessage(), nfa.getMessage());
        assertEquals(notification.isShown(), nfa.isShown());
        assertEquals(notification.getCreatedAt(), nfa.getCreatedAt());
        assertEquals(notification.getUpdatedAt(), nfa.getUpdatedAt());
    }

    @Test
    public void testBuilder_withNullArguments_shouldThrowException() {
        assertThrows(AssertionError.class, () -> {
            NotificationAttributes
                    .builder(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            NotificationAttributes
                    .builder("notificationId")
                    .withStartTime(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            NotificationAttributes
                    .builder("notificationId")
                    .withEndTime(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            NotificationAttributes
                    .builder("notificationId")
                    .withType(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            NotificationAttributes
                    .builder("notificationId")
                    .withTargetUser(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            NotificationAttributes
                    .builder("notificationId")
                    .withTitle(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            NotificationAttributes
                    .builder("notificationId")
                    .withMessage(null)
                    .build();
        });
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttributes() {
        Notification notification = new Notification(startTime1, endTime1,
                NotificationType.TIPS, NotificationTargetUser.GENERAL,
                "Another tip for usage", "Here the message starts");
        NotificationAttributes nfa = NotificationAttributes.valueOf(notification);
        assertEquals(startTime1, nfa.getStartTime());
        assertEquals(endTime1, nfa.getEndTime());
        assertEquals(NotificationType.TIPS, nfa.getType());
        assertEquals(NotificationTargetUser.GENERAL, nfa.getTargetUser());
        assertEquals("Another tip for usage", nfa.getTitle());
        assertEquals("Here the message starts", nfa.getMessage());
    }

    @Test
    public void testCopyConstructor_shouldDoDeepCopyOfNotificationDetails() {
        NotificationAttributes nfa1 = NotificationAttributes.builder("notificationId")
                .withStartTime(startTime1)
                .withEndTime(endTime1)
                .withType(NotificationType.DEPRECATION)
                .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                .withTitle("valid notification title")
                .withMessage("The first message")
                .build();
        NotificationAttributes nfa2 = nfa1.getCopy();
        nfa2.setMessage("The second message");

        assertEquals(nfa1.getMessage(), "The first message");
        assertEquals(nfa2.getMessage(), "The second message");
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        NotificationAttributes.UpdateOptions updateOptions =
                NotificationAttributes.updateOptionsBuilder("notificationId")
                        .withStartTime(startTime2)
                        .withEndTime(endTime2)
                        .withType(NotificationType.VERSION_NOTE)
                        .withTargetUser(NotificationTargetUser.STUDENT)
                        .withTitle("The edited title")
                        .withMessage("The edited message")
                        .build();

        assertEquals("notificationId", updateOptions.getNotificationId());

        NotificationAttributes notificationAttributes =
                NotificationAttributes.builder("notificationId")
                .withStartTime(startTime1)
                .withEndTime(endTime1)
                .withType(NotificationType.DEPRECATION)
                .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                .withTitle("valid notification title")
                .withMessage("The first message")
                .build();

        notificationAttributes.update(updateOptions);

        assertEquals(startTime2, notificationAttributes.getStartTime());
        assertEquals(endTime2, notificationAttributes.getEndTime());
        assertEquals(NotificationType.VERSION_NOTE, notificationAttributes.getType());
        assertEquals(NotificationTargetUser.STUDENT, notificationAttributes.getTargetUser());
        assertEquals("The edited title", notificationAttributes.getTitle());
        assertEquals("The edited message", notificationAttributes.getMessage());
    }

    @Test
    public void testUpdateOptionsBuilder_withNullInput_shouldFailWithAssertionError() {
        assertThrows(AssertionError.class, () ->
                NotificationAttributes.updateOptionsBuilder((String) null));
        assertThrows(AssertionError.class, () ->
                NotificationAttributes.updateOptionsBuilder("notificationId")
                        .withStartTime(null));
        assertThrows(AssertionError.class, () ->
                NotificationAttributes.updateOptionsBuilder("notificationId")
                        .withEndTime(null));
        assertThrows(AssertionError.class, () ->
                NotificationAttributes.updateOptionsBuilder("notificationId")
                        .withType(null));
        assertThrows(AssertionError.class, () ->
                NotificationAttributes.updateOptionsBuilder("notificationId")
                        .withTargetUser(null));
        assertThrows(AssertionError.class, () ->
                NotificationAttributes.updateOptionsBuilder("notificationId")
                        .withTitle(null));
        assertThrows(AssertionError.class, () ->
                NotificationAttributes.updateOptionsBuilder("notificationId")
                        .withMessage(null));
    }

    @Test
    public void testEquals() {
        NotificationAttributes notificationAttributes =
                NotificationAttributes.builder("notificationId")
                .withStartTime(startTime1)
                .withEndTime(endTime1)
                .withType(NotificationType.DEPRECATION)
                .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                .withTitle("valid notification title")
                .withMessage("valid message")
                .build();

        // When the two notifications are exact copies
        NotificationAttributes notificationAttributesCopy = notificationAttributes.getCopy();

        assertTrue(notificationAttributes.equals(notificationAttributesCopy));

        // When the two notifications have same values but created at different time
        NotificationAttributes notificationAttributesSimilar =
                NotificationAttributes.builder("notificationId")
                .withStartTime(startTime1)
                .withEndTime(endTime1)
                .withType(NotificationType.DEPRECATION)
                .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                .withTitle("valid notification title")
                .withMessage("valid message")
                .build();

        assertTrue(notificationAttributes.equals(notificationAttributesSimilar));

        NotificationAttributes notificationAttributesDifferent =
                NotificationAttributes.builder("differentId")
                .withStartTime(startTime1)
                .withEndTime(endTime1)
                .withType(NotificationType.DEPRECATION)
                .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                .withTitle("valid notification title")
                .withMessage("valid message")
                .build();

        assertFalse(notificationAttributes.equals(notificationAttributesDifferent));

        // When the other object is of different class
        assertFalse(notificationAttributes.equals(3));
    }

    @Test
    public void testHashCode() {
        NotificationAttributes notificationAttributes =
                NotificationAttributes.builder("notificationId")
                        .withStartTime(startTime1)
                        .withEndTime(endTime1)
                        .withType(NotificationType.DEPRECATION)
                        .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                        .withTitle("valid notification title")
                        .withMessage("valid message")
                        .build();

        // When the two notifications are exact copies
        NotificationAttributes notificationAttributesCopy = notificationAttributes.getCopy();

        assertTrue(notificationAttributes.hashCode() == notificationAttributesCopy.hashCode());

        // When the two notifications have same values but created at different time
        NotificationAttributes notificationAttributesSimilar =
                NotificationAttributes.builder("notificationId")
                        .withStartTime(startTime1)
                        .withEndTime(endTime1)
                        .withType(NotificationType.DEPRECATION)
                        .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                        .withTitle("valid notification title")
                        .withMessage("valid message")
                        .build();

        assertTrue(notificationAttributes.hashCode() == notificationAttributesSimilar.hashCode());

        NotificationAttributes notificationAttributesDifferent =
                NotificationAttributes.builder("differentId")
                        .withStartTime(startTime1)
                        .withEndTime(endTime1)
                        .withType(NotificationType.DEPRECATION)
                        .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                        .withTitle("valid notification title")
                        .withMessage("valid message")
                        .build();

        assertFalse(notificationAttributes.hashCode() == notificationAttributesDifferent.hashCode());
    }

}
