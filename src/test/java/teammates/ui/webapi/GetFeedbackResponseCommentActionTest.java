package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetFeedbackResponseCommentAction}.
 */
public class GetFeedbackResponseCommentActionTest extends BaseActionTest<GetFeedbackResponseCommentAction> {

    private InstructorAttributes instructor1OfCourse1;
    private InstructorAttributes instructor1OfCourse2;
    private FeedbackResponseAttributes response1ForQ1;
    private FeedbackResponseAttributes response1ForQ3;
    private FeedbackResponseAttributes response2ForQ4;
    private StudentAttributes student1InCourse1;
    private StudentAttributes student1InCourse2;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = loadDataBundle("/FeedbackResponseCommentCRUDTest.json");
        removeAndRestoreDataBundle(dataBundle);

        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        FeedbackQuestionAttributes qn3InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 3);
        FeedbackQuestionAttributes qn4InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 4);

        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        student1InCourse2 = dataBundle.students.get("student1InCourse2");
        instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        instructor1OfCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
        response1ForQ1 = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());
        response1ForQ3 = logic.getFeedbackResponse(qn3InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
        response2ForQ4 = logic.getFeedbackResponse(qn4InSession1InCourse1.getId(),
                student1InCourse1.getTeam(), student1InCourse1.getTeam());
    }

    @Override
    @Test
    protected void testExecute() {
        // ses individual test cases
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                StringHelper.encrypt(response1ForQ1.getId()));
        verifyHttpParameterFailure(Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {

        ______TS("invalid intent as instructor_result");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };
        verifyHttpParameterFailure(submissionParams);

        ______TS("invalid intent as student_result");
        loginAsStudent(student1InCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };
        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    protected void testExecute_typicalSuccessCase_shouldPass() {

        ______TS("typical successful case as student_submission");

        loginAsStudent(student1InCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };

        FeedbackResponseCommentData actualComment = getFeedbackResponseComments(submissionParams);
        FeedbackResponseCommentAttributes expected =
                logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQ3.getId());
        assertEquals(actualComment.getFeedbackCommentText(), expected.getCommentText());
        assertEquals(actualComment.getCommentGiver(), expected.getCommentGiver());

        ______TS("typical successful case as instructor_submission");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };
        actualComment = getFeedbackResponseComments(submissionParams);
        expected = logic.getFeedbackResponseCommentForResponseFromParticipant(response1ForQ1.getId());
        assertEquals(actualComment.getFeedbackCommentText(), expected.getCommentText());
        assertEquals(actualComment.getCommentGiver(), expected.getCommentGiver());

        ______TS("non-existent comment in existing response, should return 204");

        loginAsStudent(student1InCourse1.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response2ForQ4.getId()),
        };
        GetFeedbackResponseCommentAction action = getAction(submissionParams);
        getJsonResult(action, HttpStatus.SC_NO_CONTENT);

        ______TS("non-existent response, should return 404");

        String[] nonExistentResponseSubmissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt("randomresponseid"),
        };

        verifyEntityNotFound(nonExistentResponseSubmissionParams);
    }

    @Override
    @Test
    protected void testAccessControl() {
        // see individual test cases
    }

    @Test
    protected void testAccessControl_typicalSuccessCase_shouldPass() {

        ______TS("typical success case as student_submission");
        loginAsStudent(student1InCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };

        verifyCanAccess(submissionParams);

        ______TS("typical success case as instructor_submission");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };
        verifyCanAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_invalidIntent_shouldFail() {

        ______TS("invalid intent as student_result");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] studentInvalidIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };
        verifyHttpParameterFailureAcl(studentInvalidIntentParams);

        ______TS("invalid intent as instructor_result");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] instructorInvalidIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };
        verifyHttpParameterFailureAcl(instructorInvalidIntentParams);
    }

    @Test
    protected void testAccessControl_responseNotExisting_shouldFail() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt("responseIdOfNonExistingResponse"),
        };

        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    protected void testAccessControl_accessAcrossCourses_shouldFail() {

        ______TS("instructor access other instructor's response from different course");
        loginAsInstructor(instructor1OfCourse2.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ1.getId()),
        };

        verifyCannotAccess(submissionParams);

        ______TS("students access other students' response from different course");
        loginAsStudent(student1InCourse2.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(response1ForQ3.getId()),
        };

        verifyCannotAccess(submissionParams);

    }

    private FeedbackResponseCommentData getFeedbackResponseComments(String[] params) {
        GetFeedbackResponseCommentAction action = getAction(params);
        JsonResult actualResult = getJsonResult(action);
        return (FeedbackResponseCommentData) actualResult.getOutput();
    }

}
