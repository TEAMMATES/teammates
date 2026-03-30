package teammates.e2e.cases;

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
        String email = "ahpuit+++_.instr1!@gmail.tmt";
        String institute = "TEAMMATES Test Institute 1";

        homePage.addInstructor(name, email, institute);
        homePage.verifyStatusMessage("Instructor \"" + name + "\" has been successfully created");

        String singleLineDetails = "Instructor With Invalid Email | invalidemail | TEAMMATES Test Institute 1";

        homePage.addInstructor(singleLineDetails);
        homePage.verifyStatusMessage(
                "\"invalidemail\" is not acceptable to TEAMMATES as a/an email because it is not in the correct format.");

        ______TS("Verify that newly added instructor appears in account request table");

        homePage.verifyInstructorInAccountRequestTable(name, email, institute);

    }

}
