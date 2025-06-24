package teammates.e2e.cases.sql;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseDetailsPageSql;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.e2e.pageobjects.InstructorStudentRecordsPage;
import teammates.e2e.util.TestProperties;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_DETAILS_PAGE}.
 */
public class InstructorCourseDetailsPageE2ETest extends BaseE2ETestCase {
    private Course course;
    private Student student3;
    private String downloadedFileName;

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorCourseDetailsPageE2ESqlTest.json");
        student3 = testData.students.get("charlie.tmms@ICDet.CS2104");
        student3.setEmail(TestProperties.TEST_EMAIL);
        removeAndRestoreDataBundle(testData);
        course = testData.courses.get("ICDet.CS2104");
        downloadedFileName = "/" + course.getId() + "_studentList.csv";
        deleteDownloadsFile(downloadedFileName);
    }

    @Test
    @Override
    protected void testAll() {
        Instructor instructor1 = testData.instructors.get("ICDet.instr");
        AppUrl detailsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                .withCourseId(course.getId());
        InstructorCourseDetailsPageSql detailsPage =
                loginToPage(detailsPageUrl, InstructorCourseDetailsPageSql.class, instructor1.getGoogleId());

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
        students.forEach(student -> {
            sectionNames.add(student.getSectionName());
            teamNames.add(student.getTeamName());
        });
        detailsPage.verifyCourseDetails(course, instructors, sectionNames.size(), teamNames.size(), students.size());
        detailsPage.verifyNumStudents(students.size());
        detailsPage.verifyStudentDetails(students);

        ______TS("link: view student details page");
        Student studentToView = testData.students.get("benny.tmms@ICDet.CS2104");
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
        detailsPage.sendInvite(student3.getEmail());
        detailsPage.verifyStatusMessage("An email has been sent to " + student3.getEmail());
        String expectedEmailSubject = "TEAMMATES: Invitation to join course ["
                + course.getName() + "][Course ID: " + course.getId() + "]";
        verifyEmailSent(student3.getEmail(), expectedEmailSubject);

        ______TS("remind all students to join");
        detailsPage.remindAllToJoin();
        detailsPage.verifyStatusMessage("Emails have been sent to unregistered students.");
        verifyEmailSent(student3.getEmail(), expectedEmailSubject);

        ______TS("download student list");
        detailsPage.downloadStudentList();
        String status = "Yet to Join";
        String[] studentInfo = { student3.getTeamName(), student3.getName(), status, student3.getEmail() };
        List<String> expectedContent = Arrays.asList("Course ID," + course.getId(),
                "Course Name," + course.getName(), String.join(",", studentInfo));
        verifyDownloadedFile(downloadedFileName, expectedContent);

        ______TS("delete student");
        detailsPage.sortByName();
        detailsPage.sortByStatus();
        List<Student> studentsAfterDelete = Arrays.asList(
                testData.students.get("alice.tmms@ICDet.CS2104"),
                testData.students.get("danny.tmms@ICDet.CS2104"),
                testData.students.get("benny.tmms@ICDet.CS2104")
        );
        detailsPage.deleteStudent(student3.getEmail());
        detailsPage.verifyStatusMessage("Student is successfully deleted from course \""
                + course.getId() + "\"");
        detailsPage.verifyNumStudents(studentsAfterDelete.size());
        detailsPage.verifyStudentDetails(studentsAfterDelete);
        verifyAbsentInDatabase(student3);

        ______TS("delete all students");
        detailsPage.deleteAllStudents();
        detailsPage.verifyStatusMessage("All the students have been removed from the course");
        detailsPage.verifyNumStudents(0);
        studentsAfterDelete.forEach(this::verifyAbsentInDatabase);
    }
}
