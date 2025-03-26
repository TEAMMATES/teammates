package teammates.common.datatransfer.attributes;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackSessionAttributes}.
 */
public class FeedbackSessionAttributesTest extends BaseTestCase {

    @Test
    public void testBuilder_buildNothing_shouldUseDefaultValues() {
        FeedbackSessionAttributes fsa = FeedbackSessionAttributes
                .builder("name", "course")
                .build();

        assertEquals("name", fsa.getFeedbackSessionName());
        assertEquals("course", fsa.getCourseId());

        // Default values
        assertNull(fsa.getCreatorEmail());
        assertNotNull(fsa.getCreatedTime());
        assertEquals("", fsa.getInstructions());
        assertNull(fsa.getDeletedTime());
        assertNull(fsa.getStartTime());
        assertNull(fsa.getEndTime());
        assertNull(fsa.getSessionVisibleFromTime());
        assertNull(fsa.getResultsVisibleFromTime());
        assertEquals(Const.DEFAULT_TIME_ZONE, fsa.getTimeZone());
        assertEquals(0, fsa.getGracePeriodMinutes());

        assertFalse(fsa.isSentOpeningSoonEmail());
        assertFalse(fsa.isSentOpenedEmail());
        assertFalse(fsa.isSentClosingSoonEmail());
        assertFalse(fsa.isSentClosedEmail());
        assertFalse(fsa.isSentPublishedEmail());

        assertTrue(fsa.isOpenedEmailEnabled());
        assertTrue(fsa.isClosingSoonEmailEnabled());
        assertTrue(fsa.isPublishedEmailEnabled());

        assertEquals(new HashMap<>(), fsa.getStudentDeadlines());
        assertEquals(new HashMap<>(), fsa.getInstructorDeadlines());

        assertEquals(fsa.getEndTime(), fsa.getDeadline());
    }

