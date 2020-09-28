package teammates.test.cases.browsertests;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.util.Priority;
import teammates.e2e.util.TestProperties;
import teammates.test.BackDoor;
import teammates.test.pageobjects.AdminAccountDetailsPage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_ACCOUNTS_PAGE}.
 */
@Priority(1)
public class AdminAccountDetailsPageUiTest extends BaseLegacyUiTestCase {
    private AdminAccountDetailsPage detailsPage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminAccountDetailsPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() throws Exception {
        testContent();
        //no links or input validation to check
        testRemoveFromCourseAction();
    }

    private void testContent() throws Exception {

        ______TS("content: typical page");

        AppUrl detailsPageUrl = createUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                // .withInstructorId("AAMgtUiT.instr2")
                .withUserId(TestProperties.TEST_ADMIN_ACCOUNT);
        detailsPage = loginAdminToPageOld(detailsPageUrl, AdminAccountDetailsPage.class);

        detailsPage.verifyHtml("/adminAccountDetails.html");
    }

    private void testRemoveFromCourseAction() throws Exception {

        ______TS("action: remove instructor from course");

        String googleId = "AAMgtUiT.instr2";
        String courseId = "AAMgtUiT.CS2104";

        detailsPage.clickRemoveInstructorFromCourse(courseId)
                .waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.INSTRUCTOR_REMOVED_FROM_COURSE);
        assertNull(BackDoor.getInstructorByGoogleId(googleId, courseId));

        ______TS("action: remove student from course");

        courseId = "AAMgtUiT.CS1101";
        detailsPage.clickRemoveStudentFromCourse(courseId)
                .waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.STUDENT_DELETED);
        assertNull(BackDoor.getStudent(courseId, "AAMgtUiT.instr2@gmail.com"));
        detailsPage.verifyHtmlMainContent("/adminAccountDetailsRemoveStudent.html");
    }

}
