package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.PublishFeedbackSessionAction;
import teammates.ui.webapi.output.FeedbackSessionData;
import teammates.ui.webapi.output.FeedbackSessionPublishStatus;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link PublishFeedbackSessionAction}.
 */
public class PublishFeedbackSessionActionTest extends BaseActionTest<PublishFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_PUBLISH;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {

        ______TS("Typical case");

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };

        PublishFeedbackSessionAction publishFeedbackSessionAction = getAction(params);

        JsonResult result = getJsonResult(publishFeedbackSessionAction);
        FeedbackSessionData feedbackSessionData = (FeedbackSessionData) result.getOutput();

        assertEquals(feedbackSessionData.getFeedbackSessionName(), session.getFeedbackSessionName());
        assertEquals(feedbackSessionData.getPublishStatus(), FeedbackSessionPublishStatus.PUBLISHED);

        ______TS("Failure case: Session is already published");

        assertTrue(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()).isPublished());

        result = getJsonResult(getAction(params));
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(output.getMessage(), "Error publishing feedback session: Session has already been published.");
        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

        ______TS("Failure case: Session not found");
        // TODO
        // EntityDoesNotExistException is never thrown because session is guaranteed to be present
        // before entering the try-catch block.
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

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyCoursePrivilege(submissionParams);
    }

}
