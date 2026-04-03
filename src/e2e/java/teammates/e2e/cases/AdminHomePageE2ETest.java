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

        ______TS("Test adding instructors with valid details");

        String name = "AHPUiT Instrúctör WithPlusInEmail";
        String email = "ahpuit+++_.instr1!@gmail.tmt";
        String institute = "TEAMMATES Test Institute 1";

        homePage.addInstructor(name, email, institute);
        homePage.verifyStatusMessage("Account request was successfully created");

        ______TS("Verify that newly added instructor appears in account request table");

        homePage.verifyInstructorInAccountRequestTable(name, email, institute);

        ______TS("Test adding multiple instructors with invalid details");

        String invalidMultipleInstructorDetails = String.join("\n",
                "Instructor A | instructora@example.com | Institution A",
                "Instructor B | instructorb@example.com",
                "| instructorc@example.com | Institution C");

        homePage.addInstructor(invalidMultipleInstructorDetails);
        homePage.verifyStatusMessage("2 line(s) with missing or invalid fields. "
                + "Format required: Name | Email | Institution");
        homePage.verifyMultipleInstructorDetails(invalidMultipleInstructorDetails);

    }

}
