package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.DataBundleHelper;
import teammates.ui.webapi.action.GetCourseStudentsAction;
import teammates.ui.webapi.action.GetCourseStudentsAction.CourseDetails;
import teammates.ui.webapi.action.GetCourseStudentsAction.GetCourseStudentsActionResult;
import teammates.ui.webapi.action.GetCourseStudentsAction.SectionDetails;
import teammates.ui.webapi.action.GetCourseStudentsAction.StudentDetails;
import teammates.ui.webapi.action.GetCourseStudentsAction.TeamDetails;
import teammates.ui.webapi.action.JsonResult;

/**
 * SUT: {@link GetCourseStudentsAction}.
 */
public class GetCourseStudentsActionTest extends BaseActionTest<GetCourseStudentsAction> {

    DataBundleHelper dataBundleHelper = new DataBundleHelper(typicalBundle);

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
                Const.ParamsNames.COURSE_ID, ",,,,,",
        };
        GetCourseStudentsAction action = getAction(invalidSubmissionParams1);
        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());

        String[] invalidSubmissionParams3 = new String[] {
                Const.ParamsNames.COURSE_ID, "&idOfTypicalCourse2&idOfTypicalCourse3",
        };
        action = getAction(invalidSubmissionParams3);
        result = getJsonResult(action);
        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());

        String[] invalidSubmissionParams4 = new String[] {
                Const.ParamsNames.COURSE_ID, "\"idOfTypicalCourse2\"",
        };
        action = getAction(invalidSubmissionParams4);
        result = getJsonResult(action);
        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());

        ______TS("valid query param");

        CourseAttributes course1 = typicalBundle.courses.get("typicalCourse1");

        CourseDetails expectedCourseDetails = getCourseDetails(course1.getId(), instructor3);

        // accessible courseid
        String[] validSubmmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course1.getId(),
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
    }

    @Override
    @Test
    public void testAccessControl() throws Exception {
        CourseAttributes course1 = typicalBundle.courses.get("typicalCourse1");
        InstructorAttributes instructor3 = typicalBundle.instructors.get("instructor3OfCourse2");

        String[] validSubmmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course1.getId(),
        };

        verifyAccessibleForAdminToMasqueradeAsInstructor(validSubmmissionParams);
        verifyInaccessibleWithoutLogin(validSubmmissionParams);
        verifyInaccessibleForStudents(validSubmmissionParams);
        verifyInaccessibleForUnregisteredUsers(validSubmmissionParams);

        // inaccessible courseid
        CourseAttributes course4 = typicalBundle.courses.get("typicalCourse4");
        String[] inaccessibleSubmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course4.getId(),
        };

        verifyInaccessibleForSpecificInstructor(instructor3, inaccessibleSubmissionParams);
    }

    private CourseDetails getCourseDetails(String courseId, InstructorAttributes instructor) {
        CourseAttributes course = dataBundleHelper.getCourseById(courseId);

        List<SectionDetails> sectionDetails = getSectionDetailsForCOurse(course, instructor);

        return new CourseDetails(course.getId(), course.getName(), course.createdAt, sectionDetails);
    }

    private List<SectionDetails> getSectionDetailsForCOurse(CourseAttributes course, InstructorAttributes instructor) {
        List<SectionDetails> sectionDetails = new ArrayList<>();

        List<String> sections = dataBundleHelper.getSectionNamesForCourse(course.getId());
        sections.forEach(section -> {
            List<TeamDetails> teamDetails = getTeamDetailsForSection(course, section);

            sectionDetails.add(
                    new SectionDetails(
                            section,
                            teamDetails,
                            instructor.isAllowedForPrivilege(
                                    section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS),
                            instructor.isAllowedForPrivilege(
                                    section, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT),
                            course.getId()));
        });

        return sectionDetails;
    }

    private List<TeamDetails> getTeamDetailsForSection(CourseAttributes course, String section) {
        List<TeamDetails> teamDetails = new ArrayList<>();

        List<String> teams = dataBundleHelper.getTeamsForSection(course.getId(), section);
        teams.forEach(team -> {
            List<StudentDetails> studentDetailsList = getStudentDetailsForTeam(course, section, team);

            teamDetails.add(new TeamDetails(team, studentDetailsList, section, course.getId()));
        });

        return teamDetails;
    }

    private List<StudentDetails> getStudentDetailsForTeam(CourseAttributes course, String section, String team) {
        List<StudentDetails> studentDetailsList = new ArrayList<>();

        List<StudentAttributes> students = dataBundleHelper.getStudentsForTeam(course.getId(), section, team);
        students.forEach(student -> {
            StudentDetails studentDetails = new StudentDetails(student.name,
                    student.email, student.getStudentStatus(), team, section, course.getId());
            studentDetailsList.add(studentDetails);
        });

        return studentDetailsList;
    }

}
