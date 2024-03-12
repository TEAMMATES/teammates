package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentHomePage;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_HOME_PAGE}.
 */
public class StudentHomePageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentHomePageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        sqlTestData =
                removeAndRestoreSqlDataBundle(loadSqlDataBundle("/StudentHomePageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {

        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_HOME_PAGE);
        StudentHomePage homePage = loginToPage(url, StudentHomePage.class, "tm.e2e.SHome.student");

        Results results = getAxeBuilder().analyze(homePage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
