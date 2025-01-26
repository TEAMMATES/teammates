package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteStudentAction;

/**
 * SUT: {@link DeleteStudentAction}.
 */
public class DeleteStudentActionTest extends BaseActionTest<DeleteStudentAction> {

    String googleId = "user-googleId";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    void testExecute_deleteStudentByEmail_success() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Student student = new Student(course, "Student Name", "studentEmail@gmail.tmt", "Some comments");

        when(mockLogic.getStudentByGoogleId(course.getId(), student.getGoogleId())).thenReturn(student);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_deleteStudentById_success() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Student student = new Student(course, "Student Name", "studentEmail@gmail.tmt", "Some comments");

        when(mockLogic.getStudentByGoogleId(course.getId(), student.getGoogleId())).thenReturn(student);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, student.getGoogleId(),
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_courseDoesNotExist_failSilently() {
        String studentId = "student-id";

        when(mockLogic.getStudentByGoogleId("RANDOM_COURSE", studentId)).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "RANDOM_COURSE",
                Const.ParamsNames.STUDENT_ID, studentId,
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_studentDoesNotExist_failSilently() {
        String courseId = "course-id";

        when(mockLogic.getStudentByGoogleId(courseId, "RANDOM_STUDENT")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_ID, "RANDOM_STUDENT",
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_missingStudentIdOrEmail_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "course-id",
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingCourseIdWithStudentId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.STUDENT_ID, "student-id",
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingCourseIdWithStudentEmail_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, "studentEmail@gmail.tmt",
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_randomEmail_failSilently() {
        String courseId = "course-id";

        when(mockLogic.getStudentByGoogleId(courseId, "RANDOM_EMAIL")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, "RANDOM_EMAIL",
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        Course course = new Course("course-id", "Course Name", Const.DEFAULT_TIME_ZONE, "institute");
        Student student = new Student(course, "Student Name", "studentEmail@gmail.tmt", "Some comments");

        loginAsAdmin();
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentByGoogleId(course.getId(), student.getGoogleId())).thenReturn(student);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, student.getGoogleId(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE, true);
        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt",
                false, "", null, instructorPrivileges);

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, "student-id",
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithInvalidPermission_cannotAccess() {
        Course course = new Course("course-id", "Course Name", Const.DEFAULT_TIME_ZONE, "institute");
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE, false);
        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt",
                false, "", null, instructorPrivileges);

        loginAsInstructor(googleId);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, "student-id",
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "course-id",
                Const.ParamsNames.STUDENT_ID, "student-id",
        };

        loginAsStudent(googleId);
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}
