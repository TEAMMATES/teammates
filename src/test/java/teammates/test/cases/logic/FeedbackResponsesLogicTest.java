package teammates.test.cases.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SectionDetail;
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
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link FeedbackResponsesLogic}.
 */
public class FeedbackResponsesLogicTest extends BaseLogicTest {

    private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();

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
    public void testSpecialCharactersInTeamName() {
        ______TS("test special characters");

        FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(
                "First Session", "FQLogicPCT.CS2104", 1);

        // Alice will see 4 responses
        assertEquals(4,
                frLogic.getViewableFeedbackResponsesForQuestionInSection(
                question,
                "FQLogicPCT.alice.b@gmail.tmt",
                UserRole.STUDENT,
                "First Session", null).size());

        // Benny will see 4 responses
        assertEquals(4,
                frLogic.getViewableFeedbackResponsesForQuestionInSection(
                question,
                "FQLogicPCT.benny.c@gmail.tmt",
                UserRole.STUDENT,
                "First Session", null).size());

        // Charlie will see 3 responses
        assertEquals(3,
                frLogic.getViewableFeedbackResponsesForQuestionInSection(
                question,
                "FQLogicPCT.charlie.d@gmail.tmt",
                UserRole.STUDENT,
                "First Session", null).size());

        // Danny will see 3 responses
        assertEquals(3,
                frLogic.getViewableFeedbackResponsesForQuestionInSection(
                question,
                "FQLogicPCT.danny.e@gmail.tmt",
                UserRole.STUDENT,
                "First Session", null).size());

        // Emily will see 1 response
        assertEquals(1,
                frLogic.getViewableFeedbackResponsesForQuestionInSection(
                question,
                "FQLogicPCT.emily.f@gmail.tmt",
                UserRole.STUDENT,
                "First Session", null).size());

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
                new FeedbackResponseAttributes(
                        responseToUpdate.feedbackSessionName,
                        responseToUpdate.courseId,
                        responseToUpdate.feedbackQuestionId,
                        responseToUpdate.giver,
                        responseToUpdate.giverSection,
                        "student3InCourse1@gmail.tmt",
                        responseToUpdate.recipientSection,
                        responseToUpdate.responseDetails);

        frLogic.createFeedbackResponses(Arrays.asList(existingResponse));

        FeedbackResponseAttributes[] finalResponse = new FeedbackResponseAttributes[] { responseToUpdate };
        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> frLogic.updateFeedbackResponseCascade(
                        FeedbackResponseAttributes.updateOptionsBuilder(finalResponse[0].getId())
                                .withRecipient("student3InCourse1@gmail.tmt")
                                .build()));
        AssertHelper.assertContains("Trying to create a Feedback Response that exists", eaee.getMessage());

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

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frLogic.updateFeedbackResponseCascade(
                        FeedbackResponseAttributes.updateOptionsBuilder("non-existent")
                                .withGiver("random")
                                .build()));
        AssertHelper.assertContains(
                FeedbackResponsesDb.ERROR_UPDATE_NON_EXISTENT,
                ednee.getMessage());
    }

    @Test
    public void testUpdateFeedbackResponsesForChangingTeam_typicalData_shouldDoCascadeDeletion() throws Exception {

        StudentAttributes studentToUpdate = dataBundle.students.get("student4InCourse1");

        // Student 4 has 1 responses to him from team members,
        // 1 response from him a team member, and
        // 1 team response from him to another team.
        FeedbackQuestionAttributes teamQuestion = getQuestionFromDatastore("team.members.feedback");
        assertEquals(1,
                frLogic.getFeedbackResponsesForReceiverForQuestion(
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
                new FeedbackResponseAttributes("First feedback session", "idOfTypicalCourse1",
                                               getQuestionFromDatastore("qn1InSession1InCourse1").getId(),
                                               studentToUpdate.email, "Section 1",
                                               studentToUpdate.email, "Section 1",
                                               new FeedbackTextResponseDetails("New Response to self"));
        frLogic.createFeedbackResponses(Arrays.asList(responseToAdd));

        // All these responses should be gone after he changes teams

        frLogic.updateFeedbackResponsesForChangingTeam(
                studentToUpdate.course, studentToUpdate.email, studentToUpdate.team, "Team 1.2");

        teamQuestion = getQuestionFromDatastore("team.members.feedback");
        assertEquals(0,
                frLogic.getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size());
        assertEquals(0,
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size());

        teamQuestion = getQuestionFromDatastore("team.feedback");
        assertEquals(0,
                frLogic.getFeedbackResponsesForReceiverForQuestion(
                teamQuestion.getId(), studentToUpdate.email).size());

        // Non-team response should remain

        assertEquals(1,
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                getQuestionFromDatastore("qn1InSession1InCourse1").getId(),
                studentToUpdate.email).size());
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
        return sessionFromDataStore.getRespondingInstructorList().size()
                + sessionFromDataStore.getRespondingStudentList().size();
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
    public void testGetViewableResponsesForQuestionInSection() throws Exception {

        ______TS("success: GetViewableResponsesForQuestion - instructor");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestionAttributes fq = getQuestionFromDatastore("qn3InSession1InCourse1");
        List<FeedbackResponseAttributes> responses =
                frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, instructor.email,
                                                                         UserRole.INSTRUCTOR, null, null);

        assertEquals(1, responses.size());

        ______TS("success: GetViewableResponsesForQuestionInSection - instructor");

        // other more in-depth sectionDetail types are tested in FeedbackResponsesDbTest.java
        fq = getQuestionFromDatastore("qn2InSession1InCourse1");
        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(
                fq, instructor.email, UserRole.INSTRUCTOR, "Section 1", SectionDetail.EITHER);

        assertEquals(3, responses.size());

        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(
                fq, instructor.email, UserRole.INSTRUCTOR, "Section 2", SectionDetail.BOTH);

        assertEquals(0, responses.size());

        ______TS("success: GetViewableResponsesForQuestion - student");

        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        fq = getQuestionFromDatastore("qn2InSession1InCourse1");
        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, student.email, UserRole.STUDENT,
                null, null);

        assertEquals(0, responses.size());

        fq = getQuestionFromDatastore("qn3InSession1InCourse1");
        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, student.email, UserRole.STUDENT,
                null, null);

        assertEquals(1, responses.size());

        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.showResponsesTo.add(FeedbackParticipantType.RECEIVER);
        fq.showResponsesTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        fq.showResponsesTo.remove(FeedbackParticipantType.STUDENTS);
        FeedbackResponseAttributes fr = getResponseFromDatastore("response1ForQ3S1C1");
        frLogic.updateFeedbackResponseCascade(
                FeedbackResponseAttributes.updateOptionsBuilder(fr.getId())
                        .withRecipient(student.email)
                        .build());

        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, student.email, UserRole.STUDENT,
                null, null);

        assertEquals(1, responses.size());

        ______TS("success: Null student in response, should skip over null student");
        fq = getQuestionFromDatastore("qn2InSession1InCourse1");
        fq.showResponsesTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);

        FeedbackResponseAttributes existingResponse = getResponseFromDatastore("response1ForQ2S1C1");

        //Create a "null" response to simulate trying to get a null student's response
        FeedbackResponseAttributes newResponse =
                new FeedbackResponseAttributes(
                        existingResponse.feedbackSessionName,
                        "nullCourse",
                        existingResponse.feedbackQuestionId,
                        existingResponse.giver,
                        "Section 1",
                        "nullRecipient@gmail.tmt",
                        "Section 1",
                        existingResponse.responseDetails);

        frLogic.createFeedbackResponses(Arrays.asList(newResponse));
        student = dataBundle.students.get("student2InCourse1");
        responses = frLogic.getViewableFeedbackResponsesForQuestionInSection(fq, student.email, UserRole.STUDENT,
                null, null);
        assertEquals(4, responses.size());

        ______TS("failure: GetViewableResponsesForQuestion invalid role");

        FeedbackQuestionAttributes finalFq = fq;
        AssertionError ae = assertThrows(AssertionError.class,
                () -> frLogic.getViewableFeedbackResponsesForQuestionInSection(
                        finalFq, instructor.email, UserRole.ADMIN, null, null));
        assertEquals("The role of the requesting use has to be Student or Instructor", ae.getMessage());
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
                new StudentsDb().getStudentsForCourse(fq.courseId),
                new InstructorsDb().getInstructorsForCourse(fq.courseId));

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
    public void testDeleteFeedbackResponsesForStudent() throws Exception {

        ______TS("standard delete");

        StudentAttributes studentToDelete = dataBundle.students.get("student1InCourse1");
        List<FeedbackResponseAttributes> responsesForStudent1 =
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email);
        responsesForStudent1
                .addAll(
                        frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));
        frLogic.deleteFeedbackResponsesForStudentAndCascade(studentToDelete.course, studentToDelete.email);

        List<FeedbackResponseAttributes> remainingResponses = new ArrayList<>();
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));
        assertEquals(0, remainingResponses.size());

        List<FeedbackResponseCommentAttributes> remainingComments = new ArrayList<>();
        for (FeedbackResponseAttributes response : responsesForStudent1) {
            remainingComments.addAll(frcLogic.getFeedbackResponseCommentForResponse(response.getId()));
        }
        assertEquals(0, remainingComments.size());

        ______TS("shift team then delete");

        remainingResponses.clear();

        studentToDelete = dataBundle.students.get("student2InCourse1");

        studentToDelete.team = "Team 1.3";
        StudentsLogic.inst().updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(studentToDelete.course, studentToDelete.email)
                        .withTeamName(studentToDelete.team)
                        .build()
        );

        frLogic.deleteFeedbackResponsesForStudentAndCascade(studentToDelete.course, studentToDelete.email);

        remainingResponses.addAll(
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));
        assertEquals(0, remainingResponses.size());

        ______TS("delete last person in team");

        remainingResponses.clear();

        studentToDelete = dataBundle.students.get("student5InCourse1");

        frLogic.deleteFeedbackResponsesForStudentAndCascade(studentToDelete.course, studentToDelete.email);
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesFromGiverForCourse(studentToDelete.course, studentToDelete.email));
        remainingResponses.addAll(
                frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, studentToDelete.email));

        // check that team responses are gone too. already checked giver as it is stored by giver email not team id.
        remainingResponses.addAll(frLogic.getFeedbackResponsesForReceiverForCourse(studentToDelete.course, "Team 1.2"));

        assertEquals(0, remainingResponses.size());
    }

    @Test
    public void testDeleteFeedbackResponsesForCourse() {
        ______TS("standard delete");

        // test that responses are deleted
        String courseId = "idOfTypicalCourse1";
        assertFalse(frLogic.getFeedbackResponsesForSession("First feedback session", courseId).isEmpty());
        assertFalse(frLogic.getFeedbackResponsesForSession("Grace Period Session", courseId).isEmpty());
        assertFalse(frLogic.getFeedbackResponsesForSession("Closed Session", courseId).isEmpty());
        frLogic.deleteFeedbackResponsesForCourse(courseId);

        assertEquals(0, frLogic.getFeedbackResponsesForSession("First feedback session", courseId).size());
        assertEquals(0, frLogic.getFeedbackResponsesForSession("Grace Period Session", courseId).size());
        assertEquals(0, frLogic.getFeedbackResponsesForSession("Closed Session", courseId).size());

        // test that responses from other courses are unaffected
        String otherCourse = "idOfTypicalCourse2";
        assertFalse(frLogic.getFeedbackResponsesForSession("Instructor feedback session", otherCourse).isEmpty());
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
