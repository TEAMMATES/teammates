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
    public void testDuplicate() {
        FeedbackResponseAttributes fra1 = new FeedbackResponseAttributes(
                "Session1", "CS3281",
                "questionId", FeedbackQuestionType.TEXT,
                "giver@email.com", "giverSection",
                "recipient@email.com", "recipientSection",
                "My original answer");
        FeedbackResponseAttributes fra2 = new FeedbackResponseAttributes(fra1);

        FeedbackResponseDetails injectDetail = new FeedbackTextResponseDetails("My second answer");

        fra2.responseDetails = injectDetail;
        assertEquals(fra1.responseDetails.getAnswerString(), "My original answer");
        assertEquals(fra2.responseDetails.getAnswerString(), "My second answer");

        ______TS("success : Copy of FeedbackResponseAttributes is deep copy");
    }

}
