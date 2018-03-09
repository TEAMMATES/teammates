package teammates.test.cases.datatransfer;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.TimeHelperExtension;

/**
 * SUT: {@link FeedbackSessionAttributes}.
 */
public class FeedbackSessionAttributesTest extends BaseTestCase {
    private Date startTime;
    private Date endTime;

    private FeedbackSessionAttributes fsa;

    @BeforeClass
    public void classSetup() {
        startTime = TimeHelper.combineDateTime("Mon, 09 May, 2016", "1000");
        endTime = TimeHelper.combineDateTime("Tue, 09 May, 2017", "1000");

        fsa = FeedbackSessionAttributes
                .builder("", "", "")
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withTimeZone(8)
                .withGracePeriod(15)
                .withFeedbackSessionType(FeedbackSessionType.STANDARD)
                .withOpeningEmailEnabled(false)
                .withClosingEmailEnabled(false)
                .withPublishedEmailEnabled(false)
                .build();
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
        assertEquals(new Text(""), fsa.getInstructions());
        assertNotNull(fsa.getRespondingInstructorList());
        assertNotNull(fsa.getRespondingStudentList());
    }

    @Test
    public void testBuilderCopy() {
        FeedbackSessionAttributes original = FeedbackSessionAttributes
                .builder("newFeedbackSessionName", "course", "email")
                .withInstructions(new Text("default instructions"))
                .withCreatedTime(TimeHelperExtension.getHoursOffsetToCurrentTime(0))
                .withStartTime(TimeHelperExtension.getHoursOffsetToCurrentTime(2))
                .withEndTime(TimeHelperExtension.getHoursOffsetToCurrentTime(5))
                .withSessionVisibleFromTime(TimeHelperExtension.getHoursOffsetToCurrentTime(1))
                .withResultsVisibleFromTime(TimeHelperExtension.getHoursOffsetToCurrentTime(6))
                .withTimeZone(8).withGracePeriod(0).withFeedbackSessionType(FeedbackSessionType.PRIVATE)
                .withOpeningEmailEnabled(false).withClosingEmailEnabled(false).withPublishedEmailEnabled(false)
                .build();

        FeedbackSessionAttributes copy = original.getCopy();

        assertEquals(original.getFeedbackSessionName(), copy.getFeedbackSessionName());
        assertEquals(original.getCourseId(), copy.getCourseId());
        assertEquals(original.getCreatorEmail(), copy.getCreatorEmail());
        assertEquals(original.getInstructions(), copy.getInstructions());
        assertEquals(original.getCreatedTime(), copy.getCreatedTime());
        assertEquals(original.getStartTime(), copy.getStartTime());
        assertEquals(original.getEndTime(), copy.getEndTime());
        assertEquals(original.getSessionVisibleFromTime(), copy.getSessionVisibleFromTime());
        assertEquals(original.getResultsVisibleFromTime(), copy.getResultsVisibleFromTime());
        assertEquals(original.getTimeZone(), copy.getTimeZone());
        assertEquals(original.getGracePeriod(), copy.getGracePeriod());
        assertEquals(original.getFeedbackSessionType(), copy.getFeedbackSessionType());
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

        assertEquals(fsa.getSessionEndTime(), endTime);
        assertEquals(fsa.getSessionStartTime(), startTime);

    }

    @Test
    public void testValidate() {
        ______TS("invalid parameter error messages");

        FeedbackSessionAttributes feedbackSessionAttributes = FeedbackSessionAttributes
                .builder("", "", "")
                .withStartTime(new Date())
                .withEndTime(new Date())
                .withCreatedTime(new Date())
                .withResultsVisibleFromTime(new Date())
                .withSessionVisibleFromTime(new Date())
                .build();
        assertEquals(feedbackSessionAttributes.getInvalidityInfo(), buildExpectedErrorMessages());
    }

    private List<String> buildExpectedErrorMessages() {
        String feedbackSessionNameError = "The field 'feedback session name' is empty. The value of a/an feedback "
                + "session name should be no longer than 38 characters. It should not be empty.";
        String courseIdError = "The field 'course ID' is empty. A course ID can contain letters, numbers, fullstops, "
                + "hyphens, underscores, and dollar signs. It cannot be longer than 40 characters, cannot be empty and "
                + "cannot contain spaces.";
        String creatorEmailError = "The field 'email' is empty. An email address contains some text followed "
                + "by one '@' sign followed by some more text. It cannot be longer than 254 characters, cannot be empty"
                + " and cannot contain spaces.";

        return Arrays.asList(feedbackSessionNameError, courseIdError, creatorEmailError);
    }

}
