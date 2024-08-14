package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class StudentCourseJoinConfirmationPageE2ETest extends BaseE2ETestCase {
    private Student newStudent;
    private String newStudentGoogleId;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/StudentCourseJoinConfirmationPageE2ETestSql.json"));

        newStudent = testData.students.get("alice.tmms@SCJoinConf.CS2104");
        newStudentGoogleId = testData.accounts.get("alice.tmms").getGoogleId();
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
                joinLink, CourseJoinConfirmationPage.class, newStudentGoogleId);

        confirmationPage.verifyDisplayedMessage("The course join link is invalid. You may have "
                + "entered the URL incorrectly or the URL may correspond to a/an student that does not exist.");

        ______TS("Click join link: valid key");
        joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(newStudent.getRegKey())
                .withCourseId(courseId)
                .withEntityType(Const.EntityType.STUDENT);
        confirmationPage = getNewPageInstance(joinLink, CourseJoinConfirmationPage.class);

        confirmationPage.verifyJoiningUser(newStudentGoogleId);
        confirmationPage.confirmJoinCourse(StudentHomePage.class);

        ______TS("Already joined, no confirmation page");

        getNewPageInstance(joinLink, StudentHomePage.class);
    }
}
