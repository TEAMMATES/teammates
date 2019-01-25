package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.ui.newcontroller.GetStudentProfileAction;
import teammates.ui.newcontroller.GetStudentProfileAction.StudentProfile;
import teammates.ui.newcontroller.JsonResult;

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
        AccountAttributes student = typicalBundle.accounts.get("student1InCourse1");
        StudentProfileAttributes student1InCourse1Profile = typicalBundle.profiles.get("student1InCourse1");
        testActionSuccess(student, student1InCourse1Profile, "Typical case");

        testActionInMasquerade(student);

        student = typicalBundle.accounts.get("student1InTestingSanitizationCourse");
        StudentProfileAttributes student1InTestingSanitizationCourseProfile =
                typicalBundle.profiles.get("student1InCourse1");
        // simulate sanitization that occurs before persistence
        student.sanitizeForSaving();
        testActionSuccess(student, student1InTestingSanitizationCourseProfile,
                "Typical case: attempted script injection");
    }

    private void testActionSuccess(AccountAttributes student, StudentProfileAttributes studentProfileAttributes,
                                   String caseDescription) {
        loginAsStudent(student.googleId);

        ______TS(caseDescription);

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_ID, student.googleId
        };
        GetStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        StudentProfile output = (StudentProfile) result.getOutput();
        output.getStudentProfile().modifiedDate = null; // ignore this field for testing
        assertEquals(studentProfileAttributes.toString(), output.getStudentProfile().toString());
    }

    private void testActionInMasquerade(AccountAttributes student) {
        gaeSimulation.loginAsAdmin("admin.user");

        ______TS("Typical case: masquerade mode");
        StudentProfileAttributes student1InCourse1Profile = typicalBundle.profiles.get("student1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_ID, student.googleId
        };

        GetStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        StudentProfile output = (StudentProfile) result.getOutput();
        output.getStudentProfile().modifiedDate = null; // ignore this field for testing
        assertEquals(student1InCourse1Profile.toString(), output.getStudentProfile().toString());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyInaccessibleWithoutLogin(submissionParams);

        verifyInaccessibleForUnregisteredUsers(submissionParams);
    }

}
