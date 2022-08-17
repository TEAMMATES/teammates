package teammates.e2e.cases;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminRequestsPage;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_REQUESTS_PAGE}.
 */
public class AdminRequestsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminRequestsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_REQUESTS_PAGE);
        AdminRequestsPage requestsPage = loginAdminToPage(url, AdminRequestsPage.class);

        ______TS("load account requests pending processing during initialization");

        // there should be at least 3 account requests but the real number is not predictable
        // because it filters from all the account requests in the database
        requestsPage.verifyTabExpanded("pp-0");
        requestsPage.verifyTabExpanded("pp-1");
        requestsPage.verifyTabExpanded("pp-2");
        requestsPage.clickPanelHeader("pp-0");
        requestsPage.verifyTabCollapsed("pp-0");

        // can only verify presence (not total number) due to the same reason as above
        requestsPage.verifyAccountRequestPanelPendingProcessingContent(testData.accountRequests.get("accountRequest1"));
        requestsPage.verifyAccountRequestPanelPendingProcessingContent(testData.accountRequests.get("accountRequest2"));
        requestsPage.verifyAccountRequestPanelPendingProcessingContent(testData.accountRequests.get("accountRequest3"));

        ______TS("process an account request pending processing: edit > save");

        AccountRequestAttributes accountRequest = testData.accountRequests.get("accountRequest1");
        Integer index = requestsPage.getAccountRequestPanelPendingProcessingIndex(accountRequest);
        String panelIdentifier = "pp-" + index;

        String updatedName = "Updated Name";
        String updatedInstitute = "Updated Institute, Singapore";
        String updatedEmail = TestProperties.TEST_EMAIL;

        requestsPage.clickEditButton(panelIdentifier);
        requestsPage.fillNewName(panelIdentifier, updatedName);
        requestsPage.fillNewInstitute(panelIdentifier, updatedInstitute);
        requestsPage.fillNewEmail(panelIdentifier, updatedEmail);
        requestsPage.clickSaveButton(panelIdentifier);

        requestsPage.verifyStatusMessage("Account request successfully updated.");
        verifyAbsentInDatabase(accountRequest);

        AccountRequestAttributes updatedAccountRequest = getAccountRequest(updatedEmail, updatedInstitute);
        verifyNewlySavedAccountRequest(accountRequest, updatedAccountRequest, updatedName, updatedInstitute, updatedEmail);
        requestsPage.verifyAccountRequestPanelPendingProcessingContent(index, updatedAccountRequest);

        ______TS("process an account request pending processing: cannot save invalid > cancel");

        requestsPage.verifyErrorMessageNotPresent(panelIdentifier);

        String invalidEmail = "invalid_email";

        requestsPage.clickEditButton(panelIdentifier);
        requestsPage.fillNewEmail(panelIdentifier, invalidEmail);
        requestsPage.clickSaveButton(panelIdentifier);

        requestsPage.verifyStatusMessage("Failed to update account request.");
        requestsPage.verifyErrorMessagePresent(panelIdentifier,
                "\"invalid_email\" is not acceptable to TEAMMATES as a/an email because it is not in the correct format.");

        requestsPage.clickCancelButton(panelIdentifier);

        // clicking "Cancel" clears the error message
        requestsPage.verifyErrorMessageNotPresent(panelIdentifier);
        requestsPage.verifyAccountRequestPanelPendingProcessingContent(index, updatedAccountRequest);

        ______TS("process an account request pending processing: approve > reject > reset");

        // approve
        accountRequest = updatedAccountRequest;

        requestsPage.clickApproveButton(panelIdentifier);

        requestsPage.verifyStatusMessage("Account request successfully approved.");
        verifyEmailSent(accountRequest.getEmail(), "TEAMMATES: Welcome to TEAMMATES! " + accountRequest.getName());

        updatedAccountRequest = getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyNewlyApprovedAccountRequest(accountRequest, updatedAccountRequest);
        requestsPage.verifyAccountRequestPanelPendingProcessingContent(index, updatedAccountRequest);

        // reject
        accountRequest = updatedAccountRequest;

        requestsPage.clickRejectButton(panelIdentifier);

        requestsPage.verifyStatusMessage("Account request successfully rejected.");

        updatedAccountRequest = getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyNewlyRejectedAccountRequest(accountRequest, updatedAccountRequest);
        requestsPage.verifyAccountRequestPanelPendingProcessingContent(index, updatedAccountRequest);

        // reset
        accountRequest = updatedAccountRequest;

        requestsPage.clickResetButton(panelIdentifier, false);

        requestsPage.verifyStatusMessage("Account request successfully reset.");

        updatedAccountRequest = getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyNewlyResetAccountRequest(accountRequest, updatedAccountRequest);
        requestsPage.verifyAccountRequestPanelPendingProcessingContent(index, updatedAccountRequest);

        ______TS("process an account request pending processing: reject > approve");

        // reject
        accountRequest = updatedAccountRequest;

        requestsPage.clickRejectButton(panelIdentifier);

        requestsPage.verifyStatusMessage("Account request successfully rejected.");

        updatedAccountRequest = getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyNewlyRejectedAccountRequest(accountRequest, updatedAccountRequest);
        requestsPage.verifyAccountRequestPanelPendingProcessingContent(index, updatedAccountRequest);

        // approve
        accountRequest = updatedAccountRequest;

        requestsPage.clickApproveButton(panelIdentifier);

        requestsPage.verifyStatusMessage("Account request successfully approved.");
        verifyEmailSent(accountRequest.getEmail(), "TEAMMATES: Welcome to TEAMMATES! " + accountRequest.getName());

        updatedAccountRequest = getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyNewlyApprovedAccountRequest(accountRequest, updatedAccountRequest);
        requestsPage.verifyAccountRequestPanelPendingProcessingContent(index, updatedAccountRequest);

        ______TS("process an account request pending processing: delete (when approved)");

        accountRequest = updatedAccountRequest;

        requestsPage.clickDeleteButton(panelIdentifier);

        requestsPage.verifyStatusMessage("Account request successfully deleted.");

        verifyAbsentInDatabase(accountRequest);
        requestsPage.verifyDisplayedStatusForNewlyDeletedAccountRequest(panelIdentifier);

        ______TS("filter by submission time");

        // the test ensures that regardless of the current system timezone,
        // account request 3, 4, 5 will be in the filtered results
        Instant startTime = testData.accountRequests.get("accountRequest3").getCreatedAt().minus(1, ChronoUnit.DAYS);
        Instant endTime = testData.accountRequests.get("accountRequest5").getCreatedAt().plus(1, ChronoUnit.DAYS);

        requestsPage.waitForPageToLoad(); // to prevent interception when clicking on the date later
        requestsPage.fillFromDate(startTime);
        requestsPage.fillToDate(endTime);
        requestsPage.clickShowAccountRequestsButton();

        // there should be at least 3 account requests but the real number is not predictable
        // because it filters from all the account requests in the database
        requestsPage.verifyTabCollapsed("wp-0");
        requestsPage.verifyTabCollapsed("wp-1");
        requestsPage.verifyTabCollapsed("wp-2");
        requestsPage.clickPanelHeader("wp-0");
        requestsPage.verifyTabExpanded("wp-0");

        // can only verify presence (not total number) due to the same reason as above
        requestsPage.verifyAccountRequestPanelWithinPeriodContent(testData.accountRequests.get("accountRequest3"));
        requestsPage.verifyAccountRequestPanelWithinPeriodContent(testData.accountRequests.get("accountRequest4"));
        requestsPage.verifyAccountRequestPanelWithinPeriodContent(testData.accountRequests.get("accountRequest5"));

        ______TS("process an account request within period: reset (when registered) > delete (when submitted)");

        // reset
        accountRequest = testData.accountRequests.get("accountRequest5");
        index = requestsPage.getAccountRequestPanelWithinPeriodIndex(accountRequest);
        panelIdentifier = "wp-" + index;

        requestsPage.clickResetButton(panelIdentifier, true);

        requestsPage.verifyStatusMessage("Account request successfully reset.");

        updatedAccountRequest = getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyNewlyResetAccountRequest(accountRequest, updatedAccountRequest);
        requestsPage.verifyAccountRequestPanelWithinPeriodContent(index, updatedAccountRequest);

        // delete
        accountRequest = updatedAccountRequest;

        requestsPage.clickDeleteButton(panelIdentifier);

        requestsPage.verifyStatusMessage("Account request successfully deleted.");

        verifyAbsentInDatabase(accountRequest);
        requestsPage.verifyDisplayedStatusForNewlyDeletedAccountRequest(panelIdentifier);

        ______TS("process an account request within period: reject (when submitted) > delete (when rejected)");

        accountRequest = testData.accountRequests.get("accountRequest3");
        index = requestsPage.getAccountRequestPanelWithinPeriodIndex(accountRequest);
        panelIdentifier = "wp-" + index;

        requestsPage.clickDeleteButton(panelIdentifier);

        requestsPage.verifyStatusMessage("Account request successfully deleted.");

        verifyAbsentInDatabase(accountRequest);
        requestsPage.verifyDisplayedStatusForNewlyDeletedAccountRequest(panelIdentifier);
    }

    private void verifyNewlySavedAccountRequest(AccountRequestAttributes oldAccountRequest,
                                           AccountRequestAttributes updatedAccountRequest,
                                           String expectedNewName, String expectedNewInstitute, String expectedNewEmail) {
        verifyAccountRequestUpdated(oldAccountRequest, updatedAccountRequest, AccountRequestStatus.SUBMITTED);
        assertEquals(expectedNewName, updatedAccountRequest.getName());
        assertEquals(expectedNewInstitute, updatedAccountRequest.getInstitute());
        assertEquals(expectedNewEmail, updatedAccountRequest.getEmail());
    }

    private void verifyNewlyApprovedAccountRequest(AccountRequestAttributes oldAccountRequest,
                                           AccountRequestAttributes updatedAccountRequest) {
        verifyAccountRequestUpdated(oldAccountRequest, updatedAccountRequest, AccountRequestStatus.APPROVED);
    }

    private void verifyNewlyRejectedAccountRequest(AccountRequestAttributes oldAccountRequest,
                                              AccountRequestAttributes updatedAccountRequest) {
        verifyAccountRequestUpdated(oldAccountRequest, updatedAccountRequest, AccountRequestStatus.REJECTED);
    }

    private void verifyNewlyResetAccountRequest(AccountRequestAttributes oldAccountRequest,
                                                   AccountRequestAttributes updatedAccountRequest) {
        verifyAccountRequestUpdated(oldAccountRequest, updatedAccountRequest, AccountRequestStatus.SUBMITTED);
    }

    private void verifyAccountRequestUpdated(AccountRequestAttributes oldAccountRequest,
                                             AccountRequestAttributes updatedAccountRequest,
                                             AccountRequestStatus expectedStatus) {
        assertNotNull(updatedAccountRequest);
        assertNotEquals(oldAccountRequest.getLastProcessedAt(), updatedAccountRequest.getLastProcessedAt());
        assertEquals(expectedStatus, updatedAccountRequest.getStatus());
    }

}
