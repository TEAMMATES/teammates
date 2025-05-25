package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeMethod;
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
import teammates.test.AssertHelper;

/**
 * SUT: {@link DeadlineExtensionsLogic}.
 */
public class DeadlineExtensionsLogicTest extends BaseLogicTest {

    private static final String VALID_COURSE_ID = "valid-course-id";
    private static final String VALID_FEEDBACK_SESSION_NAME = "valid-feedback-session-name";
    private static final String VALID_USER_EMAIL = "valid@gmail.tmt";

    private final DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testCreateDeadlineExtension() throws Exception {

        ______TS("typical success case");

        DeadlineExtensionAttributes deadlineExtension = DeadlineExtensionAttributes
                .builder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false)
                .withEndTime(Const.TIME_REPRESENTS_LATER)
                .withSentClosingSoonEmail(true)
                .build();

        DeadlineExtensionAttributes createdDeadlineExtension =
                deadlineExtensionsLogic.createDeadlineExtension(deadlineExtension);
        verifyPresentInDatabase(createdDeadlineExtension);

        assertEquals(deadlineExtension.getCourseId(), createdDeadlineExtension.getCourseId());
        assertEquals(deadlineExtension.getFeedbackSessionName(), createdDeadlineExtension.getFeedbackSessionName());
        assertEquals(deadlineExtension.getUserEmail(), createdDeadlineExtension.getUserEmail());
        assertEquals(deadlineExtension.getIsInstructor(), createdDeadlineExtension.getIsInstructor());
        assertEquals(deadlineExtension.getEndTime(), createdDeadlineExtension.getEndTime());
        assertEquals(deadlineExtension.getSentClosingSoonEmail(), createdDeadlineExtension.getSentClosingSoonEmail());

        ______TS("failure: duplicate deadline extension");

