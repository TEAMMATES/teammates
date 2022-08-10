package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminHomePage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_HOME_PAGE}.
 */
public class AdminHomePageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminHomePageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_HOME_PAGE);
        AdminHomePage homePage = loginAdminToPage(url, AdminHomePage.class);

        ______TS("Test adding instructors with both valid and invalid details");

        String name = "AHPUiT Instrúctör WithPlusInEmail";
        String email = "AHPUiT+++_.instr1!@gmail.tmt";
        String institute = "TEAMMATES Test Institute 1";

        homePage.queueInstructorForAdding(name, email, institute);

        String singleLineDetails = "Instructor With Invalid Email | invalidemail | TEAMMATES Test Institute 1";

        homePage.queueInstructorForAdding(singleLineDetails);

        homePage.addAllInstructors();

        String successMessage = homePage.getMessageForInstructor(0);
        assertTrue(successMessage.contains(
                "Instructor \"AHPUiT Instrúctör WithPlusInEmail\" has been successfully created"));

        String failureMessage = homePage.getMessageForInstructor(1);
        assertTrue(failureMessage.contains(
                "\"invalidemail\" is not acceptable to TEAMMATES as a/an email because it is not in the correct format."));

        assertNotNull(getAccountRequest(email, institute));
        BACKDOOR.deleteAccountRequest(email, institute);

        ______TS("Failure case: Existing account request and instructor is already registered");

        AccountRequestAttributes registeredAccountRequest = testData.accountRequests.get("AHome.instructor1OfCourse1");
        homePage.queueInstructorForAdding(registeredAccountRequest.getName(),
                registeredAccountRequest.getEmail(), registeredAccountRequest.getInstitute());

        homePage.addAllInstructors();

        failureMessage = homePage.getMessageForInstructor(2);
        assertTrue(failureMessage.contains("An account request already exists with status REGISTERED."));

        ______TS("Reset the existing account request in the opened panel");

        homePage.clickMoreInfoButtonForExistingAccountRequest(2);
        homePage.clickAccountRequestPanelRegisteredResetButton();

        homePage.verifyStatusMessage("Account request successfully reset.");

        assertNull(getAccountRequest(registeredAccountRequest).getRegisteredAt());

        ______TS("Delete the existing account request in the opened panel after resetting it");

        homePage.clickAccountRequestPanelSubmittedDeleteButton();

        homePage.verifyStatusMessage("Account request successfully deleted.");

        // there is a limitation that the message inside new-instructor-data-row cannot be updated automatically
        failureMessage = homePage.getMessageForInstructor(2);
        assertTrue(failureMessage.contains("An account request already exists with status REGISTERED."));

        ______TS("Success case: Add the account request again");

        homePage.waitForConfirmationModalAndClickOk(); // it's actually an information modal instead of confirmation modal
        homePage.addAllInstructors();

        successMessage = homePage.getMessageForInstructor(2);
        assertTrue(successMessage.contains("Instructor \"Teammates Instr1\" has been successfully created"));
    }

}
