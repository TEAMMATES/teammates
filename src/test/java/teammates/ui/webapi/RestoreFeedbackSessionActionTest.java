package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import teammates.common.datatransfer.InstructorPrivileges;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.output.FeedbackSessionData;

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
    void testAccessControl_sameCourseInstructorWithModifySessionPrivilege_canAccess() {
        Course course = stubFeedbackSession.getCourse();
        stubInstructor.setCourse(course);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId())).thenReturn(stubFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(stubInstructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        loginAsInstructor(stubInstructor.getId().toString());
        verifyCanAccess(Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString());
    }

    @Test
    void testAccessControl_sameCourseInstructorWithoutModifySessionPrivilege_cannotAccess() {
        Course course = stubFeedbackSession.getCourse();
        stubInstructor.setCourse(course);
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, false);
        stubInstructor.setPrivileges(privileges);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId())).thenReturn(stubFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(stubInstructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        loginAsInstructor(stubInstructor.getId().toString());
        verifyCannotAccess(Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString());
    }

    @Test
    void testAccessControl_differentCourseInstructor_cannotAccess() {
        Course otherCourse = new Course("other-course-id", "other-course-name", Const.DEFAULT_TIME_ZONE, "teammates");
        stubInstructor.setCourse(otherCourse);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId())).thenReturn(stubFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(stubInstructor);
        when(mockLogic.getCourse(stubFeedbackSession.getCourse().getId())).thenReturn(stubFeedbackSession.getCourse());
        loginAsInstructor(stubInstructor.getId().toString());
        verifyCannotAccess(Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString());
    }

    @Test
    void testAccessControl_student_cannotAccess() {
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId())).thenReturn(stubFeedbackSession);
        loginAsStudent("student-googleId");
        verifyCannotAccess(Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString());
    }

    @Test
    void testAccessControl_unregistered_cannotAccess() {
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId())).thenReturn(stubFeedbackSession);
        loginAsUnregistered("unregistered-googleId");
        verifyCannotAccess(Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString());
    }

    @Test
    void testAccessControl_loggedOut_cannotAccess() {
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId())).thenReturn(stubFeedbackSession);
        logoutUser();
        verifyCannotAccess(Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString());
    }
}
