package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.FieldValidator;
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
        startTime = TimeHelper.combineDateTime("09/05/2016", "1000");
        endTime = TimeHelper.combineDateTime("09/05/2017", "1000");

        fsa = FeedbackSessionAttributes
                .builder(null, null, null)
                .withStartTime(startTime).withEndTime(endTime).withTimeZone(8).withGracePeriod(15)
                .withFeedbackSessionType(FeedbackSessionType.STANDARD).withOpeningEmailEnabled(false)
                .withClosingEmailEnabled(false).withPublishedEmailEnabled(false)
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
        assertNotNull(fsa.getRespondingInstructorList());
        assertNotNull(fsa.getRespondingStudentList());
    }

    @Test
    public void testBuilderWithNullValues() {
        FeedbackSessionAttributes fsa = FeedbackSessionAttributes
                .builder(null, null, null)
                .withInstructions(null)
                .withCreatedTime(null)
                .withStartTime(null)
                .withEndTime(null)
                .withSessionVisibleFromTime(null)
                .withResultsVisibleFromTime(null)
                .withRespondingStudentList(null)
                .withRespondingInstructorList(null)
                .build();

        // Not null fields
        assertNotNull(fsa.getRespondingInstructorList());
        assertNotNull(fsa.getRespondingStudentList());

        // Nullable fields
        assertNull(fsa.getFeedbackSessionName());
        assertNull(fsa.getCourseId());
        assertNull(fsa.getCreatorEmail());
        assertNull(fsa.getInstructions());
        assertNull(fsa.getStartTime());
        assertNull(fsa.getEndTime());
        assertNull(fsa.getCreatedTime());
        assertNull(fsa.getSessionVisibleFromTime());
        assertNull(fsa.getResultsVisibleFromTime());
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

        ______TS("null parameter error messages");

        List<String> expectedErrorMessage = new ArrayList<>();
        String[] fieldNames = new String[]{
                "feedback session name",
                "course ID",
                "instructions to students",
                "time for the session to become visible",
                "creator's email",
                "session creation time"};
        for (String fieldName : fieldNames) {
            expectedErrorMessage.add(FieldValidator.NON_NULL_FIELD_ERROR_MESSAGE.replace("${fieldName}", fieldName));
        }

        //expect all the error messages to be appended together.
        assertEquals(fsa.getInvalidityInfo(), expectedErrorMessage);

        ______TS("invalid parameters error messages");

    }

}
