package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link CreateFeedbackSessionLogAction}.
 */
public class CreateFeedbackSessionLogActionTest extends BaseActionTest<CreateFeedbackSessionLogAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.TRACK_SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        JsonResult actionOutput;

        ______TS("Failure case: not enough parameters");
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, "course-id");
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, "course-id",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "feedback-session-name"
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "feedback-session-name",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, Const.FeedbackSessionLogTypes.SUBMISSION,
                Const.ParamsNames.STUDENT_EMAIL, "student@email.com"
        );

        ______TS("Failure case: invalid log type");
        String[] paramsInvalid = {
                Const.ParamsNames.COURSE_ID, "course-id",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "feedback-session-name",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, "invalid log type",
                Const.ParamsNames.STUDENT_EMAIL, "student@email.com",
        };
        actionOutput = getJsonResult(getAction(paramsInvalid));
        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

        ______TS("Success case: typical access");
        String[] paramsSuccessfulAccess = {
                Const.ParamsNames.COURSE_ID, "course-id",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "feedback-session-name",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, Const.FeedbackSessionLogTypes.ACCESS,
                Const.ParamsNames.STUDENT_EMAIL, "student@email.com",
        };
        actionOutput = getJsonResult(getAction(paramsSuccessfulAccess));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        ______TS("Success case: typical submission");
        String[] paramsSuccessfulSubmission = {
                Const.ParamsNames.COURSE_ID, "course-id-2",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "feedback-session-name-2",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, Const.FeedbackSessionLogTypes.SUBMISSION,
                Const.ParamsNames.STUDENT_EMAIL, "student2@email.com",
        };
        actionOutput = getJsonResult(getAction(paramsSuccessfulSubmission));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyAnyUserCanAccess();
    }
}
