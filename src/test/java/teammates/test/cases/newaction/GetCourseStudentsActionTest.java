package teammates.test.cases.newaction;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.newcontroller.GetCourseStudentsAction;
import teammates.ui.newcontroller.GetCourseStudentsAction.CourseDetails;
import teammates.ui.newcontroller.GetCourseStudentsAction.GetCourseStudentsActionResult;
import teammates.ui.newcontroller.GetCourseStudentsAction.SectionDetails;
import teammates.ui.newcontroller.GetCourseStudentsAction.StudentDetails;
import teammates.ui.newcontroller.GetCourseStudentsAction.TeamDetails;
import teammates.ui.newcontroller.JsonResult;

/**
 * SUT: {@link GetCourseStudentsAction}.
 */
public class GetCourseStudentsActionTest extends BaseActionTest<GetCourseStudentsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {

        InstructorAttributes instructor3 = typicalBundle.instructors.get("instructor3OfCourse2");
        loginAsInstructor(instructor3.googleId);

        ______TS("invalid query param");

        // null for query param
        verifyHttpParameterFailure();

        // invalid query param format
        String[] invalidSubmissionParams1 = new String[] {
                Const.ParamsNames.COURSE_ID, ",,,,,"
        };
        GetCourseStudentsAction action = getAction(invalidSubmissionParams1);
        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());

        String[] invalidSubmissionParams3 = new String[] {
                Const.ParamsNames.COURSE_ID, "&idOfTypicalCourse2&idOfTypicalCourse3"
        };
        action = getAction(invalidSubmissionParams3);
        result = getJsonResult(action);
        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());

        String[] invalidSubmissionParams4 = new String[] {
                Const.ParamsNames.COURSE_ID, "\"idOfTypicalCourse2\""
        };
        action = getAction(invalidSubmissionParams4);
        result = getJsonResult(action);
        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());

        ______TS("valid query param");

        CourseAttributes course1 = typicalBundle.courses.get("typicalCourse1");

        CourseDetails expectedCourseDetails = getCourseDetails(course1.getId(), instructor3);

        // all accessible courseids
        String[] validSubmmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course1.getId()
        };

        action = getAction(validSubmmissionParams);
        result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        GetCourseStudentsActionResult output = (GetCourseStudentsActionResult) result.getOutput();

        assertEquals(expectedCourseDetails, output.getCourse());
        AssertHelper.assertSameContentIgnoreOrder(
                expectedCourseDetails.getSections(), output.getCourse().getSections());
        assertNotEquals(
                expectedCourseDetails.getSections().get(0), expectedCourseDetails.getSections().get(1));
        AssertHelper.assertSameContentIgnoreOrder(
                expectedCourseDetails.getSections().get(0).getTeams(),
                output.getCourse().getSections().get(0).getTeams());
        AssertHelper.assertSameContentIgnoreOrder(
                expectedCourseDetails.getSections().get(0).getTeams().get(0).getStudents(),
                output.getCourse().getSections().get(0).getTeams().get(0).getStudents());

        // some inaccessible courseids
    }

    @Override
    @Test
    public void testAccessControl() throws Exception {
        CourseAttributes course1 = typicalBundle.courses.get("typicalCourse1");

        String[] validSubmmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course1.getId()
        };

        verifyAccessibleForAdminToMasqueradeAsInstructor(validSubmmissionParams);
        verifyInaccessibleWithoutLogin(validSubmmissionParams);
        verifyInaccessibleForStudents(validSubmmissionParams);
        verifyInaccessibleForUnregisteredUsers(validSubmmissionParams);
    }

    private CourseDetails getCourseDetails(String courseId, InstructorAttributes instructor) {
        CourseAttributes course = typicalBundle.getCourseById(courseId);

        List<SectionDetails> sectionDetails = new ArrayList<>();

        List<String> sections = typicalBundle.getSectionNamesForCourse(course.getId());
        sections.forEach(section -> {
            List<TeamDetails> teamDetails = new ArrayList<>();

            List<String> teams = typicalBundle.getTeamsForSection(course.getId(), section);
            teams.forEach(team -> {
                List<StudentDetails> studentDetailsList = new ArrayList<>();

                List<StudentAttributes> students = typicalBundle.getStudentsForTeam(course.getId(), section, team);
                students.forEach(student -> {
                    StudentDetails studentDetails = new StudentDetails(student.name,
                            student.email, student.getStudentStatus());
                    studentDetailsList.add(studentDetails);
                });

                teamDetails.add(new TeamDetails(team, studentDetailsList));
            });

            sectionDetails.add(
                    new SectionDetails(
                        section,
                        teamDetails,
                        instructor.isAllowedForPrivilege(
                                section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS),
                        instructor.isAllowedForPrivilege(
                                section, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)));
        });

        return new CourseDetails(course.getId(), course.getName(), course.createdAt, sectionDetails);
    }
}
