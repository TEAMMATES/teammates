package teammates.test.cases.datatransfer;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.TimeHelperExtension;

/**
 * SUT: {@link FeedbackSessionAttributes}.
 */
public class FeedbackSessionAttributesTest extends BaseTestCase {
    private Instant startTime;
    private Instant endTime;

    private FeedbackSessionAttributes fsa;

    @BeforeClass
    public void classSetup() {
        ZoneId timeZone = ZoneId.of("Asia/Singapore");
        startTime = TimeHelper.convertLocalDateTimeToInstant(
                TimeHelper.parseDateTimeFromSessionsForm("Mon, 09 May, 2016", "10", "0"), timeZone);
        endTime = TimeHelper.convertLocalDateTimeToInstant(
                TimeHelper.parseDateTimeFromSessionsForm("Tue, 09 May, 2017", "10", "0"), timeZone);

        fsa = FeedbackSessionAttributes
                .builder("", "", "")
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withTimeZone(timeZone)
                .withGracePeriodMinutes(15)
                .withOpeningEmailEnabled(false)
                .withClosingEmailEnabled(false)
                .withPublishedEmailEnabled(false)
                .build();
    }

    @Test
    public void testSort() {
        List<FeedbackSessionAttributes> testList = new ArrayList<>();
        List<FeedbackSessionAttributes> expected = new ArrayList<>();

        Instant time1 = TimeHelper.parseInstant("2014-01-01 12:00 AM +0000");
        Instant time2 = TimeHelper.parseInstant("2014-02-01 12:00 AM +0000");
        Instant time3 = TimeHelper.parseInstant("2014-03-01 12:00 AM +0000");

        FeedbackSessionAttributes s1 =
                FeedbackSessionAttributes.builder("Session 1", "", "")
                        .withStartTime(time1)
                        .withEndTime(time2)
                        .build();
        FeedbackSessionAttributes s2 =
                FeedbackSessionAttributes.builder("Session 2", "", "")
                        .withStartTime(time2)
                        .withEndTime(time3)
                        .build();
        FeedbackSessionAttributes s3 =
                FeedbackSessionAttributes.builder("Session 3", "", "")
                        .withStartTime(time1)
                        .withEndTime(time2)
                        .build();
        FeedbackSessionAttributes s4 =
                FeedbackSessionAttributes.builder("Session 4", "", "")
                        .withStartTime(time1)
                        .withEndTime(time3)
                        .build();
        FeedbackSessionAttributes s5 =
                FeedbackSessionAttributes.builder("Session 5", "", "")
                        .withStartTime(time2)
                        .withEndTime(time3)
                        .build();

        testList.add(s1);
        testList.add(s2);
        testList.add(s3);
        testList.add(s4);
        testList.add(s5);

        expected.add(s2);
        expected.add(s5);
        expected.add(s4);
        expected.add(s1);
        expected.add(s3);

        testList.sort(FeedbackSessionAttributes.DESCENDING_ORDER);
        for (int i = 0; i < testList.size(); i++) {
            assertEquals(expected.get(i), testList.get(i));
        }
    }

    @Test
    public void testBuilderWithDefaultValues() {
        FeedbackSessionAttributes fsa = FeedbackSessionAttributes
                .builder("name", "course", "email")
                .build();

        // Default values for next fields
        assertTrue(fsa.isOpeningEmailEnabled());
        assertTrue(fsa.isClosingEmailEnabled());
        assertTrue(fsa.isPublishedEmailEnabled());
        assertEquals("", fsa.getInstructions());
        assertNotNull(fsa.getRespondingInstructorList());
        assertNotNull(fsa.getRespondingStudentList());
        assertNull(fsa.getDeletedTime());
    }

