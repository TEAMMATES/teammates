package teammates.common.datatransfer.attributes;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.entity.DeadlineExtension;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link DeadlineExtensionAttributes}.
 */
public class DeadlineExtensionAttributesTest extends BaseTestCase {
    private static final String VALID_COURSE_ID = "valid-course-id";
    private static final String VALID_FEEDBACK_SESSION_NAME = "valid feedback session name";
    private static final String VALID_USER_EMAIL = "valid@gmail.com";

    @Test
    public void testValueOf_withTypicalData_shouldGenerateAttributesCorrectly() {
        DeadlineExtension deadlineExtension = new DeadlineExtension(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME,
                VALID_USER_EMAIL, false, true, Const.TIME_REPRESENTS_LATER);

        DeadlineExtensionAttributes deadlineExtensionAttributes = DeadlineExtensionAttributes.valueOf(deadlineExtension);

        assertEquals(VALID_COURSE_ID, deadlineExtensionAttributes.getCourseId());
        assertEquals(VALID_FEEDBACK_SESSION_NAME, deadlineExtensionAttributes.getFeedbackSessionName());
        assertEquals(VALID_USER_EMAIL, deadlineExtensionAttributes.getUserEmail());
        assertFalse(deadlineExtensionAttributes.getIsInstructor());
        assertEquals(Const.TIME_REPRESENTS_LATER, deadlineExtensionAttributes.getEndTime());
        assertTrue(deadlineExtensionAttributes.getSentClosingSoonEmail());
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttributes() {
        DeadlineExtensionAttributes deadlineExtensionAttributes = DeadlineExtensionAttributes
                .builder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true)
                .withEndTime(Const.TIME_REPRESENTS_LATER)
                .withSentClosingSoonEmail(true)
                .build();

        assertEquals(VALID_COURSE_ID, deadlineExtensionAttributes.getCourseId());
        assertEquals(VALID_FEEDBACK_SESSION_NAME, deadlineExtensionAttributes.getFeedbackSessionName());
        assertEquals(VALID_USER_EMAIL, deadlineExtensionAttributes.getUserEmail());
        assertTrue(deadlineExtensionAttributes.getIsInstructor());
        assertEquals(Const.TIME_REPRESENTS_LATER, deadlineExtensionAttributes.getEndTime());
        assertTrue(deadlineExtensionAttributes.getSentClosingSoonEmail());
    }

    @Test
    public void testBuilder_buildNothing_shouldUseDefaultValues() {
        DeadlineExtensionAttributes deadlineExtensionAttributes = DeadlineExtensionAttributes
                .builder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true)
                .build();

