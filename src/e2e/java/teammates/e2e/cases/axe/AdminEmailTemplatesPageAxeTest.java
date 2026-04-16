package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminEmailTemplatesPage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_EMAIL_TEMPLATES_PAGE}.
 */
public class AdminEmailTemplatesPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        // not needed
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_EMAIL_TEMPLATES_PAGE);
        AdminEmailTemplatesPage emailTemplatesPage = loginAdminToPage(url, AdminEmailTemplatesPage.class);

        Results results = getAxeBuilder().analyze(emailTemplatesPage.getBrowser().getDriver());
        assertViolationFree(results);
    }

}
