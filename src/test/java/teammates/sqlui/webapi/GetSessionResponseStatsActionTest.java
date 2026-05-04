package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.logic.entity.Course;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
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
        verifyHttpParameterFailure();

        String[] params1 = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, null,
        };
        verifyHttpParameterFailure(params1);
    }

    @Test
    void testExecute_instructorAccessOwnStats_getStats() {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
        };
        when(mockLogic.getExpectedTotalSubmission(stubFeedbackSession))
                .thenReturn(stubFeedbackSessionStatsData.getExpectedTotal());
        when(mockLogic.getActualTotalSubmission(stubFeedbackSession))
                .thenReturn(stubFeedbackSessionStatsData.getSubmittedTotal());
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId()))
                .thenReturn(stubFeedbackSession);

        GetSessionResponseStatsAction action = getAction(params);
        FeedbackSessionStatsData output = (FeedbackSessionStatsData) getJsonResult(action).getOutput();
        assertEquals(stubFeedbackSessionStatsData.getSubmittedTotal(), output.getSubmittedTotal());
        assertEquals(stubFeedbackSessionStatsData.getExpectedTotal(), output.getExpectedTotal());
    }

    @Test
    void testExecute_nonExistentFeedbackSession_throwsEntityDoesNotExistException() {
        loginAsInstructor(stubInstructor.getGoogleId());

        UUID nonExistentId = UUID.randomUUID();
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, nonExistentId.toString(),
        };
        when(mockLogic.getFeedbackSession(nonExistentId)).thenReturn(null);
        verifyEntityNotFound(params);
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
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId())).thenReturn(stubFeedbackSession);
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorNonExistentFeedbackSession_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructor.getGoogleId());
        UUID nonExistentId = UUID.randomUUID();

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, nonExistentId.toString(),
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession(nonExistentId)).thenReturn(null);
        verifyEntityNotFoundAcl(params);
    }

    @Test
    void testSpecificAccessControl_invalidInstructor_cannotAccess() {
        loginAsInstructor("invalid-id");

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), "invalid-id"))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId()))
                .thenReturn(stubFeedbackSession);
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorAccessNotOwnCourse_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId()))
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
