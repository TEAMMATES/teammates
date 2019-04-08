package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.e2e.pageobjects.StudentProfilePage;
import teammates.e2e.util.BackDoor;

/**
 * SUT: {@link teammates.common.util.Const.WebPageURIs#STUDENT_PROFILE_PAGE}.
 */
public class StudentProfilePageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentProfilePageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() {

        ______TS("Typical case: Log in with filled profile values");

        AppUrl url = createUrl(Const.WebPageURIs.STUDENT_PROFILE_PAGE).withUserId("SProfUiT.student");
        loginAdminToPage(url, StudentHomePage.class);
        StudentProfilePage profilePage = AppPage.getNewPageInstance(browser, url, StudentProfilePage.class);

        profilePage.ensureProfileContains("Ben", "i.m.benny@gmail.tmt", "TEAMMATES Test Institute 4",
                "Singaporean", StudentProfileAttributes.Gender.MALE, "I am just another student :P");

        ______TS("Typical case: no picture");
        profilePage.editProfileThroughUi("short.name", "e@email.tmt", "inst", "American",
                StudentProfileAttributes.Gender.FEMALE, "this is enough!$%&*</>");

        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                StudentProfileAttributes.Gender.FEMALE, "this is enough!$%&*</>");
        profilePage.verifyStatusMessage(Const.StatusMessages.STUDENT_PROFILE_EDITED);

        ______TS("Typical case: picture upload and edit");

        profilePage.fillProfilePic("src/test/resources/images/profile_pic.png");
        profilePage.uploadPicture();
        profilePage.verifyStatusMessage(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED);

        profilePage.showPictureEditor();
        profilePage.waitForUploadEditModalVisible();

        profilePage.editProfilePhoto();
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                StudentProfileAttributes.Gender.FEMALE, "this is enough!$%&*</>");
        profilePage.verifyPhotoSize("295px", "295px");

        StudentProfileAttributes studentProfileAttributes =
                BackDoor.getStudentProfile("SProfUiT.CS2104", "SProfUiT.student@gmail.tmt");
        // checks that the pictureKey value is within the newly uploaded profile picture link
        assertTrue(profilePage.getProfilePicLink().contains(studentProfileAttributes.pictureKey));

        logout();
    }
}
