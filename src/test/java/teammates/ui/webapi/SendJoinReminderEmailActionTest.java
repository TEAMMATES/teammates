package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.MessageOutput;

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
        Mockito.reset(mockLogic, mockEmailGenerator);

        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        student = new Student(course, "student name", "student_email@tm.tmt", null);
        instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);

        instructorGoogleId = "user-id";
        loginAsInstructor(instructorGoogleId);
    }

    @Test
    public void testExecute_sendToStudent_success() {
        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        when(mockLogic.getUser(student.getId())).thenReturn(student);
        when(mockEmailGenerator.generateStudentCourseJoinEmail(course, student)).thenReturn(mock(EmailWrapper.class));

        SendJoinReminderEmailAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals(
                "An email has been sent to " + student.getEmail(),
                actionOutput.getMessage());

        verifySpecifiedTasksAdded(Const.TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    public void testExecute_sendToInstructor_success() {
        String[] params = {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        when(mockLogic.getUser(instructor.getId())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorGoogleId)).thenReturn(instructor);
        when(mockEmailGenerator.generateInstructorCourseJoinEmail(any(Instructor.class), eq(instructor), eq(course)))
                .thenReturn(mock(EmailWrapper.class));

        SendJoinReminderEmailAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals(
                "An email has been sent to " + instructor.getEmail(),
                actionOutput.getMessage());

        verifySpecifiedTasksAdded(Const.TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    public void testExecute_sendToAllUnregisteredStudents_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        List<Student> unregisteredStudents = List.of(student);

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getUnregisteredStudentsForCourse(course.getId())).thenReturn(unregisteredStudents);
        when(mockEmailGenerator.generateStudentCourseJoinEmail(course, student)).thenReturn(mock(EmailWrapper.class));

        SendJoinReminderEmailAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals(
                "Emails have been sent to unregistered students.",
                actionOutput.getMessage());

        verifySpecifiedTasksAdded(Const.TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, unregisteredStudents.size());
    }

    @Test
    public void testSpecificAccessControl_sendToStudentInstructorCanModifyStudent_canAccess() {
        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        InstructorPrivileges canModifyStudentPrivileges = new InstructorPrivileges();
        canModifyStudentPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);

        instructor.setPrivileges(canModifyStudentPrivileges);

        when(mockLogic.getUser(student.getId())).thenReturn(student);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorGoogleId)).thenReturn(instructor);

        verifyCanAccess(params);
    }

    @Test
    public void testSpecificAccessControl_sendToStudentInstructorCannotModifyStudent_cannotAccess() {
        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        InstructorPrivileges cannotModifyStudentPrivileges = new InstructorPrivileges();
        cannotModifyStudentPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, false);

        instructor.setPrivileges(cannotModifyStudentPrivileges);

        when(mockLogic.getUser(student.getId())).thenReturn(student);
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
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        InstructorPrivileges canModifyInstructorPrivileges = new InstructorPrivileges();
        canModifyInstructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);

        instructor.setPrivileges(canModifyInstructorPrivileges);

        when(mockLogic.getUser(instructor.getId())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorGoogleId)).thenReturn(instructor);

        verifyCanAccess(params);
    }

    @Test
    public void testSpecificAccessControl_sendToInstructorInstructorCannotModifyInstructor_cannotAccess() {
        String[] params = {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        InstructorPrivileges cannotModifyInstructorPrivileges = new InstructorPrivileges();
        cannotModifyInstructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);

        instructor.setPrivileges(cannotModifyInstructorPrivileges);

        when(mockLogic.getUser(instructor.getId())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorGoogleId)).thenReturn(instructor);

        verifyCannotAccess(params);
    }
}
