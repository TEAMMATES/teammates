package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionStatsData;
import teammates.ui.webapi.GetSessionResponseStatsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetSessionResponseStatsAction}.
 */
public class GetSessionResponseStatsActionIT extends BaseActionIT<GetSessionResponseStatsAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_STATS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        ______TS("typical: instructor accesses feedback stats of his/her course");

        FeedbackSession accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourse().getId(),
        };

        GetSessionResponseStatsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        FeedbackSessionStatsData output = (FeedbackSessionStatsData) r.getOutput();
        assertEquals(5, output.getExpectedTotal());
        assertEquals(3, output.getSubmittedTotal());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("accessible for admin");
        verifyAccessibleForAdmin();

        ______TS("accessible for authenticated instructor");
        FeedbackSession accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Course course = accessibleFeedbackSession.getCourse();
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(course, submissionParams);
    }

}
