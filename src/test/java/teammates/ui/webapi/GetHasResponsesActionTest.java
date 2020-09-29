package teammates.ui.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.ui.output.HasResponsesData;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link GetHasResponsesAction}.
 */
public class GetHasResponsesActionTest extends BaseActionTest<GetHasResponsesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.HAS_RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        //set test cases below
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_asInstructorWithFakeCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Get respondents for fake course");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, "fake-course",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        assertNull(logic.getCourse("fake-course"));

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        MessageOutput messageOutput = (MessageOutput) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, jsonResult.getStatusCode());
        assertEquals("No course with id: fake-course", messageOutput.getMessage());
    }

    @Test
    protected void testExecute_asInstructorWithFakeQuestion_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse1.googleId);
        assertNull(logic.getFeedbackQuestion("fake-question-id"));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "fake-question-id",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        MessageOutput messageOutput = (MessageOutput) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, jsonResult.getStatusCode());
        assertEquals("No feedback question with id: fake-question-id", messageOutput.getMessage());
    }

    @Test
    protected void testExecute_asInstructorGetRespondentsInCourse_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("At least 1 respondent");
        assertTrue(logic.hasResponsesForCourse(instructor1OfCourse1.getCourseId()));

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());
        assertTrue(hasResponsesData.hasResponses());

        ______TS("Course with 0 respondents");

        InstructorAttributes instructor1OfCourse4 = typicalBundle.instructors.get("instructor1OfCourse4");
        loginAsInstructor(instructor1OfCourse4.googleId);

        assertFalse(logic.hasResponsesForCourse(instructor1OfCourse4.getCourseId()));

        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse4.courseId,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        getHasResponsesAction = getAction(params);
        jsonResult = getJsonResult(getHasResponsesAction);
        hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());
        assertFalse(hasResponsesData.hasResponses());
    }

    @Test
    protected void asInstructor_getRespondentsForQuestion_shouldPass() {
        ______TS("Question with more than 1 response");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        FeedbackQuestionAttributes fQuestion = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        fQuestion = logic.getFeedbackQuestion(fQuestion.getFeedbackSessionName(), fQuestion.getCourseId(),
                fQuestion.questionNumber);

        loginAsInstructor(instructor1OfCourse1.googleId);
        assertTrue(logic.areThereResponsesForQuestion(fQuestion.getFeedbackQuestionId()));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());
        assertTrue(hasResponsesData.hasResponses());

        ______TS("Question with 0 responses");

        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("awaiting.session");
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(feedbackSessionAttributes.getCourseId());

        fQuestion = typicalBundle.feedbackQuestions.get("qn1InSession4InCourse1");
        fQuestion = logic.getFeedbackQuestion(fQuestion.getFeedbackSessionName(), fQuestion.getCourseId(),
                fQuestion.questionNumber);

        InstructorAttributes currentInstructor = instructors.get(0);

        loginAsInstructor(currentInstructor.googleId);
        assertFalse(logic.areThereResponsesForQuestion(fQuestion.getFeedbackQuestionId()));

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        getHasResponsesAction = getAction(params);
        jsonResult = getJsonResult(getHasResponsesAction);
        hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());
        assertFalse(hasResponsesData.hasResponses());
    }

    @Test
    protected void testExecute_asInstructorWithQuestionIdAndCourseId_preferQuestionId() {
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("awaiting.session");
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(feedbackSessionAttributes.getCourseId());
        FeedbackQuestionAttributes fQuestion = typicalBundle.feedbackQuestions.get("qn1InSession4InCourse1");
        fQuestion = logic.getFeedbackQuestion(fQuestion.getFeedbackSessionName(), fQuestion.getCourseId(),
                fQuestion.questionNumber);

        InstructorAttributes currentInstructor = instructors.get(0);

        loginAsInstructor(currentInstructor.googleId);
        //Different results for question and course
        assertFalse(logic.areThereResponsesForQuestion(fQuestion.getFeedbackQuestionId()));
        assertTrue(logic.hasResponsesForCourse(currentInstructor.courseId));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.COURSE_ID, currentInstructor.courseId,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());
        assertFalse(hasResponsesData.hasResponses());
    }

    @Test
    protected void testExecute_asStudentWithFakeFeedbackSessionName_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(student1InCourse1.googleId);

        assertNull(logic.getFeedbackSession("fake-session-name", student1InCourse1.course));

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.course,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "fake-session-name",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        assertThrows(EntityNotFoundException.class, () -> getJsonResult(getHasResponsesAction));
    }

    @Test
    protected void testExecute_asStudentGetHasRespondedForSession_shouldPass() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(student1InCourse1.googleId);

        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");

        FeedbackResponseAttributes feedbackResponse = typicalBundle.feedbackResponses.get("response1ForQ1S1C1");

        assertEquals(feedbackResponse.getFeedbackSessionName(), feedbackSession.getFeedbackSessionName());
        assertEquals(feedbackResponse.getGiver(), student1InCourse1.getEmail());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.course,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());
        assertTrue(hasResponsesData.hasResponses());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("Only instructors of the course can check if there are responses.");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);

        ______TS("Only instructors of the feedback session can check if there are responses for a question within.");

        List<FeedbackSessionAttributes> feedbackSessionAttributesList =
                logic.getFeedbackSessionsForCourse(instructor1OfCourse1.getCourseId());
        List<FeedbackQuestionAttributes> feedbackQuestionAttributesList = logic.getFeedbackQuestionsForSession(
                feedbackSessionAttributesList.get(0).getFeedbackSessionName(), instructor1OfCourse1.getCourseId());

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionAttributesList.get(0).getFeedbackQuestionId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);

        ______TS("Students of the course can check if they responded.");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.course,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        verifyAccessibleForStudentsOfTheSameCourse(params);
    }

    @Test
    public void testAccessControl_notEnoughParameters_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
    }

    @Test
    public void testAccessControl_wrongEntityType_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("wrong entity type");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.ENTITY_TYPE, "wrongtype",
        };

        verifyCannotAccess(params);
    }
}
