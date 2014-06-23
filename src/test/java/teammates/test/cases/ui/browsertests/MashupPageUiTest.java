package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentProfilePage;

/**
 * Loads the Mashup page for the tester to do a visual inspection.
 */
public class MashupPageUiTest extends BaseUiTestCase {
    private static Browser browser;


    private static DataBundle testData;
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/MashupPageUiTest.json");
        restoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }

    @Test
    public void loadWebpageCompilation() throws Exception {
        AppPage page = loginAdmin(browser);
        uploadNewPhotoForStudent();
        page.navigateTo(createUrl(Const.ViewURIs.MASHUP));
    }

    private void uploadNewPhotoForStudent() throws Exception {
        StudentProfilePage profilePage = getProfilePageForStudent("benny.c.tmms");
        profilePage.fillProfilePic("src\\test\\resources\\images\\profile_pic_update.png");
        profilePage.submitEditedProfile();
    }
    
    private StudentProfilePage getProfilePageForStudent(String studentId) {
        Url profileUrl = createUrl(Const.ActionURIs.STUDENT_PROFILE_PAGE)
            .withUserId(testData.accounts.get(studentId).googleId);
        return loginAdminToPage(browser, profileUrl, StudentProfilePage.class);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        //We do not release the browser instance here because we want the tester
        //  to see the loaded page.
    }

}