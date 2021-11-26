package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionStatsData;

/**
 * SUT: {@link GetSessionResponseStatsAction}.
 */
public class GetSessionResponseStatsActionTest extends BaseActionTest<GetSessionResponseStatsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_STATS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructorAttributes = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructorAttributes.getGoogleId());

        ______TS("typical: instructor accesses feedback stats of his/her course");

        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
        };

        GetSessionResponseStatsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        FeedbackSessionStatsData output = (FeedbackSessionStatsData) r.getOutput();
        assertEquals(10, output.getExpectedTotal());
        assertEquals(5, output.getSubmittedTotal());

        ______TS("fail: instructor accesses stats of non-existent feedback session");

        String nonexistentFeedbackSession = "nonexistentFeedbackSession";
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, nonexistentFeedbackSession,
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
        };

        verifyEntityNotFound(submissionParams);

    }

    @Override
    @Test
    protected void testAccessControl() {
        ______TS("accessible for admin");
        verifyAccessibleForAdmin();

        ______TS("accessible for authenticated instructor");
        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
