package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.RequestPage;

/**
 * SUT: {@link Const.WebPageURIs#ACCOUNT_REQUEST_PAGE}.
 */
public class RequestPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        // No test data needed
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ACCOUNT_REQUEST_PAGE);
        RequestPage requestPage = getNewPageInstance(url, RequestPage.class);

        Results results = getAxeBuilder().analyze(requestPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
