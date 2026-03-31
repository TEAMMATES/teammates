package teammates.sqlui.webapi;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.HasResponsesData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetHasResponsesAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetHasResponsesAction}.
 */
public class GetHasResponsesActionTest extends BaseActionTest<GetHasResponsesAction> {

    private Course typicalCourse;
    private Instructor typicalInstructor;
    private FeedbackSession typicalFeedbackSession;
    private FeedbackQuestion typicalFeedbackQuestion;
    private Student typicalStudent;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.HAS_RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUpMethod() {
        typicalCourse = getTypicalCourse();
        typicalInstructor = getTypicalInstructor();
        typicalFeedbackSession = getTypicalFeedbackSession(typicalCourse);
        typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        typicalStudent = getTypicalStudent();
    }

    @AfterMethod
    void tearDownMethod() {
        Mockito.reset(mockLogic);
    }

    @Test
    void testExecute_emptyParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_instructorWithNonExistentCourse_throwsEntityNotFoundException() {
        loginAsInstructor(typicalInstructor.getAccountId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, "non-existent course",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("No course with id: non-existent course", enfe.getMessage());
    }

    @Test
    void testExecute_instructorWithNonExistentFeedbackQuestion_throwsEntityNotFoundException() {
        loginAsInstructor(typicalInstructor.getAccountId());

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "00000000-0000-0000-0000-000000000000",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("No feedback question with id: 00000000-0000-0000-0000-000000000000", enfe.getMessage());
    }

    @Test
    void testExecute_instructorGetRespondentsInCourse_success() {
        loginAsInstructor(typicalInstructor.getAccountId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalInstructor.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        when(mockLogic.getCourse(typicalInstructor.getCourseId())).thenReturn(typicalCourse);

        // mock that the course has responses
        when(mockLogic.hasResponsesForCourse(typicalInstructor.getCourseId())).thenReturn(true);

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertTrue(hasResponsesData.getHasResponses());

        verify(mockLogic, times(1)).getCourse(typicalInstructor.getCourseId());
        verify(mockLogic, times(1)).hasResponsesForCourse(typicalInstructor.getCourseId());
    }

    @Test
    void testExecute_instructorGetRespondentsForQuestion_success() {
        loginAsInstructor(typicalInstructor.getAccountId());

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        // mock that the question has responses
        when(mockLogic.areThereResponsesForQuestion(typicalFeedbackQuestion.getId())).thenReturn(true);

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertTrue(hasResponsesData.getHasResponses());

        verify(mockLogic, times(1)).getFeedbackQuestion(typicalFeedbackQuestion.getId());
        verify(mockLogic, times(1))
                .areThereResponsesForQuestion(typicalFeedbackQuestion.getId());
    }

    @Test
    void testExecute_instructorWithQuestionIdAndCourseId_preferQuestionId() {
        loginAsInstructor(typicalInstructor.getAccountId());

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.COURSE_ID, typicalInstructor.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        when(mockLogic.getCourse(typicalInstructor.getCourseId())).thenReturn(typicalCourse);
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        // different results for question and course
        when(mockLogic.hasResponsesForCourse(typicalInstructor.getCourseId())).thenReturn(true);
        when(mockLogic.areThereResponsesForQuestion(typicalFeedbackQuestion.getId())).thenReturn(false);

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertFalse(hasResponsesData.getHasResponses());

        verify(mockLogic, times(1)).getFeedbackQuestion(typicalFeedbackQuestion.getId());
        verify(mockLogic, times(1))
                .areThereResponsesForQuestion(typicalFeedbackQuestion.getId());

        // path to get responses for course is not executed
        verify(mockLogic, never()).getCourse(typicalInstructor.getCourseId());
        verify(mockLogic, never()).hasResponsesForCourse(typicalInstructor.getCourseId());
    }

    @Test
    void testExecute_studentWithNonExistentFeedbackSession_throwsEntityNotFoundException() {
        loginAsStudent(typicalStudent.getAccountId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalStudent.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent session",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Feedback session not found", enfe.getMessage());

        verify(mockLogic, times(1))
                .getFeedbackSession("non-existent session", typicalStudent.getCourseId());
    }

    @Test
    void testExecute_studentGetHasRespondedForSession_success() {
        loginAsStudent(typicalStudent.getAccountId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalStudent.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalStudent.getCourseId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.getStudentByAccountId(typicalStudent.getCourseId(), typicalStudent.getAccountId()))
                .thenReturn(typicalStudent);

        // mock that the student has responded
        when(mockLogic.isFeedbackSessionAttemptedByStudent(
                typicalFeedbackSession, typicalStudent.getEmail(), typicalStudent.getTeamName()))
                .thenReturn(true);

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertTrue(hasResponsesData.getHasResponses());

        verify(mockLogic, times(1))
                .getFeedbackSession(typicalFeedbackSession.getName(), typicalStudent.getCourseId());
        verify(mockLogic, times(1))
                .getStudentByAccountId(typicalStudent.getCourseId(), typicalStudent.getAccountId());
        verify(mockLogic, times(1))
                .isFeedbackSessionAttemptedByStudent(
                        typicalFeedbackSession, typicalStudent.getEmail(), typicalStudent.getTeamName());
    }

    @Test
    void testExecute_studentGetHasRespondedForSessionWithoutFeedbackSessionNameParam_success() {
        loginAsStudent(typicalStudent.getAccountId());
        List<FeedbackSession> feedbackSessions = getTypicalFeedbackSessions(typicalCourse);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalStudent.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        when(mockLogic.getFeedbackSessionsForCourse(typicalCourse.getId())).thenReturn(feedbackSessions);
        when(mockLogic.getStudentByAccountId(typicalStudent.getCourseId(), typicalStudent.getAccountId()))
                .thenReturn(typicalStudent);

        // mock that student has responded to all feedback sessions
        for (FeedbackSession feedbackSession : feedbackSessions) {
            when(mockLogic.isFeedbackSessionAttemptedByStudent(
                    feedbackSession, typicalStudent.getEmail(), typicalStudent.getTeamName())).thenReturn(true);
        }

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        Map<String, Boolean> responseStats = hasResponsesData.getHasResponsesBySessions();

        Map<String, Boolean> expectedResponseStats = new HashMap<>();

        // student has responded here
        expectedResponseStats.put("First feedback session", true);
        expectedResponseStats.put("Second feedback session", true);
        expectedResponseStats.put("Third feedback session", true);

        assertEquals(expectedResponseStats, responseStats);

        verify(mockLogic, times(1)).getFeedbackSessionsForCourse(typicalCourse.getId());
        verify(mockLogic, times(1))
                .getStudentByAccountId(typicalStudent.getCourseId(), typicalStudent.getAccountId());
        for (FeedbackSession feedbackSession : feedbackSessions) {
            if ("invisible session".equals(feedbackSession.getName())) {
                // invisible session is skipped
                continue;
            }
            verify(mockLogic, times(3)).isFeedbackSessionAttemptedByStudent(
                    feedbackSession, typicalStudent.getEmail(), typicalStudent.getTeamName());
        }
    }

    @Test
    void testAccessControl_nonInstructor_cannotAccessResponses() {
        String[] paramsWithCourse = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        String[] paramsWithFeedbackQuestion = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        ______TS("Non-logged-in users cannot access");

        logoutUser();
        verifyCannotAccess(paramsWithCourse);
        verifyCannotAccess(paramsWithFeedbackQuestion);

        ______TS("Non-registered users cannot access");

        loginAsUnregistered(TEST_UNREGISTERED_ACCOUNT_ID.toString());

        verifyCannotAccess(paramsWithCourse);
        verifyCannotAccess(paramsWithFeedbackQuestion);

        verify(mockLogic, times(2))
                .getInstructorByAccountId(typicalCourse.getId(), TEST_UNREGISTERED_ACCOUNT_ID.toString());

        ______TS("Students cannot access");

        loginAsStudent(getTypicalStudent().getAccountId());

        verifyCannotAccess(paramsWithCourse);
        verifyCannotAccess(paramsWithFeedbackQuestion);

        verify(mockLogic, times(2))
                .getInstructorByAccountId(typicalCourse.getId(), getTypicalStudent().getAccountId());

        // check that getCourse and getFeedbackQuestion are run once per test for logged in users
        verify(mockLogic, times(2)).getCourse(typicalCourse.getId());
        verify(mockLogic, times(2)).getFeedbackQuestion(typicalFeedbackQuestion.getId());
    }

    @Test
    void testAccessControl_instructorOfDifferentCourse_cannotAccessResponses() {
        String[] paramsWithCourse = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        String[] paramsWithFeedbackQuestion = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        Instructor instructorOfOtherCourse = getTypicalInstructor();
        Course otherCourse = new Course("different-id", "different-name",
                Const.DEFAULT_TIME_ZONE, "teammates");
        instructorOfOtherCourse.setCourse(otherCourse);

        loginAsInstructor(instructorOfOtherCourse.getAccountId());

        verifyCannotAccess(paramsWithCourse);
        verifyCannotAccess(paramsWithFeedbackQuestion);

        verify(mockLogic, times(2))
                .getInstructorByAccountId(typicalCourse.getId(), instructorOfOtherCourse.getAccountId());
        verify(mockLogic, times(1)).getCourse(typicalCourse.getId());
        verify(mockLogic, times(1)).getFeedbackQuestion(typicalFeedbackQuestion.getId());
    }

    @Test
    void testAccessControl_instructorOfSameCourse_canAccessResponses() {
        String[] paramsWithCourse = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        String[] paramsWithFeedbackQuestion = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        when(mockLogic.getCourse(typicalCourse.getId())).thenReturn(typicalCourse);
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getInstructorByAccountId(typicalCourse.getId(), typicalInstructor.getAccountId()))
                .thenReturn(typicalInstructor);

        loginAsInstructor(typicalInstructor.getAccountId());

        verifyCanAccess(paramsWithCourse);
        verifyCanAccess(paramsWithFeedbackQuestion);

        verify(mockLogic, times(2))
                .getInstructorByAccountId(typicalCourse.getId(), typicalInstructor.getAccountId());
        verify(mockLogic, times(1)).getCourse(typicalCourse.getId());
        verify(mockLogic, times(1)).getFeedbackQuestion(typicalFeedbackQuestion.getId());
    }

    @Test
    void testAccessControl_studentOfSameCourse_canAccessStudentGetHasResponded() {
        loginAsStudent(typicalStudent.getAccountId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalStudent.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        when(mockLogic.getStudentByAccountId(typicalStudent.getCourseId(), typicalStudent.getAccountId()))
                .thenReturn(typicalStudent);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalFeedbackSession.getCourseId()))
                .thenReturn(typicalFeedbackSession);

        verifyCanAccess(params);

        verify(mockLogic, times(1))
                .getStudentByAccountId(typicalStudent.getCourseId(), typicalStudent.getAccountId());
        verify(mockLogic, times(1))
                .getFeedbackSession(typicalFeedbackSession.getName(), typicalFeedbackSession.getCourseId());
    }

