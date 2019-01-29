package teammates.test.cases.datatransfer;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackResponseAttributes}.
 */
public class FeedbackResponseAttributesTest extends BaseTestCase {

    private static class FeedbackResponseAttributesWithModifiableTimestamp extends FeedbackResponseAttributes {

        void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }

        void setUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
        }

    }

    @Test
    public void testDefaultTimestamp() {
        FeedbackResponseAttributesWithModifiableTimestamp fra =
                new FeedbackResponseAttributesWithModifiableTimestamp();

        fra.setCreatedAt(null);
        fra.setUpdatedAt(null);

        Instant defaultTimeStamp = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;

        ______TS("success : defaultTimeStamp for createdAt date");

        assertEquals(defaultTimeStamp, fra.getCreatedAt());

        ______TS("success : defaultTimeStamp for updatedAt date");

        assertEquals(defaultTimeStamp, fra.getUpdatedAt());
    }

    @Test
    public void testShouldDoDeepCopy() {
        FeedbackResponseAttributes fra1 = new FeedbackResponseAttributes(
                "Session1", "CS3281",
                "questionId", FeedbackQuestionType.TEXT,
                "giver@email.com", "giverSection",
                "recipient@email.com", "recipientSection",
                new FeedbackTextResponseDetails("My original answer"));
        FeedbackResponseAttributes fra2 = new FeedbackResponseAttributes(fra1);

        ((FeedbackTextResponseDetails) fra2.responseDetails).answer = "My second answer";
        assertEquals(fra1.responseDetails.getAnswerString(), "My original answer");
        assertEquals(fra2.responseDetails.getAnswerString(), "My second answer");

    }

    @Test
    public void testGetDeepCopyResponseDetails() {
        FeedbackResponseAttributes fra = new FeedbackResponseAttributes(
                "Session1", "CS3281",
                "questionId", FeedbackQuestionType.TEXT,
                "giver@email.com", "giverSection",
                "recipient@email.com", "recipientSection",
                new FeedbackTextResponseDetails("My original answer"));
        FeedbackResponseDetails frdDeep = fra.getResponseDetails();

        ((FeedbackTextResponseDetails) fra.responseDetails).answer = "My second answer";
        assertEquals(frdDeep.getAnswerString(), "My original answer");
    }

    @Test
    public void testSettingDeepCopyResponseDetails() {
        FeedbackResponseAttributes fra = new FeedbackResponseAttributes(
                "Session1", "CS3281",
                "questionId", FeedbackQuestionType.TEXT,
                "giver@email.com", "giverSection",
                "recipient@email.com", "recipientSection",
                new FeedbackTextResponseDetails("My original answer"));
        FeedbackTextResponseDetails updatedDetails = new FeedbackTextResponseDetails("Updated answer");
        fra.setResponseDetails(updatedDetails);
        updatedDetails.answer = "Modified deep copy answer";

        assertEquals(updatedDetails.getAnswerString(), "Modified deep copy answer");
        assertEquals(fra.responseDetails.getAnswerString(), "Updated answer");

        ______TS("success: setting a deep copy of responseDetails");
    }
}
