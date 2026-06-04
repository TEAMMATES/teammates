package teammates.ui.webapi;

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

/**
 * SUT: {@link DeleteStudentAction}.
 */
public class DeleteStudentActionIT extends BaseActionIT<DeleteStudentAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    String getRequestMethod() {
        return DELETE;
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        Student student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        Student student3InCourse1 = typicalBundle.students.get("student3InCourse1");
        String courseId = instructor.getCourseId();

        ______TS("Typical Success Case delete a student by user id");
        loginAsInstructor(instructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.USER_ID, student1InCourse1.getId().toString(),
        };

        DeleteStudentAction deleteStudentAction = getAction(params);
        getJsonResult(deleteStudentAction);

        assertNull(inTransaction(() -> logic.getStudent(student1InCourse1.getId())));

        ______TS("Typical Success Case delete another student by user id");
        params = new String[] {
                Const.ParamsNames.USER_ID, student2InCourse1.getId().toString(),
        };

        deleteStudentAction = getAction(params);
        getJsonResult(deleteStudentAction);

        assertNull(inTransaction(() -> logic.getStudent(student2InCourse1.getId())));

        ______TS("Student does not exist, access control fails");
        UUID nonExistentStudentId = UUID.randomUUID();
        params = new String[] {
                Const.ParamsNames.USER_ID, nonExistentStudentId.toString(),
        };

        verifyEntityNotFoundAcl(params);
        assertNotNull(inTransaction(() -> logic.getStudent(student3InCourse1.getId())));

        ______TS("Incomplete params given");
        verifyHttpParameterFailure();

        params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyHttpParameterFailure(params);
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testAccessControl() throws Exception {
        Student student = typicalBundle.students.get("student1InCourse1");
        Course course = typicalBundle.courses.get("course1");

        String[] params = new String[] {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        verifyAccessibleForAdmin(params);
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(course,
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, params);
    }

}
