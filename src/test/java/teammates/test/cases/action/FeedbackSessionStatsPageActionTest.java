package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.FeedbackSessionStatsPageAction;
import teammates.ui.pagedata.FeedbackSessionStatsPageData;

/**
 * SUT: {@link FeedbackSessionStatsPageAction}.
 */
public class FeedbackSessionStatsPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String[] submissionParams;

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("typical: instructor accesses feedback stats of his/her course");

        FeedbackSessionAttributes accessableFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessableFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };

        FeedbackSessionStatsPageAction a = getAction(addUserIdToParams(instructorId, submissionParams));
        AjaxResult r = getAjaxResult(a);
        FeedbackSessionStatsPageData data = (FeedbackSessionStatsPageData) r.data;

        assertEquals(
                getPageResultDestination("", false, "idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertEquals(10, data.sessionDetails.stats.expectedTotal);
        assertEquals(4, data.sessionDetails.stats.submittedTotal);
        assertEquals("", r.getStatusMessage());

        ______TS("fail: instructor accesses stats of non-existent feedback session");

        String nonexistentFeedbackSession = "nonexistentFeedbackSession";
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, nonexistentFeedbackSession,
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };

        boolean hasThrownUnauthorizedAccessException = false;
        String exceptionMessage = "";

        a = getAction(addUserIdToParams(instructorId, submissionParams));

        try {
            r = getAjaxResult(a);
        } catch (UnauthorizedAccessException e) {
            hasThrownUnauthorizedAccessException = true;
            exceptionMessage = e.getMessage();
        }

        assertTrue(hasThrownUnauthorizedAccessException);
        assertEquals("Trying to access system using a non-existent feedback session entity", exceptionMessage);
        assertEquals("", r.getStatusMessage());
    }

    @Override
    protected FeedbackSessionStatsPageAction getAction(String... params) {
        return (FeedbackSessionStatsPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