    @Test
    public void testBuilder_withNullInput_shouldFailWithAssertionError() {
        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder(null, "course")
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", "course")
                    .withCreatorEmail(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", "course")
                    .withInstructions(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", "course")
                    .withStartTime(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", "course")
                    .withEndTime(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", "course")
                    .withSessionVisibleFromTime(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", "course")
                    .withResultsVisibleFromTime(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", "course")
                    .withTimeZone(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", "course")
                    .withGracePeriod(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", "course")
                    .withStudentDeadlines(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackSessionAttributes.builder("session name", "course")
                    .withInstructorDeadlines(null)
                    .build();
        });
    }

    @Test
    public void testValueOf_withAllFieldPopulatedFeedbackSession_shouldGenerateAttributesCorrectly() {
        FeedbackSession feedbackSession = new FeedbackSession(
                "testName", "testCourse", "email@email.com", "text",
                Instant.now(), null,
                Instant.now().minusSeconds(10), Instant.now().plusSeconds(10),
                Instant.now().minusSeconds(20), Instant.now().plusSeconds(20),
                "UTC", 10,
                false, false, false, false, false,
                true, true, true, new HashMap<>(), new HashMap<>());

        FeedbackSessionAttributes feedbackSessionAttributes = FeedbackSessionAttributes.valueOf(feedbackSession);

        assertEquals(feedbackSession.getFeedbackSessionName(), feedbackSessionAttributes.getFeedbackSessionName());
        assertEquals(feedbackSession.getCourseId(), feedbackSessionAttributes.getCourseId());
        assertEquals(feedbackSession.getCreatorEmail(), feedbackSessionAttributes.getCreatorEmail());
        assertEquals(feedbackSession.getInstructions(), feedbackSessionAttributes.getInstructions());
        assertEquals(feedbackSession.getCreatedTime(), feedbackSessionAttributes.getCreatedTime());
        assertEquals(feedbackSession.getDeletedTime(), feedbackSessionAttributes.getDeletedTime());
        assertEquals(feedbackSession.getSessionVisibleFromTime(), feedbackSessionAttributes.getSessionVisibleFromTime());
        assertEquals(feedbackSession.getStartTime(), feedbackSessionAttributes.getStartTime());
        assertEquals(feedbackSession.getEndTime(), feedbackSessionAttributes.getEndTime());
        assertEquals(feedbackSession.getResultsVisibleFromTime(), feedbackSessionAttributes.getResultsVisibleFromTime());
        assertEquals(feedbackSession.isSentOpeningSoonEmail(), feedbackSessionAttributes.isSentOpeningSoonEmail());
        assertEquals(feedbackSession.isSentOpenedEmail(), feedbackSessionAttributes.isSentOpenedEmail());
        assertEquals(feedbackSession.isSentClosingSoonEmail(), feedbackSessionAttributes.isSentClosingSoonEmail());
        assertEquals(feedbackSession.isSentClosedEmail(), feedbackSessionAttributes.isSentClosedEmail());
        assertEquals(feedbackSession.isSentPublishedEmail(), feedbackSessionAttributes.isSentPublishedEmail());
        assertEquals(feedbackSession.isOpenedEmailEnabled(), feedbackSessionAttributes.isOpenedEmailEnabled());
        assertEquals(feedbackSession.isClosingSoonEmailEnabled(), feedbackSessionAttributes.isClosingSoonEmailEnabled());
        assertEquals(feedbackSession.isPublishedEmailEnabled(), feedbackSessionAttributes.isPublishedEmailEnabled());
        assertEquals(feedbackSession.getStudentDeadlines(), feedbackSessionAttributes.getStudentDeadlines());
        assertEquals(feedbackSession.getInstructorDeadlines(), feedbackSessionAttributes.getInstructorDeadlines());

        assertEquals(feedbackSession.getEndTime(), feedbackSessionAttributes.getDeadline());
    }

    @Test
    public void testValueOf_withSomeFieldsPopulatedAsNull_shouldUseDefaultValues() {
        FeedbackSession feedbackSession = new FeedbackSession(
                "testName", "testCourse", "email@email.com", null,
                Instant.now(), null,
                Instant.now().minusSeconds(10), Instant.now().plusSeconds(10),
                Instant.now().minusSeconds(20), Instant.now().plusSeconds(20),
                "UTC", 10, false,
                false, false, false, false,
                true, true, true, null, null);
        assertNull(feedbackSession.getInstructions());

        FeedbackSessionAttributes feedbackSessionAttributes = FeedbackSessionAttributes.valueOf(feedbackSession);

        assertEquals(feedbackSession.getFeedbackSessionName(), feedbackSessionAttributes.getFeedbackSessionName());
        assertEquals(feedbackSession.getCourseId(), feedbackSessionAttributes.getCourseId());
        assertEquals(feedbackSession.getCreatorEmail(), feedbackSessionAttributes.getCreatorEmail());
        assertEquals("", feedbackSessionAttributes.getInstructions());
        assertEquals(feedbackSession.getCreatedTime(), feedbackSessionAttributes.getCreatedTime());
        assertEquals(feedbackSession.getDeletedTime(), feedbackSessionAttributes.getDeletedTime());
        assertEquals(feedbackSession.getSessionVisibleFromTime(), feedbackSessionAttributes.getSessionVisibleFromTime());
        assertEquals(feedbackSession.getStartTime(), feedbackSessionAttributes.getStartTime());
        assertEquals(feedbackSession.getEndTime(), feedbackSessionAttributes.getEndTime());
        assertEquals(feedbackSession.getResultsVisibleFromTime(), feedbackSessionAttributes.getResultsVisibleFromTime());
        assertEquals(feedbackSession.isSentOpeningSoonEmail(), feedbackSessionAttributes.isSentOpeningSoonEmail());
        assertEquals(feedbackSession.isSentOpenedEmail(), feedbackSessionAttributes.isSentOpenedEmail());
        assertEquals(feedbackSession.isSentClosingSoonEmail(), feedbackSessionAttributes.isSentClosingSoonEmail());
        assertEquals(feedbackSession.isSentClosedEmail(), feedbackSessionAttributes.isSentClosedEmail());
        assertEquals(feedbackSession.isSentPublishedEmail(), feedbackSessionAttributes.isSentPublishedEmail());
        assertEquals(feedbackSession.isOpenedEmailEnabled(), feedbackSessionAttributes.isOpenedEmailEnabled());
        assertEquals(feedbackSession.isClosingSoonEmailEnabled(), feedbackSessionAttributes.isClosingSoonEmailEnabled());
        assertEquals(feedbackSession.isPublishedEmailEnabled(), feedbackSessionAttributes.isPublishedEmailEnabled());
        assertEquals(new HashMap<>(), feedbackSessionAttributes.getStudentDeadlines());
        assertEquals(new HashMap<>(), feedbackSessionAttributes.getInstructorDeadlines());

        assertEquals(feedbackSession.getEndTime(), feedbackSessionAttributes.getDeadline());
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectly() {

        String timeZone = "Asia/Singapore";
        Instant startTime = TimeHelper.parseInstant("2016-05-09T10:00:00+08:00");
        Instant endTime = TimeHelper.parseInstant("2017-05-09T10:00:00+08:00");

        FeedbackSessionAttributes fsa = FeedbackSessionAttributes
                .builder("sessionName", "courseId")
                .withCreatorEmail("email@email.com")
                .withInstructions("instructor")
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withSessionVisibleFromTime(startTime.minusSeconds(60))
                .withResultsVisibleFromTime(endTime.plusSeconds(60))
                .withTimeZone(timeZone)
                .withGracePeriod(Duration.ofMinutes(15))
                .withIsClosingSoonEmailEnabled(false)
                .withIsPublishedEmailEnabled(false)
                .build();

        assertEquals("sessionName", fsa.getFeedbackSessionName());
        assertEquals("courseId", fsa.getCourseId());
        assertEquals("email@email.com", fsa.getCreatorEmail());
        assertEquals(startTime, fsa.getStartTime());
        assertEquals(endTime, fsa.getEndTime());
        assertEquals(startTime.minusSeconds(60), fsa.getSessionVisibleFromTime());
        assertEquals(endTime.plusSeconds(60), fsa.getResultsVisibleFromTime());
        assertEquals(timeZone, fsa.getTimeZone());
        assertEquals(15, fsa.getGracePeriodMinutes());
        assertTrue(fsa.isOpenedEmailEnabled());
        assertFalse(fsa.isClosingSoonEmailEnabled());
        assertFalse(fsa.isPublishedEmailEnabled());

        assertFalse(fsa.isSentOpeningSoonEmail());
        assertFalse(fsa.isSentOpenedEmail());
        assertFalse(fsa.isSentClosingSoonEmail());
        assertFalse(fsa.isSentClosedEmail());
        assertFalse(fsa.isSentPublishedEmail());

        assertEquals(new HashMap<>(), fsa.getStudentDeadlines());
        assertEquals(new HashMap<>(), fsa.getInstructorDeadlines());

        assertEquals(endTime, fsa.getDeadline());

    }

    @Test
    public void testGetCopy() {
        FeedbackSessionAttributes original = FeedbackSessionAttributes
                .builder("newFeedbackSessionName", "course")
                .withCreatorEmail("email@email.com")
                .withInstructions("default instructions")
                .withStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(2))
                .withEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(5))
                .withSessionVisibleFromTime(TimeHelperExtension.getInstantHoursOffsetFromNow(1))
                .withResultsVisibleFromTime(TimeHelperExtension.getInstantHoursOffsetFromNow(6))
                .withTimeZone("Asia/Singapore")
                .withGracePeriod(Duration.ZERO)
                .withIsClosingSoonEmailEnabled(false)
                .withIsPublishedEmailEnabled(false)
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
        assertEquals(original.isOpenedEmailEnabled(), copy.isOpenedEmailEnabled());
        assertEquals(original.isClosingSoonEmailEnabled(), copy.isClosingSoonEmailEnabled());
        assertEquals(original.isPublishedEmailEnabled(), copy.isPublishedEmailEnabled());
        assertEquals(original.isSentClosedEmail(), copy.isSentClosedEmail());
        assertEquals(original.isSentClosingSoonEmail(), copy.isSentClosingSoonEmail());
        assertEquals(original.isSentOpeningSoonEmail(), copy.isSentOpeningSoonEmail());
        assertEquals(original.isSentOpenedEmail(), copy.isSentOpenedEmail());
        assertEquals(original.isSentPublishedEmail(), copy.isSentPublishedEmail());
        assertEquals(original.getStudentDeadlines(), copy.getStudentDeadlines());
        assertEquals(original.getInstructorDeadlines(), copy.getInstructorDeadlines());

        assertEquals(original.getEndTime(), copy.getDeadline());
    }

    @Test
    public void testGetCopyForStudent() {
        DataBundle typicalDataBundle = getTypicalDataBundle();
        FeedbackSessionAttributes session1InCourse1 = typicalDataBundle.feedbackSessions
                .get("session1InCourse1");

        StudentAttributes student1InCourse1 = typicalDataBundle.students.get("student1InCourse1");
        StudentAttributes student3InCourse1 = typicalDataBundle.students.get("student3InCourse1");

        FeedbackSessionAttributes sanitizedSession1InCourse1 = session1InCourse1.getCopyForStudent(
                student1InCourse1.getEmail());
        assertEquals(sanitizedSession1InCourse1.getEndTime(), sanitizedSession1InCourse1.getDeadline());
        assertEquals(student1InCourse1.getEmail(), sanitizedSession1InCourse1.getUserEmail());

        sanitizedSession1InCourse1 = session1InCourse1.getCopyForStudent(student3InCourse1.getEmail());
        assertEquals(sanitizedSession1InCourse1.getStudentDeadlines().get(student3InCourse1.getEmail()),
                sanitizedSession1InCourse1.getDeadline());
        assertEquals(student3InCourse1.getEmail(), sanitizedSession1InCourse1.getUserEmail());

        assertEquals(session1InCourse1.getEndTime(), session1InCourse1.getDeadline());
    }

    @Test
    public void testGetCopyForInstructor() {
        DataBundle typicalDataBundle = getTypicalDataBundle();
        FeedbackSessionAttributes session1InCourse1 = typicalDataBundle.feedbackSessions
                .get("session1InCourse1");

        InstructorAttributes helperOfCourse1 = typicalDataBundle.instructors.get("helperOfCourse1");
        InstructorAttributes instructor1OfCourse1 = typicalDataBundle.instructors.get("instructor1OfCourse1");

        FeedbackSessionAttributes sanitizedSession1InCourse1 = session1InCourse1.getCopyForInstructor(
                helperOfCourse1.getEmail());
        assertEquals(sanitizedSession1InCourse1.getEndTime(), sanitizedSession1InCourse1.getDeadline());
        assertEquals(helperOfCourse1.getEmail(), sanitizedSession1InCourse1.getUserEmail());

        sanitizedSession1InCourse1 = session1InCourse1.getCopyForInstructor(instructor1OfCourse1.getEmail());
        assertEquals(sanitizedSession1InCourse1.getInstructorDeadlines().get(instructor1OfCourse1.getEmail()),
                sanitizedSession1InCourse1.getDeadline());
        assertEquals(instructor1OfCourse1.getEmail(), sanitizedSession1InCourse1.getUserEmail());

        assertEquals(session1InCourse1.getEndTime(), session1InCourse1.getDeadline());
    }

    @Test
    public void testValidate() {
        ______TS("invalid parameter error messages");

        FeedbackSessionAttributes feedbackSessionAttributes = FeedbackSessionAttributes
                .builder("", "")
                .withCreatorEmail("")
                .withStartTime(Instant.now())
                .withEndTime(Instant.now())
                .withResultsVisibleFromTime(Instant.now())
                .withSessionVisibleFromTime(Instant.now())
                .withGracePeriod(Duration.ofMinutes(-100L))
                .build();

        String feedbackSessionNameError = "The field 'feedback session name' should not be empty. The value of 'feedback "
                + "session name' field should be no longer than 64 characters.";
        String courseIdError = "The field 'course ID' is empty. A course ID can contain letters, numbers, fullstops, "
                + "hyphens, underscores, and dollar signs. It cannot be longer than 64 characters, cannot be empty and "
                + "cannot contain spaces.";
        String creatorEmailError = "The field 'email' is empty. An email address contains some text followed "
                + "by one '@' sign followed by some more text, and should end with a top level domain address like .com. "
                + "It cannot be longer than 254 characters, cannot be empty"
                + " and cannot contain spaces.";
        String gracePeriodError = "Grace period should not be negative." + " "
                + "The value must be one of the options in the grace period dropdown selector.";

        assertEquals(Arrays.asList(feedbackSessionNameError, courseIdError, creatorEmailError, gracePeriodError),
                feedbackSessionAttributes.getInvalidityInfo());
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        Instant sessionVisibleTime = TimeHelper.getInstantDaysOffsetFromNow(-3);
        Instant startTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        Instant endTime = TimeHelper.getInstantDaysOffsetFromNow(-1);
        Instant resultVisibleTime = TimeHelper.getInstantDaysOffsetFromNow(1);
        Map<String, Instant> newStudentDeadlines = new HashMap<>();
        newStudentDeadlines.put("student@school.edu", endTime.plusSeconds(3600L));
        Map<String, Instant> newInstructorDeadlines = new HashMap<>();
        newInstructorDeadlines.put("instructor@university.edu", endTime.plusSeconds(7200L));
        FeedbackSessionAttributes.UpdateOptions updateOptions =
                FeedbackSessionAttributes.updateOptionsBuilder("sessionName", "courseId")
                        .withInstructions("instruction 1")
                        .withStartTime(startTime)
                        .withEndTime(endTime)
                        .withSessionVisibleFromTime(sessionVisibleTime)
                        .withResultsVisibleFromTime(resultVisibleTime)
                        .withTimeZone("Asia/Singapore")
                        .withGracePeriod(Duration.ofMinutes(5))
                        .withSentOpeningSoonEmail(true)
                        .withSentOpenedEmail(true)
                        .withSentClosingSoonEmail(true)
                        .withSentClosedEmail(true)
                        .withSentPublishedEmail(true)
                        .withIsClosingSoonEmailEnabled(true)
                        .withIsPublishedEmailEnabled(true)
                        .withStudentDeadlines(newStudentDeadlines)
                        .withInstructorDeadlines(newInstructorDeadlines)
                        .build();

        assertEquals("sessionName", updateOptions.getFeedbackSessionName());
        assertEquals("courseId", updateOptions.getCourseId());

        FeedbackSessionAttributes feedbackSessionAttributes =
                FeedbackSessionAttributes.builder("sessionName", "courseId")
                        .withCreatorEmail("i@email.com")
                        .withInstructions("instruction")
                        .withStartTime(TimeHelper.getInstantDaysOffsetFromNow(1))
                        .withEndTime(TimeHelper.getInstantDaysOffsetFromNow(2))
                        .withSessionVisibleFromTime(sessionVisibleTime.minusSeconds(60))
                        .withResultsVisibleFromTime(Instant.now().minusSeconds(60))
                        .withTimeZone("UTC")
                        .withGracePeriod(Duration.ofMinutes(20))
                        .withIsClosingSoonEmailEnabled(false)
                        .withIsPublishedEmailEnabled(false)
                        .build();

        feedbackSessionAttributes.update(updateOptions);

        assertEquals("instruction 1", feedbackSessionAttributes.getInstructions());
        assertEquals(startTime, feedbackSessionAttributes.getStartTime());
        assertEquals(endTime, feedbackSessionAttributes.getEndTime());
        assertEquals(sessionVisibleTime, feedbackSessionAttributes.getSessionVisibleFromTime());
        assertEquals(resultVisibleTime, feedbackSessionAttributes.getResultsVisibleFromTime());
        assertEquals("Asia/Singapore", feedbackSessionAttributes.getTimeZone());
        assertEquals(5, feedbackSessionAttributes.getGracePeriodMinutes());
        assertTrue(feedbackSessionAttributes.isSentOpeningSoonEmail());
        assertTrue(feedbackSessionAttributes.isSentOpenedEmail());
        assertTrue(feedbackSessionAttributes.isSentClosingSoonEmail());
        assertTrue(feedbackSessionAttributes.isSentClosedEmail());
        assertTrue(feedbackSessionAttributes.isSentPublishedEmail());
        assertTrue(feedbackSessionAttributes.isOpenedEmailEnabled());
        assertTrue(feedbackSessionAttributes.isClosingSoonEmailEnabled());
        assertTrue(feedbackSessionAttributes.isPublishedEmailEnabled());
        assertEquals(newStudentDeadlines, feedbackSessionAttributes.getStudentDeadlines());
        assertEquals(newInstructorDeadlines, feedbackSessionAttributes.getInstructorDeadlines());

        // build update option based on existing update option
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
                        .withStudentDeadlines(null));
        assertThrows(AssertionError.class, () ->
                FeedbackSessionAttributes.updateOptionsBuilder("session", "courseId")
                        .withInstructorDeadlines(null));
    }

    @Test
    public void testEquals() {
        FeedbackSessionAttributes feedbackSession = generateTypicalFeedbackSessionAttributesObject();

        // When the two feedback sessions are exact copies
        FeedbackSessionAttributes feedbackSessionCopy = feedbackSession.getCopy();

        assertTrue(feedbackSession.equals(feedbackSessionCopy));

        // When the two feedback sessions have same values but created at different time
        FeedbackSessionAttributes feedbackSessionSimilar = generateTypicalFeedbackSessionAttributesObject();

        assertTrue(feedbackSession.equals(feedbackSessionSimilar));

        // When the two feedback sessions are different
        FeedbackSessionAttributes feedbackSessionDifferent =
                FeedbackSessionAttributes.builder("differentSession", "courseId")
                .withCreatorEmail("email@email.com")
                .withInstructions("instructor")
                .build();

        assertFalse(feedbackSession.equals(feedbackSessionDifferent));

        // When the other object is of different class
        assertFalse(feedbackSession.equals(3));
    }

    @Test
    public void testHashCode() {
        FeedbackSessionAttributes feedbackSession = generateTypicalFeedbackSessionAttributesObject();

        // When the two feedback sessions are exact copies, they should have the same hash code
        FeedbackSessionAttributes feedbackSessionCopy = feedbackSession.getCopy();

        assertTrue(feedbackSession.hashCode() == feedbackSessionCopy.hashCode());

        // When the two feedback sessions have same values but created at different time,
        // they should still have the same hash code
        FeedbackSessionAttributes feedbackSessionSimilar = generateTypicalFeedbackSessionAttributesObject();

        assertTrue(feedbackSession.hashCode() == feedbackSessionSimilar.hashCode());

        // When the two feedback sessions are different, they should have different hash code
        FeedbackSessionAttributes feedbackSessionDifferent =
                FeedbackSessionAttributes.builder("differentSession", "courseId")
                .withCreatorEmail("email@email.com")
                .withInstructions("instructor")
                .build();

        assertFalse(feedbackSession.hashCode() == feedbackSessionDifferent.hashCode());
    }

    private FeedbackSessionAttributes generateTypicalFeedbackSessionAttributesObject() {
        String timeZone = "Asia/Singapore";
        Instant startTime = TimeHelper.parseInstant("2016-05-09T10:00:00+08:00");
        Instant endTime = TimeHelper.parseInstant("2017-05-09T10:00:00+08:00");

        return FeedbackSessionAttributes
                .builder("sessionName", "courseId")
                .withCreatorEmail("email@email.com")
                .withInstructions("instructor")
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withSessionVisibleFromTime(startTime.minusSeconds(60))
                .withResultsVisibleFromTime(endTime.plusSeconds(60))
                .withTimeZone(timeZone)
                .withGracePeriod(Duration.ofMinutes(15))
                .withIsClosingSoonEmailEnabled(false)
                .withIsPublishedEmailEnabled(false)
                .build();
    }

}
