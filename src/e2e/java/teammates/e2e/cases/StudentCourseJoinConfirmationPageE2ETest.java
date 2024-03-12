package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
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

        SqlDataBundle sqlTestData = loadSqlDataBundle("/StudentCourseJoinConfirmationPageE2ETest_SqlEntities.json");
        removeAndRestoreSqlDataBundle(sqlTestData);

        newStudent = testData.students.get("alice.tmms@SCJoinConf.CS2104");
        newStudent.setGoogleId(sqlTestData.accounts.get("alice.tmms").getGoogleId());
    }

    @Test
    @Override
    public void testAll() {
        ______TS("Click join link: invalid key");
        String courseId = testData.courses.get("SCJoinConf.CS2104").getId();
        String invalidKey = "invalidKey";
        AppUrl joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(invalidKey)
                .withCourseId(courseId)
                .withEntityType(Const.EntityType.STUDENT);
        CourseJoinConfirmationPage confirmationPage = loginToPage(
                joinLink, CourseJoinConfirmationPage.class, newStudent.getGoogleId());

        confirmationPage.verifyDisplayedMessage("The course join link is invalid. You may have "
                + "entered the URL incorrectly or the URL may correspond to a/an student that does not exist.");

        ______TS("Click join link: valid key");
        joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getKeyForStudent(newStudent))
                .withCourseId(courseId)
                .withEntityType(Const.EntityType.STUDENT);
        confirmationPage = getNewPageInstance(joinLink, CourseJoinConfirmationPage.class);

        confirmationPage.verifyJoiningUser(newStudent.getGoogleId());
        confirmationPage.confirmJoinCourse(StudentHomePage.class);

        ______TS("Already joined, no confirmation page");

        getNewPageInstance(joinLink, StudentHomePage.class);
    }
}
