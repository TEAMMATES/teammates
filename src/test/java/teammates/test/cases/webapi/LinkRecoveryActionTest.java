package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.LinkRecoveryAction;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link LinkRecoveryAction}.
 */
public class LinkRecoveryActionTest extends BaseActionTest<LinkRecoveryAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.LINK_RECOVERY;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("Invalid parameters");

        // no params
        verifyHttpParameterFailure();

        ______TS("Typical case: non-existing email");

        String[] nonExistingParam = new String[] {
                Const.ParamsNames.RECOVERY_EMAIL, "non-existent email",
        };

        LinkRecoveryAction a = getAction(nonExistingParam);
        JsonResult result = getJsonResult(a);

        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(output.getMessage(), "No response found with given email");
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyNumberOfEmailsSent(a, 0);

        ______TS("Typical case: successfully sent recovery link email");

        String[] param = new String[] {
                Const.ParamsNames.RECOVERY_EMAIL, student1InCourse1.getEmail(),
        };

        a = getAction(param);
        result = getJsonResult(a);

        output = (MessageOutput) result.getOutput();

        assertEquals(output.getMessage(), "An recovery link has just been sent to the given email.");
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyNumberOfEmailsSent(a, 1);

        EmailWrapper emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(EmailType.FEEDBACK_ACCESS_LINKS_RESENT.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse1.getEmail(), emailSent.getRecipient());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAnyUserCanAccess();
    }
}
