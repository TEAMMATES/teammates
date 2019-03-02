package teammates.test.cases.browsertests;

import java.io.File;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.e2e.cases.e2e.BaseE2ETestCase;
import teammates.test.driver.BackDoor;
import teammates.test.driver.FileHelper;
import teammates.test.pageobjects.AppPage;

/**
 * Loads the Mashup page for the tester to do a visual inspection.
 */
public class MashupPageUiTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/MashupPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void loadWebpageCompilation() throws Exception {
        AppPage page = null; // loginAdmin();
        uploadNewPhotoForStudent();
        page.navigateTo(createUrl(Const.ViewURIs.MASHUP));
    }

    private void uploadNewPhotoForStudent() throws Exception {
        String googleId = testData.accounts.get("benny.c.tmms").googleId;
        File picture = new File("src/test/resources/images/profile_pic_updated.png");
        String pictureData = JsonUtils.toJson(FileHelper.readFileAsBytes(picture.getAbsolutePath()));

        BackDoor.uploadAndUpdateStudentProfilePicture(googleId, pictureData);
    }

    @Override
    protected void releaseBrowser() {
        // We do not release the browser instance here because we want the tester to see the loaded page.
    }

}
