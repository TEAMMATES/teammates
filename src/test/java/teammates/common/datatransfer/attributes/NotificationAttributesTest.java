package teammates.common.datatransfer.attributes;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.storage.entity.Notification;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link NotificationAttributes}.
 */
public class NotificationAttributesTest extends BaseTestCase {
    @Test
    public void testValueOf_withAllFieldPopulatedNotificationAttributes_shouldGenerateAttributesCorrectly() {
        Notification notification = new Notification("valid-notification-id",
                Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200),
                NotificationStyle.SUCCESS, NotificationTargetUser.INSTRUCTOR,
                "valid notification title", "valid notification message", false,
                Instant.now(), Instant.now());
        NotificationAttributes nfa = NotificationAttributes.valueOf(notification);
        verifyNotificationEquals(nfa, notification);
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
                    .withStyle(null)
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
        NotificationAttributes nfa = generateTypicalNotificationAttributesObject();
        assertEquals("notificationId", nfa.getNotificationId());
        assertEquals(Instant.ofEpochSecond(1234567890), nfa.getStartTime());
        assertEquals(Instant.ofEpochSecond(1234567890).plusSeconds(7200), nfa.getEndTime());
        assertEquals(NotificationStyle.SUCCESS, nfa.getStyle());
        assertEquals(NotificationTargetUser.INSTRUCTOR, nfa.getTargetUser());
        assertEquals("valid notification title", nfa.getTitle());
        assertEquals("valid message", nfa.getMessage());
    }

    @Test
    public void testCopyConstructor_shouldDoDeepCopyOfNotificationDetails() {
        NotificationAttributes nfa1 = generateTypicalNotificationAttributesObject();
        NotificationAttributes nfa2 = nfa1.getCopy();
        nfa1.setMessage("The first message");
        nfa2.setMessage("The second message");

        assertEquals(nfa1.getMessage(), "The first message");
        assertEquals(nfa2.getMessage(), "The second message");
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        NotificationAttributes.UpdateOptions updateOptions =
                NotificationAttributes.updateOptionsBuilder("notificationId")
                        .withStartTime(Instant.ofEpochSecond(1234567890).plusSeconds(1000))
                        .withEndTime(Instant.ofEpochSecond(1234567890).plusSeconds(10000))
                        .withStyle(NotificationStyle.WARNING)
                        .withTargetUser(NotificationTargetUser.STUDENT)
                        .withTitle("The edited title")
                        .withMessage("The edited message")
                        .build();

        assertEquals("notificationId", updateOptions.getNotificationId());

        NotificationAttributes notificationAttributes = generateTypicalNotificationAttributesObject();
        notificationAttributes.update(updateOptions);

        assertEquals(Instant.ofEpochSecond(1234567890).plusSeconds(1000), notificationAttributes.getStartTime());
        assertEquals(Instant.ofEpochSecond(1234567890).plusSeconds(10000), notificationAttributes.getEndTime());
        assertEquals(NotificationStyle.WARNING, notificationAttributes.getStyle());
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
                        .withStyle(null));
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
        NotificationAttributes notificationAttributes = generateTypicalNotificationAttributesObject();

        // When the two notifications are exact copies
        NotificationAttributes notificationAttributesCopy = notificationAttributes.getCopy();

        assertTrue(notificationAttributes.equals(notificationAttributesCopy));

        // When the two notifications have same values but created at different time
        NotificationAttributes notificationAttributesSimilar = generateTypicalNotificationAttributesObject();

        assertTrue(notificationAttributes.equals(notificationAttributesSimilar));

        NotificationAttributes notificationAttributesDifferent =
                NotificationAttributes.builder("differentId")
                .withStartTime(Instant.ofEpochSecond(1234567890))
                .withEndTime(Instant.ofEpochSecond(1234567890).plusSeconds(7200))
                .withStyle(NotificationStyle.SUCCESS)
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
        NotificationAttributes notificationAttributes = generateTypicalNotificationAttributesObject();

        // When the two notifications are exact copies
        NotificationAttributes notificationAttributesCopy = notificationAttributes.getCopy();

        assertTrue(notificationAttributes.hashCode() == notificationAttributesCopy.hashCode());

        // When the two notifications have same values but created at different time
        NotificationAttributes notificationAttributesSimilar = generateTypicalNotificationAttributesObject();

        assertTrue(notificationAttributes.hashCode() == notificationAttributesSimilar.hashCode());

        // notification attributes with a different id.
        NotificationAttributes notificationAttributesDifferent =
                NotificationAttributes.builder("differentId")
                        .withStartTime(Instant.ofEpochSecond(1234567890))
                        .withEndTime(Instant.ofEpochSecond(1234567890).plusSeconds(7200))
                        .withStyle(NotificationStyle.SUCCESS)
                        .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                        .withTitle("valid notification title")
                        .withMessage("valid message")
                        .build();

        assertFalse(notificationAttributes.hashCode() == notificationAttributesDifferent.hashCode());
    }

    private NotificationAttributes generateTypicalNotificationAttributesObject() {
        return NotificationAttributes.builder("notificationId")
                .withStartTime(Instant.ofEpochSecond(1234567890))
                .withEndTime(Instant.ofEpochSecond(1234567890).plusSeconds(7200))
                .withStyle(NotificationStyle.SUCCESS)
                .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                .withTitle("valid notification title")
                .withMessage("valid message")
                .build();
    }

    private void verifyNotificationEquals(NotificationAttributes nfa, Notification notification) {
        assertEquals(notification.getNotificationId(), nfa.getNotificationId());
        assertEquals(notification.getStartTime(), nfa.getStartTime());
        assertEquals(notification.getEndTime(), nfa.getEndTime());
        assertEquals(notification.getStyle(), nfa.getStyle());
        assertEquals(notification.getTargetUser(), nfa.getTargetUser());
        assertEquals(notification.getTitle(), nfa.getTitle());
        assertEquals(notification.getMessage(), nfa.getMessage());
        assertEquals(notification.isShown(), nfa.isShown());
        assertEquals(notification.getCreatedAt(), nfa.getCreatedAt());
        assertEquals(notification.getUpdatedAt(), nfa.getUpdatedAt());
    }
}
