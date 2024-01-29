package teammates.it.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.EnrollStudentsData;
import teammates.ui.request.StudentsEnrollRequest;
import teammates.ui.webapi.EnrollStudentsAction;
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

    private StudentsEnrollRequest prepareRequest(List<StudentAttributes> students) {
        List<StudentsEnrollRequest.StudentEnrollRequest> studentEnrollRequests = new ArrayList<>();
        students.forEach(student -> {
            studentEnrollRequests.add(new StudentsEnrollRequest.StudentEnrollRequest(student.getName(),
                    student.getEmail(), student.getTeam(), student.getSection(), student.getComments()));
        });

        return new StudentsEnrollRequest(studentEnrollRequests);
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String courseId = typicalBundle.students.get("student1InCourse1").getCourseId();
        StudentAttributes newStudent = StudentAttributes.builder(courseId, "test@email.com")
                .withName("Test Student")
                .withTeamName("Team 1")
                .withSectionName("Section 1")
                .withComment("Test Comment")
                .build();

        loginAsInstructor(instructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        List<Student> students = logic.getStudentsForCourse(courseId);
        assertEquals(3, students.size());

        ______TS("Typical Success Case For Enrolling a Student");

        StudentsEnrollRequest request = prepareRequest(Arrays.asList(newStudent));
        EnrollStudentsAction enrollStudentsAction = getAction(request, params);
        JsonResult res = getJsonResult(enrollStudentsAction);
        EnrollStudentsData data = (EnrollStudentsData) res.getOutput();
        assertEquals(1, data.getStudentsData().getStudents().size());
        List<Student> studentsInCourse = logic.getStudentsForCourse(courseId);
        assertEquals(4, studentsInCourse.size());

        ______TS("Typical Success Case For Changing Details (except email) of a Student");

        StudentAttributes changedTeam = StudentAttributes.builder(courseId, "student1@teammates.tmt")
                .withName("Student 1")
                .withTeamName("Team 3")
                .withSectionName("Section 3")
                .withComment("Test Comment")
                .build();

        request = prepareRequest(Arrays.asList(changedTeam));
        enrollStudentsAction = getAction(request, params);
        res = getJsonResult(enrollStudentsAction);
        data = (EnrollStudentsData) res.getOutput();
        assertEquals(1, data.getStudentsData().getStudents().size());
        studentsInCourse = logic.getStudentsForCourse(courseId);
        assertEquals(4, studentsInCourse.size());

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
