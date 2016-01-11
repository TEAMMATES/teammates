package teammates.test.cases.ui.browsertests;

import java.io.File;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.FileHelper;
import teammates.common.util.Utils;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

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
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }

    @Test
    public void loadWebpageCompilation() throws Exception {
        AppPage page = loginAdmin(browser);
        uploadNewPhotoForStudent();
        page.navigateTo(createUrl(Const.ViewURIs.MASHUP));
    }

    private void uploadNewPhotoForStudent() throws Exception {
        String googleId = testData.accounts.get("benny.c.tmms").googleId;
        File picture = new File("src/test/resources/images/profile_pic_updated.png");
        String pictureData = Utils.getTeammatesGson().toJson(FileHelper.readFileAsBytes(picture.getAbsolutePath()));
         
        BackDoor.uploadAndUpdateStudentProfilePicture(googleId, pictureData);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        //We do not release the browser instance here because we want the tester
        //  to see the loaded page.
    }

}