        DeadlineExtensionAttributes duplicateDeadlineExtension = DeadlineExtensionAttributes
                .builder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false)
                .withEndTime(Const.TIME_REPRESENTS_LATER)
                .withSentClosingSoonEmail(true)
                .build();

        assertThrows(EntityAlreadyExistsException.class,
                () -> deadlineExtensionsLogic.createDeadlineExtension(duplicateDeadlineExtension));

        deadlineExtensionsLogic
                .deleteDeadlineExtension(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, VALID_USER_EMAIL, false);

        ______TS("failure case: invalid parameters");

        DeadlineExtensionAttributes invalidEmailDeadlineExtension = DeadlineExtensionAttributes
                .builder(VALID_COURSE_ID, VALID_FEEDBACK_SESSION_NAME, "invalid-email", false)
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> deadlineExtensionsLogic.createDeadlineExtension(invalidEmailDeadlineExtension));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "invalid-email",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        DeadlineExtensionAttributes invalidFeedbackSessionNameDeadlineExtension = DeadlineExtensionAttributes
                .builder(VALID_COURSE_ID, "", VALID_USER_EMAIL, false)
                .build();

        ipe = assertThrows(InvalidParametersException.class,
                () -> deadlineExtensionsLogic.createDeadlineExtension(invalidFeedbackSessionNameDeadlineExtension));
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
                () -> deadlineExtensionsLogic.createDeadlineExtension(invalidCourseIdDeadlineExtension));
        AssertHelper.assertContains(
                getPopulatedEmptyStringErrorMessage(
                        FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                        FieldValidator.COURSE_ID_FIELD_NAME,
                        FieldValidator.COURSE_ID_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> deadlineExtensionsLogic.createDeadlineExtension(null));
    }

    @Test
    public void testUpdateDeadlineExtension() throws Exception {
        DeadlineExtensionAttributes deadlineExtension =
                dataBundle.deadlineExtensions.get("student3InCourse1Session1");
        Instant now = Instant.now();

        ______TS("typical success case");
        DeadlineExtensionAttributes.UpdateOptions updateOptions = DeadlineExtensionAttributes
                .updateOptionsBuilder(
                        deadlineExtension.getCourseId(),
                        deadlineExtension.getFeedbackSessionName(),
                        deadlineExtension.getUserEmail(),
                        deadlineExtension.getIsInstructor())
                .withEndTime(now)
                .withSentClosingSoonEmail(true)
                .build();

        deadlineExtensionsLogic.updateDeadlineExtension(updateOptions);

        DeadlineExtensionAttributes updatedDeadlineExtension = deadlineExtensionsLogic.getDeadlineExtension(
                deadlineExtension.getCourseId(),
                deadlineExtension.getFeedbackSessionName(),
                deadlineExtension.getUserEmail(),
                deadlineExtension.getIsInstructor());

        assertEquals(deadlineExtension.getCourseId(), updatedDeadlineExtension.getCourseId());
        assertEquals(deadlineExtension.getFeedbackSessionName(), updatedDeadlineExtension.getFeedbackSessionName());
        assertEquals(deadlineExtension.getUserEmail(), updatedDeadlineExtension.getUserEmail());
        assertEquals(deadlineExtension.getIsInstructor(), updatedDeadlineExtension.getIsInstructor());
        assertEquals(now, updatedDeadlineExtension.getEndTime());
        assertTrue(updatedDeadlineExtension.getSentClosingSoonEmail());

        ______TS("endTime modified, sentClosingSoonEmail not set: sentClosingSoonEmail updated to false");

        updateOptions = DeadlineExtensionAttributes
                .updateOptionsBuilder(
                        deadlineExtension.getCourseId(),
                        deadlineExtension.getFeedbackSessionName(),
                        deadlineExtension.getUserEmail(),
                        deadlineExtension.getIsInstructor())
                .withEndTime(Const.TIME_REPRESENTS_LATER)
                .build();

        deadlineExtensionsLogic.updateDeadlineExtension(updateOptions);

        updatedDeadlineExtension = deadlineExtensionsLogic.getDeadlineExtension(
                deadlineExtension.getCourseId(),
                deadlineExtension.getFeedbackSessionName(),
                deadlineExtension.getUserEmail(),
                deadlineExtension.getIsInstructor());

        assertEquals(deadlineExtension.getCourseId(), updatedDeadlineExtension.getCourseId());
        assertEquals(deadlineExtension.getFeedbackSessionName(), updatedDeadlineExtension.getFeedbackSessionName());
        assertEquals(deadlineExtension.getUserEmail(), updatedDeadlineExtension.getUserEmail());
        assertEquals(deadlineExtension.getIsInstructor(), updatedDeadlineExtension.getIsInstructor());
        assertEquals(Const.TIME_REPRESENTS_LATER, updatedDeadlineExtension.getEndTime());
        assertFalse(updatedDeadlineExtension.getSentClosingSoonEmail());

        ______TS("failure: deadline extension not found");
        DeadlineExtensionAttributes.UpdateOptions updateOptionsNotFound = DeadlineExtensionAttributes
                .updateOptionsBuilder("unknown-course-id", "unknown-fs-name", "unknown@gmail.tmt", true)
                .withEndTime(Const.TIME_REPRESENTS_LATER)
                .withSentClosingSoonEmail(!deadlineExtension.getSentClosingSoonEmail())
                .build();

        assertThrows(EntityDoesNotExistException.class,
                () -> deadlineExtensionsLogic.updateDeadlineExtension(updateOptionsNotFound));
    }

    @Test
    public void testDeleteDeadlineExtension() {
        DeadlineExtensionAttributes deadlineExtension =
                dataBundle.deadlineExtensions.get("student3InCourse1Session1");

        ______TS("silent deletion of non-existent deadline extension");

        deadlineExtensionsLogic.deleteDeadlineExtension("unknown-course-id", "unknown-fs-name", "not-found@test.com", false);

        ______TS("typical success case");

        verifyPresentInDatabase(deadlineExtension);

        deadlineExtensionsLogic.deleteDeadlineExtension(
                deadlineExtension.getCourseId(),
                deadlineExtension.getFeedbackSessionName(),
                deadlineExtension.getUserEmail(),
                deadlineExtension.getIsInstructor());

        verifyAbsentInDatabase(deadlineExtension);

        ______TS("silent deletion of same deadline extension");

        deadlineExtensionsLogic.deleteDeadlineExtension(
                deadlineExtension.getCourseId(),
                deadlineExtension.getFeedbackSessionName(),
                deadlineExtension.getUserEmail(),
                deadlineExtension.getIsInstructor());

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> deadlineExtensionsLogic.deleteDeadlineExtension(null, null, null, false));
    }

    @Test
    public void testGetDeadlineExtension() {
        DeadlineExtensionAttributes originalDeadlineExtension =
                dataBundle.deadlineExtensions.get("student3InCourse1Session1");

        ______TS("typical success case");

        DeadlineExtensionAttributes retrievedDeadlineExtension = deadlineExtensionsLogic.getDeadlineExtension(
                originalDeadlineExtension.getCourseId(),
                originalDeadlineExtension.getFeedbackSessionName(),
                originalDeadlineExtension.getUserEmail(),
                originalDeadlineExtension.getIsInstructor());

        assertEquals(originalDeadlineExtension, retrievedDeadlineExtension);

        ______TS("deadline extension not found");

        assertNull(deadlineExtensionsLogic
                .getDeadlineExtension("unknown-course-id", "unknown-fs-name", "not-found@test.com", false));

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> deadlineExtensionsLogic.getDeadlineExtension(null, null, null, false));
    }

    @Test
    public void testDeleteDeadlineExtensions_byAttributeDeletionQuery() throws Exception {
        DeadlineExtensionAttributes deadlineExtension1 =
                dataBundle.deadlineExtensions.get("student4InCourse1Session1");
        DeadlineExtensionAttributes deadlineExtension2 =
                dataBundle.deadlineExtensions.get("student4InCourse1Session2");

        ______TS("typical success case: only delete deadline extensions in feedback session");

        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(deadlineExtension1.getCourseId())
                .withFeedbackSessionName(deadlineExtension1.getFeedbackSessionName())
                .build();
        deadlineExtensionsLogic.deleteDeadlineExtensions(query);
        verifyAbsentInDatabase(deadlineExtension1);
        verifyPresentInDatabase(deadlineExtension2);

        ______TS("typical success case: delete all deadline extensions in course");

        query = AttributesDeletionQuery.builder()
                .withCourseId(deadlineExtension1.getCourseId())
                .build();
        deadlineExtensionsLogic.deleteDeadlineExtensions(query);
        verifyAbsentInDatabase(deadlineExtension2);

        ______TS("query is null: throw assertion error");

        assertThrows(AssertionError.class, () -> deadlineExtensionsLogic.deleteDeadlineExtensions(null));
    }

    @Test
    public void testDeleteDeadlineExtensions_byCourseIdAndUserDetails() throws Exception {

        ______TS("Typical success case");

        DeadlineExtensionAttributes deadlineExtension =
                dataBundle.deadlineExtensions.get("student4InCourse1Session1");
        DeadlineExtensionAttributes deadlineExtensionDifferentFs =
                dataBundle.deadlineExtensions.get("student4InCourse1Session2");
        DeadlineExtensionAttributes deadlineExtensionDifferentCourse = DeadlineExtensionAttributes
                .builder("different-course-id", deadlineExtension.getFeedbackSessionName(),
                        deadlineExtension.getUserEmail(), deadlineExtension.getIsInstructor())
                .build();
        DeadlineExtensionAttributes deadlineExtensionDifferentUserType = DeadlineExtensionAttributes
                .builder(deadlineExtension.getCourseId(), deadlineExtension.getFeedbackSessionName(),
                        deadlineExtension.getUserEmail(), true)
                .build();
        deadlineExtensionsLogic.createDeadlineExtension(deadlineExtensionDifferentCourse);
        deadlineExtensionsLogic.createDeadlineExtension(deadlineExtensionDifferentUserType);

        deadlineExtensionsLogic.deleteDeadlineExtensions(deadlineExtension.getCourseId(),
                deadlineExtension.getUserEmail(), false);

        ______TS("Deadline extension with same course id deleted");

        verifyAbsentInDatabase(deadlineExtension);
        verifyAbsentInDatabase(deadlineExtensionDifferentFs);

        ______TS("Deadline extension with different course id not deleted");

        verifyPresentInDatabase(deadlineExtensionDifferentCourse);

        ______TS("Deadline extension with different user type not deleted");

        verifyPresentInDatabase(deadlineExtensionDifferentUserType);

        ______TS("Delete single deadline extension");

        deadlineExtensionsLogic.deleteDeadlineExtensions("different-course-id", deadlineExtension.getUserEmail(), false);
        verifyAbsentInDatabase(deadlineExtensionDifferentCourse);

        deadlineExtensionsLogic.deleteDeadlineExtensions(
                deadlineExtension.getCourseId(), deadlineExtension.getUserEmail(), true);
        verifyAbsentInDatabase(deadlineExtensionDifferentUserType);
    }

    @Test
    public void testUpdateDeadlineExtensionsWithNewEmail() throws Exception {
        String newEmail = "new-email@gmail.tmt";

        DeadlineExtensionAttributes deadlineExtension =
                dataBundle.deadlineExtensions.get("student4InCourse1Session1");
        DeadlineExtensionAttributes deadlineExtensionDifferentFs =
                dataBundle.deadlineExtensions.get("student4InCourse1Session2");
        DeadlineExtensionAttributes deadlineExtensionDifferentCourse = DeadlineExtensionAttributes
                .builder("different-course-id", deadlineExtension.getFeedbackSessionName(),
                        deadlineExtension.getUserEmail(), deadlineExtension.getIsInstructor())
                .build();
        DeadlineExtensionAttributes deadlineExtensionDifferentUserType = DeadlineExtensionAttributes
                .builder(deadlineExtension.getCourseId(), deadlineExtension.getFeedbackSessionName(),
                        deadlineExtension.getUserEmail(), true)
                .build();
        deadlineExtensionsLogic.createDeadlineExtension(deadlineExtensionDifferentCourse);
        deadlineExtensionsLogic.createDeadlineExtension(deadlineExtensionDifferentUserType);

        deadlineExtensionsLogic.updateDeadlineExtensionsWithNewEmail(deadlineExtension.getCourseId(),
                deadlineExtension.getUserEmail(), newEmail, false);

        ______TS("Deadline extension with same course id updated");

        verifyAbsentInDatabase(deadlineExtension);
        assertNotNull(deadlineExtensionsLogic.getDeadlineExtension(
                deadlineExtension.getCourseId(),
                deadlineExtension.getFeedbackSessionName(),
                newEmail,
                false));

        verifyAbsentInDatabase(deadlineExtensionDifferentFs);
        assertNotNull(deadlineExtensionsLogic.getDeadlineExtension(
                deadlineExtensionDifferentFs.getCourseId(),
                deadlineExtensionDifferentFs.getFeedbackSessionName(),
                newEmail,
                false));

        ______TS("Deadline extension with different course id not updated");

        verifyPresentInDatabase(deadlineExtensionDifferentCourse);

        ______TS("Deadline extension with different user type not updated");

        verifyPresentInDatabase(deadlineExtensionDifferentUserType);
    }

    @Test
    public void testGetDeadlineExtensionsPossiblyNeedingClosingSoonEmail() throws Exception {
        String validCourseId = VALID_COURSE_ID + "-closing";

        DeadlineExtensionAttributes deadlineExtensionNow = DeadlineExtensionAttributes
                .builder(validCourseId, VALID_FEEDBACK_SESSION_NAME, "1-" + VALID_USER_EMAIL, false)
                .withSentClosingSoonEmail(false)
                .withEndTime(Instant.now().plusSeconds(10))
                .build();
        DeadlineExtensionAttributes deadlineExtensionTwelveHoursAhead = DeadlineExtensionAttributes
                .builder(validCourseId, VALID_FEEDBACK_SESSION_NAME, "2-" + VALID_USER_EMAIL, false)
                .withSentClosingSoonEmail(false)
                .withEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(12))
                .build();
        DeadlineExtensionAttributes deadlineExtensionOneDayAhead = DeadlineExtensionAttributes
                .builder(validCourseId, VALID_FEEDBACK_SESSION_NAME, "3-" + VALID_USER_EMAIL, false)
                .withSentClosingSoonEmail(false)
                .withEndTime(TimeHelper.getInstantDaysOffsetFromNow(1))
                .build();
        DeadlineExtensionAttributes deadlineExtensionInstructor = DeadlineExtensionAttributes
                .builder(validCourseId, VALID_FEEDBACK_SESSION_NAME, "4-" + VALID_USER_EMAIL, true)
                .withSentClosingSoonEmail(false)
                .withEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(12))
                .build();
        DeadlineExtensionAttributes deadlineExtensionOneDayBefore = DeadlineExtensionAttributes
                .builder(validCourseId, VALID_FEEDBACK_SESSION_NAME, "5-" + VALID_USER_EMAIL, false)
                .withSentClosingSoonEmail(false)
                .withEndTime(TimeHelper.getInstantDaysOffsetBeforeNow(1))
                .build();
        DeadlineExtensionAttributes deadlineExtensionEmailSent = DeadlineExtensionAttributes
                .builder(validCourseId, VALID_FEEDBACK_SESSION_NAME, "6-" + VALID_USER_EMAIL, false)
                .withSentClosingSoonEmail(true)
                .withEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(12))
                .build();

        List<DeadlineExtensionAttributes> deadlineExtensions = List.of(deadlineExtensionNow,
                deadlineExtensionTwelveHoursAhead, deadlineExtensionOneDayAhead,
                deadlineExtensionInstructor, deadlineExtensionOneDayBefore, deadlineExtensionEmailSent);

        for (var deadlineExtension : deadlineExtensions) {
            deadlineExtensionsLogic.createDeadlineExtension(deadlineExtension);
        }

        List<DeadlineExtensionAttributes> deadlineExtensionsNeedingClosing =
                deadlineExtensionsLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();

        assertTrue(deadlineExtensionsNeedingClosing.contains(deadlineExtensionNow));
        assertTrue(deadlineExtensionsNeedingClosing.contains(deadlineExtensionTwelveHoursAhead));
        assertTrue(deadlineExtensionsNeedingClosing.contains(deadlineExtensionOneDayAhead));
        assertTrue(deadlineExtensionsNeedingClosing.contains(deadlineExtensionInstructor));
        assertFalse(deadlineExtensionsNeedingClosing.contains(deadlineExtensionOneDayBefore));
        assertFalse(deadlineExtensionsNeedingClosing.contains(deadlineExtensionEmailSent));

        for (var deadlineExtension : deadlineExtensionsNeedingClosing) {
            assertTrue(deadlineExtension.getEndTime().isAfter(Instant.now().minusSeconds(60)));
            assertTrue(deadlineExtension.getEndTime().isBefore(TimeHelper.getInstantDaysOffsetFromNow(1).plusSeconds(60)));
            assertFalse(deadlineExtension.getSentClosingSoonEmail());
        }
    }

}
