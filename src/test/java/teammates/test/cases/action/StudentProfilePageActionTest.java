package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentProfilePageAction;
import teammates.ui.pagedata.PageData;

/**
 * SUT: {@link StudentProfilePageAction}.
 */
public class StudentProfilePageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_PROFILE_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        AccountAttributes student = typicalBundle.accounts.get("student1InCourse1");
        testActionSuccess(student, "Typical case");
        testActionInMasquerade(student);
        student = typicalBundle.accounts.get("student1InTestingSanitizationCourse");
        // simulate sanitization that occurs before persistence
        student.sanitizeForSaving();
        testActionSuccess(student, "Typical case: attempted script injection");
    }

    private void testActionSuccess(AccountAttributes student, String caseDescription) {
        gaeSimulation.loginAsStudent(student.googleId);
        ______TS(caseDescription);
        String[] submissionParams = new String[] {};
        StudentProfilePageAction action = getAction(submissionParams);
        ShowPageResult result = getShowPageResult(action);

        AssertHelper.assertContains(
                getPageResultDestination(Const.ViewURIs.STUDENT_PROFILE_PAGE, false, student.googleId),
                result.getDestinationWithParams());
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
        ShowPageResult result = getShowPageResult(action);

        AssertHelper.assertContains(
                getPageResultDestination(Const.ViewURIs.STUDENT_PROFILE_PAGE, false, student.googleId),
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
                                  + student.googleId
                                  + "|||/page/studentProfilePage";
        AssertHelper.assertLogMessageEqualsIgnoreLogId(expectedLogMessage, action.getLogMessage());
    }

    @Override
    protected StudentProfilePageAction getAction(String... params) {
        return (StudentProfilePageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyAnyRegisteredUserCanAccess(submissionParams);
    }

}
