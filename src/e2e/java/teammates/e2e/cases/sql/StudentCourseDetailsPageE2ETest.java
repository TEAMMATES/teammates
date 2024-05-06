package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentCourseDetailsPage;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_COURSE_DETAILS_PAGE}.
 */
public class StudentCourseDetailsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                        loadSqlDataBundle("/StudentCourseDetailsPageE2ETestSql.json"));
    }

    @Test
    @Override
    public void testAll() {

        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_COURSE_DETAILS_PAGE)
                .withCourseId("tm.e2e.SCDet.CS2104");
        StudentCourseDetailsPage detailsPage = loginToPage(url, StudentCourseDetailsPage.class,
                testData.accounts.get("SCDet.alice").getGoogleId());

        ______TS("verify loaded data");
        Instructor[] instructors = { testData.instructors.get("SCDet.instr"),
                testData.instructors.get("SCDet.instr2") };
        Student[] teammates = { testData.students.get("SCDet.benny"),
                testData.students.get("SCDet.charlie") };

        detailsPage.verifyCourseDetails(testData.courses.get("SCDet.CS2104"));
        detailsPage.verifyInstructorsDetails(instructors);
        detailsPage.verifyStudentDetails(testData.students.get("SCDet.alice"));
        detailsPage.sortTeammatesByName();
        detailsPage.verifyTeammatesDetails(teammates);
    }
}
