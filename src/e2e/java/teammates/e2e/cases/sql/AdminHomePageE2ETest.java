package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminHomePage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_HOME_PAGE}.
 */
public class AdminHomePageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        // not needed
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
        homePage.verifyStatusMessage("\"invalidemail\" is not acceptable to TEAMMATES as a/an email because it is not "
                + "in the correct format. An email address contains some text followed by one '@' sign followed by some "
                + "more text, and should end with a top level domain address like .com. It cannot be longer than 254 "
                + "characters, cannot be empty and cannot contain spaces.");

        ______TS("Verify that newly added instructor appears in account request table");
        homePage.verifyInstructorInAccountRequestTable(name, email, institute);

        ______TS("Test approving a valid account request");
        homePage.clickApproveAccountRequestButton(name, email, institute);
        homePage.verifyStatusMessage("Account request was successfully approved. Email has been sent to "
                + "AHPUiT+++_.instr1!@gmail.tmt.");
    }

}
