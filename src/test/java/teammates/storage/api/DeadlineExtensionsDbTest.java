package teammates.storage.api;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;
import teammates.storage.entity.DeadlineExtension;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link DeadlineExtensionsDb}.
 */
public class DeadlineExtensionsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    private static final String VALID_COURSE_ID = "valid-course-id";
    private static final String VALID_FEEDBACK_SESSION_NAME = "valid feedback session name";
    private static final String VALID_USER_EMAIL = "valid@gmail.com";

    private final DeadlineExtensionsDb deadlineExtensionsDb = DeadlineExtensionsDb.inst();

    @Test
    public void testCreateDeadlineExtension() throws Exception {
        String validCourseId = VALID_COURSE_ID + "-create";

        ______TS("typical success case");

        DeadlineExtensionAttributes deadlineExtension = DeadlineExtensionAttributes
                .builder(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false)
                .withEndTime(Const.TIME_REPRESENTS_LATER)
                .build();

        deadlineExtension = deadlineExtensionsDb.createEntity(deadlineExtension);
        verifyPresentInDatabase(deadlineExtension);

        ______TS("failure: duplicate deadline extension");

        DeadlineExtensionAttributes duplicateDeadlineExtension = DeadlineExtensionAttributes
                .builder(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false)
                .withEndTime(Const.TIME_REPRESENTS_LATER)
                .build();

        assertThrows(EntityAlreadyExistsException.class,
                () -> deadlineExtensionsDb.createEntity(duplicateDeadlineExtension));

        deadlineExtensionsDb.deleteDeadlineExtension(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false);

        ______TS("failure case: invalid parameters");

        DeadlineExtensionAttributes invalidEmailDeadlineExtension = DeadlineExtensionAttributes
                .builder(validCourseId, VALID_FEEDBACK_SESSION_NAME, "invalid-email", false)
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> deadlineExtensionsDb.createEntity(invalidEmailDeadlineExtension));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "invalid-email",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        DeadlineExtensionAttributes invalidFeedbackSessionNameDeadlineExtension = DeadlineExtensionAttributes
                .builder(validCourseId, "", VALID_USER_EMAIL, false)
                .build();

        ipe = assertThrows(InvalidParametersException.class,
                () -> deadlineExtensionsDb.createEntity(invalidFeedbackSessionNameDeadlineExtension));
        AssertHelper.assertContains(
                getPopulatedEmptyStringErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING_FOR_SESSION_NAME,
                        FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
                        FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH),
                ipe.getMessage());

        DeadlineExtensionAttributes invalidCourseIdDeadlineExtension = DeadlineExtensionAttributes
                .builder("", VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false)
                .build();

        ipe = assertThrows(InvalidParametersException.class,
                () -> deadlineExtensionsDb.createEntity(invalidCourseIdDeadlineExtension));
        AssertHelper.assertContains(
                getPopulatedEmptyStringErrorMessage(
                        FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                        FieldValidator.COURSE_ID_FIELD_NAME,
                        FieldValidator.COURSE_ID_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> deadlineExtensionsDb.createEntity(null));
    }

    @Test
    public void testUpdateDeadlineExtension() throws Exception {
        String validCourseId = VALID_COURSE_ID + "-update";

        deadlineExtensionsDb.createEntity(DeadlineExtensionAttributes
                .builder(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true)
                .withEndTime(Const.TIME_REPRESENTS_LATER)
                .build());

        ______TS("typical success case");

        DeadlineExtensionAttributes.UpdateOptions updateOptions = DeadlineExtensionAttributes
                .updateOptionsBuilder(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true)
                .withEndTime(Const.TIME_REPRESENTS_LATER)
                .withSentClosingSoonEmail(true)
                .build();
        deadlineExtensionsDb.updateDeadlineExtension(updateOptions);

        DeadlineExtensionAttributes deadlineExtension = deadlineExtensionsDb
                .getDeadlineExtension(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true);

        assertEquals(Const.TIME_REPRESENTS_LATER, deadlineExtension.getEndTime());
        assertTrue(deadlineExtension.getSentClosingSoonEmail());

        ______TS("update invalid email throws invalid parameter exception");

        DeadlineExtensionAttributes.UpdateOptions invalidEmailUpdateOptions = DeadlineExtensionAttributes
                .updateOptionsBuilder(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true)
                .withNewEmail("invalid-email")
                .build();
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> deadlineExtensionsDb.updateDeadlineExtension(invalidEmailUpdateOptions));

        assertEquals("\"invalid-email\" is not acceptable to TEAMMATES as a/an email because "
                + "it is not in the correct format. An email address contains some text followed "
                + "by one '@' sign followed by some more text, and should end with a top level domain "
                + "address like .com. It cannot be longer than 254 characters, cannot be empty and cannot "
                + "contain spaces.", ipe.getMessage());

        ______TS("update new email: success");

        String newEmail = "new-email@gmail.tmt";
        updateOptions = DeadlineExtensionAttributes
                .updateOptionsBuilder(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true)
                .withNewEmail(newEmail)
                .build();
        deadlineExtensionsDb.updateDeadlineExtension(updateOptions);

        deadlineExtension = deadlineExtensionsDb
                .getDeadlineExtension(validCourseId, VALID_FEEDBACK_SESSION_NAME, newEmail, true);
        assertEquals(newEmail, deadlineExtension.getUserEmail());
        assertNull(deadlineExtensionsDb
                .getDeadlineExtension(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true));

        ______TS("failure: deadline extension not found");

        DeadlineExtensionAttributes.UpdateOptions updateOptionsNotFound = DeadlineExtensionAttributes
                .updateOptionsBuilder(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false)
                .withEndTime(Const.TIME_REPRESENTS_LATER)
                .withSentClosingSoonEmail(true)
                .build();

        assertThrows(EntityDoesNotExistException.class,
                () -> deadlineExtensionsDb.updateDeadlineExtension(updateOptionsNotFound));
    }

    @Test
    public void testDeleteDeadlineExtension() {
        String validCourseId = VALID_COURSE_ID + "-delete";

        DeadlineExtension deadlineExtension = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL,
                false, true, Const.TIME_REPRESENTS_NOW);

        deadlineExtensionsDb.saveEntity(deadlineExtension);

        ______TS("silent deletion of non-existent deadline extension");

        deadlineExtensionsDb.deleteDeadlineExtension("not_exist", "not_exist", "not_exist", false);

        ______TS("typical success case");

        DeadlineExtensionAttributes deadlineExtensionAttributes = DeadlineExtensionAttributes.valueOf(deadlineExtension);
        verifyPresentInDatabase(deadlineExtensionAttributes);
        deadlineExtensionsDb.deleteDeadlineExtension(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false);
        verifyAbsentInDatabase(deadlineExtensionAttributes);

        ______TS("silent deletion of same deadline extension");

        deadlineExtensionsDb.deleteDeadlineExtension(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false);

        ______TS("failure null parameter");

        assertThrows(AssertionError.class, () -> deadlineExtensionsDb
                .deleteDeadlineExtension(null, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false));

        assertThrows(AssertionError.class, () -> deadlineExtensionsDb
                .deleteDeadlineExtension(validCourseId, null, VALID_USER_EMAIL, true));

        assertThrows(AssertionError.class, () -> deadlineExtensionsDb
                .deleteDeadlineExtension(validCourseId, VALID_FEEDBACK_SESSION_NAME, null, false));
    }

    @Test
    public void testGetDeadlineExtension() {
        String validCourseId = VALID_COURSE_ID + "-get";
        DeadlineExtension deadlineExtension = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL,
                true, true, Const.TIME_REPRESENTS_NOW);

        deadlineExtensionsDb.saveEntity(deadlineExtension);

        ______TS("typical success case");

        DeadlineExtensionAttributes deadlineExtensionAttributes = deadlineExtensionsDb
                .getDeadlineExtension(validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true);
        assertEquals(DeadlineExtensionAttributes.valueOf(deadlineExtension), deadlineExtensionAttributes);

        ______TS("deadline extension not found");

        DeadlineExtensionAttributes notFoundDeadlineExtension =
                deadlineExtensionsDb.getDeadlineExtension("not-found", "not-found", "not-found@gmail.tmt", false);
        assertNull(notFoundDeadlineExtension);

        ______TS("failure null parameter");

        assertThrows(AssertionError.class, () -> deadlineExtensionsDb
                .getDeadlineExtension(null, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, true));

        assertThrows(AssertionError.class, () -> deadlineExtensionsDb
                .getDeadlineExtension(validCourseId, null, VALID_USER_EMAIL, false));

        assertThrows(AssertionError.class, () -> deadlineExtensionsDb
                .getDeadlineExtension(validCourseId, VALID_FEEDBACK_SESSION_NAME, null, true));
    }

    @Test
    public void testDeleteDeadlineExtensions_byCourseAndFeedbackSessionName() throws Exception {
        String validCourseId = VALID_COURSE_ID + "-delete-query";
        DeadlineExtension deadlineExtension1 = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL,
                true, true, Const.TIME_REPRESENTS_NOW);
        DeadlineExtension deadlineExtension2 = new DeadlineExtension(
                validCourseId, "different fs name", VALID_USER_EMAIL,
                true, true, Const.TIME_REPRESENTS_NOW);
        deadlineExtensionsDb.saveEntity(deadlineExtension1);
        deadlineExtensionsDb.saveEntity(deadlineExtension2);
        verifyPresentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtension1));
        verifyPresentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtension2));

        ______TS("typical success case: only delete deadline extensions in feedback session");

        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(validCourseId)
                .withFeedbackSessionName(VALID_FEEDBACK_SESSION_NAME)
                .build();
        deadlineExtensionsDb.deleteDeadlineExtensions(query);
        verifyAbsentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtension1));
        verifyPresentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtension2));

        ______TS("typical success case: delete all deadline extensions in course");

        query = AttributesDeletionQuery.builder()
                .withCourseId(validCourseId)
                .build();
        deadlineExtensionsDb.deleteDeadlineExtensions(query);
        verifyAbsentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtension2));

        ______TS("query is null: throw assertion error");

        assertThrows(AssertionError.class, () -> deadlineExtensionsDb.deleteDeadlineExtensions(null));
    }

    @Test
    public void testDeleteDeadlineExtensions_byCourseIdAndUserDetails() {
        String validCourseId = VALID_COURSE_ID + "-delete-courseid-user";
        DeadlineExtension deadlineExtension = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL,
                false, true, Const.TIME_REPRESENTS_NOW);
        DeadlineExtension deadlineExtensionDifferentFs = new DeadlineExtension(
                validCourseId, "different fs name", VALID_USER_EMAIL,
                false, true, Const.TIME_REPRESENTS_NOW);
        DeadlineExtension deadlineExtensionDifferentCourseId = new DeadlineExtension(
                "different-course-id-delete-courseid-user", VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL,
                false, true, Const.TIME_REPRESENTS_NOW);
        DeadlineExtension deadlineExtensionDifferentUserType = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL,
                true, true, Const.TIME_REPRESENTS_NOW);
        deadlineExtensionsDb.saveEntity(deadlineExtension);
        deadlineExtensionsDb.saveEntity(deadlineExtensionDifferentFs);
        deadlineExtensionsDb.saveEntity(deadlineExtensionDifferentCourseId);
        deadlineExtensionsDb.saveEntity(deadlineExtensionDifferentUserType);

        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(validCourseId)
                .withUserEmail(VALID_USER_EMAIL)
                .withIsInstructor(false)
                .build();
        deadlineExtensionsDb.deleteDeadlineExtensions(query);

        ______TS("Deadline extension with same course id deleted");

        verifyAbsentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtension));
        verifyAbsentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentFs));

        ______TS("Deadline extension with different course id not deleted");

        verifyPresentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentCourseId));

        ______TS("Deadline extension with different user type not deleted");

        verifyPresentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentUserType));

        ______TS("Deleting single deadline extension");

        query = AttributesDeletionQuery.builder()
                .withCourseId("different-course-id-delete-courseid-user")
                .withUserEmail(VALID_USER_EMAIL)
                .withIsInstructor(false)
                .build();
        deadlineExtensionsDb.deleteDeadlineExtensions(query);

        verifyAbsentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentCourseId));

        query = AttributesDeletionQuery.builder()
                .withCourseId(validCourseId)
                .withUserEmail(VALID_USER_EMAIL)
                .withIsInstructor(true)
                .build();
        deadlineExtensionsDb.deleteDeadlineExtensions(query);

        verifyAbsentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentUserType));
    }

    @Test
    public void testUpdateDeadlineExtensionsWithNewEmail() throws Exception {
        String validCourseId = VALID_COURSE_ID + "-update-email";
        String newEmail = "new-email@gmail.tmt";

        DeadlineExtension deadlineExtension = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL,
                false, true, Const.TIME_REPRESENTS_NOW);
        DeadlineExtension deadlineExtensionDifferentFs = new DeadlineExtension(
                validCourseId, "different fs name", VALID_USER_EMAIL,
                false, true, Const.TIME_REPRESENTS_NOW);
        DeadlineExtension deadlineExtensionDifferentCourseId = new DeadlineExtension(
                "different-course-id-update", VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL,
                false, true, Const.TIME_REPRESENTS_NOW);
        DeadlineExtension deadlineExtensionDifferentUserType = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL,
                true, true, Const.TIME_REPRESENTS_NOW);
        deadlineExtensionsDb.saveEntity(deadlineExtension);
        deadlineExtensionsDb.saveEntity(deadlineExtensionDifferentFs);
        deadlineExtensionsDb.saveEntity(deadlineExtensionDifferentCourseId);
        deadlineExtensionsDb.saveEntity(deadlineExtensionDifferentUserType);

        deadlineExtensionsDb.updateDeadlineExtensionsWithNewEmail(
                validCourseId, VALID_USER_EMAIL, newEmail, false);

        ______TS("Deadline extension with same course id updated");

        verifyAbsentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtension));
        deadlineExtension.setUserEmail(newEmail);
        verifyPresentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtension));

        verifyAbsentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentFs));
        deadlineExtensionDifferentFs.setUserEmail(newEmail);
        verifyPresentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentFs));

        ______TS("Deadline extension with different course id not updated");

        verifyPresentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentCourseId));
        deadlineExtensionDifferentCourseId.setUserEmail(newEmail);
        verifyAbsentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentCourseId));

        ______TS("Deadline extension with different user type not updated");

        verifyPresentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentUserType));
        deadlineExtensionDifferentUserType.setUserEmail(newEmail);
        verifyAbsentInDatabase(DeadlineExtensionAttributes.valueOf(deadlineExtensionDifferentUserType));
    }

    @Test
    public void testGetDeadlineExtensionsPossiblyNeedingClosingSoonEmail() {
        String validCourseId = VALID_COURSE_ID + "-closing";

        DeadlineExtension deadlineExtensionNow = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, "1-" + VALID_USER_EMAIL,
                false, false, Instant.now().plusSeconds(10));
        DeadlineExtension deadlineExtensionTwelveHoursAhead = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, "2-" + VALID_USER_EMAIL,
                false, false, TimeHelperExtension.getInstantHoursOffsetFromNow(12));
        DeadlineExtension deadlineExtensionOneDayAhead = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, "3-" + VALID_USER_EMAIL,
                false, false, TimeHelper.getInstantDaysOffsetFromNow(1));
        DeadlineExtension deadlineExtensionInstructor = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, "4-" + VALID_USER_EMAIL,
                true, false, TimeHelperExtension.getInstantHoursOffsetFromNow(12));
        DeadlineExtension deadlineExtensionOneDayBefore = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, "5-" + VALID_USER_EMAIL,
                false, false, TimeHelper.getInstantDaysOffsetBeforeNow(1));
        DeadlineExtension deadlineExtensionEmailSent = new DeadlineExtension(
                validCourseId, VALID_FEEDBACK_SESSION_NAME, "6-" + VALID_USER_EMAIL,
                false, true, TimeHelperExtension.getInstantHoursOffsetFromNow(12));
        List<DeadlineExtension> deadlineExtensions = List.of(deadlineExtensionNow, deadlineExtensionTwelveHoursAhead,
                deadlineExtensionOneDayAhead, deadlineExtensionInstructor, deadlineExtensionOneDayBefore,
                deadlineExtensionEmailSent);
        deadlineExtensionsDb.saveEntities(deadlineExtensions);

        List<DeadlineExtensionAttributes> deadlineExtensionsNeedingClosing =
                deadlineExtensionsDb.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();

        assertTrue(deadlineExtensionsNeedingClosing.contains(DeadlineExtensionAttributes.valueOf(deadlineExtensionNow)));
        assertTrue(deadlineExtensionsNeedingClosing
                .contains(DeadlineExtensionAttributes.valueOf(deadlineExtensionTwelveHoursAhead)));
        assertTrue(deadlineExtensionsNeedingClosing
                .contains(DeadlineExtensionAttributes.valueOf(deadlineExtensionOneDayAhead)));
        assertTrue(deadlineExtensionsNeedingClosing
                .contains(DeadlineExtensionAttributes.valueOf(deadlineExtensionInstructor)));
        assertFalse(deadlineExtensionsNeedingClosing
                .contains(DeadlineExtensionAttributes.valueOf(deadlineExtensionOneDayBefore)));
        assertFalse(deadlineExtensionsNeedingClosing
                .contains(DeadlineExtensionAttributes.valueOf(deadlineExtensionEmailSent)));

        for (DeadlineExtensionAttributes deadlineExtension : deadlineExtensionsNeedingClosing) {
            assertTrue(deadlineExtension.getEndTime().isAfter(Instant.now().minusSeconds(60)));
            assertTrue(deadlineExtension.getEndTime().isBefore(TimeHelper.getInstantDaysOffsetFromNow(1).plusSeconds(60)));
            assertFalse(deadlineExtension.getSentClosingSoonEmail());
        }
    }
}
