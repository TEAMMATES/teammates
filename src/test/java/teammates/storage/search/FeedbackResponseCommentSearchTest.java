package teammates.storage.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.retry.RetryableTask;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;

/**
 * SUT: {@link FeedbackResponseCommentsDb},
 *      {@link teammates.storage.search.FeedbackResponseCommentSearchDocument},
 *      {@link teammates.storage.search.FeedbackResponseCommentSearchQuery}.
 */
public class FeedbackResponseCommentSearchTest extends BaseSearchTest {

    private FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private FeedbackResponseCommentsDb commentsDb = new FeedbackResponseCommentsDb();
    private FeedbackSessionsDb fsDb = new FeedbackSessionsDb();

    @Test
    public void allTests() throws Exception {
        FeedbackResponseCommentAttributes frc1I1Q1S1C1 = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes frc1I1Q2S1C1 = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q2S1C1");
        FeedbackResponseCommentAttributes frc1I1Q3S1C1 = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q3S1C1");
        FeedbackResponseCommentAttributes frc1I3Q1S2C2 = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S2C2");

        List<InstructorAttributes> instructors = new ArrayList<>();

        ______TS("success: search for comments; no results found as instructor doesn't have privileges");

        instructors.add(dataBundle.instructors.get("helperOfCourse1"));
        FeedbackResponseCommentSearchResultBundle bundle = commentsDb.search("\"self-feedback\"", instructors);
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.comments.isEmpty());

        ______TS("success: search for comments; query string does not match any comment");

        instructors.clear();
        instructors.add(dataBundle.instructors.get("instructor3OfCourse1"));
        instructors.add(dataBundle.instructors.get("instructor3OfCourse2"));
        bundle = commentsDb.search("non-existent", instructors);
        assertEquals(0, bundle.numberOfResults);
        assertTrue(bundle.comments.isEmpty());

        ______TS("success: search for comments; query string matches single comment");

        bundle = commentsDb.search("\"Instructor 3 comment to instr1C2 response to student1C2\"", instructors);
        verifySearchResults(bundle, frc1I3Q1S2C2);

        ______TS("success: search for comments in instructor's course; query string matches some comments");

        bundle = commentsDb.search("\"self feedback\"", instructors);
        verifySearchResults(bundle, frc1I1Q1S1C1, frc1I1Q3S1C1);

        ______TS("success: search for comments in instructor's course; confirms query string is case insensitive");

        bundle = commentsDb.search("\"Instructor 1 comment to response from "
                + "student 2 to student 5 in feedback Question 2\"", instructors);
        verifySearchResults(bundle, frc1I1Q2S1C1);

        ______TS("success: search for comments using feedbackSessionName");

        bundle = commentsDb.search("\"First feedback session\"", instructors);
        verifySearchResults(bundle, frc1I1Q2S1C1, frc1I1Q1S1C1, frc1I1Q3S1C1);

        ______TS("success: search for comments using Instructor's email");

        bundle = commentsDb.search("instructor1@course1.tmt", instructors);
        verifySearchResults(bundle, frc1I1Q2S1C1, frc1I1Q1S1C1, frc1I1Q3S1C1);

        ______TS("success: search for comments using Student name");

        bundle = commentsDb.search("\"student2 In Course1\"", instructors);
        verifySearchResults(bundle, frc1I1Q2S1C1);

