package teammates.sqlui.webapi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.webapi.BinFeedbackSessionAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link BinFeedbackSessionAction}.
 */
public class BinFeedbackSessionActionTest extends BaseActionTest<BinFeedbackSessionAction> {

    private Instructor typicalInstructor;
    private FeedbackSession typicalFeedbackSession;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUpMethod() {
        Course typicalCourse = getTypicalCourse();
        typicalInstructor = getTypicalInstructor();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        typicalFeedbackSession.setCreatedAt(Instant.now().minus(Duration.ofMinutes(15)));
    }

    @AfterMethod
    void tearDownMethod() {
        Mockito.reset(mockLogic);
    }

    @Test
    void testExecute_typicalCase_success() throws Exception {
        loginAsInstructor(typicalInstructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.moveFeedbackSessionToRecycleBin(typicalFeedbackSession.getName(),
                typicalFeedbackSession.getCourseId())).thenReturn(typicalFeedbackSession);

        typicalFeedbackSession.setDeletedAt(Instant.now());

        BinFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(typicalFeedbackSession.getName(), response.getFeedbackSessionName());
        assertEquals(typicalFeedbackSession.getCourseId(), response.getCourseId());
        assertEquals(typicalFeedbackSession.getId(), response.getFeedbackSessionId());

        verify(mockLogic, times(1))
                .moveFeedbackSessionToRecycleBin(typicalFeedbackSession.getName(), typicalFeedbackSession.getCourseId());
    }

    @Test
    void testExecute_invalidHttpParameters_throwsInvalidHttpParameterException() {
        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, typicalFeedbackSession.getCourseId());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName());
    }

    @Test
    void testAccessControl_nonExistentFeedbackSession_throwsEntityNotFoundException() {
        loginAsInstructor(typicalInstructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName",
        };

        verifyEntityNotFoundAcl(params);
        verify(mockLogic, times(1))
                .getFeedbackSession("randomName", typicalFeedbackSession.getCourseId());
    }

    @Test
    void testAccessControl_instructorWithoutPrivilege_cannotAccess() {
        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setPrivileges(
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER));
        instructorWithoutPrivilege.setGoogleId("instructorWithoutPrivilege");

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalFeedbackSession.getCourseId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalFeedbackSession.getCourseId(),
                instructorWithoutPrivilege.getGoogleId())).thenReturn(instructorWithoutPrivilege);

        verifyCannotAccess(params);

        verify(mockLogic, times(1))
                .getFeedbackSession(typicalFeedbackSession.getName(), typicalFeedbackSession.getCourseId());
        verify(mockLogic, times(1))
                .getInstructorByGoogleId(typicalFeedbackSession.getCourseId(),
                        instructorWithoutPrivilege.getGoogleId());
    }

    @Test
    void testAccessControl_instructorOfOtherCourse_cannotAccess() {
        Instructor instructorOfOtherCourse = getTypicalInstructor();
        Course otherCourse = new Course("different-id", "different-name",
                Const.DEFAULT_TIME_ZONE, "teammates");
        instructorOfOtherCourse.setCourse(otherCourse);

        loginAsInstructor(instructorOfOtherCourse.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalFeedbackSession.getCourseId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalFeedbackSession.getCourseId(),
                instructorOfOtherCourse.getGoogleId())).thenReturn(instructorOfOtherCourse);

        verifyCannotAccess(params);

        verify(mockLogic, times(1))
                .getFeedbackSession(typicalFeedbackSession.getName(), typicalFeedbackSession.getCourseId());
        verify(mockLogic, times(1))
                .getInstructorByGoogleId(typicalFeedbackSession.getCourseId(),
                        instructorOfOtherCourse.getGoogleId());
    }

    @Test
    void testAccessControl_nonInstructor_cannotAccess() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalFeedbackSession.getCourseId()))
                .thenReturn(typicalFeedbackSession);

        ______TS("Non-logged-in users cannot access");

        logoutUser();
        verifyCannotAccess(params);

        ______TS("Non-registered users cannot access");

        loginAsUnregistered("unregistered user");
        verifyCannotAccess(params);
        verify(mockLogic, times(1))
                .getInstructorByGoogleId(typicalFeedbackSession.getCourseId(), "unregistered user");

        ______TS("Students cannot access");

        loginAsStudent(getTypicalStudent().getGoogleId());
        verifyCannotAccess(params);
        verify(mockLogic, times(1))
                .getInstructorByGoogleId(typicalFeedbackSession.getCourseId(), getTypicalStudent().getGoogleId());
    }

}
