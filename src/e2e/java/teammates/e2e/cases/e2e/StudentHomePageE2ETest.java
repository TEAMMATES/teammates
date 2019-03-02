package teammates.e2e.cases.e2e;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.e2e.util.BackDoor;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_HOME_PAGE}
 */
public class StudentHomePageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentHomePageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void allTests() throws Exception {
        testContentAndLogin();
    }

    private void testContentAndLogin() {

        ______TS("login successfully");

        StudentHomePage studentHome = getHomePage().clickStudentLogin()
                                   .loginAsStudent(TestProperties.TEST_STUDENT1_ACCOUNT,
                                                   TestProperties.TEST_STUDENT1_PASSWORD);

        browser.waitForPageLoad();
        assertTrue(verifyFirstPanelFeedbackSessionName("[SHomeUiT.CS1101]: Programming Methodology"));
    }

    private boolean verifyFirstPanelFeedbackSessionName(String message) {
        return message.equals(browser.driver.findElement(By.cssSelector("div.card-header.bg-primary strong")).getText());
    }

}
