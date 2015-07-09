package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.util.TestHelper;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentFeedbackResultsPageAction;
import teammates.ui.controller.StudentFeedbackResultsPageData;

public class StudentFeedbackResultsPageActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSessionAttributes emptySession = dataBundle.feedbackSessions.get("empty.session");
        FeedbackSessionAttributes closedSession = dataBundle.feedbackSessions.get("closedSession");
        FeedbackSessionAttributes gracePeriodSession = dataBundle.feedbackSessions.get("gracePeriodSession");

        session1InCourse1.resultsVisibleFromTime = session1InCourse1.startTime;
        FeedbackSessionsLogic.inst().updateFeedbackSession(session1InCourse1);

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("invalid params");

        String[] submissionParams = new String[] {};

        verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
        };

        verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.feedbackSessionName
        };

        verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);

        ______TS("results not viewable when not published");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.feedbackSessionName
        };

        FeedbackSessionsLogic.inst()
                .unpublishFeedbackSession(session1InCourse1.getSessionName(), session1InCourse1.courseId);

        StudentFeedbackResultsPageAction pageAction = getAction(submissionParams);

        try {
            @SuppressWarnings("unused")
            ShowPageResult pageResult = getShowPageResult(pageAction);
        } catch (UnauthorizedAccessException exception) {
            assertEquals("This feedback session is not yet visible.", exception.getMessage());
        }

        ______TS("cannot access a private session");

        FeedbackSessionsLogic.inst()
                .publishFeedbackSession(session1InCourse1.getSessionName(), session1InCourse1.courseId);

        session1InCourse1.feedbackSessionType = FeedbackSessionType.PRIVATE;
        FeedbackSessionsLogic.inst().updateFeedbackSession(session1InCourse1);

        pageAction = getAction(submissionParams);

        try {
            @SuppressWarnings("unused")
            ShowPageResult pageResult = getShowPageResult(pageAction);
        } catch (UnauthorizedAccessException exception) {
            assertEquals("Feedback session [First feedback session] is not accessible to student "
                         + "[" + student1InCourse1.email + "]", exception.getMessage());
        }

        session1InCourse1.feedbackSessionType = FeedbackSessionType.STANDARD;
        FeedbackSessionsLogic.inst().updateFeedbackSession(session1InCourse1);

        ______TS("access a empty session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, emptySession.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, emptySession.feedbackSessionName
        };

        pageAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(pageAction);
        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_RESULTS, pageResult.destination);
        assertFalse(pageResult.isError);
        assertEquals("You have not received any new feedback but you may review your own submissions below.",
                     pageResult.getStatusMessage());

        ______TS("access a gracePeriodSession session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, gracePeriodSession.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, gracePeriodSession.feedbackSessionName
        };

        pageAction = getAction(submissionParams);

        try {
            pageResult = getShowPageResult(pageAction);
        } catch (UnauthorizedAccessException exception) {
            assertEquals("This feedback session is not yet visible.", exception.getMessage());
        }

        ______TS("access a closed session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, closedSession.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                closedSession.feedbackSessionName
        };

        pageAction = getAction(submissionParams);

        try {
            pageResult = getShowPageResult(pageAction);
        } catch (UnauthorizedAccessException exception) {
            assertEquals("This feedback session is not yet visible.", exception.getMessage());
        }

        ______TS("access a non-existent session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent session"
        };

        pageAction = getAction(submissionParams);

        try {
            pageResult = getShowPageResult(pageAction);
        } catch (UnauthorizedAccessException exception) {
            assertEquals("Trying to access system using a non-existent feedback session entity",
                         exception.getMessage());
        }

        ______TS("typical case");

        removeAndRestoreTypicalDataInDatastore();

        FeedbackSessionsLogic.inst()
                .publishFeedbackSession(session1InCourse1.getSessionName(), session1InCourse1.courseId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.feedbackSessionName
        };

        pageAction = getAction(submissionParams);
        pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_RESULTS, pageResult.destination);
        assertFalse(pageResult.isError);
        assertEquals("You have received feedback from others. Please see below.", pageResult.getStatusMessage());

        StudentFeedbackResultsPageData pageData = (StudentFeedbackResultsPageData) pageResult.data;

        // databundle time changed here because publishing sets resultsVisibleTime to now.
        dataBundle.feedbackSessions.get("session1InCourse1").resultsVisibleFromTime =
                TimeHelper.now( dataBundle.feedbackSessions.get("session1InCourse1").timeZone).getTime();

        /*
         * The above test can fail if the time elapsed between pageData... and dataBundle...
         * changes the time recorded by dataBundle up to the precision of seconds.
         * To solve that, verify that the time elapsed is less than one second (or else the test
         * fails after all) and if it does, change the value in the dataBundle to match.
         */
        long pageDataResultsVisibleFromTime = pageData.getBundle().feedbackSession.resultsVisibleFromTime.getTime();
        long dataBundleResultsVisibleFromTime = dataBundle.feedbackSessions.get("session1InCourse1")
                                                                           .resultsVisibleFromTime.getTime();
        long TOLERANCE_TIME_IN_MILLISECONDS = 1000;
        if (dataBundleResultsVisibleFromTime - pageDataResultsVisibleFromTime < TOLERANCE_TIME_IN_MILLISECONDS) {
            // change to the value that will never make the test fail
            dataBundle.feedbackSessions.get("session1InCourse1").resultsVisibleFromTime = 
                                            pageData.getBundle().feedbackSession.resultsVisibleFromTime;
        }

        List<FeedbackSessionAttributes> expectedInfoList = new ArrayList<FeedbackSessionAttributes>();
        List<FeedbackSessionAttributes> actualInfoList = new ArrayList<FeedbackSessionAttributes>();
        expectedInfoList.add(dataBundle.feedbackSessions.get("session1InCourse1"));
        actualInfoList.add(pageData.getBundle().feedbackSession);

        assertTrue(TestHelper.isSameContentIgnoreOrder(expectedInfoList, actualInfoList));
        assertEquals(student1InCourse1.googleId, pageData.account.googleId);
        assertEquals(student1InCourse1.getIdentificationString(), pageData.student.getIdentificationString());
    }

    private StudentFeedbackResultsPageAction getAction(String... params) throws Exception {
        return (StudentFeedbackResultsPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}