    @Test
    public void testBuilderCopy() {
        FeedbackSessionAttributes original = FeedbackSessionAttributes
                .builder("newFeedbackSessionName", "course", "email")
                .withInstructions("default instructions")
                .withCreatedTime(Instant.now())
                .withStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(2))
                .withEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(5))
                .withSessionVisibleFromTime(TimeHelperExtension.getInstantHoursOffsetFromNow(1))
                .withResultsVisibleFromTime(TimeHelperExtension.getInstantHoursOffsetFromNow(6))
                .withTimeZone(ZoneId.of("Asia/Singapore")).withGracePeriodMinutes(0)
                .withOpeningEmailEnabled(false).withClosingEmailEnabled(false).withPublishedEmailEnabled(false)
                .build();

        FeedbackSessionAttributes copy = original.getCopy();

        assertEquals(original.getFeedbackSessionName(), copy.getFeedbackSessionName());
        assertEquals(original.getCourseId(), copy.getCourseId());
        assertEquals(original.getCreatorEmail(), copy.getCreatorEmail());
        assertEquals(original.getInstructions(), copy.getInstructions());
        assertEquals(original.getCreatedTime(), copy.getCreatedTime());
        assertEquals(original.getDeletedTime(), copy.getDeletedTime());
        assertEquals(original.getStartTime(), copy.getStartTime());
        assertEquals(original.getEndTime(), copy.getEndTime());
        assertEquals(original.getSessionVisibleFromTime(), copy.getSessionVisibleFromTime());
        assertEquals(original.getResultsVisibleFromTime(), copy.getResultsVisibleFromTime());
        assertEquals(original.getTimeZone(), copy.getTimeZone());
        assertEquals(original.getGracePeriodMinutes(), copy.getGracePeriodMinutes());
        assertEquals(original.isOpeningEmailEnabled(), copy.isOpeningEmailEnabled());
        assertEquals(original.isClosingEmailEnabled(), copy.isClosingEmailEnabled());
        assertEquals(original.isPublishedEmailEnabled(), copy.isPublishedEmailEnabled());
        assertEquals(original.isSentClosedEmail(), copy.isSentClosedEmail());
        assertEquals(original.isSentClosingEmail(), copy.isSentClosingEmail());
        assertEquals(original.isSentOpenEmail(), copy.isSentOpenEmail());
        assertEquals(original.isSentPublishedEmail(), copy.isSentPublishedEmail());
        assertEquals(original.getRespondingInstructorList(), copy.getRespondingInstructorList());
        assertEquals(original.getRespondingStudentList(), copy.getRespondingStudentList());
    }

    @Test
    public void testBasicGetters() {
        ______TS("get session stime, etime, name");

        assertEquals(fsa.getEndTime(), endTime);
        assertEquals(fsa.getStartTime(), startTime);

    }

    @Test
    public void testValidate() {
        ______TS("invalid parameter error messages");

        FeedbackSessionAttributes feedbackSessionAttributes = FeedbackSessionAttributes
                .builder("", "", "")
                .withStartTime(Instant.now())
                .withEndTime(Instant.now())
                .withCreatedTime(Instant.now())
                .withResultsVisibleFromTime(Instant.now())
                .withSessionVisibleFromTime(Instant.now())
                .withGracePeriodMinutes(-100)
                .build();
        assertEquals(feedbackSessionAttributes.getInvalidityInfo(), buildExpectedErrorMessages());
    }

    @Test
    public void testGetBackUpIdentifier() {
        FeedbackSessionAttributes sessionAttributes = FeedbackSessionAttributes
                .builder("newFeedbackSessionName", "course", "email")
                .withInstructions("default instructions")
                .withCreatedTime(Instant.now())
                .withStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(2))
                .withEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(5))
                .withSessionVisibleFromTime(TimeHelperExtension.getInstantHoursOffsetFromNow(1))
                .withResultsVisibleFromTime(TimeHelperExtension.getInstantHoursOffsetFromNow(6))
                .withTimeZone(ZoneId.of("Asia/Singapore")).withGracePeriodMinutes(0)
                .withOpeningEmailEnabled(false).withClosingEmailEnabled(false)
                .withPublishedEmailEnabled(false)
                .build();

        String expectedBackUpIdentifierMessage = "Recently modified feedback session::"
                + sessionAttributes.getCourseId() + "::" + sessionAttributes.getFeedbackSessionName();
        assertEquals(expectedBackUpIdentifierMessage, sessionAttributes.getBackupIdentifier());
    }

    private List<String> buildExpectedErrorMessages() {
        String feedbackSessionNameError = "The field 'feedback session name' should not be empty. The value of 'feedback "
                + "session name' field should be no longer than 38 characters.";
        String courseIdError = "The field 'course ID' is empty. A course ID can contain letters, numbers, fullstops, "
                + "hyphens, underscores, and dollar signs. It cannot be longer than 40 characters, cannot be empty and "
                + "cannot contain spaces.";
        String creatorEmailError = "The field 'email' is empty. An email address contains some text followed "
                + "by one '@' sign followed by some more text. It cannot be longer than 254 characters, cannot be empty"
                + " and cannot contain spaces.";
        String gracePeriodError = "Grace period should not be negative." + " "
                + "The value must be one of the options in the grace period dropdown selector.";

        return Arrays.asList(feedbackSessionNameError, courseIdError, creatorEmailError, gracePeriodError);
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        Instant sessionVisibleTime = TimeHelper.getInstantDaysOffsetFromNow(-3);
        Instant startTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        Instant endTime = TimeHelper.getInstantDaysOffsetFromNow(-1);
        Instant resultVisibleTime = TimeHelper.getInstantDaysOffsetFromNow(1);
        FeedbackSessionAttributes.UpdateOptions updateOptions =
                FeedbackSessionAttributes.updateOptionsBuilder("sessionName", "courseId")
                        .withInstructions("instruction 1")
                        .withStartTime(startTime)
                        .withEndTime(endTime)
                        .withSessionVisibleFromTime(sessionVisibleTime)
                        .withResultsVisibleFromTime(resultVisibleTime)
                        .withTimeZone(ZoneId.of("Asia/Singapore"))
                        .withGracePeriod(Duration.ofMinutes(5))
                        .withSentOpenEmail(true)
                        .withSentClosingEmail(true)
                        .withSentClosedEmail(true)
                        .withSentPublishedEmail(false)
                        .withIsClosingEmailEnabled(true)
                        .withIsPublishedEmailEnabled(false)
                        .withAddingInstructorRespondent("instructor@email.com")
                        .withAddingStudentRespondent("student@email.com")
                        .withUpdatingStudentRespondent("studentA@email.com", "studentB@email.com")
                        .withUpdatingInstructorRespondent("insturctorA@email.com", "insturctorB@email.com")
                        .withRemovingStudentRespondent("studentF@email.com")
                        .withRemovingInstructorRespondent("instructorF@email.com")
                        .build();

        assertEquals("sessionName", updateOptions.getFeedbackSessionName());
        assertEquals("courseId", updateOptions.getCourseId());

        FeedbackSessionAttributes feedbackSessionAttributes =
                FeedbackSessionAttributes.builder("sessionName", "courseId", "i@email.com")
                        .withInstructions("instruction")
                        .withCreatedTime(TimeHelper.getInstantDaysOffsetFromNow(-10))
                        .withStartTime(TimeHelper.getInstantDaysOffsetFromNow(1))
                        .withEndTime(TimeHelper.getInstantDaysOffsetFromNow(2))
                        .withSessionVisibleFromTime(sessionVisibleTime.minusSeconds(60))
                        .withResultsVisibleFromTime(Instant.now().minusSeconds(60))
                        .withTimeZone(ZoneId.of("UTC"))
                        .withGracePeriodMinutes(20)
                        .withSentOpenEmail(false)
                        .withSentClosingEmail(false)
                        .withSentClosedEmail(false)
                        .withSentPublishedEmail(true)
                        .withOpeningEmailEnabled(true)
                        .withClosingEmailEnabled(false)
                        .withPublishedEmailEnabled(false)
                        .withRespondingInstructorList(Sets.newHashSet("insturctorA@email.com"))
                        .withRespondingStudentList(Sets.newHashSet("studentA@email.com", "studentF@email.com"))
                        .build();

        feedbackSessionAttributes.update(updateOptions);

        assertEquals("instruction 1", feedbackSessionAttributes.getInstructions());
        assertEquals(startTime, feedbackSessionAttributes.getStartTime());
        assertEquals(endTime, feedbackSessionAttributes.getEndTime());
        assertEquals(sessionVisibleTime, feedbackSessionAttributes.getSessionVisibleFromTime());
        assertEquals(resultVisibleTime, feedbackSessionAttributes.getResultsVisibleFromTime());
        assertEquals(ZoneId.of("Asia/Singapore"), feedbackSessionAttributes.getTimeZone());
        assertEquals(5, feedbackSessionAttributes.getGracePeriodMinutes());
        assertTrue(feedbackSessionAttributes.isSentOpenEmail());
        assertTrue(feedbackSessionAttributes.isSentClosingEmail());
        assertTrue(feedbackSessionAttributes.isSentClosedEmail());
        assertFalse(feedbackSessionAttributes.isSentPublishedEmail());
        assertTrue(feedbackSessionAttributes.isOpeningEmailEnabled());
        assertTrue(feedbackSessionAttributes.isClosingEmailEnabled());
        assertFalse(feedbackSessionAttributes.isPublishedEmailEnabled());
        assertEquals(Sets.newHashSet("student@email.com", "studentB@email.com"),
                feedbackSessionAttributes.getRespondingStudentList());
        assertEquals(Sets.newHashSet("instructor@email.com", "insturctorB@email.com"),
                feedbackSessionAttributes.getRespondingInstructorList());

        // constructor update option based on existing update option
        FeedbackSessionAttributes.UpdateOptions newUpdateOptions =
                FeedbackSessionAttributes.updateOptionsBuilder(updateOptions)
                        .withInstructions("instruction")
                        .build();
        feedbackSessionAttributes.update(newUpdateOptions);
        assertEquals("instruction", feedbackSessionAttributes.getInstructions());
    }

    @Test
    public void testUpdateOptionsBuilder_withNullInput_shouldFailWithAssertionError() {
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder(null, "courseId"));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withInstructions(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withStartTime(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withEndTime(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withSessionVisibleFromTime(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withResultsVisibleFromTime(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withTimeZone(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withGracePeriod(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withAddingInstructorRespondent(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withAddingStudentRespondent(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withUpdatingStudentRespondent(null, "email@email.com"));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withUpdatingStudentRespondent("email@email.com", null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withUpdatingInstructorRespondent(null, "email@email.com"));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withUpdatingInstructorRespondent("email@email.com", null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withRemovingStudentRespondent(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withRemovingInstructorRespondent(null));
    }

}
