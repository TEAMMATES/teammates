package teammates.test.cases.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.webapi.action.Action;
import teammates.ui.webapi.action.GetFeedbackResponsesAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackResponseData;
import teammates.ui.webapi.output.FeedbackResponsesData;

/**
 * SUT: {@link GetFeedbackResponsesAction}.
 */
public class GetFeedbackResponsesActionTest extends BaseActionTest<GetFeedbackResponsesAction> {

    private FeedbackQuestionAttributes qn1InSession1InCourse1;
    private StudentAttributes student1InCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private FeedbackQuestionAttributes qn2InGracePeriodInCourse1;
    private StudentAttributes student1InCourse2;
    private InstructorAttributes instructor1OfCourse2;

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
        // See independent test cases
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        loginAsStudent(student1InCourse1.getGoogleId());
        ______TS("Not enough parameters");
        verifyHttpParameterFailure_excute();
        verifyHttpParameterFailure_excute(Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId());
        verifyHttpParameterFailure_excute(Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString());
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {
        loginAsStudent(student1InCourse1.getGoogleId());
        ______TS("Invalid Intent");
        String[] paramsForInvalidIntent = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyHttpParameterFailure_excute(paramsForInvalidIntent);
    }

    @Test
    protected void testExecute_studentSubmission_shouldGetResponseSuccessfully() {
        loginAsStudent(student1InCourse1.getGoogleId());
        ______TS("Typical success case as a student");
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction a = getAction(params);
        JsonResult actualResult = getJsonResult(a);

        verifyHttpStatusIsOk(actualResult.getStatusCode());
        FeedbackResponsesData actualData = (FeedbackResponsesData) actualResult.getOutput();
        List<FeedbackResponseData> actualResponses = actualData.getResponses();
        assertEquals(1, actualResponses.size());

        FeedbackResponseData actualResponse = actualResponses.get(0);
        FeedbackResponseAttributes expectedStudent =
                logic.getFeedbackResponsesFromStudentOrTeamForQuestion(qn1InSession1InCourse1,
                        student1InCourse1).get(0);
        verifyIdNotNull(actualResponse.getFeedbackResponseId());
        assertEquals(expectedStudent.getId(), actualResponse.getFeedbackResponseId());
        assertEquals(expectedStudent.getGiver(), actualResponse.getGiverIdentifier());
        assertEquals(expectedStudent.getRecipient(), actualResponse.getRecipientIdentifier());
        assertEquals(expectedStudent.getResponseDetails().getAnswerString(), actualResponse
                .getResponseDetails()
                .getAnswerString());
        assertEquals(expectedStudent.getResponseDetails().questionType, actualResponse
                .getResponseDetails().questionType);

        assertEquals(JsonUtils.toJson(expectedStudent.getResponseDetails()),
                JsonUtils.toJson(actualResponse.getResponseDetails()));

    }

    @Test
    protected void testExecute_instructorSubmission_shouldGetResponseSuccessfully() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        ______TS("Typical success case as a instructor");
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction a = getAction(params);
        JsonResult actualResult = getJsonResult(a);

        verifyHttpStatusIsOk(actualResult.getStatusCode());
        FeedbackResponsesData actualData =
                (FeedbackResponsesData) actualResult.getOutput();
        List<FeedbackResponseData> actualResponses = actualData.getResponses();
        assertEquals(1, actualResponses.size());

        FeedbackResponseData actualResponse = actualResponses.get(0);
        FeedbackResponseAttributes expectedForInstructor =
                logic.getFeedbackResponsesFromInstructorForQuestion(qn2InGracePeriodInCourse1, instructor1OfCourse1)
                        .get(0);
        verifyIdNotNull(actualResponse.getFeedbackResponseId());
        assertEquals(expectedForInstructor.getId(), actualResponse.getFeedbackResponseId());
        assertEquals(expectedForInstructor.getGiver(), actualResponse.getGiverIdentifier());
        assertEquals(expectedForInstructor.getRecipient(), actualResponse.getRecipientIdentifier());
        assertEquals(expectedForInstructor.getResponseDetails().getAnswerString(), actualResponse
                .getResponseDetails()
                .getAnswerString());
        assertEquals(expectedForInstructor.getResponseDetails().questionType, actualResponse
                .getResponseDetails().questionType);

        assertEquals(JsonUtils.toJson(expectedForInstructor.getResponseDetails()),
                JsonUtils.toJson(actualResponse.getResponseDetails()));
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("non-existing feedback response");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] nonExistParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "randomNonExistId",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCannotFindEntify(nonExistParams);

        ______TS("Not answerable to students");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] notAnaserableToStudents = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        verifyCannotAccess(notAnaserableToStudents);

        ______TS("Not answerable to instructors");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] notAnswerableToInstructors = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCannotAccess(notAnswerableToInstructors);

        ______TS("Cannot get responses in preview request");
        String[] inPreviewRequest = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, "randomPreviewers",
        };
        verifyCannotAccess(inPreviewRequest);

        ______TS("student access other student's response from different course");
        loginAsStudent(student1InCourse2.getGoogleId());
        String[] studentAccessOtherStudentsParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        verifyCannotFindEntify(studentAccessOtherStudentsParams);

        ______TS("instructor access other instructor's response from different course");
        loginAsInstructor(instructor1OfCourse2.getGoogleId());
        String[] instructorAccessOtherInstructorsParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCannotAccess(instructorAccessOtherInstructorsParams);

        ______TS("Unauthorized Intent Full Detail");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] unauthorizedIntentFullDetail = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyHttpParameterFailure_checkAccessControl(unauthorizedIntentFullDetail);
        ______TS("Unauthorized Intent Instructor Result");
        String[] unauthorizedIntentInstructorResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        verifyHttpParameterFailure_checkAccessControl(unauthorizedIntentInstructorResult);
        ______TS("Unauthorized Intent Student Result");
        String[] unauthorizedIntentStudentResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        verifyHttpParameterFailure_checkAccessControl(unauthorizedIntentStudentResult);
        ______TS("typical success case");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] validParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        verifyCanAccess(validParams);

    }

    private void verifyCannotFindEntify(String... params) {
        Action a = getAction(params);
        assertThrows(EntityNotFoundException.class, () -> a.checkAccessControl());
    }

    private void verifyHttpParameterFailure_excute(String... params) {
        verifyHttpParameterFailure(params);
    }

    private void verifyHttpParameterFailure_checkAccessControl(String... params) {
        Action a = getAction(params);
        assertThrows(InvalidHttpParameterException.class, () -> a.checkAccessControl());
    }

    private void verifyHttpStatusIsOk(int statusCode) {
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    private void verifyIdNotNull(String id) {
        assertNotNull(id);
    }

    @Override
    protected void prepareTestData() {
        removeAndRestoreTypicalDataBundle();
        FeedbackSessionAttributes gracePeriodSession;
        FeedbackSessionAttributes session1InCourse1;

        session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        gracePeriodSession = typicalBundle.feedbackSessions.get("gracePeriodSession");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        qn2InGracePeriodInCourse1 = logic.getFeedbackQuestion(
                gracePeriodSession.getFeedbackSessionName(), gracePeriodSession.getCourseId(), 2);
        student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
    }

}
