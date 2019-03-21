package teammates.test.cases.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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

        assertEquals(jsonResult.getStatusCode(), HttpStatus.SC_NOT_FOUND);
        assertEquals(messageOutput.getMessage(), "No course with id: fake-course");
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

        assertEquals(jsonResult.getStatusCode(), HttpStatus.SC_NOT_FOUND);
        assertEquals(messageOutput.getMessage(), "No feedback question with id: fake-question-id");
    }

    @Test
    protected void testExecute_getRespondentsInCourse_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        testGettingRespondentsInCourse();

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        testGettingRespondentsInCourse();
    }

    protected void testGettingRespondentsInCourse() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("At least 1 respondent");
        assertTrue(logic.hasResponsesForCourse(instructor1OfCourse1.getCourseId()));

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(jsonResult.getStatusCode(), HttpStatus.SC_OK);
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

        assertEquals(jsonResult.getStatusCode(), HttpStatus.SC_OK);
        assertFalse(hasResponsesData.hasResponses());
    }

    @Test
    protected void getRespondentsForQuestion_shouldPass() {
        ______TS("Question with more than 1 response");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        List<FeedbackSessionAttributes> feedbackSessionAttributesList =
                logic.getFeedbackSessionsForCourse(instructor1OfCourse1.getCourseId());
        List<FeedbackQuestionAttributes> feedbackQuestionAttributesList = logic.getFeedbackQuestionsForSession(
                feedbackSessionAttributesList.get(0).getFeedbackSessionName(), instructor1OfCourse1.getCourseId());

        loginAsInstructor(instructor1OfCourse1.googleId);
        assertTrue(logic.areThereResponsesForQuestion(feedbackQuestionAttributesList.get(0).getFeedbackQuestionId()));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionAttributesList.get(0).getFeedbackQuestionId(),
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(jsonResult.getStatusCode(), HttpStatus.SC_OK);
        assertTrue(hasResponsesData.hasResponses());

        ______TS("Question with 0 responses");

        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("awaiting.session");
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(feedbackSessionAttributes.getCourseId());
        feedbackQuestionAttributesList = logic.getFeedbackQuestionsForSession(
                feedbackSessionAttributes.getFeedbackSessionName(), feedbackSessionAttributes.getCourseId());

        InstructorAttributes currentInstructor = instructors.get(0);

        loginAsInstructor(currentInstructor.googleId);
        assertFalse(logic.areThereResponsesForQuestion(feedbackQuestionAttributesList.get(0).getFeedbackQuestionId()));

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionAttributesList.get(0).getFeedbackQuestionId(),
        };

        getHasResponsesAction = getAction(params);
        jsonResult = getJsonResult(getHasResponsesAction);
        hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(jsonResult.getStatusCode(), HttpStatus.SC_OK);
        assertFalse(hasResponsesData.hasResponses());
    }

    @Test
    protected void testExecute_hasQuestionIdAndCourseId_preferQuestionId() {
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("awaiting.session");
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(feedbackSessionAttributes.getCourseId());
        List<FeedbackQuestionAttributes> feedbackQuestionAttributesList = logic.getFeedbackQuestionsForSession(
                feedbackSessionAttributes.getFeedbackSessionName(), feedbackSessionAttributes.getCourseId());

        InstructorAttributes currentInstructor = instructors.get(0);

        loginAsInstructor(currentInstructor.googleId);
        //Different results for question and course
        assertFalse(logic.areThereResponsesForQuestion(feedbackQuestionAttributesList.get(0).getFeedbackQuestionId()));
        assertTrue(logic.hasResponsesForCourse(currentInstructor.courseId));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionAttributesList.get(0).getFeedbackQuestionId(),
                Const.ParamsNames.COURSE_ID, currentInstructor.courseId,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertEquals(jsonResult.getStatusCode(), HttpStatus.SC_OK);
        assertFalse(hasResponsesData.hasResponses());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        verifyAnyLoggedInUserCanAccess(params);

        List<FeedbackSessionAttributes> feedbackSessionAttributesList =
                logic.getFeedbackSessionsForCourse(instructor1OfCourse1.getCourseId());
        List<FeedbackQuestionAttributes> feedbackQuestionAttributesList = logic.getFeedbackQuestionsForSession(
                feedbackSessionAttributesList.get(0).getFeedbackSessionName(), instructor1OfCourse1.getCourseId());

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionAttributesList.get(0).getFeedbackQuestionId(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
    }
}
