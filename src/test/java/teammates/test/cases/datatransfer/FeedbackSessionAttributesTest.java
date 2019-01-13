package teammates.test.cases.datatransfer;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

}
