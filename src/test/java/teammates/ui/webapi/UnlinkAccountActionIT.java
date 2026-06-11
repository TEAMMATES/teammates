package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link UnlinkAccountAction}.
 */
public class UnlinkAccountActionIT extends BaseActionIT<UnlinkAccountAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_UNLINK;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testExecute() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student = typicalBundle.students.get("student1InCourse1");

        loginAsAdmin();

        ______TS("Typical Success Case with Student user ID param given and Student exists");
        String[] params = new String[] {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        UnlinkAccountAction resetAccountAction = getAction(params);
        JsonResult actionOutput = getJsonResult(resetAccountAction);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Account unlinked successfully.", response.getMessage());
        Student updatedStudent = inTransaction(() -> logic.getStudent(student.getId()));
        assertNotNull(updatedStudent);
        assertNull(updatedStudent.getAccount());
        assertNull(updatedStudent.getGoogleId());

        ______TS("User ID param given but user is non existent");
        UUID invalidUserId = UUID.randomUUID();
        String[] invalidParams = new String[] {
                Const.ParamsNames.USER_ID, invalidUserId.toString(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(invalidParams);
        assertEquals(Const.ERROR_UPDATE_NON_EXISTENT + "User [id=" + invalidUserId + "]", enfe.getMessage());

        ______TS("Typical Success Case with Instructor user ID param given and Instructor exists");
        params = new String[] {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        resetAccountAction = getAction(params);
        actionOutput = getJsonResult(resetAccountAction);
        response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Account unlinked successfully.", response.getMessage());
        Instructor updatedInstructor = inTransaction(() -> logic.getInstructor(instructor.getId()));
        assertNotNull(updatedInstructor);
        assertNull(updatedInstructor.getAccount());
        assertNull(updatedInstructor.getGoogleId());

    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");

        verifyOnlyAdminCanAccess(course);
    }

}
