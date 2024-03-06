package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseEnrollPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_ENROLL_PAGE}.
 */
public class InstructorCourseEnrollPageE2ETest extends BaseE2ETestCase {
    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseEnrollPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorCourseEnrollPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withCourseId(testData.courses.get("ICEnroll.CS2104").getId());
        InstructorCourseEnrollPage enrollPage = loginToPage(url, InstructorCourseEnrollPage.class,
                testData.instructors.get("ICEnroll.teammates.test").getGoogleId());

        ______TS("Add rows to enroll spreadsheet");
        int numRowsToAdd = 30;
        enrollPage.addEnrollSpreadsheetRows(numRowsToAdd);
        enrollPage.verifyNumAddedEnrollSpreadsheetRows(numRowsToAdd);

        ______TS("Enroll students to empty course");
        StudentAttributes student1 = createCourseStudent("Section 1", "Team 1", "Alice Betsy",
                "alice.b.tmms@gmail.tmt", "Comment for Alice");
        StudentAttributes student2 = createCourseStudent("Section 1", "Team 1", "Benny Charles",
                "benny.c.tmms@gmail.tmt", "Comment for Benny");
        StudentAttributes student3 = createCourseStudent("Section 2", "Team 2", "Charlie Davis",
                "charlie.d.tmms@gmail.tmt", "Comment for Charlie");

        StudentAttributes[] studentsEnrollingToEmptyCourse = { student1, student2, student3 };

        enrollPage.enroll(studentsEnrollingToEmptyCourse);
        enrollPage.verifyStatusMessage("Enrollment successful. Summary given below.");
        enrollPage.verifyResultsPanelContains(studentsEnrollingToEmptyCourse, null, null, null, null);

        // refresh page to confirm enrollment
        enrollPage = getNewPageInstance(url, InstructorCourseEnrollPage.class);
        enrollPage.verifyExistingStudentsTableContains(studentsEnrollingToEmptyCourse);

        // verify students in database
        assertEquals(getStudent(student1), student1);
        assertEquals(getStudent(student2), student2);
        assertEquals(getStudent(student3), student3);

        ______TS("Enroll and modify students in existing course");
        // modify team details of existing student
        student3.setTeam("Team 3");
        // add valid new student
        StudentAttributes student4 = createCourseStudent("Section 2", "Team 2", "Danny Engrid",
                "danny.e.tmms@gmail.tmt", "Comment for Danny");
        // add new student with invalid email
        StudentAttributes student5 = createCourseStudent("Section 2", "Team 2", "Invalid Student",
                "invalid.email", "Comment for Invalid");

        // student2 included to test modified without change table
        StudentAttributes[] studentsEnrollingToExistingCourse = {student2, student3, student4, student5};
        enrollPage.enroll(studentsEnrollingToExistingCourse);
        enrollPage.verifyStatusMessage("Some students failed to be enrolled, see the summary below.");

        StudentAttributes[] newStudentsData = {student4};
        StudentAttributes[] modifiedStudentsData = {student3};
        StudentAttributes[] modifiedWithoutChangeStudentsData = {student2};
        StudentAttributes[] errorStudentsData = {student5};
        StudentAttributes[] unmodifiedStudentsData = {student1};

        enrollPage.verifyResultsPanelContains(newStudentsData, modifiedStudentsData, modifiedWithoutChangeStudentsData,
                errorStudentsData, unmodifiedStudentsData);

        // verify students in database
        assertEquals(getStudent(student1), student1);
        assertEquals(getStudent(student2), student2);
        assertEquals(getStudent(student3), student3);
        assertEquals(getStudent(student4), student4);
        assertNull(getStudent(student5));

        // refresh page to confirm enrollment
        enrollPage = getNewPageInstance(url, InstructorCourseEnrollPage.class);
        StudentAttributes[] expectedExistingData = {student1, student2, student3, student4};
        enrollPage.verifyExistingStudentsTableContains(expectedExistingData);
    }

    private StudentAttributes createCourseStudent(String section, String team, String name,
                                                  String email, String comments) {
        return StudentAttributes.builder("tm.e2e.ICEnroll.CS2104", email)
                .withName(name)
                .withComment(comments)
                .withTeamName(team)
                .withSectionName(section)
                .build();
    }
}
