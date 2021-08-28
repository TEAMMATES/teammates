package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.e2e.pageobjects.CreateAccountConfirmationPage;
import teammates.e2e.pageobjects.InstructorHomePage;

/**
 * SUT: {@link Const.WebPageURIs#CREATE_ACCOUNT_PAGE}.
 */
public class InstructorCreateAccountConfirmationPageE2ETest extends BaseE2ETestCase {
    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCreateAccountConfirmationPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        String userId = "test";

        ______TS("Invalid Join Link");

        AppUrl joinLink = createUrl(Const.WebPageURIs.CREATE_ACCOUNT_PAGE);
        CreateAccountConfirmationPage errorPage = getNewPageInstance(joinLink, CreateAccountConfirmationPage.class);

        assertTrue(errorPage.isInvalidLinkMessageShowing());

        ______TS("Click join link: valid key");

        String regKey = StringHelper.encrypt(testData.accountRequests.get("accountRequest1").getRegistrationKey());
        joinLink = createUrl(Const.WebPageURIs.CREATE_ACCOUNT_PAGE).withRegistrationKey(regKey);
        CreateAccountConfirmationPage confirmationPage = loginToPage(joinLink, CreateAccountConfirmationPage.class, userId);

        confirmationPage.verifyJoiningUser(userId);
        confirmationPage.confirmJoinCourse(InstructorHomePage.class);

        ______TS("Already joined, no confirmation page");

        getNewPageInstance(joinLink, InstructorHomePage.class);
    }
}
