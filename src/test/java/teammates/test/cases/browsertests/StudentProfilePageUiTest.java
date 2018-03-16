package teammates.test.cases.browsertests;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Priority;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.EntityNotFoundPage;
import teammates.test.pageobjects.GenericAppPage;
import teammates.test.pageobjects.NotAuthorizedPage;
import teammates.test.pageobjects.NotFoundPage;
import teammates.test.pageobjects.StudentHomePage;
import teammates.test.pageobjects.StudentProfilePage;
import teammates.test.pageobjects.StudentProfilePicturePage;

/**
 * SUT: {@link Const.ActionURIs#STUDENT_PROFILE_PAGE}.
 */
@Priority(-3)
public class StudentProfilePageUiTest extends BaseUiTestCase {
    private StudentProfilePage profilePage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentProfilePageUiTest.json");

        // inject the 1st student account as student of the course where instructor is only the helper
        String student1GoogleId = TestProperties.TEST_STUDENT1_ACCOUNT;
        String student1Email = student1GoogleId + "@gmail.com";
        testData.accounts.get("studentForHelperCourse").googleId = student1GoogleId;
        testData.accounts.get("studentForHelperCourse").email = student1Email;
        testData.accounts.get("studentForHelperCourse").studentProfile.googleId = student1GoogleId;
        testData.students.get("studentForHelperCourse").googleId = student1GoogleId;
        testData.students.get("studentForHelperCourse").email = student1Email;

        // use the 2nd student account injected for this test

        String student2GoogleId = TestProperties.TEST_STUDENT2_ACCOUNT;
        String student2Email = student2GoogleId + "@gmail.com";
        testData.accounts.get("studentWithExistingProfile").googleId = student2GoogleId;
        testData.accounts.get("studentWithExistingProfile").email = student2Email;
        testData.accounts.get("studentWithExistingProfile").studentProfile.googleId = student2GoogleId;
        testData.students.get("studentWithExistingProfile").googleId = student2GoogleId;
        testData.students.get("studentWithExistingProfile").email = student2Email;

        // also inject instructor account for this test
        String instructorGoogleId = TestProperties.TEST_INSTRUCTOR_ACCOUNT;
        String instructorEmail = instructorGoogleId + "@gmail.com";
        testData.accounts.get("SHomeUiT.instr").googleId = instructorGoogleId;
        testData.accounts.get("SHomeUiT.instr").email = instructorEmail;
        testData.instructors.get("SHomeUiT.instr.CS2104").googleId = instructorGoogleId;
        testData.instructors.get("SHomeUiT.instr.CS2104").email = instructorEmail;
        testData.instructors.get("SHomeUiT.instr.CS2103").googleId = instructorGoogleId;
        testData.instructors.get("SHomeUiT.instr.CS2103").email = instructorEmail;

        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void allTests() throws Exception {
        // Do not change the order
        testNavLinkToPage();
        testContent();
        testActions();
        testJsFunctions();
        testAjaxPictureUrl();
    }

    private void testJsFunctions() {
        ______TS("Test disabling and enabling of upload button");
        // initial disabled state
        profilePage.verifyUploadButtonState(false);

        //enabled when a file is selected
        profilePage.fillProfilePic("src/test/resources/images/profile_pic.png");
        profilePage.verifyUploadButtonState(true);

        // disabled when file is cancelled
        profilePage.fillProfilePic("");
        profilePage.verifyUploadButtonState(false);

        // re-enabled when a new file is selected
        profilePage.fillProfilePic("src/test/resources/images/profile_pic.png");
        profilePage.verifyUploadButtonState(true);

    }

