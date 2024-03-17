package teammates.e2e.cases.sql;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseDetailsPage;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_DETAILS_PAGE}.
 */
public class InstructorCourseDetailsPageE2ETest extends BaseE2ETestCase {
    private Course course;

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorCourseDetailsPageE2ESqlTest.json");
        removeAndRestoreDataBundle(testData);
        course = testData.courses.get("ICDet.CS2104");
    }

    @Test
    @Override
    protected void testAll() {
        Instructor instructor1 = testData.instructors.get("ICDet.instr");
        AppUrl detailsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                .withCourseId(course.getId());
        InstructorCourseDetailsPage detailsPage =
                loginToPage(detailsPageUrl, InstructorCourseDetailsPage.class, instructor1.getGoogleId());

        ______TS("verify loaded details");
        List<Instructor> instructors = Arrays.asList(instructor1, testData.instructors.get("ICDet.instr2"));
        List<Student> students = Arrays.asList(
                testData.students.get("alice.tmms@ICDet.CS2104"),
                testData.students.get("benny.tmms@ICDet.CS2104"),
                testData.students.get("charlie.tmms@ICDet.CS2104"),
                testData.students.get("danny.tmms@ICDet.CS2104")
        );
        Set<String> sectionNames = new HashSet<>();
        Set<String> teamNames = new HashSet<>();
        students.forEach((student) -> {
            sectionNames.add(student.getSectionName());
            teamNames.add(student.getTeamName());
        });
        detailsPage.verifyCourseDetails(course, instructors, sectionNames.size(), teamNames.size(), students.size());
        detailsPage.verifyNumStudents(students.size());
        detailsPage.verifyStudentDetails(students);
    }
}
