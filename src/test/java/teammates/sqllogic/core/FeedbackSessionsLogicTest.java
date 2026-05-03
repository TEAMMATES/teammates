package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlapi.FeedbackSessionsDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.BaseTestCase;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackSessionUpdateRequest;

/**
 * SUT: {@link FeedbackSessionsLogic}.
 */
public class FeedbackSessionsLogicTest extends BaseTestCase {

    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    private FeedbackSessionsDb fsDb;
    private CoursesLogic coursesLogic;
    private FeedbackQuestionsLogic fqLogic;
    private UsersLogic usersLogic;

    @BeforeMethod
    public void setUpMethod() {
        fsDb = mock(FeedbackSessionsDb.class);
        coursesLogic = mock(CoursesLogic.class);
        FeedbackResponsesLogic frLogic = mock(FeedbackResponsesLogic.class);
        fqLogic = mock(FeedbackQuestionsLogic.class);
        usersLogic = mock(UsersLogic.class);
        fsLogic.initLogicDependencies(fsDb, coursesLogic, frLogic, fqLogic, usersLogic);
    }

    @Test
    public void testGetFeedbackSession_sessionExists_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);

        when(fsDb.getFeedbackSession(sessionId)).thenReturn(session);

        FeedbackSession result = fsLogic.getFeedbackSession(sessionId);

        assertNotNull(result);
        assertEquals(session, result);
        verify(fsDb, times(1)).getFeedbackSession(sessionId);
    }

    @Test
    public void testGetFeedbackSession_sessionDoesNotExist_returnsNull() {
        UUID nonExistentId = UUID.randomUUID();

        when(fsDb.getFeedbackSession(nonExistentId)).thenReturn(null);

        FeedbackSession result = fsLogic.getFeedbackSession(nonExistentId);

        assertNull(result);
        verify(fsDb, times(1)).getFeedbackSession(nonExistentId);
    }

    @Test
    public void testGetFeedbackSessionByNameAndCourse_sessionExists_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        String sessionName = session.getName();
        String courseId = course.getId();

        when(fsDb.getFeedbackSession(sessionName, courseId)).thenReturn(session);

        FeedbackSession result = fsLogic.getFeedbackSession(sessionName, courseId);

        assertNotNull(result);
        assertEquals(session, result);
        verify(fsDb, times(1)).getFeedbackSession(sessionName, courseId);
    }

    @Test
    public void testGetFeedbackSessionsForCourse_hasSession_success() {
        Course course = getTypicalCourse();
        FeedbackSession session1 = getTypicalFeedbackSessionForCourse(course);
        FeedbackSession session2 = getTypicalFeedbackSessionForCourse(course);
        List<FeedbackSession> sessions = List.of(session1, session2);

        when(fsDb.getFeedbackSessionEntitiesForCourse(course.getId())).thenReturn(sessions);

        List<FeedbackSession> result = fsLogic.getFeedbackSessionsForCourse(course.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(session1));
        assertTrue(result.contains(session2));
    }

    @Test
    public void testGetFeedbackSessionsForCourse_noSessions_returnsEmptyList() {
        String courseId = "non-existent-course";

        when(fsDb.getFeedbackSessionEntitiesForCourse(courseId)).thenReturn(new ArrayList<>());

        List<FeedbackSession> result = fsLogic.getFeedbackSessionsForCourse(courseId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetFeedbackSessionFromRecycleBin_sessionInRecycleBin_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setDeletedAt(Instant.now());

        when(fsDb.getSoftDeletedFeedbackSession(session.getName(), course.getId())).thenReturn(session);

        FeedbackSession result = fsLogic.getFeedbackSessionFromRecycleBin(session.getName(), course.getId());

        assertNotNull(result);
        assertEquals(session, result);
        assertNotNull(result.getDeletedAt());
    }

    @Test
    public void testGetFeedbackSessionFromRecycleBin_sessionNotInRecycleBin_returnsNull() {
        when(fsDb.getSoftDeletedFeedbackSession("session", "course")).thenReturn(null);

        FeedbackSession result = fsLogic.getFeedbackSessionFromRecycleBin("session", "course");

        assertNull(result);
    }

    @Test
    public void testGetOngoingSessions_sessionsExist_success() {
        Instant rangeStart = Instant.now().minusSeconds(3600);
        Instant rangeEnd = Instant.now().plusSeconds(3600);
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        List<FeedbackSession> sessions = List.of(session);

        when(fsDb.getOngoingSessions(rangeStart, rangeEnd)).thenReturn(sessions);

        List<FeedbackSession> result = fsLogic.getOngoingSessions(rangeStart, rangeEnd);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(session, result.get(0));
    }

    @Test
    public void testPublishFeedbackSession_unpublishedSession_success()
            throws EntityDoesNotExistException, InvalidFeedbackSessionStateException {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setPublishedEmailSent(true);
        session.setResultsVisibleFromTime(Instant.now().plusSeconds(86400)); // Not yet published

        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        FeedbackSession result = fsLogic.publishFeedbackSession(session.getId());

        assertNotNull(result);
        assertEquals(session, result);
        assertTrue(result.isPublished());
        assertFalse(result.isPublishedEmailSent());
        assertNotNull(result.getResultsVisibleFromTime());
    }

    @Test
    public void testPublishFeedbackSession_alreadyPublished_throwsException() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setResultsVisibleFromTime(Instant.now().minusSeconds(3600)); // Already published

        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        InvalidFeedbackSessionStateException ex = assertThrows(InvalidFeedbackSessionStateException.class,
                () -> fsLogic.publishFeedbackSession(session.getId()));
        assertEquals("Feedback Session is already published.", ex.getMessage());
    }

    @Test
    public void testPublishFeedbackSession_sessionDoesNotExist_throwsException() {
        UUID nonExistentId = UUID.fromString("2da92144-63f3-4da5-9148-dbcbdef6dc2c");
        when(fsDb.getFeedbackSession(nonExistentId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.publishFeedbackSession(nonExistentId));
        assertEquals(String.format("Feedback session with id %s not found.", nonExistentId), ex.getMessage());
    }

    @Test
    public void testUnpublishFeedbackSession_publishedSession_success()
            throws EntityDoesNotExistException, InvalidFeedbackSessionStateException {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setPublishedEmailSent(false);
        session.setResultsVisibleFromTime(Instant.now().minusSeconds(3600)); // Published

        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        FeedbackSession result = fsLogic.unpublishFeedbackSession(session.getId());

        assertNotNull(result);
        assertEquals(session, result);
        assertFalse(result.isPublished());
        assertFalse(result.isPublishedEmailSent());
        assertEquals(Const.TIME_REPRESENTS_LATER, result.getResultsVisibleFromTime());
    }

    @Test
    public void testUnpublishFeedbackSession_notPublished_throwsException() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setResultsVisibleFromTime(Instant.now().plusSeconds(86400)); // Not published

        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        InvalidFeedbackSessionStateException ex = assertThrows(InvalidFeedbackSessionStateException.class,
                () -> fsLogic.unpublishFeedbackSession(session.getId()));
        assertEquals("Feedback Session is already unpublished.", ex.getMessage());
    }

    @Test
    public void testUnpublishFeedbackSession_sessionDoesNotExist_throwsException() {
        UUID nonExistentId = UUID.fromString("2da92144-63f3-4da5-9148-dbcbdef6dc2c");
        when(fsDb.getFeedbackSession(nonExistentId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.unpublishFeedbackSession(nonExistentId));
        assertEquals(String.format("Feedback session with id %s not found.", nonExistentId), ex.getMessage());
    }

    @Test
    public void testRestoreFeedbackSessionFromRecycleBin_sessionExists_success() throws EntityDoesNotExistException {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setDeletedAt(Instant.now());
        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        fsLogic.restoreFeedbackSessionFromRecycleBin(session.getId());

        assertNull(session.getDeletedAt());
    }

    @Test
    public void testDeleteFeedbackSessionCascade_sessionExists_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);

        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        fsLogic.deleteFeedbackSessionCascade(session.getId());

        verify(fsDb, times(1)).getFeedbackSession(session.getId());
        verify(fsDb, times(1)).deleteFeedbackSession(session);
    }

    @Test
    public void testIsFeedbackSessionAttemptedByStudent_noQuestions_returnsTrue() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setFeedbackQuestions(new ArrayList<>());
        Student student = getTypicalStudent();
        student.setTeam(getTypicalTeam());

        when(fqLogic.hasFeedbackQuestionsForStudents(session.getFeedbackQuestions())).thenReturn(false);

        boolean result = fsLogic.isFeedbackSessionAttemptedByStudent(session, student.getEmail(), student.getTeamName());

        assertTrue(result);
    }

    @Test
    public void testIsFeedbackSessionViewableToUserType_hasQuestionsForUser_returnsTrue() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setSessionVisibleFromTime(Instant.now().minusSeconds(3600)); // Visible
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        session.setFeedbackQuestions(List.of(question));

        when(fqLogic.hasFeedbackQuestionsForStudents(session.getFeedbackQuestions())).thenReturn(true);

        boolean result = fsLogic.isFeedbackSessionViewableToUserType(session, false);

        assertTrue(result);
    }

    @Test
    public void testIsFeedbackSessionForUserTypeToAnswer_sessionNotVisible_returnsFalse() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setSessionVisibleFromTime(Instant.now().plusSeconds(86400)); // Not visible yet

        boolean result = fsLogic.isFeedbackSessionForUserTypeToAnswer(session, false);

        assertFalse(result);
    }

    @Test
    public void testGetExpectedTotalSubmission_sessionWithQuestions_success() {
        Course course = getTypicalCourse();
        String courseId = course.getId();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        List<FeedbackQuestion> questions = List.of(question);
        Student student1 = getTypicalStudent();
        Student student2 = getTypicalStudent();
        List<Student> students = List.of(student1, student2);
        session.setFeedbackQuestions(questions);

        when(fqLogic.hasFeedbackQuestionsForStudents(questions)).thenReturn(true);
        when(usersLogic.getStudentsForCourse(courseId)).thenReturn(students);
        when(fqLogic.hasFeedbackQuestionsForInstructors(questions, true)).thenReturn(false);

        int result = fsLogic.getExpectedTotalSubmission(session);

        assertEquals(2, result);
        verify(fqLogic, times(1)).hasFeedbackQuestionsForStudents(questions);
        verify(usersLogic, times(1)).getStudentsForCourse(courseId);
        verify(fqLogic, times(1)).hasFeedbackQuestionsForInstructors(questions, true);
    }

    @Test
    public void testGetExpectedTotalSubmission_withInstructors_includesInstructors() {
        Course course = getTypicalCourse();
        String courseId = course.getId();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        List<FeedbackQuestion> questions = List.of(question);
        List<Student> students = List.of(getTypicalStudent());
        Instructor instructor1 = getTypicalInstructor();
        Instructor instructor2 = getTypicalInstructor();
        List<Instructor> instructors = List.of(instructor1, instructor2);
        session.setFeedbackQuestions(questions);

        when(fqLogic.hasFeedbackQuestionsForStudents(questions)).thenReturn(true);
        when(usersLogic.getStudentsForCourse(courseId)).thenReturn(students);
        when(fqLogic.hasFeedbackQuestionsForInstructors(questions, true)).thenReturn(true);
        when(usersLogic.getInstructorsForCourse(courseId)).thenReturn(instructors);
        when(fqLogic.hasFeedbackQuestionsForInstructors(questions, false)).thenReturn(true);

        int result = fsLogic.getExpectedTotalSubmission(session);

        assertEquals(3, result); // 1 student + 2 instructors
        verify(usersLogic, times(1)).getInstructorsForCourse(courseId);
    }

    @Test
    public void testGetActualTotalSubmission_sessionWithResponses_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question1 = getTypicalFeedbackQuestionForSession(session);
        FeedbackQuestion question2 = getTypicalFeedbackQuestionForSession(session);
        session.setFeedbackQuestions(List.of(question1, question2));

        // Mock responses from different givers
        FeedbackResponse response1 = getTypicalFeedbackResponseForQuestion(question1);
        response1.setGiver("student1@email.com");
        FeedbackResponse response2 = getTypicalFeedbackResponseForQuestion(question1);
        response2.setGiver("student2@email.com");
        FeedbackResponse response3 = getTypicalFeedbackResponseForQuestion(question2);
        response3.setGiver("student1@email.com"); // Same giver as response1

        question1.setFeedbackResponses(List.of(response1, response2));
        question2.setFeedbackResponses(List.of(response3));

        int result = fsLogic.getActualTotalSubmission(session);

        // Should return unique givers: student1@email.com and student2@email.com = 2
        assertEquals(2, result);
    }

    @Test
    public void testGetActualTotalSubmission_noResponses_returnsZero() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion question = getTypicalFeedbackQuestionForSession(session);
        session.setFeedbackQuestions(List.of(question));
        question.setFeedbackResponses(new ArrayList<>());

        int result = fsLogic.getActualTotalSubmission(session);

        assertEquals(0, result);
    }

    @Test
    public void testGetFeedbackSessionsForInstructors_instructorHasSessions_success() {
        Course course = getTypicalCourse();
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        List<Instructor> instructors = List.of(instructor);

        when(coursesLogic.getCourse(instructor.getCourseId())).thenReturn(course);
        when(fsDb.getFeedbackSessionEntitiesForCourse(course.getId())).thenReturn(List.of(session));

        List<FeedbackSession> result = fsLogic.getFeedbackSessionsForInstructors(instructors);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(session, result.get(0));
        assertEquals(course.getId(), result.get(0).getCourseId());
    }

    @Test
    public void testGetSoftDeletedFeedbackSessionsForInstructors_hasSoftDeletedSessions_success() {
        Course course = getTypicalCourse();
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setDeletedAt(Instant.now());
        List<Instructor> instructors = List.of(instructor);

        when(coursesLogic.getCourse(instructor.getCourseId())).thenReturn(course);
        when(fsDb.getSoftDeletedFeedbackSessionsForCourse(course.getId())).thenReturn(List.of(session));

        List<FeedbackSession> result = fsLogic.getSoftDeletedFeedbackSessionsForInstructors(instructors);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(session, result.get(0));
        assertNotNull(result.get(0).getDeletedAt());
    }

    @Test
    public void testUpdateFeedbackSession_validUpdate_success() throws Exception {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();

        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        session = fsLogic.updateFeedbackSession(session.getId(), updateRequest);

        assertEquals(updateRequest.getInstructions(), session.getInstructions());
        assertEquals(updateRequest.getGracePeriod(), session.getGracePeriod());
        assertEquals(updateRequest.getSessionVisibleFromTime(), session.getSessionVisibleFromTime());
        assertEquals(updateRequest.getResultsVisibleFromTime(), session.getResultsVisibleFromTime());
        assertEquals(updateRequest.getSubmissionStartTime(), session.getStartTime());
        assertEquals(updateRequest.getSubmissionEndTime(), session.getEndTime());
        assertEquals(updateRequest.isClosingSoonEmailEnabled(), session.isClosingSoonEmailEnabled());
        assertEquals(updateRequest.isPublishedEmailEnabled(), session.isPublishedEmailEnabled());
    }

    @Test
    public void testUpdateFeedbackSession_sessionDoesNotExist_throwsEntityDoesNotExistException() {
        UUID nonExistentId = UUID.randomUUID();
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();

        when(fsDb.getFeedbackSession(nonExistentId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.updateFeedbackSession(nonExistentId, updateRequest));
        assertEquals(String.format("Feedback session with id %s not found.", nonExistentId), ex.getMessage());
    }

    @Test
    public void testUpdateFeedbackSession_invalidStartTime_throwsInvalidParametersException() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();
        // Start time more than 2 hours in the past
        Instant newStartTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantHoursOffsetFromNow(-3)
        );
        updateRequest.setSubmissionStartTimestamp(newStartTime.toEpochMilli());
        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> fsLogic.updateFeedbackSession(session.getId(), updateRequest));
        assertEquals("Invalid submission opening time: "
                + "The start time for this feedback session cannot "
                + "be earlier than 2 hours before now.", ex.getMessage());
    }

    @Test
    public void testUpdateFeedbackSession_invalidEndTime_throwsInvalidParametersException() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();
        // End time more than 1 hour in the past
        Instant newEndTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantHoursOffsetFromNow(-2)
        );
        updateRequest.setSubmissionEndTimestamp(newEndTime.toEpochMilli());
        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> fsLogic.updateFeedbackSession(session.getId(), updateRequest));
        assertEquals("Invalid submission closing time: "
                + "The end time for this feedback session cannot "
                + "be earlier than 1 hour before now.", ex.getMessage());
    }

    @Test
    public void testUpdateFeedbackSession_invalidSessionVisibleTime_throwsInvalidParametersException() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();
        // Session visible time more than 30 days before start time
        Instant newSessionVisibleTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantDaysOffsetFromNow(-40)
        );
        updateRequest.setCustomSessionVisibleTimestamp(newSessionVisibleTime.toEpochMilli());
        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> fsLogic.updateFeedbackSession(session.getId(), updateRequest));
        assertEquals("Invalid session visible time: "
                + "The time when the session will be visible for this feedback session "
                + "cannot be earlier than 30 days before start time.", ex.getMessage());
    }

    @Test
    public void testUpdateFeedbackSession_endTimeBeforeStartTime_throwsInvalidParametersException() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest();
        // End time before start time
        Instant newStartTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantHoursOffsetFromNow(1)
        );
        Instant newEndTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantHoursOffsetFromNow(-1)
        );
        updateRequest.setSubmissionEndTimestamp(newEndTime.toEpochMilli());
        updateRequest.setSubmissionStartTimestamp(newStartTime.toEpochMilli());
        when(fsDb.getFeedbackSession(session.getId())).thenReturn(session);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> fsLogic.updateFeedbackSession(session.getId(), updateRequest));
        assertEquals("Invalid submission closing time: "
                + "The end time for this feedback session "
                + "cannot be earlier than 1 hour before now.", ex.getMessage());
    }

    private FeedbackSessionUpdateRequest getTypicalFeedbackSessionUpdateRequest() {
        Instant newStartTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantHoursOffsetFromNow(1)
        );
        Instant newEndTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantHoursOffsetFromNow(24)
        );
        Instant newSessionVisibleFromTime = newStartTime;
        Instant newResultsVisibleFromTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantHoursOffsetFromNow(48)
        );

        FeedbackSessionUpdateRequest updateRequest = new FeedbackSessionUpdateRequest();
        updateRequest.setInstructions("new instructions");
        updateRequest.setGracePeriod(60);
        updateRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        updateRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        updateRequest.setSubmissionStartTimestamp(newStartTime.toEpochMilli());
        updateRequest.setSubmissionEndTimestamp(newEndTime.toEpochMilli());
        updateRequest.setCustomSessionVisibleTimestamp(newSessionVisibleFromTime.toEpochMilli());
        updateRequest.setCustomResponseVisibleTimestamp(newResultsVisibleFromTime.toEpochMilli());
        updateRequest.setClosingSoonEmailEnabled(false);
        updateRequest.setPublishedEmailEnabled(false);

        return updateRequest;
    }
}
