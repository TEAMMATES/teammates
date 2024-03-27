package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminSearchPage;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_SEARCH_PAGE}.
 */
public class AdminSearchPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        if (!TestProperties.INCLUDE_SEARCH_TESTS) {
            return;
        }

        testData = loadDataBundle("/AdminSearchPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
        sqlTestData = loadSqlDataBundle("/AdminSearchPageE2ETest_SqlEntities.json");
        removeAndRestoreSqlDataBundle(sqlTestData);
        putSqlDocuments(sqlTestData);
    }

    @Test
    @Override
    public void testAll() {
        if (!TestProperties.INCLUDE_SEARCH_TESTS) {
            return;
        }

        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_SEARCH_PAGE);
        AdminSearchPage searchPage = loginAdminToPage(url, AdminSearchPage.class);

        searchPage.inputSearchContent(testData.students.get("student1InCourse1").getEmail());
        searchPage.clickSearchButton();

        Results results = getAxeBuilder().analyze(searchPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
