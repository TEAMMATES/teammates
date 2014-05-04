package teammates.test.cases.ui.browsertests;

import java.lang.reflect.Constructor;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.DevServerLoginPage;
import teammates.test.pageobjects.GoogleLoginPage;
import teammates.test.pageobjects.InstructorCourseJoinConfirmationPage;
import teammates.test.pageobjects.InstructorHomePage;
import teammates.test.pageobjects.LoginPage;

public class InstructorCourseJoinConfirmationPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static DataBundle testData;
    private static InstructorCourseJoinConfirmationPage confirmationPage;
    String invalidEncryptedKey = StringHelper.encrypt("invalidKey");
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCourseJoinConfirmationPageUiTest.json");
        restoreTestDataOnServer(testData);
        
        browser = BrowserPool.getBrowser();
        browser.driver.manage().deleteAllCookies();
    }

    @Test
    public void testJoinConfirmation() throws Exception {
        
        ______TS("click join link then cancel");
        
        String joinActionUrl = TestProperties.inst().TEAMMATES_URL
                + Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;

        String joinLink = Url.addParamToUrl(
                joinActionUrl,
                Const.ParamsNames.REGKEY, invalidEncryptedKey);
        
        browser.driver.get(joinLink);
        confirmationPage =
                createCorretLoginPageType(browser.driver.getPageSource())
                        .loginAsJoiningInstructor(
                                TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT,
                                TestProperties.inst().TEST_INSTRUCTOR_PASSWORD);
        confirmationPage.clickCancelButton();
        
        ______TS("click join link then confirm: fail: invalid key");
        
        browser.driver.get(joinLink);
        confirmationPage =
                createCorretLoginPageType(browser.driver.getPageSource())
                        .loginAsJoiningInstructor(
                                TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT,
                                TestProperties.inst().TEST_INSTRUCTOR_PASSWORD);
        InstructorHomePage instructorHome = confirmationPage.clickConfirmButton();
        instructorHome.verifyHtml("/instructorHomeInvalidKey.html");
        
        ______TS("click join link then confirm: success: valid key");

        String courseId = testData.courses.get("ICJConfirmationUiT.CS1101").id;
        String instructorEmail = testData.instructors
                .get("ICJConfirmationUiT.instr.CS1101").email;

        joinLink = Url.addParamToUrl(
                joinActionUrl,
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(BackDoor.getKeyForInstructor(courseId, instructorEmail)));
        
        browser.driver.get(joinLink);
        confirmationPage = createNewPage(browser, InstructorCourseJoinConfirmationPage.class);
        instructorHome = confirmationPage.clickConfirmButton();
        instructorHome.verifyHtml("/InstructorHomeJoined.html");
        
        ______TS("already joined, no confirmation page");
                
        browser.driver.get(joinLink);
        instructorHome = createNewPage(browser, InstructorHomePage.class);
        instructorHome.verifyHtml("/InstructorHomeAlreadyJoined.html");
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
    private LoginPage createCorretLoginPageType(String pageSource) {
        if (DevServerLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, DevServerLoginPage.class);
        } else if (GoogleLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, GoogleLoginPage.class);
        } else {
            throw new IllegalStateException("Not a valid login page :"    + pageSource);
        }
    }

    private <T extends AppPage> T createNewPage(Browser browser, Class<T> typeOfPage) {
        Constructor<T> constructor;
        try {
            constructor = typeOfPage.getConstructor(Browser.class);
            T page = constructor.newInstance(browser);
            PageFactory.initElements(browser.driver, page);
            return page;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
