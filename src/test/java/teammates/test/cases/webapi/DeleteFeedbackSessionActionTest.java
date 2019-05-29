package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.DeleteFeedbackSessionAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link DeleteFeedbackSessionAction}.
 */
public class DeleteFeedbackSessionActionTest extends BaseActionTest<DeleteFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case: Delete from recycle bin");

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };

        assertNotNull(logic.getFeedbackSessionDetails(session.getFeedbackSessionName(), course.getId()));

        logic.moveFeedbackSessionToRecycleBin(session.getFeedbackSessionName(), course.getId());

        DeleteFeedbackSessionAction deleteFeedbackSessionAction = getAction(params);
        JsonResult result = getJsonResult(deleteFeedbackSessionAction);
        MessageOutput messageOutput = (MessageOutput) result.getOutput();

        assertEquals(messageOutput.getMessage(), "The feedback session is deleted.");
        assertNull(logic.getFeedbackSessionFromRecycleBin(session.getFeedbackSessionName(), course.getId()));

        ______TS("Delete session that has already been deleted");

        // Will fail silently and not throw any exception
        getJsonResult(deleteFeedbackSessionAction);

        ______TS("Delete session that does not exist");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName",
        };

        deleteFeedbackSessionAction = getAction(params);

        // Will fail silently and not throw any exception
        getJsonResult(deleteFeedbackSessionAction);

        ______TS("Rare success case: Delete session not in recycle bin");

        session = typicalBundle.feedbackSessions.get("session2InCourse1");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };

        assertNotNull(logic.getFeedbackSessionDetails(session.getFeedbackSessionName(), course.getId()));

        deleteFeedbackSessionAction = getAction(params);
        result = getJsonResult(deleteFeedbackSessionAction);
        messageOutput = (MessageOutput) result.getOutput();

        assertEquals(messageOutput.getMessage(), "The feedback session is deleted.");
        assertNull(logic.getFeedbackSessionFromRecycleBin(session.getFeedbackSessionName(), course.getId()));

    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };

        logic.moveFeedbackSessionToRecycleBin(session.getFeedbackSessionName(), course.getId());

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyCoursePrivilege(submissionParams);
    }
}
