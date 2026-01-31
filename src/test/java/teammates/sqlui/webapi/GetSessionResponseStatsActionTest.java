package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionStatsData;
import teammates.ui.webapi.GetSessionResponseStatsAction;

/**
 * SUT: {@link GetSessionResponseStatsAction}.
 */
public class GetSessionResponseStatsActionTest extends BaseActionTest<GetSessionResponseStatsAction> {
    private Course stubCourse;
    private FeedbackSession stubFeedbackSession;
    private Instructor stubInstructor;
    private FeedbackSessionStatsData stubFeedbackSessionStatsData;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_STATS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        logoutUser();
        stubCourse = getTypicalCourse();
        stubFeedbackSession = getTypicalFeedbackSessionForCourse(stubCourse);
        stubInstructor = getTypicalInstructor();
        stubInstructor.setAccount(getTypicalAccount());
        stubFeedbackSessionStatsData = new FeedbackSessionStatsData(5, 10);
        reset(mockLogic);
    }

    @Test
    void testExecute_invalidParams_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.COURSE_ID, null
        );

        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, null
        );

        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.COURSE_ID, stubCourse.getId()
        );

        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName()
        );

        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, stubCourse.getId()
        );

        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_instructorAccessOwnStats_getStats() {
        loginAsInstructor(stubInstructor.getGoogleId());

        mockInstructorAndSession(stubCourse.getId(), stubInstructor, stubFeedbackSession, stubFeedbackSessionStatsData);
        FeedbackSessionStatsData output = executeAction(stubFeedbackSession.getName(), stubCourse.getId());

        assertEquals(stubFeedbackSessionStatsData.getSubmittedTotal(), output.getSubmittedTotal());
        assertEquals(stubFeedbackSessionStatsData.getExpectedTotal(), output.getExpectedTotal());
    }

    @Test
    void testExecute_nonExistentFeedbackSession_throwsEntityDoesNotExistException() {
        loginAsInstructor(stubInstructor.getGoogleId());

        verifyEntityNotFound(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent-feedback-session",
                Const.ParamsNames.COURSE_ID, stubCourse.getId()
        );
    }

    @Test
    void testExecute_nonExistentCourse_throwsEntityDoesNotExistException() {
        loginAsInstructor(stubInstructor.getGoogleId());

        verifyEntityNotFound(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, "non-existent-course"
        );
    }

    @Test
    void testSpecificAccessControl_invalidParams_cannotAccess() {
        verifyCannotAccess(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.COURSE_ID, null
        );

        verifyCannotAccess(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, null
        );

        verifyCannotAccess(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.COURSE_ID, stubCourse.getId()
        );

        verifyCannotAccess(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName()
        );

        verifyCannotAccess(
                Const.ParamsNames.COURSE_ID, stubCourse.getId()
        );

        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        verifyCannotAccess();
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testSpecificAccessControl_instructorValidParamsOwnCourse_canAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());

        mockInstructorAndSession(stubCourse.getId(), stubInstructor, stubFeedbackSession, stubFeedbackSessionStatsData);
        executeAction(stubFeedbackSession.getName(), stubCourse.getId());
        verifyCanAccess(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, stubCourse.getId()
        );
    }

    @Test
    void testSpecificAccessControl_instructorNonExistentCourse_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, "non-existent-course",
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), "non-existent-course"))
                .thenReturn(null);

        verifyEntityNotFoundAcl(params);
    }

    @Test
    void testSpecificAccessControl_instructorNonExistentFeedbackSession_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent-feedback-session",
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession("non-existent-feedback-session", stubCourse.getId()))
                .thenReturn(null);

        verifyEntityNotFoundAcl(params);
    }

    @Test
    void testSpecificAccessControl_invalidInstructor_cannotAccess() {
        loginAsInstructor("invalid-id");

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), "invalid-id"))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubCourse.getId()))
                .thenReturn(stubFeedbackSession);

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorAccessNotOwnCourse_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubCourse.getId()))
                .thenReturn(stubFeedbackSession);
        verifyCannotAccess(params);

        Instructor anotherInstructor = getTypicalInstructor();
        anotherInstructor.setAccount(getTypicalAccount());
        anotherInstructor.setCourse(
                new Course("another-course", "Another Course", Const.DEFAULT_TIME_ZONE, "teammates")
        );
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(anotherInstructor);
        verifyCannotAccess(params);
    }

    private FeedbackSessionStatsData executeAction(String feedbackSessionName, String courseId) {
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.COURSE_ID, courseId
        };
        GetSessionResponseStatsAction action = getAction(params);
        return (FeedbackSessionStatsData) getJsonResult(action).getOutput();
    }

    private void mockInstructorAndSession(String courseId, Instructor instructor, FeedbackSession session,
                                          FeedbackSessionStatsData statsData) {
        when(mockLogic.getInstructorByGoogleId(courseId, instructor.getGoogleId()))
                .thenReturn(instructor);
        when(mockLogic.getFeedbackSession(session.getName(), courseId))
                .thenReturn(session);
        when(mockLogic.getExpectedTotalSubmission(session))
                .thenReturn(statsData.getExpectedTotal());
        when(mockLogic.getActualTotalSubmission(session))
                .thenReturn(statsData.getSubmittedTotal());
    }
}
