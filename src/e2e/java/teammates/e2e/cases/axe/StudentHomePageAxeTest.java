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
        testData = loadDataBundle("/StudentHomePageE2ESqlTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {

        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_HOME_PAGE);
        String studentAccountId = testData.accounts.get("SHome.student").getAccountId();
        StudentHomePage homePage = loginToPage(url, StudentHomePage.class, studentAccountId);

        Results results = getAxeBuilder().analyze(homePage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
