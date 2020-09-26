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
        testData = loadDataBundle(Const.TestCase.STUDENT_COURSE_DETAILS_PAGE_E2E_TEST_JSON);
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() {

        AppUrl url = createUrl(Const.WebPageURIs.STUDENT_COURSE_DETAILS_PAGE)
                .withCourseId(Const.TestCase.SC_DETAILS_E2E_T_CS2104)
                .withUserId(testData.students.get(Const.TestCase.SC_DETAILS_E2E_T_ALICE).googleId);
        StudentCourseDetailsPage detailsPage = loginAdminToPage(url, StudentCourseDetailsPage.class);

        ______TS(Const.TestCase.VERIFY_LOADED_DATA);
        InstructorAttributes[] instructors = { testData.instructors.get(Const.TestCase.SC_DETAILS_E2E_T_INSTR),
                testData.instructors.get(Const.TestCase.SC_DETAILS_E2E_T_INSTR2) };
        StudentAttributes[] teammates = { testData.students.get(Const.TestCase.SC_DETAILS_E2E_T_BENNY),
                testData.students.get(Const.TestCase.SC_DETAILS_E2E_T_CHARLIE) };
        StudentProfileAttributes[] teammatesProfiles = { testData.profiles.get(Const.TestCase.SC_DETAILS_E2E_T_BENNY),
                testData.profiles.get(Const.TestCase.SC_DETAILS_E2E_T_CHARLIE) };

        detailsPage.verifyCourseDetails(testData.courses.get(Const.TestCase.SC_DETAILS_E2E_T_CS2104));
        detailsPage.verifyInstructorsDetails(instructors);
        detailsPage.verifyStudentDetails(testData.students.get(Const.TestCase.SC_DETAILS_E2E_T_ALICE));
        detailsPage.sortTeammatesByName();
        detailsPage.verifyTeammatesDetails(teammates, teammatesProfiles);
    }
}
