package teammates.test.cases.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetHasResponsesAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.HasResponsesData;
import teammates.ui.webapi.output.MessageOutput;

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
    protected void testExecute_fakeCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Get respondents for fake course");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, "fake-course",
        };

        assertNull(logic.getCourse("fake-course"));

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        MessageOutput messageOutput = (MessageOutput) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, jsonResult.getStatusCode());
        assertEquals("No course with id: fake-course", messageOutput.getMessage());
    }

    @Test
    protected void testExecute_fakeQuestion_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse1.googleId);
        assertNull(logic.getFeedbackQuestion("fake-question-id"));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "fake-question-id",
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        MessageOutput messageOutput = (MessageOutput) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, jsonResult.getStatusCode());
        assertEquals("No feedback question with id: fake-question-id", messageOutput.getMessage());
    }

    @Test
    protected void testExecute_getRespondentsInCourse_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("At least 1 respondent");
        assertTrue(logic.hasResponsesForCourse(instructor1OfCourse1.getCourseId()));

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
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
        };

        getHasResponsesAction = getAction(params);
        jsonResult = getJsonResult(getHasResponsesAction);
        hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());
        assertFalse(hasResponsesData.hasResponses());
    }

    @Test
    protected void getRespondentsForQuestion_shouldPass() {
        ______TS("Question with more than 1 response");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        FeedbackQuestionAttributes fQuestion = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        fQuestion = logic.getFeedbackQuestion(fQuestion.getFeedbackSessionName(), fQuestion.getCourseId(),
                fQuestion.questionNumber);

        loginAsInstructor(instructor1OfCourse1.googleId);
        assertTrue(logic.areThereResponsesForQuestion(fQuestion.getFeedbackQuestionId()));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fQuestion.getFeedbackQuestionId(),
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
        };

        getHasResponsesAction = getAction(params);
        jsonResult = getJsonResult(getHasResponsesAction);
        hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());
        assertFalse(hasResponsesData.hasResponses());
    }

    @Test
    protected void testExecute_hasQuestionIdAndCourseId_preferQuestionId() {
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
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());
        assertFalse(hasResponsesData.hasResponses());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("Only instructors of the course can check if there are responses.");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);

        ______TS("Only instructors of the feedback session can check if there are responses for a question within.");

        List<FeedbackSessionAttributes> feedbackSessionAttributesList =
                logic.getFeedbackSessionsForCourse(instructor1OfCourse1.getCourseId());
        List<FeedbackQuestionAttributes> feedbackQuestionAttributesList = logic.getFeedbackQuestionsForSession(
                feedbackSessionAttributesList.get(0).getFeedbackSessionName(), instructor1OfCourse1.getCourseId());

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionAttributesList.get(0).getFeedbackQuestionId(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
    }

    @Test
    public void testAccessControl_notEnoughParameters_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
    }
}
