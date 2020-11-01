package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.UserRole;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.test.AssertHelper;

/**
 * SUT: {@link FeedbackResponsesLogic}.
 */
public class FeedbackResponsesLogicTest extends BaseLogicTest {

    private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static StudentsLogic studentsLogic = StudentsLogic.inst();

    private DataBundle questionTypeBundle;

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        questionTypeBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");

        removeAndRestoreTypicalDataBundle();
        // extra test data used on top of typical data bundle
        removeAndRestoreDataBundle(loadDataBundle("/SpecialCharacterTest.json"));
        removeAndRestoreDataBundle(questionTypeBundle);
    }

    @Test
    public void testAreThereResponsesForQuestion() {
        FeedbackQuestionAttributes questionWithResponse;
        FeedbackQuestionAttributes questionWithoutResponse;

        ______TS("Check that a question has some responses");

        questionWithResponse = getQuestionFromDatastore("qn1InSession1InCourse2");
        assertTrue(frLogic.areThereResponsesForQuestion(questionWithResponse.getId()));

        ______TS("Check that a question has no responses");

        questionWithoutResponse = getQuestionFromDatastore("qn2InSession1InCourse2");
        assertFalse(frLogic.areThereResponsesForQuestion(questionWithoutResponse.getId()));
    }

    @Test
    public void testUpdateFeedbackResponseCascade() throws Exception {

        ______TS("success: standard update");

        FeedbackResponseAttributes responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");

        FeedbackResponseDetails frd = new FeedbackTextResponseDetails("Updated Response");

        frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(responseToUpdate.getId())
                        .withResponseDetails(frd)
                        .build());

        responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");

        assertEquals(responseToUpdate.toString(),
                frLogic.getFeedbackResponse(responseToUpdate.getId()).toString());

        ______TS("failure: recipient one that is already exists");

        responseToUpdate = getResponseFromDatastore("response1ForQ2S1C1");

        FeedbackResponseAttributes existingResponse =
                FeedbackResponseAttributes.builder(
                        responseToUpdate.getFeedbackQuestionId(), responseToUpdate.getGiver(), "student3InCourse1@gmail.tmt")
                .withFeedbackSessionName(responseToUpdate.feedbackSessionName)
                .withCourseId(responseToUpdate.courseId)
                .withGiverSection(responseToUpdate.giverSection)
                .withRecipientSection(responseToUpdate.recipientSection)
                .withResponseDetails(responseToUpdate.responseDetails)
                .build();

        frLogic.createFeedbackResponse(existingResponse);

        FeedbackResponseAttributes[] finalResponse = new FeedbackResponseAttributes[] { responseToUpdate };
        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> frLogic.updateFeedbackResponseCascade(
                        FeedbackResponseAttributes.updateOptionsBuilder(finalResponse[0].getId())
                                .withRecipient("student3InCourse1@gmail.tmt")
                                .build()));
        AssertHelper.assertContains("Trying to create an entity that exists", eaee.getMessage());

        ______TS("success: recipient changed to something else");

        responseToUpdate.recipient = "student5InCourse1@gmail.tmt";

        frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(responseToUpdate.getId())
                        .withRecipient(responseToUpdate.recipient)
                        .build());

        assertEquals(responseToUpdate.toString(),
                frLogic.getFeedbackResponse(responseToUpdate.feedbackQuestionId, responseToUpdate.giver,
                responseToUpdate.recipient).toString());
        assertNull(frLogic.getFeedbackResponse(
                responseToUpdate.feedbackQuestionId, responseToUpdate.giver, "student2InCourse1@gmail.tmt"));

        ______TS("success: both giver and recipient changed (teammate changed response)");

        responseToUpdate = getResponseFromDatastore("response1GracePeriodFeedback");
        responseToUpdate.giver = "student5InCourse1@gmail.tmt";
        responseToUpdate.recipient = "Team 1.1";

        assertNotNull(frLogic.getFeedbackResponse(
                responseToUpdate.feedbackQuestionId, "student4InCourse1@gmail.tmt", "Team 1.2"));

        frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(responseToUpdate.getId())
                        .withGiver(responseToUpdate.giver)
                        .withRecipient(responseToUpdate.recipient)
                        .build());

        assertEquals(responseToUpdate.toString(),
                frLogic.getFeedbackResponse(responseToUpdate.feedbackQuestionId, responseToUpdate.giver,
                responseToUpdate.recipient).toString());
        assertNull(frLogic.getFeedbackResponse(
                responseToUpdate.feedbackQuestionId, "student4InCourse1@gmail.tmt", "Team 1.2"));

        ______TS("success: update giver, recipient, giverSection and recipientSection, "
                + "should do cascade update to comments");

        responseToUpdate = getResponseFromDatastore("response1ForQ1S1C1");
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(responseToUpdate.getId()).isEmpty());

        FeedbackResponseAttributes updatedResponse = frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(responseToUpdate.getId())
                        .withGiver("test@example.com")
                        .withGiverSection("giverSection")
                        .withRecipient("test@example.com")
                        .withRecipientSection("recipientSection")
                        .build());
        assertEquals("test@example.com", updatedResponse.giver);
        assertEquals("giverSection", updatedResponse.giverSection);
        assertEquals("test@example.com", updatedResponse.recipient);
        assertEquals("recipientSection", updatedResponse.recipientSection);
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(responseToUpdate.getId()).isEmpty());
        List<FeedbackResponseCommentAttributes> associatedComments =
                frcLogic.getFeedbackResponseCommentForResponse(updatedResponse.getId());
        assertFalse(associatedComments.isEmpty());
        assertTrue(associatedComments.stream()
                .allMatch(c -> "giverSection".equals(c.giverSection) && "recipientSection".equals(c.receiverSection)));

        ______TS("failure: invalid params");

        // Cannot have invalid params as all possible invalid params
        // are copied over from an existing response.

        ______TS("failure: no such response");

        assertThrows(EntityDoesNotExistException.class,
                () -> frLogic.updateFeedbackResponseCascade(
                        FeedbackResponseAttributes.updateOptionsBuilder("non-existent")
                                .withGiver("random")
                                .build()));
    }

    @Test
    public void testUpdateFeedbackResponsesForChangingTeam_typicalData_shouldDoCascadeDeletion() throws Exception {

        StudentAttributes studentToUpdate = dataBundle.students.get("student4InCourse1");

        // Student 4 has 1 responses to him from team members,
        // 1 response from him a team member, and
        // 1 team response from him to another team.
        FeedbackQuestionAttributes teamQuestion = getQuestionFromDatastore("team.members.feedback");
        assertEquals(1, getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size());
        assertEquals(1,
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size());

        teamQuestion = getQuestionFromDatastore("team.feedback");
        assertEquals(1,
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size());

        // Add one more non-team response
        FeedbackResponseAttributes responseToAdd =
                FeedbackResponseAttributes.builder(
                        getQuestionFromDatastore("qn1InSession1InCourse1").getId(),
                        studentToUpdate.email, studentToUpdate.email)
                .withFeedbackSessionName("First feedback session")
                .withCourseId("idOfTypicalCourse1")
                .withGiverSection("Section 1")
                .withRecipientSection("Section 1")
                .withResponseDetails(new FeedbackTextResponseDetails("New Response to self"))
                .build();

        frLogic.createFeedbackResponse(responseToAdd);

        // All these responses should be gone after he changes teams

        frLogic.updateFeedbackResponsesForChangingTeam(
                studentToUpdate.course, studentToUpdate.email, studentToUpdate.team, "Team 1.2");

        teamQuestion = getQuestionFromDatastore("team.members.feedback");
        assertEquals(0, getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size());
        assertEquals(0,
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size());

        teamQuestion = getQuestionFromDatastore("team.feedback");
        assertEquals(0, getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size());

        // Non-team response should remain

        assertEquals(1,
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                getQuestionFromDatastore("qn1InSession1InCourse1").getId(),
                studentToUpdate.email).size());
    }

    /**
     * Gets all responses for a recipient of a question.
     */
    private List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion(
            String questionId, String receiver) {
        List<FeedbackResponseAttributes> allResponses = frLogic.getFeedbackResponsesForQuestion(questionId);
        return allResponses.stream()
                .filter(response -> response.getRecipient().equals(receiver))
                .collect(Collectors.toList());
    }

    @Test
    public void testUpdateFeedbackResponsesForChangingTeam_deleteLastResponse_decreaseResponseRate()
            throws Exception {
        FeedbackResponseAttributes responseShouldBeDeleted =
                getResponseFromDatastore(questionTypeBundle, "response1ForQ1ContribSession2Course2");
        // make sure it's the last response by the student
        assertEquals(1, numResponsesFromGiverInSession(responseShouldBeDeleted.giver,
                                                       responseShouldBeDeleted.feedbackSessionName,
                                                       responseShouldBeDeleted.courseId));
        StudentAttributes student = questionTypeBundle.students.get("student2InCourse2");
        // the response is given by the student
        assertEquals(student.getEmail(), responseShouldBeDeleted.giver);

        int originalResponseRate = getResponseRate(responseShouldBeDeleted.feedbackSessionName,
                                                   responseShouldBeDeleted.courseId);

        frLogic.updateFeedbackResponsesForChangingTeam(student.getCourse(), student.getEmail(), student.getTeam(),
                student.getTeam() + "tmp");

        int responseRateAfterDeletion = getResponseRate(responseShouldBeDeleted.feedbackSessionName,
                                                        responseShouldBeDeleted.courseId);
        assertEquals(originalResponseRate - 1, responseRateAfterDeletion);
    }

    @Test
    public void testUpdateFeedbackResponsesForChangingTeam_noResponseShouldBeDeleted_shouldReaminSameResponseRate()
            throws Exception {
        FeedbackResponseAttributes responseShouldBeDeleted =
                getResponseFromDatastore(questionTypeBundle, "response1ForQ1RankSession");
        // make sure it's not the last response by the student
        assertTrue(1 < numResponsesFromGiverInSession(responseShouldBeDeleted.giver,
                                                      responseShouldBeDeleted.feedbackSessionName,
                                                      responseShouldBeDeleted.courseId));
        StudentAttributes student = questionTypeBundle.students.get("student1InCourse1");
        // the response is given by the student
        assertEquals(student.getEmail(), responseShouldBeDeleted.giver);

        int originalResponseRate = getResponseRate(responseShouldBeDeleted.feedbackSessionName,
                                                   responseShouldBeDeleted.courseId);

        frLogic.updateFeedbackResponsesForChangingTeam(student.getCourse(), student.getEmail(), student.getTeam(),
                student.getTeam() + "tmp");

        int responseRateAfterDeletion = getResponseRate(responseShouldBeDeleted.feedbackSessionName,
                                                        responseShouldBeDeleted.courseId);
        assertEquals(originalResponseRate, responseRateAfterDeletion);
    }

    private int numResponsesFromGiverInSession(String studentEmail, String sessionName, String courseId) {
        int numResponses = 0;
        for (FeedbackResponseAttributes response : questionTypeBundle.feedbackResponses.values()) {
            if (response.giver.equals(studentEmail) && response.feedbackSessionName.equals(sessionName)
                    && response.courseId.equals(courseId)) {
                numResponses++;
            }
        }
        return numResponses;
    }

    private int getResponseRate(String sessionName, String courseId) {
        FeedbackSessionAttributes sessionFromDataStore = fsLogic.getFeedbackSession(sessionName, courseId);
        return fsLogic.getActualTotalSubmission(sessionFromDataStore);
    }

    @Test
    public void testUpdateFeedbackResponsesForChangingEmail() throws Exception {
        ______TS("standard update email case");

        // Student 1 currently has 11 responses to him and 2 from himself.
        // Student 1 currently has 1 response comment for responses from instructor to him
        // and 1 response comment from responses from himself.
        StudentAttributes studentToUpdate = questionTypeBundle.students.get("student2InCourse1");
        List<FeedbackResponseAttributes> responsesForReceiver =
                frLogic.getFeedbackResponsesForReceiverForCourse(
                        studentToUpdate.course, studentToUpdate.email);
        List<FeedbackResponseAttributes> responsesFromGiver =
                frLogic.getFeedbackResponsesFromGiverForCourse(
                        studentToUpdate.course, studentToUpdate.email);
        Set<String> responseIdsToAndFromStudent = new HashSet<>();
        responseIdsToAndFromStudent.addAll(
                responsesForReceiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        responseIdsToAndFromStudent.addAll(
                responsesFromGiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        List<FeedbackResponseCommentAttributes> responseCommentsForStudent =
                getFeedbackResponseCommentsForResponsesFromDatastore(responseIdsToAndFromStudent);

        assertEquals(11, responsesForReceiver.size());
        assertEquals(8, responsesFromGiver.size());
        assertEquals(2, responseCommentsForStudent.size());
        // student's comment
        assertTrue(responseCommentsForStudent.stream().anyMatch(r -> r.isCommentFromFeedbackParticipant));
        // instructor comment
        assertTrue(responseCommentsForStudent.stream().anyMatch(r -> !r.isCommentFromFeedbackParticipant));

        frLogic.updateFeedbackResponsesForChangingEmail(
                studentToUpdate.course, studentToUpdate.email, "new@email.tmt");

        responsesForReceiver = frLogic.getFeedbackResponsesForReceiverForCourse(
                studentToUpdate.course, studentToUpdate.email);
        responsesFromGiver = frLogic.getFeedbackResponsesFromGiverForCourse(
                studentToUpdate.course, studentToUpdate.email);
        responseIdsToAndFromStudent = new HashSet<>();
        responseIdsToAndFromStudent.addAll(
                responsesForReceiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        responseIdsToAndFromStudent.addAll(
                responsesFromGiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        responseCommentsForStudent =
                getFeedbackResponseCommentsForResponsesFromDatastore(responseIdsToAndFromStudent);

        assertEquals(0, responsesForReceiver.size());
        assertEquals(0, responsesFromGiver.size());
        assertEquals(0, responseCommentsForStudent.size());

        responsesForReceiver = frLogic.getFeedbackResponsesForReceiverForCourse(
                studentToUpdate.course, "new@email.tmt");
        responsesFromGiver = frLogic.getFeedbackResponsesFromGiverForCourse(
                studentToUpdate.course, "new@email.tmt");
        responseIdsToAndFromStudent = new HashSet<>();
        responseIdsToAndFromStudent.addAll(
                responsesForReceiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        responseIdsToAndFromStudent.addAll(
                responsesFromGiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        responseCommentsForStudent =
                getFeedbackResponseCommentsForResponsesFromDatastore(responseIdsToAndFromStudent);

        assertEquals(11, responsesForReceiver.size());
        assertEquals(8, responsesFromGiver.size());
        assertEquals(2, responseCommentsForStudent.size());
    }

    @Test
    public void testIsNameVisibleTo() {

        ______TS("testIsNameVisibleTo");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        StudentAttributes student2 = dataBundle.students.get("student2InCourse1");
        StudentAttributes student3 = dataBundle.students.get("student3InCourse1");
        StudentAttributes student5 = dataBundle.students.get("student5InCourse1");

        FeedbackQuestionAttributes fq = getQuestionFromDatastore("qn3InSession1InCourse1");
        FeedbackResponseAttributes fr = getResponseFromDatastore("response1ForQ3S1C1");

        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(fq.courseId),
                instructorsLogic.getInstructorsForCourse(fq.courseId));

        assertTrue(frLogic.isNameVisibleToUser(fq, fr, instructor.email, UserRole.INSTRUCTOR, true, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, instructor.email, UserRole.INSTRUCTOR, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.email, UserRole.STUDENT, false, roster));

        ______TS("test if visible to own team members");

        fr.giver = student.email;
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.email, UserRole.STUDENT, false, roster));

        ______TS("test if visible to receiver/reciever team members");

        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.showRecipientNameTo.clear();
        fq.showRecipientNameTo.add(FeedbackParticipantType.RECEIVER);
        fr.recipient = student.team;
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.email, UserRole.STUDENT, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student3.email, UserRole.STUDENT, false, roster));

        fq.recipientType = FeedbackParticipantType.STUDENTS;
        fr.recipient = student.email;
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.email, UserRole.STUDENT, false, roster));
        assertFalse(frLogic.isNameVisibleToUser(fq, fr, student2.email, UserRole.STUDENT, false, roster));

        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.showRecipientNameTo.clear();
        fq.showRecipientNameTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        fr.recipient = student.team;
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.email, UserRole.STUDENT, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student3.email, UserRole.STUDENT, false, roster));

        fq.recipientType = FeedbackParticipantType.STUDENTS;
        fr.recipient = student.email;
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.email, UserRole.STUDENT, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student3.email, UserRole.STUDENT, false, roster));
        assertFalse(frLogic.isNameVisibleToUser(fq, fr, student5.email, UserRole.STUDENT, false, roster));

        ______TS("test if visible to receiver/giver team members for team questions");

        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.showRecipientNameTo.clear();
        fq.showRecipientNameTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        fq.giverType = FeedbackParticipantType.TEAMS;
        fq.showGiverNameTo.clear();
        fq.showGiverNameTo.add(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);

        fr.recipient = student5.team;
        fr.giver = student.team;
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.email, UserRole.STUDENT, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student3.email, UserRole.STUDENT, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student5.email, UserRole.STUDENT, false, roster));

        fr.giver = student.email;
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.email, UserRole.STUDENT, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student3.email, UserRole.STUDENT, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student5.email, UserRole.STUDENT, false, roster));

        ______TS("test anonymous team recipients");
        // Only members of the recipient team should be able to see the recipient name
        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.showRecipientNameTo.clear();
        fq.showRecipientNameTo.add(FeedbackParticipantType.RECEIVER);
        fq.showResponsesTo.add(FeedbackParticipantType.STUDENTS);
        fr.recipient = "Team 1.1";
        assertFalse(frLogic.isNameVisibleToUser(fq, fr, student5.email, UserRole.STUDENT, false, roster));

        ______TS("null question");

        assertFalse(frLogic.isNameVisibleToUser(null, fr, student.email, UserRole.STUDENT, false, roster));

    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_shouldDeleteRelatedResponses() throws Exception {
        StudentAttributes studentToDelete = dataBundle.students.get("student1InCourse1");
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");

        // the responses also have some associated comments
        List<FeedbackResponseAttributes> remainingResponses = new ArrayList<>();
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));
        assertFalse(remainingResponses.isEmpty());

        // the student has some responses
        List<FeedbackResponseAttributes> responsesForStudent1 =
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email);
        responsesForStudent1
                .addAll(
                        frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));
        assertFalse(responsesForStudent1.isEmpty());
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(session1InCourse1.getCourseId(),
                        session1InCourse1.getFeedbackSessionName()).contains(studentToDelete.email));

        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(
                studentToDelete.getCourse(), studentToDelete.getEmail());

        // responses should be deleted
        remainingResponses = new ArrayList<>();
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));
        assertEquals(0, remainingResponses.size());

        // comments should also be deleted
        List<FeedbackResponseCommentAttributes> remainingComments = new ArrayList<>();
        for (FeedbackResponseAttributes response : responsesForStudent1) {
            remainingComments.addAll(frcLogic.getFeedbackResponseCommentForResponse(response.getId()));
        }
        assertEquals(0, remainingComments.size());

        // the student no longer has responses for the session
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(session1InCourse1.getCourseId(),
                        session1InCourse1.getFeedbackSessionName()).contains(studentToDelete.email));
    }

    @Test
    public void testDeleteFeedbackResponseCascade() {
        ______TS("non-existent response");

        // should pass silently
        frLogic.deleteFeedbackResponseCascade("not-exist");

        ______TS("standard delete");

        FeedbackResponseAttributes fra = getResponseFromDatastore("response1ForQ1S1C1");
        assertNotNull(fra);
        // the response has comments
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(fra.getId()).isEmpty());

        frLogic.deleteFeedbackResponseCascade(fra.getId());

        assertNull(frLogic.getFeedbackResponse(fra.getId()));
        // associated comments are deleted
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(fra.getId()).isEmpty());
    }

    @Test
    public void testDeleteFeedbackResponses_byCourseId() {
        ______TS("standard delete");

        // test that responses are deleted
        String courseId = "idOfTypicalCourse1";
        assertFalse(frLogic.getFeedbackResponsesForSession("First feedback session", courseId).isEmpty());
        assertFalse(frLogic.getFeedbackResponsesForSession("Grace Period Session", courseId).isEmpty());
        assertFalse(frLogic.getFeedbackResponsesForSession("Closed Session", courseId).isEmpty());

        frLogic.deleteFeedbackResponses(
                AttributesDeletionQuery.builder()
                        .withCourseId(courseId)
                        .build());

        assertEquals(0, frLogic.getFeedbackResponsesForSession("First feedback session", courseId).size());
        assertEquals(0, frLogic.getFeedbackResponsesForSession("Grace Period Session", courseId).size());
        assertEquals(0, frLogic.getFeedbackResponsesForSession("Closed Session", courseId).size());

        // test that responses from other courses are unaffected
        String otherCourse = "idOfTypicalCourse2";
        assertFalse(frLogic.getFeedbackResponsesForSession("Instructor feedback session", otherCourse).isEmpty());
    }

    @Test
    public void testDeleteFeedbackResponsesForQuestionCascade_studentsQuestion_shouldUpdateRespondents() throws Exception {
        FeedbackResponseAttributes fra = getResponseFromDatastore("response1ForQ1S1C1");

        // this is the only response the student has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.courseId, fra.giver).stream()
                .filter(response -> response.feedbackSessionName.equals(fra.feedbackSessionName))
                .count());
        // the student has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.giver));

        frLogic.deleteFeedbackResponsesForQuestionCascade(fra.feedbackQuestionId);

        // there is no student X as respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.giver));
    }

    @Test
    public void testDeleteFeedbackResponsesForQuestionCascade_instructorsQuestion_shouldUpdateRespondents()
            throws Exception {
        FeedbackResponseAttributes fra = getResponseFromDatastore("response1ForQ3S1C1");

        // this is the only response the instructor has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.courseId, fra.giver).stream()
                .filter(response -> response.feedbackSessionName.equals(fra.feedbackSessionName))
                .count());
        // the instructor has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.giver));

        frLogic.deleteFeedbackResponsesForQuestionCascade(fra.feedbackQuestionId);

        // there is not instructor X in instructor respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.giver));
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_giverIsStudent_shouldUpdateRespondents()
            throws Exception {
        FeedbackResponseAttributes fra = getResponseFromDatastore("response3ForQ2S1C1");
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        // giver is student
        assertEquals(FeedbackParticipantType.STUDENTS, fqLogic.getFeedbackQuestion(fra.feedbackQuestionId).getGiverType());
        // student is the recipient
        assertEquals(fra.recipient, student2InCourse1.getEmail());

        // this is the only response the giver has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.courseId, fra.giver).stream()
                .filter(response -> response.feedbackSessionName.equals(fra.feedbackSessionName))
                .count());
        // the student has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.giver));

        // after the giver is removed from the course
        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(
                student2InCourse1.getCourse(), student2InCourse1.getEmail());

        // there is no student X as respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.giver));
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_giverIsInstructor_shouldUpdateRespondents()
            throws Exception {
        FeedbackResponseAttributes fra = getResponseFromDatastore("response1ForQ1S2C2");
        StudentAttributes student1InCourse2 = dataBundle.students.get("student1InCourse2");
        // giver is instructor
        assertEquals(FeedbackParticipantType.SELF,
                fqLogic.getFeedbackQuestion(fra.feedbackQuestionId).getGiverType());
        // student is the recipient
        assertEquals(fra.recipient, student1InCourse2.getEmail());

        // this is the only response the instructor has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.courseId, fra.giver).stream()
                .filter(response -> response.feedbackSessionName.equals(fra.feedbackSessionName))
                .count());
        // the instructor has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.giver));

        // after the giver is removed from the course
        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(
                student1InCourse2.getCourse(), student1InCourse2.getEmail());

        // there is no instructor X as respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.giver));
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_shouldDeleteRelevantResponsesAsRecipient()
            throws Exception {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        FeedbackResponseAttributes fra1ReceivedByTeam = getResponseFromDatastore("response1ForQ1S2C1");
        frcLogic.createFeedbackResponseComment(
                FeedbackResponseCommentAttributes
                        .builder()
                        .withCourseId(fra1ReceivedByTeam.getCourseId())
                        .withFeedbackSessionName(fra1ReceivedByTeam.getFeedbackSessionName())
                        .withCommentGiver(instructor1OfCourse1.getEmail())
                        .withCommentText("Comment 1")
                        .withFeedbackQuestionId(fra1ReceivedByTeam.getFeedbackQuestionId())
                        .withFeedbackResponseId(fra1ReceivedByTeam.getId())
                        .withGiverSection(fra1ReceivedByTeam.getGiverSection())
                        .withReceiverSection(fra1ReceivedByTeam.getRecipientSection())
                        .withCommentFromFeedbackParticipant(false)
                        .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                        .withVisibilityFollowingFeedbackQuestion(false)
                        .build());
        FeedbackResponseAttributes fra2ReceivedByTeam = getResponseFromDatastore("response1GracePeriodFeedback");
        frcLogic.createFeedbackResponseComment(
                FeedbackResponseCommentAttributes
                        .builder()
                        .withCourseId(fra2ReceivedByTeam.getCourseId())
                        .withFeedbackSessionName(fra2ReceivedByTeam.getFeedbackSessionName())
                        .withCommentGiver(instructor1OfCourse1.getEmail())
                        .withCommentText("Comment 2")
                        .withFeedbackQuestionId(fra2ReceivedByTeam.getFeedbackQuestionId())
                        .withFeedbackResponseId(fra2ReceivedByTeam.getId())
                        .withGiverSection(fra2ReceivedByTeam.getGiverSection())
                        .withReceiverSection(fra2ReceivedByTeam.getRecipientSection())
                        .withCommentFromFeedbackParticipant(false)
                        .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                        .withVisibilityFollowingFeedbackQuestion(false)
                        .build());

        String teamName = "Team 1.2";
        assertEquals(teamName, fra1ReceivedByTeam.getRecipient());
        assertEquals(teamName, fra2ReceivedByTeam.getRecipient());

        // both responses got some comments
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(fra1ReceivedByTeam.getId()).isEmpty());
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(fra2ReceivedByTeam.getId()).isEmpty());

        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(fra1ReceivedByTeam.getCourseId(), teamName);

        // responses received by the team should be deleted
        assertNull(frLogic.getFeedbackResponse(fra1ReceivedByTeam.getId()));
        assertNull(frLogic.getFeedbackResponse(fra2ReceivedByTeam.getId()));

        // their associated comments should be deleted
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(fra1ReceivedByTeam.getId()).isEmpty());
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(fra2ReceivedByTeam.getId()).isEmpty());
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_shouldDeleteRelevantResponsesAsGiver()
            throws Exception {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student4InCourse1 = dataBundle.students.get("student4InCourse1");

        // the following two responses are given by student4InCourse1 as a representative of his team
        FeedbackResponseAttributes fra1GivenByTeam = getResponseFromDatastore("response1ForQ1S2C1");
        // update the response's giver to the team name
        fra1GivenByTeam = frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(fra1GivenByTeam.getId())
                        .withGiver(student4InCourse1.getTeam())
                        .build());
        frcLogic.createFeedbackResponseComment(
                FeedbackResponseCommentAttributes
                        .builder()
                        .withCourseId(fra1GivenByTeam.getCourseId())
                        .withFeedbackSessionName(fra1GivenByTeam.getFeedbackSessionName())
                        .withCommentGiver(instructor1OfCourse1.getEmail())
                        .withCommentText("Comment 1")
                        .withFeedbackQuestionId(fra1GivenByTeam.getFeedbackQuestionId())
                        .withFeedbackResponseId(fra1GivenByTeam.getId())
                        .withGiverSection(fra1GivenByTeam.getGiverSection())
                        .withReceiverSection(fra1GivenByTeam.getRecipientSection())
                        .withCommentFromFeedbackParticipant(false)
                        .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                        .withVisibilityFollowingFeedbackQuestion(false)
                        .build());
        FeedbackResponseAttributes fra2GivenByTeam = getResponseFromDatastore("response1GracePeriodFeedback");
        // update the response's giver to the team name
        fra2GivenByTeam = frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(fra2GivenByTeam.getId())
                        .withGiver(student4InCourse1.getTeam())
                        .build());
        frcLogic.createFeedbackResponseComment(
                FeedbackResponseCommentAttributes
                        .builder()
                        .withCourseId(fra2GivenByTeam.getCourseId())
                        .withFeedbackSessionName(fra2GivenByTeam.getFeedbackSessionName())
                        .withCommentGiver(instructor1OfCourse1.getEmail())
                        .withCommentText("Comment 2")
                        .withFeedbackQuestionId(fra2GivenByTeam.getFeedbackQuestionId())
                        .withFeedbackResponseId(fra2GivenByTeam.getId())
                        .withGiverSection(fra2GivenByTeam.getGiverSection())
                        .withReceiverSection(fra2GivenByTeam.getRecipientSection())
                        .withCommentFromFeedbackParticipant(false)
                        .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                        .withVisibilityFollowingFeedbackQuestion(false)
                        .build());

        String teamName = student4InCourse1.getTeam();
        assertEquals(teamName, fra1GivenByTeam.getGiver());
        assertEquals(teamName, fra2GivenByTeam.getGiver());

        // both responses got some comments
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(fra1GivenByTeam.getId()).isEmpty());
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(fra2GivenByTeam.getId()).isEmpty());

        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(fra1GivenByTeam.getCourseId(), teamName);

        // responses received by the team should be deleted
        assertNull(frLogic.getFeedbackResponse(fra1GivenByTeam.getId()));
        assertNull(frLogic.getFeedbackResponse(fra2GivenByTeam.getId()));

        // their associated comments should be deleted
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(fra1GivenByTeam.getId()).isEmpty());
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(fra2GivenByTeam.getId()).isEmpty());
    }

    private FeedbackQuestionAttributes getQuestionFromDatastore(DataBundle dataBundle, String jsonId) {
        FeedbackQuestionAttributes questionToGet = dataBundle.feedbackQuestions.get(jsonId);
        questionToGet = fqLogic.getFeedbackQuestion(questionToGet.feedbackSessionName,
                                                    questionToGet.courseId,
                                                    questionToGet.questionNumber);

        return questionToGet;
    }

    private FeedbackQuestionAttributes getQuestionFromDatastore(String jsonId) {
        return getQuestionFromDatastore(dataBundle, jsonId);
    }

    private FeedbackResponseAttributes getResponseFromDatastore(DataBundle dataBundle, String jsonId) {
        FeedbackResponseAttributes response =
                                        dataBundle.feedbackResponses.get(jsonId);

        String qnId;
        try {
            int qnNumber = Integer.parseInt(response.feedbackQuestionId);
            qnId = fqLogic.getFeedbackQuestion(response.feedbackSessionName, response.courseId, qnNumber).getId();
        } catch (NumberFormatException e) {
            qnId = response.feedbackQuestionId;
        }

        return frLogic.getFeedbackResponse(
                qnId, response.giver, response.recipient);
    }

    private FeedbackResponseAttributes getResponseFromDatastore(String jsonId) {
        return getResponseFromDatastore(dataBundle, jsonId);
    }

    private List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForResponsesFromDatastore(
            Set<String> responseIds) {
        List<FeedbackResponseCommentAttributes> responseComments = new ArrayList<>();
        for (String id : responseIds) {
            List<FeedbackResponseCommentAttributes> responseCommentsForResponse =
                    frcLogic.getFeedbackResponseCommentForResponse(id);
            responseComments.addAll(responseCommentsForResponse);
        }
        return responseComments;
    }
}
