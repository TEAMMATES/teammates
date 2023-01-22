package teammates.logic.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
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
    private DataBundle responseVisibilityBundle;

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        questionTypeBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        responseVisibilityBundle = loadDataBundle("/FeedbackResponseVisibilityTest.json");

        removeAndRestoreTypicalDataBundle();
        // extra test data used on top of typical data bundle
        removeAndRestoreDataBundle(loadDataBundle("/SpecialCharacterTest.json"));
        removeAndRestoreDataBundle(questionTypeBundle);
        removeAndRestoreDataBundle(responseVisibilityBundle);
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

    @Test
    public void testUpdateResponsesForDeletingStudent_rankRecipientQuestionResponse_newResponsesShouldBeConsistent()
            throws Exception {

        FeedbackQuestionAttributes distinctRankQuestion =
                getQuestionFromDatabase(questionTypeBundle, "qn1InRANKSession");
        String courseId = distinctRankQuestion.getCourseId();
        List<StudentAttributes> studentsInCourse = studentsLogic.getStudentsForCourse(courseId);
        Map<String, List<FeedbackResponseAttributes>> giverResponseMap = new HashMap<>();

        for (StudentAttributes student : studentsInCourse) {
            giverResponseMap.put(student.getEmail(), frLogic.getFeedbackResponsesFromGiverForQuestion(
                    distinctRankQuestion.getFeedbackQuestionId(), student.getEmail()));
        }

        int numStudents;
        List<FeedbackResponseAttributes> responsesFromStudent;
        while (!studentsInCourse.isEmpty()) {
            studentsLogic.deleteStudentCascade(courseId, studentsInCourse.get(0).getEmail());
            numStudents = studentsLogic.getNumberOfStudentsForCourse(courseId);
            studentsInCourse = studentsLogic.getStudentsForCourse(courseId);
            for (StudentAttributes student : studentsInCourse) {
                responsesFromStudent = frLogic.getFeedbackResponsesFromGiverForQuestion(
                        distinctRankQuestion.getId(), student.getEmail());
                assertTrue(areRankResponsesConsistent(responsesFromStudent, numStudents));
                assertTrue(areRankResponsesInSameOrder(giverResponseMap.get(student.getEmail()), responsesFromStudent));
                giverResponseMap.put(student.getEmail(), responsesFromStudent);
            }
        }

        refreshTestData();
        FeedbackQuestionAttributes nonDistinctRankQuestion =
                getQuestionFromDatabase(questionTypeBundle, "qn2InRANKSession");

        for (StudentAttributes student : studentsInCourse) {
            giverResponseMap.put(student.getEmail(), frLogic.getFeedbackResponsesFromGiverForQuestion(
                    nonDistinctRankQuestion.getFeedbackQuestionId(), student.getEmail()));
        }

        int numTeamMembers;
        while (!studentsInCourse.isEmpty()) {
            studentsLogic.deleteStudentCascade(courseId, studentsInCourse.get(0).getEmail());
            studentsInCourse = studentsLogic.getStudentsForCourse(courseId);
            for (StudentAttributes student : studentsInCourse) {
                numTeamMembers = studentsLogic.getStudentsForTeam(student.getTeam(), courseId).size();
                responsesFromStudent = frLogic.getFeedbackResponsesFromGiverForQuestion(
                        nonDistinctRankQuestion.getId(), student.getEmail());
                assertTrue(areRankResponsesConsistent(responsesFromStudent, numTeamMembers));
                assertTrue(areRankResponsesInSameOrder(giverResponseMap.get(student.getEmail()), responsesFromStudent));
                giverResponseMap.put(student.getEmail(), responsesFromStudent);
            }
        }
    }

    private boolean areRankResponsesConsistent(List<FeedbackResponseAttributes> responses, int maxRank) {
        for (FeedbackResponseAttributes response : responses) {
            if (!response.getFeedbackQuestionType().equals(FeedbackQuestionType.RANK_RECIPIENTS)) {
                return false;
            }
            FeedbackRankRecipientsResponseDetails responseDetails =
                    (FeedbackRankRecipientsResponseDetails) response.getResponseDetails();
            if (responseDetails.getAnswer() > maxRank) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether two list of responses for 'rank recipient question' have the same order for each recipient.
     * The recipients of the updated responses should be a subset of that of the original responses.
     * @param responses the original response list
     * @param modifiedResponses the updated response list
     * @return true if the modified response list maintain the original order of the responses
     */
    private boolean areRankResponsesInSameOrder(List<FeedbackResponseAttributes> responses,
                                                List<FeedbackResponseAttributes> modifiedResponses) {
        if (responses.isEmpty()) {
            return modifiedResponses.isEmpty();
        }
        if (modifiedResponses.isEmpty()) {
            return true;
        }

        // Expects responses to rank recipient questions.
        for (FeedbackResponseAttributes r : responses) {
            assert r.getFeedbackQuestionType().equals(FeedbackQuestionType.RANK_RECIPIENTS);
        }
        for (FeedbackResponseAttributes r : modifiedResponses) {
            assert r.getFeedbackQuestionType().equals(FeedbackQuestionType.RANK_RECIPIENTS);
        }

        responses.sort(Comparator.comparing(
                response -> ((FeedbackRankRecipientsResponseDetails) response.getResponseDetails()).getAnswer()));
        modifiedResponses.sort(Comparator.comparing(
                response -> ((FeedbackRankRecipientsResponseDetails) response.getResponseDetails()).getAnswer()));

        int pointer1 = 0;
        int pointer2 = 0;
        String recipient1Email;
        String recipient2Email;
        while (pointer1 < responses.size() && pointer2 < modifiedResponses.size()) {
            recipient1Email = responses.get(pointer1).getRecipient();
            recipient2Email = responses.get(pointer2).getRecipient();
            if (recipient1Email.equals(recipient2Email)) {
                pointer1++;
                pointer2++;
            } else {
                pointer1++; // Skips one response from first list.
            }
        }

        return pointer2 == modifiedResponses.size();
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
    public void testIsResponseVisibleForUser() {
        InstructorAttributes instructor1 = responseVisibilityBundle.instructors.get("FRV.instructor1OfCourse1");
        InstructorAttributes instructor2 = responseVisibilityBundle.instructors.get("FRV.instructor2OfCourse1");
        InstructorAttributes instructor3 = responseVisibilityBundle.instructors.get("FRV.instructor3OfCourse1");
        InstructorAttributes instructor4 = responseVisibilityBundle.instructors.get("FRV.instructor4OfCourse1");
        InstructorAttributes instructor5 = responseVisibilityBundle.instructors.get("FRV.instructor5OfCourse1");
        InstructorAttributes instructor6 = responseVisibilityBundle.instructors.get("FRV.instructor6OfCourse1");

        StudentAttributes student1 = responseVisibilityBundle.students.get("FRV.student1InCourse1");
        StudentAttributes student2 = responseVisibilityBundle.students.get("FRV.student2InCourse1");
        StudentAttributes student3 = responseVisibilityBundle.students.get("FRV.student3InCourse1");
        StudentAttributes student4 = responseVisibilityBundle.students.get("FRV.student4InCourse1");
        StudentAttributes student5 = responseVisibilityBundle.students.get("FRV.student5InCourse1");
        StudentAttributes student6 = responseVisibilityBundle.students.get("FRV.student6InCourse1");
        StudentAttributes student7 = responseVisibilityBundle.students.get("FRV.student7InCourse1");
        StudentAttributes student8 = responseVisibilityBundle.students.get("FRV.student8InCourse1");

        // stu -> self : instructors
        FeedbackQuestionAttributes fq11 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn1InSession1InCourse1");
        // stu -> stu : instructors, receiver
        FeedbackQuestionAttributes fq12 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn2InSession1InCourse1");
        // self -> none : other students, instructors
        FeedbackQuestionAttributes fq13 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn3InSession1InCourse1");
        // team -> ins : instructors, other students
        FeedbackQuestionAttributes fq16 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn6InSession1InCourse1");
        // stu -> stu in same section : own team members, receiver team members
        FeedbackQuestionAttributes fq17 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn7InSession1InCourse1");
        // ins -> ins : -
        FeedbackQuestionAttributes fq18 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn8InSession1InCourse1");
        // ins -> ins team : instructors
        FeedbackQuestionAttributes fq19 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn9InSession1InCourse1");
        // team -> team : receiver
        FeedbackQuestionAttributes fq21 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn1InSession2InCourse1");
        // stu -> own team mem : receiver, own team members
        FeedbackQuestionAttributes fq22 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn2InSession2InCourse1");
        // ins -> stu : receiver, instructors, other students, receiver's team members
        FeedbackQuestionAttributes fq23 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn3InSession2InCourse1");
        // stu -> ins : receiver
        FeedbackQuestionAttributes fq24 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn4InSession2InCourse1");
        // stu -> own team : own team members, instructors
        FeedbackQuestionAttributes fq25 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn5InSession2InCourse1");
        // stu -> team : other students
        FeedbackQuestionAttributes fq26 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn6InSession2InCourse1");
        // team -> stu in same section : instructors
        FeedbackQuestionAttributes fq27 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn7InSession2InCourse1");
        // team -> team in same section : receiver
        FeedbackQuestionAttributes fq28 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn8InSession2InCourse1");
        // stu -> team in same section : instructors
        FeedbackQuestionAttributes fq29 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn9InSession2InCourse1");
        // stu -> team excluding self : instructors, receiver
        FeedbackQuestionAttributes fq30 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn11InSession2InCourse1");
        // ins -> team : instructors
        FeedbackQuestionAttributes fq20 = responseVisibilityBundle.feedbackQuestions.get("FRV.qn10InSession2InCourse1");

        // stu1 -> stu1 (self feedback)
        FeedbackResponseAttributes fr111 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ1S1C1");
        // stu5 -> stu5 (self feedback)
        FeedbackResponseAttributes fr112 = responseVisibilityBundle.feedbackResponses.get("FRV.response2ForQ1S1C1");
        // stu2 -> stu5
        FeedbackResponseAttributes fr121 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ2S1C1");
        // stu5 -> stu2
        FeedbackResponseAttributes fr122 = responseVisibilityBundle.feedbackResponses.get("FRV.response2ForQ2S1C1");
        // stu3 -> stu2
        FeedbackResponseAttributes fr123 = responseVisibilityBundle.feedbackResponses.get("FRV.response3ForQ2S1C1");
        // ins1 -> General
        FeedbackResponseAttributes fr131 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ3S1C1");
        // team2 -> ins2
        FeedbackResponseAttributes fr161 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ6S1C1");
        // team3 -> ins2
        FeedbackResponseAttributes fr162 = responseVisibilityBundle.feedbackResponses.get("FRV.response2ForQ6S1C1");
        // stu1 -> stu6
        FeedbackResponseAttributes fr171 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ7S1C1");
        // ins1 -> ins2
        FeedbackResponseAttributes fr181 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ8S1C1");
        // ins2 -> ins team
        FeedbackResponseAttributes fr191 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ9S1C1");
        // team1 -> team2
        FeedbackResponseAttributes fr211 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ1S2C1");
        // stu4 -> stu3
        FeedbackResponseAttributes fr221 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ2S2C1");
        // ins1 -> stu3
        FeedbackResponseAttributes fr231 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ3S2C1");
        // ins6 -> stu5
        FeedbackResponseAttributes fr232 = responseVisibilityBundle.feedbackResponses.get("FRV.response2ForQ3S2C1");
        // stu1 -> ins1
        FeedbackResponseAttributes fr241 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ4S2C1");
        // stu1 -> ins4
        FeedbackResponseAttributes fr242 = responseVisibilityBundle.feedbackResponses.get("FRV.response2ForQ4S2C1");
        // stu6 -> team3 (own team)
        FeedbackResponseAttributes fr251 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ5S2C1");
        // stu5 -> team2 (own team)
        FeedbackResponseAttributes fr252 = responseVisibilityBundle.feedbackResponses.get("FRV.response2ForQ5S2C1");
        // stu1 -> team3
        FeedbackResponseAttributes fr261 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ6S2C1");
        // team1 -> stu7
        FeedbackResponseAttributes fr271 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ7S2C1");
        // team3 -> team1
        FeedbackResponseAttributes fr281 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ8S2C1");
        // stu8 -> team1
        FeedbackResponseAttributes fr291 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ9S2C1");
        // stu1 -> team2
        FeedbackResponseAttributes fr2111 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ11S2C1");
        // stu6 -> team1
        FeedbackResponseAttributes fr2112 = responseVisibilityBundle.feedbackResponses.get("FRV.response2ForQ11S2C1");
        // stu7 -> team2
        FeedbackResponseAttributes fr2113 = responseVisibilityBundle.feedbackResponses.get("FRV.response3ForQ11S2C1");
        // ins2 -> team2
        FeedbackResponseAttributes fr201 = responseVisibilityBundle.feedbackResponses.get("FRV.response1ForQ10S2C1");

        Set<String> studentsEmailInTeam1 = new HashSet<>(
                Arrays.asList(student1.getEmail(), student2.getEmail(), student3.getEmail(), student4.getEmail()));
        Set<String> studentsEmailInTeam2 = new HashSet<>(Arrays.asList(student5.getEmail()));
        Set<String> studentsEmailInTeam3 = new HashSet<>(Arrays.asList(student6.getEmail(), student7.getEmail()));
        Set<String> studentsEmailInTeam4 = new HashSet<>(Arrays.asList(student8.getEmail()));
        Set<String> studentsEmailEmpty = Collections.emptySet();

        ______TS("test if visible to giver");

        assertTrue(frLogic.isResponseVisibleForUser(student1.getEmail(), false, student1, studentsEmailInTeam1,
                fr111, fq11, null));
        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, null,
                fr131, fq13, instructor1));
        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr181, fq18, instructor1));
        assertTrue(frLogic.isResponseVisibleForUser(instructor2.getEmail(), true, null, studentsEmailEmpty,
                fr191, fq19, instructor2));
        assertTrue(frLogic.isResponseVisibleForUser(student2.getEmail(), false, student2, studentsEmailInTeam1,
                fr211, fq21, null));
        assertTrue(frLogic.isResponseVisibleForUser(student4.getEmail(), false, student4, studentsEmailInTeam1,
                fr221, fq22, null));
        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr231, fq23, instructor1));
        assertTrue(frLogic.isResponseVisibleForUser(student1.getEmail(), false, student1, studentsEmailInTeam1,
                fr2111, fq30, null));
        assertTrue(frLogic.isResponseVisibleForUser(student6.getEmail(), false, student6, studentsEmailInTeam3,
                fr2112, fq30, null));
        assertTrue(frLogic.isResponseVisibleForUser(student7.getEmail(), false, student7, studentsEmailInTeam3,
                fr2113, fq30, null));

        ______TS("test if visible to other students");

        assertTrue(frLogic.isResponseVisibleForUser(student3.getEmail(), false, student3, studentsEmailInTeam1,
                fr131, fq13, null));
        assertTrue(frLogic.isResponseVisibleForUser(student5.getEmail(), false, student5, studentsEmailInTeam2,
                fr161, fq16, null));
        assertTrue(frLogic.isResponseVisibleForUser(student5.getEmail(), false, student5, studentsEmailInTeam2,
                fr211, fq21, null));
        assertTrue(frLogic.isResponseVisibleForUser(student7.getEmail(), false, student7, studentsEmailInTeam3,
                fr231, fq23, null));
        assertTrue(frLogic.isResponseVisibleForUser(student5.getEmail(), false, student5, studentsEmailInTeam2,
                fr261, fq26, null));
        assertFalse(frLogic.isResponseVisibleForUser(student1.getEmail(), false, student1, studentsEmailInTeam1,
                fr112, fq11, null));
        assertFalse(frLogic.isResponseVisibleForUser(student1.getEmail(), false, student1, studentsEmailInTeam1,
                fr121, fq12, null));
        assertFalse(frLogic.isResponseVisibleForUser(student5.getEmail(), false, student5, studentsEmailInTeam2,
                fr171, fq17, null));
        assertFalse(frLogic.isResponseVisibleForUser(student7.getEmail(), false, student7, studentsEmailInTeam3,
                fr291, fq29, null));

        ______TS("test if visible to receiver");

        assertTrue(frLogic.isResponseVisibleForUser(student2.getEmail(), false, student2, studentsEmailInTeam1,
                fr122, fq12, null));
        // receiver's team members include receiver
        assertTrue(frLogic.isResponseVisibleForUser(student6.getEmail(), false, student6, studentsEmailInTeam3,
                fr171, fq17, null));
        assertTrue(frLogic.isResponseVisibleForUser(student5.getEmail(), false, student5, studentsEmailInTeam2,
                fr211, fq21, null));
        assertTrue(frLogic.isResponseVisibleForUser(student3.getEmail(), false, student3, studentsEmailInTeam1,
                fr221, fq22, null));
        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr241, fq24, instructor1));
        // other students include receiver
        assertTrue(frLogic.isResponseVisibleForUser(student6.getEmail(), false, student6, studentsEmailInTeam3,
                fr261, fq26, null));
        assertTrue(frLogic.isResponseVisibleForUser(student1.getEmail(), false, student1, studentsEmailInTeam1,
                fr281, fq28, null));
        assertFalse(frLogic.isResponseVisibleForUser(instructor2.getEmail(), true, null, studentsEmailEmpty,
                fr181, fq18, instructor2));
        assertFalse(frLogic.isResponseVisibleForUser(student7.getEmail(), false, student7, studentsEmailInTeam3,
                fr271, fq27, null));
        assertFalse(frLogic.isResponseVisibleForUser(student8.getEmail(), false, student8, studentsEmailInTeam4,
                fr281, fq28, null));
        assertFalse(frLogic.isResponseVisibleForUser(student5.getEmail(), false, student5, studentsEmailInTeam2,
                fr201, fq20, null));
        assertTrue(frLogic.isResponseVisibleForUser(student6.getEmail(), false, student6, studentsEmailInTeam3,
                fr2111, fq30, null));
        assertTrue(frLogic.isResponseVisibleForUser(student7.getEmail(), false, student7, studentsEmailInTeam3,
                fr2111, fq30, null));
        assertTrue(frLogic.isResponseVisibleForUser(student1.getEmail(), false, student1, studentsEmailInTeam1,
                fr2112, fq30, null));
        assertTrue(frLogic.isResponseVisibleForUser(student2.getEmail(), false, student2, studentsEmailInTeam1,
                fr2112, fq30, null));
        assertTrue(frLogic.isResponseVisibleForUser(student3.getEmail(), false, student3, studentsEmailInTeam1,
                fr2112, fq30, null));
        assertTrue(frLogic.isResponseVisibleForUser(student4.getEmail(), false, student4, studentsEmailInTeam1,
                fr2112, fq30, null));
        assertTrue(frLogic.isResponseVisibleForUser(student4.getEmail(), false, student5, studentsEmailInTeam2,
                fr2113, fq30, null));

        ______TS("test if visible to giver's team members");

        assertTrue(frLogic.isResponseVisibleForUser(student2.getEmail(), false, student2, studentsEmailInTeam1,
                fr171, fq17, null));
        assertTrue(frLogic.isResponseVisibleForUser(student1.getEmail(), false, student1, studentsEmailInTeam1,
                fr221, fq22, null));
        assertTrue(frLogic.isResponseVisibleForUser(student7.getEmail(), false, student7, studentsEmailInTeam3,
                fr251, fq25, null));
        // other students include giver's team members
        assertTrue(frLogic.isResponseVisibleForUser(student3.getEmail(), false, student3, studentsEmailInTeam1,
                fr261, fq26, null));
        assertFalse(frLogic.isResponseVisibleForUser(student3.getEmail(), false, student3, studentsEmailInTeam1,
                fr111, fq11, null));
        assertFalse(frLogic.isResponseVisibleForUser(student4.getEmail(), false, student4, studentsEmailInTeam1,
                fr123, fq12, null));
        assertFalse(frLogic.isResponseVisibleForUser(student2.getEmail(), false, student2, studentsEmailInTeam1,
                fr241, fq24, null));
        // invalid usage
        assertFalse(frLogic.isResponseVisibleForUser(student8.getEmail(), false, student8, null,
                fr281, fq28, null));

        ______TS("test if visible to receiver's team members");

        assertTrue(frLogic.isResponseVisibleForUser(student7.getEmail(), false, student7, studentsEmailInTeam3,
                fr171, fq17, null));
        assertTrue(frLogic.isResponseVisibleForUser(student1.getEmail(), false, student1, studentsEmailInTeam1,
                fr231, fq23, null));
        assertFalse(frLogic.isResponseVisibleForUser(student2.getEmail(), false, student2, studentsEmailInTeam1,
                fr111, fq11, null));
        assertFalse(frLogic.isResponseVisibleForUser(student4.getEmail(), false, student4, studentsEmailInTeam1,
                fr122, fq12, null));
        assertFalse(frLogic.isResponseVisibleForUser(student6.getEmail(), false, student6, studentsEmailInTeam3,
                fr271, fq27, null));

        ______TS("test if visible to instructors");

        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr111, fq11, instructor1));
        assertTrue(frLogic.isResponseVisibleForUser(instructor2.getEmail(), true, null, studentsEmailEmpty,
                fr123, fq12, instructor2));
        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr131, fq13, instructor1));
        assertTrue(frLogic.isResponseVisibleForUser(instructor2.getEmail(), true, null, studentsEmailEmpty,
                fr161, fq16, instructor2));
        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr191, fq19, instructor1));
        assertTrue(frLogic.isResponseVisibleForUser(instructor2.getEmail(), true, null, studentsEmailEmpty,
                fr231, fq23, instructor2));
        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr271, fq27, instructor1));
        assertFalse(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr171, fq17, instructor1));
        assertFalse(frLogic.isResponseVisibleForUser(instructor2.getEmail(), true, null, studentsEmailEmpty,
                fr181, fq18, instructor2));
        assertFalse(frLogic.isResponseVisibleForUser(instructor2.getEmail(), true, null, studentsEmailEmpty,
                fr211, fq21, instructor2));
        assertFalse(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr221, fq22, instructor1));

        assertFalse(frLogic.isResponseVisibleForUser(instructor3.getEmail(), true, null, studentsEmailEmpty,
                fr123, fq12, instructor3));
        assertFalse(frLogic.isResponseVisibleForUser(instructor3.getEmail(), true, null, studentsEmailEmpty,
                fr161, fq16, instructor3));
        assertFalse(frLogic.isResponseVisibleForUser(instructor3.getEmail(), true, null, studentsEmailEmpty,
                fr191, fq19, instructor3));
        assertFalse(frLogic.isResponseVisibleForUser(instructor3.getEmail(), true, null, studentsEmailEmpty,
                fr251, fq25, instructor3));

        assertTrue(frLogic.isResponseVisibleForUser(instructor4.getEmail(), true, null, studentsEmailEmpty,
                fr112, fq11, instructor4));
        assertTrue(frLogic.isResponseVisibleForUser(instructor4.getEmail(), true, null, studentsEmailEmpty,
                fr252, fq25, instructor4));
        assertFalse(frLogic.isResponseVisibleForUser(instructor4.getEmail(), true, null, studentsEmailEmpty,
                fr121, fq12, instructor4));
        assertFalse(frLogic.isResponseVisibleForUser(instructor4.getEmail(), true, null, studentsEmailEmpty,
                fr122, fq12, instructor4));
        assertFalse(frLogic.isResponseVisibleForUser(instructor4.getEmail(), true, null, studentsEmailEmpty,
                fr211, fq21, instructor4));
        assertFalse(frLogic.isResponseVisibleForUser(instructor4.getEmail(), true, null, studentsEmailEmpty,
                fr242, fq24, instructor4));
        assertFalse(frLogic.isResponseVisibleForUser(instructor4.getEmail(), true, null, studentsEmailEmpty,
                fr271, fq27, instructor4));

        assertTrue(frLogic.isResponseVisibleForUser(instructor5.getEmail(), true, null, studentsEmailEmpty,
                fr162, fq16, instructor5));
        assertTrue(frLogic.isResponseVisibleForUser(instructor5.getEmail(), true, null, studentsEmailEmpty,
                fr191, fq19, instructor5));
        assertTrue(frLogic.isResponseVisibleForUser(instructor5.getEmail(), true, null, studentsEmailEmpty,
                fr271, fq27, instructor5));
        assertFalse(frLogic.isResponseVisibleForUser(instructor5.getEmail(), true, null, studentsEmailEmpty,
                fr121, fq12, instructor5));
        assertFalse(frLogic.isResponseVisibleForUser(instructor5.getEmail(), true, null, studentsEmailEmpty,
                fr122, fq12, instructor5));
        assertFalse(frLogic.isResponseVisibleForUser(instructor5.getEmail(), true, null, studentsEmailEmpty,
                fr161, fq16, instructor5));

        assertTrue(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr111, fq11, instructor6));
        assertTrue(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr123, fq12, instructor6));
        assertTrue(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr252, fq25, instructor6));
        assertFalse(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr112, fq11, instructor6));
        assertFalse(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr121, fq12, instructor6));
        // "general" involved is checked as a section
        assertFalse(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr131, fq13, instructor6));
        assertFalse(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr161, fq16, instructor6));
        // "instructors" involved is checked as a section
        assertFalse(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr162, fq16, instructor6));
        assertFalse(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr191, fq19, instructor6));
        assertFalse(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr232, fq23, instructor6));

        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr2111, fq30, instructor1));
        assertTrue(frLogic.isResponseVisibleForUser(instructor2.getEmail(), true, null, studentsEmailEmpty,
                fr2111, fq30, instructor2));
        assertFalse(frLogic.isResponseVisibleForUser(instructor3.getEmail(), true, null, studentsEmailEmpty,
                fr2111, fq30, instructor3));
        assertFalse(frLogic.isResponseVisibleForUser(instructor4.getEmail(), true, null, studentsEmailEmpty,
                fr2111, fq30, instructor4));
        assertTrue(frLogic.isResponseVisibleForUser(instructor5.getEmail(), true, null, studentsEmailEmpty,
                fr2111, fq30, instructor5));
        assertFalse(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr2111, fq30, instructor6));

        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr2112, fq30, instructor1));
        assertTrue(frLogic.isResponseVisibleForUser(instructor2.getEmail(), true, null, studentsEmailEmpty,
                fr2112, fq30, instructor2));
        assertFalse(frLogic.isResponseVisibleForUser(instructor3.getEmail(), true, null, studentsEmailEmpty,
                fr2112, fq30, instructor3));
        assertFalse(frLogic.isResponseVisibleForUser(instructor4.getEmail(), true, null, studentsEmailEmpty,
                fr2112, fq30, instructor4));
        assertTrue(frLogic.isResponseVisibleForUser(instructor5.getEmail(), true, null, studentsEmailEmpty,
                fr2112, fq30, instructor5));
        assertFalse(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr2112, fq30, instructor6));

        assertTrue(frLogic.isResponseVisibleForUser(instructor1.getEmail(), true, null, studentsEmailEmpty,
                fr2113, fq30, instructor1));
        assertTrue(frLogic.isResponseVisibleForUser(instructor2.getEmail(), true, null, studentsEmailEmpty,
                fr2113, fq30, instructor2));
        assertFalse(frLogic.isResponseVisibleForUser(instructor3.getEmail(), true, null, studentsEmailEmpty,
                fr2113, fq30, instructor3));
        assertFalse(frLogic.isResponseVisibleForUser(instructor4.getEmail(), true, null, studentsEmailEmpty,
                fr2113, fq30, instructor4));
        assertFalse(frLogic.isResponseVisibleForUser(instructor5.getEmail(), true, null, studentsEmailEmpty,
                fr2113, fq30, instructor5));
        assertFalse(frLogic.isResponseVisibleForUser(instructor6.getEmail(), true, null, studentsEmailEmpty,
                fr2113, fq30, instructor6));
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
                fq.getId(), null, FeedbackResultFetchType.BOTH);
        assertEquals(1, bundle.getQuestionResponseMap().size());
        List<FeedbackResponseAttributes> responseForQuestion =
                bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(1, responseForQuestion.size());

        // section specified
        fq = getQuestionFromDatabase("qn2InSession1InCourse1");
        bundle = frLogic.getSessionResultsForCourse(
                fq.getFeedbackSessionName(), fq.getCourseId(), instructor.getEmail(),
                fq.getId(), "Section 1", FeedbackResultFetchType.BOTH);
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
                null, null, FeedbackResultFetchType.BOTH);

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
                null, "Section A", FeedbackResultFetchType.BOTH);

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

    @Test
    public void testGetSessionResultsForCourse_responseFetchByGiverOrReceiverOnly_shouldGenerateCorrectBundle() {
        var responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        var session = responseBundle.feedbackSessions.get("standard.session");

        var instructor = responseBundle.instructors.get("instructor1OfCourse1");
        var sectionToTest = "Section A";
        Map<String, List<FeedbackResponseAttributes>> questionResponseMapByGiver = frLogic.getSessionResultsForCourse(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.getEmail(),
                null, sectionToTest, FeedbackResultFetchType.GIVER)
                .getQuestionResponseMap();
        questionResponseMapByGiver.forEach((key, responses) -> {
            responses.forEach(resp -> {
                assertEquals(sectionToTest, resp.getGiverSection());
            });
        });

        Map<String, List<FeedbackResponseAttributes>> questionResponseMapByReceiver =
                frLogic.getSessionResultsForCourse(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.getEmail(),
                null, sectionToTest, FeedbackResultFetchType.RECEIVER)
                .getQuestionResponseMap();
        questionResponseMapByReceiver.forEach((key, responses) -> {
            responses.forEach(resp -> {
                assertEquals(sectionToTest, resp.getRecipientSection());
            });
        });
    }

    @Test
    public void testGetSessionResultsForCourse_splitResponseFetchByGiverAndReceiver_shouldGenerateCorrectBundle() {
        var responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("standard.session");

        InstructorAttributes instructor = responseBundle.instructors.get("instructor1OfCourse1");
        Map<String, List<FeedbackResponseAttributes>> questionResponseMapFromMultiFetch =
                frLogic.getSessionResultsForCourse(
                        session.getFeedbackSessionName(), session.getCourseId(), instructor.getEmail(),
                        null, "Section A", FeedbackResultFetchType.GIVER).getQuestionResponseMap();
        frLogic.getSessionResultsForCourse(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.getEmail(),
                null, "Section A", FeedbackResultFetchType.RECEIVER)
                .getQuestionResponseMap()
                .forEach(questionResponseMapFromMultiFetch::putIfAbsent);

        // Equal to session result fetch by both type
        Map<String, List<FeedbackResponseAttributes>> questionResponseMapFromFetchBoth =
                frLogic.getSessionResultsForCourse(
                        session.getFeedbackSessionName(), session.getCourseId(), instructor.getEmail(),
                        null, "Section A", FeedbackResultFetchType.BOTH).getQuestionResponseMap();

        for (var entry : questionResponseMapFromFetchBoth.entrySet()) {
            List<FeedbackResponseAttributes> respFromFetchBoth = entry.getValue();
            List<FeedbackResponseAttributes> respFromMultiFetch = questionResponseMapFromMultiFetch.get(entry.getKey());
            assertEquals(respFromFetchBoth.size(), respFromMultiFetch.size());
            assertTrue(new HashSet<>(respFromMultiFetch).equals(new HashSet<>(respFromFetchBoth)));
        }
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
