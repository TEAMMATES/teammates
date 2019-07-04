package teammates.test.cases.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
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

    @Override
    protected void prepareTestData() {
        removeAndRestoreTypicalDataBundle();
        FeedbackSessionAttributes gracePeriodSession = typicalBundle.feedbackSessions.get("gracePeriodSession");
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        qn2InGracePeriodInCourse1 = logic.getFeedbackQuestion(
                gracePeriodSession.getFeedbackSessionName(), gracePeriodSession.getCourseId(), 2);
        student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // See independent test cases
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        loginAsStudent(student1InCourse1.getGoogleId());

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId());
        verifyHttpParameterFailure(Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString());
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {
        loginAsStudent(student1InCourse1.getGoogleId());

        String[] paramsForInvalidIntent = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyHttpParameterFailure(paramsForInvalidIntent);
    }

    @Test
    protected void testExecute_studentSubmission_shouldGetResponseSuccessfully() {
        loginAsStudent(student1InCourse1.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        FeedbackResponsesData actualData = getFeedbackResponse(params);
        List<FeedbackResponseData> actualResponses = actualData.getResponses();
        assertEquals(1, actualResponses.size());
        FeedbackResponseData actualResponse = actualResponses.get(0);
        FeedbackResponseAttributes expected =
                logic.getFeedbackResponsesFromStudentOrTeamForQuestion(qn1InSession1InCourse1,
                        student1InCourse1).get(0);
        assertNotNull(actualResponse.getFeedbackResponseId());
        verifyFeedbackResponseEquals(expected, actualResponse);
    }

    @Test
    protected void testExecute_instructorSubmission_shouldGetResponseSuccessfully() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        FeedbackResponsesData actualData = getFeedbackResponse(params);
        List<FeedbackResponseData> actualResponses = actualData.getResponses();
        assertEquals(1, actualResponses.size());

        FeedbackResponseData actualResponse = actualResponses.get(0);
        FeedbackResponseAttributes expected =
                logic.getFeedbackResponsesFromInstructorForQuestion(qn2InGracePeriodInCourse1, instructor1OfCourse1)
                        .get(0);
        assertNotNull(actualResponse.getFeedbackResponseId());
        verifyFeedbackResponseEquals(expected, actualResponse);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        //see independent test cases
    }

    @Test
    protected void testAccessControl_notAnswerable_cannotAccess() {

        ______TS("Not answerable to students");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] notAnswerableToStudents = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        assertEquals(FeedbackParticipantType.INSTRUCTORS, qn2InGracePeriodInCourse1.getGiverType());
        verifyCannotAccess(notAnswerableToStudents);

        ______TS("Not answerable to instructors");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] notAnswerableToInstructors = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertEquals(FeedbackParticipantType.STUDENTS, qn1InSession1InCourse1.getGiverType());
        verifyCannotAccess(notAnswerableToInstructors);
    }

    @Test
    protected void testAccessControl_invalidIntent_shouldFail() {

        ______TS("Unauthorized Intent Full Detail");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] unauthorizedIntentFullDetail = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        assertThrows(InvalidHttpParameterException.class,
                () -> getAction(unauthorizedIntentFullDetail).checkAccessControl());

        ______TS("Unauthorized Intent Instructor Result");
        String[] unauthorizedIntentInstructorResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        assertThrows(InvalidHttpParameterException.class,
                () -> getAction(unauthorizedIntentInstructorResult).checkAccessControl());

        ______TS("Unauthorized Intent Student Result");
        String[] unauthorizedIntentStudentResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        assertThrows(InvalidHttpParameterException.class,
                () -> getAction(unauthorizedIntentStudentResult).checkAccessControl());
    }

    @Test
    protected void testAccessControl_typicalStudentAccess_canAccess() {
        loginAsStudent(student1InCourse1.getGoogleId());

        String[] validStudentParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        verifyCanAccess(validStudentParams);
    }

    @Test
    protected void testAccessControl_typicalInstructorAccess_canAccess() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] validInstructorParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCanAccess(validInstructorParams);
    }

    @Test
    protected void testAccessControl_getNonExistingFeedbackResponse_shouldFail() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] nonExistParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "randomNonExistId",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertThrows(EntityNotFoundException.class,
                () -> getAction(nonExistParams).checkAccessControl());
    }

    @Test
    protected void testAccessControl_getResponseInPreview_shouldFail() {
        String[] inPreviewRequest = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, instructor1OfCourse1.getEmail(),
        };
        verifyCannotAccess(inPreviewRequest);
    }

    @Test
    protected void testAccessControl_accessAcrossCourses_shouldFail() {

        ______TS("student access other student's response from different course");
        loginAsStudent(student1InCourse2.getGoogleId());
        String[] studentAccessOtherStudentsParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        assertThrows(EntityNotFoundException.class,
                () -> getAction(studentAccessOtherStudentsParams).checkAccessControl());

        ______TS("instructor access other instructor's response from different course");
        loginAsInstructor(instructor1OfCourse2.getGoogleId());
        String[] instructorAccessOtherInstructorsParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCannotAccess(instructorAccessOtherInstructorsParams);
    }

    private FeedbackResponsesData getFeedbackResponse(String[] params) {
        GetFeedbackResponsesAction a = getAction(params);
        JsonResult actualResult = getJsonResult(a);
        assertEquals(HttpStatus.SC_OK, actualResult.getStatusCode());
        return (FeedbackResponsesData) actualResult.getOutput();
    }

    private void verifyFeedbackResponseEquals(FeedbackResponseAttributes expected, FeedbackResponseData actual) {
        assertEquals(expected.getId(), actual.getFeedbackResponseId());
        assertEquals(expected.getGiver(), actual.getGiverIdentifier());
        assertEquals(expected.getRecipient(), actual.getRecipientIdentifier());
        assertEquals(expected.getResponseDetails().getAnswerString(), actual.getResponseDetails().getAnswerString());
        assertEquals(expected.getResponseDetails().questionType, actual.getResponseDetails().questionType);
        assertEquals(JsonUtils.toJson(expected.getResponseDetails()),
                JsonUtils.toJson(actual.getResponseDetails()));
    }
}
