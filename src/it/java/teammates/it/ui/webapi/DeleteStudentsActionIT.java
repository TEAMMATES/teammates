package teammates.it.ui.webapi;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.webapi.DeleteStudentsAction;

/**
 * SUT: {@link DeleteStudentsAction}.
 */
public class DeleteStudentsActionIT extends BaseActionIT<DeleteStudentsAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    String getRequestMethod() {
        return DELETE;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String courseId = instructor.getCourseId();
        // TODO Remove limit after migration completes
        int deleteLimit = 4;

        ______TS("Typical Success Case delete a limited number of students");
        loginAsInstructor(instructor.getGoogleId());

        List<Student> studentsToDelete = logic.getStudentsForCourse(courseId);

        assertEquals(5, studentsToDelete.size());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.LIMIT, String.valueOf(deleteLimit),
        };

        DeleteStudentsAction deleteStudentsAction = getAction(params);
        getJsonResult(deleteStudentsAction);

        for (Student student : studentsToDelete) {
            assertNull(logic.getStudentByRegistrationKey(student.getRegKey()));
        }

        ______TS("Random course given, fails silently");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, "non-existent-course-id",
                Const.ParamsNames.LIMIT, String.valueOf(deleteLimit),
        };

        deleteStudentsAction = getAction(params);
        getJsonResult(deleteStudentsAction);

        ______TS("Invalid params");
        verifyHttpParameterFailure();
    }

    @Test
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
