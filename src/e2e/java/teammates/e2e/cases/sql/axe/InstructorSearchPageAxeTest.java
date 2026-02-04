package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorSearchPageSql;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SEARCH_PAGE}.
 */
public class InstructorSearchPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        if (!TestProperties.INCLUDE_SEARCH_TESTS) {
            return;
        }

        testData = loadSqlDataBundle("/InstructorSearchPageE2ETestSql.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        if (!TestProperties.INCLUDE_SEARCH_TESTS) {
            return;
        }

        AppUrl searchPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SEARCH_PAGE);

        InstructorSearchPageSql searchPage = loginToPage(searchPageUrl, InstructorSearchPageSql.class,
                testData.accounts.get("instructor1OfCourse1").getGoogleId());

        searchPage.search("student2");

        Results results = getAxeBuilder().analyze(searchPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
