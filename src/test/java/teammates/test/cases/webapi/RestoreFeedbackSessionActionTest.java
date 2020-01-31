package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.RestoreFeedbackSessionAction;
import teammates.ui.webapi.output.FeedbackSessionData;

/**
 * SUT: {@link RestoreFeedbackSessionAction}.
 */
public class RestoreFeedbackSessionActionTest extends BaseActionTest<RestoreFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes firstFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");

        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;
        String feedbackSessionName = firstFeedbackSession.getFeedbackSessionName();

        loginAsInstructor(instructorId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
        };

        ______TS("Not found in recycle bin");

        RestoreFeedbackSessionAction notFoundAction = getAction(submissionParams);
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundException.class, () -> {
            getJsonResult(notFoundAction);
        });

        assertEquals("Feedback session is not in recycle bin", notFoundException.getMessage());

        ______TS("Typical case, restore a deleted feedback session from recycle bin");

        logic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);

        RestoreFeedbackSessionAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        FeedbackSessionData feedbackSessionMessage = (FeedbackSessionData) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals(courseId, feedbackSessionMessage.getCourseId());
        assertEquals(feedbackSessionName, feedbackSessionMessage.getFeedbackSessionName());
        assertFalse(logic.getFeedbackSession(feedbackSessionName, courseId).isSessionDeleted());

        ______TS("Not enough parameters");

        NullHttpParameterException emptyParamsException = assertThrows(NullHttpParameterException.class, () -> {
            RestoreFeedbackSessionAction emptyParamsAction = getAction();
            getJsonResult(emptyParamsAction);
        });

        assertEquals(
                String.format(Const.StatusCodes.NULL_HTTP_PARAMETER, Const.ParamsNames.COURSE_ID),
                emptyParamsException.getMessage()
        );
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        String courseId = "idOfTypicalCourse1";
        String feedbackSessionName = "First feedback session";

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
        };
        logic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);

        verifyOnlyInstructorsCanAccess(submissionParams);
        verifyInaccessibleWithoutModifySessionPrivilege(submissionParams);
    }

}
