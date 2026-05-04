package teammates.ui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.entity.Course;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Section;
import teammates.logic.entity.Student;
import teammates.logic.entity.Team;
import teammates.ui.output.EnrollStudentsData;
import teammates.ui.output.StudentData;
import teammates.ui.request.StudentsEnrollRequest;

/**
 * Test cases for the {@link EnrollStudentsAction} class.
 */
public class EnrollStudentsActionTest extends BaseActionTest<EnrollStudentsAction> {
    private Course course;
    private Team team;
    private Section section;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        section = new Section(course, "section");
        team = new Team(section, course.getId());
    }

    @Test
    public void testExecute_withNewStudent_shouldBeAdded() throws Exception {
        Instructor instructor = getTypicalInstructor();
        // Ensure instructor has the required permissions
        instructor.getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        loginAsInstructor(instructor.getGoogleId());
        Student newStudent = new Student(course, "name", "email.com", "", team);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentsForCourse(course.getId())).thenReturn(new ArrayList<>());
        when(mockLogic.getStudentForEmail(course.getId(), newStudent.getEmail())).thenReturn(null);
        when(mockLogic.getInstructorForEmail(course.getId(), newStudent.getEmail())).thenReturn(null);
        when(mockLogic.createStudent(
                argThat(argument -> Objects.equals(argument.getName(), newStudent.getName())
                        && Objects.equals(argument.getEmail(), newStudent.getEmail())
                        && Objects.equals(argument.getTeam(), newStudent.getTeam())
                        && Objects.equals(argument.getSection(), newStudent.getSection())))).thenReturn(newStudent);
        when(mockLogic.getTeamOrCreate(section, "team")).thenReturn(team);
        when(mockLogic.getSectionOrCreate(course.getId(), "section")).thenReturn(section);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);

        StudentsEnrollRequest req = prepareRequest(newStudent);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        EnrollStudentsAction action = getAction(req, params);
        JsonResult result = getJsonResult(action);

        List<StudentData> enrolledStudents = ((EnrollStudentsData) result.getOutput()).getStudentsData().getStudents();
        assertEquals(1, enrolledStudents.size());
        assertEquals(enrolledStudents.get(0).getEmail(), newStudent.getEmail());
    }

    @Test
    public void testExecute_studentAlreadyEnrolled_updateStudent() throws Exception {
        Instructor instructor = getTypicalInstructor();
        // Ensure instructor has the required permissions
        instructor.getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        loginAsInstructor(instructor.getGoogleId());
        Student newStudent = new Student(course, "name", "email.com", "", team);
        Student existingStudent = new Student(course, "oldName", "email.com", "", team);
        when(mockLogic.getStudentsForCourse(course.getId())).thenReturn(new ArrayList<>(List.of(existingStudent)));
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentForEmail(course.getId(), newStudent.getEmail())).thenReturn(existingStudent);
        when(mockLogic.getInstructorForEmail(course.getId(), newStudent.getEmail())).thenReturn(null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getTeamOrCreate(section, "team")).thenReturn(team);
        when(mockLogic.getSectionOrCreate(course.getId(), "section")).thenReturn(section);
        when(mockLogic.updateStudentCascade(
                argThat(argument -> argument != null
                        && Objects.equals(argument.getName(), newStudent.getName())
                        && Objects.equals(argument.getEmail(), newStudent.getEmail())
                        && Objects.equals(argument.getTeam(), newStudent.getTeam())
                        && Objects.equals(argument.getSection(), newStudent.getSection())))).thenReturn(newStudent);
        StudentsEnrollRequest req = prepareRequest(newStudent);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        EnrollStudentsAction action = getAction(req, params);
        JsonResult result = getJsonResult(action);

        List<StudentData> enrolledStudents = ((EnrollStudentsData) result.getOutput()).getStudentsData().getStudents();
        assertEquals(enrolledStudents.size(), 1);
        assertEquals(enrolledStudents.get(0).getName(), newStudent.getName());
    }

    @Test
    public void testExecute_studentAlreadyEnrolled_caseInsensitiveEmail() throws Exception {
        Instructor instructor = getTypicalInstructor();
        instructor.getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        loginAsInstructor(instructor.getGoogleId());

        Student existingStudent = new Student(course, "oldName", "email.com", "", team);
        Student requestStudent = new Student(course, "name", "Email.Com", "", team);
        Student updatedStudent = new Student(course, "name", "email.com", "", team);

        when(mockLogic.getStudentsForCourse(course.getId())).thenReturn(new ArrayList<>(List.of(existingStudent)));
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentForEmail(course.getId(), "email.com")).thenReturn(existingStudent);
        when(mockLogic.getInstructorForEmail(course.getId(), "email.com")).thenReturn(null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getTeamOrCreate(section, "team")).thenReturn(team);
        when(mockLogic.getSectionOrCreate(course.getId(), "section")).thenReturn(section);
        when(mockLogic.updateStudentCascade(
                argThat(argument -> argument != null
                        && Objects.equals(argument.getName(), requestStudent.getName())
                        && Objects.equals(argument.getEmail(), "email.com")
                        && Objects.equals(argument.getTeam(), requestStudent.getTeam())
                        && Objects.equals(argument.getSection(), requestStudent.getSection())))).thenReturn(updatedStudent);

        StudentsEnrollRequest req = prepareRequest(requestStudent);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        EnrollStudentsAction action = getAction(req, params);
        JsonResult result = getJsonResult(action);

        List<StudentData> enrolledStudents = ((EnrollStudentsData) result.getOutput()).getStudentsData().getStudents();
        assertEquals(enrolledStudents.size(), 1);
        assertEquals(enrolledStudents.get(0).getEmail(), "email.com");
    }

    @Test
    public void testExecute_invalidParamsAndNotAlreadyEnrolled_studentAddedToErrorList() throws
            EntityAlreadyExistsException, InvalidParametersException {
        doThrow(new InvalidParametersException("")).when(mockLogic).createStudent(any(Student.class));

        Instructor instructor = getTypicalInstructor();
        // Ensure instructor has the required permissions
        instructor.getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        loginAsInstructor(instructor.getGoogleId());
        Student newStudent = new Student(course, "name", "email.com", "", team);
        when(mockLogic.getStudentsForCourse(course.getId())).thenReturn(new ArrayList<>());
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentForEmail(course.getId(), newStudent.getEmail())).thenReturn(null);
        when(mockLogic.getInstructorForEmail(course.getId(), newStudent.getEmail())).thenReturn(null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getTeamOrCreate(section, "team")).thenReturn(team);
        when(mockLogic.getSectionOrCreate(course.getId(), "section")).thenReturn(section);

        StudentsEnrollRequest req = prepareRequest(newStudent);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        EnrollStudentsAction action = getAction(req, params);
        JsonResult result = getJsonResult(action);
        List<StudentData> enrolledStudents = ((EnrollStudentsData) result.getOutput()).getStudentsData().getStudents();
        List<EnrollStudentsData.EnrollErrorResults> errors =
                ((EnrollStudentsData) result.getOutput()).getUnsuccessfulEnrolls();
        assertEquals(errors.size(), 1);
        assertEquals(enrolledStudents.size(), 0);
    }

    @Test
    public void testExecute_invalidParamsAndAlreadyEnrolled_studentAddedToErrorList()
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Instructor instructor = getTypicalInstructor();
        // Ensure instructor has the required permissions
        instructor.getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        loginAsInstructor(instructor.getGoogleId());

        doThrow(new InvalidParametersException("")).when(mockLogic).updateStudentCascade(any(Student.class));

        Student newStudent = new Student(course, "name", "email.com", "", team);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentsForCourse(course.getId())).thenReturn(new ArrayList<>(List.of(newStudent)));
        when(mockLogic.getStudentForEmail(course.getId(), newStudent.getEmail())).thenReturn(newStudent);
        when(mockLogic.getInstructorForEmail(course.getId(), newStudent.getEmail())).thenReturn(null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getTeamOrCreate(section, "team")).thenReturn(team);
        when(mockLogic.getSectionOrCreate(course.getId(), "section")).thenReturn(section);

        StudentsEnrollRequest req = prepareRequest(newStudent);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        EnrollStudentsAction action = getAction(req, params);
        JsonResult result = getJsonResult(action);
        List<StudentData> enrolledStudents = ((EnrollStudentsData) result.getOutput()).getStudentsData().getStudents();
        List<EnrollStudentsData.EnrollErrorResults> errors =
                ((EnrollStudentsData) result.getOutput()).getUnsuccessfulEnrolls();
        assertEquals(errors.size(), 1);
        assertEquals(enrolledStudents.size(), 0);
    }

    @Test
    public void testExecute_invalidCourseId_invalidHttpRequestBodyException() {
        Instructor instructor = getTypicalInstructor();
        loginAsInstructor(instructor.getGoogleId());
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, null,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testAccessControl() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyAccessibleWithCorrectSameCoursePrivilege(course, Const.InstructorPermissions.CAN_MODIFY_STUDENT, params);
        verifyInaccessibleWithoutCorrectSameCoursePrivilege(course, Const.InstructorPermissions.CAN_MODIFY_STUDENT, params);
    }

    @Test
    public void testSpecificAccessControl_instructorWithInvalidPermission_cannotAccess() {
        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt",
                false, "", null, new InstructorPrivileges());
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        loginAsInstructor(instructor.getGoogleId());
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyCannotAccess(params);
    }

    @Test
    public void testSpecificAccessControl_instructorWithValidPermission_canAccess() {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt",
                false, "", null, instructorPrivileges);
        loginAsInstructor(instructor.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        verifyCanAccess(params);
    }

    @Test
    public void testSpecificAccessControl_notInstructor_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        loginAsStudent("random-id");
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }

    private StudentsEnrollRequest prepareRequest(Student... studentsToEnroll) {
        List<StudentsEnrollRequest.StudentEnrollRequest> requestList = new ArrayList<>();
        Arrays.stream(studentsToEnroll).forEach(student -> requestList.add(
                new StudentsEnrollRequest.StudentEnrollRequest(student.getName(), student.getEmail(),
                "team", "section", student.getComments())));
        return new StudentsEnrollRequest(requestList);
    }

}
