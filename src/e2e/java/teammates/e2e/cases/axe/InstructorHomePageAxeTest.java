package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorHomePage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_HOME_PAGE}.
 */
public class InstructorHomePageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorHomePageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorHomePageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE);
        InstructorHomePage homePage = loginToPage(url, InstructorHomePage.class,
                testData.instructors.get("IHome.instr.CS2104").getGoogleId());

        Results results = getAxeBuilder().analyze(homePage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
