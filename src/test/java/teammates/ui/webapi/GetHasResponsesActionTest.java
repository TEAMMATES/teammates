package teammates.ui.webapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.HasResponsesData;

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
    protected void testExecute() {
        //set test cases below
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_asInstructorWithFakeCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Get respondents for fake course");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, "fake-course",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        assertNull(logic.getCourse("fake-course"));

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("No course with id: fake-course", enfe.getMessage());
    }

    @Test
    protected void testExecute_asInstructorWithFakeQuestion_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        assertNull(logic.getFeedbackQuestion("fake-question-id"));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "fake-question-id",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("No feedback question with id: fake-question-id", enfe.getMessage());
    }

    @Test
    protected void testExecute_asInstructorGetRespondentsInCourse_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("At least 1 respondent");
        assertTrue(logic.hasResponsesForCourse(instructor1OfCourse1.getCourseId()));

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertTrue(hasResponsesData.getHasResponses());

        ______TS("Course with 0 respondents");

        InstructorAttributes instructor1OfCourse4 = typicalBundle.instructors.get("instructor1OfCourse4");
        loginAsInstructor(instructor1OfCourse4.getGoogleId());

        assertFalse(logic.hasResponsesForCourse(instructor1OfCourse4.getCourseId()));

        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse4.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        getHasResponsesAction = getAction(params);
        jsonResult = getJsonResult(getHasResponsesAction);
        hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertFalse(hasResponsesData.getHasResponses());
    }

    @Test
    protected void asInstructor_getRespondentsForQuestion_shouldPass() {
        ______TS("Question with more than 1 response");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        FeedbackQuestionAttributes fQuestion = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        fQuestion = logic.getFeedbackQuestion(fQuestion.getFeedbackSessionName(), fQuestion.getCourseId(),
                fQuestion.getQuestionNumber());

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        assertTrue(logic.areThereResponsesForQuestion(fQuestion.getFeedbackQuestionId()));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertTrue(hasResponsesData.getHasResponses());

        ______TS("Question with 0 responses");

        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("awaiting.session");
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(feedbackSessionAttributes.getCourseId());

        fQuestion = typicalBundle.feedbackQuestions.get("qn1InSession4InCourse1");
        fQuestion = logic.getFeedbackQuestion(fQuestion.getFeedbackSessionName(), fQuestion.getCourseId(),
                fQuestion.getQuestionNumber());

        InstructorAttributes currentInstructor = instructors.get(0);

        loginAsInstructor(currentInstructor.getGoogleId());
        assertFalse(logic.areThereResponsesForQuestion(fQuestion.getFeedbackQuestionId()));

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        getHasResponsesAction = getAction(params);
        jsonResult = getJsonResult(getHasResponsesAction);
        hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertFalse(hasResponsesData.getHasResponses());
    }

    @Test
    protected void testExecute_asInstructorWithQuestionIdAndCourseId_preferQuestionId() {
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("awaiting.session");
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(feedbackSessionAttributes.getCourseId());
        FeedbackQuestionAttributes fQuestion = typicalBundle.feedbackQuestions.get("qn1InSession4InCourse1");
        fQuestion = logic.getFeedbackQuestion(fQuestion.getFeedbackSessionName(), fQuestion.getCourseId(),
                fQuestion.getQuestionNumber());

        InstructorAttributes currentInstructor = instructors.get(0);

        loginAsInstructor(currentInstructor.getGoogleId());
        //Different results for question and course
        assertFalse(logic.areThereResponsesForQuestion(fQuestion.getFeedbackQuestionId()));
        assertTrue(logic.hasResponsesForCourse(currentInstructor.getCourseId()));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.COURSE_ID, currentInstructor.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertFalse(hasResponsesData.getHasResponses());
    }

    @Test
    protected void testExecute_asStudentWithFakeFeedbackSessionName_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(student1InCourse1.getGoogleId());

        assertNull(logic.getFeedbackSession("fake-session-name", student1InCourse1.getCourse()));

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "fake-session-name",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        verifyEntityNotFound(params);
    }

    @Test
    protected void testExecute_asStudentGetHasRespondedForSession_shouldPass() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(student1InCourse1.getGoogleId());

        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");

        FeedbackResponseAttributes feedbackResponse = typicalBundle.feedbackResponses.get("response1ForQ1S1C1");

        assertEquals(feedbackResponse.getFeedbackSessionName(), feedbackSession.getFeedbackSessionName());
        assertEquals(feedbackResponse.getGiver(), student1InCourse1.getEmail());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        assertTrue(hasResponsesData.getHasResponses());
    }

    @Test
    protected void testExecute_asStudentGetHasRespondedForSessionWithoutFsParam_shouldPass() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(student1InCourse1.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetHasResponsesAction getHasResponsesAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getHasResponsesAction);
        HasResponsesData hasResponsesData = (HasResponsesData) jsonResult.getOutput();

        Map<String, Boolean> responseStats = hasResponsesData.getHasResponsesBySessions();

        // we gathered expected from typical bundle
        Map<String, Boolean> expectedResponseStats = new HashMap<>();

        // student has responded here
        expectedResponseStats.put("First feedback session", true);
        expectedResponseStats.put("Second feedback session", true);

        // no questions here for student
        expectedResponseStats.put("Closed Session", true);
        expectedResponseStats.put("Empty session", true);

        // team has responded here
        expectedResponseStats.put("Grace Period Session", true);

        assertEquals(expectedResponseStats, responseStats);
    }

    @Test
    @Override
    protected void testAccessControl() {
        ______TS("Only instructors of the course can check if there are responses.");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
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
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        verifyAccessibleForStudentsOfTheSameCourse(params);
    }

    @Test
    public void testAccessControl_notEnoughParameters_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
    }

    @Test
    public void testAccessControl_wrongEntityType_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("wrong entity type");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, "wrongtype",
        };

        verifyCannotAccess(params);
    }
}
