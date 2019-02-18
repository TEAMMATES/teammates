package teammates.test.cases.datatransfer;

import java.time.Instant;
import java.util.ArrayList;

import org.testng.annotations.Test;
import org.testng.collections.Lists;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes}.
 */
public class FeedbackResponseCommentAttributesTest extends BaseTestCase {

    @Test
    public void testBuilderWithDefaultValues() {
        FeedbackResponseCommentAttributes feedbackAttributes = FeedbackResponseCommentAttributes
                .builder("course", "name", "email", "")
                .build();

        // Default values for following fields
        assertEquals(feedbackAttributes.giverSection, "None");
        assertEquals(feedbackAttributes.receiverSection, "None");
        assertEquals(feedbackAttributes.showCommentTo, new ArrayList<>());
        assertEquals(feedbackAttributes.showGiverNameTo, new ArrayList<>());
        assertTrue(feedbackAttributes.isVisibilityFollowingFeedbackQuestion);
    }

    @Test
    public void testBuilderWithNullValues() {
        FeedbackResponseCommentAttributes feedbackAttributes = FeedbackResponseCommentAttributes
                .builder("course", "name", "email", "")
                .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withFeedbackResponseId(null)
                .withFeedbackQuestionId(null)
                .withShowGiverNameTo(null)
                .withShowCommentTo(null)
                .withLastEditorEmail(null)
                .withReceiverSection(null)
                .withGiverSection(null)
                .withCreatedAt(Instant.now())
                .withLastEditedAt(null)
                .withFeedbackResponseCommentId(null)
                .withVisibilityFollowingFeedbackQuestion(null)
                .withCommentFromFeedbackParticipant(false)
                .build();

        // Default values for following fields
        assertEquals(feedbackAttributes.giverSection, "None");
        assertEquals(feedbackAttributes.receiverSection, "None");
        assertEquals(feedbackAttributes.lastEditorEmail, feedbackAttributes.commentGiver);
        assertEquals(feedbackAttributes.lastEditedAt, feedbackAttributes.createdAt);
        assertTrue(feedbackAttributes.isVisibilityFollowingFeedbackQuestion);
    }

    @Test
    public void testValueOf() {
        FeedbackResponseComment responseComment = new FeedbackResponseComment("course", "name",
                "question", "giver", FeedbackParticipantType.STUDENTS, null, Instant.now(),
                "comment", "giverSection", "receiverSection",
                null, null, null, null, false, false);

        FeedbackResponseCommentAttributes feedbackAttributes =
                FeedbackResponseCommentAttributes.valueOf(responseComment);

        assertEquals(responseComment, feedbackAttributes);
    }

    private void assertEquals(FeedbackResponseComment responseComment,
                              FeedbackResponseCommentAttributes feedbackAttributes) {
        assertEquals(responseComment.getCourseId(), feedbackAttributes.courseId);
        assertEquals(responseComment.getFeedbackSessionName(), feedbackAttributes.feedbackSessionName);
        assertEquals(responseComment.getFeedbackQuestionId(), feedbackAttributes.feedbackQuestionId);
        assertEquals(responseComment.getGiverEmail(), feedbackAttributes.commentGiver);
        assertEquals(responseComment.getFeedbackResponseId(), feedbackAttributes.feedbackResponseId);
        assertEquals(responseComment.getShowCommentTo(), feedbackAttributes.showCommentTo);
        assertEquals(responseComment.getShowGiverNameTo(), feedbackAttributes.showGiverNameTo);
        assertEquals(responseComment.getCreatedAt(), feedbackAttributes.createdAt);
        assertEquals(responseComment.getCommentText(), feedbackAttributes.commentText);
        assertEquals(responseComment.getLastEditorEmail(), feedbackAttributes.lastEditorEmail);
        assertEquals(responseComment.getLastEditedAt(), feedbackAttributes.lastEditedAt);
        assertEquals(responseComment.getGiverSection(), feedbackAttributes.giverSection);
        assertEquals(responseComment.getReceiverSection(), feedbackAttributes.receiverSection);
        assertEquals(responseComment.getFeedbackResponseCommentId(), feedbackAttributes.feedbackResponseCommentId);

        if (responseComment.getIsVisibilityFollowingFeedbackQuestion() == null) {
            assertTrue(feedbackAttributes.isVisibilityFollowingFeedbackQuestion);
        } else {
            assertEquals(responseComment.getIsVisibilityFollowingFeedbackQuestion().booleanValue(),
                    feedbackAttributes.isVisibilityFollowingFeedbackQuestion);
        }
    }

    @Test
    public void testConvertCommentTextToStringForCsv() {
        String text = "aaa , bb\"b, c\"\"cc <image src=\"http://test.com/test.png\"></image> hello";
        FeedbackResponseCommentAttributes feedbackAttributes = FeedbackResponseCommentAttributes
                .builder("course", "name", "email", text)
                .build();
        String commentText = feedbackAttributes.getCommentAsCsvString();
        assertEquals("\"aaa , bb\"\"b, c\"\"\"\"cc hello Images Link: http://test.com/test.png \"", commentText);
    }

