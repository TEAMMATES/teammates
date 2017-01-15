package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.PageData;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentProfilePageAction;

public class StudentProfilePageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.STUDENT_PROFILE_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() {
        AccountAttributes student = dataBundle.accounts.get("student1InCourse1");
        testActionSuccessTypical(student);
        testActionInMasquerade(student);
    }

    private void testActionSuccessTypical(AccountAttributes student) {
        gaeSimulation.loginAsStudent(student.googleId);
        ______TS("Typical case");
        String[] submissionParams = new String[] {};
        StudentProfilePageAction action = getAction(submissionParams);
        ShowPageResult result = (ShowPageResult) action.executeAndPostProcess();

        AssertHelper.assertContains("/jsp/studentProfilePage.jsp?error=false&user="
                                    + student.googleId, result.getDestinationWithParams());
        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());

        verifyAccountsAreSame(student, result);
        verifyLogMessage(student, action, false);
    }

    private void testActionInMasquerade(AccountAttributes student) {
        gaeSimulation.loginAsAdmin("admin.user");
        ______TS("Typical case: masquerade mode");
        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_PROFILE_PHOTOEDIT, "false",
                Const.ParamsNames.USER_ID, student.googleId
        };

        StudentProfilePageAction action = getAction(addUserIdToParams(
                student.googleId, submissionParams));
        ShowPageResult result = (ShowPageResult) action.executeAndPostProcess();

        AssertHelper.assertContains(Const.ViewURIs.STUDENT_PROFILE_PAGE
                                    + "?error=false&user=" + student.googleId,
                                    result.getDestinationWithParams());
        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());

        verifyAccountsAreSame(student, result);
        verifyLogMessage(student, action, true);
    }

    // -------------------------------------------------------------------------------------------------------
    // -------------------------------------- Helper Functions
    // -----------------------------------------------
    // -------------------------------------------------------------------------------------------------------

    private void verifyAccountsAreSame(AccountAttributes student,
            ShowPageResult result) {
        PageData data = result.data;
        student.studentProfile.modifiedDate = data.account.studentProfile.modifiedDate;
        student.createdAt = data.account.createdAt;
        assertEquals(student.toString(), data.account.toString());
    }

    private void verifyLogMessage(AccountAttributes student,
            StudentProfilePageAction action, boolean isMasquerade) {
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePage|||studentProfilePage"
                                  + "|||true|||Student" + (isMasquerade ? "(M)" : "") + "|||"
                                  + student.name + "|||" + student.googleId + "|||" + student.email
                                  + "|||studentProfile Page Load <br> Profile: "
                                  + student.studentProfile.toString() + "|||/page/studentProfilePage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    private StudentProfilePageAction getAction(String... params) {
        return (StudentProfilePageAction) gaeSimulation.getActionObject(uri, params);
    }

}
