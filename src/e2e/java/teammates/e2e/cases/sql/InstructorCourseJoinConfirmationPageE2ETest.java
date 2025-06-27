package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.e2e.pageobjects.InstructorHomePageSql;
import teammates.storage.sqlentity.Instructor;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class InstructorCourseJoinConfirmationPageE2ETest extends BaseE2ETestCase {
    private Instructor newInstructor;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorCourseJoinConfirmationPageE2ETestSql.json"));

        newInstructor = testData.instructors.get("ICJoinConf.instr.CS1101");
    }

    @Test
    @Override
    public void testAll() {
        ______TS("Click join link: invalid key");
        String invalidKey = "invalidKey";
        AppUrl joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(invalidKey)
                .withEntityType(Const.EntityType.INSTRUCTOR);
        String newInstructorId = "tm.e2e.ICJoinConf.instr2";
        CourseJoinConfirmationPage confirmationPage = loginToPage(
                joinLink, CourseJoinConfirmationPage.class, newInstructorId);

        confirmationPage.verifyDisplayedMessage("The course join link is invalid. You may have "
                + "entered the URL incorrectly or the URL may correspond to a/an instructor that does not exist.");

        ______TS("Click join link: valid key");
        String courseId = testData.courses.get("ICJoinConf.CS1101").getId();
        String instructorEmail = newInstructor.getEmail();
        joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getKeyForInstructor(courseId, instructorEmail))
                .withEntityType(Const.EntityType.INSTRUCTOR);
        confirmationPage = getNewPageInstance(joinLink, CourseJoinConfirmationPage.class);

        confirmationPage.verifyJoiningUser(newInstructorId);
        confirmationPage.confirmJoinCourse(InstructorHomePageSql.class);

        ______TS("Already joined, no confirmation page");

        getNewPageInstance(joinLink, InstructorHomePageSql.class);

        logout();

        ______TS("Click join link: invalid key");
        joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withIsCreatingAccount("true")
                .withRegistrationKey(invalidKey);
        confirmationPage = loginToPage(joinLink, CourseJoinConfirmationPage.class, "ICJoinConf.newinstr");

        confirmationPage.verifyDisplayedMessage("The course join link is invalid. You may have "
                + "entered the URL incorrectly or the URL may correspond to a/an instructor that does not exist.");

        ______TS("Click join link: valid account request key");

        String regKey = BACKDOOR
                .getRegKeyForAccountRequest(testData.accountRequests.get("ICJoinConf.newinstr").getId());

        joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withIsCreatingAccount("true")
                .withRegistrationKey(regKey);

        confirmationPage = getNewPageInstance(joinLink, CourseJoinConfirmationPage.class);
        confirmationPage.verifyJoiningUser("ICJoinConf.newinstr");
        confirmationPage.confirmJoinCourse(InstructorHomePageSql.class);

        ______TS("Regkey for account request used, no confirmation page");

        getNewPageInstance(joinLink, InstructorHomePageSql.class);
    }
}
