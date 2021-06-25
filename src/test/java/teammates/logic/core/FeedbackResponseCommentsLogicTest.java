package teammates.logic.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.UserRole;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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
        verifyPresentInDatastore(frComment);
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
        frComment.commentGiverType = FeedbackParticipantType.STUDENTS;
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
        verifyPresentInDatastore(frComment);
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

        verifyPresentInDatastore(actualFrComment);
        frcLogic.deleteFeedbackResponseComment(actualFrComment.getId());
        verifyAbsentInDatastore(actualFrComment);
    }

    @Test
    public void testDeleteFeedbackResponseComments_deleteByResponseId() {

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q3S1C1");
        verifyPresentInDatastore(frComment);
        frcLogic.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withResponseId(frComment.feedbackResponseId)
                        .build());
        verifyAbsentInDatastore(frComment);
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
    public void testIsResponseCommentVisibleForUser() {
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        CourseRoster roster = new CourseRoster(new ArrayList<>(dataBundle.students.values()),
                new ArrayList<>(dataBundle.instructors.values()));
        List<StudentAttributes> studentsInTeam =
                roster.getTeamToMembersTable().getOrDefault(student.getTeam(), Collections.emptyList());
        Set<String> studentsEmailInTeam = studentsInTeam.stream()
                .map(StudentAttributes::getEmail)
                .collect(Collectors.toSet());

        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        FeedbackQuestionAttributes relatedQuestion = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackResponseCommentAttributes commentFromT1C1ToR1Q1S1C1VisibleToStudent =
                FeedbackResponseCommentAttributes.builder()
                .withCommentGiver("instructor1@course1.tmt")
                .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withVisibilityFollowingFeedbackQuestion(false)
                .withCourseId("idOfTypicalCourse1")
                .withFeedbackSessionName("First feedback session")
                .withFeedbackResponseId("2%student2InCourse1@gmail.tmt%student5InCourse1@gmail.tmt")
                .withShowCommentTo(Arrays.asList(FeedbackParticipantType.STUDENTS))
                .build();

        ______TS("failure: response is null");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, null, relatedQuestion, commentFromT1C1ToR1Q1S1C1VisibleToStudent));

        ______TS("failure: related question is null");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, null, commentFromT1C1ToR1Q1S1C1VisibleToStudent));

        ______TS("success: giver is instructor; show comment to student; comment is visible to response recipient");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromT1C1ToR1Q1S1C1VisibleToStudent));

        ______TS("success: giver is instructor; show comment to student; "
                + "comment is visible to response recipient's teammates");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student4InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromT1C1ToR1Q1S1C1VisibleToStudent));

        assertTrue(frcLogic.isResponseCommentVisibleForUser("student3InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromT1C1ToR1Q1S1C1VisibleToStudent));

        ______TS("success: giver is instructor; comment is visible to giver by default");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("instructor1@course1.tmt", UserRole.INSTRUCTOR,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromT1C1ToR1Q1S1C1VisibleToStudent));

        FeedbackResponseCommentAttributes commentFromS4C1ToR1Q1S1C1VisibleToStudent =
                FeedbackResponseCommentAttributes.builder()
                .withCommentGiver("student4InCourse1@gmail.tmt")
                .withCommentGiverType(FeedbackParticipantType.STUDENTS)
                .withVisibilityFollowingFeedbackQuestion(false)
                .withCourseId("idOfTypicalCourse1")
                .withFeedbackSessionName("First feedback session")
                .withShowCommentTo(Arrays.asList(FeedbackParticipantType.STUDENTS))
                .build();

        ______TS("failure: giver is student; show comment to student; comment is not visible to instructor");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("instructor1@course1.tmt", UserRole.INSTRUCTOR,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromS4C1ToR1Q1S1C1VisibleToStudent));

        ______TS("success: giver is student; show comment to student; comment is visible to comment giver");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student4InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromS4C1ToR1Q1S1C1VisibleToStudent));

        ______TS("success: giver is student; show comment to student; comment is visible to response recipient");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromS4C1ToR1Q1S1C1VisibleToStudent));

        ______TS("success: giver is student; show comment to student; comment is visible to response recipient's teammates");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student3InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromS4C1ToR1Q1S1C1VisibleToStudent));

        FeedbackResponseCommentAttributes commentFromS4C1ToR1Q1S1C1VisibleToNoOneExceptSelf =
                FeedbackResponseCommentAttributes.builder()
                .withCommentGiver("student4InCourse1@gmail.tmt")
                .withCommentGiverType(FeedbackParticipantType.STUDENTS)
                .withVisibilityFollowingFeedbackQuestion(false)
                .withCourseId("idOfTypicalCourse1")
                .withFeedbackSessionName("First feedback session")
                .withShowCommentTo(Collections.emptyList())
                .build();

        ______TS("success: giver is student; do not show comment to anyone except self;"
                + "comment is visible to response giver");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student4InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromS4C1ToR1Q1S1C1VisibleToNoOneExceptSelf));

        ______TS("failure: giver is student; do not show comment to anyone except self;"
                + "comment is not visible to response recipient");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromS4C1ToR1Q1S1C1VisibleToNoOneExceptSelf));

        ______TS("failure: giver is student; do not show comment to anyone except self;"
                + "comment is not visible to response recipient's teammates");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student3InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromS4C1ToR1Q1S1C1VisibleToNoOneExceptSelf));

        ______TS("failure: giver is student; do not show comment to anyone except self;"
                + "comment is not visible to instructor");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("instructor1@course1.tmt", UserRole.INSTRUCTOR,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromS4C1ToR1Q1S1C1VisibleToNoOneExceptSelf));

        FeedbackResponseCommentAttributes commentFromT1C1ToR1Q1S1C1VisibleToNoOneExceptSelf =
                FeedbackResponseCommentAttributes.builder()
                .withCommentGiver("instructor1@course1.tmt")
                .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withVisibilityFollowingFeedbackQuestion(false)
                .withCourseId("idOfTypicalCourse1")
                .withFeedbackSessionName("First feedback session")
                .withShowCommentTo(Collections.emptyList())
                .build();

        ______TS("success: giver is instructor; do not show comment to anyone except self;"
                + "comment is visible to comment giver by default");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("instructor1@course1.tmt", UserRole.INSTRUCTOR,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromT1C1ToR1Q1S1C1VisibleToNoOneExceptSelf));

        ______TS("failure: giver is instructor; do not show comment to anyone except self;"
                + "comment is not visible to other instructor");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("instructor2@course1.tmt", UserRole.INSTRUCTOR,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromT1C1ToR1Q1S1C1VisibleToNoOneExceptSelf));

        ______TS("failure: giver is instructor; do not show comment to anyone except self;"
                + "comment is not visible to response recipient");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromT1C1ToR1Q1S1C1VisibleToNoOneExceptSelf));

        ______TS("failure: giver is instructor; do not show comment to anyone except self;"
                + "comment is not visible to other student");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student3InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion, commentFromT1C1ToR1Q1S1C1VisibleToNoOneExceptSelf));

        FeedbackResponseCommentAttributes commentFromS4C1ToR1Q1S1C1VisibleToTeamMembersInstructor =
                FeedbackResponseCommentAttributes.builder()
                .withCommentGiver("student4InCourse1@gmail.tmt")
                .withCommentGiverType(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                .withVisibilityFollowingFeedbackQuestion(false)
                .withCourseId("idOfTypicalCourse1")
                .withFeedbackSessionName("First feedback session")
                .withShowCommentTo(Arrays.asList(FeedbackParticipantType.OWN_TEAM_MEMBERS,
                        FeedbackParticipantType.INSTRUCTORS))
                .build();

        ______TS("success: giver is student; show comment to own team members, instructors; comment is visible"
                + "to student from same team");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR1Q1S1C1VisibleToTeamMembersInstructor));

        assertTrue(frcLogic.isResponseCommentVisibleForUser("student4InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR1Q1S1C1VisibleToTeamMembersInstructor));

        assertTrue(frcLogic.isResponseCommentVisibleForUser("student3InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR1Q1S1C1VisibleToTeamMembersInstructor));

        ______TS("failure: giver is student; show comment to own team members, instructors; "
                + "comment is not visible to student not from same team");
        student = dataBundle.students.get("student5InCourse1");
        studentsInTeam = roster.getTeamToMembersTable().getOrDefault(student.getTeam(), Collections.emptyList());
        studentsEmailInTeam = studentsInTeam.stream()
                .map(StudentAttributes::getEmail)
                .collect(Collectors.toSet());
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student5InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR1Q1S1C1VisibleToTeamMembersInstructor));

        ______TS("success: giver is student; show comment to own team members, instructors; comment is visible"
                + "to instructor");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("instructor1@course1.tmt", UserRole.INSTRUCTOR,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR1Q1S1C1VisibleToTeamMembersInstructor));

        response = dataBundle.feedbackResponses.get("response2ForQ2S1C1");
        response.recipient = "student1InCourse1@gmail.tmt";
        FeedbackResponseCommentAttributes commentFromS4C1ToR2Q2S1C1VisibleToResponseReceiver =
                FeedbackResponseCommentAttributes.builder()
                .withCommentGiver("student4InCourse1@gmail.tmt")
                .withCommentGiverType(FeedbackParticipantType.GIVER)
                .withVisibilityFollowingFeedbackQuestion(false)
                .withCourseId("idOfTypicalCourse1")
                .withFeedbackSessionName("First feedback session")
                .withShowCommentTo(Arrays.asList(FeedbackParticipantType.RECEIVER))
                .build();

        ______TS("success: giver is student; show comment to response receiver only; "
                + "comment is visible to response recipient");
        student = dataBundle.students.get("student1InCourse1");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibleToResponseReceiver));

        ______TS("failure: giver is student; show comment to response receiver only; "
                + "comment is not visible to response giver");
        student = dataBundle.students.get("student5InCourse1");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student5InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibleToResponseReceiver));

        FeedbackResponseCommentAttributes commentFromS4C1ToR2Q2S1C1VisibleToResponseGiver =
                FeedbackResponseCommentAttributes.builder()
                .withCommentGiver("student4InCourse1@gmail.tmt")
                .withCommentGiverType(FeedbackParticipantType.GIVER)
                .withVisibilityFollowingFeedbackQuestion(false)
                .withCourseId("idOfTypicalCourse1")
                .withFeedbackSessionName("First feedback session")
                .withShowCommentTo(Arrays.asList(FeedbackParticipantType.GIVER))
                .build();

        ______TS("failure: giver is student; show comment to response receiver only; "
                + "comment is not visible to response recipient");
        student = dataBundle.students.get("student1InCourse1");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibleToResponseGiver));

        ______TS("success: giver is student; show comment to response receiver only; "
                + "comment is visible to response giver");
        student = dataBundle.students.get("student5InCourse1");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student5InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibleToResponseGiver));

        // test comment visibility to recipient's team
        response = dataBundle.feedbackResponses.get("response2ForQ2S1C1");
        response.recipient = "student1InCourse1@gmail.tmt";
        FeedbackResponseCommentAttributes commentFromS5C1ToR2Q2S1C1VisibleToResponseReceiverTeamMember =
                FeedbackResponseCommentAttributes.builder()
                .withCommentGiver("student5InCourse1@gmail.tmt")
                .withCommentGiverType(FeedbackParticipantType.GIVER)
                .withVisibilityFollowingFeedbackQuestion(false)
                .withCourseId("idOfTypicalCourse1")
                .withFeedbackSessionName("First feedback session")
                .withShowCommentTo(Arrays.asList(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS))
                .build();

        ______TS("success: giver is student; show comment to comment recipient's team; comment is visible"
                + "to response recipient");
        student = dataBundle.students.get("student1InCourse1");
        studentsInTeam = roster.getTeamToMembersTable().getOrDefault(student.getTeam(), Collections.emptyList());
        studentsEmailInTeam = studentsInTeam.stream()
                .map(StudentAttributes::getEmail)
                .collect(Collectors.toSet());
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS5C1ToR2Q2S1C1VisibleToResponseReceiverTeamMember));

        ______TS("success: giver is student; show comment to comment recipient's team; comment is visible"
                + "to response recipient's team members");
        student = dataBundle.students.get("student3InCourse1");
        studentsInTeam = roster.getTeamToMembersTable().getOrDefault(student.getTeam(), Collections.emptyList());
        studentsEmailInTeam = studentsInTeam.stream()
                .map(StudentAttributes::getEmail)
                .collect(Collectors.toSet());
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student3InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS5C1ToR2Q2S1C1VisibleToResponseReceiverTeamMember));

        student = dataBundle.students.get("student4InCourse1");
        studentsInTeam = roster.getTeamToMembersTable().getOrDefault(student.getTeam(), Collections.emptyList());
        studentsEmailInTeam = studentsInTeam.stream()
                .map(StudentAttributes::getEmail)
                .collect(Collectors.toSet());
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student4InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS5C1ToR2Q2S1C1VisibleToResponseReceiverTeamMember));

        ______TS("failure: giver is student; show comment to comment recipient's team; comment is not visible"
                + "to students not from response recipient's team");
        student = dataBundle.students.get("student1InCourse2");
        studentsInTeam = roster.getTeamToMembersTable().getOrDefault(student.getTeam(), Collections.emptyList());
        studentsEmailInTeam = studentsInTeam.stream()
                .map(StudentAttributes::getEmail)
                .collect(Collectors.toSet());
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student1InCourse2@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS5C1ToR2Q2S1C1VisibleToResponseReceiverTeamMember));

        student = dataBundle.students.get("student1InUnregisteredCourse");
        studentsInTeam = roster.getTeamToMembersTable().getOrDefault(student.getTeam(), Collections.emptyList());
        studentsEmailInTeam = studentsInTeam.stream()
                .map(StudentAttributes::getEmail)
                .collect(Collectors.toSet());
        assertFalse(frcLogic.isResponseCommentVisibleForUser("student1InUnregisteredCourse@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS5C1ToR2Q2S1C1VisibleToResponseReceiverTeamMember));

        // comment visibility follows feedback question's visibility
        FeedbackResponseCommentAttributes commentFromS4C1ToR2Q2S1C1VisibilityFollowsQuestion =
                FeedbackResponseCommentAttributes.builder()
                .withCommentGiver("student4InCourse1@gmail.tmt")
                .withCommentGiverType(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                .withVisibilityFollowingFeedbackQuestion(true)
                .withCourseId("idOfTypicalCourse1")
                .withFeedbackSessionName("First feedback session")
                .build();
        relatedQuestion.setShowResponsesTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS));
        response = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        student = dataBundle.students.get("student1InCourse1");
        studentsInTeam = roster.getTeamToMembersTable().getOrDefault(student.getTeam(), Collections.emptyList());
        studentsEmailInTeam = studentsInTeam.stream()
                .map(StudentAttributes::getEmail)
                .collect(Collectors.toSet());

        ______TS("success: question is visible to instructor; visibility follows feedback question;"
                + "comment is visible to receiver by default");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student1InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibilityFollowsQuestion));

        ______TS("success: question is visible to instructor; visibility follows feedback question;"
                + "comment is visible to giver by default");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student4InCourse1@gmail.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibilityFollowsQuestion));

        ______TS("success: question is visible to instructor; visibility follows feedback question;"
                + "comment is visible to instructor");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("instructor1@course1.tmt", UserRole.INSTRUCTOR,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibilityFollowsQuestion));

        ______TS("failure: question is visible to instructor; visibility follows feedback question;"
                + "comment is visible to teammate");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("instructor3@course1.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibilityFollowsQuestion));

        relatedQuestion.setShowResponsesTo(Arrays.asList(FeedbackParticipantType.STUDENTS));

        ______TS("success: question is visible to instructor; visibility follows feedback question;"
                + "comment is visible to recipient");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student1@course1.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibilityFollowsQuestion));

        ______TS("success: question is visible to instructor; visibility follows feedback question;"
                + "comment is visible to teammate");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student3@course1.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibilityFollowsQuestion));

        ______TS("success: question is visible to instructor; visibility follows feedback question;"
                + "comment is visible to student not from the same team");
        assertTrue(frcLogic.isResponseCommentVisibleForUser("student2@course1.tmt", UserRole.STUDENT,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibilityFollowsQuestion));

        ______TS("failure: question is visible to instructor; visibility follows feedback question;"
                + "comment is not visible to instructor");
        assertFalse(frcLogic.isResponseCommentVisibleForUser("instructor3@course1.tmt", UserRole.INSTRUCTOR,
                student, studentsEmailInTeam, response, relatedQuestion,
                commentFromS4C1ToR2Q2S1C1VisibilityFollowsQuestion));

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
