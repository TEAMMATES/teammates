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
    String getActionUri() {
        return Const.ResourceURIs.SESSION_STATS;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        ______TS("typical: instructor accesses feedback stats of his/her course");

        FeedbackSession accessibleFs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFs.getName(),
                Const.ParamsNames.COURSE_ID, accessibleFs.getCourse().getId(),
        };

        GetSessionResponseStatsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        FeedbackSessionStatsData output = (FeedbackSessionStatsData) r.getOutput();
        assertEquals(8, output.getExpectedTotal());
        assertEquals(3, output.getSubmittedTotal());

        ______TS("fail: instructor accesses stats of non-existent feedback session");

        String nonexistentFeedbackSession = "nonexistentFeedbackSession";
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, nonexistentFeedbackSession,
                Const.ParamsNames.COURSE_ID, accessibleFs.getCourse().getId(),
        };

        verifyEntityNotFound(submissionParams);

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        ______TS("accessible for admin");
        verifyAccessibleForAdmin();

        ______TS("accessible for authenticated instructor");
        Course course1 = typicalBundle.courses.get("course1");
        FeedbackSession accessibleFs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFs.getName(),
                Const.ParamsNames.COURSE_ID, accessibleFs.getCourse().getId(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(course1, submissionParams);
    }
}
