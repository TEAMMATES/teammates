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
    public void testConstructorWithCopy_shouldDoDeepCopyOfResponseDetails() {
        FeedbackResponseAttributes fra1 = new FeedbackResponseAttributes(
                "Session1", "CS3281",
                "questionId", "giver@email.com", "giverSection",
                "recipient@email.com", "recipientSection",
                new FeedbackTextResponseDetails("My original answer"));
        FeedbackResponseAttributes fra2 = new FeedbackResponseAttributes(fra1);

        ((FeedbackTextResponseDetails) fra2.responseDetails).answer = "My second answer";
        assertEquals(fra1.responseDetails.getAnswerString(), "My original answer");
        assertEquals(fra2.responseDetails.getAnswerString(), "My second answer");

    }

    @Test
    public void testConstructorWithAllAttributes_shouldDoDeepCopyOfResponseDetails() {
        FeedbackTextResponseDetails detail = new FeedbackTextResponseDetails("My original answer");
        FeedbackResponseAttributes fra = new FeedbackResponseAttributes(
                "Session1", "CS3281",
                "questionId", "giver@email.com", "giverSection",
                "recipient@email.com", "recipientSection", detail);

        detail.answer = "Updated answer";

        assertEquals("My original answer", fra.responseDetails.getAnswerString());
        assertEquals("Updated answer", detail.answer);
    }

    @Test
    public void testGetResponseDetails_shouldDoDeepCopy() {
        FeedbackResponseAttributes fra = new FeedbackResponseAttributes(
                "Session1", "CS3281",
                "questionId", "giver@email.com", "giverSection",
                "recipient@email.com", "recipientSection",
                new FeedbackTextResponseDetails("My original answer"));
        FeedbackResponseDetails frdDeep = fra.getResponseDetails();

        ((FeedbackTextResponseDetails) fra.responseDetails).answer = "My second answer";
        assertEquals(frdDeep.getAnswerString(), "My original answer");
    }

    @Test
    public void testSetResponseDetails_shouldDoDeepCopy() {
        FeedbackResponseAttributes fra = new FeedbackResponseAttributes(
                "Session1", "CS3281",
                "questionId", "giver@email.com", "giverSection",
                "recipient@email.com", "recipientSection",
                new FeedbackTextResponseDetails("My original answer"));
        FeedbackTextResponseDetails updatedDetails = new FeedbackTextResponseDetails("Updated answer");
        fra.setResponseDetails(updatedDetails);
        updatedDetails.answer = "Modified deep copy answer";

        assertEquals(updatedDetails.getAnswerString(), "Modified deep copy answer");
        assertEquals(fra.responseDetails.getAnswerString(), "Updated answer");

    }

    @Test
    public void testGetBackUpIdentifier() {
        FeedbackResponseAttributes responseAttributes = new FeedbackResponseAttributes();
        responseAttributes.setId("Valid-Response-id");

        String expectedBackUpIdentifierMessage = "Recently modified feedback response::" + responseAttributes.getId();
        assertEquals(expectedBackUpIdentifierMessage, responseAttributes.getBackupIdentifier());
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        FeedbackResponseAttributes.UpdateOptions updateOptions =
                FeedbackResponseAttributes.updateOptionsBuilder("responseId")
                        .withGiver("giver1")
                        .withGiverSection("section1")
                        .withRecipient("recipient1")
                        .withRecipientSection("section2")
                        .withResponseDetails(new FeedbackTextResponseDetails("Test 1"))
                        .build();

        assertEquals("responseId", updateOptions.getFeedbackResponseId());

        FeedbackResponseAttributes feedbackResponseAttributes =
                new FeedbackResponseAttributes("session", "course", "questionId",
                        "giver2", "section3", "recipient2", "section4", new FeedbackTextResponseDetails("Test 2"));

        feedbackResponseAttributes.update(updateOptions);

        assertEquals("session", feedbackResponseAttributes.feedbackSessionName);
        assertEquals("course", feedbackResponseAttributes.courseId);
        assertEquals("questionId", feedbackResponseAttributes.feedbackQuestionId);
        assertEquals(FeedbackQuestionType.TEXT, feedbackResponseAttributes.getFeedbackQuestionType());
        assertEquals("giver1", feedbackResponseAttributes.giver);
        assertEquals("section1", feedbackResponseAttributes.giverSection);
        assertEquals("recipient1", feedbackResponseAttributes.recipient);
        assertEquals("section2", feedbackResponseAttributes.recipientSection);
        assertEquals("Test 1", feedbackResponseAttributes.getResponseDetails().getAnswerString());
    }

    @Test
    public void testUpdateOptionsBuilder_withNullInput_shouldFailWithAssertionError() {
        assertThrows(AssertionError.class, () ->
                FeedbackResponseAttributes.updateOptionsBuilder(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseAttributes.updateOptionsBuilder("id")
                        .withGiver(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseAttributes.updateOptionsBuilder("id")
                        .withGiverSection(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseAttributes.updateOptionsBuilder("id")
                        .withRecipient(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseAttributes.updateOptionsBuilder("id")
                        .withRecipientSection(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseAttributes.updateOptionsBuilder("id")
                        .withResponseDetails(null));
    }

    private static class FeedbackResponseAttributesWithModifiableTimestamp extends FeedbackResponseAttributes {

        void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }

        void setUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
        }

    }
}
