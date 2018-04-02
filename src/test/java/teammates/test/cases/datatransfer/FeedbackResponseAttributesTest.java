package teammates.test.cases.datatransfer;

import java.time.Instant;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackResponse;
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
    public void testBuilderWithDefaultValues() {
        FeedbackResponseAttributes observedFeedbackResponseAttributes =
                FeedbackResponseAttributes.builder().build();

        assertNull(observedFeedbackResponseAttributes.getId());
        assertNull(observedFeedbackResponseAttributes.feedbackSessionName);
        assertNull(observedFeedbackResponseAttributes.courseId);
        assertNull(observedFeedbackResponseAttributes.feedbackQuestionId);
        assertNull(observedFeedbackResponseAttributes.feedbackQuestionType);
        assertNull(observedFeedbackResponseAttributes.giver);
        assertNull(observedFeedbackResponseAttributes.recipient);
        assertNull(observedFeedbackResponseAttributes.giverSection);
        assertNull(observedFeedbackResponseAttributes.recipientSection);
        assertNull(observedFeedbackResponseAttributes.getResponseDetails());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, observedFeedbackResponseAttributes.getCreatedAt());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, observedFeedbackResponseAttributes.getUpdatedAt());
    }

    @Test
    public void testBuilderWithPopulatedFieldValues() {
        String expectedSessionName = "dummyName";
        String expectedCourseId = "dummyCourseId";
        String expectedQuestionId = "dummyQuestionId";
        FeedbackQuestionType expectedQuestionType = FeedbackQuestionType.TEXT;
        String expectedGiver = "dummyGiver";
        String expectedRecipient = "dummyRecipient";
        String expectedGiverSection = Const.DEFAULT_SECTION;
        String expectedRecipientSection = Const.DEFAULT_SECTION;
        Text expectedResponseMetaData = new Text("dummy meta data");
        String expectedId = expectedQuestionId + "%" + expectedGiver + "%" + expectedRecipient;

        FeedbackResponseAttributes observedFeedbackResponseAttributes = FeedbackResponseAttributes.builder()
                .withFeedbackResponseId(expectedId)
                .withFeedbackSessionName(expectedSessionName)
                .withCourseId(expectedCourseId)
                .withFeedbackQuestionId(expectedQuestionId)
                .withFeedbackQuestionType(expectedQuestionType)
                .withGiver(expectedGiver)
                .withGiverSection(expectedGiverSection)
                .withRecipient(expectedRecipient)
                .withRecipientSection(expectedRecipientSection)
                .withResponseMetaData(expectedResponseMetaData)
                .build();

        assertEquals(expectedId, observedFeedbackResponseAttributes.getId());
        assertEquals(expectedSessionName, observedFeedbackResponseAttributes.feedbackSessionName);
        assertEquals(expectedCourseId, observedFeedbackResponseAttributes.courseId);
        assertEquals(expectedQuestionId, observedFeedbackResponseAttributes.feedbackQuestionId);
        assertEquals(expectedQuestionType, observedFeedbackResponseAttributes.feedbackQuestionType);
        assertEquals(expectedGiver, observedFeedbackResponseAttributes.giver);
        assertEquals(expectedRecipient, observedFeedbackResponseAttributes.recipient);
        assertEquals(expectedGiverSection, observedFeedbackResponseAttributes.giverSection);
        assertEquals(expectedRecipientSection, observedFeedbackResponseAttributes.recipientSection);
        assertEquals(expectedResponseMetaData, observedFeedbackResponseAttributes.responseMetaData);
    }

    @Test
    public void testBuilderCopy() {
        String originalGiver = "giver";
        String originalRecipient = "recipient";
        String originalQuestionId = "someQuestionId";
        String originalId = originalQuestionId + "%" + originalGiver + "%" + originalRecipient;
        FeedbackResponseAttributes original = FeedbackResponseAttributes.builder()
                .withFeedbackResponseId(originalId)
                .withFeedbackResponseId("originalFeedbackQuestionId%originalGiver%")
                .withFeedbackSessionName("originalName")
                .withCourseId("originalCourseId")
                .withFeedbackQuestionId(originalQuestionId)
                .withFeedbackQuestionType(FeedbackQuestionType.TEXT)
                .withGiver(originalGiver)
                .withRecipient(originalRecipient)
                .withResponseMetaData(new Text("original meta data"))
                .build();

        FeedbackResponseAttributes copy = original.getCopy();

        assertNotSame(copy, original);
        assertEquals(copy.getId(), original.getId());
        assertEquals(copy.feedbackSessionName, original.feedbackSessionName);
        assertEquals(copy.courseId, original.courseId);
        assertEquals(copy.feedbackQuestionId, original.feedbackQuestionId);
        assertEquals(copy.feedbackQuestionType, original.feedbackQuestionType);
        assertEquals(copy.giver, original.giver);
        assertEquals(copy.recipient, original.recipient);
        assertEquals(copy.giverSection, original.giverSection);
        assertEquals(copy.recipientSection, original.recipientSection);
        assertEquals(copy.responseMetaData, original.responseMetaData);
    }

    @Test
    public void testValueOf() {
        FeedbackResponse genericResponse = new FeedbackResponse("genericName", "genericCourseId",
                "genericFeedbackQuestionId", FeedbackQuestionType.TEXT, "genericGiver",
                Const.DEFAULT_SECTION, "genericRecipient", Const.DEFAULT_SECTION, new Text("genericAnswer"));

        FeedbackResponseAttributes observedResponse = FeedbackResponseAttributes.valueOf(genericResponse);

        assertEquals(genericResponse.getId(), observedResponse.getId());
        assertEquals(genericResponse.getFeedbackSessionName(), observedResponse.feedbackSessionName);
        assertEquals(genericResponse.getCourseId(), observedResponse.courseId);
        assertEquals(genericResponse.getFeedbackQuestionId(), observedResponse.feedbackQuestionId);
        assertEquals(genericResponse.getFeedbackQuestionType(), observedResponse.feedbackQuestionType);
        assertEquals(genericResponse.getGiverEmail(), observedResponse.giver);
        assertEquals(genericResponse.getRecipientEmail(), observedResponse.recipient);
        assertEquals(genericResponse.getGiverSection(), observedResponse.giverSection);
        assertEquals(genericResponse.getRecipientSection(), observedResponse.recipientSection);
        assertEquals(genericResponse.getResponseMetaData(), observedResponse.responseMetaData);
    }

}
