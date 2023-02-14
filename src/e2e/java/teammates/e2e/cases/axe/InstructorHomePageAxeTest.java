package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.InstructorHomePage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_HOME_PAGE}.
 */
public class InstructorHomePageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorHomePageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE);
        InstructorHomePage homePage = loginToPage(url, InstructorHomePage.class,
                testData.instructors.get("IHome.instr.CS2104").getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(homePage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }

}
