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
        String[] params1 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.COURSE_ID, null,
        };
        verifyHttpParameterFailure(params1);

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, null,
        };
        verifyHttpParameterFailure(params2);

        String[] params3 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyHttpParameterFailure(params3);

        String[] params4 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),

        };
        verifyHttpParameterFailure(params4);

        String[] params5 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyHttpParameterFailure(params5);

        String[] params6 = {};
        verifyHttpParameterFailure(params6);
    }

    @Test
    void testExecute_instructorAccessOwnStats_getStats() {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        when(mockLogic.getExpectedTotalSubmission(stubFeedbackSession))
                .thenReturn(stubFeedbackSessionStatsData.getExpectedTotal());
        when(mockLogic.getActualTotalSubmission(stubFeedbackSession))
                .thenReturn(stubFeedbackSessionStatsData.getSubmittedTotal());
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubCourse.getId()))
                .thenReturn(stubFeedbackSession);

        GetSessionResponseStatsAction action = getAction(params);
        FeedbackSessionStatsData output = (FeedbackSessionStatsData) getJsonResult(action).getOutput();
        assertEquals(stubFeedbackSessionStatsData.getSubmittedTotal(), output.getSubmittedTotal());
        assertEquals(stubFeedbackSessionStatsData.getExpectedTotal(), output.getExpectedTotal());
    }

    @Test
    void testExecute_nonExistentFeedbackSession_throwsEntityDoesNotExistException() {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "nonexistentFeedbackSession",
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        when(mockLogic.getFeedbackSession("nonexistentFeedbackSession", stubCourse.getId())).thenReturn(null);
        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_nonExistentCourse_throwsEntityDoesNotExistException() {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, "nonexistentCourse",
        };
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), "nonexistentCourse")).thenReturn(null);
        verifyEntityNotFound(params);
    }

    @Test
    void testSpecificAccessControl_invalidParams_cannotAccess() {
        String[] params1 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.COURSE_ID, null,
        };
        verifyCannotAccess(params1);

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, null,
        };
        verifyCannotAccess(params2);

        String[] params3 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCannotAccess(params3);

        String[] params4 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),

        };
        verifyCannotAccess(params4);

        String[] params5 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCannotAccess(params5);

        String[] params6 = {};
        verifyCannotAccess(params6);
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

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, stubFeedbackSession.getName(),
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubCourse.getId()))
                .thenReturn(stubFeedbackSession);
        verifyCanAccess(params);
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
                new Course("another-course", "Another Course", Const.DEFAULT_TIME_ZONE, "teammates"));
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(anotherInstructor);
        verifyCannotAccess(params);
    }
}
