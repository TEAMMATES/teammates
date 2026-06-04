package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.TestGroups;

/**
 * SUT: {@link DeleteStudentsAction}.
 */
public class DeleteStudentsActionIT extends BaseActionIT<DeleteStudentsAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    String getRequestMethod() {
        return DELETE;
    }

    @Test(groups = TestGroups.INTEGRATION)
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String courseId = instructor.getCourseId();

        ______TS("Typical Success Case delete all students in the course");
        loginAsInstructor(instructor.getGoogleId());

        List<Student> studentsToDelete = inTransaction(() -> logic.getStudentsForCourse(courseId));

        assertEquals(5, studentsToDelete.size());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        DeleteStudentsAction deleteStudentsAction = getAction(params);
        getJsonResult(deleteStudentsAction);

        for (Student student : studentsToDelete) {
            assertNull(inTransaction(() -> logic.getStudentByRegistrationKey(student.getRegKey())));
        }

        ______TS("Random course given, fails silently");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, "non-existent-course-id",
        };

        deleteStudentsAction = getAction(params);
        getJsonResult(deleteStudentsAction);

        ______TS("Invalid params");
        verifyHttpParameterFailure();
    }

    @Test(groups = TestGroups.INTEGRATION)
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                course, Const.InstructorPermissions.CAN_MODIFY_STUDENT, params);
    }

}
