package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.User;
import teammates.ui.output.DeadlineExtensionData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetDeadlineExtensionAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetDeadlineExtensionAction}.
 */
public class GetDeadlineExtensionActionTest extends BaseActionTest<GetDeadlineExtensionAction> {
    private DeadlineExtension deadlineExtension;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.DEADLINE_EXTENSION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    public void setUp() {
        logoutUser();
        deadlineExtension = getTypicalDeadlineExtensionStudent();
    }

    @Test
    void testAccessControl() {
        verifyNoUsersCanAccess();
        verifyMaintainersCannotAccess();
    }

    @Test
    void testExecute_noParameters_shouldFail() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingParameter_shouldFail() {
        /*
         * Loops through each parameter pairs and removes each parameter pair to test
         * for that
         * missing parameter pair case.
         */
        for (int i = 0; i < getNormalParams().length / 2; i++) {
            ArrayList<String> params = new ArrayList<>(Arrays.asList(getNormalParams()));
            params.remove(i * 2);
            params.remove(i * 2);
            String[] paramsArray = params.toArray(new String[0]);
            verifyHttpParameterFailure(paramsArray);
        }
    }

    @Test
    void testExecute_deadlineExtensionMissing_shouldFail() {
        String[] params = getNormalParams();

        when(mockLogic.getFeedbackSession(deadlineExtension.getFeedbackSession().getName(),
                deadlineExtension.getFeedbackSession().getCourseId()))
                .thenReturn(getTypicalDeadlineExtensionStudent().getFeedbackSession());
        when(mockLogic.getDeadlineExtensionEntityForUser(isA(FeedbackSession.class), isA(User.class)))
                .thenReturn(null);

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Deadline extension for course id: " + deadlineExtension.getFeedbackSession().getCourseId()
                + " and feedback session name: " + deadlineExtension.getFeedbackSession().getName()
                + " and student email: " + deadlineExtension.getUser().getEmail() + " not found.",
                enfe.getMessage());
    }

    @Test
    void testExecute_typicalCaseStudent_shouldSucceed() {
        String[] params = getNormalParams();

        when(mockLogic.getFeedbackSession(deadlineExtension.getFeedbackSession().getName(),
                deadlineExtension.getFeedbackSession().getCourseId()))
                .thenReturn(getTypicalDeadlineExtensionStudent().getFeedbackSession());
        when(mockLogic.getStudentForEmail(deadlineExtension.getFeedbackSession().getCourseId(),
                deadlineExtension.getUser().getEmail()))
                .thenReturn(getTypicalStudent());
        when(mockLogic.getDeadlineExtensionEntityForUser(isA(FeedbackSession.class), isA(User.class)))
                .thenReturn(deadlineExtension);

        GetDeadlineExtensionAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        DeadlineExtensionData response = (DeadlineExtensionData) r.getOutput();
        compareOutput(response);
    }

    @Test
    void testExecute_typicalCaseInstructor_shouldSucceed() {
        deadlineExtension = getTypicalDeadlineExtensionInstructor();
        String[] params = getNormalParams();

        when(mockLogic.getFeedbackSession(deadlineExtension.getFeedbackSession().getName(),
                deadlineExtension.getFeedbackSession().getCourseId()))
                .thenReturn(getTypicalDeadlineExtensionInstructor().getFeedbackSession());
        when(mockLogic.getInstructorForEmail(deadlineExtension.getFeedbackSession().getCourseId(),
                deadlineExtension.getUser().getEmail()))
                .thenReturn(getTypicalInstructor());
        when(mockLogic.getDeadlineExtensionEntityForUser(isA(FeedbackSession.class), isA(User.class)))
                .thenReturn(deadlineExtension);

        GetDeadlineExtensionAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        DeadlineExtensionData response = (DeadlineExtensionData) r.getOutput();

        compareOutput(response);
    }

    private String[] getNormalParams() {
        return new String[] {
                Const.ParamsNames.COURSE_ID, deadlineExtension.getFeedbackSession().getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSession().getName(),
                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUser().getEmail(),
                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getUser() instanceof Instructor),
        };
    }

    private void compareOutput(DeadlineExtensionData response) {
        assertEquals(deadlineExtension.getFeedbackSession().getCourseId(), response.getCourseId());
        assertEquals(deadlineExtension.getFeedbackSession().getName(), response.getFeedbackSessionName());
        assertEquals(deadlineExtension.getUser().getEmail(), response.getUserEmail());
        assertEquals(deadlineExtension.getUser() instanceof Instructor, response.getIsInstructor());
        assertEquals(deadlineExtension.getEndTime().toEpochMilli(),
                Instant.ofEpochMilli(response.getEndTime()).toEpochMilli());
        assertEquals(deadlineExtension.isClosingSoonEmailSent(), response.getSentClosingSoonEmail());
    }

}