    private void testNavLinkToPage() {
        AppUrl profileUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE)
                                   .withUserId(testData.accounts.get("studentWithEmptyProfile").googleId);
        StudentHomePage shp = loginAdminToPage(profileUrl, StudentHomePage.class);
        profilePage = shp.loadProfileTab();
    }

    private void testContent() throws Exception {
        // assumes it is run after NavLinks Test
        // (ie already logged in as studentWithEmptyProfile
        ______TS("Typical case: empty profile values");

        // This is the full HTML verification for Registered Student Profile Submit Page, the rest can all be verifyMainHtml
        profilePage.verifyHtml("/studentProfilePageDefault.html");

        ______TS("Typical case: existing profile values");
        // this test uses actual user accounts
        profilePage = getProfilePageForStudent("studentWithExistingProfile");
        profilePage.verifyHtmlPart(By.id("editProfileDiv"), "/studentProfileEditDivExistingValues.html");

        ______TS("Typical case: existing profile with attempted script injection");
        profilePage = getProfilePageForStudent("student1InTestingSanitizationCourse");
        profilePage.verifyHtmlPart(
                By.id("editProfileDiv"), "/studentProfilePageWithAttemptedScriptInjection.html");

        ______TS("Typical case: edit profile picture modal (without existing picture)");
        profilePage = getProfilePageForStudent("studentWithExistingProfile");
        profilePage.showPictureEditor();
        profilePage.verifyHtmlPart(By.id("studentPhotoUploader"), "/studentProfilePictureModalDefault.html");

        ______TS("Typical case: edit profile picture modal (with existing picture)");
        profilePage = getProfilePageForStudent("studentWithExistingProfile");
        profilePage.fillProfilePic("src/test/resources/images/profile_pic.png");
        profilePage.uploadPicture();

        profilePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED);
        profilePage.waitForUploadEditModalVisible();
        profilePage.verifyHtmlMainContent("/studentProfilePageFilled.html");

        profilePage.closeEditPictureModal();
    }

    private void testActions() {
        // assumes it is run after NavLinks Test
        // (ie already logged in as studentWithExistingProfile
        String studentGoogleId = testData.accounts.get("studentWithExistingProfile").googleId;

        ______TS("Typical case: no picture");

        profilePage.editProfileThroughUi("short.name", "e@email.tmt", "inst", "Singaporean", "male",
                                         "this is enough!$%&*</>");
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "Singaporean",
                                          "male", "this is enough!$%&*</>");
        profilePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.STUDENT_PROFILE_EDITED);

        ______TS("Typical case: attempted script injection");

        StudentProfileAttributes spa = StudentProfileAttributes.builder("valid.id")
                .withShortName("name<script>alert(\"Hello world!\");</script>")
                .withEmail("e@email.tmt")
                .withGender("male")
                .withMoreInfo("this is enough!$%&*</><script>alert(\"Hello world!\");</script>")
                .withInstitute("inst<script>alert(\"Hello world!\");</script>")
                .withNationality("American")
                .build();
        profilePage.editProfileThroughUi(
                spa.shortName, spa.email, spa.institute, spa.nationality, spa.gender, spa.moreInfo);
        profilePage.ensureProfileContains("name<script>alert(\"Hello world!\");</script>",
                "e@email.tmt", "inst<script>alert(\"Hello world!\");</script>", "American",
                "male", "this is enough!$%&*</><script>alert(\"Hello world!\");</script>");
        profilePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.STUDENT_PROFILE_EDITED);

        ______TS("Typical case: changing genders for complete coverage");

        profilePage.editProfileThroughUi("short.name", "e@email.tmt", "inst", "American", "other",
                                         "this is enough!$%&*</>");
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                                          "other", "this is enough!$%&*</>");
        profilePage.editProfileThroughUi("short.name", "e@email.tmt", "inst", "American", "female",
                                         "this is enough!$%&*</>");
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                                         "female", "this is enough!$%&*</>");

        ______TS("Failure case: invalid institute with attempted script injection");

        spa = StudentProfileAttributes.builder("valid.id")
                .withShortName("short.name").withEmail("e@email.tmt")
                .withGender("male").withMoreInfo("this is enough!$%&*</>")
                .withInstitute("<script>alert(\"Hello world!\");</script>").withNationality("American")
                .build();
        profilePage.editProfileThroughUi(spa.shortName, spa.email, spa.institute, spa.nationality, spa.gender,
                                         spa.moreInfo);
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                                          "female", "this is enough!$%&*</>");
        profilePage.waitForTextsForAllStatusMessagesToUserEquals(StringHelper.toString(spa.getInvalidityInfo(), " ")
                                             // de-sanitize
                                             .replace("&lt;", "<").replace("&gt;", ">")
                                             .replace("&quot;", "\"").replace("&#x2f;", "/"));

        ______TS("Failure case: invalid data");

        spa = StudentProfileAttributes.builder("valid.id")
                .withShortName("$$short.name").withEmail("e@email.tmt")
                .withGender("male").withMoreInfo("this is enough!$%&*</>")
                .withInstitute(" inst  ").withNationality("American")
                .build();
        profilePage.editProfileThroughUi(spa.shortName, spa.email, spa.institute, spa.nationality, spa.gender,
                                         spa.moreInfo);
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                                          "female", "this is enough!$%&*</>");
        profilePage.waitForTextsForAllStatusMessagesToUserEquals(StringHelper.toString(spa.getInvalidityInfo(), " "));

        ______TS("Typical case: picture upload and edit");

        profilePage.fillProfilePic("src/test/resources/images/profile_pic.png");
        profilePage.uploadPicture();

        profilePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED);
        profilePage.waitForUploadEditModalVisible();

        profilePage.editProfilePhoto();
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                                          "female", "this is enough!$%&*</>");
        profilePage.verifyPhotoSize(150, 150);

        String prevPictureKey = BackDoor.getStudentProfile(studentGoogleId).pictureKey;
        verifyPictureIsPresent(prevPictureKey);

        ______TS("Typical case: repeated edit");

        profilePage.showPictureEditor();
        profilePage.editProfilePhoto();
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                                          "female", "this is enough!$%&*</>");
        profilePage.verifyPhotoSize(150, 150);

        prevPictureKey = BackDoor.getStudentProfile(studentGoogleId).pictureKey;
        verifyPictureIsPresent(prevPictureKey);

        ______TS("Failure case: not a picture");

        profilePage.fillProfilePic("src/test/resources/images/not_a_picture.txt");
        profilePage.uploadPicture();

        profilePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE);
        verifyPictureIsPresent(prevPictureKey);

        ______TS("Failure case: picture too large");

        profilePage.fillProfilePic("src/test/resources/images/profile_pic_too_large.jpg");
        profilePage.uploadPicture();

        profilePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE);
        verifyPictureIsPresent(prevPictureKey);

        ______TS("Typical case: update picture (too tall)");

        profilePage.fillProfilePic("src/test/resources/images/image_tall.jpg");
        profilePage.uploadPicture();

        profilePage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED);
        profilePage.waitForUploadEditModalVisible();
        profilePage.verifyPhotoSize(3074, 156);

        String currentPictureKey = BackDoor.getStudentProfile(studentGoogleId).pictureKey;
        verifyPictureIsPresent(currentPictureKey);
    }

    private void testAjaxPictureUrl() {
        String studentId = "studentWithExistingProfile";
        String instructorId = "SHomeUiT.instr";
        String instructorGoogleId = testData.accounts.get(instructorId).googleId;
        String studentGoogleId = testData.accounts.get("studentWithExistingProfile").googleId;
        String currentPictureKey = BackDoor.getStudentProfile(studentGoogleId).pictureKey;
        String email = testData.students.get("studentWithExistingProfile").email;
        String courseId = testData.students.get("studentWithExistingProfile").course;

        email = StringHelper.encrypt(email);
        courseId = StringHelper.encrypt(courseId);
        String invalidEmail = StringHelper.encrypt("random-EmAIl");

        ______TS("Typical case: with blob-key");

        getProfilePicturePage(studentId, currentPictureKey, StudentProfilePicturePage.class).verifyHasPicture();

        ______TS("Failure case: invalid blob-key");

        String invalidKey = "random-StRing123";
        if (TestProperties.isDevServer()) {
            getProfilePicturePage(studentId, invalidKey, NotFoundPage.class);
        } else {
            getProfilePicturePage(studentId, invalidKey, GenericAppPage.class);
            assertEquals("", browser.driver.findElement(By.tagName("body")).getText());
        }

        ______TS("Typical case: with email and course");

        getProfilePicturePage(instructorGoogleId, TestProperties.TEST_INSTRUCTOR_PASSWORD,
                email, courseId, StudentProfilePicturePage.class).verifyHasPicture();

        ______TS("Failure case: instructor does not have privilege");

        String studentForHelperCourseEmail = testData.students.get("studentForHelperCourse").email;
        String helperCourseId = testData.students.get("studentForHelperCourse").course;

        studentForHelperCourseEmail = StringHelper.encrypt(studentForHelperCourseEmail);
        helperCourseId = StringHelper.encrypt(helperCourseId);
        getProfilePicturePage(instructorGoogleId, TestProperties.TEST_INSTRUCTOR_PASSWORD,
                studentForHelperCourseEmail, helperCourseId, NotAuthorizedPage.class);

        ______TS("Failure case: non-existent student");

        getProfilePicturePage(instructorGoogleId, TestProperties.TEST_INSTRUCTOR_PASSWORD,
                invalidEmail, courseId, EntityNotFoundPage.class);
    }

    private <T extends AppPage> T getProfilePicturePage(String instructorGoogleId, String password,
            String email, String courseId, Class<T> typeOfPage) {
        AppUrl profileUrl = createUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
                                   .withParam(Const.ParamsNames.STUDENT_EMAIL, email)
                                   .withParam(Const.ParamsNames.COURSE_ID, courseId);
        return loginInstructorToPage(instructorGoogleId, password, profileUrl, typeOfPage);
    }

    private <T extends AppPage> T getProfilePicturePage(String studentId, String pictureKey, Class<T> typeOfPage) {
        AppUrl profileUrl = createUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
                                   .withUserId(testData.accounts.get(studentId).googleId)
                                   .withParam(Const.ParamsNames.BLOB_KEY, pictureKey);
        return loginAdminToPage(profileUrl, typeOfPage);
    }

    private void verifyPictureIsPresent(String pictureKey) {
        assertTrue(BackDoor.getWhetherPictureIsPresentInGcs(pictureKey));
    }

    private StudentProfilePage getProfilePageForStudent(String studentId) {
        AppUrl profileUrl = createUrl(Const.ActionURIs.STUDENT_PROFILE_PAGE)
                                   .withUserId(testData.accounts.get(studentId).googleId);
        return loginAdminToPage(profileUrl, StudentProfilePage.class);
    }

}
