package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.e2e.pageobjects.ErrorReportingModal;
import teammates.e2e.pageobjects.StudentHomePage;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class StudentCourseJoinConfirmationPageE2ETest extends BaseE2ETestCase {
    private StudentAttributes newStudent;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentCourseJoinConfirmationPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        newStudent = testData.students.get("alice.tmms@SCJoinConf.CS2104");
        newStudent.googleId = testData.accounts.get("alice.tmms").googleId;
    }

    @Test
    @Override
    public void testAll() {
        ______TS("Click join link: invalid key");
        String courseId = testData.courses.get("SCJoinConf.CS2104").getId();
        String invalidEncryptedKey = "invalidKey";
        AppUrl joinLink = createUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(invalidEncryptedKey)
                .withCourseId(courseId)
                .withEntityType(Const.EntityType.STUDENT)
                .withUserId(newStudent.googleId);
        ErrorReportingModal errorPage = loginAdminToPage(joinLink, ErrorReportingModal.class);

        errorPage.verifyErrorMessage("No student with given registration key: " + invalidEncryptedKey);

        ______TS("Click join link: valid key");
        joinLink = createUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getKeyForStudent(newStudent))
                .withCourseId(courseId)
                .withEntityType(Const.EntityType.STUDENT)
                .withUserId(newStudent.googleId);
        CourseJoinConfirmationPage confirmationPage = loginAdminToPage(joinLink, CourseJoinConfirmationPage.class);

        confirmationPage.verifyJoiningUser(newStudent.googleId);
        confirmationPage.confirmJoinCourse(StudentHomePage.class);

        ______TS("Already joined, no confirmation page");

        getNewPageInstance(joinLink, StudentHomePage.class);
    }
}