    @Test
    void testAccessControl_studentOfSameCourse_canAccessStudentGetHasRespondedWithoutFsParam() {
        loginAsStudent(typicalStudent.getAccountId());
        List<FeedbackSession> feedbackSessions = getTypicalFeedbackSessions(typicalCourse);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalStudent.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        when(mockLogic.getStudentByAccountId(typicalStudent.getCourseId(), typicalStudent.getAccountId()))
                .thenReturn(typicalStudent);
        when(mockLogic.getFeedbackSessionsForCourse(typicalCourse.getId())).thenReturn(feedbackSessions);

        verifyCanAccess(params);

        verify(mockLogic, times(1)).getFeedbackSessionsForCourse(typicalCourse.getId());
        verify(mockLogic, times(3))
                .getStudentByAccountId(typicalStudent.getCourseId(), typicalStudent.getAccountId());
    }

    @Test
    void testAccessControl_notEnoughParameters_throwsInvalidHttpParameterException() {
        loginAsInstructor(typicalInstructor.getAccountId());
        verifyHttpParameterFailure();
    }

    @Test
    void testAccessControl_wrongEntityType_cannotAccess() {
        loginAsInstructor(typicalInstructor.getAccountId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalInstructor.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, "wrongtype",
        };

        verifyCannotAccess(params);
    }

