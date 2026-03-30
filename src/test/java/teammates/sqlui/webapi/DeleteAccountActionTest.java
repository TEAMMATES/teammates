package teammates.sqlui.webapi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteAccountAction;

/**
 * SUT: {@link DeleteAccountAction}.
 */
public class DeleteAccountActionTest extends BaseActionTest<DeleteAccountAction> {

    private static final String ACCOUNT_ID = "00000000-0000-4000-8000-0000000000a1";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    void testAccessControl() throws Exception {
        verifyOnlyAdminsCanAccess();
    }

    @Test
    protected void textExecute_nullParams_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, null,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    protected void testExecute_nonNullParams_success() {
        Course stubCourse = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Account stubAccount = new Account("name", "instructoremail@tm.tmt");
        stubAccount.setId(UUID.fromString(ACCOUNT_ID));
        Instructor instructor = new Instructor(stubCourse, "name", "instructoremail@tm.tmt",
                false, "", null, new InstructorPrivileges());
        instructor.setAccount(stubAccount);
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getAccountId(),
        };
        DeleteAccountAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("Account is successfully deleted.", actionOutput.getMessage());
        verify(mockLogic, times(1)).deleteAccountCascade(ACCOUNT_ID);
    }
}
