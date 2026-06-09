package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivilegesLegacy;
import teammates.common.datatransfer.Provider;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteAccountAction}.
 */
public class DeleteAccountActionTest extends BaseActionTest<DeleteAccountAction> {
    String googleId = "user-googleId";
    UUID accountId = UUID.fromString("00000000-0000-4000-8000-000000000001");

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
    }

    @Test
    protected void textExecute_nullParams_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.ACCOUNT_ID, null,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    protected void testExecute_nonNullParams_success() {
        Course stubCourse = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Account stubAccount = new Account(googleId, Provider.TEAMMATES_DEV, "validInstructorSubject",
                "validTenantId", "name", "instructoremail@tm.tmt");
        Instructor instructor = new Instructor(stubCourse, "name", "instructoremail@tm.tmt",
                false, "", null, new InstructorPrivilegesLegacy());
        instructor.setAccount(stubAccount);
        when(mockLogic.getAccount(accountId)).thenReturn(stubAccount);
        String[] params = {
                Const.ParamsNames.ACCOUNT_ID, accountId.toString(),
        };
        DeleteAccountAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("Account is successfully deleted.", actionOutput.getMessage());
        verify(mockLogic, times(1)).getAccount(accountId);
        verify(mockLogic, times(1)).deleteAccountCascade(googleId);
    }
}
