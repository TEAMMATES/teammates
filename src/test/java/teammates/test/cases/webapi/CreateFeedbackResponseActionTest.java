package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.Action;
import teammates.ui.webapi.action.CreateFeedbackResponseAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackResponseData;
import teammates.ui.webapi.request.FeedbackResponseCreateRequest;

/**
 * SUT: {@link CreateFeedbackResponseAction}.
 */
public class CreateFeedbackResponseActionTest extends BaseActionTest<CreateFeedbackResponseAction> {
    private FeedbackQuestionAttributes qn1InSession1InCourse1;
    private StudentAttributes student1InCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private InstructorAttributes instructor2OfCourse1;
    private FeedbackQuestionAttributes qn2InGracePeriodInCourse1;
    private InstructorAttributes instructor1OfCourse2;
    private FeedbackQuestionAttributes qn1InClosedSessionInCourse1;
    private StudentAttributes student2InCourse1;
    private StudentAttributes student5InCourse1;
    private FeedbackQuestionAttributes qn2InSession1InCourse1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        useTypicalDataBundle();
        ______TS("not enough attributes");
        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        ______TS("null recipient Identifier");
        loginAsStudent(student1InCourse1.getGoogleId());
        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            FeedbackResponseCreateRequest createRequest = getResponseRequest(student1InCourse1.getEmail());
            createRequest.setRecipientIdentifier(null);
            Action a = getAction(createRequest, params);
            a.execute();
        });
        ______TS("null questionType");
        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            FeedbackResponseCreateRequest createRequest = getResponseRequest(student1InCourse1.getEmail());
            createRequest.setQuestionType(null);
            Action a = getAction(createRequest, params);
            a.execute();
        });
        ______TS("null responseDetail");
        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            FeedbackResponseCreateRequest createRequest = getResponseRequest(student1InCourse1.getEmail());
            createRequest.setResponseDetails(null);
            Action a = getAction(createRequest, params);
            a.execute();
        });
        ______TS("typical case for student");
        loginAsStudent(student2InCourse1.getGoogleId());
        String[] paramsQn2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        FeedbackResponseCreateRequest createRequest = getResponseRequest(student1InCourse1.getEmail());
        CreateFeedbackResponseAction typicalAction = getAction(createRequest, paramsQn2);
        JsonResult typicalResult = getJsonResult(typicalAction);

        assertEquals(HttpStatus.SC_OK, typicalResult.getStatusCode());
        FeedbackResponseData typicalData = (FeedbackResponseData) typicalResult.getOutput();
        assertEquals("This is the text", typicalData.getResponseDetails().getAnswerString());
        assertEquals(student1InCourse1.getEmail(), typicalData.getRecipientIdentifier());
        assertNotNull(typicalData.getFeedbackResponseId());
        assertEquals(student2InCourse1.getEmail(), typicalData.getGiverIdentifier());

        ______TS("typical case for instructor");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        String[] paramsQn2Grace = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        FeedbackResponseCreateRequest requestForInstructor = getResponseRequest(instructor2OfCourse1.getEmail());
        CreateFeedbackResponseAction typicalActionForInstructor = getAction(requestForInstructor, paramsQn2Grace);
        JsonResult typicalResultInstructor = getJsonResult(typicalActionForInstructor);

        assertEquals(HttpStatus.SC_OK, typicalResultInstructor.getStatusCode());
        FeedbackResponseData typicalDataInstructor = (FeedbackResponseData) typicalResultInstructor.getOutput();
        assertEquals("This is the text", typicalDataInstructor.getResponseDetails().getAnswerString());
        assertNotNull(typicalDataInstructor.getFeedbackResponseId());
        assertEquals(instructor2OfCourse1.getEmail(), typicalDataInstructor.getGiverIdentifier());
        assertEquals(instructor2OfCourse1.getEmail(), typicalDataInstructor.getRecipientIdentifier());

        ______TS("response already exists");
        loginAsStudent(student2InCourse1.getGoogleId());
        FeedbackResponseCreateRequest createRequestAlreadyExists = getResponseRequest(student5InCourse1.getEmail());
        CreateFeedbackResponseAction typicalActionAlreadyExists = getAction(createRequestAlreadyExists, paramsQn2);
        assertThrows(InvalidHttpRequestBodyException.class, () -> getJsonResult(typicalActionAlreadyExists));

        ______TS("invalid intent");
        String[] invalidIntentParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntentParams).execute());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        useTypicalDataBundle();
        ______TS("non-exist feedback question");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] nonExistFeedbackQuestionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "randomNonExist",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertThrows(EntityNotFoundException.class, () -> getAction(nonExistFeedbackQuestionParams)
                .checkAccessControl());
        ______TS("feedback session is closed");
        String[] closedFeedbackSessionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InClosedSessionInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertThrows(UnauthorizedAccessException.class, () -> getAction(closedFeedbackSessionParams)
                .checkAccessControl());
        ______TS("in preview request");
        String[] previewParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, "nonEmptyPreviewParam",
        };
        assertThrows(UnauthorizedAccessException.class, () -> getAction(previewParams).checkAccessControl());
        ______TS("not answerable for students");
        String[] notAnswerableForStudents = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        assertThrows(UnauthorizedAccessException.class, () -> getAction(notAnswerableForStudents).checkAccessControl());
        ______TS("not answerable to instructors");
        String[] notAnswerableForInstructors = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertThrows(UnauthorizedAccessException.class, () -> getAction(notAnswerableForInstructors)
                .checkAccessControl());
        ______TS("invalid HTTP parameters");
        String[] invalidParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidParams).checkAccessControl());
        invalidParams[3] = Intent.INSTRUCTOR_RESULT.toString();
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidParams).checkAccessControl());

        ______TS("invalid recipient for student");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] invalidRecipientForStudent = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        assertThrows(UnauthorizedAccessException.class, () -> {
            FeedbackResponseCreateRequest createRequest = getResponseRequest(student2InCourse1.getEmail());
            getAction(createRequest, invalidRecipientForStudent).checkAccessControl();
        });
        ______TS("invalid recipient for instructor");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] invalidRecipientForInstructor = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertThrows(UnauthorizedAccessException.class, () -> {
            FeedbackResponseCreateRequest createRequest = getResponseRequest(instructor1OfCourse2.getEmail());
            getAction(createRequest, invalidRecipientForInstructor).checkAccessControl();
        });

    }

    private FeedbackResponseCreateRequest getResponseRequest(String email) {
        FeedbackResponseCreateRequest createRequest = new FeedbackResponseCreateRequest();
        createRequest.setQuestionType(FeedbackQuestionType.TEXT);
        createRequest.setRecipientIdentifier(email);
        FeedbackResponseDetails responseDetails = new FeedbackTextResponseDetails("This is the text");
        createRequest.setResponseDetails(responseDetails);
        return createRequest;
    }

    private void useTypicalDataBundle() {
        removeAndRestoreTypicalDataBundle();
        FeedbackSessionAttributes gracePeriodSession;
        FeedbackSessionAttributes session1InCourse1;
        FeedbackSessionAttributes closedSession;

        session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        gracePeriodSession = typicalBundle.feedbackSessions.get("gracePeriodSession");
        closedSession = typicalBundle.feedbackSessions.get("closedSession");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        int q1 = 1;
        qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), q1);
        int q2 = 2;
        qn2InGracePeriodInCourse1 = logic.getFeedbackQuestion(
                gracePeriodSession.getFeedbackSessionName(), gracePeriodSession.getCourseId(), q2);
        int qClosed = 1;
        qn1InClosedSessionInCourse1 = logic.getFeedbackQuestion(
                closedSession.getFeedbackSessionName(), closedSession.getCourseId(), qClosed);
        instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
        student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        student5InCourse1 = typicalBundle.students.get("student5InCourse1");
        instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        int q2Insession1 = 2;
        qn2InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), q2Insession1);
    }
}
