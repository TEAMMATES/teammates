package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SessionResultsBundle;
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

    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();

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

        questionWithResponse = getQuestionFromDatabase("qn1InSession1InCourse2");
        assertTrue(frLogic.areThereResponsesForQuestion(questionWithResponse.getId()));

        ______TS("Check that a question has no responses");

        questionWithoutResponse = getQuestionFromDatabase("qn2InSession1InCourse2");
        assertFalse(frLogic.areThereResponsesForQuestion(questionWithoutResponse.getId()));
    }

    @Test
    public void testUpdateFeedbackResponseCascade() throws Exception {

        ______TS("success: standard update");

        FeedbackResponseAttributes responseToUpdate = getResponseFromDatabase("response1ForQ2S1C1");

        FeedbackResponseDetails frd = new FeedbackTextResponseDetails("Updated Response");

        frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(responseToUpdate.getId())
                        .withResponseDetails(frd)
                        .build());

        responseToUpdate = getResponseFromDatabase("response1ForQ2S1C1");

        assertEquals(responseToUpdate.toString(),
                frLogic.getFeedbackResponse(responseToUpdate.getId()).toString());

        ______TS("failure: recipient one that is already exists");

        responseToUpdate = getResponseFromDatabase("response1ForQ2S1C1");

        FeedbackResponseAttributes existingResponse =
                FeedbackResponseAttributes.builder(
                        responseToUpdate.getFeedbackQuestionId(), responseToUpdate.getGiver(), "student3InCourse1@gmail.tmt")
                .withFeedbackSessionName(responseToUpdate.getFeedbackSessionName())
                .withCourseId(responseToUpdate.getCourseId())
                .withGiverSection(responseToUpdate.getGiverSection())
                .withRecipientSection(responseToUpdate.getRecipientSection())
                .withResponseDetails(responseToUpdate.getResponseDetails())
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

        responseToUpdate.setRecipient("student5InCourse1@gmail.tmt");

        frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(responseToUpdate.getId())
                        .withRecipient(responseToUpdate.getRecipient())
                        .build());

        assertEquals(responseToUpdate.toString(),
                frLogic.getFeedbackResponse(responseToUpdate.getFeedbackQuestionId(), responseToUpdate.getGiver(),
                        responseToUpdate.getRecipient()).toString());
        assertNull(frLogic.getFeedbackResponse(
                responseToUpdate.getFeedbackQuestionId(), responseToUpdate.getGiver(), "student2InCourse1@gmail.tmt"));

        ______TS("success: update giver, recipient, giverSection and recipientSection, "
                + "should do cascade update to comments");

        responseToUpdate = getResponseFromDatabase("response1ForQ1S1C1");
        assertFalse(frcLogic.getFeedbackResponseCommentForResponse(responseToUpdate.getId()).isEmpty());

        FeedbackResponseAttributes updatedResponse = frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(responseToUpdate.getId())
                        .withGiver("test@example.com")
                        .withGiverSection("giverSection")
                        .withRecipient("test@example.com")
                        .withRecipientSection("recipientSection")
                        .build());
        assertEquals("test@example.com", updatedResponse.getGiver());
        assertEquals("giverSection", updatedResponse.getGiverSection());
        assertEquals("test@example.com", updatedResponse.getRecipient());
        assertEquals("recipientSection", updatedResponse.getRecipientSection());
        assertTrue(frcLogic.getFeedbackResponseCommentForResponse(responseToUpdate.getId()).isEmpty());
        List<FeedbackResponseCommentAttributes> associatedComments =
                frcLogic.getFeedbackResponseCommentForResponse(updatedResponse.getId());
        assertFalse(associatedComments.isEmpty());
        assertTrue(associatedComments.stream()
                .allMatch(c -> "giverSection".equals(c.getGiverSection())
                        && "recipientSection".equals(c.getReceiverSection())));

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
        FeedbackQuestionAttributes teamQuestion = getQuestionFromDatabase("team.members.feedback");
        assertEquals(1, getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.getEmail()).size());
        assertEquals(1,
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                teamQuestion.getId(), studentToUpdate.getEmail()).size());

        teamQuestion = getQuestionFromDatabase("team.feedback");
        assertEquals(1,
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                teamQuestion.getId(), studentToUpdate.getTeam()).size());

        // Add one more non-team response
        FeedbackResponseAttributes responseToAdd =
                FeedbackResponseAttributes.builder(
                        getQuestionFromDatabase("qn1InSession1InCourse1").getId(),
                        studentToUpdate.getEmail(), studentToUpdate.getEmail())
                .withFeedbackSessionName("First feedback session")
                .withCourseId("idOfTypicalCourse1")
                .withGiverSection("Section 1")
                .withRecipientSection("Section 1")
                .withResponseDetails(new FeedbackTextResponseDetails("New Response to self"))
                .build();

        frLogic.createFeedbackResponse(responseToAdd);

        // All these responses should be gone after he changes teams

        frLogic.updateFeedbackResponsesForChangingTeam(
                studentToUpdate.getCourse(), studentToUpdate.getEmail(), studentToUpdate.getTeam(), "Team 1.2");

        teamQuestion = getQuestionFromDatabase("team.members.feedback");
        assertEquals(0, getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.getEmail()).size());
        assertEquals(0,
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                teamQuestion.getId(), studentToUpdate.getEmail()).size());

        teamQuestion = getQuestionFromDatabase("team.feedback");
        assertEquals(0, getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.getEmail()).size());

        // Non-team response should remain

        assertEquals(1,
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                        getQuestionFromDatabase("qn1InSession1InCourse1").getId(),
                        studentToUpdate.getEmail()).size());
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
    public void testUpdateFeedbackResponsesForChangingTeam_deleteLastResponse_decreaseResponseRate() {
        FeedbackResponseAttributes responseShouldBeDeleted =
                getResponseFromDatabase(questionTypeBundle, "response1ForQ1ContribSession2Course2");
        // make sure it's the last response by the student
        assertEquals(1, numResponsesFromGiverInSession(responseShouldBeDeleted.getGiver(),
                responseShouldBeDeleted.getFeedbackSessionName(),
                responseShouldBeDeleted.getCourseId()));
        StudentAttributes student = questionTypeBundle.students.get("student2InCourse2");
        // the response is given by the student
        assertEquals(student.getEmail(), responseShouldBeDeleted.getGiver());

        int originalResponseRate = getResponseRate(responseShouldBeDeleted.getFeedbackSessionName(),
                responseShouldBeDeleted.getCourseId());

        frLogic.updateFeedbackResponsesForChangingTeam(student.getCourse(), student.getEmail(), student.getTeam(),
                student.getTeam() + "tmp");

        int responseRateAfterDeletion = getResponseRate(responseShouldBeDeleted.getFeedbackSessionName(),
                responseShouldBeDeleted.getCourseId());
        assertEquals(originalResponseRate - 1, responseRateAfterDeletion);
    }

    @Test
    public void testUpdateFeedbackResponsesForChangingTeam_noResponseShouldBeDeleted_shouldReaminSameResponseRate() {
        FeedbackResponseAttributes responseShouldBeDeleted =
                getResponseFromDatabase(questionTypeBundle, "response1ForQ1RankSession");
        // make sure it's not the last response by the student
        assertTrue(1 < numResponsesFromGiverInSession(responseShouldBeDeleted.getGiver(),
                responseShouldBeDeleted.getFeedbackSessionName(),
                responseShouldBeDeleted.getCourseId()));
        StudentAttributes student = questionTypeBundle.students.get("student1InCourse1");
        // the response is given by the student
        assertEquals(student.getEmail(), responseShouldBeDeleted.getGiver());

        int originalResponseRate = getResponseRate(responseShouldBeDeleted.getFeedbackSessionName(),
                responseShouldBeDeleted.getCourseId());

        frLogic.updateFeedbackResponsesForChangingTeam(student.getCourse(), student.getEmail(), student.getTeam(),
                student.getTeam() + "tmp");

        int responseRateAfterDeletion = getResponseRate(responseShouldBeDeleted.getFeedbackSessionName(),
                responseShouldBeDeleted.getCourseId());
        assertEquals(originalResponseRate, responseRateAfterDeletion);
    }

    private int numResponsesFromGiverInSession(String studentEmail, String sessionName, String courseId) {
        int numResponses = 0;
        for (FeedbackResponseAttributes response : questionTypeBundle.feedbackResponses.values()) {
            if (response.getGiver().equals(studentEmail) && response.getFeedbackSessionName().equals(sessionName)
                    && response.getCourseId().equals(courseId)) {
                numResponses++;
            }
        }
        return numResponses;
    }

    private int getResponseRate(String sessionName, String courseId) {
        FeedbackSessionAttributes sessionFromDatabase = fsLogic.getFeedbackSession(sessionName, courseId);
        return fsLogic.getActualTotalSubmission(sessionFromDatabase);
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
                        studentToUpdate.getCourse(), studentToUpdate.getEmail());
        List<FeedbackResponseAttributes> responsesFromGiver =
                frLogic.getFeedbackResponsesFromGiverForCourse(
                        studentToUpdate.getCourse(), studentToUpdate.getEmail());
        Set<String> responseIdsToAndFromStudent = new HashSet<>();
        responseIdsToAndFromStudent.addAll(
                responsesForReceiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        responseIdsToAndFromStudent.addAll(
                responsesFromGiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        List<FeedbackResponseCommentAttributes> responseCommentsForStudent =
                getFeedbackResponseCommentsForResponsesFromDatabase(responseIdsToAndFromStudent);

        assertEquals(11, responsesForReceiver.size());
        assertEquals(8, responsesFromGiver.size());
        assertEquals(2, responseCommentsForStudent.size());
        // student's comment
        assertTrue(responseCommentsForStudent.stream().anyMatch(r -> r.isCommentFromFeedbackParticipant()));
        // instructor comment
        assertTrue(responseCommentsForStudent.stream().anyMatch(r -> !r.isCommentFromFeedbackParticipant()));

        frLogic.updateFeedbackResponsesForChangingEmail(
                studentToUpdate.getCourse(), studentToUpdate.getEmail(), "new@email.tmt");

        responsesForReceiver = frLogic.getFeedbackResponsesForReceiverForCourse(
                studentToUpdate.getCourse(), studentToUpdate.getEmail());
        responsesFromGiver = frLogic.getFeedbackResponsesFromGiverForCourse(
                studentToUpdate.getCourse(), studentToUpdate.getEmail());
        responseIdsToAndFromStudent = new HashSet<>();
        responseIdsToAndFromStudent.addAll(
                responsesForReceiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        responseIdsToAndFromStudent.addAll(
                responsesFromGiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        responseCommentsForStudent =
                getFeedbackResponseCommentsForResponsesFromDatabase(responseIdsToAndFromStudent);

        assertEquals(0, responsesForReceiver.size());
        assertEquals(0, responsesFromGiver.size());
        assertEquals(0, responseCommentsForStudent.size());

        responsesForReceiver = frLogic.getFeedbackResponsesForReceiverForCourse(
                studentToUpdate.getCourse(), "new@email.tmt");
        responsesFromGiver = frLogic.getFeedbackResponsesFromGiverForCourse(
                studentToUpdate.getCourse(), "new@email.tmt");
        responseIdsToAndFromStudent = new HashSet<>();
        responseIdsToAndFromStudent.addAll(
                responsesForReceiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        responseIdsToAndFromStudent.addAll(
                responsesFromGiver.stream().map(FeedbackResponseAttributes::getId).collect(Collectors.toList()));
        responseCommentsForStudent =
                getFeedbackResponseCommentsForResponsesFromDatabase(responseIdsToAndFromStudent);

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

        FeedbackQuestionAttributes fq = getQuestionFromDatabase("qn3InSession1InCourse1");
        FeedbackResponseAttributes fr = getResponseFromDatabase("response1ForQ3S1C1");

        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(fq.getCourseId()),
                instructorsLogic.getInstructorsForCourse(fq.getCourseId()));

        assertTrue(frLogic.isNameVisibleToUser(fq, fr, instructor.getEmail(), true, true, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, instructor.getEmail(), true, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.getEmail(), false, false, roster));

        ______TS("test if visible to own team members");

        fr.setGiver(student.getEmail());
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.getEmail(), false, false, roster));

        ______TS("test if visible to receiver/reciever team members");

        fq.setRecipientType(FeedbackParticipantType.TEAMS);
        fq.getShowRecipientNameTo().clear();
        fq.getShowRecipientNameTo().add(FeedbackParticipantType.RECEIVER);
        fr.setRecipient(student.getTeam());
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.getEmail(), false, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student3.getEmail(), false, false, roster));

        fq.setRecipientType(FeedbackParticipantType.STUDENTS);
        fr.setRecipient(student.getEmail());
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.getEmail(), false, false, roster));
        assertFalse(frLogic.isNameVisibleToUser(fq, fr, student2.getEmail(), false, false, roster));

        fq.setRecipientType(FeedbackParticipantType.TEAMS);
        fq.getShowRecipientNameTo().clear();
        fq.getShowRecipientNameTo().add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        fr.setRecipient(student.getTeam());
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.getEmail(), false, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student3.getEmail(), false, false, roster));

        fq.setRecipientType(FeedbackParticipantType.STUDENTS);
        fr.setRecipient(student.getEmail());
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.getEmail(), false, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student3.getEmail(), false, false, roster));
        assertFalse(frLogic.isNameVisibleToUser(fq, fr, student5.getEmail(), false, false, roster));

        ______TS("test if visible to receiver/giver team members for team questions");

        fq.setRecipientType(FeedbackParticipantType.TEAMS);
        fq.getShowRecipientNameTo().clear();
        fq.getShowRecipientNameTo().add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        fq.setGiverType(FeedbackParticipantType.TEAMS);
        fq.getShowGiverNameTo().clear();
        fq.getShowGiverNameTo().add(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);

        fr.setRecipient(student5.getTeam());
        fr.setGiver(student.getTeam());
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student.getEmail(), false, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student3.getEmail(), false, false, roster));
        assertTrue(frLogic.isNameVisibleToUser(fq, fr, student5.getEmail(), false, false, roster));

        ______TS("test anonymous team recipients");
        // Only members of the recipient team should be able to see the recipient name
        fq.setRecipientType(FeedbackParticipantType.TEAMS);
        fq.getShowRecipientNameTo().clear();
        fq.getShowRecipientNameTo().add(FeedbackParticipantType.RECEIVER);
        fq.getShowResponsesTo().add(FeedbackParticipantType.STUDENTS);
        fr.setRecipient("Team 1.1");
        assertFalse(frLogic.isNameVisibleToUser(fq, fr, student5.getEmail(), false, false, roster));

        ______TS("null question");

        assertFalse(frLogic.isNameVisibleToUser(null, fr, student.getEmail(), false, false, roster));

    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_shouldDeleteRelatedResponses() {
        StudentAttributes studentToDelete = dataBundle.students.get("student1InCourse1");
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");

        // the responses also have some associated comments
        List<FeedbackResponseAttributes> remainingResponses = new ArrayList<>();
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail()));
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail()));
        assertFalse(remainingResponses.isEmpty());

        // the student has some responses
        List<FeedbackResponseAttributes> responsesForStudent1 =
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail());
        responsesForStudent1.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail()));
        assertFalse(responsesForStudent1.isEmpty());
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(session1InCourse1.getCourseId(),
                        session1InCourse1.getFeedbackSessionName()).contains(studentToDelete.getEmail()));

        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(
                studentToDelete.getCourse(), studentToDelete.getEmail());

        // responses should be deleted
        remainingResponses = new ArrayList<>();
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail()));
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.getCourse(), studentToDelete.getEmail()));
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
                        session1InCourse1.getFeedbackSessionName()).contains(studentToDelete.getEmail()));
    }

    @Test
    public void testDeleteFeedbackResponseCascade() {
        ______TS("non-existent response");

        // should pass silently
        frLogic.deleteFeedbackResponseCascade("not-exist");

        ______TS("standard delete");

        FeedbackResponseAttributes fra = getResponseFromDatabase("response1ForQ1S1C1");
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
    public void testDeleteFeedbackResponsesForQuestionCascade_studentsQuestion_shouldUpdateRespondents() {
        FeedbackResponseAttributes fra = getResponseFromDatabase("response1ForQ1S1C1");

        // this is the only response the student has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.getCourseId(), fra.getGiver()).stream()
                .filter(response -> response.getFeedbackSessionName().equals(fra.getFeedbackSessionName()))
                .count());
        // the student has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));

        frLogic.deleteFeedbackResponsesForQuestionCascade(fra.getFeedbackQuestionId());

        // there is no student X as respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));
    }

    @Test
    public void testDeleteFeedbackResponsesForQuestionCascade_instructorsQuestion_shouldUpdateRespondents() {
        FeedbackResponseAttributes fra = getResponseFromDatabase("response1ForQ3S1C1");

        // this is the only response the instructor has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.getCourseId(), fra.getGiver()).stream()
                .filter(response -> response.getFeedbackSessionName().equals(fra.getFeedbackSessionName()))
                .count());
        // the instructor has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));

        frLogic.deleteFeedbackResponsesForQuestionCascade(fra.getFeedbackQuestionId());

        // there is not instructor X in instructor respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_giverIsStudent_shouldUpdateRespondents() {
        FeedbackResponseAttributes fra = getResponseFromDatabase("response3ForQ2S1C1");
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        // giver is student
        assertEquals(FeedbackParticipantType.STUDENTS,
                fqLogic.getFeedbackQuestion(fra.getFeedbackQuestionId()).getGiverType());
        // student is the recipient
        assertEquals(fra.getRecipient(), student2InCourse1.getEmail());

        // this is the only response the giver has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.getCourseId(), fra.getGiver()).stream()
                .filter(response -> response.getFeedbackSessionName().equals(fra.getFeedbackSessionName()))
                .count());
        // the student has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));

        // after the giver is removed from the course
        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(
                student2InCourse1.getCourse(), student2InCourse1.getEmail());

        // there is no student X as respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_giverIsInstructor_shouldUpdateRespondents() {
        FeedbackResponseAttributes fra = getResponseFromDatabase("response1ForQ1S2C2");
        StudentAttributes student1InCourse2 = dataBundle.students.get("student1InCourse2");
        // giver is instructor
        assertEquals(FeedbackParticipantType.SELF,
                fqLogic.getFeedbackQuestion(fra.getFeedbackQuestionId()).getGiverType());
        // student is the recipient
        assertEquals(fra.getRecipient(), student1InCourse2.getEmail());

        // this is the only response the instructor has given for the session
        assertEquals(1, frLogic.getFeedbackResponsesFromGiverForCourse(fra.getCourseId(), fra.getGiver()).stream()
                .filter(response -> response.getFeedbackSessionName().equals(fra.getFeedbackSessionName()))
                .count());
        // the instructor has answers for the session
        assertTrue(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));

        // after the giver is removed from the course
        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(
                student1InCourse2.getCourse(), student1InCourse2.getEmail());

        // there is no instructor X as respondents
        assertFalse(
                frLogic.getGiverSetThatAnswerFeedbackSession(fra.getCourseId(),
                        fra.getFeedbackSessionName()).contains(fra.getGiver()));
    }

    @Test
    public void testDeleteFeedbackResponsesInvolvedEntityOfCourseCascade_shouldDeleteRelevantResponsesAsRecipient()
            throws Exception {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        FeedbackResponseAttributes fra1ReceivedByTeam = getResponseFromDatabase("response1ForQ1S2C1");
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
        FeedbackResponseAttributes fra2ReceivedByTeam = getResponseFromDatabase("response1GracePeriodFeedback");
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
        FeedbackResponseAttributes fra1GivenByTeam = getResponseFromDatabase("response1ForQ1S2C1");
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
        FeedbackResponseAttributes fra2GivenByTeam = getResponseFromDatabase("response1GracePeriodFeedback");
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

    @Test
    public void testGetSessionResultsForUser_studentSpecificQuestion_shouldHaveCorrectResponsesFiltered() {
        // extra test data used on top of typical data bundle
        removeAndRestoreDataBundle(loadDataBundle("/SpecialCharacterTest.json"));

        FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(
                "First Session", "FQLogicPCT.CS2104", 1);

        // Alice will see 3 responses
        SessionResultsBundle bundle = frLogic.getSessionResultsForUser(
                "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.alice.b@gmail.tmt",
                false, question.getId());
        assertEquals(1, bundle.getQuestionResponseMap().size());
        List<FeedbackResponseAttributes> responseForQuestion =
                bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(3, responseForQuestion.size());

        // Benny will see 3 responses
        bundle = frLogic.getSessionResultsForUser(
                "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.benny.c@gmail.tmt",
                false, question.getId());
        assertEquals(1, bundle.getQuestionResponseMap().size());
        responseForQuestion = bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(3, responseForQuestion.size());

        // Charlie will see 2 responses
        bundle = frLogic.getSessionResultsForUser(
                "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.charlie.d@gmail.tmt",
                false, question.getId());
        assertEquals(1, bundle.getQuestionResponseMap().size());
        responseForQuestion = bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(2, responseForQuestion.size());

        // Danny will see 2 responses
        bundle = frLogic.getSessionResultsForUser(
                "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.danny.e@gmail.tmt",
                false, question.getId());
        assertEquals(1, bundle.getQuestionResponseMap().size());
        responseForQuestion = bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(2, responseForQuestion.size());

        // Emily will see 1 response
        bundle = frLogic.getSessionResultsForUser(
                "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.emily.f@gmail.tmt",
                false, question.getId());
        assertEquals(1, bundle.getQuestionResponseMap().size());
        responseForQuestion = bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(1, responseForQuestion.size());
    }

    @Test
    public void testGetSessionResultsForUser_studentAllQuestions_shouldGenerateCorrectBundle() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("standard.session");

        // Test result bundle for student1
        StudentAttributes student = responseBundle.students.get("student1InCourse1");
        SessionResultsBundle bundle = frLogic.getSessionResultsForUser(
                session.getFeedbackSessionName(), session.getCourseId(), student.getEmail(),
                false, null);

        // Student can see responses: q1r1, q2r1,3, q3r1, qr4r2-3, q5r1, q7r1-2, q8r1-2
        // We don't check the actual IDs as this is also implicitly tested
        // later when checking the visibility table.
        int totalResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionResponseMap().entrySet()) {
            totalResponse += entry.getValue().size();
        }
        int totalMissingResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionMissingResponseMap().entrySet()) {
            totalMissingResponse += entry.getValue().size();
        }
        assertEquals(11, totalResponse);
        // student should not see missing responses
        assertEquals(0, totalMissingResponse);
        // student cannot see q6 because there is no viewable response
        assertEquals(7, bundle.getQuestionsMap().size());
        assertEquals(7, bundle.getQuestionResponseMap().size());
        assertEquals(7, bundle.getQuestionMissingResponseMap().size());

        // Test the generated response visibilityTable for userNames.
        Map<String, Boolean> responseGiverVisibilityTable = bundle.getResponseGiverVisibilityTable();
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn1.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn4.resp2", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn5.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn7.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn7.resp2", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn8.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn8.resp2", responseBundle)));
        assertEquals(totalResponse, responseGiverVisibilityTable.size());

        Map<String, Boolean> responseRecipientVisibilityTable = bundle.getResponseRecipientVisibilityTable();
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn1.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp2", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn5.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn7.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn7.resp2", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn8.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn8.resp2", responseBundle)));
        assertEquals(totalResponse, responseRecipientVisibilityTable.size());

        // no entry in comment visibility table
        Map<Long, Boolean> commentGiverVisibilityTable = bundle.getCommentGiverVisibilityTable();
        assertEquals(0, commentGiverVisibilityTable.size());
    }

    @Test
    public void testGetSessionResultsForUser_instructor_shouldGenerateCorrectBundle() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("standard.session");

        // Test result bundle for instructor1
        InstructorAttributes instructor = responseBundle.instructors.get("instructor1OfCourse1");
        SessionResultsBundle bundle = frLogic.getSessionResultsForUser(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.getEmail(),
                true, null);

        // Instructor can see responses: q3r1, q6r1
        // We don't check the actual IDs as this is also implicitly tested
        // later when checking the visibility table.
        int totalResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionResponseMap().entrySet()) {
            totalResponse += entry.getValue().size();
        }
        int totalMissingResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionMissingResponseMap().entrySet()) {
            totalMissingResponse += entry.getValue().size();
        }
        assertEquals(2, totalResponse);
        // instructor should not see missing responses
        assertEquals(0, totalMissingResponse);

        assertEquals(2, bundle.getQuestionsMap().size());
        assertEquals(2, bundle.getQuestionResponseMap().size());
        assertEquals(2, bundle.getQuestionMissingResponseMap().size());

        // Test the generated response visibilityTable for userNames.
        Map<String, Boolean> responseGiverVisibilityTable = bundle.getResponseGiverVisibilityTable();
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn6.resp1", responseBundle)));
        assertEquals(totalResponse, responseGiverVisibilityTable.size());

        Map<String, Boolean> responseRecipientVisibilityTable = bundle.getResponseRecipientVisibilityTable();
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn6.resp1", responseBundle)));
        assertEquals(totalResponse, responseRecipientVisibilityTable.size());

        // no entry in comment visibility table
        Map<Long, Boolean> commentGiverVisibilityTable = bundle.getCommentGiverVisibilityTable();
        assertEquals(0, commentGiverVisibilityTable.size());
    }

    @Test
    public void testGetSessionResultsForCourse_specificQuestion_shouldHaveCorrectResponsesFiltered() {
        FeedbackQuestionAttributes fq = getQuestionFromDatabase("qn3InSession1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");

        // no section specified
        SessionResultsBundle bundle = frLogic.getSessionResultsForCourse(
                fq.getFeedbackSessionName(), fq.getCourseId(), instructor.getEmail(),
                fq.getId(), null);
        assertEquals(1, bundle.getQuestionResponseMap().size());
        List<FeedbackResponseAttributes> responseForQuestion =
                bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(1, responseForQuestion.size());

        // section specified
        fq = getQuestionFromDatabase("qn2InSession1InCourse1");
        bundle = frLogic.getSessionResultsForCourse(
                fq.getFeedbackSessionName(), fq.getCourseId(), instructor.getEmail(),
                fq.getId(), "Section 1");
        assertEquals(1, bundle.getQuestionResponseMap().size());
        responseForQuestion = bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(3, responseForQuestion.size());
    }

    @Test
    public void testGetSessionResultsForCourse_allQuestions_shouldGenerateCorrectBundle() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("standard.session");

        InstructorAttributes instructor = responseBundle.instructors.get("instructor1OfCourse1");
        SessionResultsBundle bundle = frLogic.getSessionResultsForCourse(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.getEmail(),
                null, null);

        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        int totalResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionResponseMap().entrySet()) {
            totalResponse += entry.getValue().size();
        }
        int totalMissingResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionMissingResponseMap().entrySet()) {
            totalMissingResponse += entry.getValue().size();
        }
        assertEquals(10, totalResponse);
        assertEquals(4, totalMissingResponse);
        // Instructor should still see all questions
        assertEquals(8, bundle.getQuestionsMap().size());
        assertEquals(8, bundle.getQuestionResponseMap().size());
        assertEquals(8, bundle.getQuestionMissingResponseMap().size());

        // Test the generated response visibilityTable for userNames.
        Map<String, Boolean> responseGiverVisibilityTable = bundle.getResponseGiverVisibilityTable();
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn2.resp2", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn3.resp2", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn4.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn4.resp2", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn5.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn6.resp1", responseBundle)));
        assertEquals(totalResponse + totalMissingResponse, responseGiverVisibilityTable.size());

        Map<String, Boolean> responseRecipientVisibilityTable = bundle.getResponseRecipientVisibilityTable();
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn2.resp2", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn3.resp2", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp2", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn5.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn6.resp1", responseBundle)));
        assertEquals(totalResponse + totalMissingResponse, responseRecipientVisibilityTable.size());

        // no entry in comment visibility table
        Map<Long, Boolean> commentGiverVisibilityTable = bundle.getCommentGiverVisibilityTable();
        assertEquals(0, commentGiverVisibilityTable.size());
    }

    @Test
    public void testGetSessionResultsForCourse_allQuestionsSpecificSection_shouldGenerateCorrectBundle() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("standard.session");

        InstructorAttributes instructor = responseBundle.instructors.get("instructor1OfCourse1");
        SessionResultsBundle bundle = frLogic.getSessionResultsForCourse(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.getEmail(),
                null, "Section A");

        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        // after filtering by section, the number of responses seen by instructor will differ.
        // Responses viewed by instructor after filtering: q2r1-3, q3r1, q4r2-3, q5r1
        int totalResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionResponseMap().entrySet()) {
            totalResponse += entry.getValue().size();
        }
        int totalMissingResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionMissingResponseMap().entrySet()) {
            totalMissingResponse += entry.getValue().size();
        }
        assertEquals(7, totalResponse);
        assertEquals(0, totalMissingResponse);
        // Instructor should still see all questions
        assertEquals(8, bundle.getQuestionsMap().size());
        assertEquals(8, bundle.getQuestionResponseMap().size());
        assertEquals(8, bundle.getQuestionMissingResponseMap().size());

        // Test the generated response visibilityTable for userNames.
        Map<String, Boolean> responseGiverVisibilityTable = bundle.getResponseGiverVisibilityTable();
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertEquals(totalResponse + totalMissingResponse, responseGiverVisibilityTable.size());

        Map<String, Boolean> responseRecipientVisibilityTable = bundle.getResponseRecipientVisibilityTable();
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertEquals(totalResponse + totalMissingResponse, responseGiverVisibilityTable.size());
        assertEquals(totalResponse + totalMissingResponse, responseRecipientVisibilityTable.size());

        // no entry in comment visibility table
        Map<Long, Boolean> commentGiverVisibilityTable = bundle.getCommentGiverVisibilityTable();
        assertEquals(0, commentGiverVisibilityTable.size());
    }

    // TODO: check for cases where a person is both a student and an instructor

    @Test
    public void testGetSessionResultsForUser_orphanResponseInDB_shouldStillHandleCorrectly() throws Exception {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);

        FeedbackQuestionAttributes fq = getQuestionFromDatabase("qn2InSession1InCourse1");
        FeedbackResponseAttributes existingResponse = getResponseFromDatabase(dataBundle, "response1ForQ2S1C1");
        // create a "null" response to simulate trying to get a null student's response
        FeedbackResponseAttributes newResponse =
                FeedbackResponseAttributes.builder(
                        existingResponse.getFeedbackQuestionId(), existingResponse.getGiver(), "nullRecipient@gmail.tmt")
                        .withFeedbackSessionName(existingResponse.getFeedbackSessionName())
                        .withCourseId("nullCourse")
                        .withGiverSection("Section 1")
                        .withRecipientSection("Section 1")
                        .withResponseDetails(existingResponse.getResponseDetailsCopy())
                        .build();
        frLogic.createFeedbackResponse(newResponse);
        StudentAttributes student = dataBundle.students.get("student2InCourse1");

        SessionResultsBundle bundle = frLogic.getSessionResultsForUser(
                fq.getFeedbackSessionName(), fq.getCourseId(), student.getEmail(),
                false, fq.getId());
        assertEquals(1, bundle.getQuestionResponseMap().size());
        List<FeedbackResponseAttributes> responseForQuestion =
                bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(4, responseForQuestion.size());
    }

    private FeedbackQuestionAttributes getQuestionFromDatabase(DataBundle dataBundle, String jsonId) {
        FeedbackQuestionAttributes questionToGet = dataBundle.feedbackQuestions.get(jsonId);
        questionToGet = fqLogic.getFeedbackQuestion(questionToGet.getFeedbackSessionName(),
                questionToGet.getCourseId(),
                questionToGet.getQuestionNumber());

        return questionToGet;
    }

    private FeedbackQuestionAttributes getQuestionFromDatabase(String jsonId) {
        return getQuestionFromDatabase(dataBundle, jsonId);
    }

    private FeedbackResponseAttributes getResponseFromDatabase(DataBundle dataBundle, String jsonId) {
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get(jsonId);

        String qnId;
        try {
            int qnNumber = Integer.parseInt(response.getFeedbackQuestionId());
            qnId = fqLogic.getFeedbackQuestion(response.getFeedbackSessionName(), response.getCourseId(), qnNumber).getId();
        } catch (NumberFormatException e) {
            qnId = response.getFeedbackQuestionId();
        }

        return frLogic.getFeedbackResponse(
                qnId, response.getGiver(), response.getRecipient());
    }

    private FeedbackResponseAttributes getResponseFromDatabase(String jsonId) {
        return getResponseFromDatabase(dataBundle, jsonId);
    }

    private String getResponseId(String jsonId, DataBundle bundle) {
        return getResponseFromDatabase(bundle, jsonId).getId();
    }

    private List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForResponsesFromDatabase(
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
