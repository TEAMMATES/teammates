package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteAccountRequestAction}.
 */
public class DeleteDeadlineExtensionActionTest extends BaseActionTest<DeleteDeadlineExtensionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.DEADLINE_EXTENSION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() {
        DeadlineExtensionAttributes deadlineExtension = typicalBundle.deadlineExtensions.get("student4InCourse1Session1");

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Missing parameter");

        String[] params = new String[] {
                // Const.ParamsNames.USER_EMAIL
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

        ______TS("Typical case, delete an existing deadline extension");

        verifyPresentInDatabase(deadlineExtension);

        params = new String[] {
                Const.ParamsNames.COURSE_ID, deadlineExtension.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, deadlineExtension.getFeedbackSessionName(),
                Const.ParamsNames.USER_EMAIL, deadlineExtension.getUserEmail(),
                Const.ParamsNames.IS_INSTRUCTOR, Boolean.toString(deadlineExtension.getIsInstructor()),
        };

        DeleteDeadlineExtensionAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        MessageOutput msg = (MessageOutput) result.getOutput();

        assertEquals(msg.getMessage(), "Deadline extension successfully deleted.");

        verifyAbsentInDatabase(deadlineExtension);

        ______TS("Typical case, delete non-existing deadline extension");

        action = getAction(params);
        result = getJsonResult(action);
        msg = (MessageOutput) result.getOutput();

        // should fail silently.
        assertEquals(msg.getMessage(), "Deadline extension successfully deleted.");
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
