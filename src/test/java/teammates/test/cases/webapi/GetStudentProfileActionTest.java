package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetStudentProfileAction;
import teammates.ui.webapi.action.GetStudentProfileAction.StudentProfile;
import teammates.ui.webapi.action.JsonResult;

/**
 * SUT: {@link GetStudentProfileAction}.
 */
public class GetStudentProfileActionTest extends BaseActionTest<GetStudentProfileAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        AccountAttributes student1 = typicalBundle.accounts.get("student1InCourse1");
        AccountAttributes student2 = typicalBundle.accounts.get("student2InCourse1");

        StudentProfileAttributes student1InCourse1Profile = typicalBundle.profiles.get("student1InCourse1");
        testActionSuccess(student1, student1InCourse1Profile, "Typical case");

        testActionForbidden(student1, student2, "Forbidden case");

        testActionInMasquerade(student1);

        student1 = typicalBundle.accounts.get("student1InTestingSanitizationCourse");
        StudentProfileAttributes student1InTestingSanitizationCourseProfile =
                typicalBundle.profiles.get("student1InTestingSanitizationCourse");
        // simulate sanitization that occurs before persistence
        student1.sanitizeForSaving();
        testActionSuccess(student1, student1InTestingSanitizationCourseProfile,
                "Typical case: attempted script injection");
    }

    private void testActionSuccess(AccountAttributes student, StudentProfileAttributes studentProfileAttributes,
                                   String caseDescription) {
        loginAsStudent(student.googleId);

        ______TS(caseDescription);

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_ID, student.googleId,
        };
        GetStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        StudentProfile output = (StudentProfile) result.getOutput();
        output.getStudentProfile().modifiedDate = null; // ignore this field for testing
        assertEquals(studentProfileAttributes.toString(), output.getStudentProfile().toString());
        assertEquals(student.getName(), output.getName());
    }

    private void testActionForbidden(AccountAttributes student1, AccountAttributes student2,
                                   String caseDescription) {
        loginAsStudent(student2.googleId);

        ______TS(caseDescription);
        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_ID, student1.googleId,
        };

        GetStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_FORBIDDEN, result.getStatusCode());
    }

    private void testActionInMasquerade(AccountAttributes student) {
        gaeSimulation.loginAsAdmin("admin.user");

        ______TS("Typical case: masquerade mode");
        StudentProfileAttributes student1InCourse1Profile = typicalBundle.profiles.get("student1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_ID, student.googleId,
                Const.ParamsNames.USER_ID, student.googleId,
        };

        GetStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        StudentProfile output = (StudentProfile) result.getOutput();
        output.getStudentProfile().modifiedDate = null; // ignore this field for testing
        assertEquals(student1InCourse1Profile.toString(), output.getStudentProfile().toString());
        assertEquals(student.getName(), output.getName());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }
}
