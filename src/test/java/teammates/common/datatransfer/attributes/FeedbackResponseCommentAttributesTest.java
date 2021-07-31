package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;

import org.testng.annotations.Test;
import org.testng.collections.Lists;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.test.BaseTestCase;

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
    public void testValueOf_modificationInAttributes_shouldNotLeakStateToEntity() {
        FeedbackResponseComment responseComment = new FeedbackResponseComment("course", "name",
                "question", "giver", FeedbackParticipantType.STUDENTS, "id", null,
                "comment", null, null,
                new ArrayList<>(), new ArrayList<>(), "lastEditor", Instant.now(), false, false);

        FeedbackResponseCommentAttributes commentAttributes =
                FeedbackResponseCommentAttributes.valueOf(responseComment);

        commentAttributes.getShowCommentTo().add(FeedbackParticipantType.STUDENTS);
        commentAttributes.getShowGiverNameTo().add(FeedbackParticipantType.STUDENTS);

        assertTrue(responseComment.getShowCommentTo().isEmpty());
        assertTrue(responseComment.getShowGiverNameTo().isEmpty());
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
        feedbackResponseCommentAttributes.setLastEditedAt(lastEditorAt.minusSeconds(60));
        feedbackResponseCommentAttributes.setLastEditorEmail("editor2@email.com");

        Instant expectedCreatedAt = feedbackResponseCommentAttributes.getCreatedAt();
        feedbackResponseCommentAttributes.update(updateOptions);

        assertEquals("courseId", feedbackResponseCommentAttributes.getCourseId());
        assertEquals("sessionName", feedbackResponseCommentAttributes.getFeedbackSessionName());
        assertEquals("giver@email.com", feedbackResponseCommentAttributes.getCommentGiver());
        assertEquals("commentText1", feedbackResponseCommentAttributes.getCommentText());
        assertEquals("responseId1", feedbackResponseCommentAttributes.getFeedbackResponseId());
        assertEquals("questionId", feedbackResponseCommentAttributes.getFeedbackQuestionId());
        assertEquals(expectedCreatedAt, feedbackResponseCommentAttributes.getCreatedAt());
        assertEquals("section1", feedbackResponseCommentAttributes.getGiverSection());
        assertEquals("section1", feedbackResponseCommentAttributes.getReceiverSection());
        assertEquals(FeedbackParticipantType.STUDENTS, feedbackResponseCommentAttributes.getCommentGiverType());
        assertEquals("editor1@email.com", feedbackResponseCommentAttributes.getLastEditorEmail());
        assertEquals(lastEditorAt, feedbackResponseCommentAttributes.getLastEditedAt());
        assertTrue(feedbackResponseCommentAttributes.isVisibilityFollowingFeedbackQuestion());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS),
                feedbackResponseCommentAttributes.getShowCommentTo());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS),
                feedbackResponseCommentAttributes.getShowGiverNameTo());
        assertTrue(feedbackResponseCommentAttributes.isCommentFromFeedbackParticipant());
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

    @Test
    public void testEquals() {
        FeedbackResponseCommentAttributes feedbackResponseComment =
                generateTypicalFeedbackResponseCommentAttributesObject();

        // When the two feedback response comments have same values
        FeedbackResponseCommentAttributes feedbackResponseCommentSimilar =
                generateTypicalFeedbackResponseCommentAttributesObject();

        assertTrue(feedbackResponseComment.equals(feedbackResponseCommentSimilar));

        // When the two feedback response comments are different
        FeedbackResponseCommentAttributes feedbackResponseCommentDifferent =
                generateValidFeedbackResponseCommentAttributesObject();

        assertFalse(feedbackResponseComment.equals(feedbackResponseCommentDifferent));

        // When the other object is of different class
        assertFalse(feedbackResponseComment.equals(3));
    }

    @Test
    public void testHashCode() {
        FeedbackResponseCommentAttributes feedbackResponseComment =
                generateTypicalFeedbackResponseCommentAttributesObject();

        // When the two feedback response comments have same values, they should have the same hash code
        FeedbackResponseCommentAttributes feedbackResponseCommentSimilar =
                generateTypicalFeedbackResponseCommentAttributesObject();

        assertTrue(feedbackResponseComment.equals(feedbackResponseCommentSimilar));

        // When the two feedback response comments are different, they should have different hash code
        FeedbackResponseCommentAttributes feedbackResponseCommentDifferent =
                generateValidFeedbackResponseCommentAttributesObject();

        assertFalse(feedbackResponseComment.hashCode() == feedbackResponseCommentDifferent.hashCode());
    }

    private static FeedbackResponseCommentAttributes generateValidFeedbackResponseCommentAttributesObject() {
        return FeedbackResponseCommentAttributes.builder()
                .withCourseId("courseId")
                .withFeedbackSessionName("validSessionName")
                .withCommentGiver("giver@email.com")
                .withFeedbackResponseId("responseId")
                .withFeedbackQuestionId("questionId")
                .withGiverSection("testSection")
                .withReceiverSection("testSection")
                .build();
    }

    private static FeedbackResponseCommentAttributes generateTypicalFeedbackResponseCommentAttributesObject() {
        return FeedbackResponseCommentAttributes.builder()
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
    }
}
