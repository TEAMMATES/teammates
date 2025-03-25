package teammates.sqlui.webapi;

import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.*;
import teammates.ui.output.HasResponsesData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetHasResponsesAction;
import teammates.ui.webapi.JsonResult;

import static org.mockito.Mockito.*;

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
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
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
        loginAsInstructor(typicalInstructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, "non-existent course",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("No course with id: non-existent course", enfe.getMessage());
    }

    @Test
    void testExecute_instructorWithNonExistentFeedbackQuestion_throwsEntityNotFoundException() {
        loginAsInstructor(typicalInstructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "non-existent question id",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("No feedback question with id: non-existent question id", enfe.getMessage());
    }

    @Test
    void testExecute_instructorGetRespondentsInCourse_success() {
        loginAsInstructor(typicalInstructor.getGoogleId());

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
        loginAsInstructor(typicalInstructor.getGoogleId());

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
        loginAsInstructor(typicalInstructor.getGoogleId());

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
    void testExecute_studentWithNonExistentFeedbackSession_throws() {
        loginAsStudent(typicalStudent.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalStudent.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent session",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("No feedback question with id: non-existent question id", enfe.getMessage());
    }
}
