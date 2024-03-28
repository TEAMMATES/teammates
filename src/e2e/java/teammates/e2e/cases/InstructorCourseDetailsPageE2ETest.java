package teammates.e2e.cases;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseDetailsPage;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.e2e.pageobjects.InstructorStudentRecordsPage;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_DETAILS_PAGE}.
 */
public class InstructorCourseDetailsPageE2ETest extends BaseE2ETestCase {
    private StudentAttributes student;
    private CourseAttributes course;

    private String fileName;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseDetailsPageE2ETest.json");
        student = testData.students.get("charlie.tmms@ICDet.CS2104");
        student.setEmail(TestProperties.TEST_EMAIL);

        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorCourseDetailsPageE2ETest_SqlEntities.json"));

        course = testData.courses.get("ICDet.CS2104");
        fileName = "/" + course.getId() + "_studentList.csv";
    }

    @BeforeClass
    public void classSetup() {
        deleteDownloadsFile(fileName);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl detailsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                .withCourseId(course.getId());
        InstructorCourseDetailsPage detailsPage = loginToPage(detailsPageUrl, InstructorCourseDetailsPage.class,
                testData.instructors.get("ICDet.instr").getGoogleId());

        ______TS("verify loaded details");
        InstructorAttributes[] instructors = {
                testData.instructors.get("ICDet.instr"),
                testData.instructors.get("ICDet.instr2"),
        };
        StudentAttributes[] students = {
                testData.students.get("alice.tmms@ICDet.CS2104"),
                testData.students.get("benny.tmms@ICDet.CS2104"),
                testData.students.get("charlie.tmms@ICDet.CS2104"),
                testData.students.get("danny.tmms@ICDet.CS2104"),
        };

        verifyCourseDetails(detailsPage, course, instructors, students);
        detailsPage.verifyNumStudents(students.length);
        detailsPage.verifyStudentDetails(students);

        ______TS("link: view student details page");

        StudentAttributes studentToView = testData.students.get("benny.tmms@ICDet.CS2104");

        InstructorCourseStudentDetailsViewPage studentDetailsViewPage =
                detailsPage.clickViewStudent(studentToView.getEmail());
        studentDetailsViewPage.verifyIsCorrectPage(course.getId(), studentToView.getEmail());
        studentDetailsViewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: edit student details page");

        InstructorCourseStudentDetailsEditPage studentDetailsEditPage =
                detailsPage.clickEditStudent(studentToView.getEmail());
        studentDetailsEditPage.verifyIsCorrectPage(course.getId(), studentToView.getEmail());
        studentDetailsEditPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: view all records page");

        InstructorStudentRecordsPage studentRecordsPage =
                detailsPage.clickViewAllRecords(studentToView.getEmail());
        studentRecordsPage.verifyIsCorrectPage(course.getId(), studentToView.getName());
        studentRecordsPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("send invite");
        detailsPage.sendInvite(student.getEmail());

        detailsPage.verifyStatusMessage("An email has been sent to " + student.getEmail());
        String expectedEmailSubject = "TEAMMATES: Invitation to join course ["
                + course.getName() + "][Course ID: " + course.getId() + "]";
        verifyEmailSent(student.getEmail(), expectedEmailSubject);

        ______TS("remind all students to join");
        detailsPage.remindAllToJoin();

        detailsPage.verifyStatusMessage("Emails have been sent to unregistered students.");
        verifyEmailSent(student.getEmail(), expectedEmailSubject);

        ______TS("download student list");
        detailsPage.downloadStudentList();
        String status = student.getGoogleId().isEmpty() ? "Yet to Join" : "Joined";
        String[] studentInfo = { student.getTeam(), student.getName(), status, student.getEmail() };
        List<String> expectedContent = Arrays.asList("Course ID," + course.getId(),
                "Course Name," + course.getName(), String.join(",", studentInfo));
        verifyDownloadedFile(fileName, expectedContent);

        ______TS("delete student");
        detailsPage.sortByName();
        detailsPage.sortByStatus();
        StudentAttributes[] studentsAfterDelete = { students[0], students[3], students[1] };
        detailsPage.deleteStudent(student.getEmail());

        detailsPage.verifyStatusMessage("Student is successfully deleted from course \""
                + course.getId() + "\"");
        detailsPage.verifyNumStudents(studentsAfterDelete.length);
        detailsPage.verifyStudentDetails(studentsAfterDelete);
        verifyAbsentInDatabase(student);

        ______TS("delete all students");
        detailsPage.deleteAllStudents();

        detailsPage.verifyStatusMessage("All the students have been removed from the course");
        detailsPage.verifyNumStudents(0);
        for (StudentAttributes student : studentsAfterDelete) {
            verifyAbsentInDatabase(student);
        }
    }

    @AfterClass
    public void classTearDown() {
        BACKDOOR.removeDataBundle(testData);
    }

    private void verifyCourseDetails(InstructorCourseDetailsPage detailsPage, CourseAttributes course,
                                     InstructorAttributes[] instructors, StudentAttributes[] students) {
        Set<String> sections = new HashSet<>();
        Set<String> teams = new HashSet<>();

        for (StudentAttributes student : students) {
            sections.add(student.getSection());
            teams.add(student.getTeam());
        }

        detailsPage.verifyCourseDetails(course, instructors, sections.size(), teams.size(), students.length);
    }
}
