package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.RequestPage;

public class RequestPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        // No test data needed
    }

    @Test
    @Override
    protected void testAll() {
        String name = "arf-test-name";
        String institution = "arf-test-institution";
        String country = "arf-test-country";
        String email = "arf-test@tmts.tmt";
        String comments = "arf-test-comments";

        AppUrl url = createFrontendUrl(Const.WebPageURIs.ACCOUNT_REQUEST_PAGE);
        RequestPage  requestPage = getNewPageInstance(url, RequestPage.class);

        ______TS("verify submission with comments");
        requestPage.clickAmInstructorButton();
        requestPage.fillForm(name, institution, country, email, comments);
        requestPage.clickSubmitFormButton();
        requestPage.verifySubmittedInfo(name, institution, country, email, comments);

        ______TS("verify submission without comments");
        requestPage = getNewPageInstance(url, RequestPage.class);
        requestPage.clickAmInstructorButton();
        requestPage.fillForm(name, institution, country, email, null);
        requestPage.clickSubmitFormButton();
        requestPage.verifySubmittedInfo(name, institution, country, email, null);
    }
    
}
