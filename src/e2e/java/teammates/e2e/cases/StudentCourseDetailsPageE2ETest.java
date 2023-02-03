package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentCourseDetailsPage;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_COURSE_DETAILS_PAGE}.
 */
public class StudentCourseDetailsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentCourseDetailsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {

        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_COURSE_DETAILS_PAGE)
                .withCourseId("tm.e2e.SCDet.CS2104");
        StudentCourseDetailsPage detailsPage = loginToPage(url, StudentCourseDetailsPage.class,
                testData.students.get("SCDet.alice").getGoogleId());

        ______TS("verify loaded data");
        InstructorAttributes[] instructors = { testData.instructors.get("SCDet.instr"),
                testData.instructors.get("SCDet.instr2") };
        StudentAttributes[] teammates = { testData.students.get("SCDet.benny"),
                testData.students.get("SCDet.charlie") };

        detailsPage.verifyCourseDetails(testData.courses.get("SCDet.CS2104"));
        detailsPage.verifyInstructorsDetails(instructors);
        detailsPage.verifyStudentDetails(testData.students.get("SCDet.alice"));
        detailsPage.sortTeammatesByName();
        detailsPage.verifyTeammatesDetails(teammates);
    }
}
