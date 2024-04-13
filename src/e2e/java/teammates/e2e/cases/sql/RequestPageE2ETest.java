package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.e2e.pageobjects.RequestPage;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#ACCOUNT_REQUEST_PAGE}.
 */
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
        String email = TestProperties.TEST_EMAIL;
        String comments = "arf-test-comments";

        AppUrl url = createFrontendUrl(Const.WebPageURIs.ACCOUNT_REQUEST_PAGE);
        RequestPage requestPage = getNewPageInstance(url, RequestPage.class);

        ______TS("verify submission with comments");
        requestPage.clickAmInstructorButton();
        requestPage.fillForm(name, institution, country, email, comments);
        requestPage.clickSubmitFormButton();
        requestPage.verifySubmittedInfo(name, institution, country, email, comments);

        String expectedEmailSubject = EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT.toString();
        verifyEmailSent(email, expectedEmailSubject);

        ______TS("verify submission without comments");
        requestPage = getNewPageInstance(url, RequestPage.class);
        requestPage.clickAmInstructorButton();
        requestPage.fillForm(name, institution, country, email, "");
        requestPage.clickSubmitFormButton();
        requestPage.verifySubmittedInfo(name, institution, country, email, "");

        expectedEmailSubject = EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT.toString();
        verifyEmailSent(email, expectedEmailSubject);
    }
}
