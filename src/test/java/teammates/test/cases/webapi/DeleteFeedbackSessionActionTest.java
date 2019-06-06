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
        // see test cases below
    }

    @Test
    public void testDeleteFeedbackSessionAction_invalidParameters_shouldThrowHttpParameterException() {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("No course ID");
        String[] noCourseIdParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        verifyHttpParameterFailure(noCourseIdParams);

        ______TS("No session name");
        String[] noSessionname = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyHttpParameterFailure(noSessionname);

        ______TS("Empty parameters");
        verifyHttpParameterFailure();
    }

    @Test
    public void testDeleteFeedbackSessionAction_typicalCase_shouldPass() throws Exception {
        ______TS("Delete session that has been soft deleted");

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };

        assertNotNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));

        logic.moveFeedbackSessionToRecycleBin(session.getFeedbackSessionName(), course.getId());
        assertNotNull(logic.getFeedbackSessionFromRecycleBin(session.getFeedbackSessionName(), course.getId()));

        DeleteFeedbackSessionAction deleteFeedbackSessionAction = getAction(params);
        JsonResult result = getJsonResult(deleteFeedbackSessionAction);
        MessageOutput messageOutput = (MessageOutput) result.getOutput();

        assertEquals(messageOutput.getMessage(), "The feedback session is deleted.");
        assertNull(logic.getFeedbackSessionFromRecycleBin(session.getFeedbackSessionName(), course.getId()));
        assertNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));

        ______TS("Delete session not in recycle bin");

        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session2InCourse1");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session2.getFeedbackSessionName(),
        };

        assertNull(logic.getFeedbackSessionFromRecycleBin(session2.getFeedbackSessionName(), course.getId()));
        assertNotNull(logic.getFeedbackSession(session2.getFeedbackSessionName(), course.getId()));

        deleteFeedbackSessionAction = getAction(params);
        result = getJsonResult(deleteFeedbackSessionAction);
        messageOutput = (MessageOutput) result.getOutput();

        assertEquals(messageOutput.getMessage(), "The feedback session is deleted.");
        assertNull(logic.getFeedbackSessionFromRecycleBin(session2.getFeedbackSessionName(), course.getId()));
        assertNull(logic.getFeedbackSession(session2.getFeedbackSessionName(), course.getId()));
    }

    @Test
    public void testDeleteFeedbackSession_failureCases_shouldFailSilently() {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("Delete session that has already been deleted");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        assertNotNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));
        DeleteFeedbackSessionAction deleteFeedbackSessionAction = getAction(params);

        // Delete once
        getJsonResult(deleteFeedbackSessionAction);
        assertNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));

        // Delete again
        // Will fail silently and not throw any exception
        getJsonResult(deleteFeedbackSessionAction);
        assertNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));

        ______TS("Delete session that does not exist");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName",
        };

        assertNull(logic.getFeedbackSession("randomName", course.getId()));
        deleteFeedbackSessionAction = getAction(params);

        // Will fail silently and not throw any exception
        getJsonResult(deleteFeedbackSessionAction);
        assertNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));
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
