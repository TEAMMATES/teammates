package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.logic.backdoor.BackDoorServlet;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentHomePage;
import teammates.test.pageobjects.StudentProfilePage;

public class StudentProfilePageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static DataBundle testData;
    private StudentProfilePage profilePage;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/StudentProfilePageUiTest.json");
        restoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void allTests() throws Exception {
        testContent();
        testNavLink();
        testActions();
    }

    private void testContent() {
        
        ______TS("typical success case");
        profilePage = getProfilePageForStudent("studentWithEmptyProfile");
        profilePage.verifyHtml("/studentProfilePageDefault.html");
        AppPage.logout(browser);
        
        ______TS("existing profile values");
        // this test uses actual user accounts
        profilePage = getProfilePageForStudent("studentWithoutProfilePicture");
        profilePage.verifyHtmlPart(By.id("editProfileDiv"), "/studentProfileEditDivExistingValues.html");
        AppPage.logout(browser);
    }

    private void testActions() throws Exception {
        profilePage = getProfilePageForStudent("studentWithoutProfilePicture");
        
        ______TS("typical success case, no picture");
        
        profilePage.editProfileThroughUi("", "short.name", "e@email.com", "inst", "Usual Country", 
                "female", "this is enough!$%&*</>");
        profilePage.ensureProfileContains("short.name", "e@email.com", "inst", "Usual Country", 
                "female", "this is enough!$%&*</>");
        profilePage.verifyStatus(Const.StatusMessages.STUDENT_PROFILE_EDITED);
        
        ______TS("success case, with picture");
        
        AccountAttributes studentWithoutPic = testData.accounts.get("studentWithoutProfilePicture");
        
        profilePage.fillProfilePic("src\\test\\resources\\images\\profile_pic.jpg");
        profilePage.submitEditedProfile();
        
        profilePage.ensureProfileContains("short.name", "e@email.com", "inst", "Usual Country", 
                "female", "this is enough!$%&*</>");
        profilePage.verifyStatus(Const.StatusMessages.STUDENT_PROFILE_EDITED);
        
        ______TS("image too big");
        String prevPictureKey = BackDoor.getStudentProfile(studentWithoutPic.googleId).pictureKey;
        verifyPictureIsPresent(prevPictureKey);
        
        profilePage.fillProfilePic("src\\test\\resources\\images\\profile_pic_too_large.jpg");
        profilePage.submitEditedProfile();
        
        profilePage.verifyStatus(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE);
        verifyPictureIsPresent(prevPictureKey);
        
        ______TS("success case, update picture");
        
        profilePage.fillProfilePic("src\\test\\resources\\images\\profile_pic_update.jpg");
        profilePage.submitEditedProfile();
        
        verifyPictureIsDeleted(prevPictureKey);
        String currentPictureKey = BackDoor.getStudentProfile(studentWithoutPic.googleId).pictureKey;
        verifyPictureIsPresent(currentPictureKey);
        
        ______TS("invalid data");
        
        StudentProfileAttributes spa = new StudentProfileAttributes("valid.id", "$$short.name", 
                "e@email.com", " inst  ", StringHelper.generateStringOfLength(54), 
                "male", "this is enough!$%&*</>", "");
        profilePage.editProfileThroughUi("", spa.shortName, spa.email, spa.institute, 
                spa.country, spa.gender, spa.moreInfo);
        
        profilePage.ensureProfileContains("short.name", "e@email.com", "inst", "Usual Country", 
                "female", "this is enough!$%&*</>");
        
        profilePage.verifyStatus(StringHelper.toString(spa.getInvalidityInfo(), " "));
        
        
    }

    private void verifyPictureIsDeleted(String pictureKey) {
        assertEquals(BackDoorServlet.RETURN_VALUE_FALSE, BackDoor.getWhetherPictureIsPresentInGcs(pictureKey));
    }

    private void verifyPictureIsPresent(String pictureKey) {
        assertEquals(BackDoorServlet.RETURN_VALUE_TRUE, BackDoor.getWhetherPictureIsPresentInGcs(pictureKey));
    }

    private void testNavLink() {
        Url profileUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE)
                .withUserId(testData.accounts.get("studentWithoutProfilePicture").googleId);
         StudentHomePage shp = loginAdminToPage(browser, profileUrl, StudentHomePage.class);
         
         profilePage = shp.loadProfileTab().changePageType(StudentProfilePage.class);
    }
    
    private StudentProfilePage getProfilePageForStudent(String studentId) {
        Url profileUrl = createUrl(Const.ActionURIs.STUDENT_PROFILE_PAGE)
            .withUserId(testData.accounts.get(studentId).googleId);
        return loginAdminToPage(browser, profileUrl, StudentProfilePage.class);
    }
}
