package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.e2e.pageobjects.StudentProfilePage;
import teammates.e2e.util.BackDoor;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link teammates.common.util.Const.WebPageURIs#STUDENT_PROFILE_PAGE}.
 */
public class StudentProfilePageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentProfilePageE2ETest.json");

        String studentGoogleId = TestProperties.TEST_STUDENT2_ACCOUNT;
        String studentEmail = studentGoogleId + "@gmail.com";
        testData.accounts.get("studentWithExistingProfile").googleId = studentGoogleId;
        testData.profiles.get("studentWithExistingProfile").googleId = studentGoogleId;
        testData.accounts.get("studentWithExistingProfile").email = studentEmail;
        testData.students.get("studentWithExistingProfile").googleId = studentGoogleId;
        testData.students.get("studentWithExistingProfile").email = studentEmail;
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void allTests() throws Exception {
        testContent();
    }

    private void testContent() throws Exception {
        ______TS("Typical case: Log in with filled profile values");

        StudentHomePage shp = getHomePage().clickStudentLogin().loginAsStudent(
                TestProperties.TEST_STUDENT2_ACCOUNT, TestProperties.TEST_STUDENT2_PASSWORD);
        StudentProfilePage profilePage = shp.loadProfileTab();

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
                BackDoor.getStudentProfile(TestProperties.TEST_STUDENT2_ACCOUNT);
        // checks that the pictureKey value is within the newly uploaded profile picture link
        assertTrue(profilePage.getProfilePicLink().contains(studentProfileAttributes.pictureKey));
    }
}
