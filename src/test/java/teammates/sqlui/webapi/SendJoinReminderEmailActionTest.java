package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.SendJoinReminderEmailAction;

/**
 * SUT: {@link SendJoinReminderEmailAction}.
 */
public class SendJoinReminderEmailActionTest
        extends BaseActionTest<SendJoinReminderEmailAction> {

    private Course course;
    private Student student;
    private Instructor instructor;
    private String instructorGoogleId;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN_REMIND;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        student = new Student(course, "student name", "student_email@tm.tmt", null);
        instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);

        instructorGoogleId = "user-id";
        loginAsInstructor(instructorGoogleId);
    }

    @Test
    public void testExecute_sendToStudent_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, null,
        };

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail())).thenReturn(student);

        SendJoinReminderEmailAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals(
                "An email has been sent to " + student.getEmail(),
                actionOutput.getMessage());

        verifySpecifiedTasksAdded(Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    public void testExecute_sendToInstructor_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, null,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);

        SendJoinReminderEmailAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals(
                "An email has been sent to " + instructor.getEmail(),
                actionOutput.getMessage());

        verifySpecifiedTasksAdded(Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    public void testExecute_sendToAllUnregisteredStudents_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, null,
                Const.ParamsNames.INSTRUCTOR_EMAIL, null,
        };

        List<Student> unregisteredStudents = List.of(student);

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getUnregisteredStudentsForCourse(course.getId())).thenReturn(unregisteredStudents);

        SendJoinReminderEmailAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals(
                "Emails have been sent to unregistered students.",
                actionOutput.getMessage());

        verifySpecifiedTasksAdded(Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, unregisteredStudents.size());
    }

    @Test
    public void testSpecificAccessControl_sendToStudentInstructorCanModifyStudent_canAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        InstructorPrivileges canModifyStudentPrivileges = new InstructorPrivileges();
        canModifyStudentPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);

        instructor.setPrivileges(canModifyStudentPrivileges);

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorGoogleId)).thenReturn(instructor);

        verifyCanAccess(params);
    }

    @Test
    public void testSpecificAccessControl_sendToStudentInstructorCannotModifyStudent_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        InstructorPrivileges cannotModifyStudentPrivileges = new InstructorPrivileges();
        cannotModifyStudentPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, false);

        instructor.setPrivileges(cannotModifyStudentPrivileges);

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorGoogleId)).thenReturn(instructor);

        verifyCannotAccess(params);
    }

    @Test
    public void testSpecificAccessControl_sendToAllUnregisteredStudentsInstructorCanModifyStudent_canAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        InstructorPrivileges canModifyStudentPrivileges = new InstructorPrivileges();
        canModifyStudentPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);

        instructor.setPrivileges(canModifyStudentPrivileges);

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorGoogleId)).thenReturn(instructor);

        verifyCanAccess(params);
    }

    @Test
    public void testSpecificAccessControl_sendToAllUnregisteredStudentsInstructorCannotModifyStudent_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        InstructorPrivileges cannotModifyStudentPrivileges = new InstructorPrivileges();
        cannotModifyStudentPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, false);

        instructor.setPrivileges(cannotModifyStudentPrivileges);

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorGoogleId)).thenReturn(instructor);

        verifyCannotAccess(params);
    }

    @Test
    public void testSpecificAccessControl_sendToInstructorInstructorCanModifyInstructor_canAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };

        InstructorPrivileges canModifyInstructorPrivileges = new InstructorPrivileges();
        canModifyInstructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);

        instructor.setPrivileges(canModifyInstructorPrivileges);

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorGoogleId)).thenReturn(instructor);

        verifyCanAccess(params);
    }

    @Test
    public void testSpecificAccessControl_sendToInstructorInstructorCannotModifyInstructor_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };

        InstructorPrivileges cannotModifyInstructorPrivileges = new InstructorPrivileges();
        cannotModifyInstructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);

        instructor.setPrivileges(cannotModifyInstructorPrivileges);

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorGoogleId)).thenReturn(instructor);

        verifyCannotAccess(params);
    }
}