    private FeedbackSession getTypicalFeedbackSession(Course course) {
        Instant startTime = Instant.now().minus(Duration.ofDays(3));
        Instant endTime = Instant.now().minus(Duration.ofDays(1));
        return new FeedbackSession("Template feedback session",
                course,
                "test@teammates.tmt",
                "test-instructions",
                startTime,
                endTime,
                startTime,
                endTime,
                Duration.ofMinutes(5),
                false,
                false,
                false);
    }

    private List<FeedbackSession> getTypicalFeedbackSessions(Course course) {
        FeedbackSession feedbackSessionTemplate = getTypicalFeedbackSession(course);
        FeedbackSession feedbackSession1 = feedbackSessionTemplate.getCopy();
        feedbackSession1.setName("First feedback session");
        FeedbackSession feedbackSession2 = feedbackSessionTemplate.getCopy();
        feedbackSession2.setName("Second feedback session");
        FeedbackSession feedbackSession3 = feedbackSessionTemplate.getCopy();
        feedbackSession3.setName("Third feedback session");
        FeedbackSession invisibleSession = getTypicalFeedbackSessionForCourse(course);
        invisibleSession.setName("invisible session");

        return Arrays.asList(feedbackSession1, feedbackSession2, feedbackSession3, invisibleSession);
    }

}
