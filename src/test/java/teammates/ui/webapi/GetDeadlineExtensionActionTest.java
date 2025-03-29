package teammates.ui.webapi;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.util.Const;
import teammates.ui.output.DeadlineExtensionData;

/**
 * SUT: {@link GetDeadlineExtensionsAction}.
 */
public class GetDeadlineExtensionActionTest extends BaseActionTest<GetDeadlineExtensionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.DEADLINE_EXTENSION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        DeadlineExtensionAttributes deadlineExtension = typicalBundle.deadlineExtensions.get("student4InCourse1Session1");

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Missing parameter");

        String[] params = new String[] {
                // Const.ParamsNames.COURSE_ID
                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
        };

        verifyHttpParameterFailure(params);

        params = new String[] {
                Const.ParamsNames.COURSE_ID, deadlineExtension.getCourseId(),
                // Const.ParamsNames.FEEDBACK_SESSION_NAME
                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
        };

        verifyHttpParameterFailure(params);

        params = new String[] {
                Const.ParamsNames.COURSE_ID, deadlineExtension.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
                // Const.ParamsNames.USER_EMAIL
                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
        };

        verifyHttpParameterFailure(params);

        params = new String[] {
                Const.ParamsNames.COURSE_ID, deadlineExtension.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
                // Const.ParamsNames.IS_INSTRUCTOR
        };

        verifyHttpParameterFailure(params);

        ______TS("deadline extension does not exist");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, deadlineExtension.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "unknown-fs-name",
                Const.ParamsNames.USER_EMAIL, "unknown@gmail.tmt",
                Const.ParamsNames.IS_INSTRUCTOR, "false",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Deadline extension for course id: " + deadlineExtension.getCourseId() + " and "
                + "feedback session name: unknown-fs-name and student email: unknown@gmail.tmt not found.",
                enfe.getMessage());

        ______TS("typical success case");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, deadlineExtension.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
        };

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

    @Override
    @Test
    protected void testAccessControl() {
        // Only can access with backdoor key
        verifyInaccessibleForAdmin();
        verifyInaccessibleForInstructors();
        verifyInaccessibleForStudents();
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }

}
