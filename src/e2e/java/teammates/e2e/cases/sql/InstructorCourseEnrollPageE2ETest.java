package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseEnrollPageSql;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.ui.output.StudentData;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_ENROLL_PAGE}.
 */
public class InstructorCourseEnrollPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/InstructorCourseEnrollPageE2ETestSql.json"));
    }

    @Test
    @Override
    protected void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withCourseId(testData.courses.get("ICEnroll.CS2104").getId());
        InstructorCourseEnrollPageSql enrollPage = loginToPage(url, InstructorCourseEnrollPageSql.class,
                testData.instructors.get("ICEnroll.teammates.test").getGoogleId());
        Course course = testData.courses.get("ICEnroll.CS2104");
        Team team1 = testData.teams.get("tm.e2e.ICEnroll.courseICEnroll.CS2104-SectionA-Team1");
        Team team2 = testData.teams.get("tm.e2e.ICEnroll.courseICEnroll.CS2104-SectionB-Team2");
        Team team3 = testData.teams.get("tm.e2e.ICEnroll.courseICEnroll.CS2104-SectionC-Team3");

        ______TS("Add rows to enroll spreadsheet");
        int numRowsToAdd = 30;
        enrollPage.addEnrollSpreadsheetRows(numRowsToAdd);
        enrollPage.verifyNumAddedEnrollSpreadsheetRows(numRowsToAdd);

        ______TS("Enroll students to empty course");
        Student student1 = createCourseStudent(course, "Alice Betsy",
                "alice.b.tmms@gmail.tmt", "This student's name is Alice Betsy", team1);
        Student student2 = createCourseStudent(course, "Benny Charles",
                "benny.c.tmms@gmail.tmt", "This student's name is Benny Charles", team1);
        Student student3 = createCourseStudent(course, "Charlie Davis",
                "charlie.d.tmms@gmail.tmt", "This student's name is Charlie Davis", team2);

        Student[] studentsEnrollingToEmptyCourse = { student1, student2, student3 };

        enrollPage.enroll(studentsEnrollingToEmptyCourse);
        enrollPage.verifyStatusMessage("Enrollment successful. Summary given below.");
        enrollPage.verifyResultsPanelContains(studentsEnrollingToEmptyCourse, null, null, null, null);

        // refresh page to confirm enrollment
        enrollPage = getNewPageInstance(url, InstructorCourseEnrollPageSql.class);
        enrollPage.verifyExistingStudentsTableContains(studentsEnrollingToEmptyCourse);

        StudentData s1 = new StudentData(student1);
        StudentData s2 = new StudentData(student2);
        StudentData s3 = new StudentData(student3);

        // verify students in database
        assertEquals(getStudent(student1).getEmail(), s1.getEmail());
        assertEquals(getStudent(student2).getEmail(), s2.getEmail());
        assertEquals(getStudent(student3).getEmail(), s3.getEmail());

        ______TS("Enroll and modify students in existing course");
        // modify team details of existing student
        student3.setTeam(team3);
        // add valid new student
        Student student4 = createCourseStudent(course, "Danny Engrid",
                "danny.e.tmms@gmail.tmt", "This student's name is Danny Engrid", team2);
        // add new student with invalid email
        Student student5 = createCourseStudent(course, "Invalid Student",
                "invalid.email", "Comment for Invalid", team2);

        // student2 included to test modified without change table
        Student[] studentsEnrollingToExistingCourse = {student2, student3, student4, student5};
        enrollPage.enroll(studentsEnrollingToExistingCourse);
        enrollPage.verifyStatusMessage("Some students failed to be enrolled, see the summary below.");

        Student[] newStudentsData = {student4};
        Student[] modifiedStudentsData = {student3};
        Student[] modifiedWithoutChangeStudentsData = {student2};
        Student[] errorStudentsData = {student5};
        Student[] unmodifiedStudentsData = {student1};

        enrollPage.verifyResultsPanelContains(newStudentsData, modifiedStudentsData, modifiedWithoutChangeStudentsData,
                errorStudentsData, unmodifiedStudentsData);

        s3 = new StudentData(student3);
        StudentData s4 = new StudentData(student4);

        // verify students in database
        assertEquals(getStudent(student1).getEmail(), s1.getEmail());
        assertEquals(getStudent(student2).getEmail(), s2.getEmail());
        assertEquals(getStudent(student3).getEmail(), s3.getEmail());
        assertEquals(getStudent(student4).getEmail(), s4.getEmail());
        assertNull(getStudent(student5));

        // refresh page to confirm enrollment
        enrollPage = getNewPageInstance(url, InstructorCourseEnrollPageSql.class);
        Student[] expectedExistingData = {student1, student2, student3, student4};
        enrollPage.verifyExistingStudentsTableContains(expectedExistingData);
    }

    private Student createCourseStudent(Course course, String name, String email, String comments, Team team) {
        return new Student(course, name, email, comments, team);
    }

}
