package teammates.logic.core;

import static teammates.common.datatransfer.FeedbackParticipantType.GIVER;
import static teammates.common.datatransfer.FeedbackParticipantType.INSTRUCTORS;
import static teammates.common.datatransfer.FeedbackParticipantType.OWN_TEAM_MEMBERS;
import static teammates.common.datatransfer.FeedbackParticipantType.RECEIVER;
import static teammates.common.datatransfer.FeedbackParticipantType.RECEIVER_TEAM_MEMBERS;
import static teammates.common.datatransfer.FeedbackParticipantType.STUDENTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
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

    private static final String COMMENT1_FROM_T1C1_TO_R1Q1S1C1 = "comment1FromT1C1ToR1Q1S1C1";
    private static final String COMMENT1_FROM_T1C1_TO_R1Q1S2C2 = "comment1FromT1C1ToR1Q1S2C2";
    private static final String COMMENT1_FROM_T1C1_TO_R1Q2S1C1 = "comment1FromT1C1ToR1Q2S1C1";
    private static final String RESPONSE1_FOR_Q1S1C1 = "response1ForQ1S1C1";
    private static final String RESPONSE1_FOR_Q2S1C1 = "response1ForQ2S1C1";
    private static final String INSTRUCTOR1_IN_COURSE1_EMAIL = "instructor1@course1.tmt";
    private static final String INSTRUCTOR3_IN_UNKNOWN_COURSE_EMAIL = "instructor3@course.tmt";
    private static final String INSTRUCTOR2_IN_COURSE1_EMAIL = "instructor2@course1.tmt";
    private static final String STUDENT1_IN_COURSE1_EMAIL = "student1InCourse1@gmail.tmt";
    private static final String STUDENT3_IN_COURSE1_EMAIL = "student3InCourse1@gmail.tmt";
    private static final String STUDENT4_IN_COURSE1_EMAIL = "student4InCourse1@gmail.tmt";
    private static final String STUDENT5_IN_COURSE1_EMAIL = "student5InCourse1@gmail.tmt";
    private static final String STUDENT6_IN_COURSE1_EMAIL = "student6InCourse1@gmail.tmt";
    private static final String STUDENT1_IN_COURSE2_EMAIL = "student1InCourse2@gmail.tmt";

    private DataBundle courseRosterDataBundle;

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        courseRosterDataBundle = loadDataBundle("/CourseRosterDataBundle.json");
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testCreateFeedbackResponseComment() throws Exception {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes[] finalFrc = new FeedbackResponseCommentAttributes[] { frComment };

        ______TS("fail: non-existent course");

        frComment.courseId = "no-such-course";

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.createFeedbackResponseComment(finalFrc[0]));
        assertEquals("Trying to create feedback response comments for a course that does not exist.",
                ednee.getMessage());
        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        finalFrc[0] = frComment;

        ______TS("fail: giver is not an instructor for the course");

        frComment.commentGiver = "instructor1@course2.com";

        ednee = assertThrows(EntityDoesNotExistException.class, () -> frcLogic.createFeedbackResponseComment(finalFrc[0]));
        assertEquals(
                "User " + frComment.commentGiver + " is not a registered instructor for course " + frComment.courseId + ".",
                ednee.getMessage());
        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        finalFrc[0] = frComment;

        ______TS("fail: feedback session is not a session for the course");

        frComment.feedbackSessionName = "Instructor feedback session";

        ednee = assertThrows(EntityDoesNotExistException.class, () -> frcLogic.createFeedbackResponseComment(finalFrc[0]));
        assertEquals(
                "Feedback session " + frComment.feedbackSessionName + " is not a session for course "
                        + frComment.courseId + ".",
                ednee.getMessage());

        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");

        ______TS("typical successful case");

        frComment.setId(null);
        frComment.feedbackQuestionId = getQuestionIdInDataBundle("qn1InSession1InCourse1");
        frComment.feedbackResponseId = getResponseIdInDataBundle("response2ForQ1S1C1", "qn1InSession1InCourse1");

        frcLogic.createFeedbackResponseComment(frComment);
        verifyPresentInDatabase(frComment);
    }

    @Test
    public void testCreateFeedbackResponseComment_invalidCommentGiverType_exceptionShouldBeThrown() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        frComment.commentGiverType = FeedbackParticipantType.SELF;
        frComment.isCommentFromFeedbackParticipant = true;
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.createFeedbackResponseComment(frComment));
        assertEquals("Unknown giver type: " + FeedbackParticipantType.SELF, ednee.getMessage());
    }

    @Test
    public void testCreateFeedbackResponseComment_unknownFeedbackParticipant_exceptionShouldBeThrown() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        frComment.commentGiverType = STUDENTS;
        frComment.isCommentFromFeedbackParticipant = true;
        frComment.commentGiver = "XYZ";
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.createFeedbackResponseComment(frComment));
        assertEquals("User XYZ is not a registered student for course idOfTypicalCourse1.", ednee.getMessage());
    }

    @Test
    public void testCreateFeedbackResponseComment_invalidVisibilitySettings_exceptionShouldBeThrown() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        frComment.isCommentFromFeedbackParticipant = true;
        frComment.isVisibilityFollowingFeedbackQuestion = false;
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

        frComment.courseId = "invalid course id";
        frComment.commentGiver = "invalid giver email";

        verifyNullFromGetFrCommentForSession(frComment);
        verifyNullFromGetFrComment(frComment);
        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");

        ______TS("Typical successful case");

        List<FeedbackResponseCommentAttributes> actualFrComments =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                                 frComment.courseId, frComment.feedbackSessionName, null);
        FeedbackResponseCommentAttributes actualFrComment = actualFrComments.get(0);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.commentGiver, actualFrComment.commentGiver);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        ______TS("Typical successful case by feedback response comment details");

        actualFrComment =
                frcLogic.getFeedbackResponseComment(
                                 frComment.feedbackResponseId, frComment.commentGiver, frComment.createdAt);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.commentGiver, actualFrComment.commentGiver);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        ______TS("Typical successful case by feedback response id");

        actualFrComments = frcLogic.getFeedbackResponseCommentForResponse(frComment.feedbackResponseId);
        actualFrComment = actualFrComments.get(0);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.commentGiver, actualFrComment.commentGiver);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        ______TS("Typical successful case by feedback response comment id");

        actualFrComment = frcLogic.getFeedbackResponseComment(frComment.getId());

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.commentGiver, actualFrComment.commentGiver);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        ______TS("Typical successful case for giver");

        actualFrComments = frcLogic.getFeedbackResponseCommentsForGiver(
                                            frComment.courseId, frComment.commentGiver);
        FeedbackResponseCommentAttributes tempFrComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        expectedFrComments.add(tempFrComment);
        tempFrComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q2S1C1");
        expectedFrComments.add(tempFrComment);
        tempFrComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q3S1C1");
        expectedFrComments.add(tempFrComment);

        assertEquals(expectedFrComments.size(), actualFrComments.size());

        for (int i = 0; i < expectedFrComments.size(); i++) {
            assertEquals(expectedFrComments.get(i).courseId, actualFrComments.get(i).courseId);
            assertEquals(expectedFrComments.get(i).commentGiver, actualFrComments.get(i).commentGiver);
            assertEquals(expectedFrComments.get(i).feedbackSessionName,
                         actualFrComments.get(i).feedbackSessionName);
        }

    }

    @Test
    public void testUpdateFeedbackResponseComment() throws Exception {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");

        ______TS("typical success case");
        frComment.commentText = "Updated feedback response comment";
        FeedbackResponseCommentAttributes updatedComment = frcLogic.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(frComment.getId())
                        .withCommentText(frComment.commentText)
                        .build()
        );
        assertEquals(frComment.commentText, updatedComment.commentText);
        verifyPresentInDatabase(frComment);
        List<FeedbackResponseCommentAttributes> actualFrComments =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        frComment.courseId, frComment.feedbackSessionName, null);

        FeedbackResponseCommentAttributes actualFrComment = null;
        for (FeedbackResponseCommentAttributes comment : actualFrComments) {
            if (comment.commentText.equals(frComment.commentText)) {
                actualFrComment = comment;
                break;
            }
        }
        assertNotNull(actualFrComment);

        ______TS("typical success case update feedback response comment giver email");

        String oldEmail = frComment.commentGiver;
        String updatedEmail = "newEmail@gmail.tmt";
        frcLogic.updateFeedbackResponseCommentsEmails(frComment.courseId, oldEmail, updatedEmail);

        actualFrComment = frcLogic.getFeedbackResponseComment(
                                           frComment.feedbackResponseId, updatedEmail, frComment.createdAt);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(updatedEmail, actualFrComment.commentGiver);
        assertEquals(updatedEmail, actualFrComment.lastEditorEmail);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        // reset email
        frcLogic.updateFeedbackResponseCommentsEmails(frComment.courseId, updatedEmail, oldEmail);
    }

    @Test
    public void testDeleteFeedbackResponseComment() throws Exception {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes actualFrComment =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        frComment.courseId, frComment.feedbackSessionName, null).get(1);

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
                        .withResponseId(frComment.feedbackResponseId)
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

    @Test
    public void testIsNameVisibleToUser() {
        FeedbackResponseCommentAttributes comment = dataBundle.feedbackResponseComments.get(COMMENT1_FROM_T1C1_TO_R1Q1S1C1);
        FeedbackResponseAttributes relatedResponse = dataBundle.feedbackResponses.get(RESPONSE1_FOR_Q1S1C1);
        CourseRoster roster = new CourseRoster(new ArrayList<>(courseRosterDataBundle.students.values()),
                new ArrayList<>(courseRosterDataBundle.instructors.values()));

        ______TS("success: the list that comment giver's name shown to is null; always return true");
        comment.showGiverNameTo = null;
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, "", roster));

        ______TS("success: comment's visibility follows feedback question; always return ture");
        comment.isVisibilityFollowingFeedbackQuestion = true;
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, "", roster));

        ______TS("success: comment is always visible to its giver");
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR1_IN_COURSE1_EMAIL, roster));

        comment = dataBundle.feedbackResponseComments.get(COMMENT1_FROM_T1C1_TO_R1Q1S2C2);
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR3_IN_UNKNOWN_COURSE_EMAIL, roster));

        // test user's participant type is in the list that comment giver's name is shown to
        comment = dataBundle.feedbackResponseComments.get(COMMENT1_FROM_T1C1_TO_R1Q2S1C1);
        relatedResponse = dataBundle.feedbackResponses.get(RESPONSE1_FOR_Q2S1C1);
        relatedResponse.giver = STUDENT1_IN_COURSE1_EMAIL;

        ______TS("success: comment is only visible to instructors");
        comment.commentGiver = STUDENT6_IN_COURSE1_EMAIL;
        comment.commentGiverType = STUDENTS;
        comment.showGiverNameTo = Arrays.asList(INSTRUCTORS);
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR2_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT5_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT3_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE2_EMAIL, roster));

        ______TS("success: comment is only visible to response recipient");
        comment.commentGiver = STUDENT4_IN_COURSE1_EMAIL;
        comment.commentGiverType = STUDENTS;
        comment.showGiverNameTo = Arrays.asList(RECEIVER);
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR2_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT5_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT6_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT3_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE2_EMAIL, roster));

        ______TS("success: comment is only visible to response recipient team");
        comment.showGiverNameTo = Arrays.asList(RECEIVER_TEAM_MEMBERS);
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR2_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT5_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT6_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT3_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE2_EMAIL, roster));

        ______TS("success: comment is only visible to response giver team");
        comment.showGiverNameTo = Arrays.asList(OWN_TEAM_MEMBERS);
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR2_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT5_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT6_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT3_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE2_EMAIL, roster));

        ______TS("success: comment is only visible to students");
        comment.showGiverNameTo = Arrays.asList(STUDENTS);
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR2_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT5_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT6_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT3_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE2_EMAIL, roster));

        ______TS("success: comment is only visible to response giver");
        comment.showGiverNameTo = Arrays.asList(GIVER);
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR2_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT5_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT6_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT3_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE2_EMAIL, roster));

        ______TS("success: comment is only visible to instructor, response giver and recipient's team");
        comment.showGiverNameTo = Arrays.asList(INSTRUCTORS, GIVER, RECEIVER_TEAM_MEMBERS);
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR2_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT5_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT6_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT3_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE2_EMAIL, roster));

        ______TS("success: comment is only visible to response giver's team and recipient");
        comment.showGiverNameTo = Arrays.asList(OWN_TEAM_MEMBERS, RECEIVER);
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR2_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT5_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT6_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT3_IN_COURSE1_EMAIL, roster));
        assertFalse(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE2_EMAIL, roster));

        ______TS("success: comment is visible to everyone");
        comment.showGiverNameTo = Arrays.asList(INSTRUCTORS, OWN_TEAM_MEMBERS, RECEIVER_TEAM_MEMBERS, RECEIVER,
                STUDENTS, GIVER);
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, INSTRUCTOR2_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT5_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT6_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT3_IN_COURSE1_EMAIL, roster));
        assertTrue(frcLogic.isNameVisibleToUser(comment, relatedResponse, STUDENT1_IN_COURSE2_EMAIL, roster));
    }

    private void verifyNullFromGetFrCommentForSession(FeedbackResponseCommentAttributes frComment) {
        List<FeedbackResponseCommentAttributes> frCommentsGot =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                        frComment.courseId, frComment.feedbackSessionName, null);
        assertEquals(0, frCommentsGot.size());
    }

    private void verifyNullFromGetFrComment(FeedbackResponseCommentAttributes frComment) {
        FeedbackResponseCommentAttributes frCommentGot =
                frcLogic.getFeedbackResponseComment(
                                 frComment.feedbackResponseId, frComment.commentGiver, frComment.createdAt);
        assertNull(frCommentGot);
    }

    private FeedbackResponseCommentAttributes restoreFrCommentFromDataBundle(String existingFrCommentInDataBundle) {

        FeedbackResponseCommentAttributes existingFrComment =
                dataBundle.feedbackResponseComments.get(existingFrCommentInDataBundle);

        FeedbackResponseCommentAttributes frComment = FeedbackResponseCommentAttributes.builder()
                .withCourseId(existingFrComment.courseId)
                .withFeedbackSessionName(existingFrComment.feedbackSessionName)
                .withCommentGiver(existingFrComment.commentGiver)
                .withCommentText(existingFrComment.commentText)
                .withFeedbackQuestionId(existingFrComment.feedbackQuestionId)
                .withFeedbackResponseId(existingFrComment.feedbackResponseId)
                .withCommentGiverType(existingFrComment.commentGiverType)
                .withCommentFromFeedbackParticipant(false)
                .build();
        frComment.createdAt = existingFrComment.getCreatedAt();

        restoreFrCommentIdFromExistingOne(frComment, existingFrComment);

        return frComment;
    }

    private void restoreFrCommentIdFromExistingOne(
            FeedbackResponseCommentAttributes frComment,
            FeedbackResponseCommentAttributes existingFrComment) {

        List<FeedbackResponseCommentAttributes> existingFrComments =
                frcLogic.getFeedbackResponseCommentForSessionInSection(
                                 existingFrComment.courseId,
                                 existingFrComment.feedbackSessionName, null);

        FeedbackResponseCommentAttributes existingFrCommentWithId = null;
        for (FeedbackResponseCommentAttributes c : existingFrComments) {
            if (c.commentText.equals(existingFrComment.commentText)) {
                existingFrCommentWithId = c;
                break;
            }
        }
        if (existingFrCommentWithId != null) {
            frComment.setId(existingFrCommentWithId.getId());
            frComment.feedbackResponseId = existingFrCommentWithId.feedbackResponseId;
        }
    }

    private String getQuestionIdInDataBundle(String questionInDataBundle) {
        FeedbackQuestionAttributes question = dataBundle.feedbackQuestions.get(questionInDataBundle);
        question = fqLogic.getFeedbackQuestion(
                                   question.feedbackSessionName, question.courseId, question.questionNumber);
        return question.getId();
    }

    private String getResponseIdInDataBundle(String responseInDataBundle, String questionInDataBundle) {
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get(responseInDataBundle);
        response = frLogic.getFeedbackResponse(
                                   getQuestionIdInDataBundle(questionInDataBundle),
                                   response.giver,
                                   response.recipient);
        return response.getId();
    }
}
