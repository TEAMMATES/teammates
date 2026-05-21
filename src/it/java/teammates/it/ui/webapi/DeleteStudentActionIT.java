package teammates.it.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.webapi.DeleteStudentAction;

/**
 * SUT: {@link DeleteStudentAction}.
 */
public class DeleteStudentActionIT extends BaseActionIT<DeleteStudentAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    String getRequestMethod() {
        return DELETE;
    }

    @Test
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

        assertNull(logic.getStudent(student1InCourse1.getId()));

        ______TS("Typical Success Case delete another student by user id");
        params = new String[] {
                Const.ParamsNames.USER_ID, student2InCourse1.getId().toString(),
        };

        deleteStudentAction = getAction(params);
        getJsonResult(deleteStudentAction);

        assertNull(logic.getStudent(student2InCourse1.getId()));

        ______TS("Student does not exist, access control fails");
        UUID nonExistentStudentId = UUID.randomUUID();
        params = new String[] {
                Const.ParamsNames.USER_ID, nonExistentStudentId.toString(),
        };

        verifyEntityNotFoundAcl(params);
        assertNotNull(logic.getStudent(student3InCourse1.getId()));

        ______TS("Incomplete params given");
        verifyHttpParameterFailure();

        params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyHttpParameterFailure(params);

        params = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyHttpParameterFailure(params);

        params = new String[] {
                Const.ParamsNames.STUDENT_ID, student1InCourse1.getGoogleId(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
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
