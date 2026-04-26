package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorSearchPageSql;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SEARCH_PAGE}.
 */
public class InstructorSearchPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorSearchPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl searchPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SEARCH_PAGE);

        InstructorSearchPageSql searchPage = loginToPage(searchPageUrl, InstructorSearchPageSql.class,
                testData.accounts.get("instructor1OfCourse1").getGoogleId());

        searchPage.search("student2");

        Results results = getAxeBuilder().analyze(searchPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
