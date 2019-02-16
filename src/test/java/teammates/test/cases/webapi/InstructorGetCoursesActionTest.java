package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.webapi.action.InstructorGetCoursesAction;
import teammates.ui.webapi.action.InstructorGetCoursesAction.CourseDetails;
import teammates.ui.webapi.action.InstructorGetCoursesAction.InstructorGetCoursesResult;
import teammates.ui.webapi.action.JsonResult;

/**
 *SUT: {@link InstructorGetCoursesAction}.
 */
public class InstructorGetCoursesActionTest extends BaseActionTest<InstructorGetCoursesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_STUDENTS_COURSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {
        DataBundleHelper dataBundleHelper = new DataBundleHelper(typicalBundle);

        InstructorAttributes instructor3 = typicalBundle.instructors.get("instructor3OfCourse2");
        loginAsInstructor(instructor3.googleId);

        boolean isInstructorAllowedToModify = instructor3.isAllowedForPrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        ______TS("Courses Exist");

        InstructorGetCoursesAction action = getAction();
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        InstructorGetCoursesResult output = (InstructorGetCoursesResult) result.getOutput();

        List<CourseAttributes> expectedCourses = dataBundleHelper.getCoursesByInstructorGoogleId(instructor3.googleId);

        List<CourseDetails> expectedCourseDetailsList = new ArrayList<>();
        expectedCourses.forEach(course -> {
            if (course != null) {
                expectedCourseDetailsList.add(new CourseDetails(
                        course.getId(), course.getName(), instructor3.isArchived, isInstructorAllowedToModify));
            }
        });

        AssertHelper.assertSameContentIgnoreOrder(expectedCourseDetailsList, output.getCourses());
    }

    @Override
    @Test
    public void testAccessControl() throws Exception {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForStudents();
        verifyInaccessibleForUnregisteredUsers();
        verifyAccessibleForAdminToMasqueradeAsInstructor(new String[] {});
    }

    static class DataBundleHelper {
        private final DataBundle dataBundle;

        DataBundleHelper(DataBundle dataBundle) {
            this.dataBundle = dataBundle;
        }

        public List<CourseAttributes> getCoursesByInstructorGoogleId(String instructorGoogleId) {
            return dataBundle.instructors.values().stream()
                    .filter(instructorAttributes -> {
                        String googleId = instructorAttributes.getGoogleId();
                        if (googleId != null && googleId.equals(instructorGoogleId)) {
                            return true;
                        }
                        return false;
                    })
                    .map(instructorAttributes -> getCourseById(instructorAttributes.getCourseId()))
                    .collect(Collectors.toList());
        }

        public CourseAttributes getCourseById(String courseId) {
            return dataBundle.courses.values()
                    .stream()
                    .filter(course -> course.getId().equals(courseId))
                    .findFirst()
                    .orElseGet(null);
        }

        public List<StudentAttributes> getStudentsForCourse(String courseId) {
            return dataBundle.students.values()
                    .stream()
                    .filter(student -> student.course.equals(courseId))
                    .collect(Collectors.toList());
        }

        public List<String> getSectionNamesForCourse(String courseId) {
            List<String> sections = new ArrayList<>();

            List<StudentAttributes> studentsOfCourse = getStudentsForCourse(courseId);
            studentsOfCourse.forEach(student -> {
                if (student.section != null && !sections.contains(student.section)) {
                    sections.add(student.section);
                }
            });

            return sections;
        }

        public List<String> getTeamsForSection(String courseId, String sectionName) {
            List<String> teams = new ArrayList<>();

            List<StudentAttributes> studentsOfCourse = getStudentsForCourse(courseId);
            studentsOfCourse.forEach(student -> {
                if (student.section != null && student.section.equals(sectionName) && student.team != null
                        && !teams.contains(student.team)) {
                    teams.add(student.team);
                }
            });

            return teams;
        }

        public List<StudentAttributes> getStudentsForTeam(String courseId, String sectionName, String teamName) {
            return dataBundle.students.values()
                    .stream()
                    .filter(student -> student.course.equals(courseId))
                    .filter(student -> student.section.equals(sectionName))
                    .filter(student -> student.team.equals(teamName))
                    .collect(Collectors.toList());
        }
    }
}
