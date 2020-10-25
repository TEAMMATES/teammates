package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
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

        AppUrl url = createUrl(Const.WebPageURIs.STUDENT_COURSE_DETAILS_PAGE)
                .withCourseId("SCDetailsE2eT.CS2104")
                .withUserId(testData.students.get("SCDetailsE2eT.alice").googleId);
        StudentCourseDetailsPage detailsPage = loginAdminToPage(url, StudentCourseDetailsPage.class);

        ______TS("verify loaded data");
        InstructorAttributes[] instructors = { testData.instructors.get("SCDetailsE2eT.instr"),
                testData.instructors.get("SCDetailsE2eT.instr2") };
        StudentAttributes[] teammates = { testData.students.get("SCDetailsE2eT.benny"),
                testData.students.get("SCDetailsE2eT.charlie") };
        StudentProfileAttributes[] teammatesProfiles = { testData.profiles.get("SCDetailsE2eT.benny"),
                testData.profiles.get("SCDetailsE2eT.charlie") };

        detailsPage.verifyCourseDetails(testData.courses.get("SCDetailsE2eT.CS2104"));
        detailsPage.verifyInstructorsDetails(instructors);
        detailsPage.verifyStudentDetails(testData.students.get("SCDetailsE2eT.alice"));
        detailsPage.sortTeammatesByName();
        detailsPage.verifyTeammatesDetails(teammates, teammatesProfiles);
    }
}
