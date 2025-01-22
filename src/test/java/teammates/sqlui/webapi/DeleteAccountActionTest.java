package teammates.sqlui.webapi;

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
    String googleId = "user-googleId";
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }
    @Test
    protected void textExecute_accountDoesNotExist_failSilently() {
         String[] params = {
              Const.ParamsNames.INSTRUCTOR_ID, "non-existing-account",
         };
            DeleteAccountAction action = getAction(params);
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
            assertEquals("Account is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    protected void testExecute_accountExists_success() {
        Course stubCourse = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Account stubAccount = new Account(googleId, "name", "instructoremail@tm.tmt");
        Instructor instructor = new Instructor(stubCourse, "name", "instructoremail@tm.tmt",
                false, "", null, new InstructorPrivileges());
        instructor.setAccount(stubAccount);
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };
        DeleteAccountAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("Account is successfully deleted.", actionOutput.getMessage());
        assertNull(mockLogic.getInstructorByGoogleId(stubCourse.getId(),instructor.getGoogleId()));
    }
}
