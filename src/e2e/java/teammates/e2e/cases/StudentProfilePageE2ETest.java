package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentProfilePage;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_PROFILE_PAGE}.
 */
public class StudentProfilePageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentProfilePageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {

        ______TS("Typical case: Log in with filled profile values");

        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_PROFILE_PAGE);
        StudentProfilePage profilePage = loginToPage(url, StudentProfilePage.class, "tm.e2e.SProf.student");

        profilePage.ensureProfileContains("Ben", "i.m.benny@gmail.tmt", "TEAMMATES Test Institute 4",
                "Singaporean", StudentProfileAttributes.Gender.MALE, "I am just another student :P");

        ______TS("Typical case: picture upload and edit");
        profilePage.uploadProfilePicAndVerifyDimensions("src/test/resources/images/profile_pic.png", 220, 220);

        profilePage.showPictureEditor();
        profilePage.waitForUploadEditModalVisible();
        profilePage.editProfilePhoto();
        profilePage.closePictureEditor();

        ______TS("Typical case: Profile picture ratios");
        profilePage.uploadProfilePicAndVerifyDimensions("src/test/resources/images/profile_pic_too_wide.jpg", 171, 220);
        profilePage.uploadProfilePicAndVerifyDimensions("src/test/resources/images/profile_pic_too_tall.jpg", 220, 121);

        ______TS("Typical case: edit profile page");
        profilePage.editProfileThroughUi("short.name", "e@email.tmt", "inst", "American",
                StudentProfileAttributes.Gender.FEMALE, "this is enough!$%&*</>");
        profilePage.verifyStatusMessage("Your profile has been edited successfully");

        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                StudentProfileAttributes.Gender.FEMALE, "this is enough!$%&*</>");
    }
}
