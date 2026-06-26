package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.KeyUtil;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.e2e.pageobjects.InstructorHomePage;
import teammates.storage.entity.Instructor;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class InstructorCourseJoinConfirmationPageE2ETest extends BaseE2ETestCase {
    private Instructor newInstructor;
    private String newInstructorAccountEmail;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadDataBundle("/InstructorCourseJoinConfirmationPageE2ETest.json"));

        newInstructor = testData.instructors.get("ICJoinConf.instr.CS1101");
        newInstructorAccountEmail = testData.accounts.get("ICJoinConf.instr.CS1101").getEmail();
    }

    @Test
    @Override
    public void testAll() {
        ______TS("Click join link: invalid key");
        String invalidKey = "invalidKey";
        AppUrl joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withKey(invalidKey)
                .withEntityType(Const.EntityType.INSTRUCTOR);
        CourseJoinConfirmationPage confirmationPage = loginToPage(
                joinLink, CourseJoinConfirmationPage.class, newInstructorAccountEmail);

        confirmationPage.verifyDisplayedMessage("The course join link is invalid. You may have "
                + "entered the URL incorrectly or the URL may correspond to a/an instructor that does not exist.");

        ______TS("Click join link: valid key");
        joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withKey(KeyUtil.encryptCourseJoinKey(newInstructor.getId(), newInstructor.getRegKey()))
                .withEntityType(Const.EntityType.INSTRUCTOR);
        confirmationPage = getNewPageInstance(joinLink, CourseJoinConfirmationPage.class);

        confirmationPage.verifyJoiningUser(newInstructorAccountEmail);
        confirmationPage.confirmJoinCourse(InstructorHomePage.class);

        ______TS("Already joined, no confirmation page");

        getNewPageInstance(joinLink, InstructorHomePage.class);

        logout();
    }
}
