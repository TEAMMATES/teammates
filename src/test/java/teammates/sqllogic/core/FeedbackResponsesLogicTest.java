package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.storage.sqlapi.FeedbackResponsesDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackResponsesLogic}.
 */
public class FeedbackResponsesLogicTest extends BaseTestCase {

    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    private FeedbackResponsesDb frDb;

    @BeforeMethod
    public void setUpMethod() {
        frDb = mock(FeedbackResponsesDb.class);
        UsersLogic usersLogic = mock(UsersLogic.class);
        FeedbackQuestionsLogic fqLogic = mock(FeedbackQuestionsLogic.class);
        FeedbackResponseCommentsLogic frcLogic = mock(FeedbackResponseCommentsLogic.class);
        frLogic.initLogicDependencies(frDb, usersLogic, fqLogic, frcLogic);
    }

    @Test
    public void testGetFeedbackResponse_responseExists_success() {
        UUID responseId = UUID.randomUUID();
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        FeedbackResponse response = getTypicalFeedbackResponseForQuestion(question);
        response.setId(responseId);
        String giver = response.getGiver();
        String recipient = response.getRecipient();

        when(frDb.getFeedbackResponse(responseId)).thenReturn(response);

        FeedbackResponse result = frLogic.getFeedbackResponse(responseId);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(giver, result.getGiver());
        assertEquals(recipient, result.getRecipient());
        verify(frDb, times(1)).getFeedbackResponse(responseId);
    }

    @Test
    public void testGetFeedbackResponse_responseDoesNotExist_returnsNull() {
        UUID nonExistentId = UUID.randomUUID();

        when(frDb.getFeedbackResponse(nonExistentId)).thenReturn(null);

        FeedbackResponse result = frLogic.getFeedbackResponse(nonExistentId);

        assertNull(result);
    }

