package teammates.sqlui.webapi;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.RestoreFeedbackSessionAction;

/**
 * SUT: {@link RestoreFeedbackSessionAction}.
 */
public class RestoreFeedbackSessionActionTest extends BaseActionTest<RestoreFeedbackSessionAction> {
    private static final String GOOGLE_ID = "user-googleId";
    private static final String COURSE_ID = "course-id";
    private static final String FEEDBACK_SESSION_NAME = "feedback-session-name";

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
        stubFeedbackSession.setName(FEEDBACK_SESSION_NAME);
        stubFeedbackSession.setCreatedAt(Instant.now());

        stubInstructor = getTypicalInstructor();
    }

    @Test
    void testExecute_typicalCase_success() throws EntityDoesNotExistException {
        when(mockLogic.getFeedbackSessionFromRecycleBin(FEEDBACK_SESSION_NAME, COURSE_ID))
                .thenReturn(stubFeedbackSession);
        doNothing().when(mockLogic).restoreFeedbackSessionFromRecycleBin(FEEDBACK_SESSION_NAME, COURSE_ID);
        when(mockLogic.getFeedbackSession(FEEDBACK_SESSION_NAME, COURSE_ID))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(COURSE_ID, GOOGLE_ID)).thenReturn(stubInstructor);

        loginAsInstructor(GOOGLE_ID);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, COURSE_ID,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME,
        };

        RestoreFeedbackSessionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        FeedbackSessionData feedbackSessionMessage = (FeedbackSessionData) result.getOutput();

        assertEquals(COURSE_ID, feedbackSessionMessage.getCourseId());
        assertEquals(FEEDBACK_SESSION_NAME, feedbackSessionMessage.getFeedbackSessionName());
    }

    @Test
    void testExecute_emptyParams_throwsInvalidHttpParameterException() {
        loginAsInstructor(GOOGLE_ID);
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_invalidCourseIdParam_throwsInvalidHttpParameterException() {
        loginAsInstructor(GOOGLE_ID);

        ______TS("Missing courseId parameter");
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME);

        ______TS("Null courseId parameter");
        String[] nullCourseIdParams = new String[] {
                Const.ParamsNames.COURSE_ID, null,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME,
        };

        verifyHttpParameterFailure(nullCourseIdParams);
    }

    @Test
    void testExecute_invalidFeedbackSessionNameParam_throwsInvalidHttpParameterException() {
        loginAsInstructor(GOOGLE_ID);

        ______TS("Missing feedbackSessionName parameter");
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, COURSE_ID);

        ______TS("Null feedbackSessionName parameter");
        String[] nullFeedbackSessionNameParams = new String[] {
                Const.ParamsNames.COURSE_ID, COURSE_ID,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
        };

        verifyHttpParameterFailure(nullFeedbackSessionNameParams);
    }

    @Test
    void testExecute_sessionNotInBin_throwsEntityNotFoundException() {
        when(mockLogic.getFeedbackSessionFromRecycleBin(FEEDBACK_SESSION_NAME, COURSE_ID)).thenReturn(null);

        loginAsInstructor(GOOGLE_ID);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, COURSE_ID,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Feedback session is not in recycle bin", enfe.getMessage());
    }

    @Test
    void testExecute_restoreFeedbackSessionThrowsEntityDoesNotExist_throwsEntityNotFoundException()
            throws EntityDoesNotExistException {
        when(mockLogic.getFeedbackSessionFromRecycleBin(FEEDBACK_SESSION_NAME, COURSE_ID))
                .thenReturn(stubFeedbackSession);
        doThrow(new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT)).when(mockLogic)
                .restoreFeedbackSessionFromRecycleBin(FEEDBACK_SESSION_NAME, COURSE_ID);

        loginAsInstructor(GOOGLE_ID);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, COURSE_ID,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals(ERROR_UPDATE_NON_EXISTENT, enfe.getMessage());
    }

    @Test
    void testAccessControl_notLoggedIn_cannotAccess() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, COURSE_ID,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME,
        };

        logoutUser();
        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_unregisteredUser_cannotAccess() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, COURSE_ID,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME,
        };

        loginAsUnregistered(GOOGLE_ID);
        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_student_cannotAccess() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, COURSE_ID,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME,
        };

        loginAsStudent(GOOGLE_ID);
        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorOfOtherCourses_cannotAccess() {
        when(mockLogic.getFeedbackSessionFromRecycleBin(FEEDBACK_SESSION_NAME, COURSE_ID))
                .thenReturn(stubFeedbackSession);
        Course otherCourse = getTypicalCourse();
        otherCourse.setId("other-course-id");
        stubInstructor.setCourse(otherCourse);
        when(mockLogic.getInstructorByGoogleId(COURSE_ID, GOOGLE_ID)).thenReturn(stubInstructor);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, COURSE_ID,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME,
        };

        loginAsInstructor(GOOGLE_ID);
        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorOfSameCourseWithoutCorrectCoursePrivilege_cannotAccess() {
        when(mockLogic.getFeedbackSessionFromRecycleBin(FEEDBACK_SESSION_NAME, COURSE_ID))
                .thenReturn(stubFeedbackSession);
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, false);
        stubInstructor.setPrivileges(privileges);
        when(mockLogic.getInstructorByGoogleId(COURSE_ID, GOOGLE_ID)).thenReturn(stubInstructor);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, COURSE_ID,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME,
        };

        loginAsInstructor(GOOGLE_ID);
        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorOfSameCourseWithCorrectCoursePrivilege_canAccess() {
        when(mockLogic.getFeedbackSessionFromRecycleBin(FEEDBACK_SESSION_NAME, COURSE_ID))
                .thenReturn(stubFeedbackSession);
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, true);
        stubInstructor.setPrivileges(privileges);
        when(mockLogic.getInstructorByGoogleId(COURSE_ID, GOOGLE_ID)).thenReturn(stubInstructor);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, COURSE_ID,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, FEEDBACK_SESSION_NAME,
        };

        loginAsInstructor(GOOGLE_ID);
        verifyCanAccess(params);
    }
}
