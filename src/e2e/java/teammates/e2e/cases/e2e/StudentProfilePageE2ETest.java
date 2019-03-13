package teammates.e2e.cases.e2e;

import org.json.JSONObject;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.e2e.pageobjects.StudentProfilePage;
import teammates.e2e.util.BackDoor;
import teammates.e2e.util.Priority;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link teammates.common.util.Const.WebPageURIs#STUDENT_PROFILE_PAGE}.
 */
@Priority(-3)
public class StudentProfilePageE2ETest extends BaseE2ETestCase {
    private StudentProfilePage profilePage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentProfilePageE2ETest.json");

        // inject the 1st student account as student of the course where instructor is only the helper
        String student1GoogleId = TestProperties.TEST_STUDENT1_ACCOUNT;
        String student1Email = student1GoogleId + "@gmail.com";
        testData.accounts.get("studentForHelperCourse").googleId = student1GoogleId;
        testData.profiles.get("studentForHelperCourse").googleId = student1GoogleId;
        testData.accounts.get("studentForHelperCourse").email = student1Email;
        testData.students.get("studentForHelperCourse").googleId = student1GoogleId;
        testData.students.get("studentForHelperCourse").email = student1Email;

        // use the 2nd student account injected for this test

        String student2GoogleId = TestProperties.TEST_STUDENT2_ACCOUNT;
        String student2Email = student2GoogleId + "@gmail.com";
        testData.accounts.get("studentWithExistingProfile").googleId = student2GoogleId;
        testData.profiles.get("studentWithExistingProfile").googleId = student2GoogleId;
        testData.accounts.get("studentWithExistingProfile").email = student2Email;
        testData.students.get("studentWithExistingProfile").googleId = student2GoogleId;
        testData.students.get("studentWithExistingProfile").email = student2Email;

        // also inject instructor account for this test
        String instructorGoogleId = TestProperties.TEST_INSTRUCTOR_ACCOUNT;
        String instructorEmail = instructorGoogleId + "@gmail.com";
        testData.accounts.get("SProfileUiT.instr").googleId = instructorGoogleId;
        testData.accounts.get("SProfileUiT.instr").email = instructorEmail;
        testData.instructors.get("SProfileUiT.instr.CS2104").googleId = instructorGoogleId;
        testData.instructors.get("SProfileUiT.instr.CS2104").email = instructorEmail;
        testData.instructors.get("SProfileUiT.instr.CS2103").googleId = instructorGoogleId;
        testData.instructors.get("SProfileUiT.instr.CS2103").email = instructorEmail;

        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void allTests() throws Exception {
        // Do not change the order
        testNavLinkToPage();
        testContent();
    }

    private void testNavLinkToPage() {
        StudentHomePage shp = getHomePage().clickStudentLogin().loginAsStudent(
                TestProperties.TEST_STUDENT2_ACCOUNT, TestProperties.TEST_STUDENT2_PASSWORD);
        profilePage = shp.loadProfileTab();
    }

    private void testContent() throws Exception {
        // assumes it is run after NavLinks Test
        // (ie already logged in as studentWithExistingProfile
        ______TS("Typical case: Log in with filled profile values");

        profilePage.ensureProfileContains("Ben", "i.m.benny@gmail.tmt", "TEAMMATES Test Institute 4",
                "Singaporean", StudentProfileAttributes.Gender.MALE, "I am just another student :P");

        ______TS("Typical case: no picture");
        profilePage.editProfileThroughUi("short.name", "e@email.tmt", "inst", "American",
                StudentProfileAttributes.Gender.FEMALE, "this is enough!$%&*</>");

        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                StudentProfileAttributes.Gender.FEMALE, "this is enough!$%&*</>");
        profilePage.verifyStatusMessage(Const.StatusMessages.STUDENT_PROFILE_EDITED);

        ______TS("Typical case: picture upload and edit");

        profilePage.fillProfilePic("src/e2e/resources/images/profile_pic.png");
        profilePage.uploadPicture();
        profilePage.verifyStatusMessage(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED);

        profilePage.showPictureEditor();
        profilePage.waitForUploadEditModalVisible();

        profilePage.editProfilePhoto();
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                StudentProfileAttributes.Gender.FEMALE, "this is enough!$%&*</>");
        profilePage.verifyPhotoSize("295px", "295px");

        JSONObject studentProfileAttributes = new JSONObject(
                BackDoor.getStudentProfile(TestProperties.TEST_STUDENT2_ACCOUNT));
        String prevPictureKey = studentProfileAttributes.getJSONObject("studentProfile").getString("pictureKey");
        verifyPictureIsPresent(prevPictureKey);

    }

    private void verifyPictureIsPresent(String pictureKey) {
        // TODO: how to check the validity of this picturekey value?
        assertEquals("encoded_gs_key", pictureKey.split(":")[0]);
        // assertTrue(BackDoor.getWhetherPictureIsPresentInGcs(pictureKey));
    }
}
