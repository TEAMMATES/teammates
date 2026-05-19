package teammates.ui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.EnrollResults;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.ui.output.EnrollStudentsData;
import teammates.ui.output.StudentData;
import teammates.ui.request.StudentEnrollRequest;
import teammates.ui.request.StudentsEnrollRequest;

/**
 * Test cases for the {@link EnrollStudentsAction} class.
 */
public class EnrollStudentsActionTest extends BaseActionTest<EnrollStudentsAction> {
    private Course course;
    private Team team;

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
        Section section = new Section("section");
        course.addSection(section);
        team = new Team(course.getId());
        section.addTeam(team);
    }

    @Test
    public void testExecute_withNewStudent_shouldBeAdded() throws Exception {
        Instructor instructor = getTypicalInstructor();
        // Ensure instructor has the required permissions
        instructor.getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        loginAsInstructor(instructor.getGoogleId());

        StudentEnrollRequest studentToEnroll = new StudentEnrollRequest("name", "email.com", "team", "section", "");

        StudentsEnrollRequest req = prepareRequest(List.of(studentToEnroll));
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        Student dummyStudent = new Student(course, "name", "email.com", "");
        dummyStudent.setTeam(team);

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.enrollStudents(any(), any()))
                .thenReturn(new EnrollResults(List.of(dummyStudent), Map.of()));

        EnrollStudentsAction action = getAction(req, params);
        JsonResult result = getJsonResult(action);

        List<StudentData> enrolledStudents = ((EnrollStudentsData) result.getOutput()).getStudentsData().getStudents();
        assertEquals(1, enrolledStudents.size());
        assertEquals(enrolledStudents.get(0).getEmail(), dummyStudent.getEmail());
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

        when(mockLogic.getInstructorByGoogleId(course.getId(), "random-id")).thenReturn(null);
        loginAsStudent("random-id");
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }

    private StudentsEnrollRequest prepareRequest(List<StudentEnrollRequest> studentsToEnroll) {
        return new StudentsEnrollRequest(studentsToEnroll);
    }

}
