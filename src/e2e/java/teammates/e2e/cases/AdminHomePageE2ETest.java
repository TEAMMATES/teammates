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

        assertNotNull(BACKDOOR.getAccountRequest(email, institute));
        BACKDOOR.deleteAccountRequest(email, institute);

        ______TS("Failure case: Instructor is already registered");
        AccountRequestAttributes registeredAccountRequest = testData.accountRequests.get("AHome.instructor1OfCourse1");
        homePage.queueInstructorForAdding(registeredAccountRequest.getName(),
                registeredAccountRequest.getEmail(), registeredAccountRequest.getInstitute());

        homePage.addAllInstructors();

        failureMessage = homePage.getMessageForInstructor(2);
        assertTrue(failureMessage.contains("An account request already exists with status REGISTERED."));

        ______TS("Success case: Reset account request");

        homePage.clickMoreInfoButtonForRegisteredInstructor(2);
        homePage.clickResetAccountRequestLink();

        successMessage = homePage.getMessageForInstructor(2);
        assertTrue(successMessage.contains(
                "Instructor \"" + registeredAccountRequest.getName() + "\" has been successfully created"));

        assertNull(BACKDOOR.getAccountRequest(
                registeredAccountRequest.getEmail(), registeredAccountRequest.getInstitute()).getRegisteredAt());
    }

}
