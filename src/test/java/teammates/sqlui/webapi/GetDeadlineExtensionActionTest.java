package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.User;
import teammates.ui.output.DeadlineExtensionData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetDeadlineExtensionAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetDeadlineExtensionAction}.
 */
public class GetDeadlineExtensionActionTest extends BaseActionTest<GetDeadlineExtensionAction> {
    private DeadlineExtensionAttributes deadlineExtension;

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
        deadlineExtension = getTypicalDeadlineExtensionAttributesStudent();
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

        when(mockLogic.getFeedbackSession(deadlineExtension.getFeedbackSessionName(), deadlineExtension.getCourseId()))
                .thenReturn(getTypicalDeadlineExtensionStudent().getFeedbackSession());
        when(mockLogic.getExtendedDeadlineForUser(isA(FeedbackSession.class), isA(User.class)))
                .thenReturn(null);

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Deadline extension for course id: " + deadlineExtension.getCourseId() + " and "
                + "feedback session name: " + deadlineExtension.getFeedbackSessionName() + " and student "
                + "email: " + deadlineExtension.getUserEmail() + " not found.",
                enfe.getMessage());
    }

    @Test
    void testExecute_typicalCaseStudent_shouldSucceed() {
        String[] params = getNormalParams();

        when(mockLogic.getFeedbackSession(deadlineExtension.getFeedbackSessionName(), deadlineExtension.getCourseId()))
                .thenReturn(getTypicalDeadlineExtensionStudent().getFeedbackSession());
        when(mockLogic.getStudentForEmail(deadlineExtension.getCourseId(), deadlineExtension.getUserEmail()))
                .thenReturn(getTypicalStudent());
        when(mockLogic.getExtendedDeadlineForUser(isA(FeedbackSession.class), isA(User.class)))
                .thenReturn(deadlineExtension.getEndTime());

        GetDeadlineExtensionAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        DeadlineExtensionData response = (DeadlineExtensionData) r.getOutput();
        compareOutput(response);
    }

    @Test
    void testExecute_typicalCaseInstructor_shouldSucceed() {
        deadlineExtension = getTypicalDeadlineExtensionAttributesInstructor();
        String[] params = getNormalParams();

        when(mockLogic.getFeedbackSession(deadlineExtension.getFeedbackSessionName(), deadlineExtension.getCourseId()))
                .thenReturn(getTypicalDeadlineExtensionInstructor().getFeedbackSession());
        when(mockLogic.getInstructorForEmail(deadlineExtension.getCourseId(), deadlineExtension.getUserEmail()))
                .thenReturn(getTypicalInstructor());
        when(mockLogic.getExtendedDeadlineForUser(isA(FeedbackSession.class), isA(User.class)))
                .thenReturn(deadlineExtension.getEndTime());

        GetDeadlineExtensionAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        DeadlineExtensionData response = (DeadlineExtensionData) r.getOutput();

        compareOutput(response);
    }

    private String[] getNormalParams() {
        return new String[] {
                Const.ParamsNames.COURSE_ID, deadlineExtension.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
        };
    }

    private void compareOutput(DeadlineExtensionData response) {
        assertEquals(deadlineExtension.getCourseId(), response.getCourseId());
        assertEquals(deadlineExtension.getFeedbackSessionName(), response.getFeedbackSessionName());
        assertEquals(deadlineExtension.getUserEmail(), response.getUserEmail());
        assertEquals(deadlineExtension.getIsInstructor(), response.getIsInstructor());
        assertEquals(deadlineExtension.getEndTime().toEpochMilli(),
                Instant.ofEpochMilli(response.getEndTime()).toEpochMilli());
        assertEquals(deadlineExtension.getSentClosingSoonEmail(), response.getSentClosingSoonEmail());
    }

}