    @Test
    public void testGetFeedbackResponsesForQuestion_responsesExist_success() {
        UUID questionId = UUID.randomUUID();
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setId(questionId);
        FeedbackResponse response1 = getTypicalFeedbackResponseForQuestion(question);
        response1.setId(UUID.randomUUID());
        FeedbackResponse response2 = getTypicalFeedbackResponseForQuestion(question);
        response2.setId(UUID.randomUUID());
        List<FeedbackResponse> responses = List.of(response1, response2);

        when(frDb.getResponsesForQuestion(questionId)).thenReturn(responses);

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesForQuestion(questionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));
        verify(frDb, times(1)).getResponsesForQuestion(questionId);
    }

    @Test
    public void testGetFeedbackResponsesForQuestion_noResponses_returnsEmptyList() {
        UUID questionId = UUID.randomUUID();

        when(frDb.getResponsesForQuestion(questionId)).thenReturn(new ArrayList<>());

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesForQuestion(questionId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testAreThereResponsesForQuestion_hasResponses_returnsTrue() {
        UUID questionId = UUID.randomUUID();

        when(frDb.areThereResponsesForQuestion(questionId)).thenReturn(true);

        boolean result = frLogic.areThereResponsesForQuestion(questionId);

        assertTrue(result);
    }

    @Test
    public void testAreThereResponsesForQuestion_noResponses_returnsFalse() {
        UUID questionId = UUID.randomUUID();

        when(frDb.areThereResponsesForQuestion(questionId)).thenReturn(false);

        boolean result = frLogic.areThereResponsesForQuestion(questionId);

        assertFalse(result);
    }

    @Test
    public void testHasResponsesForCourse_hasResponses_returnsTrue() {
        String courseId = "course-id";

        when(frDb.hasResponsesForCourse(courseId)).thenReturn(true);

        boolean result = frLogic.hasResponsesForCourse(courseId);

        assertTrue(result);
    }

    @Test
    public void testHasResponsesForCourse_noResponses_returnsFalse() {
        String courseId = "course-id";

        when(frDb.hasResponsesForCourse(courseId)).thenReturn(false);

        boolean result = frLogic.hasResponsesForCourse(courseId);

        assertFalse(result);
    }

    @Test
    public void testHasGiverRespondedForSession_hasResponded_returnsTrue() {
        String giver = "giver@email.com";
        String sessionName = "session";
        String courseId = "course";

        when(frDb.hasResponsesFromGiverInSession(giver, sessionName, courseId)).thenReturn(true);

        boolean result = frLogic.hasGiverRespondedForSession(giver, sessionName, courseId);

        assertTrue(result);
    }

    @Test
    public void testHasGiverRespondedForSession_hasNotResponded_returnsFalse() {
        String giver = "giver@email.com";
        String sessionName = "session";
        String courseId = "course";

        when(frDb.hasResponsesFromGiverInSession(giver, sessionName, courseId)).thenReturn(false);

        boolean result = frLogic.hasGiverRespondedForSession(giver, sessionName, courseId);

        assertFalse(result);
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForCourse_responsesExist_success() {
        String courseId = "course-id";
        String giverEmail = "giver@email.com";
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question1 = getTypicalFeedbackQuestionForSession(session);
        FeedbackQuestion question2 = getTypicalFeedbackQuestionForSession(session);
        FeedbackResponse response1 = getTypicalFeedbackResponseForQuestion(question1);
        response1.setGiver(giverEmail);
        FeedbackResponse response2 = getTypicalFeedbackResponseForQuestion(question2);
        response2.setGiver(giverEmail);
        List<FeedbackResponse> responses = List.of(response1, response2);

        when(frDb.getFeedbackResponsesFromGiverForCourse(courseId, giverEmail)).thenReturn(responses);

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesFromGiverForCourse(courseId, giverEmail);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));
        verify(frDb, times(1)).getFeedbackResponsesFromGiverForCourse(courseId, giverEmail);
    }

    @Test
    public void testGetFeedbackResponsesForRecipientForCourse_responsesExist_success() {
        String courseId = "course-id";
        String recipientEmail = "recipient@email.com";
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        FeedbackResponse response = getTypicalFeedbackResponseForQuestion(question);
        response.setRecipient(recipientEmail);
        List<FeedbackResponse> responses = List.of(response);

        when(frDb.getFeedbackResponsesForRecipientForCourse(courseId, recipientEmail)).thenReturn(responses);

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesForRecipientForCourse(courseId, recipientEmail);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
        assertEquals(recipientEmail, result.get(0).getRecipient());
        verify(frDb, times(1)).getFeedbackResponsesForRecipientForCourse(courseId, recipientEmail);
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForQuestion_responsesExist_success() {
        UUID questionId = UUID.randomUUID();
        String giverEmail = "giver@email.com";
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setId(questionId);
        FeedbackResponse response = getTypicalFeedbackResponseForQuestion(question);
        List<FeedbackResponse> responses = List.of(response);

        when(frDb.getFeedbackResponsesFromGiverForQuestion(questionId, giverEmail)).thenReturn(responses);

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesFromGiverForQuestion(questionId, giverEmail);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    public void testIsResponseOfFeedbackQuestionVisibleToStudent_visibleToStudents_returnsTrue() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setShowResponsesTo(List.of(FeedbackParticipantType.STUDENTS));

        boolean result = frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question);

        assertTrue(result);
        assertTrue(question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS));
    }

    @Test
    public void testIsResponseOfFeedbackQuestionVisibleToStudent_notVisibleToStudents_returnsFalse() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        question.setRecipientType(FeedbackParticipantType.INSTRUCTORS);
        question.setGiverType(FeedbackParticipantType.INSTRUCTORS);

        boolean result = frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question);

        assertFalse(result);
        assertFalse(question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS));
    }

    @Test
    public void testIsResponseOfFeedbackQuestionVisibleToInstructor_visibleToInstructors_returnsTrue() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));

        boolean result = frLogic.isResponseOfFeedbackQuestionVisibleToInstructor(question);

        assertTrue(result);
    }

    @Test
    public void testIsResponseOfFeedbackQuestionVisibleToInstructor_notVisibleToInstructors_returnsFalse() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setShowResponsesTo(List.of(FeedbackParticipantType.STUDENTS));

        boolean result = frLogic.isResponseOfFeedbackQuestionVisibleToInstructor(question);

        assertFalse(result);
    }

    @Test
    public void testDeleteFeedbackResponsesForQuestionCascade_success() {
        UUID questionId = UUID.randomUUID();

        frLogic.deleteFeedbackResponsesForQuestionCascade(questionId);

        verify(frDb, times(1)).deleteFeedbackResponsesForQuestionCascade(questionId);
    }

    @Test
    public void testDeleteFeedbackResponsesAndCommentsCascade_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        FeedbackResponse response = getTypicalFeedbackResponseForQuestion(question);

        frLogic.deleteFeedbackResponsesAndCommentsCascade(response);

        verify(frDb, times(1)).deleteFeedbackResponse(response);
    }

    @Test
    public void testGetFeedbackResponsesFromInstructorForQuestion_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setId(UUID.randomUUID());
        Instructor instructor = getTypicalInstructor();
        FeedbackResponse response = getTypicalFeedbackResponseForQuestion(question);

        when(frDb.getFeedbackResponsesFromGiverForQuestion(question.getId(), instructor.getEmail()))
                .thenReturn(List.of(response));

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesFromInstructorForQuestion(question, instructor);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    public void testGetFeedbackResponsesFromStudentOrTeamForQuestion_individualQuestion_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        UUID questionId = UUID.randomUUID();
        question.setId(questionId);
        question.setGiverType(FeedbackParticipantType.STUDENTS);
        Student student = getTypicalStudent();
        String studentEmail = student.getEmail();
        FeedbackResponse response = getTypicalFeedbackResponseForQuestion(question);
        response.setGiver(studentEmail);

        when(frDb.getFeedbackResponsesFromGiverForQuestion(questionId, studentEmail))
                .thenReturn(List.of(response));

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(question, student);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
        verify(frDb, times(1)).getFeedbackResponsesFromGiverForQuestion(questionId, studentEmail);
    }

    @Test
    public void testHasGiverRespondedForSession_hasResponded() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        FeedbackResponse response = getTypicalFeedbackResponseForQuestion(question);
        response.setGiver("student@email.com");
        question.setFeedbackResponses(List.of(response));
        List<FeedbackQuestion> questions = List.of(question);

        boolean result = frLogic.hasGiverRespondedForSession("student@email.com", questions);

        assertTrue(result);
    }

    @Test
    public void testHasGiverRespondedForSession_hasNotResponded() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setFeedbackResponses(new ArrayList<>());
        List<FeedbackQuestion> questions = List.of(question);

        boolean result = frLogic.hasGiverRespondedForSession("student@email.com", questions);

        assertFalse(result);
    }
}
