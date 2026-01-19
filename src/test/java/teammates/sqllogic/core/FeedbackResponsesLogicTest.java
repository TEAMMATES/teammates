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
    private UsersLogic usersLogic;
    private FeedbackQuestionsLogic fqLogic;
    private FeedbackResponseCommentsLogic frcLogic;

    @BeforeMethod
    public void setUpMethod() {
        frDb = mock(FeedbackResponsesDb.class);
        usersLogic = mock(UsersLogic.class);
        fqLogic = mock(FeedbackQuestionsLogic.class);
        frcLogic = mock(FeedbackResponseCommentsLogic.class);
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

        when(frDb.getFeedbackResponse(responseId)).thenReturn(response);

        FeedbackResponse result = frLogic.getFeedbackResponse(responseId);

        assertEquals(response, result);
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
        FeedbackResponse response2 = getTypicalFeedbackResponseForQuestion(question);
        List<FeedbackResponse> responses = List.of(response1, response2);

        when(frDb.getResponsesForQuestion(questionId)).thenReturn(responses);

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesForQuestion(questionId);

        assertEquals(2, result.size());
        assertTrue(result.contains(response1));
        assertTrue(result.contains(response2));
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
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        FeedbackResponse response = getTypicalFeedbackResponseForQuestion(question);
        List<FeedbackResponse> responses = List.of(response);

        when(frDb.getFeedbackResponsesFromGiverForCourse(courseId, giverEmail)).thenReturn(responses);

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesFromGiverForCourse(courseId, giverEmail);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    public void testGetFeedbackResponsesForRecipientForCourse_responsesExist_success() {
        String courseId = "course-id";
        String recipientEmail = "recipient@email.com";
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        FeedbackResponse response = getTypicalFeedbackResponseForQuestion(question);
        List<FeedbackResponse> responses = List.of(response);

        when(frDb.getFeedbackResponsesForRecipientForCourse(courseId, recipientEmail)).thenReturn(responses);

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesForRecipientForCourse(courseId, recipientEmail);

        assertEquals(1, result.size());
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

        assertEquals(1, result.size());
    }

    @Test
    public void testIsResponseOfFeedbackQuestionVisibleToStudent_visibleToStudents_returnsTrue() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setShowResponsesTo(List.of(FeedbackParticipantType.STUDENTS));

        boolean result = frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question);

        assertTrue(result);
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

        assertEquals(1, result.size());
    }

    @Test
    public void testGetFeedbackResponsesFromStudentOrTeamForQuestion_individualQuestion_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setId(UUID.randomUUID());
        question.setGiverType(FeedbackParticipantType.STUDENTS);
        Student student = getTypicalStudent();
        FeedbackResponse response = getTypicalFeedbackResponseForQuestion(question);

        when(frDb.getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getEmail()))
                .thenReturn(List.of(response));

        List<FeedbackResponse> result = frLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(question, student);

        assertEquals(1, result.size());
    }

    @Test
    public void testHasGiverRespondedForSession_withQuestions_hasResponded_returnsTrue() {
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
    public void testHasGiverRespondedForSession_withQuestions_hasNotResponded_returnsFalse() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        question.setFeedbackResponses(new ArrayList<>());
        List<FeedbackQuestion> questions = List.of(question);

        boolean result = frLogic.hasGiverRespondedForSession("student@email.com", questions);

        assertFalse(result);
    }
}
