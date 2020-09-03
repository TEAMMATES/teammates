package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DowngradeAccountAction}.
 */
public class DowngradeAccountActionTest extends BaseActionTest<DowngradeAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_DOWNGRADE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        loginAsAdmin();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case");

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1ofCourse1.getGoogleId(),
        };

        DowngradeAccountAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        MessageOutput response = (MessageOutput) r.getOutput();

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        assertEquals("Instructor account is successfully downgraded to student.", response.getMessage());
        assertFalse(logic.getAccount(instructor1ofCourse1.getGoogleId()).isInstructor);

        ______TS("Failure: Downgrades an invalid account");

        String[] invalidParams = {
                Const.ParamsNames.INSTRUCTOR_ID, "invalid-google-id",
        };

        assertThrows(EntityNotFoundException.class, () -> getJsonResult(getAction(invalidParams)));

        ______TS("Failure: Tries to downgrade a student account");

        String[] paramsStudent = {
                Const.ParamsNames.INSTRUCTOR_ID, student1InCourse1.getId(),
        };

        assertThrows(EntityNotFoundException.class, () -> getJsonResult(getAction(paramsStudent)));
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
