package teammates.it.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.ui.output.EnrollStudentsData;
import teammates.ui.request.StudentsEnrollRequest;
import teammates.ui.webapi.EnrollStudentsAction;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link EnrollStudentsAction}.
 */

public class EnrollStudentsActionIT extends BaseActionIT<EnrollStudentsAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
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

    private StudentsEnrollRequest prepareRequest(List<Student> students) {
        List<StudentsEnrollRequest.StudentEnrollRequest> studentEnrollRequests = new ArrayList<>();
        students.forEach(student -> {
            studentEnrollRequests.add(new StudentsEnrollRequest.StudentEnrollRequest(student.getName(),
                    student.getEmail(), student.getTeam().getName(), student.getSection().getName(), student.getComments()));
        });

        return new StudentsEnrollRequest(studentEnrollRequests);
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String courseId = typicalBundle.students.get("student1InCourse1").getCourseId();
        Course course = logic.getCourse(courseId);
        Section section = logic.getSection(courseId, "Section 1");
        Team team = logic.getTeamOrCreate(section, "Team 1");
        Student newStudent = new Student(course, "Test Student", "test@email.com", "Test Comment", team);

        loginAsInstructor(instructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        List<Student> students = new ArrayList<>(logic.getStudentsForCourse(courseId));
        assertEquals(5, students.size());

        ______TS("Typical Success Case For Enrolling a Student");

        StudentsEnrollRequest request = prepareRequest(Arrays.asList(newStudent));
        EnrollStudentsAction enrollStudentsAction = getAction(request, params);
        JsonResult res = getJsonResult(enrollStudentsAction);
        EnrollStudentsData data = (EnrollStudentsData) res.getOutput();
        assertEquals(1, data.getStudentsData().getStudents().size());
        List<Student> studentsInCourse = logic.getStudentsForCourse(courseId);
        assertEquals(6, studentsInCourse.size());

        ______TS("Fail to enroll due to duplicate team name across sections");

        String expectedMessage = "Team \"%s\" is detected in both Section \"%s\" and Section \"%s\"."
                + " Please use different team names in different sections.";
        Section newSection = logic.getSection(courseId, "Section 3");
        Team newTeam = new Team(newSection, "Team 1");
        newStudent = new Student(course, "Test Student", "test@email.com", "Test Comment", newTeam);
        Student secondStudent = new Student(course, "Test Student 2", "test2@email.com", "Test Comment",
                team);
        StudentsEnrollRequest req = prepareRequest(Arrays.asList(secondStudent, newStudent));
        InvalidOperationException exception = verifyInvalidOperation(req, params);
        assertEquals(String.format(expectedMessage, "Team 1", "Section 3", "Section 1"), exception.getMessage());

        ______TS("Typical Success Case For Changing Details (except email) of a Student");

        Section section3 = logic.getSection(courseId, "Section 3");
        Team team3 = logic.getTeamOrCreate(section3, "Team 3");

        Student changedTeam = new Student(course, "Student 1", "student1@teammates.tmt", "Test Comment", team3);

        request = prepareRequest(Arrays.asList(changedTeam));
        enrollStudentsAction = getAction(request, params);
        res = getJsonResult(enrollStudentsAction);
        data = (EnrollStudentsData) res.getOutput();
        assertEquals(1, data.getStudentsData().getStudents().size());
        studentsInCourse = logic.getStudentsForCourse(courseId);
        assertEquals(6, studentsInCourse.size());

        // Verify that changes have cascaded to feedback responses
        String giverEmail = "student1@teammates.tmt";

        List<FeedbackResponse> responsesFromUser =
                logic.getFeedbackResponsesFromGiverForCourse(courseId, giverEmail);

        for (FeedbackResponse response : responsesFromUser) {
            assertEquals(logic.getSection(courseId, "Section 3"), response.getGiverSection());
        }

        List<FeedbackResponse> responsesToUser =
                logic.getFeedbackResponsesForRecipientForCourse(courseId, giverEmail);

        for (FeedbackResponse response : responsesToUser) {
            assertEquals(logic.getSection(courseId, "Section 3"), response.getRecipientSection());
            List<FeedbackResponseComment> commentsFromUser = logic.getFeedbackResponseCommentsForResponse(response.getId());
            for (FeedbackResponseComment comment : commentsFromUser) {
                if (comment.getGiver().equals(giverEmail)) {
                    assertEquals(logic.getSection(courseId, "Section 3"), comment.getGiverSection());
                }
            }
        }
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
