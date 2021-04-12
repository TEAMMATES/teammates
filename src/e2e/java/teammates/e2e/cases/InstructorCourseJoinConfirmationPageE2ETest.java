package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.e2e.pageobjects.ErrorReportingModal;
import teammates.e2e.pageobjects.InstructorHomePage;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class InstructorCourseJoinConfirmationPageE2ETest extends BaseE2ETestCase {
    InstructorAttributes newInstructor;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseJoinConfirmationPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        newInstructor = testData.instructors.get("ICJoinConf.instr.CS1101");
        newInstructor.googleId = "tm.e2e.ICJoinConf.instr2";
    }

    @Test
    @Override
    public void testAll() {
        ______TS("Click join link: invalid key");
        String invalidEncryptedKey = "invalidKey";
        AppUrl joinLink = createUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(invalidEncryptedKey)
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .withUserId(newInstructor.googleId);
        ErrorReportingModal errorPage = loginAdminToPage(joinLink, ErrorReportingModal.class);

        errorPage.verifyErrorMessage("No instructor with given registration key: " + invalidEncryptedKey);

        ______TS("Click join link: valid key");
        String courseId = testData.courses.get("ICJoinConf.CS1101").getId();
        String instructorEmail = newInstructor.email;
        joinLink = createUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getKeyForInstructor(courseId, instructorEmail))
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .withUserId(newInstructor.googleId);
        CourseJoinConfirmationPage confirmationPage = loginAdminToPage(joinLink, CourseJoinConfirmationPage.class);

        confirmationPage.verifyJoiningUser(newInstructor.googleId);
        confirmationPage.confirmJoinCourse(InstructorHomePage.class);

        ______TS("Already joined, no confirmation page");

        getNewPageInstance(joinLink, InstructorHomePage.class);
    }
}