        ______TS("success: search for comments; confirms deleted comments are not included in results");
        FeedbackResponseCommentAttributes commentToDelete = commentsDb.getFeedbackResponseComment(frc1I3Q1S2C2.courseId,
                frc1I3Q1S2C2.createdAt, frc1I3Q1S2C2.commentGiver);
        commentsDb.deleteDocumentByCommentId(commentToDelete.feedbackResponseCommentId);
        // search engine need time to persist changes
        getPersistenceRetryManager().runUntilNoRecognizedException(new RetryableTask("verify search result") {
            @Override
            public void run() {
                FeedbackResponseCommentSearchResultBundle bundle =
                        commentsDb.search("\"Instructor 3 comment to instr1C2 response to student1C2\"", instructors);
                verifySearchResults(bundle);
            }
        }, AssertionError.class);
    }

    @Test
    public void testSearchComment_feedbackSessionDeleted_commentShouldNotBeSearchable()
            throws InvalidParametersException, EntityDoesNotExistException {
        // perform normal search
        FeedbackResponseCommentAttributes frc1I3Q1S2C2 =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S2C2");
        List<InstructorAttributes> instructors = new ArrayList<>();
        instructors.add(dataBundle.instructors.get("instructor3OfCourse2"));
        FeedbackResponseCommentSearchResultBundle bundle =
                commentsDb.search("\"Instructor 3 comment to instr1C2 response to student1C2\"", instructors);
        verifySearchResults(bundle, frc1I3Q1S2C2);

        // session soft-deleted
        fsDb.softDeleteFeedbackSession(frc1I3Q1S2C2.feedbackSessionName, frc1I3Q1S2C2.courseId);
        assertNotNull(fsDb
                .getSoftDeletedFeedbackSession(frc1I3Q1S2C2.courseId, frc1I3Q1S2C2.feedbackSessionName));
        bundle = commentsDb.search("\"Instructor 3 comment to instr1C2 response to student1C2\"", instructors);
        assertEquals(0, bundle.comments.size());
        assertEquals(0, bundle.responses.size());
        assertEquals(0, bundle.questions.size());
        assertEquals(0, bundle.sessions.size());

        // session deleted completely
        fsDb.deleteFeedbackSession(frc1I3Q1S2C2.feedbackSessionName, frc1I3Q1S2C2.courseId);
        assertNull(fsDb
                .getSoftDeletedFeedbackSession(frc1I3Q1S2C2.courseId, frc1I3Q1S2C2.feedbackSessionName));
        bundle = commentsDb.search("\"Instructor 3 comment to instr1C2 response to student1C2\"", instructors);
        assertEquals(0, bundle.comments.size());
        assertEquals(0, bundle.responses.size());
        assertEquals(0, bundle.questions.size());
        assertEquals(0, bundle.sessions.size());
    }

    @Test
    public void testSearchComment_createNewComment_commentShouldBeSearchable() throws Exception {
        InstructorAttributes instructor3OfCourse1 = dataBundle.instructors.get("instructor3OfCourse1");
        FeedbackResponseAttributes response1ForQ1S1C1 = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        // get response1ForQ1S1C1 from the datastore
        FeedbackQuestionAttributes correspondingQuestion =
                fqDb.getFeedbackQuestion(response1ForQ1S1C1.getFeedbackSessionName(),
                        response1ForQ1S1C1.getCourseId(), Integer.parseInt(response1ForQ1S1C1.getFeedbackQuestionId()));
        response1ForQ1S1C1 = frDb.getFeedbackResponse(correspondingQuestion.getId(),
                response1ForQ1S1C1.getGiver(), response1ForQ1S1C1.getRecipient());
        assertNotNull(response1ForQ1S1C1);

        FeedbackResponseCommentSearchResultBundle bundle =
                commentsDb.search("commentABCDE", Arrays.asList(instructor3OfCourse1));

        assertEquals(0, bundle.numberOfResults);

        // create a new comment
        commentsDb.createEntity(
                FeedbackResponseCommentAttributes.builder()
                        .withCourseId(response1ForQ1S1C1.getCourseId())
                        .withFeedbackSessionName(response1ForQ1S1C1.getFeedbackSessionName())
                        .withCommentText("commentABCDE")
                        .withCommentGiver(response1ForQ1S1C1.getGiver())
                        .withCommentGiverType(FeedbackParticipantType.STUDENTS)
                        .withGiverSection(response1ForQ1S1C1.getGiverSection())
                        .withReceiverSection(response1ForQ1S1C1.getRecipientSection())
                        .withShowGiverNameTo(new ArrayList<>())
                        .withShowCommentTo(new ArrayList<>())
                        .withFeedbackQuestionId(response1ForQ1S1C1.getFeedbackQuestionId())
                        .withFeedbackResponseId(response1ForQ1S1C1.getId())
                        .withCommentFromFeedbackParticipant(true)
                        .withVisibilityFollowingFeedbackQuestion(true)
                        .build());

        // the newly created comment is searchable
        bundle = commentsDb.search("commentABCDE", Arrays.asList(instructor3OfCourse1));
        assertEquals(1, bundle.numberOfResults);
        assertEquals("commentABCDE",
                bundle.comments.get(response1ForQ1S1C1.getId()).get(0).getCommentText());
    }

    @Test
    public void testSearchComment_commentsDeletedByBatch_shouldReturnNoResult() {
        // perform normal search
        FeedbackResponseCommentAttributes frc1I3Q1S2C2 =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S2C2");
        List<InstructorAttributes> instructors = new ArrayList<>();
        instructors.add(dataBundle.instructors.get("instructor3OfCourse2"));
        FeedbackResponseCommentSearchResultBundle bundle =
                commentsDb.search("\"Instructor 3 comment to instr1C2 response to student1C2\"", instructors);
        verifySearchResults(bundle, frc1I3Q1S2C2);

        // delete comments inside the session
        commentsDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId(frc1I3Q1S2C2.courseId)
                        .withFeedbackSessionName(frc1I3Q1S2C2.feedbackSessionName)
                        .build());

        // document deleted, should have no search result
        bundle = commentsDb.search("\"Instructor 3 comment to instr1C2 response to student1C2\"", instructors);
        assertEquals(0, bundle.comments.size());
        assertEquals(0, bundle.responses.size());
        assertEquals(0, bundle.questions.size());
        assertEquals(0, bundle.sessions.size());
    }
}
