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
    public void timestamp_testDefaultTimestamp() {
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

    /*
     * Builder values are not enforced, defaulted to {@code null}
     * and {@code TIME_REPRESENTS_DEFAULT_TIMESTAMP} for timestamps
     * and are not mutable.
     */
    @Test
    public void builder_testBuilderWithDefaultValues_nullValues() {
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

    /*
     * Not all possible states are tested, but there is an implicit invariant
     * that all values must be non-{@code null} that was not imbued in the builder.
     */
    @Test
    public void builder_testBuilderWithPopulatedFieldValues_valuePropagated() {
        String expectedSessionName = "dummyName";
        String expectedCourseId = "dummyCourseId";
        String expectedQuestionId = "dummyQuestionId";
        FeedbackQuestionType expectedQuestionType = FeedbackQuestionType.TEXT;
        String expectedGiver = "dummyGiver";
        String expectedRecipient = "dummyRecipient";
        String expectedGiverSection = Const.DEFAULT_SECTION;
        String expectedRecipientSection = Const.DEFAULT_SECTION;
        Text expectedResponseMetaData = new Text("dummy meta data");
        String expectedId = "dummyId";
        Instant expectedCreatedAt = Instant.now();
        Instant expectedUpdatedAt = Instant.now();

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
                .withCreatedAt(expectedCreatedAt)
                .withUpdatedAt(expectedUpdatedAt)
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
        assertEquals(expectedCreatedAt, observedFeedbackResponseAttributes.getCreatedAt());
        assertEquals(expectedUpdatedAt, observedFeedbackResponseAttributes.getUpdatedAt());
    }

    @Test
    public void builder_testBuilderCopy_valuesCopied() {
        String originalGiver = "giver";
        String originalRecipient = "recipient";
        String originalQuestionId = "someQuestionId";
        FeedbackResponseAttributes original = FeedbackResponseAttributes.builder()
                .withFeedbackResponseId("originalId")
                .withFeedbackSessionName("originalName")
                .withCourseId("originalCourseId")
                .withFeedbackQuestionId(originalQuestionId)
                .withFeedbackQuestionType(FeedbackQuestionType.TEXT)
                .withGiver(originalGiver)
                .withRecipient(originalRecipient)
                .withResponseMetaData(new Text("original meta data"))
                .withCreatedAt(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP)
                .withUpdatedAt(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP)
                .build();

        FeedbackResponseAttributes copy = original.getCopy();

        assertNotSame(copy, original);
        assertSame(copy.getId(), original.getId());
        assertSame(copy.feedbackSessionName, original.feedbackSessionName);
        assertSame(copy.courseId, original.courseId);
        assertSame(copy.feedbackQuestionId, original.feedbackQuestionId);
        assertSame(copy.feedbackQuestionType, original.feedbackQuestionType);
        assertSame(copy.giver, original.giver);
        assertSame(copy.recipient, original.recipient);
        assertSame(copy.giverSection, original.giverSection);
        assertSame(copy.recipientSection, original.recipientSection);
        assertSame(copy.responseMetaData, original.responseMetaData);
        assertSame(copy.getCreatedAt(), original.getCreatedAt());
        assertSame(copy.getUpdatedAt(), original.getUpdatedAt());
    }

    @Test
    public void builder_testValueOf_valuesTransferred() {
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
        assertEquals(genericResponse.getCreatedAt(), observedResponse.getCreatedAt());
        assertEquals(genericResponse.getUpdatedAt(), observedResponse.getUpdatedAt());
    }

}
