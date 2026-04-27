package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.RestoreFeedbackSessionAction;

/**
 * SUT: {@link RestoreFeedbackSessionAction}.
 */
public class RestoreFeedbackSessionActionTest extends BaseActionTest<RestoreFeedbackSessionAction> {
    private static final String GOOGLE_ID = "user-googleId";
    private static final String COURSE_ID = "course-id";

    private FeedbackSession stubFeedbackSession;
    private Instructor stubInstructor;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    protected void setUp() {
        Course stubCourse = getTypicalCourse();
        stubCourse.setId(COURSE_ID);

        stubFeedbackSession = getTypicalFeedbackSessionForCourse(stubCourse);
        stubFeedbackSession.setCreatedAt(Instant.now());

        stubInstructor = getTypicalInstructor();
    }

    @Test
    void testExecute_typicalCase_success() throws EntityDoesNotExistException {
        when(mockLogic.restoreFeedbackSessionFromRecycleBin(stubFeedbackSession.getId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(COURSE_ID, GOOGLE_ID)).thenReturn(stubInstructor);

        loginAsInstructor(GOOGLE_ID);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
        };

        RestoreFeedbackSessionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        FeedbackSessionData output = (FeedbackSessionData) result.getOutput();

        assertEquals(stubFeedbackSession.getId(), output.getFeedbackSessionId());
    }

    @Test
    void testExecute_emptyParams_throwsInvalidHttpParameterException() {
        loginAsInstructor(GOOGLE_ID);
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_invalidFeedbackSessionIdParam_throwsInvalidHttpParameterException() {
        loginAsInstructor(GOOGLE_ID);

        ______TS("Null fsId parameter");
        String[] nullFsIdParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, null,
        };

        verifyHttpParameterFailure(nullFsIdParams);
    }

    @Test
    void testExecute_entityDoesNotExist_throwsEntityNotFoundException() throws EntityDoesNotExistException {
        when(mockLogic.restoreFeedbackSessionFromRecycleBin(stubFeedbackSession.getId()))
                .thenThrow(new EntityDoesNotExistException("Error Msg"));

        loginAsInstructor(GOOGLE_ID);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
        };

        verifyEntityNotFound(params);
    }

    @Test
    void testAccessControl() {
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId()))
                .thenReturn(stubFeedbackSession);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                stubFeedbackSession.getCourse(),
                Const.InstructorPermissions.CAN_MODIFY_SESSION,
                params
        );
        verifyInstructorsOfOtherCoursesCannotAccess(params);
    }
}
