package teammates.test.cases.ui.browsertests;

import java.lang.reflect.Constructor;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
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
        removeAndRestoreTestDataOnServer(testData);
        
        browser = BrowserPool.getBrowser(true);
        browser.driver.manage().deleteAllCookies();
    }

    
    @Test
    public void testAll() throws Exception {
        
        testContent();
        testJoinConfirmation();
    }
    
 
    private void testContent(){
        
        /*covered in testJoinConfirmation() 
         *case: Click join link then confirm: success: valid key
         */
    }
    
    
    private void testJoinConfirmation() throws Exception {
        
        ______TS("Click join link then cancel");
        
        String joinActionUrl = TestProperties.inst().TEAMMATES_URL 
                               + Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;

        String joinLink = Url.addParamToUrl(joinActionUrl,
                                            Const.ParamsNames.REGKEY, invalidEncryptedKey);
        AppPage.logout(browser);
        browser.driver.get(joinLink);
        confirmationPage = createCorrectLoginPageType(browser.driver.getPageSource())
                           .loginAsJoiningInstructor(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.inst().TEST_INSTRUCTOR_PASSWORD);
        
        confirmationPage.clickCancelButton();
        
        
        ______TS("Click join link then confirm: fail: invalid key");
        
        browser.driver.get(joinLink);
        confirmationPage = createCorrectLoginPageType(browser.driver.getPageSource())
                           .loginAsJoiningInstructor(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.inst().TEST_INSTRUCTOR_PASSWORD);
        
        InstructorHomePage instructorHome = confirmationPage.clickConfirmButton();
        instructorHome.verifyContains("You have used an invalid join link: /page/instructorCourseJoin?key="
                                      + invalidEncryptedKey);
        
        ______TS("Click join link then confirm: success: valid key");

        String courseId = testData.courses.get("ICJConfirmationUiT.CS1101").id;
        String instructorEmail = testData.instructors.get("ICJConfirmationUiT.instr.CS1101").email;

        joinLink = Url.addParamToUrl(joinActionUrl, Const.ParamsNames.REGKEY,
                                     StringHelper.encrypt(BackDoor.getKeyForInstructor(courseId, instructorEmail)));
        
        browser.driver.get(joinLink);
        confirmationPage = createNewPage(browser, InstructorCourseJoinConfirmationPage.class);
        
        // test content here to make test finish faster
        ______TS("test instructor confirmation page content");
        // this test uses accounts from test.properties
        confirmationPage.verifyHtmlMainContent("/instructorCourseJoinConfirmationHTML.html");
        
        instructorHome = confirmationPage.clickConfirmButton();
        instructorHome.verifyStatus("");
        
        ______TS("Already joined, no confirmation page");
                
        browser.driver.get(joinLink);
        instructorHome = createNewPage(browser, InstructorHomePage.class);
        instructorHome.verifyStatus(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT+ " has already joined this course");
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
    private LoginPage createCorrectLoginPageType(String pageSource) {
        if (DevServerLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, DevServerLoginPage.class);
        } else if (GoogleLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, GoogleLoginPage.class);
        } else {
            throw new IllegalStateException("Not a valid login page :" + pageSource);
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
