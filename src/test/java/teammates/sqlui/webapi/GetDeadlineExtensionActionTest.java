package teammates.sqlui.webapi;


import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.User;
import teammates.ui.output.DeadlineExtensionData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetDeadlineExtensionAction;
import teammates.ui.webapi.JsonResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

/**
 * SUT: {@link GetDeadlineExtensionAction}.
 */
public class GetDeadlineExtensionActionTest extends BaseActionTest<GetDeadlineExtensionAction> {
    private DeadlineExtensionAttributes deadlineExtension = getTypicalDeadlineExtensionAttributes();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.DEADLINE_EXTENSION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testAccessControl_admin_cannotAccess() {
        logoutUser();
        loginAsAdmin();
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_maintainers_cannotAccess() {
        logoutUser();
        loginAsMaintainer();
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_instructor_cannotAccess() {
        logoutUser();
        loginAsInstructor(Const.ParamsNames.INSTRUCTOR_ID);
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_student_cannotAccess() {
        logoutUser();
        loginAsStudent(Const.ParamsNames.STUDENT_ID);
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_unregistered_cannotAccess() {
        logoutUser();
        loginAsUnregistered(Const.ParamsNames.USER_ID);
        verifyCannotAccess();
    }

    @Test
    void testExecute_insufficientParameters_shouldFail() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingParameter_shouldFail() {
        for (int i = 0; i < getNormalParams().length / 2; i++) {
            ArrayList<String> params = new ArrayList<>(Arrays.asList(getNormalParams()));
            params.remove(i * 2);
            params.remove(i * 2);
            String[] paramsArray = params.toArray(new String[params.size()]);
            verifyHttpParameterFailure(paramsArray);
        }
    }

//    @Test
//    void testExecute_missingParameterCourseId_shouldFail() {
//        String[] params = new String[] {
//                // Const.ParamsNames.COURSE_ID, getTypicalCourse().getId(),
//                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
//                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
//                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
//        };
//
//        verifyHttpParameterFailure(params);
//    }
//
//    @Test
//    void testExecute_missingParameterFeedbackSessionName_shouldFail() {
//        String[] params = new String[] {
//                Const.ParamsNames.COURSE_ID, getTypicalCourse().getId(),
//                // Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
//                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
//                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
//        };
//
//        verifyHttpParameterFailure(params);
//    }
//
//    @Test
//    void testExecute_missingParameterUserEmail_shouldFail() {
//        String[] params = new String[] {
//                Const.ParamsNames.COURSE_ID, getTypicalCourse().getId(),
//                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
//                // Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
//                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
//        };
//
//        verifyHttpParameterFailure(params);
//    }
//
//    @Test
//    void testExecute_missingParameterIsInstructor_shouldFail() {
//        String[] params = new String[] {
//                Const.ParamsNames.COURSE_ID, getTypicalCourse().getId(),
//                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
//                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
//                // Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
//        };
//
//        verifyHttpParameterFailure(params);
//    }

    @Test
    void testExecute_deadlineExtensionMissing_shouldFail() {
        String[] params = getNormalParams();

        when(mockLogic.getFeedbackSession(deadlineExtension.getFeedbackSessionName(), deadlineExtension.getCourseId()))
                .thenReturn(getTypicalDeadlineExtension().getFeedbackSession());
        when(mockLogic.getExtendedDeadlineForUser(isA(FeedbackSession.class), isA(User.class)))
                .thenReturn(null);

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Deadline extension for course id: " + deadlineExtension.getCourseId() + " and "
                        + "feedback session name: " + deadlineExtension.getFeedbackSessionName() + " and student "
                        + "email: " + deadlineExtension.getUserEmail() + " not found.",
                enfe.getMessage());
    }

    @Test
    void testExecute_typicalCase_shouldSucceed() {
        String[] params = getNormalParams();

        when(mockLogic.getFeedbackSession(deadlineExtension.getFeedbackSessionName(), deadlineExtension.getCourseId()))
                .thenReturn(getTypicalDeadlineExtension().getFeedbackSession());
        when(mockLogic.getExtendedDeadlineForUser(isA(FeedbackSession.class), isA(User.class)))
                .thenReturn(Instant.ofEpochMilli(deadlineExtension.getEndTime().toEpochMilli()));

        GetDeadlineExtensionAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        DeadlineExtensionData response = (DeadlineExtensionData) r.getOutput();

        assertEquals(deadlineExtension.getCourseId(), response.getCourseId());
        assertEquals(deadlineExtension.getFeedbackSessionName(), response.getFeedbackSessionName());
        assertEquals(deadlineExtension.getUserEmail(), response.getUserEmail());
        assertEquals(deadlineExtension.getIsInstructor(), response.getIsInstructor());
        assertEquals(deadlineExtension.getEndTime(), Instant.ofEpochMilli(response.getEndTime()));
        assertEquals(deadlineExtension.getSentClosingSoonEmail(), response.getSentClosingSoonEmail());
    }

    private String[] getNormalParams() {
        return new String[] {
                Const.ParamsNames.COURSE_ID, deadlineExtension.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
        };
    }

}
