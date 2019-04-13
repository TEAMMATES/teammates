package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminHomePage;
import teammates.e2e.pageobjects.AdminSearchPage;
import teammates.e2e.pageobjects.AppPage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_SEARCH_PAGE}.
 */
public class AdminSearchPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminSearchPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    public void testAll() {
        testRegenerateStudentCourseLinks();

        // TODO: Add test cases for other actions
    }

    private void testRegenerateStudentCourseLinks() {
        AppUrl url = createUrl(Const.WebPageURIs.ADMIN_SEARCH_PAGE);
        loginAdminToPage(url, AdminHomePage.class);
        AdminSearchPage searchPage = AppPage.getNewPageInstance(browser, url, AdminSearchPage.class);

        ______TS("Typical case: Regenerate all links for a course student");

        searchPage.search("ASPUiT.student");
        String originalCourseJoinLink = searchPage.getCourseJoinLinkForStudent(0);

        searchPage.regenerateLinksForStudent(0);

        searchPage.verifyStatusMessage("Student's links for this course have been regenerated");
        // checks that the course join link is not the same after the regeneration action
        assertNotEquals(searchPage.getCourseJoinLinkForStudent(0), originalCourseJoinLink);

        logout();
    }

}
