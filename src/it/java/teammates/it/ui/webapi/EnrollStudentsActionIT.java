package teammates.it.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.EnrollStudentsData;
import teammates.ui.request.StudentEnrollRequest;
import teammates.ui.request.StudentsEnrollRequest;
import teammates.ui.webapi.EnrollStudentsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link EnrollStudentsAction}.
 */

public class EnrollStudentsActionIT extends BaseActionIT<EnrollStudentsAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
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
    @Test
    public void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String courseId = typicalBundle.courses.get("course1").getId();
        Team team = typicalBundle.teams.get("team1InCourse1");
        StudentEnrollRequest enrollRequest = new StudentEnrollRequest(
                "Test Student", "test@email.com", team.getName(),
                team.getSection().getName(), "Test Comment");

        loginAsInstructor(instructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        List<Student> students = new ArrayList<>(logic.getStudentsForCourse(courseId));
        assertEquals(5, students.size());

        ______TS("Typical Success Case For Enrolling a Student");

        StudentsEnrollRequest request = prepareRequest(Arrays.asList(enrollRequest));
        EnrollStudentsAction enrollStudentsAction = getAction(request, params);
        JsonResult res = getJsonResult(enrollStudentsAction);
        EnrollStudentsData data = (EnrollStudentsData) res.getOutput();
        assertEquals(1, data.getStudentsData().getStudents().size());
        List<Student> studentsInCourse = logic.getStudentsForCourse(courseId);
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
        studentsInCourse = logic.getStudentsForCourse(courseId);
        assertEquals(6, studentsInCourse.size());

        // Verify that changes have cascaded to feedback responses
        List<FeedbackResponse> responsesFromUser =
                logic.getFeedbackResponsesFromGiverForCourse(courseId, giverEmail);

        for (FeedbackResponse response : responsesFromUser) {
            assertEquals(logic.getSection(courseId, "Section 3"), response.getGiverSection());
        }

        List<FeedbackResponse> responsesToUser =
                logic.getFeedbackResponsesForRecipientForCourse(courseId, giverEmail);

        for (FeedbackResponse response : responsesToUser) {
            assertEquals(logic.getSection(courseId, "Section 3"), response.getRecipientSection());
        }

        ______TS("Fail to enroll due to duplicate team name across sections");

        String expectedMessage = "Team \"Team Duplicate\" is detected in Sections \"Section 5\", \"Section 6\"."
                + " Please use different team names in different sections.";
        StudentEnrollRequest enrollRequest1 = new StudentEnrollRequest(
                "Test Student", "test-failure1@email.com", "Team Duplicate",
                "Section 5", "Test Comment");
        StudentEnrollRequest enrollRequest2 = new StudentEnrollRequest(
                "Test Student", "test-failure2@email.com", "Team Duplicate",
                "Section 6", "Test Comment");
        StudentsEnrollRequest req = prepareRequest(Arrays.asList(enrollRequest1, enrollRequest2));
        InvalidOperationException exception = verifyInvalidOperation(req, params);
        assertEquals(expectedMessage, exception.getMessage());
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
