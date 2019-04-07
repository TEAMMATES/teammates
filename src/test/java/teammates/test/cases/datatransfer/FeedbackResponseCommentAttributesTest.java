package teammates.test.cases.datatransfer;

import java.time.Instant;
import java.util.ArrayList;

import org.testng.annotations.Test;
import org.testng.collections.Lists;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackResponseCommentAttributes}.
 */
public class FeedbackResponseCommentAttributesTest extends BaseTestCase {

    @Test
    public void testBuilder_buildNothing_shouldUseDefaultValues() {
        FeedbackResponseCommentAttributes feedbackResponseCommentAttributes =
                FeedbackResponseCommentAttributes.builder().build();

        // Default values for following fields
        assertNull(feedbackResponseCommentAttributes.getCourseId());
        assertNull(feedbackResponseCommentAttributes.getFeedbackSessionName());

        assertNull(feedbackResponseCommentAttributes.getCommentGiver());
        assertNull(feedbackResponseCommentAttributes.getCommentText());

        assertNull(feedbackResponseCommentAttributes.getFeedbackQuestionId());
        assertNull(feedbackResponseCommentAttributes.getFeedbackResponseId());

        assertTrue(feedbackResponseCommentAttributes.getShowCommentTo().isEmpty());
        assertTrue(feedbackResponseCommentAttributes.getShowGiverNameTo().isEmpty());

        assertTrue(feedbackResponseCommentAttributes.isVisibilityFollowingFeedbackQuestion());
        assertNotNull(feedbackResponseCommentAttributes.getCreatedAt());
        assertNull(feedbackResponseCommentAttributes.getLastEditorEmail());
        assertNull(feedbackResponseCommentAttributes.getLastEditedAt());
        assertNull(feedbackResponseCommentAttributes.getId());

        assertEquals(Const.DEFAULT_SECTION, feedbackResponseCommentAttributes.getGiverSection());
        assertEquals(Const.DEFAULT_SECTION, feedbackResponseCommentAttributes.getReceiverSection());

        assertEquals(FeedbackParticipantType.INSTRUCTORS, feedbackResponseCommentAttributes.getCommentGiverType());
        assertFalse(feedbackResponseCommentAttributes.isCommentFromFeedbackParticipant());
    }

