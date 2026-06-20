package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.test.GroupNames;
import teammates.ui.output.EnrollStudentsData;
import teammates.ui.request.StudentEnrollRequest;
import teammates.ui.request.StudentsEnrollRequest;

/**
 * SUT: {@link EnrollStudentsAction}.
 */

public class EnrollStudentsActionIT extends BaseActionIT<EnrollStudentsAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    private StudentsEnrollRequest prepareRequest(List<StudentEnrollRequest> studentEnrollRequests) {
        return new StudentsEnrollRequest(studentEnrollRequests);
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String courseId = typicalBundle.courses.get("course1").getId();
        Team team = typicalBundle.teams.get("team1InCourse1");
        StudentEnrollRequest enrollRequest = new StudentEnrollRequest(
                "Test Student", "test@email.com", team.getName(),
                team.getSection().getName(), "Test Comment");

        loginAsInstructor(instructor);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        List<Student> students = inTransaction(() -> new ArrayList<>(logic.getStudentsForCourse(courseId)));
        assertEquals(5, students.size());

        ______TS("Typical Success Case For Enrolling a Student");

        StudentsEnrollRequest request = prepareRequest(Arrays.asList(enrollRequest));
        EnrollStudentsAction enrollStudentsAction = getAction(request, params);
        JsonResult res = getJsonResult(enrollStudentsAction);
        EnrollStudentsData data = (EnrollStudentsData) res.getOutput();
        assertEquals(1, data.getStudentsData().getStudents().size());
        List<Student> studentsInCourse = inTransaction(() -> logic.getStudentsForCourse(courseId));
        assertEquals(6, studentsInCourse.size());

        ______TS("Typical Success Case For Changing Details of a Student");

        Team team2 = typicalBundle.teams.get("team3InCourse1");
        String giverEmail = "student1@teammates.tmt";
        StudentEnrollRequest enrollRequestNewTeam = new StudentEnrollRequest(
                "New Student", giverEmail, team2.getName(),
                team2.getSection().getName(), "New Comment");

        request = prepareRequest(Arrays.asList(enrollRequestNewTeam));
        enrollStudentsAction = getAction(request, params);
        res = getJsonResult(enrollStudentsAction);
        data = (EnrollStudentsData) res.getOutput();
        assertEquals(1, data.getStudentsData().getStudents().size());
        studentsInCourse = inTransaction(() -> logic.getStudentsForCourse(courseId));
        assertEquals(6, studentsInCourse.size());
    }

    @Test(groups = GroupNames.INTEGRATION)
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
