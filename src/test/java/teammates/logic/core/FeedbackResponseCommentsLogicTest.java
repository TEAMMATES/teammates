package teammates.logic.core;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

/**
 * SUT: {@link FeedbackResponseCommentsLogic}.
 */
public class FeedbackResponseCommentsLogicTest extends BaseLogicTest {

    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testCreateFeedbackResponseComment() throws Exception {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes[] finalFrc = new FeedbackResponseCommentAttributes[] { frComment };

        ______TS("fail: non-existent course");

        frComment.setCourseId("no-such-course");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.createFeedbackResponseComment(finalFrc[0]));
        assertEquals("Trying to create feedback response comments for a course that does not exist.",
                ednee.getMessage());
        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        finalFrc[0] = frComment;

        ______TS("fail: giver is not an instructor for the course");

        frComment.setCommentGiver("instructor1@course2.com");

        ednee = assertThrows(EntityDoesNotExistException.class, () -> frcLogic.createFeedbackResponseComment(finalFrc[0]));
        assertEquals("User " + frComment.getCommentGiver() + " is not a registered instructor for course "
                + frComment.getCourseId() + ".", ednee.getMessage());
        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        finalFrc[0] = frComment;

        ______TS("fail: feedback session is not a session for the course");

        frComment.setFeedbackSessionName("Instructor feedback session");

        ednee = assertThrows(EntityDoesNotExistException.class, () -> frcLogic.createFeedbackResponseComment(finalFrc[0]));
        assertEquals(
                "Feedback session " + frComment.getFeedbackSessionName() + " is not a session for course "
                        + frComment.getCourseId() + ".",
                ednee.getMessage());

        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");

        ______TS("typical successful case");

        frComment.setId(null);
        frComment.setFeedbackQuestionId(getQuestionIdInDataBundle("qn1InSession1InCourse1"));
        frComment.setFeedbackResponseId(getResponseIdInDataBundle("response2ForQ1S1C1", "qn1InSession1InCourse1"));

