package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.*;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.webapi.action.GetFeedbackResponsesAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackResponseData;
import teammates.ui.webapi.output.FeedbackResponsesData;

import java.util.List;

/**
 * SUT: {@link GetFeedbackResponsesAction}.
 */
public class GetFeedbackResponsesActionTest extends BaseActionTest<GetFeedbackResponsesAction> {

    private FeedbackSessionAttributes session1InCourse1;
    private FeedbackQuestionAttributes qn1InSession1InCourse1;
    private StudentAttributes student1InCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private FeedbackSessionAttributes gracePeriodSession;
    private FeedbackQuestionAttributes qn2InGracePeriodInCourse1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // TODO
        useTypicalDataBundle();
        loginAsStudent(student1InCourse1.getGoogleId());

        ______TS("Not enough parameters");
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_QUESTION_ID,qn1InSession1InCourse1.getId());

        ______TS("Typical success case as a student");
        String[] paramsForStudent = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT,Intent.STUDENT_SUBMISSION.toString()
        };
        GetFeedbackResponsesAction a = getAction(paramsForStudent);
        JsonResult resultForStudent = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, resultForStudent.getStatusCode());
        FeedbackResponsesData feedbackResponsesDataForStudent = (FeedbackResponsesData) resultForStudent.getOutput();
        List<FeedbackResponseData> responsesForStudent = feedbackResponsesDataForStudent.getResponses();
        assertEquals(1, responsesForStudent.size());

        FeedbackResponseData typicalResponseForStudent = responsesForStudent.get(0);
        FeedbackResponseAttributes expectedForStudent =
                logic.getFeedbackResponsesFromStudentOrTeamForQuestion(qn1InSession1InCourse1,student1InCourse1).get(0);
        assertNotNull(typicalResponseForStudent.getFeedbackResponseId());
        assertEquals(expectedForStudent.getId(),typicalResponseForStudent.getFeedbackResponseId());
        assertEquals(expectedForStudent.getGiver(),typicalResponseForStudent.getGiverIdentifier());
        assertEquals(expectedForStudent.getRecipient(),typicalResponseForStudent.getRecipientIdentifier());
        assertEquals(expectedForStudent.getResponseDetails().getAnswerString(),typicalResponseForStudent
                .getResponseDetails()
                .getAnswerString());
        assertEquals(expectedForStudent.getResponseDetails().questionType,typicalResponseForStudent
                .getResponseDetails().questionType);

        assertEquals(JsonUtils.toJson(expectedForStudent.getResponseDetails()),
                JsonUtils.toJson(typicalResponseForStudent.getResponseDetails()));

        ______TS("Typical success case as a instructor");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] paramsForInstructor = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID,qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT,Intent.INSTRUCTOR_SUBMISSION.toString()
        };
        GetFeedbackResponsesAction b = getAction(paramsForInstructor);
        JsonResult resultForInstructor = getJsonResult(b);

        assertEquals(HttpStatus.SC_OK, resultForInstructor.getStatusCode());
        FeedbackResponsesData feedbackResponsesDataForInstructor = (FeedbackResponsesData) resultForInstructor.getOutput();
        List<FeedbackResponseData> responsesForInstructor = feedbackResponsesDataForInstructor.getResponses();
        assertEquals(1, responsesForInstructor.size());

        FeedbackResponseData typicalResponseForInstructor = responsesForInstructor.get(0);
        FeedbackResponseAttributes expectedForInstructor =
                logic.getFeedbackResponsesFromInstructorForQuestion(qn2InGracePeriodInCourse1,instructor1OfCourse1)
                        .get(0);
        assertNotNull(typicalResponseForInstructor.getFeedbackResponseId());
        assertEquals(expectedForInstructor.getId(),typicalResponseForInstructor.getFeedbackResponseId());
        assertEquals(expectedForInstructor.getGiver(),typicalResponseForInstructor.getGiverIdentifier());
        assertEquals(expectedForInstructor.getRecipient(),typicalResponseForInstructor.getRecipientIdentifier());
        assertEquals(expectedForInstructor.getResponseDetails().getAnswerString(),typicalResponseForInstructor
                .getResponseDetails()
                .getAnswerString());
        assertEquals(expectedForInstructor.getResponseDetails().questionType,typicalResponseForInstructor
                .getResponseDetails().questionType);

        assertEquals(JsonUtils.toJson(expectedForInstructor.getResponseDetails()),
                JsonUtils.toJson(typicalResponseForInstructor.getResponseDetails()));
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // TODO


    }

    private void useTypicalDataBundle(){
        removeAndRestoreTypicalDataBundle();
        //response1Q2GracePeriodFeedback;
        //
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        gracePeriodSession = typicalBundle.feedbackSessions.get("gracePeriodSession");
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        int q1 = 1;
        qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), q1);
        int q2 = 2;
        qn2InGracePeriodInCourse1 = logic.getFeedbackQuestion(
                gracePeriodSession.getFeedbackSessionName(), gracePeriodSession.getCourseId(), q2);






    }

}
