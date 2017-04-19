package teammates.test.cases.browsertests;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.StudentCommentsPage;

/**
 * SUT: {@link Const.ActionURIs#STUDENT_COMMENTS_PAGE}.
 */
public class StudentCommentsPageUiTest extends BaseUiTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCommentsPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void allTests() throws Exception {
        testContent();
    }

    private void testContent() throws Exception {

        ______TS("content: typical case");

        AppUrl commentsPageUrl = createUrl(Const.ActionURIs.STUDENT_COMMENTS_PAGE)
                .withUserId(testData.accounts.get("student1InCourse1").googleId);

        StudentCommentsPage commentsPage = loginAdminToPage(commentsPageUrl, StudentCommentsPage.class);

        // This is the full HTML verification for Student Comments Page, the rest can all be verifyMainHtml
        commentsPage.verifyHtml("/studentCommentsPageForStudent1.html");

        commentsPageUrl = createUrl(Const.ActionURIs.STUDENT_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("student2InCourse1").googleId);

        commentsPage = loginAdminToPage(commentsPageUrl, StudentCommentsPage.class);

        commentsPage.verifyHtmlMainContent("/studentCommentsPageForStudent2.html");

        commentsPageUrl = createUrl(Const.ActionURIs.STUDENT_COMMENTS_PAGE)
            .withUserId(testData.accounts.get("student3InCourse1").googleId);

        commentsPage = loginAdminToPage(commentsPageUrl, StudentCommentsPage.class);

        commentsPage.verifyHtmlMainContent("/studentCommentsPageForStudent3.html");
    }

}
