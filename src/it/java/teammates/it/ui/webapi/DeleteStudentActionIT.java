package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.webapi.DeleteStudentAction;

/**
 * SUT: {@link DeleteStudentAction}.
 */
public class DeleteStudentActionIT extends BaseActionIT<DeleteStudentAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
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

        ______TS("Typical Success Case delete a student by email");
        loginAsInstructor(instructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        DeleteStudentAction deleteStudentAction = getAction(params);
        getJsonResult(deleteStudentAction);

        assertNull(logic.getStudentForEmail(courseId, student1InCourse1.getEmail()));

        ______TS("Typical Success Case delete a student by id");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_ID, student2InCourse1.getGoogleId(),
        };

        deleteStudentAction = getAction(params);
        getJsonResult(deleteStudentAction);

        assertNull(logic.getStudentByGoogleId(courseId, student2InCourse1.getGoogleId()));

        ______TS("Course does not exist, fails silently");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, "non-existent-course",
                Const.ParamsNames.STUDENT_ID, student3InCourse1.getGoogleId(),
        };

        deleteStudentAction = getAction(params);
        getJsonResult(deleteStudentAction);

        assertNotNull(logic.getStudentByGoogleId(student3InCourse1.getCourseId(), student3InCourse1.getGoogleId()));

        ______TS("Student does not exist, fails silently");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_ID, "non-existent-id",
        };

        deleteStudentAction = getAction(params);
        getJsonResult(deleteStudentAction);

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

        verifyAccessibleForAdmin(params);

        ______TS("Random email given, fails silently");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, "random-email",
        };

        deleteStudentAction = getAction(params);
        getJsonResult(deleteStudentAction);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student = typicalBundle.students.get("student1InCourse1");
        Course course = typicalBundle.courses.get("course1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyAccessibleForAdmin(params);
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(course,
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, params);
    }

}