    @Test
    public void testConvertCommentTextToStringForHtml() {
        String text = "<script>alert('injected');</script> <image src=\"http://test.com/test.png\"></image> hello";
        FeedbackResponseCommentAttributes feedbackAttributes = FeedbackResponseCommentAttributes
                .builder("course", "name", "email", text)
                .build();
        String commentText = feedbackAttributes.getCommentAsHtmlString();
        assertEquals("hello Images Link: http:&#x2f;&#x2f;test.com&#x2f;test.png ", commentText);
    }

    @Test
    public void testGetBackUpIdentifier() {
        FeedbackResponseCommentAttributes commentAttributes = FeedbackResponseCommentAttributes
                .builder("course", "name", "email", "valid")
                .build();

        String expectedBackUpIdentifierMessage = "Recently modified feedback response comment::"
                + commentAttributes.getId();
        assertEquals(expectedBackUpIdentifierMessage, commentAttributes.getBackupIdentifier());
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        Instant lastEditorAt = Instant.now();
        FeedbackResponseCommentAttributes.UpdateOptions updateOptions =
                FeedbackResponseCommentAttributes.updateOptionsBuilder(123L)
                        .withFeedbackResponseId("responseId1")
                        .withCommentText("commentText1")
                        .withShowCommentTo(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS))
                        .withShowGiverNameTo(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS))
                        .withLastEditorEmail("editor1@email.com")
                        .withLastEditorAt(lastEditorAt)
                        .withGiverSection("section1")
                        .withReceiverSection("section1")
                        .build();

        assertEquals(123L, updateOptions.getFeedbackResponseCommentId());

        Instant createdAt = TimeHelper.getInstantDaysOffsetFromNow(-1);
        FeedbackResponseCommentAttributes feedbackResponseCommentAttributes =
                FeedbackResponseCommentAttributes.builder("courseId", "sessionName",
                        "giver@email.com", "testComment")
                        .withFeedbackResponseId("responseId")
                        .withFeedbackQuestionId("questionId")
                        .withFeedbackResponseCommentId(123L)
                        .withCreatedAt(createdAt)
                        .withGiverSection("testSection")
                        .withReceiverSection("testSection")
                        .withCommentGiverType(FeedbackParticipantType.STUDENTS)
                        .withLastEditorEmail("editor2@email.com")
                        .withLastEditedAt(lastEditorAt.minusSeconds(60))
                        .withVisibilityFollowingFeedbackQuestion(true)
                        .withShowCommentTo(new ArrayList<>())
                        .withShowGiverNameTo(new ArrayList<>())
                        .withCommentFromFeedbackParticipant(true)
                        .build();

        feedbackResponseCommentAttributes.update(updateOptions);

        assertEquals("courseId", feedbackResponseCommentAttributes.courseId);
        assertEquals("sessionName", feedbackResponseCommentAttributes.feedbackSessionName);
        assertEquals("giver@email.com", feedbackResponseCommentAttributes.commentGiver);
        assertEquals("commentText1", feedbackResponseCommentAttributes.commentText);
        assertEquals("responseId1", feedbackResponseCommentAttributes.feedbackResponseId);
        assertEquals("questionId", feedbackResponseCommentAttributes.feedbackQuestionId);
        assertEquals(createdAt, feedbackResponseCommentAttributes.createdAt);
        assertEquals("section1", feedbackResponseCommentAttributes.giverSection);
        assertEquals("section1", feedbackResponseCommentAttributes.receiverSection);
        assertEquals(FeedbackParticipantType.STUDENTS, feedbackResponseCommentAttributes.commentGiverType);
        assertEquals("editor1@email.com", feedbackResponseCommentAttributes.lastEditorEmail);
        assertEquals(lastEditorAt, feedbackResponseCommentAttributes.lastEditedAt);
        assertEquals(lastEditorAt, feedbackResponseCommentAttributes.lastEditedAt);
        assertTrue(feedbackResponseCommentAttributes.isVisibilityFollowingFeedbackQuestion);
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS),
                feedbackResponseCommentAttributes.showCommentTo);
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS),
                feedbackResponseCommentAttributes.showGiverNameTo);
        assertTrue(feedbackResponseCommentAttributes.isCommentFromFeedbackParticipant);
    }

    @Test
    public void testUpdateOptionsBuilder_withNullInput_shouldFailWithAssertionError() {
        assertThrows(AssertionError.class, () ->
                FeedbackResponseCommentAttributes.updateOptionsBuilder(123L)
                        .withFeedbackResponseId(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseCommentAttributes.updateOptionsBuilder(123L)
                        .withShowCommentTo(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseCommentAttributes.updateOptionsBuilder(123L)
                        .withShowGiverNameTo(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseCommentAttributes.updateOptionsBuilder(123L)
                        .withLastEditorEmail(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseCommentAttributes.updateOptionsBuilder(123L)
                        .withLastEditorAt(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseCommentAttributes.updateOptionsBuilder(123L)
                        .withGiverSection(null));
        assertThrows(AssertionError.class, () ->
                FeedbackResponseCommentAttributes.updateOptionsBuilder(123L)
                        .withReceiverSection(null));
    }
}
