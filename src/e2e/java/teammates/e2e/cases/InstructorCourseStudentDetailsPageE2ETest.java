package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE}.
 */
public class InstructorCourseStudentDetailsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseStudentDetailsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        ______TS("verify loaded details - student with profile");
        StudentAttributes student = testData.students.get("ICSDetailsE2eT.jose.tmms");
        StudentProfileAttributes studentProfile = testData.profiles.get("ICSDetailsE2eT.jose.tmms");
        AppUrl viewPageUrl = getStudentDetailsViewPageUrl(student.getEmail());
        InstructorCourseStudentDetailsViewPage viewPage =
                loginAdminToPage(viewPageUrl, InstructorCourseStudentDetailsViewPage.class);

        viewPage.verifyStudentDetails(studentProfile, student);

        ______TS("verify loaded details - student without profile");
        student = testData.students.get("ICSDetailsE2eT.benny.c");
        viewPageUrl = getStudentDetailsViewPageUrl(student.getEmail());
        viewPage = AppPage.getNewPageInstance(browser, viewPageUrl, InstructorCourseStudentDetailsViewPage.class);

        viewPage.verifyStudentDetails(null, student);
    }

    private AppUrl getStudentDetailsViewPageUrl(String studentEmail) {
        return createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE)
                .withUserId(testData.instructors.get("ICSDetailsE2eT.instr").getGoogleId())
                .withCourseId(testData.courses.get("ICSDetailsE2eT.CS2104").getId())
                .withStudentEmail(studentEmail);
    }
}