        assertEquals(VALID_COURSE_ID, deadlineExtensionAttributes.getCourseId());
        assertEquals(VALID_FEEDBACK_SESSION_NAME, deadlineExtensionAttributes.getFeedbackSessionName());
        assertEquals(VALID_USER_EMAIL, deadlineExtensionAttributes.getUserEmail());
        assertTrue(deadlineExtensionAttributes.getIsInstructor());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, deadlineExtensionAttributes.getEndTime());
        assertFalse(deadlineExtensionAttributes.getSentClosingSoonEmail());
    }

    @Test
    public void testBuilder_withNullArguments_shouldThrowException() {
        assertThrows(AssertionError.class, () -> DeadlineExtensionAttributes
                .builder(null, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false)
                .build());

        assertThrows(AssertionError.class, () -> DeadlineExtensionAttributes
                .builder(VALID_COURSE_ID, null, VALID_USER_EMAIL, false)
                .build());

        assertThrows(AssertionError.class, () -> DeadlineExtensionAttributes
                .builder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, null, false)
                .build());

        assertThrows(AssertionError.class, () -> DeadlineExtensionAttributes
                .builder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true)
                .withEndTime(null)
                .build());
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        DeadlineExtensionAttributes deadlineExtension = getValidDeadlineExtensionAttributesObject(false);

        DeadlineExtensionAttributes.UpdateOptions updateOptions = DeadlineExtensionAttributes
                .updateOptionsBuilder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false)
                .withEndTime(Const.TIME_REPRESENTS_NOW)
                .withNewEmail("new-email@gmail.tmt")
                .withSentClosingSoonEmail(true)
                .build();
        deadlineExtension.update(updateOptions);

        assertEquals(VALID_COURSE_ID, deadlineExtension.getCourseId());
        assertEquals(VALID_FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName());
        assertEquals("new-email@gmail.tmt", deadlineExtension.getUserEmail());
        assertFalse(deadlineExtension.getIsInstructor());
        assertEquals(Const.TIME_REPRESENTS_NOW, deadlineExtension.getEndTime());
        assertTrue(deadlineExtension.getSentClosingSoonEmail());
    }

    @Test
    public void testUpdateOptions_withExistingUpdateOptions_shouldReturnEquivalentDeadlineExtension() {
        DeadlineExtensionAttributes deadlineExtension = getValidDeadlineExtensionAttributesObject(false);
        DeadlineExtensionAttributes.UpdateOptions updateOptions = DeadlineExtensionAttributes
                .updateOptionsBuilder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false)
                .withEndTime(Const.TIME_REPRESENTS_NOW)
                .withNewEmail("new-email@gmail.tmt")
                .withSentClosingSoonEmail(true)
                .build();
        deadlineExtension.update(updateOptions);

        DeadlineExtensionAttributes newDeadlineExtension = getValidDeadlineExtensionAttributesObject(false);
        DeadlineExtensionAttributes.UpdateOptions newUpdateOptions = DeadlineExtensionAttributes
                .updateOptionsBuilder(updateOptions)
                .build();
        newDeadlineExtension.update(newUpdateOptions);

        assertEquals(deadlineExtension, newDeadlineExtension);
        assertEquals(deadlineExtension.getSentClosingSoonEmail(), newDeadlineExtension.getSentClosingSoonEmail());
    }

    @Test
    public void testUpdateOptions_withNullUpdateOptions_shouldThrowAssertionError() {
        assertThrows(AssertionError.class, () -> DeadlineExtensionAttributes
                .updateOptionsBuilder(null)
                .build());

        assertThrows(AssertionError.class, () -> DeadlineExtensionAttributes
                .updateOptionsBuilder(null, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false)
                .withEndTime(Const.TIME_REPRESENTS_NOW)
                .withNewEmail("new-email@gmail.tmt")
                .withSentClosingSoonEmail(true)
                .build());

        assertThrows(AssertionError.class, () -> DeadlineExtensionAttributes
                .updateOptionsBuilder(VALID_COURSE_ID, null, VALID_USER_EMAIL, false)
                .withEndTime(Const.TIME_REPRESENTS_NOW)
                .withNewEmail("new-email@gmail.tmt")
                .withSentClosingSoonEmail(true)
                .build());

        assertThrows(AssertionError.class, () -> DeadlineExtensionAttributes
                .updateOptionsBuilder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, null, false)
                .build());

        assertThrows(AssertionError.class, () -> DeadlineExtensionAttributes
                .updateOptionsBuilder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true)
                .withEndTime(null)
                .build());

        assertThrows(AssertionError.class, () -> DeadlineExtensionAttributes
                .updateOptionsBuilder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true)
                .withNewEmail(null)
                .build());
    }

    @Test
    public void testValidate() throws Exception {
        DeadlineExtensionAttributes validDeadlineExtension = getValidDeadlineExtensionAttributesObject(false);

        assertTrue(validDeadlineExtension.isValid());

        DeadlineExtensionAttributes invalidDeadlineExtension = DeadlineExtensionAttributes
                .builder("", "", "invalid-email", true)
                .build();

        assertFalse(invalidDeadlineExtension.isValid());

        String errorMessage =
                getPopulatedEmptyStringErrorMessage(
                    FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                    FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH)
                + System.lineSeparator()
                + getPopulatedEmptyStringErrorMessage(
                    FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING_FOR_SESSION_NAME,
                    FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH)
                + System.lineSeparator()
                + getPopulatedErrorMessage(
                    FieldValidator.EMAIL_ERROR_MESSAGE, invalidDeadlineExtension.getUserEmail(),
                    FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                    FieldValidator.EMAIL_MAX_LENGTH);

        assertEquals(errorMessage, StringHelper.toString(invalidDeadlineExtension.getInvalidityInfo()));
    }

    @Test
    public void testToString() {
        DeadlineExtensionAttributes deadlineExtension = getValidDeadlineExtensionAttributesObject(true);
        assertEquals("DeadlineExtensionAttributes ["
                + "courseId = " + VALID_COURSE_ID
                + ", feedbackSessionName = " + VALID_FEEDBACK_SESSION_NAME
                + ", userEmail = " + VALID_USER_EMAIL
                + ", isInstructor = true]", deadlineExtension.toString());
    }

    @Test
    public void testEquals() {
        DeadlineExtensionAttributes deadlineExtension = getValidDeadlineExtensionAttributesObject(true);

        // When the two deadline extensions have same values
        DeadlineExtensionAttributes deadlineExtensionSimilar = getValidDeadlineExtensionAttributesObject(true);

        assertTrue(deadlineExtension.equals(deadlineExtensionSimilar));

        // When the two deadline extensions are different
        DeadlineExtensionAttributes deadlineExtensionDifferent = getValidDeadlineExtensionAttributesObject(false);

        assertFalse(deadlineExtension.equals(deadlineExtensionDifferent));

        // When the other object is of different class
        assertFalse(deadlineExtension.equals(3));
    }

    @Test
    public void testHashCode() {
        DeadlineExtensionAttributes deadlineExtension = getValidDeadlineExtensionAttributesObject(true);

        // When the two deadline extensions have same values, they should have the same hash code
        DeadlineExtensionAttributes deadlineExtensionSimilar = getValidDeadlineExtensionAttributesObject(true);
        assertTrue(deadlineExtension.hashCode() == deadlineExtensionSimilar.hashCode());

        // When the two deadline extensions are different, they should have different hash code
        DeadlineExtensionAttributes deadlineExtensionDifferent = getValidDeadlineExtensionAttributesObject(false);
        assertFalse(deadlineExtension.hashCode() == deadlineExtensionDifferent.hashCode());

        deadlineExtensionDifferent = getValidDeadlineExtensionAttributesObject(true);
        deadlineExtensionDifferent.update(DeadlineExtensionAttributes
                .updateOptionsBuilder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true)
                .withNewEmail("new-email@gmail.com")
                .build());
        assertFalse(deadlineExtension.hashCode() == deadlineExtensionDifferent.hashCode());
    }

    private static DeadlineExtensionAttributes getValidDeadlineExtensionAttributesObject(boolean isInstructor) {
        DeadlineExtension deadlineExtension = new DeadlineExtension(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME,
                VALID_USER_EMAIL, isInstructor, false, Const.TIME_REPRESENTS_LATER);

        return DeadlineExtensionAttributes.valueOf(deadlineExtension);
    }

}