        frcLogic.createFeedbackResponseComment(frComment);
        verifyPresentInDatabase(frComment);
    }

    @Test
    public void testCreateFeedbackResponseComment_invalidCommentGiverType_exceptionShouldBeThrown() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        frComment.setCommentGiverType(FeedbackParticipantType.SELF);
        frComment.setCommentFromFeedbackParticipant(true);
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.createFeedbackResponseComment(frComment));
        assertEquals("Unknown giver type: " + FeedbackParticipantType.SELF, ednee.getMessage());
    }

    @Test
    public void testCreateFeedbackResponseComment_unknownFeedbackParticipant_exceptionShouldBeThrown() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        frComment.setCommentGiverType(FeedbackParticipantType.STUDENTS);
        frComment.setCommentFromFeedbackParticipant(true);
        frComment.setCommentGiver("XYZ");
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.createFeedbackResponseComment(frComment));
        assertEquals("User XYZ is not a registered student for course idOfTypicalCourse1.", ednee.getMessage());
    }

    @Test
    public void testCreateFeedbackResponseComment_invalidVisibilitySettings_exceptionShouldBeThrown() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        frComment.setCommentFromFeedbackParticipant(true);
        frComment.setVisibilityFollowingFeedbackQuestion(false);
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> frcLogic.createFeedbackResponseComment(frComment));
        assertEquals("Comment by feedback participant not following visibility setting of the question.",
                ipe.getMessage());
    }

    @Test
    public void testGetFeedbackResponseComments() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        List<FeedbackResponseCommentAttributes> expectedFrComments = new ArrayList<>();

        ______TS("fail: invalid parameters");

        frComment.setCourseId("invalid course id");
        frComment.setCommentGiver("invalid giver email");

        verifyNullFromGetFrCommentForSession(frComment);
        verifyNullFromGetFrComment(frComment);
        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");

        ______TS("Typical successful case");

        List<FeedbackResponseCommentAttributes> actualFrComments =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        frComment.getCourseId(), frComment.getFeedbackSessionName(), null);
        FeedbackResponseCommentAttributes actualFrComment = actualFrComments.get(0);

        assertEquals(frComment.getCourseId(), actualFrComment.getCourseId());
        assertEquals(frComment.getCommentGiver(), actualFrComment.getCommentGiver());
        assertEquals(frComment.getFeedbackSessionName(), actualFrComment.getFeedbackSessionName());

        ______TS("Typical successful case by feedback response comment details");

        actualFrComment =
                frcLogic.getFeedbackResponseComment(
                        frComment.getFeedbackResponseId(), frComment.getCommentGiver(), frComment.getCreatedAt());

        assertEquals(frComment.getCourseId(), actualFrComment.getCourseId());
        assertEquals(frComment.getCommentGiver(), actualFrComment.getCommentGiver());
        assertEquals(frComment.getFeedbackSessionName(), actualFrComment.getFeedbackSessionName());

        ______TS("Typical successful case by feedback response id");

        actualFrComments = frcLogic.getFeedbackResponseCommentForResponse(frComment.getFeedbackResponseId());
        actualFrComment = actualFrComments.get(0);

        assertEquals(frComment.getCourseId(), actualFrComment.getCourseId());
        assertEquals(frComment.getCommentGiver(), actualFrComment.getCommentGiver());
        assertEquals(frComment.getFeedbackSessionName(), actualFrComment.getFeedbackSessionName());

        ______TS("Typical successful case by feedback response comment id");

        actualFrComment = frcLogic.getFeedbackResponseComment(frComment.getId());

        assertEquals(frComment.getCourseId(), actualFrComment.getCourseId());
        assertEquals(frComment.getCommentGiver(), actualFrComment.getCommentGiver());
        assertEquals(frComment.getFeedbackSessionName(), actualFrComment.getFeedbackSessionName());

        ______TS("Typical successful case for giver");

        actualFrComments = frcLogic.getFeedbackResponseCommentsForGiver(
                frComment.getCourseId(), frComment.getCommentGiver());
        FeedbackResponseCommentAttributes tempFrComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        expectedFrComments.add(tempFrComment);
        tempFrComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q2S1C1");
        expectedFrComments.add(tempFrComment);
        tempFrComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q3S1C1");
        expectedFrComments.add(tempFrComment);

        assertEquals(expectedFrComments.size(), actualFrComments.size());

        for (int i = 0; i < expectedFrComments.size(); i++) {
            assertEquals(expectedFrComments.get(i).getCourseId(), actualFrComments.get(i).getCourseId());
            assertEquals(expectedFrComments.get(i).getCommentGiver(), actualFrComments.get(i).getCommentGiver());
            assertEquals(expectedFrComments.get(i).getFeedbackSessionName(),
                    actualFrComments.get(i).getFeedbackSessionName());
        }

    }

    @Test
    public void testUpdateFeedbackResponseComment() throws Exception {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");

        ______TS("typical success case");
        frComment.setCommentText("Updated feedback response comment");
        FeedbackResponseCommentAttributes updatedComment = frcLogic.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(frComment.getId())
                        .withCommentText(frComment.getCommentText())
                        .build()
        );
        assertEquals(frComment.getCommentText(), updatedComment.getCommentText());
        verifyPresentInDatabase(frComment);
        List<FeedbackResponseCommentAttributes> actualFrComments =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        frComment.getCourseId(), frComment.getFeedbackSessionName(), null);

        FeedbackResponseCommentAttributes actualFrComment = null;
        for (FeedbackResponseCommentAttributes comment : actualFrComments) {
            if (comment.getCommentText().equals(frComment.getCommentText())) {
                actualFrComment = comment;
                break;
            }
        }
        assertNotNull(actualFrComment);

        ______TS("typical success case update feedback response comment giver email");

        String oldEmail = frComment.getCommentGiver();
        String updatedEmail = "newEmail@gmail.tmt";
        frcLogic.updateFeedbackResponseCommentsEmails(frComment.getCourseId(), oldEmail, updatedEmail);

        actualFrComment = frcLogic.getFeedbackResponseComment(
                frComment.getFeedbackResponseId(), updatedEmail, frComment.getCreatedAt());

        assertEquals(frComment.getCourseId(), actualFrComment.getCourseId());
        assertEquals(updatedEmail, actualFrComment.getCommentGiver());
        assertEquals(updatedEmail, actualFrComment.getLastEditorEmail());
        assertEquals(frComment.getFeedbackSessionName(), actualFrComment.getFeedbackSessionName());

        // reset email
        frcLogic.updateFeedbackResponseCommentsEmails(frComment.getCourseId(), updatedEmail, oldEmail);
    }

    @Test
    public void testDeleteFeedbackResponseComment() throws Exception {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes actualFrComment =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        frComment.getCourseId(), frComment.getFeedbackSessionName(), null).get(1);

        ______TS("silent fail nothing to delete");

        assertNull(frcLogic.getFeedbackResponseComment(1234567L));
        frcLogic.deleteFeedbackResponseComment(1234567L);

        ______TS("typical success case");

        verifyPresentInDatabase(actualFrComment);
        frcLogic.deleteFeedbackResponseComment(actualFrComment.getId());
        verifyAbsentInDatabase(actualFrComment);
    }

    @Test
    public void testDeleteFeedbackResponseComments_deleteByResponseId() {

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q3S1C1");
        verifyPresentInDatabase(frComment);
        frcLogic.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withResponseId(frComment.getFeedbackResponseId())
                        .build());
        verifyAbsentInDatabase(frComment);
    }

    @Test
    public void testDeleteFeedbackResponseComments_deleteByCourseId() {

        ______TS("typical case");
        String courseId = "idOfTypicalCourse1";

        List<FeedbackResponseCommentAttributes> frcList =
                frcLogic.getFeedbackResponseCommentForSessionInSection(courseId, "First feedback session", null);
        assertFalse(frcList.isEmpty());

        frcLogic.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId(courseId)
                        .build());

        frcList = frcLogic.getFeedbackResponseCommentForSessionInSection(courseId, "First feedback session", null);
        assertEquals(0, frcList.size());
    }

    @Test
    public void testGetFeedbackResponseCommentForSessionInSection_noSectionName_shouldReturnCommentsInSession() {
        List<FeedbackResponseCommentAttributes> comments =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        "idOfTypicalCourse1", "First feedback session", null);
        assertEquals(3, comments.size());

        comments = frcLogic.getFeedbackResponseCommentForSessionInSection(
                        "not_exist", "First feedback session", null);
        assertEquals(0, comments.size());

        comments = frcLogic.getFeedbackResponseCommentForSessionInSection(
                "idOfTypicalCourse1", "not_exist", null);
        assertEquals(0, comments.size());

        comments = frcLogic.getFeedbackResponseCommentForSessionInSection(
                "not_exist", "not_exist", null);
        assertEquals(0, comments.size());
    }

    @Test
    public void testGetFeedbackResponseCommentForSessionInSection_withSectionName_shouldReturnCommentsInSection() {
        List<FeedbackResponseCommentAttributes> comments =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        "idOfTypicalCourse1", "First feedback session", "Section 1");
        assertEquals(2, comments.size());

        comments = frcLogic.getFeedbackResponseCommentForSessionInSection(
                "idOfTypicalCourse1", "First feedback session", "Section 2");
        assertEquals(1, comments.size());

        comments = frcLogic.getFeedbackResponseCommentForSessionInSection(
                "idOfTypicalCourse1", "First feedback session", "not_exist");
        assertEquals(0, comments.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestionInSection_noSectionName_shouldReturnCommentsForQuestion() {
        String questionId = getQuestionIdInDataBundle("qn1InSession1InCourse1");
        List<FeedbackResponseCommentAttributes> comments =
                frcLogic.getFeedbackResponseCommentForQuestionInSection(questionId, null);
        assertEquals(1, comments.size());

        comments = frcLogic.getFeedbackResponseCommentForQuestionInSection("not_exist", null);
        assertEquals(0, comments.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestionInSection_withSectionName_shouldReturnCommentsForQuestion() {
        String questionId = getQuestionIdInDataBundle("qn2InSession1InCourse1");
        List<FeedbackResponseCommentAttributes> comments =
                frcLogic.getFeedbackResponseCommentForQuestionInSection(questionId, "Section 1");
        assertEquals(1, comments.size());

        comments = frcLogic.getFeedbackResponseCommentForQuestionInSection(questionId, "Section 2");
        assertEquals(1, comments.size());

        comments = frcLogic.getFeedbackResponseCommentForQuestionInSection(questionId, "not_exist");
        assertEquals(0, comments.size());
    }

    private void verifyNullFromGetFrCommentForSession(FeedbackResponseCommentAttributes frComment) {
        List<FeedbackResponseCommentAttributes> frCommentsGot =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        frComment.getCourseId(), frComment.getFeedbackSessionName(), null);
        assertEquals(0, frCommentsGot.size());
    }

    private void verifyNullFromGetFrComment(FeedbackResponseCommentAttributes frComment) {
        FeedbackResponseCommentAttributes frCommentGot =
                frcLogic.getFeedbackResponseComment(
                        frComment.getFeedbackResponseId(), frComment.getCommentGiver(), frComment.getCreatedAt());
        assertNull(frCommentGot);
    }

    private FeedbackResponseCommentAttributes restoreFrCommentFromDataBundle(String existingFrCommentInDataBundle) {

        FeedbackResponseCommentAttributes existingFrComment =
                dataBundle.feedbackResponseComments.get(existingFrCommentInDataBundle);

        FeedbackResponseCommentAttributes frComment = FeedbackResponseCommentAttributes.builder()
                .withCourseId(existingFrComment.getCourseId())
                .withFeedbackSessionName(existingFrComment.getFeedbackSessionName())
                .withCommentGiver(existingFrComment.getCommentGiver())
                .withCommentText(existingFrComment.getCommentText())
                .withFeedbackQuestionId(existingFrComment.getFeedbackQuestionId())
                .withFeedbackResponseId(existingFrComment.getFeedbackResponseId())
                .withCommentGiverType(existingFrComment.getCommentGiverType())
                .withCommentFromFeedbackParticipant(false)
                .build();
        frComment.setCreatedAt(existingFrComment.getCreatedAt());

        restoreFrCommentIdFromExistingOne(frComment, existingFrComment);

        return frComment;
    }

    private void restoreFrCommentIdFromExistingOne(
            FeedbackResponseCommentAttributes frComment,
            FeedbackResponseCommentAttributes existingFrComment) {

        List<FeedbackResponseCommentAttributes> existingFrComments =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        existingFrComment.getCourseId(),
                        existingFrComment.getFeedbackSessionName(), null);

        FeedbackResponseCommentAttributes existingFrCommentWithId = null;
        for (FeedbackResponseCommentAttributes c : existingFrComments) {
            if (c.getCommentText().equals(existingFrComment.getCommentText())) {
                existingFrCommentWithId = c;
                break;
            }
        }
        if (existingFrCommentWithId != null) {
            frComment.setId(existingFrCommentWithId.getId());
            frComment.setFeedbackResponseId(existingFrCommentWithId.getFeedbackResponseId());
        }
    }

    private String getQuestionIdInDataBundle(String questionInDataBundle) {
        FeedbackQuestionAttributes question = dataBundle.feedbackQuestions.get(questionInDataBundle);
        question = fqLogic.getFeedbackQuestion(
                question.getFeedbackSessionName(), question.getCourseId(), question.getQuestionNumber());
        return question.getId();
    }

    private String getResponseIdInDataBundle(String responseInDataBundle, String questionInDataBundle) {
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get(responseInDataBundle);
        response = frLogic.getFeedbackResponse(
                                   getQuestionIdInDataBundle(questionInDataBundle),
                response.getGiver(),
                response.getRecipient());
        return response.getId();
    }
}
