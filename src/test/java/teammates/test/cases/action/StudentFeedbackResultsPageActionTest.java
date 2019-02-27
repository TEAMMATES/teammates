package teammates.test.cases.action;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentFeedbackResultsPageAction;
import teammates.ui.pagedata.StudentFeedbackResultsPageData;

/**
 * SUT: {@link StudentFeedbackResultsPageAction}.
 */
@Test(enabled = false)
public class StudentFeedbackResultsPageActionTest extends BaseActionTest {

    @Override
    protected void prepareTestData() {
        // see setup()
    }

    @BeforeMethod
    public void setup() throws Exception {
        removeAndRestoreTypicalDataBundle();
        addUnregStudentToCourse1();
    }

    @AfterMethod
    public void tearDown() {
        StudentsLogic.inst().deleteStudentCascade("idOfTypicalCourse1", "student6InCourse1@gmail.tmt");
    }

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
    }

    @Test(expectedExceptions = UnauthorizedAccessException.class, enabled = false,
            expectedExceptionsMessageRegExp = "Trying to access system using a non-existent feedback session entity")
    public void testExecuteAndPostProcess_registeredStudentAccessSoftDeletedSession_shouldNotAccess() throws Exception {
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSessionsLogic.inst()
                .moveFeedbackSessionToRecycleBin(
                        session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId());

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
        };

        StudentFeedbackResultsPageAction pageAction = getAction(submissionParams);
        getRedirectResult(pageAction);
    }

    @Test(expectedExceptions = UnauthorizedAccessException.class, enabled = false,
            expectedExceptionsMessageRegExp = "Trying to access system using a non-existent feedback session entity")
    public void testExecuteAndPostProcess_unregisteredStudentAccessSoftDeletedSession_shouldNotAccess() throws Exception {
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSessionsLogic.inst()
                .moveFeedbackSessionToRecycleBin(
                        session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId());

        StudentAttributes unregisteredStudent = StudentsLogic.inst()
                .getStudentForEmail("idOfTypicalCourse1", "student6InCourse1@gmail.tmt");

        gaeSimulation.logoutUser();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),

                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregisteredStudent.key),
                Const.ParamsNames.STUDENT_EMAIL, unregisteredStudent.email,
        };

        StudentFeedbackResultsPageAction pageAction = getAction(submissionParams);
        getRedirectResult(pageAction);
    }

    @Override
    @Test(enabled = false)
    public void testExecuteAndPostProcess() throws Exception {
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSessionAttributes emptySession = typicalBundle.feedbackSessions.get("empty.session");
        FeedbackSessionAttributes closedSession = typicalBundle.feedbackSessions.get("closedSession");
        FeedbackSessionAttributes gracePeriodSession = typicalBundle.feedbackSessions.get("gracePeriodSession");

        session1InCourse1.setResultsVisibleFromTime(session1InCourse1.getStartTime());
        FeedbackSessionsLogic.inst().updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId())
                        .withResultsVisibleFromTime(session1InCourse1.getResultsVisibleFromTime())
                        .build());

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("invalid params");

        String[] submissionParams = new String[] {};

        verifyRedirectTo(Const.WebPageURIs.STUDENT_HOME_PAGE, submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
        };

        verifyRedirectTo(Const.WebPageURIs.STUDENT_HOME_PAGE, submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
        };

        verifyRedirectTo(Const.WebPageURIs.STUDENT_HOME_PAGE, submissionParams);

        ______TS("results not viewable when not published");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
        };

        FeedbackSessionsLogic.inst().unpublishFeedbackSession(session1InCourse1);

        StudentFeedbackResultsPageAction pageAction = getAction(submissionParams);

        try {
            getShowPageResult(pageAction);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException exception) {
            assertEquals("This feedback session is not yet visible.", exception.getMessage());
        }

        ______TS("access a empty session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, emptySession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, emptySession.getFeedbackSessionName(),
        };

        pageAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(pageAction);
        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_RESULTS, pageResult.destination);
        assertFalse(pageResult.isError);
        assertEquals("You have not received any new feedback but you may review your own submissions below.",
                     pageResult.getStatusMessage());

        ______TS("access a gracePeriodSession session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, gracePeriodSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, gracePeriodSession.getFeedbackSessionName(),
        };

        pageAction = getAction(submissionParams);

        try {
            getShowPageResult(pageAction);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException exception) {
            assertEquals("This feedback session is not yet visible.", exception.getMessage());
        }

        ______TS("access a closed session");

        Instant originalResultVisibleTime = closedSession.getResultsVisibleFromTime();
        // Session is closed but not published; modify result visible time
        closedSession.setResultsVisibleFromTime(Instant.now().plus(Duration.ofDays(1L)));
        FeedbackSessionsLogic.inst().updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(closedSession.getFeedbackSessionName(), closedSession.getCourseId())
                        .withResultsVisibleFromTime(closedSession.getResultsVisibleFromTime())
                        .build());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, closedSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                closedSession.getFeedbackSessionName(),
        };

        pageAction = getAction(submissionParams);

        try {
            getShowPageResult(pageAction);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException exception) {
            assertEquals("This feedback session is not yet visible.", exception.getMessage());
        }

        // Restore original result visible time
        closedSession.setResultsVisibleFromTime(originalResultVisibleTime);
        FeedbackSessionsLogic.inst().updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(closedSession.getFeedbackSessionName(), closedSession.getCourseId())
                        .withResultsVisibleFromTime(closedSession.getResultsVisibleFromTime())
                        .build());

        ______TS("access a non-existent session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent session",
        };

        pageAction = getAction(submissionParams);

        try {
            getShowPageResult(pageAction);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException exception) {
            assertEquals("Trying to access system using a non-existent feedback session entity",
                         exception.getMessage());
        }

        ______TS("typical case");

        removeAndRestoreTypicalDataBundle();

        session1InCourse1 = FeedbackSessionsLogic.inst().getFeedbackSession(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId());
        FeedbackSessionsLogic.inst().publishFeedbackSession(session1InCourse1);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
        };

        pageAction = getAction(submissionParams);
        pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_RESULTS, pageResult.destination);
        assertFalse(pageResult.isError);
        assertEquals("You have received feedback from others. Please see below.", pageResult.getStatusMessage());

        StudentFeedbackResultsPageData pageData = (StudentFeedbackResultsPageData) pageResult.data;

        // databundle time changed here because publishing sets resultsVisibleTime to now.
        typicalBundle.feedbackSessions.get("session1InCourse1").setResultsVisibleFromTime(Instant.now());

        /*
         * The above test can fail if the time elapsed between pageData... and dataBundle...
         * changes the time recorded by dataBundle up to the precision of seconds.
         * To solve that, verify that the time elapsed is less than one second (or else the test
         * fails after all) and if it does, change the value in the dataBundle to match.
         */
        Instant pageDataResultsVisibleFromTime = pageData.getBundle().feedbackSession.getResultsVisibleFromTime();
        Instant dataBundleResultsVisibleFromTime =
                typicalBundle.feedbackSessions.get("session1InCourse1").getResultsVisibleFromTime();
        Duration difference = Duration.between(pageDataResultsVisibleFromTime, dataBundleResultsVisibleFromTime);
        long toleranceTimeInMs = 1000;
        if (difference.compareTo(Duration.ofMillis(toleranceTimeInMs)) < 0) {
            // change to the value that will never make the test fail
            typicalBundle.feedbackSessions.get("session1InCourse1").setResultsVisibleFromTime(
                    pageData.getBundle().feedbackSession.getResultsVisibleFromTime());
        }

        List<FeedbackSessionAttributes> expectedInfoList = new ArrayList<>();
        List<FeedbackSessionAttributes> actualInfoList = new ArrayList<>();
        expectedInfoList.add(typicalBundle.feedbackSessions.get("session1InCourse1"));
        actualInfoList.add(pageData.getBundle().feedbackSession);

        AssertHelper.assertSameContentIgnoreOrder(expectedInfoList, actualInfoList);
        assertEquals(student1InCourse1.googleId, pageData.account.googleId);
        assertEquals(student1InCourse1.getIdentificationString(), pageData.student.getIdentificationString());
    }

    @Override
    protected StudentFeedbackResultsPageAction getAction(String... params) {
        return (StudentFeedbackResultsPageAction) gaeSimulation.getLegacyActionObject(getActionUri(), params);
    }

    @Override
    @Test(enabled = false)
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions
                .get("session1InCourse1");
        FeedbackSessionsLogic.inst().publishFeedbackSession(session1InCourse1);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.getFeedbackSessionName(),
        };

        verifyOnlyStudentsOfTheSameCourseCanAccess(submissionParams);

        // TODO: test no questions -> redirect after moving detection logic to
        // proper access control level.
    }
}
