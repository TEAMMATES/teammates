package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.StudentsDb;
import teammates.ui.controller.Action;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentFeedbackSubmissionEditPageAction;

public class StudentFeedbackSubmissionEditPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions
                .get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.USER_ID, student1InCourse1.googleId
        };
        verifyAssumptionFailure(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.USER_ID, student1InCourse1.googleId
        };
        verifyAssumptionFailure(submissionParams);

        ______TS("feedbacksession deleted");

        FeedbackSessionsDb feedbackSessionsDb = new FeedbackSessionsDb();

        feedbackSessionsDb.deleteEntity(session1InCourse1);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.USER_ID, student1InCourse1.googleId
        };

        StudentFeedbackSubmissionEditPageAction pageAction = getAction(params);
        RedirectResult redirectResult = getRedirectResult(pageAction);

        assertEquals(
                "/page/studentHomePage?error=false&"
                        + "user=student1InCourse1",
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                "The feedback session has been deleted"
                        + " and is no longer accessible.",
                redirectResult.getStatusMessage());

        ______TS("typical success case");

        		removeAndRestoreTypicalDataInDatastore();

        session1InCourse1 = dataBundle.feedbackSessions
                .get("session1InCourse1");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.feedbackSessionName,
                Const.ParamsNames.USER_ID, student1InCourse1.googleId
        };

        pageAction = getAction(params);
        ShowPageResult pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT
                + "?error=false"
                + "&" + Const.ParamsNames.USER_ID + "="
                + student1InCourse1.googleId,
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        ______TS("masquerade mode");

        gaeSimulation.loginAsAdmin("admin.user");

        pageAction = getAction(params);
        pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT
                + "?error=false"
                + "&" + Const.ParamsNames.USER_ID + "="
                + student1InCourse1.googleId,
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        ______TS("student has not joined course");

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        student1InCourse1.googleId = null;
        new StudentsDb().updateStudentWithoutDocument(student1InCourse1.course,
                student1InCourse1.email,
                student1InCourse1.name, student1InCourse1.team, student1InCourse1.section,
                student1InCourse1.email, student1InCourse1.googleId,
                student1InCourse1.comments);

        pageAction = getAction(params);
        redirectResult = getRedirectResult(pageAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?error=true&user=student1InCourse1",
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals(
                "You are not registered in the course "
                        + session1InCourse1.courseId,
                redirectResult.getStatusMessage());
    }

    private StudentFeedbackSubmissionEditPageAction getAction(String... params)
            throws Exception {
        return (StudentFeedbackSubmissionEditPageAction) (gaeSimulation
                .getActionObject(uri, params));
    }
}