    @Test
    public void testBuilder_withNullArguments_shouldThrowException() {
        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withCourseId(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withFeedbackSessionName(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withCommentGiver(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withCommentText(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withFeedbackResponseId(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withFeedbackQuestionId(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withShowCommentTo(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withShowGiverNameTo(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withGiverSection(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withReceiverSection(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackResponseCommentAttributes
                    .builder()
                    .withCommentGiverType(null)
                    .build();
        });
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttributes() {
        FeedbackResponseCommentAttributes feedbackResponseCommentAttributes =
                FeedbackResponseCommentAttributes.builder()
                        .withCourseId("courseId")
                        .withFeedbackSessionName("sessionName")
                        .withCommentGiver("giver@email.com")
                        .withCommentText("testComment")
                        .withFeedbackResponseId("responseId")
                        .withFeedbackQuestionId("questionId")
                        .withGiverSection("testSection")
                        .withReceiverSection("testSection")
                        .withCommentGiverType(FeedbackParticipantType.STUDENTS)
                        .withVisibilityFollowingFeedbackQuestion(true)
                        .withShowCommentTo(new ArrayList<>())
                        .withShowGiverNameTo(new ArrayList<>())
                        .withCommentFromFeedbackParticipant(true)
                        .build();

        assertEquals("courseId", feedbackResponseCommentAttributes.getCourseId());
        assertEquals("sessionName", feedbackResponseCommentAttributes.getFeedbackSessionName());
        assertEquals("giver@email.com", feedbackResponseCommentAttributes.getCommentGiver());
        assertEquals("testComment", feedbackResponseCommentAttributes.getCommentText());
        assertEquals("responseId", feedbackResponseCommentAttributes.getFeedbackResponseId());
        assertEquals("questionId", feedbackResponseCommentAttributes.getFeedbackQuestionId());
        assertTrue(feedbackResponseCommentAttributes.getShowGiverNameTo().isEmpty());
        assertTrue(feedbackResponseCommentAttributes.getShowCommentTo().isEmpty());
        assertTrue(feedbackResponseCommentAttributes.isVisibilityFollowingFeedbackQuestion());
        assertNotNull(feedbackResponseCommentAttributes.getCreatedAt());
        assertNull(feedbackResponseCommentAttributes.getLastEditorEmail());
        assertNull(feedbackResponseCommentAttributes.getLastEditedAt());
        assertNull(feedbackResponseCommentAttributes.getId());
        assertEquals("testSection", feedbackResponseCommentAttributes.getGiverSection());
        assertEquals("testSection", feedbackResponseCommentAttributes.getReceiverSection());
        assertEquals(FeedbackParticipantType.STUDENTS, feedbackResponseCommentAttributes.getCommentGiverType());
        assertTrue(feedbackResponseCommentAttributes.isCommentFromFeedbackParticipant());
    }

    @Test
    public void testValueOf_withAllFieldPopulatedFeedbackResponseComment_shouldGenerateAttributesCorrectly() {
        FeedbackResponseComment responseComment = new FeedbackResponseComment("course", "name",
                "question", "giver", FeedbackParticipantType.STUDENTS, "id", Instant.now(),
                "comment", "giverSection", "receiverSection",
                new ArrayList<>(), new ArrayList<>(), "lastEditor", Instant.now(), false, false);

        FeedbackResponseCommentAttributes feedbackAttributes =
                FeedbackResponseCommentAttributes.valueOf(responseComment);

        assertEquals(responseComment.getCourseId(), feedbackAttributes.getCourseId());
        assertEquals(responseComment.getFeedbackSessionName(), feedbackAttributes.getFeedbackSessionName());
        assertEquals(responseComment.getFeedbackQuestionId(), feedbackAttributes.getFeedbackQuestionId());
        assertEquals(responseComment.getGiverEmail(), feedbackAttributes.getCommentGiver());
        assertEquals(responseComment.getCommentGiverType(), feedbackAttributes.getCommentGiverType());
        assertEquals(responseComment.getFeedbackResponseId(), feedbackAttributes.getFeedbackResponseId());
        assertEquals(responseComment.getCreatedAt(), feedbackAttributes.getCreatedAt());
        assertEquals(responseComment.getCommentText(), feedbackAttributes.getCommentText());
        assertEquals(responseComment.getGiverSection(), feedbackAttributes.getGiverSection());
        assertEquals(responseComment.getReceiverSection(), feedbackAttributes.getReceiverSection());
        assertEquals(responseComment.getShowCommentTo(), feedbackAttributes.getShowCommentTo());
        assertEquals(responseComment.getShowGiverNameTo(), feedbackAttributes.getShowGiverNameTo());
        assertEquals(responseComment.getLastEditorEmail(), feedbackAttributes.getLastEditorEmail());
        assertEquals(responseComment.getLastEditedAt(), feedbackAttributes.getLastEditedAt());
        assertEquals(responseComment.getFeedbackResponseCommentId(), feedbackAttributes.getId());

        assertEquals(responseComment.getIsCommentFromFeedbackParticipant(),
                feedbackAttributes.isCommentFromFeedbackParticipant());
        assertEquals(responseComment.getIsVisibilityFollowingFeedbackQuestion(),
                feedbackAttributes.isVisibilityFollowingFeedbackQuestion());
    }

    @Test
    public void testValueOf_withSomeFieldsPopulatedAsNull_shouldUseDefaultValues() {
        FeedbackResponseComment responseComment = new FeedbackResponseComment("course", "name",
                "question", "giver", FeedbackParticipantType.STUDENTS, "id", null,
                "comment", null, null,
                new ArrayList<>(), new ArrayList<>(), "lastEditor", Instant.now(), false, false);
        responseComment.setShowCommentTo(null);
        responseComment.setShowGiverNameTo(null);
        responseComment.setLastEditorEmail(null);
        responseComment.setLastEditedAt(null);
        assertNull(responseComment.getShowCommentTo());
        assertNull(responseComment.getShowGiverNameTo());
        assertNull(responseComment.getCreatedAt());
        assertNull(responseComment.getLastEditedAt());
        assertNull(responseComment.getLastEditorEmail());
        assertNull(responseComment.getGiverSection());
        assertNull(responseComment.getReceiverSection());

        FeedbackResponseCommentAttributes feedbackAttributes =
                FeedbackResponseCommentAttributes.valueOf(responseComment);

        assertEquals(responseComment.getCourseId(), feedbackAttributes.getCourseId());
        assertEquals(responseComment.getFeedbackSessionName(), feedbackAttributes.getFeedbackSessionName());
        assertEquals(responseComment.getFeedbackQuestionId(), feedbackAttributes.getFeedbackQuestionId());
        assertEquals(responseComment.getGiverEmail(), feedbackAttributes.getCommentGiver());
        assertEquals(responseComment.getCommentGiverType(), feedbackAttributes.getCommentGiverType());
        assertEquals(responseComment.getFeedbackResponseId(), feedbackAttributes.getFeedbackResponseId());
        assertNotNull(feedbackAttributes.getCreatedAt());
        assertEquals(responseComment.getCommentText(), feedbackAttributes.getCommentText());
        assertEquals(Const.DEFAULT_SECTION, feedbackAttributes.getGiverSection());
        assertEquals(Const.DEFAULT_SECTION, feedbackAttributes.getReceiverSection());
        assertEquals(new ArrayList<>(), feedbackAttributes.getShowCommentTo());
        assertEquals(new ArrayList<>(), feedbackAttributes.getShowGiverNameTo());
        assertEquals(feedbackAttributes.getCommentGiver(), feedbackAttributes.getLastEditorEmail());
        assertNotNull(feedbackAttributes.getLastEditedAt());
        assertEquals(responseComment.getFeedbackResponseCommentId(), feedbackAttributes.getId());

        assertEquals(responseComment.getIsCommentFromFeedbackParticipant(),
                feedbackAttributes.isCommentFromFeedbackParticipant());
        assertEquals(responseComment.getIsVisibilityFollowingFeedbackQuestion(),
                feedbackAttributes.isVisibilityFollowingFeedbackQuestion());
    }

    @Test
    public void testConvertCommentTextToStringForCsv() {
        String text = "aaa , bb\"b, c\"\"cc <image src=\"http://test.com/test.png\"></image> hello";
        FeedbackResponseCommentAttributes feedbackAttributes = FeedbackResponseCommentAttributes
                .builder()
                .withCommentText(text)
                .build();
        String commentText = feedbackAttributes.getCommentAsCsvString();
        assertEquals("\"aaa , bb\"\"b, c\"\"\"\"cc hello Images Link: http://test.com/test.png \"", commentText);
    }

    @Test
    public void testConvertCommentTextToStringForHtml() {
        String text = "<script>alert('injected');</script> <image src=\"http://test.com/test.png\"></image> hello";
        FeedbackResponseCommentAttributes feedbackAttributes = FeedbackResponseCommentAttributes
                .builder()
                .withCommentText(text)
                .build();
        String commentText = feedbackAttributes.getCommentAsHtmlString();
        assertEquals("hello Images Link: http:&#x2f;&#x2f;test.com&#x2f;test.png ", commentText);
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

        FeedbackResponseCommentAttributes feedbackResponseCommentAttributes =
                FeedbackResponseCommentAttributes.builder()
                        .withCourseId("courseId")
                        .withFeedbackSessionName("sessionName")
                        .withCommentGiver("giver@email.com")
                        .withCommentText("testComment")
                        .withFeedbackResponseId("responseId")
                        .withFeedbackQuestionId("questionId")
                        .withGiverSection("testSection")
                        .withReceiverSection("testSection")
                        .withCommentGiverType(FeedbackParticipantType.STUDENTS)
                        .withVisibilityFollowingFeedbackQuestion(true)
                        .withShowCommentTo(new ArrayList<>())
                        .withShowGiverNameTo(new ArrayList<>())
                        .withCommentFromFeedbackParticipant(true)
                        .build();
        feedbackResponseCommentAttributes.lastEditedAt = lastEditorAt.minusSeconds(60);
        feedbackResponseCommentAttributes.lastEditorEmail = "editor2@email.com";

        Instant expectedCreatedAt = feedbackResponseCommentAttributes.getCreatedAt();
        feedbackResponseCommentAttributes.update(updateOptions);

        assertEquals("courseId", feedbackResponseCommentAttributes.courseId);
        assertEquals("sessionName", feedbackResponseCommentAttributes.feedbackSessionName);
        assertEquals("giver@email.com", feedbackResponseCommentAttributes.commentGiver);
        assertEquals("commentText1", feedbackResponseCommentAttributes.commentText);
        assertEquals("responseId1", feedbackResponseCommentAttributes.feedbackResponseId);
        assertEquals("questionId", feedbackResponseCommentAttributes.feedbackQuestionId);
        assertEquals(expectedCreatedAt, feedbackResponseCommentAttributes.createdAt);
        assertEquals("section1", feedbackResponseCommentAttributes.giverSection);
        assertEquals("section1", feedbackResponseCommentAttributes.receiverSection);
        assertEquals(FeedbackParticipantType.STUDENTS, feedbackResponseCommentAttributes.commentGiverType);
        assertEquals("editor1@email.com", feedbackResponseCommentAttributes.lastEditorEmail);
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
